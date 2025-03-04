package ca.teamdman.sfml.intellisense;

import ca.teamdman.sfml.program_builder.ProgramBuildResult;

public record IntellisenseContext(
        ProgramBuildResult programBuildResult,
        int cursorPosition,
        int selectionCursorPosition
) {
//    public static Intellisensecontext buildContext(
//            String program,
//            int cursorPosition,
//            int selectionCursorPosition
//    ) {
//        return new IntellisenseContext(program, cursorPosition, selectionCursorPosition);
//    }
}
