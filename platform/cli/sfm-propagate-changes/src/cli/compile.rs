use crate::worktree::get_sorted_worktrees;
use color_eyre::owo_colors::OwoColorize;
use eyre::Context;
use eyre::bail;
use facet::Facet;
use figue::{self as args};
use std::path::PathBuf;
use std::time::Duration;
use std::time::Instant;
use tokio::process::Command;
use tokio::task::JoinSet;
use tracing::info;

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
    pub(crate) duration: Duration,
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

/// Run gradle compile for a worktree
async fn compile_worktree(branch: String, path: PathBuf, all: bool) -> BuildResult {
    let minecraft_dir = path.join("platform").join("minecraft");

    // Check if the minecraft directory exists
    if !minecraft_dir.exists() {
        info!(
            "Skipping {} - platform/minecraft directory not found",
            branch
        );
        return BuildResult {
            branch,
            main: BuildStatus::NotFound {
                reason: "platform/minecraft directory not found".to_string(),
            },
            datagen: None,
            gametest: None,
            duration: Duration::from_secs(0),
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
            main: BuildStatus::NotFound {
                reason: format!("gradlew not found in {}", minecraft_dir.display()),
            },
            datagen: None,
            gametest: None,
            duration: Duration::from_secs(0),
        };
    }

    println!(
        "{} {} {}{}",
        "Compiling".cyan().bold(),
        branch.yellow().bold(),
        format!("({})", minecraft_dir.display()).dimmed(),
        if all { " [--all]" } else { "" }
    );

    let start = Instant::now();

    // Helper to run a single gradlew task and return success
    async fn run_task(gradlew: &PathBuf, dir: &PathBuf, task: &str) -> Result<(), String> {
        let output = Command::new(gradlew)
            .arg(task)
            .current_dir(dir)
            .output()
            .await
            .map_err(|e| format!("Failed to run {}: {}", task, e))?;

        if output.status.success() {
            Ok(())
        } else {
            let stdout = String::from_utf8_lossy(&output.stdout);
            let stderr = String::from_utf8_lossy(&output.stderr);
            Err(format!("{} failed: status: {:?}, stdout: {}, stderr: {}", task, output.status, stdout, stderr))
        }
    }

    // Run compileJava first
    let mut ok = match run_task(&gradlew, &minecraft_dir, "compileJava").await {
        Ok(()) => true,
        Err(err) => {
            info!("{}", err);
            false
        }
    };

    // If --all is set, run the additional tasks sequentially
    if ok && all {
        // compileDatagenJava
        if let Err(err) = run_task(&gradlew, &minecraft_dir, "compileDatagenJava").await {
            info!("{}", err);
            ok = false;
        }

        // compileGametestJava
        if ok {
            if let Err(err) = run_task(&gradlew, &minecraft_dir, "compileGametestJava").await {
                info!("{}", err);
                ok = false;
            }
        }
    }

    let duration = start.elapsed();

    let main_status = if ok {
        BuildStatus::Success { duration }
    } else {
        BuildStatus::Failed { duration }
    };

    // datagen and gametest statuses (only present when --all is true)
    let datagen_status = if all {
        match run_task(&gradlew, &minecraft_dir, "compileDatagenJava").await {
            Ok(()) => Some(BuildStatus::Success { duration }),
            Err(err) => {
                info!("{}", err);
                Some(BuildStatus::Failed { duration })
            }
        }
    } else {
        None
    };

    let gametest_status = if all && datagen_status.is_some() && matches!(datagen_status.as_ref().unwrap(), BuildStatus::Success { .. }) {
        match run_task(&gradlew, &minecraft_dir, "compileGametestJava").await {
            Ok(()) => Some(BuildStatus::Success { duration }),
            Err(err) => {
                info!("{}", err);
                Some(BuildStatus::Failed { duration })
            }
        }
    } else {
        None
    };

    BuildResult {
        branch,
        main: main_status,
        datagen: datagen_status,
        gametest: gametest_status,
        duration,
    }
}

/// Print the summary of all builds
pub(crate) fn print_summary(results: &[BuildResult]) {
    println!();
    println!("{}", "═".repeat(60).dimmed());
    println!("{}", "  COMPILE SUMMARY".bold());
    println!("{}", "═".repeat(60).dimmed());
    println!();

    // Print per-task sections
    fn print_task_section<F>(title: &str, results: &[BuildResult], mut get_status: F)
    where
        F: FnMut(&BuildResult) -> Option<&BuildStatus>,
    {
        println!("\n{}", title);

        let mut success_list = Vec::new();
        let mut failed_list = Vec::new();
        let mut skipped_list = Vec::new();

        for r in results {
            match get_status(r) {
                Some(BuildStatus::Success { duration }) => {
                    success_list.push((r.branch.clone(), format_duration(*duration)));
                }
                Some(BuildStatus::Failed { duration }) => {
                    failed_list.push((r.branch.clone(), format_duration(*duration)));
                }
                Some(BuildStatus::NotFound { reason }) => {
                    skipped_list.push((r.branch.clone(), reason.clone()));
                }
                None => {
                    skipped_list.push((r.branch.clone(), "skipped".to_string()));
                }
            }
        }

        if !success_list.is_empty() {
            println!("  SUCCESS:");
            for (b, d) in success_list {
                println!("    {} {} ({})", "✓".green(), b.bold(), d.dimmed());
            }
        }
        if !failed_list.is_empty() {
            println!("  FAILED:");
            for (b, d) in failed_list {
                println!("    {} {} ({})", "✗".red(), b.bold(), d.dimmed());
            }
        }
        if !skipped_list.is_empty() {
            println!("  SKIPPED:");
            for (b, reason) in skipped_list {
                println!("    {} {} ({})", "○".yellow(), b.bold(), reason.dimmed());
            }
        }
    }

    print_task_section("MAIN", results, |r| Some(&r.main));
    print_task_section("DATAGEN", results, |r| r.datagen.as_ref());
    print_task_section("GAMETEST", results, |r| r.gametest.as_ref());

    println!();

    // Print totals summary (overall per-branch success/failed/skipped based on main)
    println!("{}", "─".repeat(60).dimmed());

    let mut successful = 0;
    let mut failed = 0;
    let mut skipped = 0;
    for r in results {
        match &r.main {
            BuildStatus::Success { .. } => successful += 1,
            BuildStatus::Failed { .. } => failed += 1,
            BuildStatus::NotFound { .. } => skipped += 1,
        }
    }

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
pub struct CompileCommand {
    /// If set, run additional compilation tasks: `compileDatagenJava` and `compileGametestJava`.
    #[facet(args::named)]
    pub all: bool,
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
            let all = self.all;
            join_set.spawn(compile_worktree(branch, path, all));
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
            .any(|r| matches!(r.main, BuildStatus::Failed { .. }));

        if had_failure {
            bail!("One or more compilations failed");
        }

        Ok(())
    }
}
