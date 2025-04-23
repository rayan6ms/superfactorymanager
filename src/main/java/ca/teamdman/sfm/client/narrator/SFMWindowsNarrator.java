package ca.teamdman.sfm.client.narrator;

import com.mojang.text2speech.NarratorWindows;

public class SFMWindowsNarrator extends NarratorWindows {
    private static final int VTABLE_INDEX_SETRATE = 19;

    public SFMWindowsNarrator() throws InitializeException {
        super();
    }

    /**
     * Sets the speaking rate.
     * @param rate The rate (-10 to 10, where 0 is normal speed)
     */
    public void setRate(int rate) {
        _invokeNativeInt(VTABLE_INDEX_SETRATE, new Object[] {getPointer(), rate});
    }
}
