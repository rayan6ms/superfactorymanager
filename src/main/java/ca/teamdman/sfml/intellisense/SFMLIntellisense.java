package ca.teamdman.sfml.intellisense;

import ca.teamdman.langs.SFMLParser;
import ca.teamdman.sfm.SFM;
import ca.teamdman.sfml.ext_antlr4c3.CodeCompletionCore;
import org.antlr.v4.runtime.Vocabulary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        if (!candidates.rules.isEmpty()) {
            SFM.LOGGER.warn("Expected no rule intellisense results, found {}.", candidates.rules.size());
        }

        for (Map.Entry<Integer, List<Integer>> entry : candidates.tokens.entrySet()) {
            rtn.add(new SuggestedTokensIntellisenseAction(
                    entry.getKey(),
                    entry.getValue(),
                    vocabulary
            ));
        }
        return rtn;
    }
}
