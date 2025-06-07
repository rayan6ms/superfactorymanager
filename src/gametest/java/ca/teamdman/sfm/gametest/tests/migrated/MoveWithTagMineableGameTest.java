package ca.teamdman.sfm.gametest.tests.migrated;

import ca.teamdman.sfm.gametest.LeftRightManagerTest;
import ca.teamdman.sfm.gametest.SFMGameTest;
import ca.teamdman.sfm.gametest.SFMGameTestDefinition;
import ca.teamdman.sfm.gametest.SFMGameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Arrays;

import static ca.teamdman.sfm.gametest.SFMGameTestMethodHelpers.enchant;

/**
 * Migrated from SFMWithGameTests.move_with_tag_mineable
 */
@SuppressWarnings({
        "RedundantSuppression",
        "DataFlowIssue",
        "OptionalGetWithoutIsPresent",
        "DuplicatedCode",
        "ArraysAsListWithZeroOrOneArgument"
})
@SFMGameTest
public class MoveWithTagMineableGameTest extends SFMGameTestDefinition {

    @Override
    public String template() {
        return "3x2x1";
    }

    @Override
    public void run(SFMGameTestHelper helper) {
        new LeftRightManagerTest(helper)
                .setProgram("""
                                    EVERY 20 TICKS DO
                                        INPUT WITH TAG minecraft:mineable/shovel FROM left
                                        OUTPUT TO right
                                    END
                                    """)
                .preContents("left", Arrays.asList(
                        enchant(helper, new ItemStack(Items.DIRT, 64), Enchantments.SHARPNESS, 100), // Slot 0
                        new ItemStack(Items.DIRT, 64),                                       // Slot 1
                        new ItemStack(Items.STONE, 64)                                       // Slot 2
                ))
                .postContents("left", Arrays.asList(
                        ItemStack.EMPTY,                    // Slot 0 (Dirt moved)
                        ItemStack.EMPTY,                    // Slot 1 (Dirt moved)
                        new ItemStack(Items.STONE, 64)      // Slot 2 (Stone remains)
                ))
                .postContents("right", Arrays.asList(
                        enchant(helper, new ItemStack(Items.DIRT, 64), Enchantments.SHARPNESS, 100), // Slot 0
                        new ItemStack(Items.DIRT, 64)                                        // Slot 1
                        // The rest are empty by default
                ))
                .run();
    }
}
