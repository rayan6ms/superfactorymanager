use clap::Parser;
use clap::Subcommand;
use eye_config::persistable_state::PersistableState;
use eye_config::persistence_key::PersistenceKey;
use git2::Repository;
use holda::Holda;
use holda::StringHolda;
use serde::Deserialize;
use serde::Serialize;
use serde_json;
use std::env;
use std::fs;
use std::path::PathBuf;
use tracing::info;

#[derive(Debug, Serialize, Deserialize, Clone, PartialEq, Default)]
pub struct TranslationHelperConfig {
    pub translation_file_paths: Vec<PathBuf>,
    pub root_dir: Option<PathBuf>,
}

#[eye_config::async_trait::async_trait]
impl PersistableState for TranslationHelperConfig {
    async fn key() -> eyre::Result<PersistenceKey> {
        Ok(PersistenceKey::new("translation_helper", "config.json"))
    }
}

#[derive(Parser)]
#[command(name = "translation-helper")]
#[command(version = "0.1.0")]
#[command(about = "A CLI tool to help with translation file management")]
struct Cli {
    #[command(subcommand)]
    command: Commands,
}

#[derive(Subcommand)]
enum Commands {
    /// Commands for working with translation files
    File {
        #[command(subcommand)]
        file_command: FileCommands,
    },
    /// Configuration commands
    Config {
        #[command(subcommand)]
        config_command: ConfigCommands,
    },
    /// Repository commands
    Repo {
        #[command(subcommand)]
        repo_command: RepoCommands,
    },
}

#[derive(Subcommand)]
enum FileCommands {
    /// List translation files
    List,
    /// Add a new translation file
    Add {
        /// Path to the translation file to add
        path: PathBuf,
    },
}

#[derive(Subcommand)]
enum ConfigCommands {
    /// Show the current configuration
    Show,
    /// Root directory configuration
    Root {
        #[command(subcommand)]
        root_command: RootCommands,
    },
}

#[derive(Subcommand)]
enum RootCommands {
    /// Set the root directory
    Set {
        /// Path to set as root directory
        path: PathBuf,
    },
}

#[derive(Subcommand)]
enum RepoCommands {
    /// List repositories adjacent to the root directory
    List,
}

#[derive(Holda)]
#[holda(NoDisplay)]
pub struct RepoPath {
    pub inner: PathBuf,
}
impl std::fmt::Display for RepoPath {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.inner.display())
    }
}

#[derive(StringHolda)]
pub struct CheckedOutBranch {
    pub inner: String,
}

#[derive(Holda)]
#[holda(NoDisplay)]
pub struct LanguageFilePath {
    pub inner: PathBuf,
}
impl std::fmt::Display for LanguageFilePath {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.inner.display())
    }
}

#[derive(Debug)]
pub struct RepositoryList {
    repos: Vec<(RepoPath, CheckedOutBranch)>,
}

impl RepositoryList {
    pub fn new() -> Self {
        Self { repos: Vec::new() }
    }

    pub async fn discover(root_dir: PathBuf) -> eyre::Result<Self> {
        let mut repo_list = Self::new();

        if !root_dir.exists() {
            return Ok(repo_list); // Return empty list if directory doesn't exist
        }

        let entries = fs::read_dir(&root_dir)?;

        for entry in entries {
            let entry = entry?;
            let path = entry.path();

            if path.is_dir() {
                if let Some(dir_name) = path.file_name() {
                    let dir_name = dir_name.to_string_lossy().to_string();

                    // Try to open as a git repository
                    match Repository::open(&path) {
                        Ok(repo) => {
                            let branch_name =
                                get_current_branch(&repo).unwrap_or_else(|_| "unknown".to_string());
                            let repo_path = RepoPath {
                                inner: PathBuf::from(dir_name),
                            };
                            let branch = CheckedOutBranch { inner: branch_name };
                            repo_list.add_repo(repo_path, branch);
                        }
                        Err(_) => {
                            // Not a git repository, skip silently
                        }
                    }
                }
            }
        }

        Ok(repo_list)
    }

    pub fn add_repo(&mut self, path: RepoPath, branch: CheckedOutBranch) {
        self.repos.push((path, branch));
    }

    pub fn is_empty(&self) -> bool {
        self.repos.is_empty()
    }
}

#[derive(Debug)]
pub struct LanguageFileList {
    files: Vec<(LanguageFilePath, CheckedOutBranch)>,
}

impl LanguageFileList {
    pub fn new() -> Self {
        Self { files: Vec::new() }
    }

