package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.client.gui.widget.SFMButtonBuilder;
import ca.teamdman.sfm.common.config.SFMClientProgramEditorConfig;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("NotNullFieldNotInitialized")
public class ProgramEditorConfigScreen extends Screen {
    private final SFMClientProgramEditorConfig config;
    private final ProgramEditorScreen parent;
    private final Runnable closeCallback;
    private Button lineNumbersOnButton;
    private Button lineNumbersOffButton;
    private Button intellisenseOffButton;
    private Button intellisenseBasicButton;
    private Button intellisenseAdvancedButton;

    public ProgramEditorConfigScreen(
            ProgramEditorScreen parent,
            SFMClientProgramEditorConfig config,
            Runnable closeCallback
    ) {
        super(LocalizationKeys.PROGRAM_EDITOR_CONFIG_SCREEN_TITLE.getComponent());
        this.config = config;
        this.parent = parent;
        this.closeCallback = closeCallback;
    }

    @Override
    public void onClose() {
        SFMScreenChangeHelpers.popScreen();
        closeCallback.run();
    }

    @Override
    public void render(
            @NotNull PoseStack pPoseStack,
            int pMouseX,
            int pMouseY,
            float pPartialTick
    ) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        int y = this.height / 2 - 65;
        int x = this.width / 2 - 150; // Shifted to the left for centering
        drawString(
                pPoseStack,
                font,
                LocalizationKeys.PROGRAM_EDITOR_CONFIG_LINE_NUMBERS.getComponent(),
                x,
                y,
                0xFFFFFF
        );
        drawString(
                pPoseStack,
                font,
                LocalizationKeys.PROGRAM_EDITOR_CONFIG_INTELLISENSE.getComponent(),
                x,
                y + 50,
                0xFFFFFF
        );
        drawCenteredString(
                pPoseStack,
                font,
                this.title,
                this.width / 2,
                15,
                0xFFFFFF
        ); // Ensure title is still displayed
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 100;
        int buttonHeight = 20;
        int x = this.width / 2 - (3 * buttonWidth) / 2
                - 10; // Centering the buttons
        int y = this.height / 2 - 50;
        int spacing = 50;
        int buttonSpacing = 10; // Space between buttons

        // Line Numbers Buttons
        lineNumbersOnButton =
                new SFMButtonBuilder()
                        .setPosition(x + buttonWidth + buttonSpacing, y)
                        .setSize(buttonWidth, buttonHeight)
                        .setText(CommonComponents.OPTION_ON)
                        .setOnPress(button -> setLineNumbers(true))
                        .build();
        lineNumbersOffButton =
                new SFMButtonBuilder()
                        .setPosition(x, y)
                        .setSize(buttonWidth, buttonHeight)
                        .setText(CommonComponents.OPTION_OFF)
                        .setOnPress(button -> setLineNumbers(false))
                        .build();

        this.addRenderableWidget(lineNumbersOnButton);
        this.addRenderableWidget(lineNumbersOffButton);

        // Intellisense Buttons
        intellisenseOffButton =
                new SFMButtonBuilder()
                        .setPosition(x, y + spacing)
                        .setSize(buttonWidth, buttonHeight)
                        .setText(LocalizationKeys.PROGRAM_EDITOR_CONFIG_INTELLISENSE_OFF)
                        .setOnPress(button -> setIntellisenseLevel(SFMClientProgramEditorConfig.IntellisenseLevel.OFF))
                        .build();
        intellisenseBasicButton =
                new SFMButtonBuilder()
                        .setPosition(x + buttonWidth + buttonSpacing, y + spacing)
                        .setSize(buttonWidth, buttonHeight)
                        .setText(LocalizationKeys.PROGRAM_EDITOR_CONFIG_INTELLISENSE_BASIC)
                        .setOnPress(button -> setIntellisenseLevel(SFMClientProgramEditorConfig.IntellisenseLevel.BASIC))
                        .build();
        intellisenseAdvancedButton =
                new SFMButtonBuilder()
                        .setPosition(
                                x + 2 * (buttonWidth + buttonSpacing), y + spacing
                        )
                        .setSize(buttonWidth, buttonHeight)
                        .setText(LocalizationKeys.PROGRAM_EDITOR_CONFIG_INTELLISENSE_ADVANCED)
                        .setOnPress(button -> setIntellisenseLevel(SFMClientProgramEditorConfig.IntellisenseLevel.ADVANCED))
                        .build();

        this.addRenderableWidget(intellisenseOffButton);
        this.addRenderableWidget(intellisenseBasicButton);
        this.addRenderableWidget(intellisenseAdvancedButton);

        // Done Button
        this.addRenderableWidget(
                new SFMButtonBuilder()
                        .setPosition(this.width / 2 - 100, this.height - 50)
                        .setSize(200, 20)
                        .setText(CommonComponents.GUI_DONE)
                        .setOnPress((button) -> this.onClose())
                        .build());

        updateButtonStates();
    }

    private void setLineNumbers(boolean show) {
        config.showLineNumbers.set(show);
        updateButtonStates();
    }

    private void setIntellisenseLevel(SFMClientProgramEditorConfig.IntellisenseLevel level) {
        config.intellisenseLevel.set(level);
        updateButtonStates();
        parent.onIntellisensePreferenceChanged();
    }

    private void updateButtonStates() {
        lineNumbersOnButton.active = !config.showLineNumbers.get();
        lineNumbersOffButton.active = config.showLineNumbers.get();

        intellisenseOffButton.active =
                config.intellisenseLevel.get() != SFMClientProgramEditorConfig.IntellisenseLevel.OFF;
        intellisenseBasicButton.active =
                config.intellisenseLevel.get() != SFMClientProgramEditorConfig.IntellisenseLevel.BASIC;
        intellisenseAdvancedButton.active =
                config.intellisenseLevel.get() != SFMClientProgramEditorConfig.IntellisenseLevel.ADVANCED;
    }
}
