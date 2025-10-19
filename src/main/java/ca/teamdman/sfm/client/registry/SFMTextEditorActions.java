package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.text_editor.action.*;
import ca.teamdman.sfm.common.registry.SFMDeferredRegister;
import ca.teamdman.sfm.common.registry.SFMRegistryObject;
import ca.teamdman.sfm.common.registry.SFMRegistryWrapper;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SFMTextEditorActions {
    public static final ResourceKey<Registry<ITextEditAction>> REGISTRY_ID = SFMResourceLocation.createSFMRegistryKey("text_editor_action");
    private static final SFMDeferredRegister<ITextEditAction> REGISTERER = SFMDeferredRegister.createForCustomClientRegistry(
            REGISTRY_ID,
            SFM.MOD_ID
    );

    public static final SFMRegistryObject<SelectAllTextAction> SELECT_ALL_TEXT = REGISTERER.register(
            "select_all_text",
            SelectAllTextAction::new
    );

    public static final SFMRegistryObject<DeleteSelectionOrCharacterToTheLeftForEachCursorAction> DELETE_SELECTION_OR_CHARACTER_TO_THE_LEFT_FOR_EACH_CURSOR = REGISTERER.register(
            "delete_selection_or_character_to_the_left_for_each_cursor",
            DeleteSelectionOrCharacterToTheLeftForEachCursorAction::new
    );
    public static final SFMRegistryObject<DeleteSelectionOrCharacterToTheRightForEachCursorAction> DELETE_SELECTION_OR_CHARACTER_TO_THE_RIGHT_FOR_EACH_CURSOR = REGISTERER.register(
            "delete_selection_or_character_to_the_right_for_each_cursor",
            DeleteSelectionOrCharacterToTheRightForEachCursorAction::new
    );

    public static final SFMRegistryObject<MoveCursorHeadsRightOneCharacter> MOVE_CURSOR_HEADS_RIGHT_ONE_CHARACTER = REGISTERER.register(
            "move_cursor_heads_right_one_character",
            MoveCursorHeadsRightOneCharacter::new
    );
    public static final SFMRegistryObject<MoveCursorHeadsLeftOneCharacter> MOVE_CURSOR_HEADS_LEFT_ONE_CHARACTER = REGISTERER.register(
            "move_cursor_heads_left_one_character",
            MoveCursorHeadsLeftOneCharacter::new
    );
    public static final SFMRegistryObject<MoveCursorsRightOneCharacter> MOVE_CURSORS_RIGHT_ONE_CHARACTER = REGISTERER.register(
            "move_cursors_right_one_character",
            MoveCursorsRightOneCharacter::new
    );
    public static final SFMRegistryObject<MoveCursorsLeftOneCharacter> MOVE_CURSORS_LEFT_ONE_CHARACTER = REGISTERER.register(
            "move_cursors_left_one_character",
            MoveCursorsLeftOneCharacter::new
    );
    public static final SFMRegistryObject<MoveCursorsUpOneLine> MOVE_CURSORS_UP_ONE_LINE = REGISTERER.register(
            "move_cursors_up_one_line",
            MoveCursorsUpOneLine::new
    );
    public static final SFMRegistryObject<MoveCursorsDownOneLine> MOVE_CURSORS_DOWN_ONE_LINE = REGISTERER.register(
            "move_cursors_down_one_line",
            MoveCursorsDownOneLine::new
    );
    public static final SFMRegistryObject<SwapCursorHeadsAndTailsAction> SWAP_CURSOR_HEADS_AND_TAILS = REGISTERER.register(
            "swap_cursor_heads_and_tails",
            SwapCursorHeadsAndTailsAction::new
    );

    public static Stream<ITextEditAction> getTextEditActions() {
        return registry().getValues().stream().sorted((a, b) -> Float.compare(a.priority(), b.priority()));
    }

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    public static SFMRegistryWrapper<ITextEditAction> registry() {
        return REGISTERER.registry();
    }
}
