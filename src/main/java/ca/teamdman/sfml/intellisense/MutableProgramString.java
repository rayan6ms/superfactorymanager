package ca.teamdman.sfml.intellisense;

public class MutableProgramString {
    private StringBuilder content;
    private int cursorPosition = 0;
    private int selectionCursorPosition = 0;

    public MutableProgramString(
            String content,
            int cursorPosition,
            int selectionCursorPosition
    ) {
        this.content = new StringBuilder(content);
        this.cursorPosition = cursorPosition;
        this.selectionCursorPosition = selectionCursorPosition;
    }

    public int getWordBeginning() {
        int start = cursorPosition;
        while (start > 0 && !Character.isWhitespace(content.charAt(start - 1))) {
            start--;
        }
        return start;
    }

    public int getWordEnd() {
        int end = cursorPosition;
        while (end < content.length() && !Character.isWhitespace(content.charAt(end))) {
            end++;
        }
        return end;
    }
    public String getWord() {
        return content.substring(getWordBeginning(), getWordEnd());
    }

    public void replaceWordAndMoveCursorsToEnd(String suggestion) {
        int start = getWordBeginning();
        int end = getWordEnd();
        content.replace(start, end, suggestion);
        cursorPosition = start + suggestion.length();
        selectionCursorPosition = start + suggestion.length();
    }

    public void insertTextWithoutMovingCursors(String text) {
        content.insert(cursorPosition, text);
    }

    public String getContent() {
        return content.toString();
    }

    public void setContent(StringBuilder content) {
        this.content = content;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(int cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    public int getSelectionCursorPosition() {
        return selectionCursorPosition;
    }

    public void setSelectionCursorPosition(int selectionCursorPosition) {
        this.selectionCursorPosition = selectionCursorPosition;
    }
    public void offsetCursors(int offset) {
        cursorPosition += offset;
        selectionCursorPosition += offset;
    }

    public int cursorInWord() {
        return cursorPosition - getWordBeginning();
    }
}
