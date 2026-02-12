use crate::worktree::get_sorted_worktrees;
use color_eyre::owo_colors::OwoColorize;
use eyre::Context;
use eyre::bail;
use facet::Facet;
use figue::{self as args};
use std::fmt::Write as _;
use std::path::Path;
use std::process::ExitStatus;
use std::process::Stdio;
use std::time::Duration;
use std::time::Instant;
use tokio::io::AsyncBufReadExt;
use tokio::io::AsyncRead;
use tokio::io::BufReader;
use tokio::process::Command;
use tracing::debug;
use tracing::error;
use tracing::info;
use tracing::warn;

#[derive(Debug, Clone)]
struct TaskRun {
    task: GradleTask,
    state: TaskState,
}

#[derive(Debug, Clone)]
struct BranchRun {
    branch: String,
    tasks: Vec<TaskRun>,
}

#[derive(Debug, Clone)]
enum TaskState {
    Waiting,
    UpNext,
    Running { start_time: Instant },
    Success { duration: Duration },
    Failed { duration: Duration },
    NotFound { reason: String },
    Skipped,
}

#[derive(Debug, Clone)]
enum GradleTask {
    RunData,
    RunGameTestServer,
    Other(String),
}

#[derive(Debug)]
struct TaskOutput {
    status: ExitStatus,
    stdout: String,
    stderr: String,
    duration: Duration,
}

#[derive(Debug)]
struct TaskError {
    message: String,
    output: Option<TaskOutput>,
}

impl GradleTask {
    fn from_input(input: &str) -> Self {
        match input.to_ascii_lowercase().as_str() {
            "rundata" => Self::RunData,
            "rungametestserver" | "rungametest" => Self::RunGameTestServer,
            _ => Self::Other(input.to_string()),
        }
    }

    fn as_gradle_arg(&self) -> &str {
        match self {
            Self::RunData => "runData",
            Self::RunGameTestServer => "runGameTestServer",
            Self::Other(task) => task,
        }
    }

    fn header_name(&self) -> String {
        self.as_gradle_arg().to_string()
    }

    fn is_success(&self, output: &TaskOutput) -> bool {
        let combined = format!("{}\n{}", output.stdout, output.stderr);

        match self {
            Self::RunData => {
                output.status.success()
                    || combined.contains("BUILD SUCCESSFUL")
                    || combined.contains("All providers took")
            }
            Self::RunGameTestServer => has_gametest_success(&combined),
            Self::Other(_) => output.status.success() || combined.contains("BUILD SUCCESSFUL"),
        }
    }
}

impl TaskState {
    fn plain_text(&self) -> String {
        match self {
            Self::Waiting => "waiting".to_string(),
            Self::UpNext => "up next".to_string(),
            Self::Running { start_time } => {
                format!("running ({})", format_duration(start_time.elapsed()))
            }
            Self::Success { duration } => format_duration(*duration),
            Self::Failed { duration } => format!("failed ({})", format_duration(*duration)),
            Self::NotFound { reason } => format!("not found ({reason})"),
            Self::Skipped => "skipped".to_string(),
        }
    }

    fn colorized_text(&self) -> String {
        let text = self.plain_text();
        match self {
            Self::Waiting | Self::Skipped => text.dimmed().to_string(),
            Self::UpNext => text.yellow().bold().to_string(),
            Self::Running { .. } => text.yellow().to_string(),
            Self::Success { .. } => text.green().bold().to_string(),
            Self::Failed { .. } => text.red().bold().to_string(),
            Self::NotFound { .. } => text.magenta().to_string(),
        }
    }
}

fn has_gametest_success(output: &str) -> bool {
    let prefix = "All ";
    let suffix = " required tests passed :)";

    for (start, _) in output.match_indices(prefix) {
        let after_prefix = &output[start + prefix.len()..];
        if let Some(end) = after_prefix.find(suffix) {
            let number = &after_prefix[..end];
            if !number.is_empty() && number.chars().all(|c| c.is_ascii_digit()) {
                return true;
            }
        }
    }

    false
}

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

fn format_report(branches: &[BranchRun], tasks: &[GradleTask]) -> String {
    let mut widths = Vec::with_capacity(tasks.len() + 1);
    widths.push(
        std::iter::once("mc version".len())
            .chain(branches.iter().map(|b| b.branch.len()))
            .max()
            .unwrap_or("mc version".len()),
    );

    for (task_index, task) in tasks.iter().enumerate() {
        let header = task.header_name();
        let max_cell = branches
            .iter()
            .filter_map(|b| b.tasks.get(task_index))
            .map(|t| t.state.plain_text().len())
            .max()
            .unwrap_or(0);
        widths.push(header.len().max(max_cell));
    }

    let mut out = String::new();
    let _ = writeln!(out, "{}", build_header_row(tasks, &widths).cyan().bold());
    for branch in branches {
        let _ = writeln!(out, "{}", build_branch_row(branch, &widths));
    }
    out
}

