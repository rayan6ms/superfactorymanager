package ca.teamdman.ai;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * A utility that:
 *  1. Reads a fully qualified class reference from the clipboard.
 *  2. Uses JavaParser's symbol solver to find the .java source file.
 *  3. Prints out that file (in a code fence) plus any directly imported classes (also in code fences).
 *  4. Places the result back on the system clipboard.
 */
public class FileImportWalker {

    // Adjust this if your sources live somewhere else.
    private static final File SOURCE_ROOT = new File("src/main/java");

    public static void main(String[] args) {
        System.out.println("=== FileImportWalker starting ===");

        // 1) Grab the reference from clipboard
        String reference = getClipboardContents();
        System.out.println("[INFO] Found clipboard reference: " + reference);

        // 2) Create a combined type solver for reflection + local Java sources
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(SOURCE_ROOT));

        // 3) Build a JavaSymbolSolver with the combined solver
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        ParserConfiguration config = new ParserConfiguration()
                .setSymbolResolver(symbolSolver);
        JavaParser parser = new JavaParser(config);

        // 4) Try to resolve the provided reference.
        ResolvedReferenceTypeDeclaration resolvedType =
                combinedTypeSolver.tryToSolveType(reference).getCorrespondingDeclaration();
        if (resolvedType == null) {
            System.err.println("[ERROR] Could not resolve type: " + reference);
            return;
        }

        // 5) Jump to the AST (for the inner class, if it is one),
        //    then go up to the CompilationUnit to get the top-level .java.
        resolvedType.toAst()
                .flatMap(typeDecl -> typeDecl.findAncestor(CompilationUnit.class))
                .ifPresentOrElse(cu -> {
                    // The top-level .java file containing the reference type
                    processCompilationUnit(reference, cu, combinedTypeSolver);
                }, () -> {
                    System.err.println("[ERROR] Could not find a CompilationUnit for " + reference);
                });

        System.out.println("=== FileImportWalker finished ===");
    }

    /**
     * Processes the main CompilationUnit (the top-level file that holds your reference class),
     * gathers its imports, fetches each imported file, and concatenates all of the contents
     * into a single output that is set to the clipboard.
     */
    private static void processCompilationUnit(String reference,
                                               CompilationUnit cu,
                                               CombinedTypeSolver combinedTypeSolver) {
        // We'll store the final big text to eventually put on the clipboard
        StringBuilder sb = new StringBuilder();

        // Indicate the "start" is the reference type
        sb.append("===\n")
                .append("Start: `").append(reference).append("`\n")
                .append("```java\n")
                .append(readRawFileContents(cu)) // The entire file text
                .append("\n```\n\n");

        // Next, handle each import
        List<ImportDeclaration> importList = cu.getImports();
        for (ImportDeclaration imp : importList) {
            // Decide how to handle static imports or wildcards
            if (imp.isStatic()) {
                System.out.println("[WARN] Skipping static import: " + imp);
                continue;
            }
            if (imp.getNameAsString().endsWith(".*")) {
                System.out.println("[WARN] Skipping wildcard import: " + imp);
                continue;
            }

            String importFqn = imp.getNameAsString(); // fully qualified name
            System.out.println("[INFO] Attempting to resolve import: " + importFqn);

            // Attempt to resolve that import to a type
            ResolvedReferenceTypeDeclaration resolvedImport =
                    combinedTypeSolver.tryToSolveType(importFqn).getCorrespondingDeclaration();

            if (resolvedImport != null) {
                // fetch the top-level CU for that import
                resolvedImport.toAst()
                        .flatMap(typeDecl -> typeDecl.findAncestor(CompilationUnit.class))
                        .ifPresent(importCU -> {
                            sb.append("Imported: `")
                                    .append(importFqn)
                                    .append("`\n```java\n")
                                    .append(readRawFileContents(importCU))
                                    .append("\n```\n\n");
                        });
            } else {
                System.out.println("[WARN] Could not resolve import: " + importFqn);
            }
        }

        sb.append("===\n");

        // 6) Place the final big chunk on the clipboard
        setClipboardContents(sb.toString());
        System.out.println("[INFO] Final concatenated content set to clipboard.");
    }

    /**
     * Read the raw file contents that back a CompilationUnit, preserving
     * the original formatting, comments, etc.
     *
     * If the CU doesn't have a source root / path set, we fall back to cu.toString().
     */
    private static String readRawFileContents(CompilationUnit cu) {
        return cu.getStorage()
                .map(storage -> {
                    try {
                        File file = storage.getPath().toFile();
                        System.out.println("[DEBUG] Reading from file: " + file.getAbsolutePath());
                        return Files.readString(file.toPath());
                    } catch (IOException e) {
                        System.err.println("[WARN] Could not read from file, fallback to AST toString().");
                        return cu.toString();
                    }
                })
                .orElseGet(cu::toString);
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
