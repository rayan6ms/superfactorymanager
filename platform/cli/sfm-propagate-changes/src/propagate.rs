use crate::cli::repo_root::get_repo_root;
use crate::sfm_path::SfmPath;
use crate::state::State;
use crate::state::Status;
use eyre::Context;
use eyre::bail;
use std::fmt::Write;
use std::io::BufRead;
use std::io::Write as IoWrite;
use std::path::PathBuf;
use std::process::Command;
use tracing::debug;
use tracing::info;
use tracing::warn;

/// Patterns for generated files that should always keep "ours" during merge conflicts.
/// These are files that are auto-generated and should be regenerated after merge.
const GENERATED_PATH_PATTERNS: &[&str] = &["src/generated/", "platform/minecraft/src/generated/"];

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

/// Get the list of conflicted files in a worktree
fn get_conflicted_files(path: &PathBuf) -> eyre::Result<Vec<String>> {
    let output = Command::new("git")
        .args(["diff", "--name-only", "--diff-filter=U"])
        .current_dir(path)
        .output()
        .wrap_err("Failed to get conflicted files")?;

    let stdout = String::from_utf8_lossy(&output.stdout);
    Ok(stdout
        .lines()
        .filter(|s| !s.is_empty())
        .map(String::from)
        .collect())
}

/// Get the list of staged files (changes to be committed) in a worktree
fn get_staged_files(path: &PathBuf) -> eyre::Result<Vec<String>> {
    let output = Command::new("git")
        .args(["diff", "--cached", "--name-only"])
        .current_dir(path)
        .output()
        .wrap_err("Failed to get staged files")?;

    let stdout = String::from_utf8_lossy(&output.stdout);
    Ok(stdout
        .lines()
        .filter(|s| !s.is_empty())
        .map(String::from)
        .collect())
}

/// Get just the filename from a path
fn filename_from_path(path: &str) -> &str {
    path.rsplit(['/', '\\']).next().unwrap_or(path)
}

/// Find potential renames: staged files with the same filename as a conflicted file
fn find_potential_renames(conflicted: &[String], staged: &[String]) -> Vec<(String, String)> {
    let mut renames = Vec::new();

    for conflict in conflicted {
        let conflict_name = filename_from_path(conflict);

        for staged_file in staged {
            // Skip if it's the same file
            if staged_file == conflict {
                continue;
            }

            let staged_name = filename_from_path(staged_file);
            if conflict_name == staged_name {
                renames.push((conflict.clone(), staged_file.clone()));
            }
        }
    }

    renames
}

/// Format a list of files for display, with a limit and "... and N more" suffix
fn format_file_list(files: &[String], limit: usize) -> String {
    let mut result = String::new();

    for (i, file) in files.iter().take(limit).enumerate() {
        if i > 0 {
            result.push('\n');
        }
        result.push_str("  - ");
        result.push_str(file);
    }

    if files.len() > limit {
        let _ = write!(result, "\n  ... and {} more", files.len() - limit);
    }

    result
}

/// Format conflict error message with file list and rename hints
fn format_conflict_error(path: &PathBuf, branch: &str) -> eyre::Result<String> {
    let conflicts = get_conflicted_files(path)?;
    let staged = get_staged_files(path)?;
    let renames = find_potential_renames(&conflicts, &staged);

    let mut msg = format!(
        "Worktree {} is in the middle of a merge with {} unresolved conflict(s).\n\n\
         Unresolved conflicts:\n{}",
        branch,
        conflicts.len(),
        format_file_list(&conflicts, 10)
    );

    if !renames.is_empty() {
        msg.push_str("\n\nPotential renames detected (same filename in different paths):");
        for (conflict, staged_file) in &renames {
            let _ = write!(msg, "\n  {conflict} <- {staged_file}");
        }
        msg.push_str(
            "\n\nHint: Git may not have detected these as renames. Check if the staged file",
        );
        msg.push_str("\nis a moved/renamed version of the conflicted file.");
    }

    let _ = write!(
        msg,
        "\n\nPlease resolve the conflicts in {} and run this command again,\n\
         or abort the merge with `git merge --abort`.",
        path.display()
    );

    Ok(msg)
}

