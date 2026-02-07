use crate::worktree::get_sorted_worktrees;
use eyre::bail;
use eyre::Context;
use facet::Facet;
use figue::{self as args};
use ratatui::{
    backend::CrosstermBackend,
    layout::Constraint,
    style::{Color, Modifier, Style},
    text::Span,
    widgets::{Block, Cell, Paragraph, Row, Table},
    Terminal, TerminalOptions, Viewport,
};
use std::io::stderr;
use std::path::PathBuf;
use std::sync::{
    atomic::{AtomicBool, Ordering},
    Arc,
};
use std::time::{Duration, Instant};
use tokio::process::Command;
use tokio::sync::mpsc;
use tokio::task::JoinSet;

use ratatui::crossterm::style::{Attribute, Color as CTermColor, SetAttribute, SetForegroundColor};
use ratatui::crossterm::ExecutableCommand;

#[derive(Debug, Clone, PartialEq, Eq)]
enum TaskState {
    NotStarted,
    Running {
        start_time: Instant,
    },
    Success {
        duration: Duration,
    },
    Failed {
        duration: Duration,
        error: String,
    },
    Skipped,
    NotFound {
        reason: String,
    },
}

#[derive(Debug)]
enum Message {
    StatusUpdate(usize, TaskState),
    AbortRemaining(String),
}

impl TaskState {
    fn is_finished(&self) -> bool {
        match self {
            TaskState::Success { .. }
            | TaskState::Failed { .. }
            | TaskState::Skipped
            | TaskState::NotFound { .. } => true,
            _ => false,
        }
    }

    fn status_text(&self) -> String {
        match self {
            TaskState::NotStarted => "NOT STARTED".to_string(),
            TaskState::Running { .. } => "RUNNING".into(),
            TaskState::Success { .. } => "SUCCESS".into(),
            TaskState::Failed { .. } => "FAILED".into(),
            TaskState::Skipped => "SKIPPED".into(),
            TaskState::NotFound { .. } => "NOT FOUND".into(),
        }
    }

    fn get_color(&self) -> Color {
        match self {
            TaskState::NotStarted => Color::Gray,
            TaskState::Running { .. } => Color::Yellow,
            TaskState::Success { .. } => Color::Green,
            TaskState::Failed { .. } => Color::Red,
            TaskState::Skipped => Color::DarkGray,
            TaskState::NotFound { .. } => Color::Magenta,
        }
    }

    fn elapsed_str(&self) -> String {
        match self {
            TaskState::Running { start_time } => {
                format!("({})", format_duration(start_time.elapsed()))
            }
            TaskState::Success { duration } | TaskState::Failed { duration, .. } => {
                if duration.as_secs_f32() > 0.0 {
                    format!("({})", format_duration(*duration))
                } else {
                    "".to_string()
                }
            }
            TaskState::NotFound { reason } => format!("({reason})"),
            _ => "".to_string(),
        }
    }
}

struct BranchState {
    branch: String,
    test: TaskState,
}

struct AppState {
    branches: Vec<BranchState>,
}

/// Format a duration as a human-readable string
fn format_duration(duration: Duration) -> String {
    let secs = duration.as_secs();
    if secs >= 60 {
        let mins = secs / 60;
        let remaining_secs = secs % 60;
        format!("{mins}m {remaining_secs:02}s")
    } else {
        format!("{}.{:01}s", secs, duration.subsec_millis() / 100)
    }
}

async fn run_task(
    gradlew: &PathBuf,
    dir: &PathBuf,
    task: &str,
    mock: bool,
) -> Result<Duration, (Duration, String)> {
    if mock {
        use rand::Rng;
        let (duration, success) = {
            let mut rng = rand::rng();
            (
                Duration::from_secs_f64(rng.random_range(0.0..4.0)),
                rng.random_bool(0.8),
            )
        };
        tokio::time::sleep(duration).await;

        if success {
            return Ok(duration);
        } else {
            return Err((duration, format!("Mock failure for {}", task)));
        }
    }

    let start = Instant::now();
    let res = Command::new(gradlew)
        .arg(task)
        .current_dir(dir)
        .kill_on_drop(true)
        .output()
        .await;

    let duration = start.elapsed();
    match res {
        Ok(output) => {
            if output.status.success() {
                Ok(duration)
            } else {
                let stdout = String::from_utf8_lossy(&output.stdout);
                let stderr = String::from_utf8_lossy(&output.stderr);
                Err((
                    duration,
                    format!(
                        "{} failed: status: {:?}\nStdout: {}\nStderr: {}",
                        task, output.status, stdout, stderr
                    ),
                ))
            }
        }
        Err(e) => Err((duration, format!("Failed to start {}: {}", task, e))),
    }
}

