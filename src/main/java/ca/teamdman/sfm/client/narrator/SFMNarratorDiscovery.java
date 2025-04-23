package ca.teamdman.sfm.client.narrator;

import ca.teamdman.sfm.SFM;
import com.mojang.text2speech.Narrator;
import com.mojang.text2speech.NarratorLinux;
import com.mojang.text2speech.NarratorMac;
import com.mojang.text2speech.OperatingSystem;

public class SFMNarratorDiscovery {
    public static Narrator getNarrator() {
        try {
            return switch (OperatingSystem.get()) {
                case LINUX -> new NarratorLinux();
                case WINDOWS -> new SFMWindowsNarrator();
                case MAC_OS -> new NarratorMac();
                default ->
                        throw new Narrator.InitializeException("Unsupported platform " + System.getProperty("os.name"));
            };
        } catch (final Narrator.InitializeException e) {
            SFM.LOGGER.error("Error while loading the narrator", e);
            return Narrator.EMPTY;
        }
    }

}
