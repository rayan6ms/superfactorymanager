package ca.teamdman.sfm.client.text_editor;

import net.minecraft.client.gui.screens.Screen;

public interface ISFMTextEditorRegistration {

    /**
     * Create, but do not display, an editor screen for the given context.
     */
    Screen createScreen(ISFMTextEditScreenOpenContext context);
}
