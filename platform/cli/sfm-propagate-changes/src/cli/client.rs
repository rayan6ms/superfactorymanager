use crate::paths::APP_HOME;
use crate::worktree::parse_version;
use eyre::Context;
use facet::Facet;
use figue as args;
use glob::Pattern;
use std::path::Path;
use std::path::PathBuf;
use tracing::info;
use tracing::warn;

const CLIENT_TARGETS_FILE: &str = "client_targets.tsv";

#[derive(Debug, Clone)]
pub struct ClientTarget {
    pub path: PathBuf,
    pub mc_version: String,
}

/// Client instance tracking and management commands
#[derive(Facet, Debug)]
#[repr(u8)]
pub enum ClientCommand {
    /// Track client directories matching a glob pattern
    Add {
        /// Glob pattern for client directories
        #[facet(args::positional)]
        glob: String,
    },
    /// Untrack client directories matching a glob pattern
    Remove {
        /// Glob pattern for tracked client directories
        #[facet(args::positional)]
        glob: String,
    },
    /// List tracked client directories matching a glob pattern
    List {
        /// Glob pattern for tracked client directories
        #[facet(default, args::positional)]
        glob: Option<String>,
    },
}

impl ClientCommand {
    /// # Errors
    ///
    /// This function will return an error if the operation fails.
    pub fn invoke(self) -> eyre::Result<()> {
        match self {
            ClientCommand::Add { glob } => add_clients(&glob),
            ClientCommand::Remove { glob } => remove_clients(&glob),
            ClientCommand::List { glob } => {
                let glob = glob.unwrap_or_else(|| "*".to_string());
                list_clients(&glob)
            }
        }
    }
}

/// Load tracked clients from persistence.
///
/// # Errors
///
/// Returns an error if persistence cannot be read.
pub fn load_client_targets() -> eyre::Result<Vec<ClientTarget>> {
    load_targets(CLIENT_TARGETS_FILE)
}

fn save_client_targets(targets: &[ClientTarget]) -> eyre::Result<()> {
    save_targets(CLIENT_TARGETS_FILE, targets)
}

fn add_clients(glob_pattern: &str) -> eyre::Result<()> {
    let matched_dirs = expand_directories(glob_pattern)?;

    if matched_dirs.is_empty() {
        eyre::bail!("No directories matched glob: {glob_pattern}");
    }

    let mut targets = load_client_targets()?;
    let mut added = 0usize;
    let mut skipped = 0usize;

    for dir in matched_dirs {
        let Some(mc_version) = determine_mc_version_from_dir_name(&dir) else {
            warn!(
                path = %dir.display(),
                "Skipping directory: unable to infer MC version from directory name"
            );
            skipped += 1;
            continue;
        };

        if targets.iter().any(|target| target.path == dir) {
            skipped += 1;
            continue;
        }

        targets.push(ClientTarget {
            path: dir,
            mc_version,
        });
        added += 1;
    }

    targets.sort_by(|a, b| a.path.cmp(&b.path));
    save_client_targets(&targets)?;

    println!("Added {added} client target(s), skipped {skipped}.");
    Ok(())
}

fn remove_clients(glob_pattern: &str) -> eyre::Result<()> {
    let mut targets = load_client_targets()?;
    let before = targets.len();

    let matcher = build_matcher(glob_pattern)?;
    targets.retain(|target| !matcher.matches(&normalize_for_match(&target.path)));

    let removed = before.saturating_sub(targets.len());
    save_client_targets(&targets)?;

    println!("Removed {removed} client target(s).");
    Ok(())
}

fn list_clients(glob_pattern: &str) -> eyre::Result<()> {
    let targets = load_client_targets()?;
    let matcher = build_matcher(glob_pattern)?;

    let filtered: Vec<ClientTarget> = targets
        .into_iter()
        .filter(|target| matcher.matches(&normalize_for_match(&target.path)))
        .collect();

    if filtered.is_empty() {
        println!("No tracked clients match {glob_pattern}.");
        return Ok(());
    }

    for target in filtered {
        println!("{}\t{}", target.mc_version, target.path.display());
    }

    Ok(())
}

fn load_targets(file_name: &str) -> eyre::Result<Vec<ClientTarget>> {
    let path = APP_HOME.file_path(file_name);
    if !path.exists() {
        return Ok(Vec::new());
    }

    let content = std::fs::read_to_string(&path)
        .wrap_err_with(|| format!("Failed to read targets file: {}", path.display()))?;

    let mut out = Vec::new();
    for line in content
        .lines()
        .map(str::trim)
        .filter(|line| !line.is_empty())
    {
        let Some((mc_version, path_text)) = line.split_once('\t') else {
            continue;
        };
        out.push(ClientTarget {
            path: PathBuf::from(path_text),
            mc_version: mc_version.to_string(),
        });
    }

    Ok(out)
}

fn save_targets(file_name: &str, targets: &[ClientTarget]) -> eyre::Result<()> {
    APP_HOME.ensure_dir()?;

    let mut lines = Vec::with_capacity(targets.len());
    for target in targets {
        lines.push(format!("{}\t{}", target.mc_version, target.path.display()));
    }

    let body = if lines.is_empty() {
        String::new()
    } else {
        format!("{}\n", lines.join("\n"))
    };

    let path = APP_HOME.file_path(file_name);
    std::fs::write(&path, body)
        .wrap_err_with(|| format!("Failed to write targets file: {}", path.display()))?;

    info!(path = %path.display(), count = targets.len(), "Saved tracked targets");
    Ok(())
}

fn expand_directories(glob_pattern: &str) -> eyre::Result<Vec<PathBuf>> {
    let mut out = Vec::new();

    for entry in glob::glob(glob_pattern)
        .wrap_err_with(|| format!("Invalid glob pattern: {glob_pattern}"))?
    {
        let candidate = entry.wrap_err("Failed to resolve glob match")?;
        if !candidate.is_dir() {
            continue;
        }

        let canonical = dunce::canonicalize(&candidate)
            .wrap_err_with(|| format!("Failed to canonicalize path: {}", candidate.display()))?;
        out.push(canonical);
    }

    out.sort();
    out.dedup();
    Ok(out)
}

fn build_matcher(glob_pattern: &str) -> eyre::Result<Pattern> {
    let normalized = glob_pattern.replace('\\', "/");
    Pattern::new(&normalized)
        .wrap_err_with(|| format!("Invalid glob pattern for remove/list: {glob_pattern}"))
}

fn determine_mc_version_from_dir_name(dir_path: &Path) -> Option<String> {
    let file_name = dir_path.file_name()?.to_str()?;
    let token = extract_version_token(file_name)?;

    let (major, minor, patch_version) = parse_version(&token)?;
    Some(if patch_version == 0 {
        format!("{major}.{minor}")
    } else {
        format!("{major}.{minor}.{patch_version}")
    })
}

fn extract_version_token(input: &str) -> Option<String> {
    let mut current = String::new();
    let mut tokens = Vec::new();

    for ch in input.chars() {
        if ch.is_ascii_digit() || ch == '.' {
            current.push(ch);
        } else if !current.is_empty() {
            tokens.push(current.clone());
            current.clear();
        }
    }

    if !current.is_empty() {
        tokens.push(current);
    }

    tokens
        .into_iter()
        .find(|token| parse_version(token).is_some())
}

fn normalize_for_match(path: &Path) -> String {
    path.to_string_lossy().replace('\\', "/")
}
