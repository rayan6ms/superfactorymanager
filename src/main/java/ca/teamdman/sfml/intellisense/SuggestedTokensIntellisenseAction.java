package ca.teamdman.sfml.intellisense;

import ca.teamdman.langs.SFMLLexer;
import ca.teamdman.sfml.manipulation.ManipulationResult;
import net.minecraft.network.chat.Component;
import org.antlr.v4.runtime.Vocabulary;

import java.util.List;

public record SuggestedTokensIntellisenseAction(
        Integer nextTokenType,
        List<Integer> followingTokenTypes,
        Vocabulary vocabulary
) implements IntellisenseAction {
    @Override
    public Component getComponent() {
        return Component.literal(getDisplay());
    }


    /*
    - If suggesting NAME nextTokenType and NAME already present, jump cursor to inside of existing string nextTokenType followingTokenTypes existing NAME nextTokenType
    - If suggesting NAME nextTokenType and NAME already present but STRING followingTokenTypes name missing, insert string and place cursor inside
     */
    @Override
    public ManipulationResult perform(IntellisenseContext context) {
        String programString = context.programBuildResult().metadata().programString();
        int cursor = context.cursorPosition();
        int selectionCursor = context.selectionCursorPosition();
        MutableProgramString programStringMut = new MutableProgramString(programString, cursor, selectionCursor);
        String word = programStringMut.getWord();
        int cursorInWord = programStringMut.cursorInWord();
        switch (nextTokenType) {
            case SFMLLexer.NAME -> {
                programStringMut.replaceWordAndMoveCursorsToEnd(vocabulary.getSymbolicName(nextTokenType) + " \"\"");
                programStringMut.offsetCursors(-1);
            }
            case SFMLLexer.STRING -> {
                if (word.equals("\"\"")) {
                    // move the cursor to the right of the last double quote
                    programStringMut.offsetCursors(1);
                } else if (word.isBlank()) {
                    // insert double quote pair
                    programStringMut.replaceWordAndMoveCursorsToEnd("\"\"");
                    programStringMut.offsetCursors(-1);
                } else if (word.contains("\"")) {
                    int offset = word.length() - cursorInWord;
                    if (offset > 0) {
                        // move to the end of the word
                        programStringMut.offsetCursors(offset);
                    } else {
                        // insert newline
                        programStringMut.insertTextWithoutMovingCursors("\n");
                        programStringMut.offsetCursors(1);
                    }
                } else {
                    // insert a closing quote
                    programStringMut.insertTextWithoutMovingCursors("\"");
                    programStringMut.offsetCursors(1);
                }
            }
            default -> programStringMut.replaceWordAndMoveCursorsToEnd(getDisplay() + " ");
        }
        return new ManipulationResult(
                programStringMut.getContent(),
                programStringMut.getCursorPosition(),
                programStringMut.getSelectionCursorPosition()
        );
    }

    private String getDisplay() {
        StringBuilder display = new StringBuilder();
        display.append(vocabulary.getSymbolicName(nextTokenType));
        for (Integer type : followingTokenTypes) {
            display.append(vocabulary.getSymbolicName(type));
            display.append(" ");
        }
        return display.toString();
    }
}
