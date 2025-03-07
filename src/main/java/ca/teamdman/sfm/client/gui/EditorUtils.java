package ca.teamdman.sfm.client.gui;

public class EditorUtils {
    /**
     * Inserts a suggestion into the content at the cursor position.
     * If the cursor is within a word that is a prefix of the suggestion, the word is replaced with the suggestion.
     * @param content The text editor content
     * @param cursor The index within the string of the cursor
     * @param selectionCursor The index within the string of the selection cursor. If equal to cursorPosition, no selection is present.
     * @param suggestion The suggestion to be inserted
     * @return The modified content, and the new cursor and selection cursor positions
     */
    public static ManipulationResult insertSuggestion(
            String content,
            int cursor,
            int selectionCursor,
            String suggestion
    ) {
        StringBuilder sb = new StringBuilder(content);
        int start = cursor;
        while (start > 0 && !Character.isWhitespace(sb.charAt(start - 1))) {
            start--;
        }
        int end = cursor;
        while (end < sb.length() && !Character.isWhitespace(sb.charAt(end))) {
            end++;
        }
        sb.replace(start, end, suggestion);
        return new ManipulationResult(sb.toString(), start + suggestion.length(), start + suggestion.length());
    }

    private static int findLineStart(String content, int cursorPos) {
        while (cursorPos > 0 && content.charAt(cursorPos - 1) != '\n') {
            cursorPos--;
        }
        return cursorPos;
    }

    private static int findLineEnd(String content, int cursorPos) {
        while (cursorPos < content.length() && content.charAt(cursorPos) != '\n') {
            cursorPos++;
        }
        return cursorPos;
    }

    /**
     * Indents the given content, and updates the cursor and selection.
     *
     * @param content            The content to indent
     * @param cursorPos          The index within the string of the cursor
     * @param selectionCursorPos The index within the string of the selection cursor. If equal to cursorPosition, no selection is present.
     * @return The indented content, and the new cursor and selection cursor positions
     */
    public static ManipulationResult indent(String content, int cursorPos, int selectionCursorPos) {
        StringBuilder sb = new StringBuilder(content);
        int lineStart = findLineStart(content, Math.min(cursorPos, selectionCursorPos));
        int lineEnd = findLineEnd(content, Math.max(cursorPos, selectionCursorPos));
        if (lineStart == lineEnd) {
            sb.insert(lineStart, "    ");
            if (lineStart <= cursorPos) {
                cursorPos += 4;
            }
            if (lineStart <= selectionCursorPos) {
                selectionCursorPos += 4;
            }
        } else {
            while (lineStart < lineEnd) {
                sb.insert(lineStart, "    ");
                lineEnd += 4;
                if (lineStart < cursorPos) {
                    cursorPos += 4;
                }
                if (lineStart < selectionCursorPos) {
                    selectionCursorPos += 4;
                }
                lineStart = findLineEnd(sb.toString(), lineStart) + 1;
            }
        }
        return new ManipulationResult(sb.toString(), cursorPos, selectionCursorPos);
    }

    /**
     * Deindents the given content, and updates the cursor and selection.
     *
     * @param content            The content to deindent
     * @param cursorPos          The index within the string of the cursor
     * @param selectionCursorPos The index within the string of the selection cursor. If equal to cursorPosition, no selection is present.
     * @return The deindented content, and the new cursor and selection cursor positions
     */
    public static ManipulationResult deindent(String content, int cursorPos, int selectionCursorPos) {
        StringBuilder sb = new StringBuilder(content);
        int lineStart = findLineStart(content, Math.min(cursorPos, selectionCursorPos));
        int lineEnd = findLineEnd(content, Math.max(cursorPos, selectionCursorPos));

        while (lineStart < lineEnd) {
            for (int i = 0; i < 4 && lineStart < sb.length() && sb.charAt(lineStart) == ' '; i++) {
                sb.deleteCharAt(lineStart);
                lineEnd--;
                if (lineStart < cursorPos) {
                    cursorPos--;
                }
                if (lineStart < selectionCursorPos) {
                    selectionCursorPos--;
                }
            }
            lineStart = findLineEnd(sb.toString(), lineStart) + 1;
        }
        return new ManipulationResult(sb.toString(), cursorPos, selectionCursorPos);
    }

    /**
     * Perform the operation for hitting Ctrl+/
     * If the selection contains a line not starting with "--", prepend each line with "--"
     * If all lines in the selection start with "--", trim "--" from the start of each line
     *
     * @param content The content in the buffer
     * @param cursorPos The index within the content for the cursor position
     * @param selectionCursorPos The index within the content for the selection cursor. If equal to cursorPosition, no selection is present.
     * @return The modified content, and the new cursor positions accommodating the shifting of said content
     */
    public static ManipulationResult toggleComments(String content, int cursorPos, int selectionCursorPos) {
        StringBuilder sb = new StringBuilder(content);
        int lineStart = findLineStart(content, Math.min(cursorPos, selectionCursorPos));
        int lineEnd = findLineEnd(content, Math.max(cursorPos, selectionCursorPos));

        boolean allLinesCommented = true;
        while (lineStart < lineEnd) {
            if (lineStart + 2 >= sb.length() || sb.charAt(lineStart) != '-' || sb.charAt(lineStart + 1) != '-') {
                allLinesCommented = false;
                break;
            }
            lineStart = findLineEnd(sb.toString(), lineStart) + 1;
        }

        lineStart = findLineStart(content, Math.min(cursorPos, selectionCursorPos));
        lineEnd = findLineEnd(content, Math.max(cursorPos, selectionCursorPos));

        if (allLinesCommented) {
            while (lineStart < lineEnd) {
                sb.delete(lineStart, lineStart + 2);
                lineEnd -= 2;
                if (lineStart < cursorPos) {
                    cursorPos -= 2;
                }
                if (lineStart < selectionCursorPos) {
                    selectionCursorPos -= 2;
                }
                lineStart = findLineEnd(sb.toString(), lineStart) + 1;
            }
        } else {
            while (lineStart < lineEnd) {
                sb.insert(lineStart, "--");
                lineEnd += 2;
                if (lineStart <= cursorPos) {
                    cursorPos += 2;
                }
                if (lineStart <= selectionCursorPos) {
                    selectionCursorPos += 2;
                }
                lineStart = findLineEnd(sb.toString(), lineStart) + 1;
            }
        }
        return new ManipulationResult(sb.toString(), cursorPos, selectionCursorPos);
    }

    public record ManipulationResult(
            String content,
            int cursorPosition,
            int selectionCursorPosition
    ) {
    }
}