fn build_header_row(tasks: &[GradleTask], widths: &[usize]) -> String {
    let mut cells = Vec::with_capacity(tasks.len() + 1);
    cells.push(format!("{:width$}", "mc version", width = widths[0]));

    for (idx, task) in tasks.iter().enumerate() {
        cells.push(format!(
            "{:width$}",
            task.header_name(),
            width = widths[idx + 1]
        ));
    }

    cells.join(" | ")
}

fn build_branch_row(branch: &BranchRun, widths: &[usize]) -> String {
    let mut row = format!("{:width$}", branch.branch, width = widths[0]);

    for (idx, task) in branch.tasks.iter().enumerate() {
        row.push_str(" | ");

        let plain = task.state.plain_text();
        let styled = task.state.colorized_text();
        let padding = widths[idx + 1].saturating_sub(plain.len());

        row.push_str(&styled);
        if padding > 0 {
            row.push_str(&" ".repeat(padding));
        }
    }

    row
}

fn print_report_to_stderr(branches: &[BranchRun], tasks: &[GradleTask]) {
    eprintln!();
    eprintln!("{}", format_report(branches, tasks));
}

fn print_report_to_stdout(branches: &[BranchRun], tasks: &[GradleTask]) {
    println!();
    println!("{}", format_report(branches, tasks));
}

async fn collect_output<R>(reader: R, hide_logs: bool) -> std::io::Result<String>
where
    R: AsyncRead + Unpin,
{
    let mut lines = BufReader::new(reader).lines();
    let mut out = String::new();

    while let Some(line) = lines.next_line().await? {
        if !hide_logs {
            eprintln!("{line}");
        }
        out.push_str(&line);
        out.push('\n');
    }

    Ok(out)
}

async fn run_gradle_task(
    gradlew: &Path,
    minecraft_dir: &Path,
    task: &GradleTask,
    hide_logs: bool,
) -> Result<TaskOutput, TaskError> {
    debug!(
        path = %minecraft_dir.display(),
        task = %task.as_gradle_arg(),
        "Running task"
    );

    if !hide_logs {
        eprintln!(
            "{}",
            format!("━━━ {} ({})", task.as_gradle_arg(), minecraft_dir.display())
                .cyan()
                .bold()
        );
    }

    let start = Instant::now();
    let mut child = Command::new(gradlew)
        .arg(task.as_gradle_arg())
        .current_dir(minecraft_dir)
        .kill_on_drop(true)
        .stdout(Stdio::piped())
        .stderr(Stdio::piped())
        .spawn()
        .map_err(|err| TaskError {
            message: format!("Failed to start {}: {err}", task.as_gradle_arg()),
            output: None,
        })?;

    let stdout = child.stdout.take().ok_or_else(|| TaskError {
        message: format!(
            "Failed to capture stdout for {} in {}",
            task.as_gradle_arg(),
            minecraft_dir.display()
        ),
        output: None,
    })?;
    let stderr = child.stderr.take().ok_or_else(|| TaskError {
        message: format!(
            "Failed to capture stderr for {} in {}",
            task.as_gradle_arg(),
            minecraft_dir.display()
        ),
        output: None,
    })?;

    let (status_res, stdout_res, stderr_res) = tokio::join!(
        child.wait(),
        collect_output(stdout, hide_logs),
        collect_output(stderr, hide_logs)
    );

    let status = status_res.map_err(|err| TaskError {
        message: format!(
            "Failed while waiting for {} in {}: {err}",
            task.as_gradle_arg(),
            minecraft_dir.display()
        ),
        output: None,
    })?;

    let stdout = stdout_res.map_err(|err| TaskError {
        message: format!(
            "Failed while reading stdout for {} in {}: {err}",
            task.as_gradle_arg(),
            minecraft_dir.display()
        ),
        output: None,
    })?;

    let stderr = stderr_res.map_err(|err| TaskError {
        message: format!(
            "Failed while reading stderr for {} in {}: {err}",
            task.as_gradle_arg(),
            minecraft_dir.display()
        ),
        output: None,
    })?;

    let task_output = TaskOutput {
        status,
        stdout,
        stderr,
        duration: start.elapsed(),
    };

    if task.is_success(&task_output) {
        Ok(task_output)
    } else {
        Err(TaskError {
            message: format!(
                "{} failed for {} (exit: {:?})",
                task.as_gradle_arg(),
                minecraft_dir.display(),
                task_output.status.code()
            ),
            output: Some(task_output),
        })
    }
}

/// Gradle command - runs arbitrary gradle tasks in each worktree in strict sequence.
#[derive(Facet, Debug, Default)]
pub struct GradleCommand {
    /// Gradle tasks to run (for example: `runData`, `runGameTestServer`, `test`).
    #[facet(args::positional)]
    pub tasks: Vec<String>,

    /// If set, hide stdout of each gradle process while it runs.
    #[facet(rename = "hide-logs", args::named, default = false)]
    pub hide_logs: bool,
}

impl GradleCommand {
    /// # Errors
    ///
    /// Returns an error if any task fails.
    pub fn invoke(self) -> eyre::Result<()> {
        let rt = tokio::runtime::Builder::new_multi_thread()
            .enable_all()
            .build()
            .wrap_err("Failed to create tokio runtime")?;

        rt.block_on(self.invoke_async())
    }

