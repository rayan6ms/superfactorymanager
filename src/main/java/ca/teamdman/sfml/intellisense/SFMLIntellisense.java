package ca.teamdman.sfml.intellisense;

import ca.teamdman.langs.SFMLLexer;
import ca.teamdman.langs.SFMLParser;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfm.common.util.SFMEnvironmentUtils;
import ca.teamdman.sfml.ext_antlr4c3.CodeCompletionCore;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class SFMLIntellisense {
    public static List<IntellisenseAction> getSuggestions(
            IntellisenseContext context
    ) {
        SFMLParser parser = context.programBuildResult().metadata().parser();

        // Create code completion core
        Set<Integer> preferredRules = new HashSet<>(List.of(
                SFMLParser.RULE_resourceId,
                SFMLParser.RULE_label
//                SFMLParser.RULE_identifier
        ));
//        preferredRules.clear();
        Set<Integer> ignoredTokens = Set.of(SFMLParser.WS, SFMLParser.EOF);
        CodeCompletionCore core = new CodeCompletionCore(parser, preferredRules, ignoredTokens);

        @Nullable Token caretToken = context.programBuildResult().getTokenAtCursorPosition(context.cursorPosition());
        if (caretToken == null) return new ArrayList<>();
        int caretTokenIndex = caretToken.getTokenIndex();

        CodeCompletionCore.CandidatesCollection candidates = core.collectCandidates(caretTokenIndex, null);
        List<IntellisenseAction> rtn = new ArrayList<>();
        Vocabulary vocabulary = parser.getVocabulary();
//        if (!candidates.rules.isEmpty()) {
//            SFM.LOGGER.warn("Expected no rule intellisense results, found {}.", candidates.rules.size());
//        }

//        System.out.printf("(RULES) %d\n", candidates.rules.size());
//        candidates.rules.forEach((a, b) -> {
//            StringBuilder display = new StringBuilder();
//            display.append("(rule) ");
//            display.append(ruleNames[a]);
//            b.forEach((c) -> {
//                display.append(" ");
//                display.append(ruleNames[c]);
//            });
//            String finalDisplay = display.toString();
//            System.out.printf("(RULE) %s\n", finalDisplay);
//        });

        candidates.rules.forEach((head, tail) -> {
            switch (head) {
                case SFMLParser.RULE_resourceId -> {
                    if (SFMEnvironmentUtils.isGameLoaded()) {
                        for (ResourceType<?, ?, ?> resourceType : SFMResourceTypes.DEFERRED_TYPES.get().getValues()) {
                            gatherIntellisenseActions(context, resourceType, rtn::add);
                        }
                    }
                }
                case SFMLParser.RULE_label -> {
                    rtn.add(new SuggestedTokensIntellisenseAction(
                            SFMLLexer.STRING,
                            new ArrayList<>(),
                            vocabulary
                    ));
                    context.labelPositionHolder().labels().forEach(
                            (key, value) ->
                                    rtn.add(
                                            new SuggestedLabelIntellisenseAction(
                                                    key,
                                                    value.size()
                                            )
                                    )
                    );
                }
            }
        });

        for (Map.Entry<Integer, List<Integer>> entry : candidates.tokens.entrySet()) {
            rtn.add(new SuggestedTokensIntellisenseAction(
                    entry.getKey(),
                    entry.getValue(),
                    vocabulary
            ));
        }

        return rtn;
    }

    private static <STACK, ITEM, CAP> void gatherIntellisenseActions(
            IntellisenseContext context,
            ResourceType<STACK, ITEM, CAP> resourceType,
            Consumer<IntellisenseAction> results
    ) {
        String word = context.createMutableProgramString().getWord();
        for (ITEM item : resourceType.getItems()) {
            var suggestion = new SuggestedResourceIntellisenseAction<>(
                    resourceType,
                    item
            );
            if (suggestion.getComponent().getString().contains(word)) {
                results.accept(suggestion);
            }
        }
    }
}
