package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.capability.SFMWellKnownCapabilities;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.NotStored;
import ca.teamdman.sfml.ast.Block;
import ca.teamdman.sfml.ast.Trigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

public class SFMGameTestHelper extends GameTestHelper {
    public SFMGameTestHelper(GameTestInfo pTestInfo) {
        super(pTestInfo);
    }
    public SFMGameTestHelper(GameTestHelper helper) {
        super(helper.testInfo);
    }

    @MCVersionDependentBehaviour
    public IFluidHandler getFluidHandler(
            @NotStored BlockPos pos,
            @Nullable Direction direction
    ) {
        BlockEntity blockEntity = getBlockEntity(pos);
        SFMGameTestMethodHelpers.assertTrue(blockEntity != null, "No block entity found at " + pos);
        Optional<IFluidHandler> found = blockEntity
                .getCapability(SFMWellKnownCapabilities.FLUID_HANDLER.capabilityKind(), direction)
                .resolve();
        SFMGameTestMethodHelpers.assertTrue(found.isPresent(), "No fluid handler found at " + pos);
        return found.get();
    }

    @MCVersionDependentBehaviour
    public IItemHandler getItemHandler(
            @NotStored BlockPos pos,
            @Nullable Direction direction
    ) {
        BlockEntity blockEntity = getBlockEntity(pos);
        SFMGameTestMethodHelpers.assertTrue(blockEntity != null, "No block entity found at " + pos);
        Optional<IItemHandler> found = blockEntity
                .getCapability(SFMWellKnownCapabilities.ITEM_HANDLER.capabilityKind(), direction)
                .resolve();
        SFMGameTestMethodHelpers.assertTrue(found.isPresent(), "No item handler found at " + pos);
        return found.get();
    }

    public IItemHandler getItemHandler(
            @NotStored BlockPos pos
    ) {
        return getItemHandler(pos, null);
    }

    public void assertManagerDidThingWithoutLagging(
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
                .range(getTick() + 1, timeoutTicks - getTick())
                .forEach(i -> runAfterDelay(i, () -> {
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

    public void succeedIfManagerDidThingWithoutLagging(
            ManagerBlockEntity manager,
            Runnable assertion
    ) {
        // a nice thing about this method is that you can change a program from
        // EVERY 20 TICKS DO
        // to
        // EVERY REDSTONE PULSE DO
        // and it will patiently wait
        assertManagerDidThingWithoutLagging(
                manager,
                assertion,
                this::succeed
        );
    }
}
