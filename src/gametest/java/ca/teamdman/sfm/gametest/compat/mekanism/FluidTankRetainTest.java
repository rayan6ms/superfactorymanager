package ca.teamdman.sfm.gametest.compat.mekanism;

import ca.teamdman.sfm.SFM;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;


@GameTestHolder(SFM.MOD_ID)
public class FluidTankRetainTest {
    @GameTest(template = "3x2x1")
    public static void it_works(GameTestHelper helper) {
        //TODO: implement, see OutputStatement TODO note
        helper.succeed();
    }
}
