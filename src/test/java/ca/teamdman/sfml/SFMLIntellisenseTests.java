package ca.teamdman.sfml;

import ca.teamdman.sfml.intellisense.IntellisenseAction;
import ca.teamdman.sfml.intellisense.IntellisenseContext;
import ca.teamdman.sfml.intellisense.SFMLIntellisense;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SFMLIntellisenseTests {
    @Test
    public void itWorksTest() {
        String program = """
                NAME "hi"
                """.stripTrailing().stripIndent();
        List<IntellisenseAction> suggestions = SFMLIntellisense.getSuggestions(new IntellisenseContext(program, 0, 0));
        for (IntellisenseAction suggestion : suggestions) {
            System.out.printf("Suggestion: %s (%s)\n", suggestion.getDisplayText(), suggestion);
        }
    }
}