    pub async fn discover(root_dir: PathBuf) -> eyre::Result<Self> {
        let mut file_list = Self::new();

        if !root_dir.exists() {
            return Ok(file_list); // Return empty list if directory doesn't exist
        }

        // Look for git repository at root_dir
        let repo = match Repository::open(&root_dir) {
            Ok(repo) => repo,
            Err(_) => return Ok(file_list), // Not a git repository
        };

        let branch_name = get_current_branch(&repo).unwrap_or_else(|_| "unknown".to_string());
        let branch = CheckedOutBranch { inner: branch_name };

        // Look for src directory
        let src_dir = root_dir.join("src");
        if !src_dir.exists() {
            return Ok(file_list);
        }

        // Read source sets (children of src)
        let src_entries = fs::read_dir(&src_dir)?;

        for src_entry in src_entries {
            let src_entry = src_entry?;
            let source_set_path = src_entry.path();

            if source_set_path.is_dir() {
                // Look for generated/resources/assets/sfm/lang or resources/assets/sfm/lang
                let possible_lang_paths = [
                    source_set_path.join("generated").join("resources").join("assets").join("sfm").join("lang"),
                    source_set_path.join("resources").join("assets").join("sfm").join("lang"),
                ];

                for lang_path in &possible_lang_paths {
                    if lang_path.exists() && lang_path.is_dir() {
                        // Find all .json files in this directory
                        let lang_entries = fs::read_dir(lang_path)?;

                        for lang_entry in lang_entries {
                            let lang_entry = lang_entry?;
                            let file_path = lang_entry.path();

                            if file_path.is_file() && file_path.extension().map_or(false, |ext| ext == "json") {
                                let language_file = LanguageFilePath {
                                    inner: file_path.clone(),
                                };
                                file_list.add_file(language_file, branch.clone());
                            }
                        }
                    }
                }
            }
        }

        Ok(file_list)
    }

    pub fn add_file(&mut self, path: LanguageFilePath, branch: CheckedOutBranch) {
        self.files.push((path, branch));
    }

    pub fn is_empty(&self) -> bool {
        self.files.is_empty()
    }
}

#[tokio::main]
async fn main() -> eyre::Result<()> {
    color_eyre::install()?;

    let cli = Cli::parse();

    match cli.command {
        Commands::File { file_command } => match file_command {
            FileCommands::List => {
                list_translation_files().await?;
            }
            FileCommands::Add { path } => {
                add_translation_file(path).await?;
            }
        },
        Commands::Config { config_command } => match config_command {
            ConfigCommands::Show => {
                show_config().await?;
            }
            ConfigCommands::Root { root_command } => match root_command {
                RootCommands::Set { path } => {
                    set_root_directory(path).await?;
                }
            },
        },
        Commands::Repo { repo_command } => match repo_command {
            RepoCommands::List => {
                list_repositories().await?;
            }
        },
    }

    Ok(())
}

async fn list_translation_files() -> eyre::Result<()> {
    let config = TranslationHelperConfig::load().await?;

    let root_dir = match config.root_dir {
        Some(root) => root,
        None => {
            println!("No root directory configured. Use 'config root set <path>' to set one.");
            return Ok(());
        }
    };

    if !root_dir.exists() {
        println!("Root directory does not exist: {}", root_dir.display());
        return Ok(());
    }

    println!(
        "Discovering language files in root directory ({}):",
        root_dir.display()
    );

    let language_files = LanguageFileList::discover(root_dir).await?;
    
    if language_files.is_empty() {
        println!("No language files found.");
    } else {
        println!("Language files:");
        for (language_file, branch) in &language_files.files {
            println!("  {} (branch: {})", language_file, branch.inner);
        }
    }

    Ok(())
}

async fn add_translation_file(path: PathBuf) -> eyre::Result<()> {
    let mut config = TranslationHelperConfig::load().await?;

    if config.translation_file_paths.contains(&path) {
        println!("Translation file already exists: {}", path.display());
    } else {
        config.translation_file_paths.push(path.clone());
        config.save().await?;
        println!("Added translation file: {}", path.display());
        info!("Translation file added successfully");
    }

    Ok(())
}

async fn set_root_directory(path: PathBuf) -> eyre::Result<()> {
    let mut config = TranslationHelperConfig::load().await?;

    // Convert to absolute path and canonicalize to resolve .. and . components
    let absolute_path = if path.is_absolute() {
        path.canonicalize()?
    } else {
        env::current_dir()?.join(path).canonicalize()?
    };

    config.root_dir = Some(absolute_path.clone());
    config.save().await?;

    println!("Root directory set to: {}", absolute_path.display());
    info!("Root directory updated successfully");

    Ok(())
}

async fn show_config() -> eyre::Result<()> {
    let config = TranslationHelperConfig::load().await?;
    
    println!("Configuration:");
    println!();
    
    // Display the configuration content in JSON format
    let config_json = serde_json::to_string_pretty(&config)?;
    println!("{}", config_json);

    Ok(())
}

async fn list_repositories() -> eyre::Result<()> {
    let config = TranslationHelperConfig::load().await?;

    let root_dir = match config.root_dir {
        Some(root) => root,
        None => {
            println!("No root directory configured. Use 'config root set <path>' to set one.");
            return Ok(());
        }
    };

    if !root_dir.exists() {
        println!("Root directory does not exist: {}", root_dir.display());
        return Ok(());
    }

    println!(
        "Repositories adjacent to root directory ({}):",
        root_dir.display()
    );

    let repo_list = RepositoryList::discover(root_dir).await?;
    println!("{:#?}", repo_list);

    Ok(())
}

fn get_current_branch(repo: &Repository) -> Result<String, git2::Error> {
    let head = repo.head()?;

    if let Some(name) = head.shorthand() {
        Ok(name.to_string())
    } else {
        // HEAD is detached, try to get the commit SHA
        if let Some(oid) = head.target() {
            Ok(format!("detached@{:.7}", oid))
        } else {
            Ok("unknown".to_string())
        }
    }
}
