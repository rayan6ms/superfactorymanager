# Merging after Repository Restructuring

When a Pull Request (PR) was created before a major repository restructuring (e.g., moving files into a platform/ directory), a standard merge will result in massive conflicts. To merge such a PR while ensuring GitHub recognizes it as a "Merged" PR (by preserving the commit history), follow this control flow:

## Process

1. **Fetch the Contributor's Branch**  
   Fetch the specific branch from the contributor's fork into a local tracking branch.
   ```powershell
   git fetch <contributor_repo_url> <branch_name>:<local_tracking_branch_name>
   # Example:
   # git fetch https://github.com/PrincessStelllar/SuperFactoryManager.git patch-3:princess-patch-3
   ```

2. **Start a Merge using the 'Ours' Strategy**  
   This strategy records the merge in the history but ignores all changes from the contributor's branch, keeping your current file structure exactly as is.
   ```powershell
   git merge --no-commit -s ours <local_tracking_branch_name>
   ```

3. **Manually Extract the Target Files**  
   Since the ours strategy ignored the contributor's changes, you must manually grab the files you want from their original paths and place them into the new repository structure.
   ```powershell
   git show <local_tracking_branch_name>:<old_path_to_file> > <new_path_to_file>
   # Example:
   # git show princess-patch-3:src/main/resources/assets/sfm/lang/pt_br.json > platform/minecraft/src/main/resources/assets/sfm/lang/pt_br.json
   ```

4. **Verify and Stage the Changes**  
   Check the content of the file to ensure it matches expectations, then stage it.
   ```powershell
   git add <new_path_to_file>
   ```

5. **Conclude the Merge**  
   Commit the merge. Using a standard merge commit message helps GitHub associate the commit with the PR.
   ```powershell
   git commit -m "Merge branch '<contributor_name>/<branch_name>' into <target_branch>"
   ```

## Handling Multiple Files

The "Manual Extraction" step (Step 3) can be scaled for multiple files depending on the volume:

### Option A: Batch Checkout (1-10 Files)
If the number of files is small, you can bring them into your workspace at their *original* paths and then move them.
```powershell
# Restore files from the contributor's branch at their original paths
git checkout <local_tracking_branch_name> -- path/to/file1.json path/to/file2.json

# Move them to the new structure
mv path/to/file1.json platform/minecraft/path/to/file1.json
```

### Option B: Automated Remap (Many Files)
If there are many files following a predictable remapping rule (e.g., all `src/` files moved to `platform/minecraft/src/`), use a loop:
```powershell
# 1. Get the list of changed files from the contributor's branch
$files = git diff --name-only HEAD...<local_tracking_branch_name>

# 2. Iterate and move
foreach ($file in $files) {
    # Define the mapping logic
    $newPath = $file.Replace("src/", "platform/minecraft/src/")
    
    # Ensure target directory exists
    New-Item -ItemType Directory -Force -Path (Split-Path $newPath)
    
    # Extract directly into the new location
    git show "<local_tracking_branch_name>:$file" | Out-File -FilePath $newPath -Encoding utf8
}
```

## Important Considerations

*   **Deletions**: The `ours` strategy ignores deletions made by the contributor. If their PR intended to remove a file, you must manually run `git rm <file>` in your branch.
*   **New Files**: Files added by the contributor will not automatically appear; use the methods above to bring them in.
*   **Manual Reconciliation**: Since you are overwriting files, you are effectively "Accepting Theirs." If the same file was modified in your branch since the restructuring, you must manually diff and reconcile the changes.

## Why this works
By using `git merge`, we create a commit with two parents. One of those parents is the contributor's original commit hash. When this merge commit is pushed to the main repository, GitHub's matching algorithm sees their commit hash in the history and marks the Pull Request as **Merged**. Using the `-s ours` strategy prevents Git from trying to automatically resolve directory renames that it might find confusing due to the scale of the restructuring.