/// Check if a file path matches a generated path pattern
fn is_generated_path(file_path: &str) -> bool {
    GENERATED_PATH_PATTERNS
        .iter()
        .any(|pattern| file_path.contains(pattern))
}

/// Partition conflicted files into generated and non-generated
fn partition_conflicts(files: &[String]) -> (Vec<&String>, Vec<&String>) {
    files.iter().partition(|f| is_generated_path(f))
}

/// Prompt user with Y/n question (defaults to yes)
fn prompt_yes_no(question: &str) -> eyre::Result<bool> {
    print!("{question} [Y/n] ");
    std::io::stdout().flush()?;

    let mut input = String::new();
    std::io::stdin().lock().read_line(&mut input)?;

    let trimmed = input.trim();
    // Default to yes if empty, only false if explicitly "n" or "no"
    Ok(trimmed.is_empty()
        || !(trimmed.eq_ignore_ascii_case("n") || trimmed.eq_ignore_ascii_case("no")))
}

/// Resolve generated file conflicts by keeping "ours" (current branch version)
fn resolve_generated_conflicts(path: &PathBuf, files: &[&String]) -> eyre::Result<()> {
    for file in files {
        info!("Resolving generated file conflict (keeping ours): {file}");

        // Try checkout --ours first (for modified files)
        let checkout_result = Command::new("git")
            .args(["checkout", "--ours", "--", file])
            .current_dir(path)
            .output();

        match checkout_result {
            Ok(output) if output.status.success() => {
                // Successfully checked out ours, now add it
                let add_output = Command::new("git")
                    .args(["add", file])
                    .current_dir(path)
                    .output()
                    .wrap_err_with(|| format!("Failed to git add {file}"))?;

                if !add_output.status.success() {
                    warn!(
                        "Failed to git add {}: {}",
                        file,
                        String::from_utf8_lossy(&add_output.stderr)
                    );
                }
            }
            _ => {
                // checkout --ours failed, might be a deleted file
                // Try to remove it (for "deleted by them" or "both deleted" conflicts)
                let rm_output = Command::new("git")
                    .args(["rm", "--cached", file])
                    .current_dir(path)
                    .output();

                if rm_output.is_ok() && rm_output.as_ref().is_ok_and(|o| o.status.success()) {
                    debug!("Removed {file} from index");
                } else {
                    // Last resort: just add it as-is
                    let add_output = Command::new("git")
                        .args(["add", file])
                        .current_dir(path)
                        .output()
                        .wrap_err_with(|| format!("Failed to resolve conflict for {file}"))?;

                    if !add_output.status.success() {
                        warn!(
                            "Could not fully resolve {}: {}",
                            file,
                            String::from_utf8_lossy(&add_output.stderr)
                        );
                    }
                }
            }
        }
    }

    Ok(())
}

/// Try to auto-resolve generated file conflicts if user agrees
fn try_auto_resolve_generated_conflicts(path: &PathBuf) -> eyre::Result<bool> {
    let conflicts = get_conflicted_files(path)?;
    if conflicts.is_empty() {
        return Ok(true); // No conflicts, all resolved
    }

    let (generated, other) = partition_conflicts(&conflicts);

    if generated.is_empty() {
        return Ok(false); // No generated conflicts to auto-resolve
    }

    println!("\nFound {} generated file conflict(s):", generated.len());
    for file in &generated {
        println!("  - {file}");
    }

    if !other.is_empty() {
        println!("\nOther conflicts remaining: {}", other.len());
        for file in &other {
            println!("  - {file}");
        }
    }

    println!();
    if prompt_yes_no("Auto-resolve generated file conflicts by keeping current branch version?")? {
        resolve_generated_conflicts(path, &generated)?;
        info!("Resolved {} generated file conflict(s)", generated.len());

        // Check if all conflicts are now resolved
        if !has_merge_conflicts(path)? {
            info!("All conflicts resolved!");
            return Ok(true);
        }

        println!(
            "\nRemaining conflicts ({}) require manual resolution.",
            other.len()
        );
    }

    Ok(false)
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

            // Try to auto-resolve generated file conflicts
            if try_auto_resolve_generated_conflicts(&dest.path)? {
                // All conflicts resolved, commit the merge
                commit_merge(source, dest)?;
                return Ok(true);
            }

            return Ok(false);
        }

        bail!("git merge failed: {}\n{}", stderr, stdout);
    }

    info!("Merge successful");
    Ok(true)
}

