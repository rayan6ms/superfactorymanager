package ca.teamdman.sfm.client.text_editor;

import net.minecraft.client.gui.screens.Screen;

public interface ISFMTextEditorRegistration {

    Screen createScreen(ISFMTextEditScreenOpenContext context);
}
