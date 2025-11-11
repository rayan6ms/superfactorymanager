package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.config.SFMConfig;
import ca.teamdman.sfm.common.config.SFMServerConfig;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.NotStored;
import ca.teamdman.sfml.ast.Program;
import ca.teamdman.sfml.program_builder.ProgramBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class SFMGameTestMethodHelpers {

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

        new ProgramBuilder(code)
                .useCache(false)
                .build()
                .caseSuccess((program, metadata) -> rtn.set(program))
                .caseFailure(result -> {
                    throw new GameTestAssertException("Failed to compile program: " + result.metadata().errors()
                            .stream()
                            .map(Object::toString)
                            .reduce("", (a, b) -> a + "\n" + b));
                });
        return rtn.get();
    }

    public static void assertManagerRunning(ManagerBlockEntity manager) {

        SFMGameTestMethodHelpers.assertTrue(manager.getDisk() != null, "No disk in manager");
        SFMGameTestMethodHelpers.assertTrue(
                manager.getState() == ManagerBlockEntity.State.RUNNING,
                "Program did not start running " + DiskItem.getErrors(manager.getDisk())
        );
    }

    public static IItemHandler getItemHandler(
            GameTestHelper helper,
            @NotStored BlockPos pos
    ) {
        BlockEntity blockEntity = helper
                .getBlockEntity(pos);
        SFMGameTestMethodHelpers.assertTrue(blockEntity != null, "No block entity found at " + pos);
        Optional<IItemHandler> found = blockEntity
                .getCapability(Capabilities.ITEM_HANDLER)
                .resolve();
        SFMGameTestMethodHelpers.assertTrue(found.isPresent(), "No item handler found at " + pos);
        return found.get();
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
            SFMConfig.SERVER_CONFIG.levelsToShards.set(configToRestore);
            helper.succeed();
            return;
        }
        var testCase = iter.next();
        SFMServerConfig.LevelsToShards levelsToShards = testCase.first();
        Integer expectedCount = testCase.second();

        SFMConfig.SERVER_CONFIG.levelsToShards.set(levelsToShards);
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

        helper.runAfterDelay(
                20,
                () -> {
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

                    var count = found.stream().mapToInt(itemEntity -> itemEntity.getItem().getCount()).sum();
                    assertTrue(
                            count == expectedCount,
                            "bad count for " + levelsToShards.name() + ": expected " + expectedCount + " but got " + count
                    );

                    falling_anvil_xp_shard_inner(helper, numBooks, configToRestore, pos, enchBook, iter);
                }
        );
    }

}
