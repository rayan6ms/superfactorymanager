package ca.teamdman.sfm.client.screen;

import ca.teamdman.sfm.client.widget.SFMButtonBuilder;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.diagnostics.SFMDiagnostics;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.logging.TranslatableLogEvent;
import ca.teamdman.sfm.common.net.ServerboundManagerClearLogsPacket;
import ca.teamdman.sfm.common.net.ServerboundManagerLogDesireUpdatePacket;
import ca.teamdman.sfm.common.net.ServerboundManagerSetLogLevelPacket;
import ca.teamdman.sfm.common.registry.SFMPackets;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.time.MutableInstant;

import java.util.HashMap;
import java.util.Map;

import static ca.teamdman.sfm.common.localization.LocalizationKeys.PROGRAM_EDIT_SCREEN_DONE_BUTTON_TOOLTIP;

// todo: checkbox for auto-scrolling
public class LogsScreen extends Screen {
    private final ManagerContainerMenu MENU;

    @SuppressWarnings("NotNullFieldNotInitialized")
    private LogsScreenMultiLineEditBox textarea;

    private int lastSize = 0;

    private Map<Level, Button> levelButtons = new HashMap<>();

    private String lastKnownLogLevel;


    public LogsScreen(ManagerContainerMenu menu) {

        super(LocalizationKeys.LOGS_SCREEN_TITLE.getComponent());
        this.MENU = menu;
        this.lastKnownLogLevel = MENU.logLevel;
    }

    @Override
    public boolean isPauseScreen() {

        return false;
    }

    public boolean isReadOnly() {

        LocalPlayer player = Minecraft.getInstance().player;
        return player == null || player.isSpectator();
    }

    public void onLogLevelChange() {
        // disable buttons that equal the current level
        for (var entry : levelButtons.entrySet()) {
            var level = entry.getKey();
            var button = entry.getValue();
            button.active = !MENU.logLevel.equals(level.name());
        }
        lastKnownLogLevel = MENU.logLevel;
    }

    @Override
    public void onClose() {

        SFMPackets.sendToServer(new ServerboundManagerLogDesireUpdatePacket(
                MENU.containerId,
                MENU.MANAGER_POSITION,
                false
        ));
        super.onClose();
    }

    public void scrollToBottom() {

        textarea.scrollToBottom();
    }

    @Override
    public void resize(
            Minecraft mc,
            int x,
            int y
    ) {

        var prev = this.textarea.getValue();
        init(mc, x, y);
        super.resize(mc, x, y);
        this.textarea.setValue(prev);
    }

    @Override
    public void render(
            GuiGraphics pGuiGraphics,
            int pMouseX,
            int pMouseY,
            float pPartialTick
    ) {

        PoseStack poseStack = pGuiGraphics.pose();

        // render background
        this.renderTransparentBackground(pGuiGraphics);

        // render widgets
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        // render tooltips
        SFMWidgetUtils.hideTooltipsWhenNotFocused(this, this.renderables);
        SFMWidgetUtils.renderChildTooltips(poseStack, pMouseX, pMouseY, this.renderables);

        if (!MENU.logLevel.equals(lastKnownLogLevel)) {
            onLogLevelChange();
        }
    }

    public boolean shouldRebuildText() {

        return MENU.logs.size() != lastSize;
    }

    public void rebuildText() {

        // If no logs present, add reminder text as a log message
        if (MENU.logs.isEmpty() && MENU.logLevel.equals(Level.OFF.name())) {
            MutableInstant instant = new MutableInstant();
            instant.initFromEpochMilli(System.currentTimeMillis(), 0);
            MENU.logs.add(new TranslatableLogEvent(
                    Level.INFO,
                    instant,
                    LocalizationKeys.LOGS_GUI_NO_CONTENT.get()
            ));
        }


        this.textarea.styledTextContentLines = LogsTextStylingHelper.getStyledLogs(MENU.logs);

        // update the text area content so select and copy works
        StringBuilder sb = new StringBuilder();
        for (var line : this.textarea.styledTextContentLines) {
            sb.append(line.getString()).append("\n");
        }
        textarea.setValue(sb.toString());

        // update rendering widget
        textarea.textRenderWidget.setStyledTextContentLines(textarea.styledTextContentLines);
        textarea.textRenderWidget.setTextContent(textarea.getValue());

        lastSize = MENU.logs.size();
    }