async fn test_worktree_task(
    index: usize,
    tx: mpsc::UnboundedSender<Message>,
    path: PathBuf,
    mock: bool,
) -> bool {
    let minecraft_dir = path.join("platform").join("minecraft");

    if !minecraft_dir.exists() {
        tx.send(Message::StatusUpdate(
            index,
            TaskState::NotFound {
                reason: "platform/minecraft not found".into(),
            },
        ))
        .ok();
        return false;
    }

    let gradlew = if cfg!(windows) {
        minecraft_dir.join("gradlew.bat")
    } else {
        minecraft_dir.join("gradlew")
    };

    if !gradlew.exists() {
        tx.send(Message::StatusUpdate(
            index,
            TaskState::NotFound {
                reason: format!("gradlew not found in {}", minecraft_dir.display()),
            },
        ))
        .ok();
        return false;
    }

    tx.send(Message::StatusUpdate(
        index,
        TaskState::Running {
            start_time: Instant::now(),
        },
    ))
    .ok();

    let test_res = run_task(&gradlew, &minecraft_dir, "test", mock).await;
    match test_res {
        Ok(duration) => {
            tx.send(Message::StatusUpdate(
                index,
                TaskState::Success { duration },
            ))
            .ok();
            true
        }
        Err((duration, err)) => {
            tx.send(Message::StatusUpdate(
                index,
                TaskState::Failed {
                    duration,
                    error: err,
                },
            ))
            .ok();
            false
        }
    }
}

fn mark_remaining_skipped(state: &mut AppState) {
    for branch in &mut state.branches {
        if !branch.test.is_finished() {
            branch.test = TaskState::Skipped;
        }
    }
}

fn render(f: &mut ratatui::Frame, state: &AppState) {
    let area = f.area();

    if state.branches.is_empty() {
        f.render_widget(Paragraph::new("No branches to display"), area);
        return;
    }

    let [table_area, _] = ratatui::layout::Layout::horizontal([
        Constraint::Length(60),
        Constraint::Min(0),
    ])
    .areas(area);

    let mut rows = Vec::new();
    for b in &state.branches {
        let branch_name = Span::styled(
            b.branch.clone(),
            Style::default().fg(Color::Yellow).add_modifier(Modifier::BOLD),
        );
        let mut row_cells = vec![Cell::from(branch_name)];

        let text = format!("{} {}", b.test.status_text(), b.test.elapsed_str());
        row_cells.push(Cell::from(text).style(Style::default().fg(b.test.get_color())));

        rows.push(Row::new(row_cells));
    }

    let table = Table::new(rows, [Constraint::Length(18), Constraint::Length(24)])
        .header(
            Row::new(vec!["BRANCH", "TEST"])
                .style(Style::default().add_modifier(Modifier::BOLD).underlined()),
        )
        .block(Block::bordered().title(format!(" Test Status ({}) ", state.branches.len())));

    f.render_widget(table, table_area);
}

