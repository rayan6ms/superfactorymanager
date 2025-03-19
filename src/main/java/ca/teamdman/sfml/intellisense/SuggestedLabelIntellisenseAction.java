package ca.teamdman.sfml.intellisense;

import ca.teamdman.sfml.manipulation.ManipulationResult;
import net.minecraft.network.chat.Component;

public record SuggestedLabelIntellisenseAction(
        String label,
        int numBlocks
) implements IntellisenseAction {
    @Override
    public Component getComponent() {
        return Component.literal("%s (%d)".formatted(label, numBlocks));
    }

    @Override
    public ManipulationResult perform(IntellisenseContext context) {
        String programString = context.programBuildResult().metadata().programString();
        int cursor = context.cursorPosition();
        int selectionCursor = context.selectionCursorPosition();
        MutableProgramString programStringMut = new MutableProgramString(programString, cursor, selectionCursor);
        programStringMut.replaceWordAndMoveCursorsToEnd("%s ".formatted(label));
        return new ManipulationResult(
                programStringMut.getContent(),
                programStringMut.getCursorPosition(),
                programStringMut.getSelectionCursorPosition()
        );
    }

}
