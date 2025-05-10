package ca.teamdman.sfm.client.gui.screen;

import java.util.function.Consumer;

public record TomlEditScreenOpenContext(
        String textContents,
        Consumer<String> saveCallback
) {
}
