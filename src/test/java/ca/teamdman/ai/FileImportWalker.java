package ca.teamdman.ai;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.model.SymbolReference;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * A utility that:
 *  1. Reads a fully qualified class reference from the clipboard.
 *  2. Uses JavaParser's symbol solver to find the .java source file (if available).
 *  3. Prints out that file (in a code fence) plus any directly imported classes (also in code fences).
 *  4. Places the result back on the system clipboard.
 *
 *  If classes are from libraries / jars but we've extracted their sources
 *  into build/tmp/expandedArchives, we should be able to find them.
 *  Otherwise, we show a loud error message.
 */
public class FileImportWalker {

    // Adjust if your project sources live elsewhere
    private static final File SOURCE_ROOT = new File("src/main/java");

    // Add the path where Gradle expands source jars
    private static final File EXPANDED_ARCHIVES_ROOT = new File("build/tmp/expandedArchives");

    public static void main(String[] args) {
        System.out.println("=== FileImportWalker starting ===");

        // 1) Grab the reference from clipboard
        String reference = getClipboardContents();
        System.out.println("[INFO] Found clipboard reference: " + reference);

        // 2) Create a combined type solver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        // a) Reflection = standard JDK
        combinedTypeSolver.add(new ReflectionTypeSolver());
        // b) Local source
        combinedTypeSolver.add(new JavaParserTypeSolver(SOURCE_ROOT));
        // c) For each subdirectory of expandedArchives, add a JavaParserTypeSolver
        addExpandedArchiveTypeSolvers(combinedTypeSolver, EXPANDED_ARCHIVES_ROOT);
        // d) ClassLoader solver as fallback (so we don't crash on unsolved external references)
        combinedTypeSolver.add(new ClassLoaderTypeSolver(FileImportWalker.class.getClassLoader()));

        // 3) Build a JavaSymbolSolver
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        ParserConfiguration config = new ParserConfiguration()
                .setSymbolResolver(symbolSolver);
        JavaParser parser = new JavaParser(config);

        // 4) Attempt to resolve the reference
        SymbolReference<ResolvedReferenceTypeDeclaration> symbolRef =
                combinedTypeSolver.tryToSolveType(reference);

        if (!symbolRef.isSolved()) {
            System.err.println("[ERROR] Could not resolve type: " + reference);
            System.err.println("        Perhaps it's not in local src or expandedArchives. Aborting.");
            return;
        }

        ResolvedReferenceTypeDeclaration resolvedType = symbolRef.getCorrespondingDeclaration();

        // 5) Convert to AST, then go up to the top-level CompilationUnit
        resolvedType.toAst()
                .flatMap(typeDecl -> typeDecl.findAncestor(CompilationUnit.class))
                .ifPresentOrElse(cu -> {
                    try {
                        processCompilationUnit(reference, cu, combinedTypeSolver);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, () -> {
                    // If no CU found, that means it's in a jar w/o source or some unresolved location
                    System.err.println("[ERROR] No CompilationUnit for " + reference
                                       + ", possibly no .java in expanded archives. Aborting.");
                });

        System.out.println("=== FileImportWalker finished ===");
    }

    /**
     * Scans build/tmp/expandedArchives for subdirectories (each containing
     * extracted source from a jar), and adds them as a JavaParserTypeSolver.
     */
    private static void addExpandedArchiveTypeSolvers(CombinedTypeSolver combinedSolver, File rootDir) {
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.out.println("[WARN] " + rootDir + " is not a directory. Not adding expanded archives solvers.");
            return;
        }

        File[] subdirs = rootDir.listFiles(File::isDirectory);
        if (subdirs == null || subdirs.length == 0) {
            System.out.println("[WARN] No subdirectories found under " + rootDir + ".");
            return;
        }

        for (File subdir : subdirs) {
            System.out.println("[INFO] Adding JavaParserTypeSolver for expanded source dir: " + subdir.getAbsolutePath());
            combinedSolver.add(new JavaParserTypeSolver(subdir));
        }
    }

    /**
     * Processes the main CompilationUnit (the top-level file that holds your reference class),
     * gathers its imports, fetches each imported file if available, and concatenates all of the contents
     * into a single output that is set to the clipboard.
     */
    private static void processCompilationUnit(String reference,
                                               @NotNull CompilationUnit cu,
                                               CombinedTypeSolver combinedTypeSolver) throws IOException {

        StringBuilder sb = new StringBuilder();

        // Mark the start
        sb.append("===\n")
                .append("Start: `").append(reference).append("`\n")
                .append("```java\n")
                .append(readRawFileContents(cu))
                .append("\n```\n\n");

        // For each import, see if we can find its local .java
        for (ImportDeclaration imp : cu.getImports()) {
            if (imp.isStatic()) {
                System.err.println("[WARN] Skipping static import: " + imp);
                continue;
            }
            if (imp.getNameAsString().endsWith(".*")) {
                System.err.println("[WARN] Skipping wildcard import: " + imp);
                continue;
            }

            String importFqn = imp.getNameAsString();
            System.out.println("[INFO] Attempting to resolve import: " + importFqn);

            try {
                SymbolReference<ResolvedReferenceTypeDeclaration> importRef =
                        combinedTypeSolver.tryToSolveType(importFqn);
                if (!importRef.isSolved()) {
                    System.err.println("[ERROR] Could NOT resolve " + importFqn
                                       + " in local or expandedArchives. Possibly external or missing sources.");
                    continue;
                }

                ResolvedReferenceTypeDeclaration resolvedImport = importRef.getCorrespondingDeclaration();
                resolvedImport.toAst()
                        .flatMap(typeDecl -> typeDecl.findAncestor(CompilationUnit.class))
                        .ifPresentOrElse(importCU -> {
                            sb.append("Imported: `")
                                    .append(importFqn)
                                    .append("`\n```java\n")
                                    .append(readRawFileContents(importCU))
                                    .append("\n```\n\n");
                        }, () -> {
                            System.err.println("[ERROR] We resolved " + importFqn
                                               + " but can't find a CompilationUnit (no .java?).");
                        });
            } catch (UnsolvedSymbolException use) {
                // That means something is definitely missing
                System.err.println("[ERROR] UnsolvedSymbol for " + importFqn
                                   + " => " + use.getMessage());
            }
        }

        sb.append("===\n");

        // Place the final chunk on the clipboard
        setClipboardContents(sb.toString());
        System.out.println("[INFO] Final concatenated content set to clipboard.");
    }

    /**
     * Read the raw file contents that back a CompilationUnit, preserving
     * the original formatting, comments, etc.
     *
     * If no storage path is found, we fallback to cu.toString().
     */
    private static String readRawFileContents(CompilationUnit cu) {
        return cu.getStorage()
                .map(storage -> {
                    File file = storage.getPath().toFile();
                    System.out.println("[DEBUG] Reading from file: " + file.getAbsolutePath());
                    try {
                        return Files.readString(file.toPath());
                    } catch (IOException e) {
                        System.err.println("[ERROR] Could not read from file. Fallback to cu.toString().");
                        return cu.toString();
                    }
                })
                .orElseGet(() -> {
                    System.err.println("[ERROR] No storage found for CU. Using cu.toString().");
                    return cu.toString();
                });
    }

    /**
     * Grab a string from the system clipboard.
     */
    private static String getClipboardContents() {
        try {
            var clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            var contents = clipboard.getContents(null);
            boolean hasText = (contents != null) &&
                              contents.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor);
            if (hasText) {
                return (String) contents.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
            } else {
                throw new RuntimeException("[ERROR] Clipboard does not contain a string.");
            }
        } catch (UnsupportedFlavorException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Put a string onto the system clipboard.
     */
    private static void setClipboardContents(String text) {
        var selection = new java.awt.datatransfer.StringSelection(text);
        var clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }
}
