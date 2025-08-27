package ca.teamdman.sfm.client.text_editor;

import ca.teamdman.sfm.client.screen.text_editor.SFMTextEditScreenV2;
import net.minecraft.client.gui.screens.Screen;

public class SFMTextEditScreenV2Registration implements ISFMTextEditorRegistration {
    @Override
    public Screen createScreen(ISFMTextEditScreenOpenContext context) {
        return new SFMTextEditScreenV2(context);
    }
}
