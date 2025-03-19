package ca.teamdman.sfm.client.gui.screen;

import net.minecraft.client.gui.screens.ConfirmScreen;

public class ExampleEditScreen extends ProgramEditScreen {
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
            assert this.minecraft != null;
            ConfirmScreen saveConfirmScreen = getSaveConfirmScreen(super::saveAndClose);
            this.minecraft.pushGuiLayer(saveConfirmScreen);
            saveConfirmScreen.setDelay(20);
        }
    }

    @Override
    public void onClose() {
        // The user has requested to close the screen
        // If the content has changed, ask to save before discarding
        if (!openContext.equalsAnyTemplate(textarea.getValue())) {
            assert this.minecraft != null;
            ConfirmScreen exitWithoutSavingConfirmScreen = getExitWithoutSavingConfirmScreen();
            this.minecraft.pushGuiLayer(exitWithoutSavingConfirmScreen);
            exitWithoutSavingConfirmScreen.setDelay(20);
        } else {
            super.onClose();
        }
    }
}
