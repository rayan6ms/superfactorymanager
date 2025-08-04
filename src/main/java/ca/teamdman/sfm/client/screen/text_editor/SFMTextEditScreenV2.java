package ca.teamdman.sfm.client.screen.text_editor;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.registry.SFMTextEditorActions;
import ca.teamdman.sfm.client.screen.SFMFontUtils;
import ca.teamdman.sfm.client.screen.SFMScreenChangeHelpers;
import ca.teamdman.sfm.client.screen.SFMScreenRenderUtils;
import ca.teamdman.sfm.client.text_editor.Caret;
import ca.teamdman.sfm.client.text_editor.Cursor;
import ca.teamdman.sfm.client.text_editor.ISFMTextEditScreenOpenContext;
import ca.teamdman.sfm.client.text_editor.TextEditContext;
import ca.teamdman.sfm.client.text_editor.action.ITextEditAction;
import ca.teamdman.sfm.client.text_editor.action.KeyboardImpulse;
import ca.teamdman.sfm.common.config.SFMConfig;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;

public class SFMTextEditScreenV2 extends Screen {
    private final boolean previousHideGui;
    private final @Nullable Screen previousScreen;
    protected TextEditContext textEditContext;
    protected ISFMTextEditScreenOpenContext openContext;

    public SFMTextEditScreenV2(
            ISFMTextEditScreenOpenContext openContext,
            @Nullable Screen previousScreen,
            boolean previousHideGui
    ) {
        super(LocalizationKeys.TEXT_EDIT_SCREEN_TITLE.getComponent());
        this.openContext = openContext;
        this.previousScreen = previousScreen;
        this.textEditContext = new TextEditContext(openContext.initialValue());
        this.previousHideGui = previousHideGui;
    }

    @Override
    public boolean keyPressed(
            int pKeyCode,
            int pScanCode,
            int pModifiers
    ) {
        // we are not calling super here because we are not using traditional widgets with tab navigation
        if (pKeyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            openContext.onTryClose(textEditContext.getContent(), this::finalizeClose);
            return true;
        }
        KeyboardImpulse impulse = new KeyboardImpulse(pKeyCode, pScanCode, pModifiers);
        var matchedActions = SFMTextEditorActions
                .getTextEditActions()
                .filter(action -> action.matches(textEditContext, impulse)).toArray(ITextEditAction[]::new);
        for (ITextEditAction matchedAction : matchedActions) {
            SFM.LOGGER.debug("Matched action: {}", matchedAction.getClass().getSimpleName());
            matchedAction.apply(textEditContext, impulse);
        }
        return matchedActions.length > 0;
    }

    @Override
    public boolean charTyped(
            char pCodePoint,
            int pModifiers
    ) {
        String text = Character.toString(pCodePoint);
        textEditContext.insertTextAtCursors(text);
        return true;
    }

    public boolean shouldShowLineNumbers() {
        return SFMConfig.getOrDefault(SFMConfig.CLIENT_TEXT_EDITOR_CONFIG.showLineNumbers);
    }

    @Override
    public void render(
            GuiGraphics pGuiGraphics,
            int pMouseX,
            int pMouseY,
            float pPartialTick
    ) {
        Matrix4f matrix4f = pGuiGraphics.pose().last().pose();
        LinkedList<StringBuilder> lines = textEditContext.lines();
        int numLines = lines.size();
        var buffer = pGuiGraphics.bufferSource();
        boolean shouldShowLineNumbers = shouldShowLineNumbers();
        int marginForLineNumber = shouldShowLineNumbers() ? this.font.width("000") + 4 : 0;
        for (int lineIndex = 0; lineIndex < numLines; lineIndex++) {
            StringBuilder line = lines.get(lineIndex);
            int lineHeight = this.font.lineHeight;
            if (shouldShowLineNumbers) {
                SFMFontUtils.drawInBatch(
                        Component.literal(String.format("%03d", lineIndex + 1)).withStyle(ChatFormatting.GRAY),
                        this.font,
                        0,
                        lineIndex * lineHeight,
                        true,
                        false,
                        matrix4f,
                        buffer
                );
            }
            SFMFontUtils.drawInBatch(
                    line.toString(),
                    this.font,
                    marginForLineNumber,
                    lineIndex * lineHeight,
                    true,
                    false, matrix4f,
                    buffer
            );
        }
        buffer.endBatch();

        // Render selection highlights
        var selectedCharactersByLine = textEditContext.selectedCharactersByLine();
        for (int lineIndex = 0; lineIndex < numLines; lineIndex++) {
            @Nullable IntervalSet selectedCharacters = selectedCharactersByLine.get(lineIndex);
            StringBuilder line = lines.get(lineIndex);
            if (selectedCharacters == null || selectedCharacters.isNil()) {
                continue; // no selection on this line
            }
            for (Interval interval : selectedCharacters.getIntervals()) {
                int selectionStartX = this.font.width(line.substring(0, interval.a)) + marginForLineNumber;
                int selectionEndX = this.font.width(line.substring(0, interval.b + 1)) + marginForLineNumber;
                int selectionY = lineIndex * this.font.lineHeight;
                SFMScreenRenderUtils.renderHighlight(
                        pGuiGraphics,
                        selectionStartX,
                        selectionY,
                        selectionEndX,
                        selectionY + this.font.lineHeight
                );
            }
        }

        // Render cursors
        for (Cursor cursor : textEditContext.multiCursor().cursors()) {
            Caret head = cursor.head();
            Caret tail = cursor.tail();
            int headX = this.font.width(lines.get(head.lineIndex()).substring(0, head.gapIndex()))
                        + marginForLineNumber;
            int tailX = this.font.width(lines.get(tail.lineIndex()).substring(0, tail.gapIndex()))
                        + marginForLineNumber;
            int headY = head.lineIndex() * this.font.lineHeight;
            int tailY = tail.lineIndex() * this.font.lineHeight;
            renderCursor(pGuiGraphics, headX, headY, FastColor.ARGB32.color(255 / 2, 255, 0, 0));
            renderCursor(pGuiGraphics, tailX, tailY, FastColor.ARGB32.color(255 / 2, 0, 0, 255));
        }
    }

    private void finalizeClose() {
        SFMScreenChangeHelpers.setScreen(previousScreen);
        assert minecraft != null;
        minecraft.options.hideGui = previousHideGui;
    }

    protected void renderCursor(
            GuiGraphics guiGraphics,
            int x,
            int y,
            int color
    ) {
        guiGraphics.fill(
                x,
                y,
                x + 2,
                y + this.font.lineHeight,
                color
        );
    }

    @Override
    protected void init() {
        super.init();
        SFMScreenRenderUtils.enableKeyRepeating();
    }
}
