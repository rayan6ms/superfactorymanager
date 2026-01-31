use crate::cli::repo_root::get_repo_root;
use crate::state::{State, Status};
use eyre::{Context, bail};
use std::path::PathBuf;
use std::process::Command;
use tracing::{debug, info, warn};

/// Represents a worktree with its path and branch name
#[derive(Debug, Clone)]
pub struct Worktree {
    pub path: PathBuf,
    pub branch: String,
}

/// Parse the output of `git worktree list` to get all worktrees
fn get_worktrees(repo_root: &PathBuf) -> eyre::Result<Vec<Worktree>> {
    let output = Command::new("git")
        .args(["worktree", "list", "--porcelain"])
        .current_dir(repo_root)
        .output()
        .wrap_err("Failed to run git worktree list")?;

    if !output.status.success() {
        bail!(
            "git worktree list failed: {}",
            String::from_utf8_lossy(&output.stderr)
        );
    }

    let stdout = String::from_utf8_lossy(&output.stdout);
    let mut worktrees = Vec::new();
    let mut current_path: Option<PathBuf> = None;
    let mut current_branch: Option<String> = None;

    for line in stdout.lines() {
        if let Some(path) = line.strip_prefix("worktree ") {
            // Save previous worktree if complete
            if let (Some(path), Some(branch)) = (current_path.take(), current_branch.take()) {
                worktrees.push(Worktree { path, branch });
            }
            current_path = Some(PathBuf::from(path));
        } else if let Some(branch_ref) = line.strip_prefix("branch refs/heads/") {
            current_branch = Some(branch_ref.to_string());
        }
    }

    // Don't forget the last one
    if let (Some(path), Some(branch)) = (current_path, current_branch) {
        worktrees.push(Worktree { path, branch });
    }

    Ok(worktrees)
}

/// Parse a Minecraft version string into comparable parts
/// Returns (major, minor, patch) as numbers for sorting
fn parse_version(version: &str) -> Option<(u32, u32, u32)> {
    let parts: Vec<&str> = version.split('.').collect();
    match parts.len() {
        2 => {
            let major = parts[0].parse().ok()?;
            let minor = parts[1].parse().ok()?;
            Some((major, minor, 0))
        }
        3 => {
            let major = parts[0].parse().ok()?;
            let minor = parts[1].parse().ok()?;
            let patch = parts[2].parse().ok()?;
            Some((major, minor, patch))
        }
        _ => None,
    }
}

/// Sort worktrees by their version number (semver-like)
fn sort_worktrees_by_version(worktrees: &mut [Worktree]) {
    worktrees.sort_by(|a, b| {
        let a_version = parse_version(&a.branch);
        let b_version = parse_version(&b.branch);

        match (a_version, b_version) {
            (Some(a_v), Some(b_v)) => a_v.cmp(&b_v),
            (Some(_), None) => std::cmp::Ordering::Less,
            (None, Some(_)) => std::cmp::Ordering::Greater,
            (None, None) => a.branch.cmp(&b.branch),
        }
    });
}

/// Check if any worktree has uncommitted changes
fn check_uncommitted_changes(worktrees: &[Worktree]) -> eyre::Result<Vec<&Worktree>> {
    let mut dirty = Vec::new();

    for wt in worktrees {
        let output = Command::new("git")
            .args(["status", "--porcelain"])
            .current_dir(&wt.path)
            .output()
            .wrap_err_with(|| format!("Failed to check status in {}", wt.path.display()))?;

        if !output.status.success() {
            bail!(
                "git status failed in {}: {}",
                wt.path.display(),
                String::from_utf8_lossy(&output.stderr)
            );
        }

        let stdout = String::from_utf8_lossy(&output.stdout);
        if !stdout.trim().is_empty() {
            dirty.push(wt);
        }
    }

    Ok(dirty)
}

/// Check if there are merge conflicts in a worktree
fn has_merge_conflicts(path: &PathBuf) -> eyre::Result<bool> {
    let output = Command::new("git")
        .args(["diff", "--name-only", "--diff-filter=U"])
        .current_dir(path)
        .output()
        .wrap_err("Failed to check for merge conflicts")?;

    let stdout = String::from_utf8_lossy(&output.stdout);
    Ok(!stdout.trim().is_empty())
}

/// Check if we're in the middle of a merge
fn is_merging(path: &PathBuf) -> eyre::Result<bool> {
    let merge_head = path.join(".git").join("MERGE_HEAD");
    // For worktrees, .git might be a file pointing to the real git dir
    if merge_head.exists() {
        return Ok(true);
    }

    // Also check via git command
    let output = Command::new("git")
        .args(["rev-parse", "--git-dir"])
        .current_dir(path)
        .output()
        .wrap_err("Failed to get git dir")?;

    let git_dir = PathBuf::from(String::from_utf8_lossy(&output.stdout).trim());
    let merge_head = if git_dir.is_absolute() {
        git_dir.join("MERGE_HEAD")
    } else {
        path.join(&git_dir).join("MERGE_HEAD")
    };

    Ok(merge_head.exists())
}

