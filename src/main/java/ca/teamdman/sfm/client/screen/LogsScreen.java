package ca.teamdman.sfm.client.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientTranslationHelpers;
import ca.teamdman.sfm.client.ProgramSyntaxHighlightingHelper;
import ca.teamdman.sfm.client.screen.text_editor.SFMTextEditorUtils;
import ca.teamdman.sfm.client.widget.SFMButtonBuilder;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.diagnostics.SFMDiagnostics;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.logging.TranslatableLogEvent;
import ca.teamdman.sfm.common.net.ServerboundManagerClearLogsPacket;
import ca.teamdman.sfm.common.net.ServerboundManagerLogDesireUpdatePacket;
import ca.teamdman.sfm.common.net.ServerboundManagerSetLogLevelPacket;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.SFMComponentUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField.StringView;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.time.MutableInstant;

import java.util.*;

import static ca.teamdman.sfm.common.localization.LocalizationKeys.PROGRAM_EDIT_SCREEN_DONE_BUTTON_TOOLTIP;

// todo: checkbox for auto-scrolling
public class LogsScreen extends Screen {
    private final ManagerContainerMenu MENU;

    @SuppressWarnings("NotNullFieldNotInitialized")
    private MyMultiLineEditBox textarea;

    private List<MutableComponent> content = Collections.emptyList();

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
            PoseStack poseStack,
            int mx,
            int my,
            float partialTicks
    ) {

        this.renderBackground(poseStack);
        super.render(poseStack, mx, my, partialTicks);
        if (!MENU.logLevel.equals(lastKnownLogLevel)) {
            onLogLevelChange();
        }
    }

    private boolean shouldRebuildText() {

        return MENU.logs.size() != lastSize;
    }

    private void rebuildText() {

        List<MutableComponent> processedLogs = new ArrayList<>();
        var toProcess = MENU.logs;
        if (toProcess.isEmpty() && MENU.logLevel.equals(Level.OFF.name())) {
            MutableInstant instant = new MutableInstant();
            instant.initFromEpochMilli(System.currentTimeMillis(), 0);
            toProcess.add(new TranslatableLogEvent(
                    Level.INFO,
                    instant,
                    LocalizationKeys.LOGS_GUI_NO_CONTENT.get()
            ));
        }
        for (TranslatableLogEvent log : toProcess) {
            int seconds = (int) (System.currentTimeMillis() - log.instant().getEpochMillisecond()) / 1000;
            int minutes = seconds / 60;
            seconds = seconds % 60;
            var ago = Component.literal(minutes + "m" + seconds + "s ago").withStyle(ChatFormatting.GRAY);

            var level = Component.literal(" [" + log.level() + "] ");
            if (log.level() == Level.ERROR) {
                level = level.withStyle(ChatFormatting.RED);
            } else if (log.level() == Level.WARN) {
                level = level.withStyle(ChatFormatting.YELLOW);
            } else if (log.level() == Level.INFO) {
                level = level.withStyle(ChatFormatting.GREEN);
            } else if (log.level() == Level.DEBUG) {
                level = level.withStyle(ChatFormatting.AQUA);
            } else if (log.level() == Level.TRACE) {
                level = level.withStyle(ChatFormatting.DARK_GRAY);
            }

            String[] lines = ClientTranslationHelpers.resolveTranslation(log.contents()).split("\n", -1);

            StringBuilder codeBlock = new StringBuilder();
            boolean insideCodeBlock = false;

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                MutableComponent lineComponent;

                if (line.equals("```")) {
                    if (insideCodeBlock) {
                        // output processed code
                        var codeLines = ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(
                                codeBlock.toString(),
                                false
                        );
                        processedLogs.addAll(codeLines);
                        codeBlock = new StringBuilder();
                    } else {
                        // begin tracking code
                        insideCodeBlock = true;
                    }
                } else if (insideCodeBlock) {
                    codeBlock.append(line).append("\n");
                } else {
                    lineComponent = Component.literal(line).withStyle(ChatFormatting.WHITE);
                    if (i == 0) {
                        lineComponent = ago
                                .append(level)
                                .append(lineComponent);
                    }
                    processedLogs.add(lineComponent);
                }
            }
        }
        this.content = processedLogs;


        // update textarea with plain string contents so select and copy works
        StringBuilder sb = new StringBuilder();
        for (var line : this.content) {
            sb.append(line.getString()).append("\n");
        }
        textarea.setValue(sb.toString());
        lastSize = MENU.logs.size();
    }

    @Override
    protected void init() {

        super.init();
        assert this.minecraft != null;
        this.textarea = this.addRenderableWidget(new MyMultiLineEditBox());

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
            for (MutableComponent line : content) {
                clip.append(line.getString()).append("\n");
            }
        }
        Minecraft.getInstance().keyboardHandler.setClipboard(clip.toString());
    }

    // TODO: enable scrolling without focus
    private class MyMultiLineEditBox extends MultiLineEditBox {
        private final List<Integer> displayedLineStartOffsets = new ArrayList<>();

        private String lastPlainText = "";

        private boolean scrollbarDragActive;

        /// Used to debounce scrolling when click-dragging to select text.
        private boolean scrollingEnabled = true;

        public MyMultiLineEditBox() {

            super(
                    LogsScreen.this.font,
                    LogsScreen.this.width / 2 - 200,
                    LogsScreen.this.height / 2 - 90,
                    400,
                    180,
                    Component.literal(""),
                    Component.literal("")
            );
            rebuildDisplayCache();
        }

        public void scrollToBottom() {

            this.setScrollAmount(this.getMaxScrollAmount());
        }

        @Override
        public void setValue(String value) {

            super.setValue(value);
            rebuildDisplayCache();
        }

        @Override
        public void setFocused(boolean focused) {

            super.setFocused(focused);
            if (!focused) {
                this.scrollbarDragActive = false;
            }
        }

        @Override
        public int getScrollBarHeight() {
            // Fix #307: divide by zero exception in AbstractScrollWidget.mouseDragged
            int rtn = super.getScrollBarHeight();
            if (rtn == this.height) {
                return rtn - 1;
            } else {
                return rtn;
            }
        }

        @MCVersionDependentBehaviour
        @Override
        public boolean mouseClicked(
                double pMouseX,
                double pMouseY,
                int pButton
        ) {

            try {
                if (pButton == 0) {
                    this.scrollbarDragActive = false;
                }
                if (pButton == 0 && this.visible && this.withinContentAreaPoint(pMouseX, pMouseY)) {
                    if (content.isEmpty()) {
                        return false;
                    }
                    // Focus the editor so the caret blinks and keys go here
                    this.setFocused(true);

                    boolean shiftDown = Screen.hasShiftDown();
                    // Move cursor to the click position
                    seekCursorFromPoint(pMouseX, pMouseY);
                    // If not extending with Shift, start a new selection anchor at the click
                    if (!shiftDown) {
                        this.textField.selectCursor = this.textField.cursor;
                    }
                    // Enable selection so dragging extends from the anchor
                    this.textField.setSelecting(true);
                    return true;
                }
                boolean clickedScrollbar =
                        pButton == 0
                        && this.visible
                        && this.scrollbarVisible()
                        && pMouseX >= SFMWidgetUtils.getX(this) + this.width
                        && pMouseX <= SFMWidgetUtils.getX(this) + this.width + 8
                        && pMouseY >= SFMWidgetUtils.getY(this)
                        && pMouseY < SFMWidgetUtils.getY(this) + this.height;
                if (clickedScrollbar) {
                    this.scrollbarDragActive = true;
                }

                return super.mouseClicked(pMouseX, pMouseY, pButton);
            } catch (Exception e) {
                SFM.LOGGER.error("Error in LogsScreen.MyMultiLineEditBox.mouseClicked", e);
                return false;
            }
        }

        @Override
        public int getInnerHeight() {
            // parent method uses this.textField.getLineCount() which is split for text wrapping
            // we don't use the wrapped text, so we need to calculate the height ourselves to avoid overshooting
            return this.font.lineHeight * (content.size() + 2);
        }

        @Override
        public boolean mouseDragged(
                double mx,
                double my,
                int button,
                double dx,
                double dy
        ) {
            // IMPORTANT: give the scrollbar drag priority.
            // If the drag started on the scrollbar, AbstractScrollWidget will
            // consume this, and we should not start a text selection.
            if (this.scrollbarDragActive && super.mouseDragged(mx, my, button, dx, dy)) {
                return true;
            }

            try {
                if (button == 0 && this.visible && this.withinContentAreaPoint(mx, my)) {
                    if (content.isEmpty()) {
                        return false;
                    }
                    // Keep selection active while dragging and update cursor
                    this.textField.setSelecting(true);
                    seekCursorFromPoint(mx, my);
                    return true;
                }
            } catch (Exception e) {
                SFM.LOGGER.error("Error in LogsScreen.MyMultiLineEditBox.mouseDragged", e);
                return false;
            }

            return false;
        }

        @Override
        public boolean mouseReleased(
                double mx,
                double my,
                int button
        ) {

            if (button == 0) {
                // Stop active selection on mouse up
                this.textField.setSelecting(false);
                this.scrollbarDragActive = false;
            }
            return super.mouseReleased(mx, my, button);
        }

        @Override
        protected void setScrollAmount(double pScrollAmount) {

            if (!scrollingEnabled) return;
            super.setScrollAmount(pScrollAmount);
        }

        private void rebuildDisplayCache() {

            this.lastPlainText = this.textField.value();
            displayedLineStartOffsets.clear();
            displayedLineStartOffsets.add(0);
            for (int i = 0; i < lastPlainText.length(); i++) {
                if (lastPlainText.charAt(i) == '\n') {
                    displayedLineStartOffsets.add(i + 1);
                }
            }
            int lines = content.size();
            while (displayedLineStartOffsets.size() > lines) {
                displayedLineStartOffsets.remove(displayedLineStartOffsets.size() - 1);
            }
            while (displayedLineStartOffsets.size() < lines) {
                displayedLineStartOffsets.add(lastPlainText.length());
            }
        }

        private void seekCursorFromPoint(
                double mx,
                double my
        ) {

            int lineCount = content.size();
            double innerX = mx - (
                    this.x + this.innerPadding() + SFMTextEditorUtils.getLineNumberWidth(
                            this.font,
                            lineCount
                    )
            );
            double innerY = my - (this.y + this.innerPadding()) + this.scrollAmount();
            int lineIndex = Mth.clamp(
                    (int) Math.floor(innerY / Math.max(1, this.font.lineHeight)),
                    0,
                    Math.max(0, lineCount - 1)
            );
            int cursorPosition = pointToCursor(innerX, lineIndex);

            this.scrollingEnabled = false;
            this.textField.seekCursor(Whence.ABSOLUTE, cursorPosition);
            this.scrollingEnabled = true;
        }

        private int getLineStartIndex(int lineIndex) {

            if (displayedLineStartOffsets.isEmpty()) return 0;
            int clamped = Mth.clamp(
                    lineIndex,
                    0,
                    Math.max(0, displayedLineStartOffsets.size() - 1)
            );
            return displayedLineStartOffsets.get(clamped);
        }

        private int pointToCursor(
                double innerX,
                int lineIndex
        ) {

            int lineStartIndex = getLineStartIndex(lineIndex);
            if (content.isEmpty()) {
                return lineStartIndex;
            }
            int clampedLine = Mth.clamp(lineIndex, 0, Math.max(0, content.size() - 1));
            String plainLine = content.get(clampedLine).getString();
            int clampedX = (int) Math.max(0, innerX);
            int cursorOffsetInLine = this.font.plainSubstrByWidth(plainLine, clampedX).length();
            int widthBeforeCursor = this.font.width(plainLine.substring(0, cursorOffsetInLine));
            if (cursorOffsetInLine < plainLine.length()) {
                int nextGlyphWidth = this.font.width(plainLine.substring(cursorOffsetInLine, cursorOffsetInLine + 1));
                if ((double) (clampedX - widthBeforeCursor) >= nextGlyphWidth / 2.0D) {
                    cursorOffsetInLine = Math.min(plainLine.length(), cursorOffsetInLine + 1);
                }
            }
            return Mth.clamp(
                    lineStartIndex + cursorOffsetInLine,
                    0,
                    this.textField.value().length()
            );
        }

        @Override
        protected int getMaxScrollAmount() {

            return Math.max(1, super.getMaxScrollAmount()); // Fix #307: divide by zero exception
        }

        @Override
        protected void renderContents(
                PoseStack poseStack,
                int mx,
                int my,
                float partialTicks
        ) {

            if (shouldRebuildText()) {
                rebuildText();
            }
            if (!lastPlainText.equals(this.textField.value())) {
                rebuildDisplayCache();
            }

            List<MutableComponent> lines = content;
            if (lines.isEmpty()) {
                return;
            }

            final boolean isCursorVisible = this.isFocused() && this.frame / 6 % 2 == 0;
            final int cursorIndex = this.textField.cursor();

            final int lineHeight = Math.max(1, this.font.lineHeight);
            final int availableHeight = this.height - this.innerPadding() * 2;
            final double scroll = this.scrollAmount();

            // Determine which logical line is at the top
            final int viewLineIndexStart = Mth.clamp(
                    (int) Math.floor(scroll / lineHeight),
                    0,
                    Math.max(0, lines.size() - 1)
            );
            // Render a small overscan
            final int numVisibleLines = Math.max(1, availableHeight / lineHeight + 2);
            final int viewLineIndexEnd = Math.min(lines.size(), viewLineIndexStart + numVisibleLines);

            final int lineX =
                    SFMWidgetUtils.getX(this) + this.innerPadding()
                    + SFMTextEditorUtils.getLineNumberWidth(this.font, content.size());

            boolean isCursorAtEndOfLine = false;
            boolean drewCursorGlyph = false;

            // IMPORTANT: do not subtract (scroll % lineHeight) here.
            // The parent has already translated by -scrollAmount.
            // Draw at content-space Y positions as if there was no scrolling:
            final int contentTopY = SFMWidgetUtils.getY(this) + this.innerPadding();
            int lineY = contentTopY + viewLineIndexStart * lineHeight;
            int charCountAccum = getLineStartIndex(viewLineIndexStart);

            int cursorX = 0;
            int cursorY = 0;

            final StringView selectedRange = this.textField.getSelected();
            final int selectionStart = selectedRange.beginIndex();
            final int selectionEnd = selectedRange.endIndex();

            // One buffer for the entire text pass
            MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

            // Collect selection highlights rects and draw them after the text
            List<int[]> highlightRects = new ArrayList<>();

            Matrix4f matrix4f = poseStack.last().pose();

            for (int line = viewLineIndexStart; line < viewLineIndexEnd; ++line) {
                var componentColoured = lines.get(line);
                String plainLine = componentColoured.getString();
                int lineLength = plainLine.length();

                boolean cursorOnThisLine =
                        isCursorVisible && cursorIndex >= charCountAccum
                        && cursorIndex <= charCountAccum + lineLength;

                if (SFMTextEditorUtils.shouldShowLineNumbers()) {
                    // Draw line number
                    String lineNumber = String.valueOf(line + 1);
                    SFMFontUtils.drawInBatch(
                            lineNumber,
                            this.font,
                            lineX - 2 - this.font.width(lineNumber),
                            lineY,
                            true,
                            false,
                            matrix4f,
                            buffer
                    );
                }

                if (cursorOnThisLine) {
                    isCursorAtEndOfLine = cursorIndex == charCountAccum + lineLength;
                    cursorY = lineY;
                    int relativeCursorIndex = cursorIndex - charCountAccum;
                    int drawnWidthBeforeCursor = this.font.width(plainLine.substring(0, relativeCursorIndex));
                    cursorX = lineX + drawnWidthBeforeCursor;
                    // draw text before cursor
                    SFMFontUtils.drawInBatch(
                            SFMComponentUtils.substring(componentColoured, 0, relativeCursorIndex),
                            font,
                            lineX,
                            lineY,
                            true,
                            false,
                            matrix4f,
                            buffer
                    );

                    // draw text after cursor
                    SFMFontUtils.drawInBatch(
                            SFMComponentUtils.substring(componentColoured, relativeCursorIndex, lineLength),
                            font,
                            cursorX,
                            lineY,
                            true,
                            false,
                            matrix4f,
                            buffer
                    );
                    drewCursorGlyph = true;
                } else {
                    SFMFontUtils.drawInBatch(
                            componentColoured,
                            font,
                            lineX,
                            lineY,
                            true,
                            false,
                            matrix4f,
                            buffer
                    );
                }

                // Check if the selection is within the current line
                if (selectionStart <= charCountAccum + lineLength && selectionEnd > charCountAccum) {
                    int lineSelectionStart = Math.max(selectionStart - charCountAccum, 0);
                    int lineSelectionEnd = Math.min(selectionEnd - charCountAccum, lineLength);

                    int highlightStartX = this.font.width(plainLine.substring(0, lineSelectionStart));
                    int highlightEndX = this.font.width(plainLine.substring(0, lineSelectionEnd));

                    highlightRects.add(new int[]{
                            lineX + highlightStartX,
                            lineY,
                            lineX + highlightEndX,
                            lineY + lineHeight
                    });
                }

                lineY += lineHeight;
                charCountAccum += lineLength + 1;
            }

            // Flush the text batch once
            buffer.endBatch();

            // Draw selection highlights after text
            for (int[] r : highlightRects) {
                SFMScreenRenderUtils.renderHighlight(
                        poseStack,
                        r[0],
                        r[1],
                        r[2],
                        r[3]
                );
            }

            if (drewCursorGlyph) {
                if (isCursorAtEndOfLine) {
                    SFMFontUtils.draw(
                            poseStack,
                            this.font,
                            "_",
                            cursorX,
                            cursorY,
                            -1,
                            true
                    );
                } else {
                    GuiComponent.fill(
                            poseStack,
                            cursorX,
                            cursorY - 1,
                            cursorX + 1,
                            cursorY + 1 + 9,
                            -1
                    );
                }
            }
        }

    }

}
