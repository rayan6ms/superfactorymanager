package ca.teamdman.sfm.client.text_editor;

import ca.teamdman.sfm.client.screen.text_editor.SFMTextEditScreenV1;
import net.minecraft.client.gui.screens.Screen;

public class SFMTextEditScreenV1Registration implements ISFMTextEditorRegistration {
    @Override
    public Screen createScreen(ISFMTextEditScreenOpenContext context) {
        return new SFMTextEditScreenV1(context);
    }
}
