package ca.teamdman.sfm.gametest.tests.falling_anvil;

import ca.teamdman.sfm.common.config.SFMServerConfig.LevelsToShards;
import ca.teamdman.sfm.common.enchantment.SFMEnchantmentAliases;
import ca.teamdman.sfm.common.enchantment.SFMEnchantmentCollection;
import ca.teamdman.sfm.common.enchantment.SFMEnchantmentEntry;
import ca.teamdman.sfm.common.handler.FallingAnvilHandler;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.SFMItemUtils;
import ca.teamdman.sfm.gametest.SFMGameTest;
import ca.teamdman.sfm.gametest.SFMGameTestDefinition;
import ca.teamdman.sfm.gametest.SFMGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static ca.teamdman.sfm.gametest.SFMGameTestMethodHelpers.assertTrue;

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

    private static final int CHEST_SPACING = 2;

    private static final int CHEST_SLOTS = 27;

    private static final List<Enchantment> ENCHANTMENT_POOL = List.of(
            SFMEnchantmentAliases.EFFICIENCY,
            Enchantments.SHARPNESS,
            SFMEnchantmentAliases.FORTUNE,
            Enchantments.UNBREAKING,
            Enchantments.MENDING
    );

    @Override
    public String template() {

        int width = LevelsToShards.values().length * CHEST_SPACING + 1;
        return width + "x3x3";
    }

    @Override
    public void run(SFMGameTestHelper helper) {

        RandomSource random = helper.getLevel().getRandom();
        TestParameters parameters = chooseParameters(helper, random);

        LevelsToShards[] modes = LevelsToShards.values();
        EnumMap<LevelsToShards, Long> expectedByMode = new EnumMap<>(LevelsToShards.class);

        for (LevelsToShards mode : modes) {
            long predicted = FallingAnvilHandler.getShardCountForEnchantments(mode, parameters.enchantments());
            long expected = computeExpectedShards(mode, parameters.enchantments());
            assertTrue(predicted == expected, "unexpected shard prediction for " + mode);
            expectedByMode.put(mode, predicted * parameters.bookCount());
        }

        for (int index = 0; index < modes.length; index++) {
            LevelsToShards mode = modes[index];
            long expectedShards = expectedByMode.get(mode);

            BlockPos chestPos = new BlockPos(index * CHEST_SPACING + 1, 2, 1);

            helper.setBlock(chestPos, SFMBlocks.TEST_BARREL_BLOCK.get());
            helper.setBlock(chestPos.above(), Blocks.OAK_SIGN.defaultBlockState().setValue(StandingSignBlock.ROTATION, 8));
            helper.setSignText(
                    chestPos.above(),
                    Component.literal(mode.toString()),
                    Component.literal(expectedShards + " shards")
            );

            IItemHandler handler = helper.getItemHandler(chestPos);

            fill(handler, parameters.bookTemplate(), parameters.bookCount());

            fill(handler, new ItemStack(SFMItems.EXPERIENCE_SHARD_ITEM.get()), expectedShards);

            long storedBooks = count(handler, parameters.bookTemplate());
            assertTrue(storedBooks == parameters.bookCount(), "chest did not receive expected book count");

            long storedShards = count(handler, new ItemStack(SFMItems.EXPERIENCE_SHARD_ITEM.get()));
            assertTrue(
                    storedShards == expectedShards,
                    "chest for " + mode + " stored " + storedShards + " shards, expected " + expectedShards
            );
        }

        helper.succeed();
    }

    private static TestParameters chooseParameters(
            SFMGameTestHelper helper,
            RandomSource random
    ) {

        for (int attempt = 0; attempt < 64; attempt++) {
            SFMEnchantmentCollection enchantments = pickRandomEnchantments(helper, random);
            ItemStack bookTemplate = enchantments.createEnchantedBook();
            int bookCount = random.nextIntBetweenInclusive(8, 20);

            long shardsPerBook = computeExpectedShards(LevelsToShards.SumLevelsScaledExponentially, enchantments);
            long totalShards = shardsPerBook * bookCount;

            int bookSlots = stackRequirement(bookCount, bookTemplate.getMaxStackSize());
            int shardSlots = stackRequirement(totalShards, 64);

            if (totalShards > 64 && bookSlots + shardSlots <= CHEST_SLOTS) {
                return new TestParameters(enchantments, bookTemplate, bookCount);
            }
        }

        SFMEnchantmentCollection fallback = new SFMEnchantmentCollection();
        fallback.add(helper.createEnchantmentEntry(Enchantments.BLOCK_EFFICIENCY, 2));
        fallback.add(helper.createEnchantmentEntry(Enchantments.SHARPNESS, 4));
        ItemStack bookTemplate = fallback.createEnchantedBook();
        return new TestParameters(fallback, bookTemplate, 10);
    }

    private static SFMEnchantmentCollection pickRandomEnchantments(
            SFMGameTestHelper helper,
            RandomSource random
    ) {

        List<Enchantment> pool = new ArrayList<>(ENCHANTMENT_POOL);
        for (int i = pool.size() - 1; i > 0; i--) {
            int swapIndex = random.nextInt(i + 1);
            Enchantment tmp = pool.get(i);
            pool.set(i, pool.get(swapIndex));
            pool.set(swapIndex, tmp);
        }

        int selectionSize = random.nextIntBetweenInclusive(1, pool.size());
        SFMEnchantmentCollection enchantments = new SFMEnchantmentCollection();
        for (int i = 0; i < selectionSize; i++) {
            Enchantment enchantment = pool.get(i);
            int maxLevel = Math.max(1, enchantment.getMaxLevel());
            int level = random.nextIntBetweenInclusive(1, maxLevel);
            SFMEnchantmentEntry entry = helper.createEnchantmentEntry(enchantment, level);
            enchantments.add(entry);
        }
        return enchantments;
    }

    private static long computeExpectedShards(
            LevelsToShards mode,
            SFMEnchantmentCollection enchantments
    ) {

        return switch (mode) {
            case JustOne -> 1;
            case EachOne -> enchantments.size();
            case SumLevels -> {
                long sum = 0;
                for (SFMEnchantmentEntry enchantment : enchantments) {
                    sum += enchantment.level();
                }
                yield sum;
            }
            case SumLevelsScaledExponentially -> {
                long sum = 0;
                for (SFMEnchantmentEntry enchantment : enchantments) {
                    int level = enchantment.level();
                    long increment = 1L << Math.max(0, level - 1);
                    sum = Math.min(Integer.MAX_VALUE, sum + increment);
                }
                yield sum;
            }
        };
    }

    private static int stackRequirement(
            long totalCount,
            int maxStackSize
    ) {

        return (int) ((totalCount + maxStackSize - 1) / maxStackSize);
    }

    private static void fill(
            IItemHandler handler,
            ItemStack template,
            long totalCount
    ) {

        long remaining = totalCount;
        while (remaining > 0) {
            int maxStack = Math.min(template.getMaxStackSize(), 64);
            int toInsert = (int) Math.min(maxStack, remaining);
            ItemStack stack = template.copy();
            stack.setCount(toInsert);
            ItemStack leftover = ItemHandlerHelper.insertItemStacked(handler, stack, false);
            assertTrue(leftover.isEmpty(), "insufficient space to store items");
            remaining -= toInsert;
        }
    }

    private static long count(
            IItemHandler handler,
            ItemStack template
    ) {

        long total = 0;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (SFMItemUtils.isSameItemSameTags(stack, template)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private record TestParameters(
            SFMEnchantmentCollection enchantments,

            ItemStack bookTemplate,

            int bookCount
    ) {

    }

}
