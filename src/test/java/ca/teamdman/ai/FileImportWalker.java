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
import java.util.List;

public class FileImportWalker {
    public static void main(String[] args) {
        // 1) Create a combined type solver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        // - ReflectionTypeSolver: basic JDK classes
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // - JavaParserTypeSolver: your source path, so it can parse .java files
        combinedTypeSolver.add(new JavaParserTypeSolver(new File("src/main/java")));

        // If you have compiled .class files in certain directories or JARs, there are other TypeSolvers too

        // 2) Build the symbol solver
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        ParserConfiguration config = new ParserConfiguration()
                .setSymbolResolver(symbolSolver);
        JavaParser parser = new JavaParser(config);

        // 3) Suppose we want to find the type for "ca.teamdman.sfm.client.gui.screen.ProgramEditScreen.MyMultiLineEditBox"
        String reference = "ca.teamdman.sfm.client.gui.screen.ProgramEditScreen.MyMultiLineEditBox";

        // 4) Let the type solver try to solve it:
        ResolvedReferenceTypeDeclaration resolvedType =
                combinedTypeSolver.tryToSolveType(reference).getCorrespondingDeclaration();

        // If found, we can ask for its AST:
        // `typeDecl` is the Node for the inner class (MyMultiLineEditBox).
        // If we want the top-level container, we can go up the tree:
        //noinspection unchecked
        resolvedType.toAst().flatMap(typeDecl -> typeDecl.findAncestor(CompilationUnit.class)).ifPresent(cu -> {
            // Now `cu` is the entire file ProgramEditScreen.java
            List<ImportDeclaration> importList = cu.getImports();
            importList.forEach(imp -> System.out.println("Import: " + imp));
        });
    }

    private static String getClipboardContents() {
        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        java.awt.datatransfer.Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null)
                                      && contents.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor);
        if (hasTransferableText) {
            String result;
            try {
                result = (String) contents.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                throw new RuntimeException(e);
            }
            return result;
        } else {
            throw new RuntimeException("Clipboard does not contain a string");
        }
    }
}
