package ca.teamdman.sfm.client.text_editor;

import ca.teamdman.sfm.client.screen.SFMScreenChangeHelpers;
import ca.teamdman.sfm.client.screen.text_editor.SFMTextEditScreenV1;

public class SFMTextEditScreenV1Registration implements ISFMTextEditorRegistration {
    @Override
    public void openScreen(ISFMTextEditScreenOpenContext context) {
        SFMTextEditScreenV1 screen = new SFMTextEditScreenV1(context);
        SFMScreenChangeHelpers.setOrPushScreen(screen); // must happen before scrollToTop
        screen.scrollToTop();
    }
}
