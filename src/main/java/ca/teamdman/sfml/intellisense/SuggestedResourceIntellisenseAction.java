package ca.teamdman.sfml.intellisense;

import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import ca.teamdman.sfml.manipulation.ManipulationResult;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public record SuggestedResourceIntellisenseAction<STACK, ITEM, CAP>(
        ResourceType<STACK, ITEM, CAP> resourceType,
        ITEM item,
        Component display
) implements IntellisenseAction {
    public SuggestedResourceIntellisenseAction(
            ResourceType<STACK, ITEM, CAP> resourceType,
            ITEM item
    ) {
        this(
                resourceType,
                item,
                Component.literal(
                        new ResourceIdentifier<STACK, ITEM, CAP>(
                                Objects.requireNonNull(SFMResourceTypes.DEFERRED_TYPES.get().getKey(resourceType)),
                                resourceType.getRegistryKeyForItem(item)
                        ).toStringCondensed()
                )
        );
    }

    @Override
    public Component getComponent() {
        return display();
    }

    @Override
    public ManipulationResult perform(IntellisenseContext context) {
        String programString = context.programBuildResult().metadata().programString();
        int cursor = context.cursorPosition();
        int selectionCursor = context.selectionCursorPosition();
        MutableProgramString programStringMut = new MutableProgramString(programString, cursor, selectionCursor);
        programStringMut.replaceWordAndMoveCursorsToEnd("%s ".formatted(display().getString()));
        return new ManipulationResult(
                programStringMut.getContent(),
                programStringMut.getCursorPosition(),
                programStringMut.getSelectionCursorPosition()
        );
    }
}
