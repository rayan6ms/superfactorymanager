use crate::worktree::{get_sorted_worktrees, Worktree};
use color_eyre::owo_colors::OwoColorize;
use eyre::{Context, bail};
use facet::Facet;
use figue as args;
use std::process::{Command, Stdio};
use std::time::{Duration, Instant};
use tracing::info;

/// Build status for a single worktree
#[derive(Debug, Clone)]
enum BuildStatus {
    Success { duration: Duration },
    Failed { duration: Duration },
    Skipped,
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
fn compile_worktree(worktree: &Worktree) -> eyre::Result<BuildStatus> {
    let minecraft_dir = worktree.path.join("platform").join("minecraft");

    // Check if the minecraft directory exists
    if !minecraft_dir.exists() {
        info!(
            "Skipping {} - platform/minecraft directory not found",
            worktree.branch
        );
        return Ok(BuildStatus::Skipped);
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
            worktree.branch,
            minecraft_dir.display()
        );
        return Ok(BuildStatus::Skipped);
    }

    println!(
        "{} {} {}",
        "Compiling".cyan().bold(),
        worktree.branch.yellow().bold(),
        format!("({})", minecraft_dir.display()).dimmed()
    );

    let start = Instant::now();

    let status = Command::new(&gradlew)
        .arg("compileJava")
        .current_dir(&minecraft_dir)
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .status()
        .wrap_err_with(|| {
            format!(
                "Failed to run gradlew compileJava in {}",
                minecraft_dir.display()
            )
        })?;

    let duration = start.elapsed();

    if status.success() {
        Ok(BuildStatus::Success { duration })
    } else {
        Ok(BuildStatus::Failed { duration })
    }
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
            BuildStatus::Skipped => {
                skipped += 1;
                (
                    "○".yellow().to_string(),
                    "SKIPPED".yellow().to_string(),
                    String::new(),
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

/// Compile command - compiles all worktrees
#[derive(Facet, Debug, Default)]
pub struct CompileCommand {
    /// Continue compiling remaining worktrees even if one fails
    #[facet(args::named)]
    pub keep_going: bool,
}

impl CompileCommand {
    /// # Errors
    ///
    /// Returns an error if compilation fails.
    pub fn invoke(self) -> eyre::Result<()> {
        let worktrees = get_sorted_worktrees()?;

        if worktrees.is_empty() {
            println!("No worktrees found.");
            return Ok(());
        }

        info!(
            "Compiling {} worktree(s): {:?}",
            worktrees.len(),
            worktrees.iter().map(|w| &w.branch).collect::<Vec<_>>()
        );

        let mut results: Vec<BuildResult> = Vec::new();
        let mut had_failure = false;

        for worktree in &worktrees {
            if had_failure && !self.keep_going {
                // Mark remaining as skipped
                results.push(BuildResult {
                    branch: worktree.branch.clone(),
                    status: BuildStatus::Skipped,
                });
                continue;
            }

            let status = compile_worktree(worktree)?;

            if matches!(status, BuildStatus::Failed { .. }) {
                had_failure = true;
            }

            results.push(BuildResult {
                branch: worktree.branch.clone(),
                status,
            });
        }

        print_summary(&results);

        if had_failure {
            bail!("One or more compilations failed");
        }

        Ok(())
    }
}
