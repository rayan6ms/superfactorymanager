package ca.teamdman.sfm.client.text_editor;

import ca.teamdman.sfm.client.screen.SFMScreenChangeHelpers;
import ca.teamdman.sfm.client.screen.text_editor.SFMTextEditScreenV2;
import net.minecraft.client.Minecraft;

public class SFMTextEditScreenV2Registration implements ISFMTextEditorRegistration {
    @Override
    public void openScreen(ISFMTextEditScreenOpenContext context) {
        SFMTextEditScreenV2 screen = new SFMTextEditScreenV2(
                context,
                SFMScreenChangeHelpers.getCurrentScreen(),
                Minecraft.getInstance().options.hideGui
        );
        Minecraft.getInstance().options.hideGui = true;
        SFMScreenChangeHelpers.setScreen(screen);
    }
}
