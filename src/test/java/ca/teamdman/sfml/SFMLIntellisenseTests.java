package ca.teamdman.sfml;

import ca.teamdman.langs.SFMLLexer;
import ca.teamdman.sfml.ast.ASTNode;
import ca.teamdman.sfml.ast.Program;
import ca.teamdman.sfml.intellisense.IntellisenseAction;
import ca.teamdman.sfml.intellisense.IntellisenseContext;
import ca.teamdman.sfml.intellisense.SFMLIntellisense;
import com.mojang.datafixers.util.Pair;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
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
        String program = """
                NAME "hi"
                EVERY 20
                """.stripTrailing().stripIndent();
        for (int cursorPosition = 0; cursorPosition < countTokens(program); cursorPosition++) {
            StringBuilder display = new StringBuilder();
            List<IntellisenseAction> suggestions = SFMLIntellisense.getSuggestions(new IntellisenseContext(
                    program,
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
            System.out.print(getCursorDisplay(programString, cursorPos));

            // print the tokens under the cursor
//            System.out.print(" [");
            for (Pair<ASTNode, ParserRuleContext> pair : program.get().builder().getNodesUnderCursor(cursorPos)) {
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
        AtomicReference<Program> programBox = new AtomicReference<>();
        Program.compile(
                programString,
                programBox::set,
                failure -> {
                    failure.forEach(error -> System.out.println(error.toString()));
                    throw new RuntimeException("Failed to compile program");
                }
        );
        Program program = programBox.get();
        for (int cursorPos = 0; cursorPos < programString.length(); cursorPos++) {
            System.out.print("||| ");
            System.out.printf("%s", getCursorDisplay(programString, cursorPos));
            System.out.print(" ||| ");
            System.out.printf("%s", getTokenHierarchyDisplay(program, cursorPos));
            System.out.print(" ||| ");
            System.out.printf("%s", getSuggestionsDisplay(programString, cursorPos));
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

    private static String getCursorDisplay(
            String programString,
            int cursorPos
    ) {
        StringBuilder rtn = new StringBuilder();
        rtn.append(" [");
        // print the 10-closest characters before the cursor
        int start = Math.max(0, cursorPos - 10);
        int end = cursorPos;
        rtn.append(String.format("%20s", programString.substring(start, end).replaceAll("\n", "\\\\n")));

        // print |
        rtn.append("|");

        // print the 10 characters after the cursor
        start = cursorPos;
        end = Math.min(programString.length(), cursorPos + 10);
        rtn.append(String.format("%-20s", programString.substring(start, end).replaceAll("\n", "\\\\n")));
        rtn.append(" ] ");
        return rtn.toString();
    }

    private static String getTokenHierarchyDisplay(
            Program program,
            int cursorPos
    ) {
        StringBuilder rtn = new StringBuilder();
        List<Pair<ASTNode, ParserRuleContext>> nodesUnderCursor = program.builder().getNodesUnderCursor(cursorPos);

        var iter = nodesUnderCursor.listIterator(nodesUnderCursor.size());
        while (iter.hasPrevious()) {
            Pair<ASTNode, ParserRuleContext> pair = iter.previous();
            ASTNode node = pair.getFirst();
            rtn.append(node.getClass().getSimpleName());
            if (iter.hasPrevious()) {
                rtn.append(" -> ");
            }
        }
        return rtn.toString();
    }

    private static String getSuggestionsDisplay(
            String programString,
            int cursorPos
    ) {
        StringBuilder rtn = new StringBuilder();
        List<IntellisenseAction> suggestions = SFMLIntellisense.getSuggestions(new IntellisenseContext(
                programString,
                cursorPos,
                0
        ));
        rtn.append('[');
        for (int i = 0; i < suggestions.size(); i++) {
            rtn.append(suggestions.get(i).getDisplayText().replaceAll("\n", "\\\\n"));
            if (i != suggestions.size() - 1) {
                rtn.append(", ");
            }
        }
        rtn.append(']');
        return rtn.toString();
    }
}
