package ca.teamdman.sfml.intellisense;

import ca.teamdman.langs.SFMLLexer;
import ca.teamdman.langs.SFMLParser;
import ca.teamdman.sfml.ext_antlr4c3.CodeCompletionCore;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Vocabulary;

import java.util.ArrayList;
import java.util.List;

public class SFMLIntellisense {
    public static List<IntellisenseAction> getSuggestions(
            IntellisenseContext context
    ) {
        SFMLLexer lexer = new SFMLLexer(CharStreams.fromString(context.program()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SFMLParser parser = new SFMLParser(tokens);
//        parser.getATN()
        CodeCompletionCore core = new CodeCompletionCore(parser, null, null);
        CodeCompletionCore.CandidatesCollection candidates = core.collectCandidates(0, parser.program());
        List<IntellisenseAction> rtn = new ArrayList<>();
        Vocabulary vocabulary = parser.getVocabulary();
        String[] ruleNames = parser.getRuleNames();
        candidates.rules.forEach((a, b) -> {
            StringBuilder display = new StringBuilder();
            display.append(ruleNames[a]);
            display.append(" <- ");
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
            display.append(vocabulary.getSymbolicName(a));
            display.append(" <- ");
            display.append(b);
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
