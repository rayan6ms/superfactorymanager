package ca.teamdman.sfml;

import ca.teamdman.langs.SFMLLexer;
import ca.teamdman.langs.SFMLParser;
import ca.teamdman.sfml.ast.ASTBuilder;
import ca.teamdman.sfml.ast.Program;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SFMLTestHelpers {
    public static CompileErrors getCompileErrors(String input) {

        var lexer = new SFMLLexer(CharStreams.fromString(input));
        var tokens = new CommonTokenStream(lexer);
        var parser = new SFMLParser(tokens);
        var builder = new ASTBuilder();
        var lexerErrors = new ArrayList<String>();
        var parserErrors = new ArrayList<String>();
        var visitProblems = new ArrayList<Throwable>();
        lexer.removeErrorListeners();
        lexer.addErrorListener(new Program.ListErrorListener(lexerErrors));
        parser.removeErrorListeners();
        parser.addErrorListener(new Program.ListErrorListener(parserErrors));
        var context = parser.program();
        if (lexerErrors.isEmpty() && parserErrors.isEmpty()) { // don't build if syntax errors present
            try {
                //noinspection unused
                var program = builder.visitProgram(context);
            } catch (Exception e) {
                visitProblems.add(e);
            }
        }

        return new CompileErrors(lexerErrors, parserErrors, visitProblems);
    }

    public static <CONTEXT, ASTNODE> CompileErrors getCompileErrorsV2(
            String input,
            Function<SFMLParser, CONTEXT> a,
            BiFunction<ASTBuilder, CONTEXT, ASTNODE> b
    ) {

        var lexer = new SFMLLexer(CharStreams.fromString(input));
        var tokens = new CommonTokenStream(lexer);
        var parser = new SFMLParser(tokens);
        var builder = new ASTBuilder();
        var lexerErrors = new ArrayList<String>();
        var parserErrors = new ArrayList<String>();
        var visitProblems = new ArrayList<Throwable>();
        lexer.removeErrorListeners();
        lexer.addErrorListener(new Program.ListErrorListener(lexerErrors));
        parser.removeErrorListeners();
        parser.addErrorListener(new Program.ListErrorListener(parserErrors));
        var context = a.apply(parser);
        if (lexerErrors.isEmpty() && parserErrors.isEmpty()) { // don't build if syntax errors present
            try {
                //noinspection unused
                var program = b.apply(builder, context);
            } catch (Exception e) {
                visitProblems.add(e);
            }
        }

        return new CompileErrors(lexerErrors, parserErrors, visitProblems);
    }

    public static void assertNoCompileErrors(String program) {

        CompileErrors compileErrors = getCompileErrors(program);
        compileErrors.printStackStraces();
        assertEquals(
                compileErrors,
                CompileErrors.NONE
        );
    }

    public static void assertCompileErrorsPresent(
            String program,
            CompileErrors expected
    ) {

        CompileErrors compileErrors = getCompileErrors(program);
        compileErrors.printStackStraces();
        assertEquals(
                compileErrors,
                expected
        );
    }

    public static void assertCompileErrorsPresent(
            String program,
            Throwable... visitProblems
    ) {

        CompileErrors compileErrors = getCompileErrors(program);
        compileErrors.printStackStraces();
        assertEquals(
                compileErrors,
                new CompileErrors(visitProblems)
        );
    }

    public static void assertCompileErrorsPresent(
            String program
    ) {

        assertNotEquals(
                getCompileErrors(program),
                CompileErrors.NONE
        );
    }

    public static Program compile(String input) {

        var lexer = new SFMLLexer(CharStreams.fromString(input));
        var tokens = new CommonTokenStream(lexer);
        var parser = new SFMLParser(tokens);
        var builder = new ASTBuilder();
        var context = parser.program();
        return builder.visitProgram(context);
    }

}
