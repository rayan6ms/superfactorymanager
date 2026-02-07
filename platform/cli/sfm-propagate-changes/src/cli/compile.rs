use crate::worktree::get_sorted_worktrees;
use color_eyre::owo_colors::OwoColorize;
use eyre::Context;
use eyre::bail;
use facet::Facet;
use figue::{self as args};
use ratatui::Terminal;
use ratatui::TerminalOptions;
use ratatui::Viewport;
use ratatui::backend::CrosstermBackend;
use ratatui::crossterm::ExecutableCommand;
use ratatui::crossterm::style::Attribute;
use ratatui::crossterm::style::Color as CTermColor;
use ratatui::crossterm::style::SetAttribute;
use ratatui::crossterm::style::SetForegroundColor;
use ratatui::layout::Constraint;
use ratatui::style::Color;
use ratatui::style::Modifier;
use ratatui::style::Style;
use ratatui::style::Stylize;
use ratatui::text::Span;
use ratatui::widgets::Block;
use ratatui::widgets::Cell;
use ratatui::widgets::Paragraph;
use ratatui::widgets::Row;
use ratatui::widgets::Table;
use std::io::Write;
use std::io::stderr;
use std::path::PathBuf;
use std::sync::Arc;
use std::sync::atomic::AtomicBool;
use std::sync::atomic::Ordering;
use std::time::Duration;
use std::time::Instant;
use tokio::process::Command;
use tokio::sync::mpsc;
use tokio::task::JoinSet;

#[derive(Debug, Clone, PartialEq, Eq)]
pub(crate) enum TaskState {
    NotStarted,
    Running { start_time: Instant },
    Success { duration: Duration },
    Failed { duration: Duration, error: String },
    Skipped,
    NotFound { reason: String },
}

#[derive(Debug, Clone, Copy)]
pub(crate) enum TaskType {
    Main,
    DataGen,
    GameTest,
}

#[derive(Debug)]
pub(crate) enum Message {
    StatusUpdate(usize, TaskType, TaskState),
    AbortRemaining(String),
}

impl TaskState {
    fn is_finished(&self) -> bool {
        matches!(
            self,
            TaskState::Success { .. }
                | TaskState::Failed { .. }
                | TaskState::Skipped
                | TaskState::NotFound { .. }
        )
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
                    String::new()
                }
            }
            TaskState::NotFound { reason } => format!("({reason})"),
            _ => String::new(),
        }
    }
}

pub(crate) struct BranchState {
    pub(crate) branch: String,
    pub(crate) main: TaskState,
    pub(crate) datagen: TaskState,
    pub(crate) gametest: TaskState,
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
        }
        return Err((duration, format!("Mock failure for {task}")));
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
        Err(e) => Err((duration, format!("Failed to start {task}: {e}"))),
    }
}

