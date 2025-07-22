use crate::cli::args::Command;
use crate::cli::args::ConfigCommands;
use crate::cli::args::FileCommands;
use crate::cli::args::RepoCommands;
use crate::cli::args::RootCommands;
use crate::config::TranslationHelperConfig;
use crate::config::get_root_dir;
use crate::repository::discover_repositories;
use crate::source_set::SourceSet;
use eye_config::persistable_state::PersistableState;
use std::env;
use tracing::info;

pub async fn handle_command(command: Command) -> eyre::Result<()> {
    match command {
        Command::File { file_command } => match file_command {
            FileCommands::List => {
                let root_dir = get_root_dir().await?;
                for (repo, game_version) in discover_repositories(root_dir).await? {
                    info!("Repo: {}, Game Version: {}", repo, game_version);
                    let source_sets = SourceSet::list_in_repo(repo.clone())?;
                    for source_set in source_sets {
                        info!("Source Set: {}", source_set);
                        for language_file in source_set.list_language_files()? {
                            info!("Language File: {}", language_file);
                        }
                    }
                }
            }
        },
        Command::Config { config_command } => match config_command {
            ConfigCommands::Show => {
                let config = TranslationHelperConfig::load().await?;
                info!("Configuration:");
                let config_json = serde_json::to_string_pretty(&config)?;
                println!("{config_json}");
            }
            ConfigCommands::Root { root_command } => match root_command {
                RootCommands::Set { path } => {
                    let mut config = TranslationHelperConfig::load().await?;
                    let absolute_path = if path.is_absolute() {
                        path.canonicalize()?
                    } else {
                        env::current_dir()?.join(path).canonicalize()?
                    };
                    config.root_dir = Some(absolute_path.clone());
                    config.save().await?;
                    info!("Root directory set to: {}", absolute_path.display());
                    println!("{}", absolute_path.display());
                }
            },
        },
        Command::Repo { repo_command } => match repo_command {
            RepoCommands::List => {
                let root_dir = get_root_dir().await?;
                let repos = discover_repositories(root_dir).await?;
                info!("Discovered {} repos", repos.len());
                let json_output = serde_json::to_string_pretty(&repos)?;
                println!("{json_output}");
            }
        },
    }
    Ok(())
}
