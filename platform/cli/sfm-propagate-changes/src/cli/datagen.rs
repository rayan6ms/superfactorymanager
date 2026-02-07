use crate::cli::compile::BuildResult;
use crate::cli::compile::BuildStatus;
use crate::cli::compile::print_summary;
use crate::cli::status::assert_worktrees_clean_or_autocommit_generated;
use crate::worktree::get_sorted_worktrees;
use color_eyre::owo_colors::OwoColorize;
use eyre::Context;
use eyre::bail;
use facet::Facet;
use std::path::PathBuf;
use std::time::Instant;
use tokio::process::Command;
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
            main: BuildStatus::NotFound {
                reason: "platform/minecraft directory not found".to_string(),
            },
            datagen: None,
            gametest: None,
            duration: std::time::Duration::from_secs(0),
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
            duration: std::time::Duration::from_secs(0),
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

    let datagen_status = match result {
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
                if let Err(err) = commit_generated_if_needed(&path, &branch).await {
                    warn!(
                        "Failed to commit generated resources for {}: {}",
                        branch, err
                    );
                }
                Some(BuildStatus::Success { duration })
            } else {
                warn!(
                    "runData for {} failed; exit: {:?}, stdout: {}, stderr: {}",
                    branch, output.status, stdout, stderr
                );
                Some(BuildStatus::Failed { duration })
            }
        }
        Err(e) => {
            warn!("Failed to run gradlew for {}: {}", branch, e);
            Some(BuildStatus::Failed { duration })
        }
    };

    BuildResult {
        branch,
        main: BuildStatus::NotFound { reason: "not run".to_string() },
        datagen: datagen_status,
        gametest: None,
        duration,
    }
}

async fn commit_generated_if_needed(repo_root: &PathBuf, branch: &str) -> eyre::Result<()> {
    let output = Command::new("git")
        .args([
            "status",
            "--porcelain=v1",
            "--",
            "platform/minecraft/src/generated",
        ])
        .current_dir(repo_root)
        .output()
        .await
        .wrap_err("Failed to check git status for generated resources")?;

    if !output.status.success() {
        bail!(
            "git status failed in {}: {}",
            repo_root.display(),
            String::from_utf8_lossy(&output.stderr)
        );
    }

    let stdout = String::from_utf8_lossy(&output.stdout);
    if stdout.trim().is_empty() {
        return Ok(());
    }

    let add_output = Command::new("git")
        .args(["add", "platform/minecraft/src/generated"])
        .current_dir(repo_root)
        .output()
        .await
        .wrap_err("Failed to stage generated resources")?;

    if !add_output.status.success() {
        bail!(
            "git add failed in {}: {}",
            repo_root.display(),
            String::from_utf8_lossy(&add_output.stderr)
        );
    }

    let message = format!("{{{}}} - resources - datagen", branch);
    let commit_output = Command::new("git")
        .args(["commit", "-m", &message])
        .current_dir(repo_root)
        .output()
        .await
        .wrap_err("Failed to commit generated resources")?;

    if !commit_output.status.success() {
        bail!(
            "git commit failed in {}: {}",
            repo_root.display(),
            String::from_utf8_lossy(&commit_output.stderr)
        );
    }

    Ok(())
}

/// Datagen command - runs `gradlew runData` for all worktrees in series
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

        assert_worktrees_clean_or_autocommit_generated(&worktrees)?;

        info!(
            "Running datagen for {} worktree(s) in series: {:?}",
            worktrees.len(),
            worktrees.iter().map(|w| &w.branch).collect::<Vec<_>>()
        );

        let mut results: Vec<BuildResult> = Vec::new();
        for worktree in worktrees {
            let branch = worktree.branch.clone();
            let path = worktree.path.clone();
            let result = run_datagen_worktree(branch, path).await;
            results.push(result);
        }

        // Sort results by branch name for consistent output
        results.sort_by(|a, b| a.branch.cmp(&b.branch));

        print_summary(&results);

        let had_failure = results
            .iter()
            .any(|r| matches!(r.datagen, Some(BuildStatus::Failed { .. })));

        if had_failure {
            bail!("One or more datagen runs failed");
        }

        Ok(())
    }
}
