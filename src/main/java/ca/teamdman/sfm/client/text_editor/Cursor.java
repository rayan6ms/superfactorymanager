package ca.teamdman.sfm.client.text_editor;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;

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
    public Cursor(Caret beginning, Caret end, boolean isBackwards) {
        this(
                isBackwards ? end : beginning,
                isBackwards ? beginning : end
        );
    }

    public Cursor growSelectionLeft(Int2IntFunction lineLengths) {
        boolean isBackwards = isBackwards();
        Caret beginning = getBeginning();
        Caret end = getEnd();
        if (beginning.lineIndex() == 0 && beginning.gapIndex() == 0) {
            // Already at the start of the document, unable to grow left
            return this;
        } else if (beginning.gapIndex() == 0) {
            // Move to the end of the previous line
            int newLineIndex = beginning.lineIndex() - 1;
            int newGapIndex = lineLengths.get(newLineIndex);
            beginning = new Caret(newLineIndex, newGapIndex);
            return new Cursor(beginning, end, isBackwards);
        } else {
            // Move one character left
            int newGapIndex = beginning.gapIndex() - 1;
            beginning = new Caret(beginning.lineIndex(), newGapIndex);
            return new Cursor(beginning, end, isBackwards);
        }
    }

    public Cursor growSelectionRight(Int2IntFunction lineLengths, int numLines) {
        boolean isBackwards = isBackwards();
        Caret beginning = getBeginning();
        Caret end = getEnd();
        if (end.lineIndex() >= numLines - 1 && end.gapIndex() >= lineLengths.get(end.lineIndex())) {
            // Already at the end of the document, unable to grow right
            return this;
        } else if (end.gapIndex() >= lineLengths.get(end.lineIndex())) {
            // Move to the start of the next line
            int newLineIndex = end.lineIndex() + 1;
            int newGapIndex = 0;
            end = new Caret(newLineIndex, newGapIndex);
            return new Cursor(beginning, end, isBackwards);
        } else {
            // Move one character right
            int newGapIndex = end.gapIndex() + 1;
            end = new Caret(end.lineIndex(), newGapIndex);
            return new Cursor(beginning, end, isBackwards);
        }
    }

    public Cursor flip() {
        return new Cursor(tail(), head());
    }

    public boolean isBackwards() {
        return head().compareTo(tail()) > 0;
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
