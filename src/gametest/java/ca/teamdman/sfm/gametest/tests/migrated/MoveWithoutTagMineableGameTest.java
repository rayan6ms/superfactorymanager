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
 * Migrated from SFMWithGameTests.move_without_tag_mineable
 */
@SuppressWarnings({
        "RedundantSuppression",
        "DataFlowIssue",
        "OptionalGetWithoutIsPresent",
        "DuplicatedCode",
        "ArraysAsListWithZeroOrOneArgument"
})
@SFMGameTest
public class MoveWithoutTagMineableGameTest extends SFMGameTestDefinition {

    @Override
    public String template() {
        return "3x2x1";
    }

    @Override
    public void testMethod(SFMGameTestHelper helper) {
        new LeftRightManagerTest(helper)
                .setProgram("""
                                    EVERY 20 TICKS DO
                                        INPUT WITHOUT TAG minecraft:mineable/pickaxe FROM left
                                        OUTPUT TO right
                                    END
                                    """)
                .preContents("left", Arrays.asList(
                        enchant(helper, new ItemStack(Items.DIRT, 64), Enchantments.SHARPNESS, 100),
                        new ItemStack(Items.DIRT, 64),
                        new ItemStack(Items.STONE, 64)
                ))
                .postContents("left", Arrays.asList(
                        ItemStack.EMPTY,
                        ItemStack.EMPTY,
                        new ItemStack(Items.STONE, 64)
                ))
                .postContents("right", Arrays.asList(
                        enchant(helper, new ItemStack(Items.DIRT, 64), Enchantments.SHARPNESS, 100),
                        new ItemStack(Items.DIRT, 64)
                ))
                .run();
    }
}
