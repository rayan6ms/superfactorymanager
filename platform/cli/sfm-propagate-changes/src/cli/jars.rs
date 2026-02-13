use crate::paths::APP_HOME;
use crate::worktree::get_sorted_worktrees;
use eyre::Context;
use facet::Facet;
use figue as args;
use std::ffi::OsStr;
use std::path::Path;
use std::path::PathBuf;
use tracing::info;
use tracing::warn;

const JARS_DIR_FILE: &str = "jars_dir.txt";

/// Jars directory and release artifact related commands
#[derive(Facet, Debug)]
#[repr(u8)]
pub enum JarsCommand {
    /// Jars directory related commands
    Dir {
        /// Directory subcommand
        #[facet(args::subcommand)]
        command: JarsDirCommand,
    },
    /// Remove jars from the configured jars directory while keeping the directory
    Clean,
    /// Collect jars from each MC version based on that version's `mod_version`
    Collect,
    /// List jars in the configured jars directory
    List,
}

/// Jars directory commands
#[derive(Facet, Debug)]
#[repr(u8)]
pub enum JarsDirCommand {
    /// Set the jars directory path
    Set {
        /// The path to the jars directory
        #[facet(args::positional)]
        path: PathBuf,
    },
    /// Show the current jars directory path
    Show,
    /// Open the jars directory in the file explorer
    Open,
}

impl JarsCommand {
    /// # Errors
    ///
    /// This function will return an error if the operation fails.
    pub fn invoke(self) -> eyre::Result<()> {
        match self {
            JarsCommand::Dir { command } => command.invoke(),
            JarsCommand::Clean => clean_jars(),
            JarsCommand::Collect => collect_jars(),
            JarsCommand::List => list_jars(),
        }
    }
}

impl JarsDirCommand {
    /// # Errors
    ///
    /// This function will return an error if the operation fails.
    pub fn invoke(self) -> eyre::Result<()> {
        match self {
            JarsDirCommand::Set { path } => {
                let canonical = dunce::canonicalize(&path)
                    .or_else(|_| {
                        std::fs::create_dir_all(&path)?;
                        dunce::canonicalize(&path)
                    })
                    .wrap_err_with(|| {
                        format!("Failed to create/canonicalize jars dir: {}", path.display())
                    })?;

                APP_HOME.ensure_dir()?;
                let jars_dir_file = APP_HOME.file_path(JARS_DIR_FILE);
                std::fs::write(&jars_dir_file, canonical.display().to_string())
                    .wrap_err("Failed to write jars dir file")?;

                info!("Set jars directory to: {}", canonical.display());
                Ok(())
            }
            JarsDirCommand::Show => {
                let path = get_jars_dir()?;
                println!("{}", path.display());
                Ok(())
            }
            JarsDirCommand::Open => {
                let path = get_jars_dir()?;
                if !path.exists() {
                    std::fs::create_dir_all(&path)?;
                }
                open::that(&path)?;
                Ok(())
            }
        }
    }
}

/// Get the configured jars directory path.
///
/// # Errors
///
/// This function will return an error if jars directory has not been set or if the file cannot be read.
pub fn get_jars_dir() -> eyre::Result<PathBuf> {
    let jars_dir_file = APP_HOME.file_path(JARS_DIR_FILE);

    if !jars_dir_file.exists() {
        eyre::bail!("Jars dir not set. Use `sfm-propagate-changes jars dir set <path>` to set it.");
    }

    let content =
        std::fs::read_to_string(&jars_dir_file).wrap_err("Failed to read jars dir file")?;
    let path = PathBuf::from(content.trim());

    Ok(path)
}

fn list_jar_files_sorted(path: &Path) -> eyre::Result<Vec<PathBuf>> {
    if !path.exists() {
        return Ok(Vec::new());
    }

    let mut jars: Vec<PathBuf> = std::fs::read_dir(path)?
        .filter_map(Result::ok)
        .map(|entry| entry.path())
        .filter(|entry_path| {
            entry_path.is_file()
                && entry_path
                    .extension()
                    .is_some_and(|extension| extension == OsStr::new("jar"))
        })
        .collect();

    jars.sort_by(|a, b| {
        let a_name = a.file_name().and_then(OsStr::to_str).unwrap_or_default();
        let b_name = b.file_name().and_then(OsStr::to_str).unwrap_or_default();
        a_name.cmp(b_name)
    });

    Ok(jars)
}

