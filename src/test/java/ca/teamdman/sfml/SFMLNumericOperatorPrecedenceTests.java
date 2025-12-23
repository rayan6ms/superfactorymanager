package ca.teamdman.sfml;

import ca.teamdman.langs.SFMLLexer;
import ca.teamdman.langs.SFMLParser;
import ca.teamdman.sfml.ast.ASTBuilder;
import ca.teamdman.sfml.ast.Number;
import ca.teamdman.sfml.ast.Program;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class SFMLNumericOperatorPrecedenceTests {
    @Test
    public void itWorks() {

        String expr = "2+2*70";
        var lexer = new SFMLLexer(CharStreams.fromString(expr));
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

        var context = parser.numberExpression();
        Number number = null;
        if (lexerErrors.isEmpty() && parserErrors.isEmpty()) { // don't build if syntax errors present
            try {
                //noinspection unused
                number = (Number) builder.visit(context);
            } catch (Exception e) {
                visitProblems.add(e);
            }
        }
        if (!lexerErrors.isEmpty()) {
            throw new IllegalArgumentException("Lexer errors: " + String.join(", ", lexerErrors));
        }
        if (!parserErrors.isEmpty()) {
            throw new IllegalArgumentException("Parser errors: " + String.join(", ", parserErrors));
        }
        if (!visitProblems.isEmpty()) {
            throw new IllegalStateException("Failed to parse expression", visitProblems.get(0));
        }
        if (number.value() != 142) {
            throw new IllegalStateException("Expected 142, got " + number.value());
        }
    }

}
