use crate::worktree::get_sorted_worktrees;
use eyre::bail;
use facet::Facet;
use figue::{self as args};
use std::fs;
use tracing::{info, warn};

/// Check command - verifies platform/minecraft/.idea/.name matches sfm-<mcversion>
#[derive(Facet, Debug, Default)]
pub struct CheckCommand {
    /// If set, update or create the `.idea/.name` files to the expected value
    #[facet(args::named)]
    pub fix: bool,
}

impl CheckCommand {
    /// # Errors
    ///
    /// Returns an error if any worktree fails the check.
    pub fn invoke(self) -> eyre::Result<()> {
        let worktrees = get_sorted_worktrees()?;

        if worktrees.is_empty() {
            println!("No worktrees found.");
            return Ok(());
        }

        let mut failures: Vec<String> = Vec::new();

        for wt in worktrees {
            let path = wt.path.join("platform").join("minecraft").join(".idea").join(".name");
            let expected = format!("sfm-{}", wt.branch);

            match fs::read_to_string(&path) {
                Ok(content) => {
                    let found = content.trim();
                    if found != expected {
                        if self.fix {
                            // Attempt to overwrite with expected value
                            if let Some(parent) = path.parent() {
                                if let Err(e) = fs::create_dir_all(parent) {
                                    warn!("{}: failed to create parent dir {}: {}", wt.branch, parent.display(), e);
                                    failures.push(format!("{}: could not create parent dir {} ({})", wt.branch, parent.display(), e));
                                    continue;
                                }
                            }

                            match fs::write(&path, expected.as_bytes()) {
                                Ok(_) => {
                                    info!("{}: fixed .idea/.name (wrote '{}')", wt.branch, expected);
                                }
                                Err(e) => {
                                    warn!("{}: failed to write {}: {}", wt.branch, path.display(), e);
                                    failures.push(format!("{}: could not write {} ({})", wt.branch, path.display(), e));
                                }
                            }
                        } else {
                            warn!("{}: expected '{}', found '{}'", wt.branch, expected, found);
                            failures.push(format!("{}: expected '{}', found '{}'", wt.branch, expected, found));
                        }
                    } else {
                        info!("{}: .idea/.name OK", wt.branch);
                    }
                }
                Err(e) => {
                    if self.fix {
                        // Try to create parent directories and write the expected file
                        if let Some(parent) = path.parent() {
                            if let Err(e2) = fs::create_dir_all(parent) {
                                warn!("{}: failed to create parent dir {}: {}", wt.branch, parent.display(), e2);
                                failures.push(format!("{}: could not create parent dir {} ({})", wt.branch, parent.display(), e2));
                                continue;
                            }
                        }

                        match fs::write(&path, expected.as_bytes()) {
                            Ok(_) => {
                                info!("{}: created .idea/.name with '{}'", wt.branch, expected);
                            }
                            Err(e2) => {
                                warn!("{}: failed to write {}: {}", wt.branch, path.display(), e2);
                                failures.push(format!("{}: could not write {} ({})", wt.branch, path.display(), e2));
                            }
                        }
                    } else {
                        warn!("{}: could not read {} ({})", wt.branch, path.display(), e);
                        failures.push(format!("{}: could not read {} ({})", wt.branch, path.display(), e));
                    }
                }
            }
        }

        if failures.is_empty() {
            println!("All worktrees have correct .idea/.name files.");
            Ok(())
        } else {
            let mut msg = String::new();
            msg.push_str("Check failed for the following worktrees:\n");
            for f in failures {
                msg.push_str("  - ");
                msg.push_str(&f);
                msg.push('\n');
            }
            bail!(msg)
        }
    }
}
