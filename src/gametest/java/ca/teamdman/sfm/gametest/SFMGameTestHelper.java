package ca.teamdman.sfm.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;

public class SFMGameTestHelper extends GameTestHelper {
    public SFMGameTestHelper(GameTestInfo pTestInfo) {
        super(pTestInfo);
    }
    public SFMGameTestHelper(GameTestHelper helper) {
        super(helper.testInfo);
    }
}
