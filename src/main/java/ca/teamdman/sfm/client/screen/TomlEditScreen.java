package ca.teamdman.sfm.client.screen;

import ca.teamdman.sfm.client.screen.text_editor.SFMTextEditScreenV1;
import ca.teamdman.sfm.common.label.LabelPositionHolder;
import ca.teamdman.sfm.common.text_editor.SFMTextEditScreenDiskOpenContext;

public class TomlEditScreen extends SFMTextEditScreenV1 {
    public TomlEditScreen(
            TomlEditScreenOpenContext openContext
    ) {
        super(new SFMTextEditScreenDiskOpenContext(
                openContext.textContents(),
                LabelPositionHolder.empty(),
                openContext.saveCallback()
        ));
    }
}