/// Commit an empty merge (when user resolves conflicts with no changes)
fn commit_merge(source: &Worktree, dest: &Worktree) -> eyre::Result<()> {
    let message = format!(
        "Propagate changes: merge {} into {}",
        source.branch, dest.branch
    );

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
            if !is_merging(&dest_path.0)? {
                info!("No longer in a merge state. Resetting to idle and starting over.");
                state.reset()?;
                return run();
            }

            // Check if there are still conflicts
            if has_merge_conflicts(&dest_path.0)? {
                // Try to auto-resolve generated file conflicts
                if try_auto_resolve_generated_conflicts(&dest_path.0)? {
                    info!("All conflicts resolved via auto-resolution");
                } else {
                    bail!(
                        "There are still merge conflicts in {}. Please resolve them and run again.",
                        dest_path.display()
                    );
                }
            }

            // Commit the merge
            let source = Worktree {
                path: source_path.0.clone(),
                branch: source_branch.clone(),
            };
            let dest = Worktree {
                path: dest_path.0.clone(),
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
        info!(
            "Only {} worktree(s) found, nothing to propagate",
            worktrees.len()
        );
        return Ok(());
    }

    // Sort by version
    sort_worktrees_by_version(&mut worktrees);
    info!(
        "Worktrees (sorted): {:?}",
        worktrees.iter().map(|w| &w.branch).collect::<Vec<_>>()
    );

    // Check if any worktree is currently in a merging state
    for wt in &worktrees {
        if is_merging(&wt.path)? {
            info!(
                "Detected in-progress merge in {} ({})",
                wt.branch,
                wt.path.display()
            );

            // Try to auto-resolve generated file conflicts
            if has_merge_conflicts(&wt.path)? {
                if try_auto_resolve_generated_conflicts(&wt.path)? {
                    info!("All conflicts resolved via auto-resolution");

                    // Commit the merge
                    let output = Command::new("git")
                        .args(["commit", "--no-edit"])
                        .current_dir(&wt.path)
                        .output()
                        .wrap_err("Failed to commit merge")?;

                    if output.status.success() {
                        info!("Merge committed in {}", wt.branch);
                        // Continue to check for more merges needed
                        continue;
                    }
                    // If commit failed, we'll fall through to the error below
                }

                bail!("{}", format_conflict_error(&wt.path, &wt.branch)?);
            }

            // No conflicts, just commit the merge
            let output = Command::new("git")
                .args(["commit", "--no-edit"])
                .current_dir(&wt.path)
                .output()
                .wrap_err("Failed to commit merge")?;

            if output.status.success() {
                info!("Merge committed in {}", wt.branch);
            } else {
                // Maybe nothing to commit
                debug!("Commit result: {}", String::from_utf8_lossy(&output.stderr));
            }
        }
    }

    // Check for uncommitted changes (but exclude worktrees in merging state)
    let dirty = check_uncommitted_changes(&worktrees)?;
    if !dirty.is_empty() {
        // Filter out any that are in merging state (we handled those above)
        let truly_dirty: Vec<_> = dirty
            .into_iter()
            .filter(|wt| !is_merging(&wt.path).unwrap_or(false))
            .collect();

        if !truly_dirty.is_empty() {
            let paths: Vec<_> = truly_dirty
                .iter()
                .map(|w| w.path.display().to_string())
                .collect();
            bail!(
                "The following worktrees have uncommitted changes:\n  {}\n\nPlease commit or stash changes before propagating.",
                paths.join("\n  ")
            );
        }
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
                source_path: SfmPath::from(source.path.clone()),
                dest_branch: dest.branch.clone(),
                dest_path: SfmPath::from(dest.path.clone()),
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
