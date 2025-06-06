package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.config.SFMConfig;
import ca.teamdman.sfm.common.config.SFMServerConfig;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.NotStored;
import ca.teamdman.sfml.ast.Block;
import ca.teamdman.sfml.ast.Program;
import ca.teamdman.sfml.ast.Trigger;
import it.unimi.dsi.fastutil.Pair;
import mekanism.api.RelativeSide;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.tile.TileEntityBin;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class SFMGameTestMethodHelpers {
    public static ItemStack enchant(
            GameTestHelper helper,
            ItemStack stack,
            ResourceKey<Enchantment> enchantment,
            int level
    ) {
        Registry<Enchantment> enchantmentRegistry = helper.getLevel().registryAccess().registry(Registries.ENCHANTMENT).get();
        EnchantmentHelper.updateEnchantments(stack, xs -> {
            xs.set(enchantmentRegistry.getHolderOrThrow(enchantment), level);
        });
        return stack;
    }

    public static void assertTrue(
            boolean condition,
            String message
    ) {
        if (!condition) {
            throw new GameTestAssertException(message);
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

    public static void succeedIfManagerDidThingWithoutLagging(
            GameTestHelper helper,
            ManagerBlockEntity manager,
            Runnable assertion
    ) {
        // a nice thing about this method is that you can change a program from
        // EVERY 20 TICKS DO
        // to
        // EVERY REDSTONE PULSE DO
        // and it will patiently wait
        assertManagerDidThingWithoutLagging(
                helper,
                manager,
                assertion,
                helper::succeed
        );
    }

    public static void assertManagerDidThingWithoutLagging(
            GameTestHelper helper,
            ManagerBlockEntity manager,
            Runnable assertion,
            Runnable onSuccess
    ) {
        SFMGameTestMethodHelpers.assertManagerRunning(manager); // the program should already be compiled so we can monkey patch it
        manager.enableRebuildProgramLock();
        var hasExecuted = new AtomicBoolean(false);
        var startTime = new AtomicLong();
        var endTime = new AtomicLong();
        List<Trigger> triggers = Objects.requireNonNull(manager.getProgram()).triggers();
        var oldFirstTrigger = triggers.get(0);
        long timeoutTicks = 200;

        Trigger startTimerTrigger = new Trigger() {
            @Override
            public boolean shouldTick(ProgramContext context) {
                return oldFirstTrigger != null
                       ? oldFirstTrigger.shouldTick(context)
                       : context.getManager().getTick() % 20 == 0;
            }

            @Override
            public void tick(ProgramContext context) {
                startTime.set(System.nanoTime());
            }

            @Override
            public Block getBlock() {
                return new Block(Collections.emptyList());
            }
        };

        Trigger endTimerTrigger = new Trigger() {
            @Override
            public boolean shouldTick(ProgramContext context) {
                return oldFirstTrigger != null
                       ? oldFirstTrigger.shouldTick(context)
                       : context.getManager().getTick() % 20 == 0;
            }

            @Override
            public void tick(ProgramContext context) {
                if (!hasExecuted.get()) {
                    hasExecuted.set(true);
                    endTime.set(System.nanoTime());
                }
            }

            @Override
            public Block getBlock() {
                return new Block(Collections.emptyList());
            }
        };

        triggers.add(0, startTimerTrigger);
        triggers.add(endTimerTrigger);


        LongStream
                .range(helper.getTick() + 1, timeoutTicks - helper.getTick())
                .forEach(i -> helper.runAfterDelay(i, () -> {
                    if (hasExecuted.get()) {
                        triggers.remove(startTimerTrigger);
                        triggers.remove(endTimerTrigger);
                        assertion.run();
                        SFMGameTestMethodHelpers.assertTrue(
                                endTime.get() - startTime.get() < 80_000_000,
                                "Program took too long to run: took " + NumberFormat
                                        .getInstance(Locale.getDefault())
                                        .format(endTime.get() - startTime.get()) + "ns"
                        );
                        hasExecuted.set(false); // prevent the assertion from running again
                        onSuccess.run();
                    }
                }));
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

    public static IItemHandler getItemHandler(
            GameTestHelper helper,
            @NotStored BlockPos pos
    ) {
        BlockPos worldPos = helper.absolutePos(pos);
        var found = helper
                .getLevel()
                .getCapability(Capabilities.ItemHandler.BLOCK, worldPos, Direction.DOWN);
        SFMGameTestMethodHelpers.assertTrue(found != null, "No item handler found at " + worldPos);
        return found;
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

    @SuppressWarnings("unchecked")
    public static <T extends TileEntityMekanism> T getAndPrepMekTile(GameTestHelper helper, BlockPos mekanismPos) {
        var tile = helper.getBlockEntity(mekanismPos);
        if (tile instanceof TileEntityConfigurableMachine mek) {
            set_all_io(mek.getConfig());
            return (T) mek;
        } else if (tile instanceof TileEntityBin bin) {
        }
        return (T) tile;
    }

    public static void set_all_io(TileComponentConfig config) {
        for (TransmissionType type : TransmissionType.values()) {
            ConfigInfo info = config.getConfig(type);
            if (info != null) {
                for (RelativeSide side : RelativeSide.values()) {
                    info.setDataType(DataType.INPUT_OUTPUT, side);
                    config.sideChanged(type, side);
                }
            }
        }
    }

}
