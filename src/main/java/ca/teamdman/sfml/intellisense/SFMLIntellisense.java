package ca.teamdman.sfml.intellisense;

import ca.teamdman.langs.SFMLParser;
import ca.teamdman.sfml.ext_antlr4c3.CodeCompletionCore;
import org.antlr.v4.runtime.Vocabulary;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SFMLIntellisense {
    public static List<IntellisenseAction> getSuggestions(
            IntellisenseContext context
    ) {
        SFMLParser parser = context.programBuildResult().metadata().parser();

        // Create code completion core
        Set<Integer> ignoredTokens = Set.of(SFMLParser.WS, SFMLParser.EOF);
        CodeCompletionCore core = new CodeCompletionCore(parser, null, ignoredTokens);

        int caretTokenIndex = context.programBuildResult().getTokenIndexAtCursorPosition(context.cursorPosition());

        CodeCompletionCore.CandidatesCollection candidates = core.collectCandidates(caretTokenIndex, null);
        List<IntellisenseAction> rtn = new ArrayList<>();
        Vocabulary vocabulary = parser.getVocabulary();
        String[] ruleNames = parser.getRuleNames();
        candidates.rules.forEach((a, b) -> {
            StringBuilder display = new StringBuilder();
            display.append("(rule) ");
            display.append(ruleNames[a]);
            b.forEach((c) -> {
                display.append(vocabulary.getSymbolicName(c));
                display.append(" ");
            });
            String finalDisplay = display.toString();
            rtn.add(new IntellisenseAction() {
                @Override
                public IntellisenseContext perform(IntellisenseContext context) {
                    return context;
                }

                @Override
                public String getDisplayText() {
                    return finalDisplay;
                }
            });
        });
        candidates.tokens.forEach((a,b) -> {
            StringBuilder display = new StringBuilder();
//            display.append("(token) ");
            display.append(vocabulary.getSymbolicName(a));
            b.forEach((c) -> {
                display.append(vocabulary.getSymbolicName(c));
                display.append(" ");
            });
            String finalDisplay = display.toString();
            rtn.add(new IntellisenseAction() {
                @Override
                public IntellisenseContext perform(IntellisenseContext context) {
                    return context;
                }

                @Override
                public String getDisplayText() {
                    return finalDisplay;
                }
            });
        });
        return rtn;
    }
}