#[expect(
    clippy::too_many_lines,
    reason = "Detailed task orchestration is easier to follow inline."
)]
async fn compile_worktree_task(
    index: usize,
    tx: mpsc::UnboundedSender<Message>,
    path: PathBuf,
    all: bool,
    mock: bool,
    parallel: bool,
) -> eyre::Result<bool> {
    let minecraft_dir = path.join("platform").join("minecraft");

    if !minecraft_dir.exists() {
        tx.send(Message::StatusUpdate(
            index,
            TaskType::Main,
            TaskState::NotFound {
                reason: "platform/minecraft not found".into(),
            },
        ))
        .wrap_err("Failed to send missing minecraft status")?;
        if all {
            tx.send(Message::StatusUpdate(
                index,
                TaskType::DataGen,
                TaskState::Skipped,
            ))
            .wrap_err("Failed to send skipped datagen status")?;
            tx.send(Message::StatusUpdate(
                index,
                TaskType::GameTest,
                TaskState::Skipped,
            ))
            .wrap_err("Failed to send skipped gametest status")?;
        }
        return Ok(false);
    }

    let gradlew = if cfg!(windows) {
        minecraft_dir.join("gradlew.bat")
    } else {
        minecraft_dir.join("gradlew")
    };

    if !gradlew.exists() {
        tx.send(Message::StatusUpdate(
            index,
            TaskType::Main,
            TaskState::NotFound {
                reason: format!("gradlew not found in {}", minecraft_dir.display()),
            },
        ))
        .wrap_err("Failed to send missing gradlew status")?;
        if all {
            tx.send(Message::StatusUpdate(
                index,
                TaskType::DataGen,
                TaskState::Skipped,
            ))
            .wrap_err("Failed to send skipped datagen status")?;
            tx.send(Message::StatusUpdate(
                index,
                TaskType::GameTest,
                TaskState::Skipped,
            ))
            .wrap_err("Failed to send skipped gametest status")?;
        }
        return Ok(false);
    }

    // MAIN
    tx.send(Message::StatusUpdate(
        index,
        TaskType::Main,
        TaskState::Running {
            start_time: Instant::now(),
        },
    ))
    .wrap_err("Failed to send main running status")?;

    let main_res = run_task(&gradlew, &minecraft_dir, "compileJava", mock).await;
    match main_res {
        Ok(duration) => {
            tx.send(Message::StatusUpdate(
                index,
                TaskType::Main,
                TaskState::Success { duration },
            ))
            .wrap_err("Failed to send main success status")?;
        }
        Err((duration, err)) => {
            tx.send(Message::StatusUpdate(
                index,
                TaskType::Main,
                TaskState::Failed {
                    duration,
                    error: err,
                },
            ))
            .wrap_err("Failed to send main failure status")?;
            if all {
                tx.send(Message::StatusUpdate(
                    index,
                    TaskType::DataGen,
                    TaskState::Skipped,
                ))
                .wrap_err("Failed to send skipped datagen status")?;
                tx.send(Message::StatusUpdate(
                    index,
                    TaskType::GameTest,
                    TaskState::Skipped,
                ))
                .wrap_err("Failed to send skipped gametest status")?;
            }
            return Ok(false);
        }
    }

    if all {
        // DATAGEN and GAMETEST
        if parallel {
            tx.send(Message::StatusUpdate(
                index,
                TaskType::DataGen,
                TaskState::Running {
                    start_time: Instant::now(),
                },
            ))
            .wrap_err("Failed to send datagen running status")?;
            tx.send(Message::StatusUpdate(
                index,
                TaskType::GameTest,
                TaskState::Running {
                    start_time: Instant::now(),
                },
            ))
            .wrap_err("Failed to send gametest running status")?;

            let gradlew_dg = gradlew.clone();
            let dir_dg = minecraft_dir.clone();
            let tx_dg = tx.clone();
            let dg_handle = tokio::spawn(async move {
                let res = run_task(&gradlew_dg, &dir_dg, "compileDatagenJava", mock).await;
                match res {
                    Ok(duration) => {
                        tx_dg
                            .send(Message::StatusUpdate(
                                index,
                                TaskType::DataGen,
                                TaskState::Success { duration },
                            ))
                            .wrap_err("Failed to send datagen success status")?;
                        Ok::<bool, eyre::Report>(true)
                    }
                    Err((duration, err)) => {
                        tx_dg
                            .send(Message::StatusUpdate(
                                index,
                                TaskType::DataGen,
                                TaskState::Failed {
                                    duration,
                                    error: err,
                                },
                            ))
                            .wrap_err("Failed to send datagen failure status")?;
                        Ok::<bool, eyre::Report>(false)
                    }
                }
            });

            let gradlew_gt = gradlew.clone();
            let dir_gt = minecraft_dir.clone();
            let tx_gt = tx.clone();
            let gt_handle = tokio::spawn(async move {
                let res = run_task(&gradlew_gt, &dir_gt, "compileGametestJava", mock).await;
                match res {
                    Ok(duration) => {
                        tx_gt
                            .send(Message::StatusUpdate(
                                index,
                                TaskType::GameTest,
                                TaskState::Success { duration },
                            ))
                            .wrap_err("Failed to send gametest success status")?;
                        Ok::<bool, eyre::Report>(true)
                    }
                    Err((duration, err)) => {
                        tx_gt
                            .send(Message::StatusUpdate(
                                index,
                                TaskType::GameTest,
                                TaskState::Failed {
                                    duration,
                                    error: err,
                                },
                            ))
                            .wrap_err("Failed to send gametest failure status")?;
                        Ok::<bool, eyre::Report>(false)
                    }
                }
            });

            let (dg_ok, gt_ok) = tokio::join!(dg_handle, gt_handle);
            let dg_ok = dg_ok.wrap_err("Datagen task panicked")??;
            let gt_ok = gt_ok.wrap_err("Gametest task panicked")??;
            return Ok(dg_ok && gt_ok);
        }

        // Serial execution within the worktree
        tx.send(Message::StatusUpdate(
            index,
            TaskType::DataGen,
            TaskState::Running {
                start_time: Instant::now(),
            },
        ))
        .wrap_err("Failed to send datagen running status")?;
        let res_dg = run_task(&gradlew, &minecraft_dir, "compileDatagenJava", mock).await;
        match res_dg {
            Ok(duration) => {
                tx.send(Message::StatusUpdate(
                    index,
                    TaskType::DataGen,
                    TaskState::Success { duration },
                ))
                .wrap_err("Failed to send datagen success status")?;
            }
            Err((duration, err)) => {
                tx.send(Message::StatusUpdate(
                    index,
                    TaskType::DataGen,
                    TaskState::Failed {
                        duration,
                        error: err,
                    },
                ))
                .wrap_err("Failed to send datagen failure status")?;
                tx.send(Message::StatusUpdate(
                    index,
                    TaskType::GameTest,
                    TaskState::Skipped,
                ))
                .wrap_err("Failed to send gametest skipped status")?;
                return Ok(false);
            }
        }

        tx.send(Message::StatusUpdate(
            index,
            TaskType::GameTest,
            TaskState::Running {
                start_time: Instant::now(),
            },
        ))
        .wrap_err("Failed to send gametest running status")?;
        let res_gt = run_task(&gradlew, &minecraft_dir, "compileGametestJava", mock).await;
        match res_gt {
            Ok(duration) => {
                tx.send(Message::StatusUpdate(
                    index,
                    TaskType::GameTest,
                    TaskState::Success { duration },
                ))
                .wrap_err("Failed to send gametest success status")?;
            }
            Err((duration, err)) => {
                tx.send(Message::StatusUpdate(
                    index,
                    TaskType::GameTest,
                    TaskState::Failed {
                        duration,
                        error: err,
                    },
                ))
                .wrap_err("Failed to send gametest failure status")?;
                return Ok(false);
            }
        }
    }

    Ok(true)
}

