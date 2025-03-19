package ca.teamdman.sfm.client.gui.screen;

import net.minecraft.client.gui.screens.ConfirmScreen;

public class ExampleEditScreen extends ProgramEditorScreen {
    private final ExampleEditScreenOpenContext openContext;

    public ExampleEditScreen(
            ExampleEditScreenOpenContext openContext
    ) {
        super(new ProgramEditScreenOpenContext(
                openContext.exampleProgrmaString(),
                openContext.labelPositionHolder(),
                openContext.saveCallback()
        ));
        this.openContext = openContext;
    }

    @Override
    public void saveAndClose() {
        if (openContext.isSafeToOverwriteDisk()) {
            super.saveAndClose();
        } else {
            // The disk contains non-template code, ask before overwriting
            ConfirmScreen saveConfirmScreen = getSaveConfirmScreen(super::saveAndClose);
            SFMScreenChangeHelpers.setOrPushScreen(saveConfirmScreen);
            saveConfirmScreen.setDelay(20);
        }
    }

    @Override
    public void onClose() {
        // The user has requested to close the screen
        // If the content has changed, ask to save before discarding
        if (!openContext.equalsAnyTemplate(textarea.getValue())) {
            ConfirmScreen exitWithoutSavingConfirmScreen = getExitWithoutSavingConfirmScreen();
            SFMScreenChangeHelpers.setOrPushScreen(exitWithoutSavingConfirmScreen);
            exitWithoutSavingConfirmScreen.setDelay(20);
        } else {
            super.onClose();
        }
    }
}
