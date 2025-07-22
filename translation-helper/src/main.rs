pub mod checked_out_branch;
pub mod cli;
pub mod commands;
pub mod config;
pub mod game_version;
pub mod language_file_path;
pub mod repo_path;
pub mod repository;
pub mod source_set;
pub mod source_set_path;
pub mod language_file;

use clap::Parser;
use cli::args::Cli;
use cli::init_tracing::init_tracing;

#[tokio::main]
pub async fn main() -> eyre::Result<()> {
    color_eyre::install()?;

    let cli = Cli::parse();

    // Initialize tracing with the global args
    init_tracing(&cli.global, std::io::stderr)?;

    commands::handle_command(cli.command).await?;

    Ok(())
}
