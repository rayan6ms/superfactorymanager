package ca.teamdman.sfm.gametest.tests.compat.mekanism;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.gametest.SFMGameTest;
import ca.teamdman.sfm.gametest.SFMGameTestDefinition;
import ca.teamdman.sfm.gametest.SFMGameTestHelper;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import static ca.teamdman.sfm.gametest.SFMGameTestMethodHelpers.assertTrue;
import static ca.teamdman.sfm.gametest.SFMGameTestMethodHelpers.succeedIfManagerDidThingWithoutLagging;

/**
 * Migrated from SFMMekanismCompatGameTests.multi_fluid
 */
@SuppressWarnings({
        "RedundantSuppression",
        "DataFlowIssue",
        "OptionalGetWithoutIsPresent",
        "DuplicatedCode",
        "ArraysAsListWithZeroOrOneArgument"
})
@SFMGameTest
public class MultiFluidGameTest extends SFMGameTestDefinition {

    @Override
    public String template() {
        return "3x4x3";
    }

    @Override
    public void testMethod(SFMGameTestHelper helper) {
        var a1Pos = new BlockPos(2, 2, 1);
        var a2Pos = new BlockPos(1, 2, 0);
        var b1Pos = new BlockPos(1, 2, 2);
        var b2Pos = new BlockPos(0, 2, 1);
        var managerPos = new BlockPos(1, 2, 1);
        helper.setBlock(a1Pos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        helper.setBlock(a2Pos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        helper.setBlock(b1Pos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        helper.setBlock(b2Pos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        var a1 = helper
                .getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(a1Pos), Direction.UP);
        var a2 = helper
                .getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(a2Pos), Direction.UP);
        var b1 = helper
                .getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(b1Pos), Direction.UP);
        var b2 = helper
                .getLevel().getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(b2Pos), Direction.UP);

        a1.fill(new FluidStack(Fluids.WATER, 3000), IFluidHandler.FluidAction.EXECUTE);
        a2.fill(new FluidStack(Fluids.LAVA, 3000), IFluidHandler.FluidAction.EXECUTE);

        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                     INPUT fluid:: FROM a NORTH SIDE
                                     OUTPUT fluid::lava, fluid::water TO b TOP SIDE
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(a1Pos))
                .add("a", helper.absolutePos(a2Pos))
                .add("b", helper.absolutePos(b1Pos))
                .add("b", helper.absolutePos(b2Pos))
                .save(manager.getDisk());

        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(a1.getFluidInTank(0).isEmpty(), "a1 did not empty");
            assertTrue(a2.getFluidInTank(0).isEmpty(), "a2 did not empty");
            assertTrue(b1.getFluidInTank(0).getFluid() == Fluids.WATER, "b1 did not fill with water");
            assertTrue(b2.getFluidInTank(0).getFluid() == Fluids.LAVA, "b2 did not fill with lava");
        });
    }
}