fn print_final_report(state: &AppState) {
    let mut stdout = std::io::stdout();

    let mut successful_branches = 0;
    let mut failed_branches = 0;
    let mut skipped_branches = 0;

    for b in &state.branches {
        match b.test {
            TaskState::Failed { .. } => failed_branches += 1,
            TaskState::Success { .. } | TaskState::Skipped => successful_branches += 1,
            _ => skipped_branches += 1,
        }
    }

    let header_color = if failed_branches > 0 {
        CTermColor::Red
    } else if failed_branches == 0 && skipped_branches == 0 {
        CTermColor::Green
    } else {
        CTermColor::Yellow
    };

    println!();
    let _ = stdout.execute(SetForegroundColor(CTermColor::DarkGrey));
    println!("{}", "═".repeat(60));
    let _ = stdout.execute(SetForegroundColor(header_color));
    let _ = stdout.execute(SetAttribute(Attribute::Bold));
    println!("  TEST SUMMARY");
    let _ = stdout.execute(SetAttribute(Attribute::Reset));
    let _ = stdout.execute(SetForegroundColor(CTermColor::DarkGrey));
    println!("{}", "═".repeat(60));
    let _ = stdout.execute(SetAttribute(Attribute::Reset));
    println!();

    print!("  Total branches: ");
    let _ = stdout.execute(SetAttribute(Attribute::Bold));
    print!("{}", state.branches.len());
    let _ = stdout.execute(SetAttribute(Attribute::Reset));
    print!(" (");

    if successful_branches > 0 {
        let _ = stdout.execute(SetForegroundColor(CTermColor::Green));
        print!("{} successful", successful_branches);
        let _ = stdout.execute(SetAttribute(Attribute::Reset));
        if failed_branches > 0 || skipped_branches > 0 {
            print!(", ");
        }
    }
    if failed_branches > 0 {
        let _ = stdout.execute(SetForegroundColor(CTermColor::Red));
        print!("{} failed", failed_branches);
        let _ = stdout.execute(SetAttribute(Attribute::Reset));
        if skipped_branches > 0 {
            print!(", ");
        }
    }
    if skipped_branches > 0 {
        let _ = stdout.execute(SetForegroundColor(CTermColor::Yellow));
        print!("{} skipped", skipped_branches);
        let _ = stdout.execute(SetAttribute(Attribute::Reset));
    }
    println!(")");

    let mut failures = Vec::new();
    for b in &state.branches {
        if let TaskState::Failed { error, .. } = &b.test {
            failures.push((&b.branch, error));
        }
    }

    if !failures.is_empty() {
        println!();
        let _ = stdout.execute(SetForegroundColor(CTermColor::Red));
        let _ = stdout.execute(SetAttribute(Attribute::Bold));
        println!("FAILURE DETAILS");
        let _ = stdout.execute(SetAttribute(Attribute::Reset));
        let _ = stdout.execute(SetForegroundColor(CTermColor::DarkGrey));
        println!("{}", "─".repeat(60));
        let _ = stdout.execute(SetAttribute(Attribute::Reset));

        for (branch, err) in failures {
            println!();
            let _ = stdout.execute(SetForegroundColor(CTermColor::Red));
            let _ = stdout.execute(SetAttribute(Attribute::Bold));
            print!("✖ ERROR ");
            let _ = stdout.execute(SetAttribute(Attribute::Reset));

            let _ = stdout.execute(SetForegroundColor(CTermColor::Cyan));
            let _ = stdout.execute(SetAttribute(Attribute::Bold));
            print!("TEST");
            let _ = stdout.execute(SetAttribute(Attribute::Reset));

            print!(" on branch ");
            let _ = stdout.execute(SetForegroundColor(CTermColor::Yellow));
            let _ = stdout.execute(SetAttribute(Attribute::Bold));
            print!("{}", branch);
            let _ = stdout.execute(SetAttribute(Attribute::Reset));
            println!(":");

            let _ = stdout.execute(SetForegroundColor(CTermColor::Grey));
            for line in err.lines() {
                println!("   {}", line);
            }
            let _ = stdout.execute(SetAttribute(Attribute::Reset));
        }
        println!();
        let _ = stdout.execute(SetForegroundColor(CTermColor::DarkGrey));
        println!("{}", "─".repeat(60));
        let _ = stdout.execute(SetAttribute(Attribute::Reset));
    } else if successful_branches == state.branches.len() {
        println!();
        let _ = stdout.execute(SetForegroundColor(CTermColor::Green));
        let _ = stdout.execute(SetAttribute(Attribute::Bold));
        println!("  ✔ All tests finished successfully!");
        let _ = stdout.execute(SetAttribute(Attribute::Reset));
    }
    println!();
}

/// Test command - runs `gradlew test` for all worktrees
#[derive(Facet, Debug, Default)]
pub struct TestCommand {
    /// If set, mock the test process with random results and durations.
    #[facet(args::named)]
    pub mock: bool,

    /// If set, run tests in parallel across worktrees.
    #[facet(args::named)]
    pub parallel: bool,
}

impl TestCommand {
    /// # Errors
    ///
    /// Returns an error if tests fail.
    pub fn invoke(self) -> eyre::Result<()> {
        let rt = tokio::runtime::Builder::new_multi_thread()
            .enable_all()
            .build()
            .wrap_err("Failed to create tokio runtime")?;

        rt.block_on(self.invoke_async())
    }

