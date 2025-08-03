package ca.teamdman.sfm.client.screen.text_editor;

import ca.teamdman.sfm.client.registry.SFMTextEditorActions;
import ca.teamdman.sfm.client.screen.SFMScreenRenderUtils;
import ca.teamdman.sfm.client.text_editor.ISFMTextEditScreenOpenContext;
import ca.teamdman.sfm.client.text_editor.TextEditContext;
import ca.teamdman.sfm.client.text_editor.action.ITextEditAction;
import ca.teamdman.sfm.client.text_editor.action.KeyboardImpulse;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class SFMTextEditScreenV2 extends Screen {
    protected TextEditContext textEditContext = new TextEditContext();

    public SFMTextEditScreenV2(ISFMTextEditScreenOpenContext ctx) {
        super(LocalizationKeys.TEXT_EDIT_SCREEN_TITLE.getComponent());
    }

    @Override
    public boolean keyPressed(
            int pKeyCode,
            int pScanCode,
            int pModifiers
    ) {
        // we are not calling super here because we are not using traditional widgets with tab navigation
        if (pKeyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            this.onClose();
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
    protected void init() {
        super.init();
        SFMScreenRenderUtils.enableKeyRepeating();
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
}
