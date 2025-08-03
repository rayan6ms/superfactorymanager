package ca.teamdman.sfm.client.text_editor.action;

import ca.teamdman.sfm.client.text_editor.TextEditContext;
import org.lwjgl.glfw.GLFW;

public class DeleteSelectionOrCharacterToTheLeftForEachCursorAction implements ITextEditAction{
    @Override
    public boolean matches(
            TextEditContext context,
            KeyboardImpulse impulse
    ) {
        return impulse.keyCode() == GLFW.GLFW_KEY_BACKSPACE;
    }

    @Override
    public void apply(
            TextEditContext context,
            KeyboardImpulse impulse
    ) {

    }
}