    async fn invoke_async(self) -> eyre::Result<()> {
        let worktrees = get_sorted_worktrees()?;

        if worktrees.is_empty() {
            println!("No worktrees found.");
            return Ok(());
        }

        let mut branches: Vec<_> = worktrees
            .iter()
            .map(|w| BranchState {
                branch: w.branch.clone(),
                test: TaskState::NotStarted,
            })
            .collect();
        branches.sort_by(|a, b| a.branch.cmp(&b.branch));

        eprintln!("Starting TUI for {} branches...", branches.len());

        let (tx, mut rx) = mpsc::unbounded_channel();
        let mut app_state = AppState { branches };

        let backend = CrosstermBackend::new(stderr());
        let terminal_height = (worktrees.len() + 3) as u16;
        let mut terminal = Terminal::with_options(
            backend,
            TerminalOptions {
                viewport: Viewport::Inline(terminal_height),
            },
        )?;
        terminal.hide_cursor()?;

        let branches_info: Vec<_> = app_state
            .branches
            .iter()
            .enumerate()
            .map(|(i, b)| {
                let worktree = worktrees.iter().find(|w| w.branch == b.branch).unwrap();
                (i, worktree.path.clone())
            })
            .collect();

        let mock = self.mock;
        let parallel = self.parallel;
        let p_tx = tx.clone();
        let cancel = Arc::new(AtomicBool::new(false));
        let cancel_worker = cancel.clone();

        tokio::spawn(async move {
            if parallel {
                let mut join_set = JoinSet::new();
                for (i, path) in branches_info {
                    if cancel_worker.load(Ordering::SeqCst) {
                        break;
                    }
                    let tx = p_tx.clone();
                    join_set.spawn(async move { test_worktree_task(i, tx, path, mock).await });
                }

                while let Some(res) = join_set.join_next().await {
                    if cancel_worker.load(Ordering::SeqCst) {
                        join_set.abort_all();
                        break;
                    }
                    match res {
                        Ok(true) => {}
                        Ok(false) | Err(_) => {
                            cancel_worker.store(true, Ordering::SeqCst);
                            join_set.abort_all();
                            let _ = p_tx
                                .send(Message::AbortRemaining("Stopped after failure".into()));
                            break;
                        }
                    }
                }
            } else {
                for (i, path) in branches_info {
                    if cancel_worker.load(Ordering::SeqCst) {
                        break;
                    }
                    let ok = test_worktree_task(i, p_tx.clone(), path, mock).await;
                    if !ok {
                        cancel_worker.store(true, Ordering::SeqCst);
                        let _ = p_tx
                            .send(Message::AbortRemaining("Stopped after failure".into()));
                        break;
                    }
                }
            }
        });

        let mut interval = tokio::time::interval(Duration::from_millis(100));
        let mut abort_reason: Option<String> = None;
        let mut ctrl_c_fired = false;
        use std::io::Write;
        loop {
            while let Ok(msg) = rx.try_recv() {
                match msg {
                    Message::StatusUpdate(index, state) => {
                        if abort_reason.is_none() {
                            let branch = &mut app_state.branches[index];
                            branch.test = state;
                        }
                    }
                    Message::AbortRemaining(reason) => {
                        if abort_reason.is_none() {
                            abort_reason = Some(reason);
                            cancel.store(true, Ordering::SeqCst);
                            mark_remaining_skipped(&mut app_state);
                        }
                    }
                }
            }

            terminal.draw(|f| render(f, &app_state))?;
            std::io::stderr().flush().ok();

            let all_finished = app_state.branches.iter().all(|b| b.test.is_finished());

            if all_finished {
                break;
            }
            tokio::select! {
                _ = interval.tick() => {}
                _ = tokio::signal::ctrl_c(), if !ctrl_c_fired => {
                    ctrl_c_fired = true;
                    abort_reason = Some("Interrupted".into());
                    cancel.store(true, Ordering::SeqCst);
                    mark_remaining_skipped(&mut app_state);
                }
            }
        }
        let _ = terminal.show_cursor();

        for _ in 0..terminal_height + 1 {
            eprintln!();
        }

        print_final_report(&app_state);

        let had_failure = app_state
            .branches
            .iter()
            .any(|b| matches!(b.test, TaskState::Failed { .. }));

        if let Some(reason) = abort_reason {
            bail!(reason);
        }

        if had_failure {
            bail!("One or more tests failed");
        }

        Ok(())
    }
}