    @Override
    protected void init() {

        super.init();
        assert this.minecraft != null;
        this.textarea = this.addRenderableWidget(new LogsScreenMultiLineEditBox(
                this, LogsScreen.this.font,
                LogsScreen.this.width / 2 - 200,
                LogsScreen.this.height / 2 - 90,
                400,
                180,
                Component.literal(""),
                Component.literal("")
        ));

        rebuildText();

        this.setInitialFocus(textarea);


        var buttons = isReadOnly() ? new Level[]{} : new Level[]{
                Level.OFF,
                Level.TRACE,
                Level.DEBUG,
                Level.INFO,
                Level.WARN,
                Level.ERROR
        };
        int buttonWidth = 60;
        int buttonHeight = 20;
        int spacing = 5;
        int startX = (this.width - (buttonWidth * buttons.length + spacing * 4)) / 2;
        int startY = this.height / 2 - 115;
        int buttonIndex = 0;

        this.levelButtons = new HashMap<>();
        for (var level : buttons) {
            Button levelButton = new SFMButtonBuilder()
                    .setSize(buttonWidth, buttonHeight)
                    .setPosition(
                            startX + (buttonWidth + spacing) * buttonIndex,
                            startY
                    )
                    .setText(Component.literal(level.name()))
                    .setOnPress(button -> {
                        String logLevel = level.name();
                        SFMPackets.sendToServer(new ServerboundManagerSetLogLevelPacket(
                                MENU.containerId,
                                MENU.MANAGER_POSITION,
                                logLevel
                        ));
                        MENU.logLevel = logLevel;
                        onLogLevelChange();
                    })
                    .build();
            levelButtons.put(level, levelButton);
            this.addRenderableWidget(levelButton);
            buttonIndex++;
        }
        onLogLevelChange();


        this.addRenderableWidget(
                new SFMButtonBuilder()
                        .setPosition(this.width / 2 - 200, this.height / 2 - 100 + 195)
                        .setSize(80, 20)
                        .setText(LocalizationKeys.LOGS_GUI_COPY_LOGS_BUTTON)
                        .setOnPress(this::onCopyLogsClicked)
                        .setTooltip(this, font, LocalizationKeys.LOGS_GUI_COPY_LOGS_BUTTON_TOOLTIP)
                        .build()
        );
        this.addRenderableWidget(
                new SFMButtonBuilder()
                        .setPosition(this.width / 2 - 2 - 100, this.height / 2 - 100 + 195)
                        .setSize(200, 20)
                        .setText(CommonComponents.GUI_DONE)
                        .setOnPress((p_97691_) -> this.onClose())
                        .setTooltip(this, font, PROGRAM_EDIT_SCREEN_DONE_BUTTON_TOOLTIP)
                        .build()
        );
        if (!isReadOnly()) {
            this.addRenderableWidget(
                    new SFMButtonBuilder()
                            .setPosition(this.width / 2 - 2 + 115, this.height / 2 - 100 + 195)
                            .setSize(80, 20)
                            .setText(LocalizationKeys.LOGS_GUI_CLEAR_LOGS_BUTTON)
                            .setOnPress((button) -> {
                                SFMPackets.sendToServer(new ServerboundManagerClearLogsPacket(
                                        MENU.containerId,
                                        MENU.MANAGER_POSITION
                                ));
                                MENU.logs.clear();
                            })
                            .build()
            );
        }
    }

    private void onCopyLogsClicked(Button button) {

        StringBuilder clip = new StringBuilder();
        clip.append(SFMDiagnostics.getDiagnosticsSummary(
                MENU.getDisk()
        ));
        clip.append("\n-- LOGS --\n");
        if (hasShiftDown()) {
            for (TranslatableLogEvent log : MENU.logs) {
                clip.append(log.level().name()).append(" ");
                clip.append(log.instant().toString()).append(" ");
                clip.append(log.contents().getKey());
                for (Object arg : log.contents().getArgs()) {
                    clip.append(" ").append(arg);
                }
                clip.append("\n");
            }
        } else {
            for (MutableComponent line : textarea.styledTextContentLines) {
                clip.append(line.getString()).append("\n");
            }
        }
        Minecraft.getInstance().keyboardHandler.setClipboard(clip.toString());
    }

}
