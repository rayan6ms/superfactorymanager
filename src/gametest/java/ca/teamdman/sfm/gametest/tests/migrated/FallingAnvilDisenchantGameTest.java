package ca.teamdman.sfm.gametest.tests.migrated;

import ca.teamdman.sfm.common.util.SFMItemUtils;
import ca.teamdman.sfm.gametest.SFMGameTest;
import ca.teamdman.sfm.gametest.SFMGameTestDefinition;
import ca.teamdman.sfm.gametest.SFMGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Migrated from SFMCorrectnessGameTests.falling_anvil_disenchant
 */
@SuppressWarnings({
        "RedundantSuppression",
        "DataFlowIssue",
        "OptionalGetWithoutIsPresent",
        "DuplicatedCode",
        "ArraysAsListWithZeroOrOneArgument"
})
@SFMGameTest
public class FallingAnvilDisenchantGameTest extends SFMGameTestDefinition {

    @Override
    public String template() {
        return "3x4x3";
    }

    @Override
    public void testMethod(SFMGameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 1), Blocks.OBSIDIAN);
        var pos = helper.absoluteVec(new Vec3(1.5, 3.5, 1.5));
        helper
                .getLevel()
                .addFreshEntity(new ItemEntity(
                        helper.getLevel(),
                        pos.x, pos.y, pos.z,
                        new ItemStack(Items.BOOK, 16),
                        0, 0, 0
                ));
        var axe = new ItemStack(Items.GOLDEN_AXE);
        axe.enchant(helper
                            .getLevel()
                            .registryAccess()
                            .registry(Registries.ENCHANTMENT)
                            .get()
                            .getHolder(Enchantments.EFFICIENCY).get(), 3);
        axe.enchant(helper
                            .getLevel()
                            .registryAccess()
                            .registry(Registries.ENCHANTMENT)
                            .get()
                            .getHolder(Enchantments.SHARPNESS).get(), 2);
        helper.getLevel().addFreshEntity(new ItemEntity(
                helper.getLevel(),
                pos.x, pos.y, pos.z,
                axe,
                0, 0, 0
        ));
        helper.setBlock(new BlockPos(1, 4, 1), Blocks.ANVIL);
        helper.runAfterDelay(20, () -> {
            List<ItemEntity> found = helper
                    .getLevel()
                    .getEntitiesOfClass(
                            ItemEntity.class,
                            new AABB(helper.absolutePos(new BlockPos(1, 4, 1))).inflate(3)
                    );
            boolean foundDisenchantedAxe = found
                    .stream()
                    .anyMatch(e -> SFMItemUtils.isSameItemSameTags(e.getItem(), new ItemStack(Items.GOLDEN_AXE)));
            boolean foundEfficiencyBook = found
                    .stream()
                    .anyMatch(e -> SFMItemUtils.isSameItemSameTags(
                            e.getItem(),
                            EnchantedBookItem.createForEnchantment(new EnchantmentInstance(
                                    helper
                                            .getLevel()
                                            .registryAccess()
                                            .registry(Registries.ENCHANTMENT)
                                            .get()
                                            .getHolder(Enchantments.EFFICIENCY).get(),
                                    3
                            ))
                    ));
            boolean foundSharpnessBook = found
                    .stream()
                    .anyMatch(e -> SFMItemUtils.isSameItemSameTags(
                            e.getItem(),
                            EnchantedBookItem.createForEnchantment(new EnchantmentInstance(
                                    helper
                                            .getLevel()
                                            .registryAccess()
                                            .registry(Registries.ENCHANTMENT)
                                            .get()
                                            .getHolder(Enchantments.SHARPNESS).get(),
                                    2
                            ))
                    ));
            boolean foundRemainingBooks = found
                                                  .stream()
                                                  .filter(e -> e.getItem().is(Items.BOOK))
                                                  .mapToInt(e -> e.getItem().getCount())
                                                  .sum() == 16 - 2;
            if (foundDisenchantedAxe && foundEfficiencyBook && foundSharpnessBook && foundRemainingBooks) {
                helper.succeed();
            } else {
                helper.fail("disenchant failed");
            }
        });
    }
}
