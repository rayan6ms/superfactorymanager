package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.config.SFMConfig;
import ca.teamdman.sfm.common.config.SFMServerConfig;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfml.ast.Program;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class SFMGameTestMethodHelpers {
    public static ItemStack enchant(
            ItemStack stack,
            Enchantment enchantment,
            int level
    ) {
        EnchantmentHelper.setEnchantments(Map.of(enchantment, level), stack);
        return stack;
    }

    public static void assertTrue(
            boolean condition,
            String message
    ) {
        if (!condition) {
            @SuppressWarnings("UnnecessaryLocalVariable")
            var toThrow = new GameTestAssertException(message);
            // Uncomment below for detailed location information
            // Note that the tests fail every tick using this until they succeed, so you will see logs that make things look like tests are failing if this is uncommented
//            SFM.LOGGER.error("Assertion failed: {}", message, toThrow);
            throw toThrow;
        }
    }

    public static Program compile(String code) {
        AtomicReference<Program> rtn = new AtomicReference<>();
        Program.compile(
                code,
                rtn::set,
                errors -> {
                    throw new GameTestAssertException("Failed to compile program: " + errors
                            .stream()
                            .map(Object::toString)
                            .reduce("", (a, b) -> a + "\n" + b));
                }
        );
        return rtn.get();
    }

    public static void assertManagerRunning(ManagerBlockEntity manager) {
        SFMGameTestMethodHelpers.assertTrue(manager.getDisk() != null, "No disk in manager");
        SFMGameTestMethodHelpers.assertTrue(
                manager.getState() == ManagerBlockEntity.State.RUNNING,
                "Program did not start running " + DiskItem.getErrors(manager.getDisk())
        );
    }

    public static int count(
            Container chest,
            @Nullable Item item
    ) {
        return IntStream.range(0, chest.getContainerSize())
                .mapToObj(chest::getItem)
                .filter(stack -> item == null || stack.getItem() == item)
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    public static int count(
            IItemHandler chest,
            @Nullable Item item
    ) {
        return IntStream.range(0, chest.getSlots())
                .mapToObj(chest::getStackInSlot)
                .filter(stack -> item == null || stack.getItem() == item)
                .mapToInt(ItemStack::getCount)
                .sum();
    }


    public static void falling_anvil_xp_shard_inner(
            GameTestHelper helper,
            int numBooks,
            SFMServerConfig.LevelsToShards configToRestore,
            Vec3 pos,
            ItemStack enchBook,
            Iterator<Pair<SFMServerConfig.LevelsToShards, Integer>> iter
    ) {
        if (!iter.hasNext()) {
            // restore config to value before the test
            SFMConfig.SERVER.levelsToShards.set(configToRestore);
            helper.succeed();
            return;
        }
        var c = iter.next();

        SFMConfig.SERVER.levelsToShards.set(c.first());
        // kill old item entities
        helper
                .getLevel()
                .getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(new BlockPos(1, 4, 1))).inflate(3))
                .forEach(Entity::discard);

        for (int i = 0; i < numBooks; i++) {
            helper
                    .getLevel()
                    .addFreshEntity(new ItemEntity(
                            helper.getLevel(),
                            pos.x, pos.y, pos.z,
                            enchBook,
                            0, 0, 0
                    ));
        }

        helper.setBlock(new BlockPos(1, 3, 1), Blocks.AIR);
        helper.setBlock(new BlockPos(1, 4, 1), Blocks.ANVIL);

        helper.runAfterDelay(20, () -> {
            List<ItemEntity> found = helper
                    .getLevel()
                    .getEntitiesOfClass(
                            ItemEntity.class,
                            new AABB(helper.absolutePos(new BlockPos(1, 4, 1))).inflate(3)
                    );
            assertTrue(
                    found.stream().allMatch(e -> e.getItem().is(SFMItems.EXPERIENCE_SHARD_ITEM.get())),
                    "should only be xp shards"
            );

            var cnt = found.stream().mapToInt(e -> e.getItem().getCount()).sum();
            assertTrue(
                    cnt == c.second(),
                    "bad count for " + c.first().name() + ": expected " + c.second() + " but got " + cnt
            );

            falling_anvil_xp_shard_inner(helper, numBooks, configToRestore, pos, enchBook, iter);
        });
    }
}
