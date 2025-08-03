package ca.teamdman.sfm.text_editor;

import ca.teamdman.sfm.client.text_editor.TextEditContext;
import ca.teamdman.sfm.client.text_editor.action.DeleteSelectionOrCharacterToTheLeftForEachCursorAction;
import ca.teamdman.sfm.client.text_editor.action.KeyboardImpulse;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextEditorTests {
    @Test
    public void insertText() {
        TextEditContext context = new TextEditContext();
        assertEquals(1, context.lines().size(), "Should have one line before insertion");
        context.insertTextAtCursors("Ahoy, world!");
        assertEquals(1, context.lines().size(), "Should have one line after insertion");
        assertEquals("Ahoy, world!", context.lines().get(0).toString(), "Inserted text should match");
    }

    @Test
    public void insertTextTwoCursors() {
        TextEditContext context = new TextEditContext();
        context.multiCursor().addCursor(0, 0, 0, 0);
        assertEquals(1, context.lines().size(), "Should have one line before insertion");
        context.insertTextAtCursors("Ahoy, world!");
        assertEquals(1, context.lines().size(), "Should have one line after insertion");
        assertEquals("Ahoy, world!Ahoy, world!", context.lines().get(0).toString(), "Inserted text should match");
    }

    @Test
    public void deleteSelection() {
        TextEditContext context = new TextEditContext();
        String text = "Hello, world!";
        context.insertTextAtCursors(text);
        assertEquals(1, context.lines().size(), "Should have one line before deletion");
        assertEquals(text, context.lines().get(0).toString(), "Initial text should match");

        // Set the cursor to select "Hello"
        context.multiCursor().cursors().clear();
        context.multiCursor().addCursor(0, 1, 0, 1 + "ello".length());

        context.deleteSelectedText();
        assertEquals(1, context.lines().size(), "Should have one line after deletion");
        assertEquals("H, world!", context.lines().get(0).toString(), "Text after deletion should match");
    }

    @Test
    public void deleteSelectionTwoCursors() {
        TextEditContext context = new TextEditContext();
        String text = "Hello, world!";
        context.insertTextAtCursors(text);
        context.multiCursor().cursors().clear();
        context.multiCursor().addCursor(0, 0, 0, "Hello".length());
        context.multiCursor().addCursor(0, "Hello, ".length(), 0, "Hello, world".length());
        context.deleteSelectedText();
        assertEquals(1, context.lines().size(), "Should have one line after deletion");
        assertEquals(", !", context.lines().get(0).toString(), "Text after deletion should match");
    }

    @Test
    public void backspace() {
        TextEditContext context = new TextEditContext();
        String text = "Hello, world!";
        context.insertTextAtCursors(text);
        context.multiCursor().cursors().clear();
        context.multiCursor().addCursor(0, text.length(), 0, text.length());
        new DeleteSelectionOrCharacterToTheLeftForEachCursorAction()
                .apply(context, new KeyboardImpulse(GLFW.GLFW_KEY_BACKSLASH, 0, 0));
        assertEquals(1, context.lines().size(), "Should have one line after backspace");
        assertEquals("Hello, world", context.lines().get(0).toString(), "Text after backspace should match");
    }
}
