use crate::game_version::GameVersion;
use crate::language_file_path::LanguageFilePath;
use eyre::Context;
use std::collections::HashMap;
use std::fs;
use std::path::Path;

pub struct LanguageFile {
    pub path: LanguageFilePath,
    pub contents: HashMap<String, String>,
}
impl LanguageFile {
    pub fn load_from_path(path: LanguageFilePath) -> eyre::Result<Self> {
        let contents = fs::read_to_string(path.as_path())
            .wrap_err_with(|| format!("Failed to read language file: {}", path.display()))?;
        let contents = serde_json::from_str(&contents)
            .wrap_err_with(|| format!("Failed to parse language file: {}", path.display()))?;
        Ok(Self { path, contents })
    }
    pub fn discover_in_root_dir(root_dir: &Path) -> eyre::Result<Vec<(LanguageFile, GameVersion)>> {
        let language_files = LanguageFilePath::discover_in_root_dir(root_dir)?;
        let mut files = Vec::new();
        for (path, game_version) in language_files {
            let file = LanguageFile::load_from_path(path)?;
            files.push((file, game_version));
        }
        Ok(files)
    }
}
