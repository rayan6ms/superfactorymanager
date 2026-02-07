use crate::worktree::get_sorted_worktrees;
use color_eyre::owo_colors::OwoColorize;
use eyre::Context;
use eyre::bail;
use facet::Facet;
use std::path::PathBuf;
use tokio::process::Command;
use tracing::info;
use tracing::warn;

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

/// Run gradle runGameTestServer for a worktree
async fn run_gametest_worktree(branch: String, path: PathBuf) -> eyre::Result<()> {
    let minecraft_dir = path.join("platform").join("minecraft");

    if !minecraft_dir.exists() {
        info!(
            "Skipping {} - platform/minecraft directory not found",
            branch
        );
        bail!("platform/minecraft directory not found");
    }

    let gradlew = if cfg!(windows) {
        minecraft_dir.join("gradlew.bat")
    } else {
        minecraft_dir.join("gradlew")
    };

    if !gradlew.exists() {
        info!(
            "Skipping {} - gradlew not found in {}",
            branch,
            minecraft_dir.display()
        );
        bail!("gradlew not found in {}", minecraft_dir.display());
    }

    println!(
        "{} {} {}",
        "Running gametest".cyan().bold(),
        branch.yellow().bold(),
        format!("({})", minecraft_dir.display()).dimmed()
    );

    let result = Command::new(&gradlew)
        .arg("runGameTestServer")
        .current_dir(&minecraft_dir)
        .output()
        .await
        .wrap_err("Failed to run gametest command")?;

    let stdout = String::from_utf8_lossy(&result.stdout);
    let stderr = String::from_utf8_lossy(&result.stderr);
    let success = has_gametest_success(&stdout) || has_gametest_success(&stderr);

    if success {
        Ok(())
    } else {
        warn!(
            "runGameTestServer for {} did not report success; exit: {:?}",
            branch, result.status
        );
        bail!(
            "runGameTestServer failed for {}\nStdout: {}\nStderr: {}",
            branch,
            stdout,
            stderr
        );
    }
}

/// Gametest command - runs `gradlew runGameTestServer` for all worktrees in series
#[derive(Facet, Debug, Default)]
pub struct GametestCommand;

impl GametestCommand {
    /// # Errors
    ///
    /// Returns an error if gametests fail.
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
            "Running gametest for {} worktree(s) in series: {:?}",
            worktrees.len(),
            worktrees.iter().map(|w| &w.branch).collect::<Vec<_>>()
        );

        for worktree in worktrees {
            let branch = worktree.branch.clone();
            let path = worktree.path.clone();
            run_gametest_worktree(branch, path).await?;
        }

        Ok(())
    }
}