fn clean_jars() -> eyre::Result<()> {
    let jars_dir = get_jars_dir()?;
    std::fs::create_dir_all(&jars_dir)?;

    let jars = list_jar_files_sorted(&jars_dir)?;

    for jar in &jars {
        std::fs::remove_file(jar)
            .wrap_err_with(|| format!("Failed to remove jar: {}", jar.display()))?;
    }

    info!("Removed {} jar(s) from {}", jars.len(), jars_dir.display());
    Ok(())
}

fn list_jars() -> eyre::Result<()> {
    let jars_dir = get_jars_dir()?;
    let jars = list_jar_files_sorted(&jars_dir)?;

    if jars.is_empty() {
        println!("No jars found in {}", jars_dir.display());
        return Ok(());
    }

    for jar in jars {
        if let Some(name) = jar.file_name().and_then(OsStr::to_str) {
            println!("{name}");
        } else {
            println!("{}", jar.display());
        }
    }

    Ok(())
}

fn collect_jars() -> eyre::Result<()> {
    let jars_dir = get_jars_dir()?;
    std::fs::create_dir_all(&jars_dir)?;

    let worktrees = get_sorted_worktrees()?;

    if worktrees.is_empty() {
        println!("No worktrees found.");
        return Ok(());
    }

    let mut copied = 0usize;
    let mut skipped = 0usize;

    for wt in worktrees {
        let gradle_properties = wt
            .path
            .join("platform")
            .join("minecraft")
            .join("gradle.properties");
        let libs_dir = wt
            .path
            .join("platform")
            .join("minecraft")
            .join("build")
            .join("libs");

        if !gradle_properties.exists() {
            warn!(
                branch = %wt.branch,
                path = %gradle_properties.display(),
                "Skipping worktree: missing gradle.properties"
            );
            skipped += 1;
            continue;
        }

        if !libs_dir.exists() {
            warn!(
                branch = %wt.branch,
                path = %libs_dir.display(),
                "Skipping worktree: missing build/libs"
            );
            skipped += 1;
            continue;
        }

        let mod_version = read_mod_version(&gradle_properties).wrap_err_with(|| {
            format!(
                "Failed to read mod_version for branch {} from {}",
                wt.branch,
                gradle_properties.display()
            )
        })?;

        let Some(jar) = pick_jar_for_mod_version(&libs_dir, &mod_version)? else {
            warn!(
                branch = %wt.branch,
                mod_version = %mod_version,
                path = %libs_dir.display(),
                "Skipping worktree: no matching jar for mod_version"
            );
            skipped += 1;
            continue;
        };

        let destination = jars_dir.join(
            jar.file_name()
                .ok_or_else(|| eyre::eyre!("Jar filename missing: {}", jar.display()))?,
        );

        std::fs::copy(&jar, &destination).wrap_err_with(|| {
            format!(
                "Failed to copy jar from {} to {}",
                jar.display(),
                destination.display()
            )
        })?;

        info!(
            branch = %wt.branch,
            mod_version = %mod_version,
            source = %jar.display(),
            dest = %destination.display(),
            "Collected jar"
        );
        copied += 1;
    }

    println!(
        "Collected {copied} jar(s) into {} (skipped {skipped}).",
        jars_dir.display()
    );

    Ok(())
}

fn read_mod_version(gradle_properties: &Path) -> eyre::Result<String> {
    let content = std::fs::read_to_string(gradle_properties)
        .wrap_err("Failed to read gradle.properties for mod_version")?;

    let mod_version = content
        .lines()
        .map(str::trim)
        .filter(|line| !line.is_empty())
        .filter(|line| !line.starts_with('#'))
        .find_map(|line| line.strip_prefix("mod_version=").map(str::trim))
        .ok_or_else(|| eyre::eyre!("mod_version not found"))?;

    if mod_version.is_empty() {
        eyre::bail!("mod_version was empty");
    }

    Ok(mod_version.to_string())
}

fn pick_jar_for_mod_version(libs_dir: &Path, mod_version: &str) -> eyre::Result<Option<PathBuf>> {
    let suffix = format!("-{mod_version}.jar");

    let mut candidates: Vec<PathBuf> = std::fs::read_dir(libs_dir)?
        .filter_map(Result::ok)
        .map(|entry| entry.path())
        .filter(|path| {
            path.is_file()
                && path
                    .extension()
                    .is_some_and(|extension| extension == OsStr::new("jar"))
                && path
                    .file_name()
                    .and_then(OsStr::to_str)
                    .is_some_and(|name| name.ends_with(&suffix))
        })
        .collect();

    candidates.sort_by(|a, b| {
        let a_name = a.file_name().and_then(OsStr::to_str).unwrap_or_default();
        let b_name = b.file_name().and_then(OsStr::to_str).unwrap_or_default();
        a_name.cmp(b_name)
    });

    Ok(candidates.into_iter().next())
}
