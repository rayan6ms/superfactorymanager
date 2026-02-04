use crate::cli::compile::BuildResult;
use crate::cli::compile::BuildStatus;
use crate::cli::compile::print_summary;
use crate::worktree::get_sorted_worktrees;
use color_eyre::owo_colors::OwoColorize;
use eyre::Context;
use eyre::bail;
use facet::Facet;
use std::path::PathBuf;
use std::time::Instant;
use tokio::process::Command;
use tokio::task::JoinSet;
use tracing::info;
use tracing::warn;

/// Run gradle runData for a worktree
async fn run_datagen_worktree(branch: String, path: PathBuf) -> BuildResult {
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
        "Running datagen".cyan().bold(),
        branch.yellow().bold(),
        format!("({})", minecraft_dir.display()).dimmed()
    );

    let start = Instant::now();

    let result = Command::new(&gradlew)
        .arg("runData")
        .current_dir(&minecraft_dir)
        .output()
        .await;

    let duration = start.elapsed();

    let status = match result {
        Ok(output) => {
            let stdout = String::from_utf8_lossy(&output.stdout);
            let stderr = String::from_utf8_lossy(&output.stderr);
            let has_all_providers =
                stdout.contains("All providers took") || stderr.contains("All providers took");

            if output.status.success() || has_all_providers {
                if !output.status.success() && has_all_providers {
                    info!(
                        "`runData` for {} produced 'All providers took' despite non-zero exit status; treating as success",
                        branch
                    );
                }
                BuildStatus::Success { duration }
            } else {
                warn!(
                    "runData for {} failed; exit: {:?}, stdout: {}, stderr: {}",
                    branch, output.status, stdout, stderr
                );
                BuildStatus::Failed { duration }
            }
        }
        Err(e) => {
            warn!("Failed to run gradlew for {}: {}", branch, e);
            BuildStatus::Failed { duration }
        }
    };

    BuildResult { branch, status }
}

/// Datagen command - runs `gradlew runData` for all worktrees in parallel
#[derive(Facet, Debug, Default)]
pub struct DatagenCommand;

impl DatagenCommand {
    /// # Errors
    ///
    /// Returns an error if datagen fails.
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
            "Running datagen for {} worktree(s) in parallel: {:?}",
            worktrees.len(),
            worktrees.iter().map(|w| &w.branch).collect::<Vec<_>>()
        );

        let mut join_set: JoinSet<BuildResult> = JoinSet::new();

        // Spawn all datagen tasks
        for worktree in worktrees {
            let branch = worktree.branch.clone();
            let path = worktree.path.clone();
            join_set.spawn(run_datagen_worktree(branch, path));
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
            bail!("One or more datagen runs failed");
        }

        Ok(())
    }
}
