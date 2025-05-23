package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.SFM;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.Collection;
import java.util.Collections;

@GameTestHolder(SFM.MOD_ID)
@PrefixGameTestTemplate
public class SFMTestGenerator {
    // just poking around for now
    @GameTestGenerator
    public Collection<TestFunction> generateTests() {
        return Collections.emptyList();
    }
//    public void registerTests(RegisterGameTestsEvent event) {
////        Consumer<GameTestHelper> methodReference = FluidTankRetainTest::it_works;
////        Method method = methodReference.getClass().getDeclaredMethod("accept", GameTestHelper.class);
////        event.register(methodReference);
//    }
}
