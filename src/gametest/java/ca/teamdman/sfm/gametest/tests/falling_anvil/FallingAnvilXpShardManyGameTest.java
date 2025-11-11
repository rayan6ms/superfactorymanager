package ca.teamdman.sfm.gametest.tests.falling_anvil;

import ca.teamdman.sfm.common.config.SFMConfig;
import ca.teamdman.sfm.common.config.SFMServerConfig.LevelsToShards;
import ca.teamdman.sfm.common.enchantment.SFMEnchantmentCollection;
import ca.teamdman.sfm.common.enchantment.SFMEnchantmentEntry;
import ca.teamdman.sfm.common.enchantment.SFMEnchantmentKey;
import ca.teamdman.sfm.gametest.SFMGameTest;
import ca.teamdman.sfm.gametest.SFMGameTestDefinition;
import ca.teamdman.sfm.gametest.SFMGameTestHelper;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static ca.teamdman.sfm.gametest.SFMGameTestMethodHelpers.falling_anvil_xp_shard_inner;

/**
 * Migrated from SFMCorrectnessGameTests.falling_anvil_xp_shard_many
 */
@SuppressWarnings({
        "RedundantSuppression",
        "DataFlowIssue",
        "OptionalGetWithoutIsPresent",
        "DuplicatedCode",
        "ArraysAsListWithZeroOrOneArgument"
})
@SFMGameTest
public class FallingAnvilXpShardManyGameTest extends SFMGameTestDefinition {

    @Override
    public String template() {
        return "3x4x3";
    }

    @Override
    public void run(SFMGameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 1), Blocks.OBSIDIAN);
        var pos = helper.absoluteVec(new Vec3(1.5, 3.5, 1.5));

        SFMEnchantmentCollection enchantments = new SFMEnchantmentCollection();
        enchantments.add(new SFMEnchantmentEntry(new SFMEnchantmentKey(Enchantments.BLOCK_EFFICIENCY), 2));
        enchantments.add(new SFMEnchantmentEntry(new SFMEnchantmentKey(Enchantments.SHARPNESS), 4));
        ItemStack enchantedBookStack = enchantments.createEnchantedBook();

        var cases = List.of(
                Pair.of(LevelsToShards.JustOne, 10),
                Pair.of(LevelsToShards.EachOne, 20),
                Pair.of(LevelsToShards.SumLevels, 60),
                Pair.of(LevelsToShards.SumLevelsScaledExponentially, 100)
        );

        var currentConfig = SFMConfig.SERVER_CONFIG.levelsToShards.get();
        falling_anvil_xp_shard_inner(
                helper,
                10,
                currentConfig,
                pos,
                enchantedBookStack,
                cases.iterator()
        );
    }
}
