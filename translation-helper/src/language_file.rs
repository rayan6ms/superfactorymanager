use crate::source_set_path::SourceSetPath;
use holda::Holda;
use std::path::PathBuf;
use tracing::debug;

#[derive(Holda)]
#[holda(NoDisplay)]
pub struct LanguageFilePath {
    inner: PathBuf,
}
impl std::fmt::Display for LanguageFilePath {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.inner.display())
    }
}

impl LanguageFilePath {
    pub fn discover_in_source_set(
        source_set_path: &SourceSetPath,
    ) -> eyre::Result<Vec<LanguageFilePath>> {
        let mut paths = Vec::new();
        let lang_dir = source_set_path.join(r"resources\assets\sfm\lang\");
        let entries = match std::fs::read_dir(&lang_dir) {
            Ok(entries) => entries,
            Err(e) => {
                debug!(
                    "Failed to read language directory, probably doesn't exist for this source set\n{:?}\n{}",
                    lang_dir.display(),
                    e
                );
                return Ok(paths);
            }
        };
        for entry in entries {
            let entry = entry?;
            if entry.file_type()?.is_file() {
                let path = entry.path();
                if path.extension().map(|e| e == "json").unwrap_or(false) {
                    paths.push(LanguageFilePath { inner: path });
                }
            }
        }
        Ok(paths)
    }
}
