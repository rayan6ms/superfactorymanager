package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.common.program.LabelPositionHolder;

import java.util.function.Consumer;

public record ProgramEditScreenOpenContext(
        String programString,
        LabelPositionHolder labelPositionHolder,
        Consumer<String> saveCallback
) {
}
