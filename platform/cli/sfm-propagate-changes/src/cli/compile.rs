use crate::worktree::{get_sorted_worktrees, Worktree};
use color_eyre::owo_colors::OwoColorize;
use eyre::{Context, bail};
use facet::Facet;
use std::path::PathBuf;
use std::time::{Duration, Instant};
use tokio::process::Command;
use tokio::task::JoinSet;
use tracing::info;

/// Build status for a single worktree
#[derive(Debug, Clone)]
enum BuildStatus {
    Success { duration: Duration },
    Failed { duration: Duration },
    NotFound { reason: String },
}

/// Result of building a worktree
#[derive(Debug, Clone)]
struct BuildResult {
    branch: String,
    status: BuildStatus,
}

/// Format a duration as a human-readable string
fn format_duration(duration: Duration) -> String {
    let secs = duration.as_secs();
    if secs >= 60 {
        let mins = secs / 60;
        let remaining_secs = secs % 60;
        format!("{}m {:02}s", mins, remaining_secs)
    } else {
        format!("{}.{:01}s", secs, duration.subsec_millis() / 100)
    }
}

/// Run gradle compile for a worktree
async fn compile_worktree(branch: String, path: PathBuf) -> BuildResult {
    let minecraft_dir = path.join("platform").join("minecraft");

    // Check if the minecraft directory exists
    if !minecraft_dir.exists() {
        info!(
            "Skipping {} - platform/minecraft directory not found",
            branch
        );
        return BuildResult {
            branch,
            status: BuildStatus::NotFound {
                reason: "platform/minecraft directory not found".to_string(),
            },
        };
    }

    let gradlew = if cfg!(windows) {
        minecraft_dir.join("gradlew.bat")
    } else {
        minecraft_dir.join("gradlew")
    };

    // Check if gradlew exists
    if !gradlew.exists() {
        info!(
            "Skipping {} - gradlew not found in {}",
            branch,
            minecraft_dir.display()
        );
        return BuildResult {
            branch,
            status: BuildStatus::NotFound {
                reason: format!("gradlew not found in {}", minecraft_dir.display()),
            },
        };
    }

    println!(
        "{} {} {}",
        "Compiling".cyan().bold(),
        branch.yellow().bold(),
        format!("({})", minecraft_dir.display()).dimmed()
    );

    let start = Instant::now();

    let result = Command::new(&gradlew)
        .arg("compileJava")
        .current_dir(&minecraft_dir)
        .output()
        .await;

    let duration = start.elapsed();

    let status = match result {
        Ok(output) if output.status.success() => BuildStatus::Success { duration },
        Ok(_) => BuildStatus::Failed { duration },
        Err(e) => {
            info!("Failed to run gradlew for {}: {}", branch, e);
            BuildStatus::Failed { duration }
        }
    };

    BuildResult { branch, status }
}

/// Print the summary of all builds
fn print_summary(results: &[BuildResult]) {
    println!();
    println!("{}", "═".repeat(60).dimmed());
    println!("{}", "  COMPILE SUMMARY".bold());
    println!("{}", "═".repeat(60).dimmed());
    println!();

    let mut successful = 0;
    let mut failed = 0;
    let mut skipped = 0;

    for result in results {
        let (status_icon, status_text, duration_text) = match &result.status {
            BuildStatus::Success { duration } => {
                successful += 1;
                (
                    "✓".green().bold().to_string(),
                    "SUCCESS".green().bold().to_string(),
                    format!(" ({})", format_duration(*duration)).dimmed().to_string(),
                )
            }
            BuildStatus::Failed { duration } => {
                failed += 1;
                (
                    "✗".red().bold().to_string(),
                    "FAILED".red().bold().to_string(),
                    format!(" ({})", format_duration(*duration)).dimmed().to_string(),
                )
            }
            BuildStatus::NotFound { reason } => {
                skipped += 1;
                (
                    "○".yellow().to_string(),
                    "NOT FOUND".yellow().to_string(),
                    format!(" ({})", reason).dimmed().to_string(),
                )
            }
        };

        println!(
            "  {} {:12} {}{}",
            status_icon,
            result.branch.bold(),
            status_text,
            duration_text
        );
    }

    println!();
    println!("{}", "─".repeat(60).dimmed());

    // Print totals
    let total = results.len();
    print!("  Total: {} ", total.to_string().bold());

    let mut parts = Vec::new();
    if successful > 0 {
        parts.push(format!("{} {}", successful, "successful".green()));
    }
    if failed > 0 {
        parts.push(format!("{} {}", failed, "failed".red()));
    }
    if skipped > 0 {
        parts.push(format!("{} {}", skipped, "skipped".yellow()));
    }

    println!("({})", parts.join(", "));
    println!();
}

/// Compile command - compiles all worktrees in parallel
#[derive(Facet, Debug, Default)]
pub struct CompileCommand {}

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

    async fn invoke_async(self) -> eyre::Result<()> {
        let worktrees = get_sorted_worktrees()?;

        if worktrees.is_empty() {
            println!("No worktrees found.");
            return Ok(());
        }

        info!(
            "Compiling {} worktree(s) in parallel: {:?}",
            worktrees.len(),
            worktrees.iter().map(|w| &w.branch).collect::<Vec<_>>()
        );

        let mut join_set: JoinSet<BuildResult> = JoinSet::new();

        // Spawn all compile tasks
        for worktree in worktrees {
            let branch = worktree.branch.clone();
            let path = worktree.path.clone();
            join_set.spawn(compile_worktree(branch, path));
        }

        // Collect all results
        let mut results: Vec<BuildResult> = Vec::new();
        while let Some(result) = join_set.join_next().await {
            match result {
                Ok(build_result) => results.push(build_result),
                Err(e) => {
                    info!("Task panicked: {}", e);
                }
            }
        }

        // Sort results by branch name for consistent output
        results.sort_by(|a, b| a.branch.cmp(&b.branch));

        print_summary(&results);

        let had_failure = results
            .iter()
            .any(|r| matches!(r.status, BuildStatus::Failed { .. }));

        if had_failure {
            bail!("One or more compilations failed");
        }

        Ok(())
    }
}