fn mark_remaining_skipped(state: &mut AppState) {
    for branch in &mut state.branches {
        if !branch.main.is_finished() {
            branch.main = TaskState::Skipped;
        }
        if !branch.datagen.is_finished() {
            branch.datagen = TaskState::Skipped;
        }
        if !branch.gametest.is_finished() {
            branch.gametest = TaskState::Skipped;
        }
    }
}

fn render(f: &mut ratatui::Frame, state: &AppState) {
    let area = f.area();

    if state.branches.is_empty() {
        f.render_widget(Paragraph::new("No branches to display"), area);
        return;
    }

    // Constrain the table width so the border doesn't stretch across the whole terminal
    let [table_area, _] = ratatui::layout::Layout::horizontal([
        Constraint::Length(80), // Increased from 72 to accommodate longer statuses
        Constraint::Min(0),
    ])
    .areas(area);

    let mut rows = Vec::new();
    for b in &state.branches {
        let branch_name = Span::styled(
            b.branch.clone(),
            Style::default()
                .fg(Color::Yellow)
                .add_modifier(Modifier::BOLD),
        );
        let mut row_cells = vec![Cell::from(branch_name)];

        for task in &[&b.main, &b.datagen, &b.gametest] {
            let text = format!("{} {}", task.status_text(), task.elapsed_str());
            row_cells.push(Cell::from(text).style(Style::default().fg(task.get_color())));
        }

        rows.push(Row::new(row_cells));
    }

    let table = Table::new(
        rows,
        [
            Constraint::Length(12),
            Constraint::Length(20), // Increased from 18
            Constraint::Length(20), // Increased from 18
            Constraint::Length(20), // Increased from 18
        ],
    )
    .header(
        Row::new(vec!["BRANCH", "MAIN", "DATAGEN", "GAMETEST"])
            .style(Style::default().add_modifier(Modifier::BOLD).underlined()),
    )
    .block(Block::bordered().title(format!(" Compilation Status ({}) ", state.branches.len())));

    f.render_widget(table, table_area);
}