/// Perform a git merge from source into dest
fn do_merge(source: &Worktree, dest: &Worktree) -> eyre::Result<bool> {
    info!(
        "Merging {} into {} (in {})",
        source.branch,
        dest.branch,
        dest.path.display()
    );

    let output = Command::new("git")
        .args(["merge", &source.branch, "--no-edit"])
        .current_dir(&dest.path)
        .output()
        .wrap_err_with(|| format!("Failed to merge {} into {}", source.branch, dest.branch))?;

    if !output.status.success() {
        let stderr = String::from_utf8_lossy(&output.stderr);
        let stdout = String::from_utf8_lossy(&output.stdout);

        // Check if it's a merge conflict
        if stderr.contains("CONFLICT") || stdout.contains("CONFLICT") {
            warn!("Merge conflict detected");
            return Ok(false);
        }

        bail!(
            "git merge failed: {}\n{}",
            stderr,
            stdout
        );
    }

    info!("Merge successful");
    Ok(true)
}

/// Commit an empty merge (when user resolves conflicts with no changes)
fn commit_merge(source: &Worktree, dest: &Worktree) -> eyre::Result<()> {
    let message = format!("Propagate changes: merge {} into {}", source.branch, dest.branch);

    // First try a normal commit
    let output = Command::new("git")
        .args(["commit", "-m", &message])
        .current_dir(&dest.path)
        .output()
        .wrap_err("Failed to commit merge")?;

    if output.status.success() {
        info!("Merge committed");
        return Ok(());
    }

    // If nothing to commit, try with --allow-empty
    let stderr = String::from_utf8_lossy(&output.stderr);
    if stderr.contains("nothing to commit") {
        let output = Command::new("git")
            .args(["commit", "--allow-empty", "-m", &message])
            .current_dir(&dest.path)
            .output()
            .wrap_err("Failed to commit empty merge")?;

        if !output.status.success() {
            bail!(
                "Failed to commit empty merge: {}",
                String::from_utf8_lossy(&output.stderr)
            );
        }

        info!("Empty merge committed");
        return Ok(());
    }

    bail!("Failed to commit merge: {}", stderr);
}

/// Run the propagation process
///
/// # Errors
///
/// Returns an error if any step of the propagation fails.
pub fn run() -> eyre::Result<()> {
    let repo_root = get_repo_root()?;
    let mut state = State::load()?;

    info!(?repo_root, "Starting propagation");

    match &state.status {
        Status::Idle => {
            run_idle_state(&repo_root, &mut state)?;
        }
        Status::MergingWithConflict {
            source_branch,
            source_path,
            dest_branch,
            dest_path,
        } => {
            info!(
                "Resuming merge: {} -> {} (in {})",
                source_branch,
                dest_branch,
                dest_path.display()
            );

            // Check if still merging
            if !is_merging(dest_path)? {
                info!("No longer in a merge state. Resetting to idle and starting over.");
                state.reset()?;
                return run();
            }

            // Check if there are still conflicts
            if has_merge_conflicts(dest_path)? {
                bail!(
                    "There are still merge conflicts in {}. Please resolve them and run again.",
                    dest_path.display()
                );
            }

            // Commit the merge
            let source = Worktree {
                path: source_path.clone(),
                branch: source_branch.clone(),
            };
            let dest = Worktree {
                path: dest_path.clone(),
                branch: dest_branch.clone(),
            };
            commit_merge(&source, &dest)?;

            // Reset state and continue
            state.reset()?;
            return run();
        }
    }

    Ok(())
}

fn run_idle_state(repo_root: &PathBuf, state: &mut State) -> eyre::Result<()> {
    // Get all worktrees
    let mut worktrees = get_worktrees(repo_root)?;
    debug!(?worktrees, "Found worktrees");

    if worktrees.len() < 2 {
        info!("Only {} worktree(s) found, nothing to propagate", worktrees.len());
        return Ok(());
    }

    // Sort by version
    sort_worktrees_by_version(&mut worktrees);
    info!(
        "Worktrees (sorted): {:?}",
        worktrees.iter().map(|w| &w.branch).collect::<Vec<_>>()
    );

    // Check for uncommitted changes
    let dirty = check_uncommitted_changes(&worktrees)?;
    if !dirty.is_empty() {
        let paths: Vec<_> = dirty.iter().map(|w| w.path.display().to_string()).collect();
        bail!(
            "The following worktrees have uncommitted changes:\n  {}\n\nPlease commit or stash changes before propagating.",
            paths.join("\n  ")
        );
    }

    // Merge oldest to newest (sliding window of size 2)
    for window in worktrees.windows(2) {
        let source = &window[0];
        let dest = &window[1];

        let success = do_merge(source, dest)?;

        if !success {
            // Merge conflict - save state and bail
            state.status = Status::MergingWithConflict {
                source_branch: source.branch.clone(),
                source_path: source.path.clone(),
                dest_branch: dest.branch.clone(),
                dest_path: dest.path.clone(),
            };
            state.save()?;

            bail!(
                "Merge conflict detected while merging {} into {}.\n\
                 Please resolve the conflicts in {} and run this command again.",
                source.branch,
                dest.branch,
                dest.path.display()
            );
        }
    }

    info!("All merges completed successfully!");
    Ok(())
}
