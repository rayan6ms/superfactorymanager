package ca.teamdman.sfm.gametest.tests.compat.mekanism;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.gametest.SFMGameTest;
import ca.teamdman.sfm.gametest.SFMGameTestDefinition;
import ca.teamdman.sfm.gametest.SFMGameTestHelper;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.TileEntityEnergyCube;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import static ca.teamdman.sfm.gametest.SFMGameTestMethodHelpers.*;

/**
 * Migrated from SFMMekanismCompatGameTests.mek_energy_one
 */
@SuppressWarnings({
        "RedundantSuppression",
        "DataFlowIssue",
        "OptionalGetWithoutIsPresent",
        "DuplicatedCode",
        "ArraysAsListWithZeroOrOneArgument"
})
@SFMGameTest
public class MekEnergyTenGameTest extends SFMGameTestDefinition {

    @Override
    public String template() {
        return "3x2x1";
    }

    @Override
    public void testMethod(SFMGameTestHelper helper) {
        // designate positions
        var leftPos = new BlockPos(2, 2, 0);
        var rightPos = new BlockPos(0, 2, 0);
        var managerPos = new BlockPos(1, 2, 0);

        // set up the world
        helper.setBlock(leftPos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock());
        TileEntityEnergyCube left = getAndPrepMekTile(helper,leftPos);
        helper.setBlock(rightPos, MekanismBlocks.ULTIMATE_ENERGY_CUBE.getBlock());
        TileEntityEnergyCube right = getAndPrepMekTile(helper,rightPos);
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        var manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));

        // set up the program
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   EVERY 20 TICKS DO
                                     INPUT 10 forge_energy:forge:energy FROM a NORTH SIDE
                                     OUTPUT forge_energy:forge:energy TO b TOP SIDE
                                   END
                                   """.stripIndent());

        // set the labels
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftPos))
                .add("b", helper.absolutePos(rightPos))
                .save(manager.getDisk());

        left.setEnergy(0, 100);
        right.setEnergy(0, 0);
        succeedIfManagerDidThingWithoutLagging(helper, manager, () -> {
            assertTrue(
                    left
                            .getEnergy(0)
                    == UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(30),
                    "Contents did not depart"
            );
            assertTrue(
                    right.getEnergy(0) == UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(10),
                    "Contents did not arrive"
            );

        });
    }
}
