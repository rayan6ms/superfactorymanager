package ca.teamdman.sfm.client.screen.text_editor;

import ca.teamdman.sfm.client.registry.SFMTextEditorActions;
import ca.teamdman.sfm.client.screen.SFMFontUtils;
import ca.teamdman.sfm.client.screen.SFMScreenChangeHelpers;
import ca.teamdman.sfm.client.screen.SFMScreenRenderUtils;
import ca.teamdman.sfm.client.text_editor.ISFMTextEditScreenOpenContext;
import ca.teamdman.sfm.client.text_editor.TextEditContext;
import ca.teamdman.sfm.client.text_editor.action.ITextEditAction;
import ca.teamdman.sfm.client.text_editor.action.KeyboardImpulse;
import ca.teamdman.sfm.common.config.SFMConfig;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.Optional;

public class SFMTextEditScreenV2 extends Screen {
    protected TextEditContext textEditContext;
    protected ISFMTextEditScreenOpenContext openContext;
    private final boolean previousHideGui;
    private final @Nullable Screen previousScreen;

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

    private void finalizeClose() {
        SFMScreenChangeHelpers.setScreen(previousScreen);
        assert minecraft != null;
        minecraft.options.hideGui = previousHideGui;
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
        Optional<ITextEditAction> matchedAction = SFMTextEditorActions
                .getTextEditActions()
                .filter(action -> action.matches(textEditContext, impulse))
                .findFirst();
        if (matchedAction.isPresent()) {
            matchedAction.get().apply(textEditContext, impulse);
            return true;
        }
        return false;
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
            PoseStack pPoseStack,
            int pMouseX,
            int pMouseY,
            float pPartialTick
    ) {
        Matrix4f matrix4f = pPoseStack.last().pose();
        LinkedList<StringBuilder> lines = textEditContext.lines();
        int numLines = lines.size();
        var buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        boolean shouldShowLineNumbers = shouldShowLineNumbers();
        int marginForLineNumber = shouldShowLineNumbers() ? this.font.width("000") : 0;
        for (int lineIndex = 0; lineIndex < numLines; lineIndex++) {
            StringBuilder line = lines.get(lineIndex);
            int lineHeight = this.font.lineHeight;
            if (shouldShowLineNumbers) {
                SFMFontUtils.drawInBatch(
                        String.format("%03d", lineIndex + 1),
                        this.font,
                        0,
                        lineIndex * lineHeight,
                        true,
                        matrix4f,
                        buffer,
                        false
                );
            }
            SFMFontUtils.drawInBatch(
                    line.toString(),
                    this.font,
                    marginForLineNumber,
                    lineIndex * lineHeight,
                    true,
                    matrix4f,
                    buffer,
                    false
            );
        }
        buffer.endBatch();
    }

    @Override
    protected void init() {
        super.init();
        SFMScreenRenderUtils.enableKeyRepeating();
    }
}