    #[expect(
        clippy::too_many_lines,
        reason = "Control flow and report updates are clearest when kept together."
    )]
    async fn invoke_async(self) -> eyre::Result<()> {
        if self.tasks.is_empty() {
            bail!("No tasks provided. Usage: sfm-propagate-changes gradle <task1> <task2> ...");
        }

        let worktrees = get_sorted_worktrees()?;
        if worktrees.is_empty() {
            println!("No worktrees found.");
            return Ok(());
        }

        let tasks: Vec<GradleTask> = self
            .tasks
            .iter()
            .map(|task| GradleTask::from_input(task))
            .collect();

        let mut branches: Vec<BranchRun> = worktrees
            .iter()
            .map(|wt| BranchRun {
                branch: wt.branch.clone(),
                tasks: tasks
                    .iter()
                    .cloned()
                    .map(|task| TaskRun {
                        task,
                        state: TaskState::Waiting,
                    })
                    .collect(),
            })
            .collect();

        info!(
            tasks = ?self.tasks,
            worktree_count = worktrees.len(),
            "Running gradle tasks in strict sequence"
        );

        for (branch_idx, wt) in worktrees.iter().enumerate() {
            let minecraft_dir = wt.path.join("platform").join("minecraft");
            let gradlew = if cfg!(windows) {
                minecraft_dir.join("gradlew.bat")
            } else {
                minecraft_dir.join("gradlew")
            };

            if !minecraft_dir.exists() {
                let reason = "platform/minecraft not found".to_string();
                warn!(branch = %wt.branch, path = %wt.path.display(), "{reason}");
                for task in &mut branches[branch_idx].tasks {
                    task.state = TaskState::NotFound {
                        reason: reason.clone(),
                    };
                }
                print_report_to_stderr(&branches, &tasks);
                continue;
            }

            if !gradlew.exists() {
                let reason = format!("gradlew not found in {}", minecraft_dir.display());
                warn!(branch = %wt.branch, path = %minecraft_dir.display(), "{reason}");
                for task in &mut branches[branch_idx].tasks {
                    task.state = TaskState::NotFound {
                        reason: reason.clone(),
                    };
                }
                print_report_to_stderr(&branches, &tasks);
                continue;
            }

            for task_idx in 0..tasks.len() {
                branches[branch_idx].tasks[task_idx].state = TaskState::UpNext;
                print_report_to_stderr(&branches, &tasks);

                branches[branch_idx].tasks[task_idx].state = TaskState::Running {
                    start_time: Instant::now(),
                };

                let current_task = branches[branch_idx].tasks[task_idx].task.clone();
                debug!(
                    branch = %wt.branch,
                    path = %minecraft_dir.display(),
                    task = %current_task.as_gradle_arg(),
                    "Starting gradle task"
                );

                let result =
                    run_gradle_task(&gradlew, &minecraft_dir, &current_task, self.hide_logs).await;

                match result {
                    Ok(output) => {
                        branches[branch_idx].tasks[task_idx].state = TaskState::Success {
                            duration: output.duration,
                        };
                        info!(
                            branch = %wt.branch,
                            task = %current_task.as_gradle_arg(),
                            duration = %format_duration(output.duration),
                            "Task succeeded"
                        );
                        print_report_to_stderr(&branches, &tasks);
                    }
                    Err(err) => {
                        let duration = err
                            .output
                            .as_ref()
                            .map_or_else(|| Duration::from_secs(0), |out| out.duration);
                        branches[branch_idx].tasks[task_idx].state = TaskState::Failed { duration };

                        for remaining in branches[branch_idx].tasks.iter_mut().skip(task_idx + 1) {
                            remaining.state = TaskState::Skipped;
                        }
                        for later_branch in branches.iter_mut().skip(branch_idx + 1) {
                            for task in &mut later_branch.tasks {
                                task.state = TaskState::Skipped;
                            }
                        }

                        error!(
                            branch = %wt.branch,
                            task = %current_task.as_gradle_arg(),
                            error = %err.message,
                            "Task failed"
                        );

                        if let Some(output) = err.output {
                            eprintln!();
                            eprintln!("{}", "FAILED COMMAND OUTPUT".red().bold());
                            eprintln!(
                                "{}",
                                format!(
                                    "branch: {}, task: {}, exit: {:?}",
                                    wt.branch,
                                    current_task.as_gradle_arg(),
                                    output.status.code()
                                )
                                .red()
                            );
                            if !output.stdout.trim().is_empty() {
                                eprintln!("{}", "--- stdout ---".red().bold());
                                eprintln!("{}", output.stdout);
                            }
                            if !output.stderr.trim().is_empty() {
                                eprintln!("{}", "--- stderr ---".red().bold());
                                eprintln!("{}", output.stderr);
                            }
                        }

                        print_report_to_stderr(&branches, &tasks);
                        print_report_to_stdout(&branches, &tasks);
                        bail!(err.message);
                    }
                }
            }
        }

        print_report_to_stdout(&branches, &tasks);
        Ok(())
    }
}
