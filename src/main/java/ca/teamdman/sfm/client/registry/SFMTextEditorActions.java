package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.text_editor.action.*;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.Registry;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SFMTextEditorActions {
    public static final ResourceLocation REGISTRY_ID = SFMResourceLocation.fromSFMPath("text_editor_action");
    private static final DeferredRegister<ITextEditAction> REGISTERER = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    private static final Registry<ITextEditAction> REGISTRY = REGISTERER.makeRegistry(
            registryBuilder -> {});

    public static final Supplier<ITextEditAction> SELECT_ALL_TEXT = REGISTERER.register(
            "select_all_text",
            SelectAllTextAction::new
    );

    public static final Supplier<ITextEditAction> DELETE_SELECTION_OR_CHARACTER_TO_THE_LEFT_FOR_EACH_CURSOR = REGISTERER.register(
            "delete_selection_or_character_to_the_left_for_each_cursor",
            DeleteSelectionOrCharacterToTheLeftForEachCursorAction::new
    );
    public static final Supplier<ITextEditAction> DELETE_SELECTION_OR_CHARACTER_TO_THE_RIGHT_FOR_EACH_CURSOR = REGISTERER.register(
            "delete_selection_or_character_to_the_right_for_each_cursor",
            DeleteSelectionOrCharacterToTheRightForEachCursorAction::new
    );

    public static final Supplier<ITextEditAction> MOVE_CURSOR_HEADS_RIGHT_ONE_CHARACTER = REGISTERER.register(
            "move_cursor_heads_right_one_character",
            MoveCursorHeadsRightOneCharacter::new
    );
    public static final Supplier<ITextEditAction> MOVE_CURSOR_HEADS_LEFT_ONE_CHARACTER = REGISTERER.register(
            "move_cursor_heads_left_one_character",
            MoveCursorHeadsLeftOneCharacter::new
    );
    public static final Supplier<ITextEditAction> MOVE_CURSORS_RIGHT_ONE_CHARACTER = REGISTERER.register(
            "move_cursors_right_one_character",
            MoveCursorsRightOneCharacter::new
    );
    public static final Supplier<ITextEditAction> MOVE_CURSORS_LEFT_ONE_CHARACTER = REGISTERER.register(
            "move_cursors_left_one_character",
            MoveCursorsLeftOneCharacter::new
    );
    public static final Supplier<ITextEditAction> MOVE_CURSORS_UP_ONE_LINE = REGISTERER.register(
            "move_cursors_up_one_line",
            MoveCursorsUpOneLine::new
    );
    public static final Supplier<ITextEditAction> MOVE_CURSORS_DOWN_ONE_LINE = REGISTERER.register(
            "move_cursors_down_one_line",
            MoveCursorsDownOneLine::new
    );
    public static final Supplier<ITextEditAction> SWAP_CURSOR_HEADS_AND_TAILS = REGISTERER.register(
            "swap_cursor_heads_and_tails",
            SwapCursorHeadsAndTailsAction::new
    );

    public static Stream<? extends ITextEditAction> getTextEditActions() {
        return REGISTERER.getEntries().stream().map(Supplier::get).sorted((a, b) -> Float.compare(a.priority(), b.priority()));
    }

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    @MCVersionDependentBehaviour
    public static Registry<ITextEditAction> registry() {
        return REGISTRY;
    }
}
