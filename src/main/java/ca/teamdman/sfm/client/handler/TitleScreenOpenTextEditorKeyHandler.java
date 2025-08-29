package ca.teamdman.sfm.client.handler;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.registry.SFMKeyMappings;
import ca.teamdman.sfm.client.screen.SFMScreenChangeHelpers;
import ca.teamdman.sfm.client.text_editor.ISFMTextEditScreenOpenContext;
import ca.teamdman.sfm.client.text_editor.SFMTextEditScreenTitleScreenOpenContext;
import ca.teamdman.sfm.common.label.LabelPositionHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TitleScreenOpenTextEditorKeyHandler {
    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        if (
                SFMKeyMappings.TITLE_SCREEN_OPEN_TEXT_EDITOR_KEY.get().matches(event.getKey(), event.getScanCode())
                && Minecraft.getInstance().screen instanceof TitleScreen titleScreen
        ) {
            String initialContent = """
                    Hi there!
                    """.stripTrailing().stripIndent();
            ISFMTextEditScreenOpenContext openContext = new SFMTextEditScreenTitleScreenOpenContext(
                    initialContent,
                    LabelPositionHolder.empty(),
                    (x) -> {
                    },
                    titleScreen
            );
            SFMScreenChangeHelpers.showProgramEditScreen(openContext);
        }
    }
}