#[expect(
    clippy::too_many_lines,
    reason = "Verbose report output is clearer inline."
)]
fn print_final_report(state: &AppState) {
    let mut stdout = std::io::stdout();

    let mut successful_branches = 0;
    let mut failed_branches = 0;
    let mut skipped_branches = 0;

    for b in &state.branches {
        let tasks = [&b.main, &b.datagen, &b.gametest];
        if tasks.iter().any(|t| matches!(t, TaskState::Failed { .. })) {
            failed_branches += 1;
        } else if tasks
            .iter()
            .all(|t| matches!(t, TaskState::Success { .. } | TaskState::Skipped))
        {
            successful_branches += 1;
        } else {
            skipped_branches += 1;
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
    println!("  COMPILE SUMMARY");
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
        print!("{successful_branches} successful");
        let _ = stdout.execute(SetAttribute(Attribute::Reset));
        if failed_branches > 0 || skipped_branches > 0 {
            print!(", ");
        }
    }
    if failed_branches > 0 {
        let _ = stdout.execute(SetForegroundColor(CTermColor::Red));
        print!("{failed_branches} failed");
        let _ = stdout.execute(SetAttribute(Attribute::Reset));
        if skipped_branches > 0 {
            print!(", ");
        }
    }
    if skipped_branches > 0 {
        let _ = stdout.execute(SetForegroundColor(CTermColor::Yellow));
        print!("{skipped_branches} skipped");
        let _ = stdout.execute(SetAttribute(Attribute::Reset));
    }
    println!(")");

    // Error details
    let mut failures = Vec::new();
    for b in &state.branches {
        if let TaskState::Failed { error, .. } = &b.main {
            failures.push((&b.branch, "MAIN", error, CTermColor::Cyan));
        }
        if let TaskState::Failed { error, .. } = &b.datagen {
            failures.push((&b.branch, "DATAGEN", error, CTermColor::Magenta));
        }
        if let TaskState::Failed { error, .. } = &b.gametest {
            failures.push((&b.branch, "GAMETEST", error, CTermColor::Blue));
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

        for (branch, task_name, err, task_color) in failures {
            println!();
            let _ = stdout.execute(SetForegroundColor(CTermColor::Red));
            let _ = stdout.execute(SetAttribute(Attribute::Bold));
            print!("✖ ERROR ");
            let _ = stdout.execute(SetAttribute(Attribute::Reset));

            let _ = stdout.execute(SetForegroundColor(task_color));
            let _ = stdout.execute(SetAttribute(Attribute::Bold));
            print!("{task_name}");
            let _ = stdout.execute(SetAttribute(Attribute::Reset));

            print!(" on branch ");
            let _ = stdout.execute(SetForegroundColor(CTermColor::Yellow));
            let _ = stdout.execute(SetAttribute(Attribute::Bold));
            print!("{branch}");
            let _ = stdout.execute(SetAttribute(Attribute::Reset));
            println!(":");

            // Indent and dim the actual error message
            let _ = stdout.execute(SetForegroundColor(CTermColor::Grey));
            for line in err.lines() {
                println!("   {line}");
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
        println!("  ✔ All compilations finished successfully!");
        let _ = stdout.execute(SetAttribute(Attribute::Reset));
    }
    println!();
}

/// Compile command - compiles all worktrees in parallel
#[derive(Facet, Debug, Default)]
pub struct CompileCommand {
    /// If set, run additional compilation tasks: `compileDatagenJava` and `compileGametestJava`.
    #[facet(args::named)]
    pub all: bool,

    /// If set, mock the compilation process with random results and durations.
    #[facet(args::named)]
    pub mock: bool,

    /// If set, run compilation tasks in parallel.
    #[facet(args::named)]
    pub parallel: bool,
}

impl CompileCommand {
    /// # Errors
    ///
    /// Returns an error if compilation fails.
    pub fn invoke(self) -> eyre::Result<()> {
        let rt = tokio::runtime::Builder::new_multi_thread()
            .enable_all()
            .build()
            .wrap_err("Failed to create tokio runtime")?;

        rt.block_on(self.invoke_async())
    }

    #[expect(
        clippy::too_many_lines,
        reason = "TUI orchestration is easier to follow inline."
    )]
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
                main: TaskState::NotStarted,
                datagen: if self.all {
                    TaskState::NotStarted
                } else {
                    TaskState::Skipped
                },
                gametest: if self.all {
                    TaskState::NotStarted
                } else {
                    TaskState::Skipped
                },
            })
            .collect();
        branches.sort_by(|a, b| a.branch.cmp(&b.branch));

        eprintln!("Starting TUI for {} branches...", branches.len());

        let (tx, mut rx) = mpsc::unbounded_channel();
        let mut app_state = AppState { branches };

        let backend = CrosstermBackend::new(stderr());
        let terminal_height = u16::try_from(worktrees.len() + 3).unwrap_or(u16::MAX);
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

        let all = self.all;
        let mock = self.mock;
        let parallel = self.parallel;
        let p_tx = tx.clone();
        let cancel = Arc::new(AtomicBool::new(false));
        let cancel_worker = Arc::clone(&cancel);

        // Spawn a manager task to handle the execution strategy
        let manager = tokio::spawn(async move {
            if parallel {
                let mut join_set = JoinSet::new();
                for (i, path) in branches_info {
                    if cancel_worker.load(Ordering::SeqCst) {
                        break;
                    }
                    let tx = p_tx.clone();
                    join_set.spawn(async move {
                        compile_worktree_task(i, tx, path, all, mock, true).await
                    });
                }

                while let Some(res) = join_set.join_next().await {
                    if cancel_worker.load(Ordering::SeqCst) {
                        join_set.abort_all();
                        break;
                    }
                    if let Ok(Ok(true)) = res {
                    } else {
                        cancel_worker.store(true, Ordering::SeqCst);
                        join_set.abort_all();
                        p_tx.send(Message::AbortRemaining("Stopped after failure".into()))
                            .wrap_err("Failed to send abort message")?;
                        break;
                    }
                }
            } else {
                for (i, path) in branches_info {
                    if cancel_worker.load(Ordering::SeqCst) {
                        break;
                    }
                    let ok = compile_worktree_task(i, p_tx.clone(), path, all, mock, false).await?;
                    if !ok {
                        cancel_worker.store(true, Ordering::SeqCst);
                        p_tx.send(Message::AbortRemaining("Stopped after failure".into()))
                            .wrap_err("Failed to send abort message")?;
                        break;
                    }
                }
            }
            Ok::<(), eyre::Report>(())
        });

        // TUI loop
        let mut interval = tokio::time::interval(Duration::from_millis(100));
        let mut abort_reason: Option<String> = None;
        let mut ctrl_c_fired = false;
        loop {
            // Handle all pending messages
            while let Ok(msg) = rx.try_recv() {
                match msg {
                    Message::StatusUpdate(index, task_type, state) => {
                        if abort_reason.is_none() {
                            let branch = &mut app_state.branches[index];
                            match task_type {
                                TaskType::Main => branch.main = state,
                                TaskType::DataGen => branch.datagen = state,
                                TaskType::GameTest => branch.gametest = state,
                            }
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
            let _ = std::io::stderr().flush();

            let all_finished = app_state.branches.iter().all(|b| {
                b.main.is_finished() && b.datagen.is_finished() && b.gametest.is_finished()
            });

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

        // Move the cursor past the TUI viewport on stderr so that error messages
        // don't overwrite the table, even if stdout is being redirected/captured.
        for _ in 0..=terminal_height {
            eprintln!();
        }

        // Ensure the manager task completed without errors.
        manager.await.wrap_err("Manager task panicked")??;

        // Print final report to stdout
        print_final_report(&app_state);

        let had_failure = app_state.branches.iter().any(|b| {
            matches!(b.main, TaskState::Failed { .. })
                || matches!(b.datagen, TaskState::Failed { .. })
                || matches!(b.gametest, TaskState::Failed { .. })
        });

        if let Some(reason) = abort_reason {
            bail!(reason);
        }

        if had_failure {
            bail!("One or more compilations failed");
        }

        Ok(())
    }
}

// ----------------------------------------------------------------------------
// Compatibility types and functions for datagen.rs
// ----------------------------------------------------------------------------

/// Build status for a single worktree
#[derive(Debug, Clone)]
pub(crate) enum BuildStatus {
    Success { duration: Duration },
    Failed { duration: Duration },
    NotFound { reason: String },
}

/// Result of building a worktree, with per-task statuses
#[derive(Debug, Clone)]
pub(crate) struct BuildResult {
    pub(crate) branch: String,
    pub(crate) main: BuildStatus,
    pub(crate) datagen: Option<BuildStatus>,
    pub(crate) gametest: Option<BuildStatus>,
    #[expect(dead_code, reason = "Reserved for summary output")]
    pub(crate) duration: Duration,
}

/// Print the summary of all results (used by datagen.rs)
pub(crate) fn print_summary(results: &[BuildResult]) {
    println!();
    println!("{}", "═".repeat(60).dimmed());
    println!("{}", "  SUMMARY".bold());
    println!("{}", "═".repeat(60).dimmed());
    println!();

    let mut rows: Vec<(String, String, String, String)> = Vec::new();
    for r in results {
        let fmt = |s: &Option<BuildStatus>| -> String {
            match s {
                Some(BuildStatus::Success { duration }) => {
                    format!("SUCCESS ({})", format_duration(*duration))
                }
                Some(BuildStatus::Failed { duration }) => {
                    format!("FAILED ({})", format_duration(*duration))
                }
                Some(BuildStatus::NotFound { reason }) => {
                    format!("NOT FOUND ({reason})")
                }
                None => "SKIPPED".to_string(),
            }
        };

        let main_cell = fmt(&Some(r.main.clone()));
        let datagen_cell = fmt(&r.datagen);
        let gametest_cell = fmt(&r.gametest);

        rows.push((r.branch.clone(), main_cell, datagen_cell, gametest_cell));
    }

    let branch_w = rows
        .iter()
        .map(|(b, _, _, _)| b.len())
        .max()
        .unwrap_or(6)
        .max("BRANCH".len());
    let main_w = rows
        .iter()
        .map(|(_, m, _, _)| m.len())
        .max()
        .unwrap_or(4)
        .max("MAIN".len());
    let datagen_w = rows
        .iter()
        .map(|(_, _, d, _)| d.len())
        .max()
        .unwrap_or(6)
        .max("DATAGEN".len());
    let gametest_w = rows
        .iter()
        .map(|(_, _, _, g)| g.len())
        .max()
        .unwrap_or(7)
        .max("GAMETEST".len());

    println!(
        "{:<branch_w$}  {:<main_w$}  {:<datagen_w$}  {:<gametest_w$}",
        "BRANCH", "MAIN", "DATAGEN", "GAMETEST"
    );

    for (b, m, d, g) in rows {
        println!("{b:<branch_w$}  {m:<main_w$}  {d:<datagen_w$}  {g:<gametest_w$}");
    }
}
