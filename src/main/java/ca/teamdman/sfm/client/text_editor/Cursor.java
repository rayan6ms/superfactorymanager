package ca.teamdman.sfm.client.text_editor;

/**
 * Represents a cursor in a text editor, with optional selection.
 */
public record Cursor(
        Caret head,
        Caret tail
) {

    @Override
    public String toString() {
        return "Cursor{" +
               "head=" + head +
               ", tail=" + tail +
               '}';
    }

    public Cursor(int position) {
        this(
                new Caret(position, 0),
                new Caret(position, 0)
        );
    }

    public Cursor flip() {
        return new Cursor(tail(), head());
    }

    public boolean hasSelection() {
        return !head().equals(tail());
    }

    public Caret getBeginning() {
        if (head().compareTo(tail()) <= 0) {
            // tail comes after, so head is the beginning
            return head();
        } else {
            return tail();
        }
    }

    public Caret getEnd() {
        if (head().compareTo(tail()) <= 0) {
            // tail comes after, so tail is the end
            return tail();
        } else {
            return head();
        }
    }
}
