package ca.teamdman.sfml.intellisense;

public record IntellisenseContext(
        String program,
        int cursorPosition,
        int selectionCursorPosition
) {
}
