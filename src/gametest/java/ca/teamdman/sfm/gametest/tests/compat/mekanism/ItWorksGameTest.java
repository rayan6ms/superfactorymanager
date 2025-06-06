package ca.teamdman.sfm.gametest.tests.compat.mekanism;

import ca.teamdman.sfm.gametest.SFMGameTest;
import ca.teamdman.sfm.gametest.SFMGameTestDefinition;
import ca.teamdman.sfm.gametest.SFMGameTestHelper;

/**
 * Migrated from FluidTankRetainTest.it_works
 */
@SuppressWarnings({
        "RedundantSuppression",
        "DataFlowIssue",
        "OptionalGetWithoutIsPresent",
        "DuplicatedCode",
        "ArraysAsListWithZeroOrOneArgument"
})
@SFMGameTest
public class ItWorksGameTest extends SFMGameTestDefinition {

    @Override
    public String template() {
        return "3x2x1";
    }

    @Override
    public void testMethod(SFMGameTestHelper helper) {
        //TODO: implement, see OutputStatement TODO note
        helper.succeed();
    }
}
