package ca.teamdman.sfml;

import ca.teamdman.langs.SFMLLexer;
import ca.teamdman.sfm.common.util.SFMDisplayUtils;
import ca.teamdman.sfml.ast.ASTNode;
import ca.teamdman.sfml.ast.Program;
import ca.teamdman.sfml.intellisense.IntellisenseAction;
import ca.teamdman.sfml.intellisense.IntellisenseContext;
import ca.teamdman.sfml.intellisense.SFMLIntellisense;
import ca.teamdman.sfml.program_builder.ProgramBuildResult;
import ca.teamdman.sfml.program_builder.ProgramBuilder;
import com.mojang.datafixers.util.Pair;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SFMLIntellisenseTests {

    private static final String SIMPLE_PROGRAM_STRING = """
            NAME "hello"
            EVERY 20 TICKS DO
              INPUT FROM a
              OUTPUT TO b
            END
             """.stripTrailing().stripIndent();

    @Test
    public void displayTokens() {
        SFMLLexer lexer = new SFMLLexer(CharStreams.fromString(SIMPLE_PROGRAM_STRING));
        lexer.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();

        int cursorPosition = 0;
        int i = 0;
        for (Token token : tokens.get(0, cursorPosition)) {
            if (i++ == cursorPosition) {
                System.out.print("|");
            }

            String text = token.getText();
            if (text.isBlank()) {
                System.out.printf("'%s'", text);
            } else {
                System.out.printf("%s", text);
            }
        }
    }

    @Test
    public void itWorksTest() {
        String programString = """
                NAME "hi"
                EVERY 20
                """.stripTrailing().stripIndent();
        for (int cursorPosition = 0; cursorPosition < countTokens(programString); cursorPosition++) {
            StringBuilder display = new StringBuilder();
            ProgramBuildResult buildResult = ProgramBuilder.build(programString);
            List<IntellisenseAction> suggestions = SFMLIntellisense.getSuggestions(new IntellisenseContext(
                    buildResult,
                    cursorPosition,
                    0
            ));
            for (IntellisenseAction suggestion : suggestions) {
                display.append("Suggestion: ");
                display.append(suggestion.getDisplayText());
                display.append('\n');
            }
            display.append('\n');
            String finalDisplay = display.toString();
            System.out.println("Cursor position: " + cursorPosition);
            System.out.println(finalDisplay);
        }
    }

    @Test
    public void getNodesUnderCursor() {
        String programString = SIMPLE_PROGRAM_STRING;

        // Build the program
        AtomicReference<Program> program = new AtomicReference<>();
        Program.compile(
                programString,
                program::set,
                failure -> {
                    failure.forEach(error -> System.out.println(error.toString()));
                    throw new RuntimeException("Failed to compile program");
                }
        );
        assertNotNull(program.get());

        // Put each cursor position as a new line in the stdout
        for (int cursorPos = 0; cursorPos < programString.length(); cursorPos++) {
            System.out.print(SFMDisplayUtils.getCursorPositionDisplay(programString, cursorPos));

            // print the tokens under the cursor
//            System.out.print(" [");
            for (Pair<ASTNode, ParserRuleContext> pair : program.get().astBuilder().getNodesUnderCursor(cursorPos)) {
                ASTNode node = pair.getFirst();
                ParserRuleContext nodeContext = pair.getSecond();
                System.out.printf("%s(%s) ", node.getClass().getSimpleName(), nodeContext.getText());
            }
//            System.out.print(" ]");
            System.out.println();
        }
    }

    @Test
    public void combinedTest() {
        String programString = SIMPLE_PROGRAM_STRING;
        ProgramBuildResult buildResult = ProgramBuilder.build(programString).caseFailure(failure -> {
            failure.metadata().errors().forEach(error -> System.out.println(error.toString()));
            throw new RuntimeException("Failed to compile program");
        });
        Program program = Objects.requireNonNull(buildResult.program());
        for (int cursorPos = 0; cursorPos < programString.length(); cursorPos++) {
            System.out.print("||| ");
            System.out.printf("%s", SFMDisplayUtils.getCursorPositionDisplay(programString, cursorPos));
            System.out.print(" ||| ");
            System.out.printf("%s", SFMDisplayUtils.getTokenHierarchyDisplay(program, cursorPos));
            System.out.print(" ||| ");
            System.out.printf("%s", SFMDisplayUtils.getSuggestionsDisplay(buildResult, cursorPos));
            System.out.print(" |||");
            System.out.println();
        }
    }

    private static int countTokens(String program) {
        SFMLLexer lexer = new SFMLLexer(CharStreams.fromString(program));
        lexer.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();

        return tokens.size();
    }

}
