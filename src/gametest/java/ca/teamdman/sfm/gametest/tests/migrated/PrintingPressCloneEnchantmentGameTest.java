package ca.teamdman.sfm.gametest.tests.migrated;

import ca.teamdman.sfm.common.blockentity.PrintingPressBlockEntity;
import ca.teamdman.sfm.common.item.FormItem;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.SFMItemUtils;
import ca.teamdman.sfm.gametest.SFMGameTest;
import ca.teamdman.sfm.gametest.SFMGameTestDefinition;
import ca.teamdman.sfm.gametest.SFMGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import static ca.teamdman.sfm.gametest.SFMGameTestMethodHelpers.assertTrue;
import static ca.teamdman.sfm.gametest.SFMGameTestMethodHelpers.getItemHandler;

/**
 * Migrated from SFMCorrectnessGameTests.printing_press_clone_enchantment
 */
@SuppressWarnings({
        "RedundantSuppression",
        "DataFlowIssue",
        "OptionalGetWithoutIsPresent",
        "DuplicatedCode",
        "ArraysAsListWithZeroOrOneArgument"
})
@SFMGameTest
public class PrintingPressCloneEnchantmentGameTest extends SFMGameTestDefinition {

    @Override
    public String template() {
        return "3x4x3";
    }

    @Override
    public void testMethod(SFMGameTestHelper helper) {
        var printingPos = new BlockPos(1, 2, 1);
        var pistonPos = new BlockPos(1, 4, 1);
        var woodPos = new BlockPos(0, 4, 1);
        var buttonPos = new BlockPos(0, 4, 0);
        var chestPos = new BlockPos(0, 2, 1);

        helper.setBlock(printingPos, SFMBlocks.PRINTING_PRESS_BLOCK.get());
        helper.setBlock(pistonPos, Blocks.PISTON.defaultBlockState().setValue(DirectionalBlock.FACING, Direction.DOWN));
        helper.setBlock(woodPos, Blocks.OAK_PLANKS);
        helper.setBlock(buttonPos, Blocks.STONE_BUTTON);
        helper.setBlock(chestPos, SFMBlocks.TEST_BARREL_BLOCK.get());

        var printingPress = (PrintingPressBlockEntity) helper.getBlockEntity(printingPos);
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(SFMItems.EXPERIENCE_GOOP_ITEM.get(), 10));
        BlockState pressState = helper.getBlockState(printingPos);
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(printingPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOOK));
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(printingPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );
        ItemStack reference = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(
                Enchantments.SHARPNESS,
                3
        ));
        player.setItemInHand(InteractionHand.MAIN_HAND, FormItem.getForm(reference));
        pressState.getBlock().use(
                pressState,
                helper.getLevel(),
                helper.absolutePos(printingPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );

        BlockState buttonState = helper.getBlockState(buttonPos);
        buttonState.getBlock().use(
                buttonState,
                helper.getLevel(),
                helper.absolutePos(buttonPos),
                player,
                InteractionHand.MAIN_HAND,
                new BlockHitResult(
                        new Vec3(0.5, 0.5, 0.5),
                        Direction.UP,
                        helper.absolutePos(printingPos),
                        false
                )
        );

        helper.runAfterDelay(5, () -> {
            pressState.getBlock().use(
                    pressState,
                    helper.getLevel(),
                    helper.absolutePos(printingPos),
                    player,
                    InteractionHand.MAIN_HAND,
                    new BlockHitResult(
                            new Vec3(0.5, 0.5, 0.5),
                            Direction.UP,
                            helper.absolutePos(printingPos),
                            false
                    )
            );
            ItemStack held = player.getMainHandItem();
            if (SFMItemUtils.isSameItemSameTags(held, reference)) {
                var chest = getItemHandler(helper, chestPos);
                chest.insertItem(0, held, false);
                assertTrue(printingPress.getInk().getCount() == 9, "Ink was not consumed properly");
                assertTrue(printingPress.getPaper().isEmpty(), "Paper was not consumed");
                assertTrue(!printingPress.getForm().isEmpty(), "Form should not be consumed");
                helper.succeed();
            } else {
                helper.fail("cloned item wasnt same");
            }
        });
    }
}
