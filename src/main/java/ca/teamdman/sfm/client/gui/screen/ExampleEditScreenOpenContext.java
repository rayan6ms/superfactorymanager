package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.common.program.LabelPositionHolder;

import java.util.Map;
import java.util.function.Consumer;

public record ExampleEditScreenOpenContext(
        String exampleProgrmaString,
        String diskProgramString,
        Map<String, String> templates,
        LabelPositionHolder labelPositionHolder,
        Consumer<String> saveCallback
) {
    public boolean equalsAnyTemplate(String content) {
        return templates().values().stream().anyMatch(content::equals);
    }

    /**
     * Check if it is safe to overwrite the disk with a new program.
     * If the disk is empty, it is safe to overwrite.
     * If the disk contains a template, it is safe to overwrite.
     * @return true if it is safe to overwrite the disk, false otherwise
     */
    public boolean isSafeToOverwriteDisk() {
        if (diskProgramString().isBlank()) return true;
        return equalsAnyTemplate(this.diskProgramString());
    }
}
