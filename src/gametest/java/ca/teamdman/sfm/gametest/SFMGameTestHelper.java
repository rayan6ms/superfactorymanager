package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityDiscovery;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityKind;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityResult;
import ca.teamdman.sfm.common.capability.SFMWellKnownCapabilities;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.util.NotStored;
import ca.teamdman.sfml.ast.Block;
import ca.teamdman.sfml.ast.Trigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class SFMGameTestHelper extends GameTestHelper {
    public SFMGameTestHelper(GameTestInfo pTestInfo) {

        super(pTestInfo);
    }

    public SFMGameTestHelper(GameTestHelper helper) {

        super(helper.testInfo);
    }

    public <CAP> CAP discoverCapability(
            SFMBlockCapabilityKind<CAP> capKind,
            @NotStored BlockPos localPos,
            @Nullable Direction direction
    ) {

        SFMBlockCapabilityResult<CAP> found = SFMBlockCapabilityDiscovery.discoverCapabilityFromLevel(
                getLevel(),
                capKind,
                absolutePos(localPos),
                direction
        );
        SFMGameTestMethodHelpers.assertTrue(found.isPresent(), "No " + capKind.getName() + " found at " + localPos);
        return found.unwrap();
    }

    public IFluidHandler getFluidHandler(
            @NotStored BlockPos pos,
            @Nullable Direction direction
    ) {

        return discoverCapability(
                SFMWellKnownCapabilities.FLUID_HANDLER,
                pos,
                direction
        );
    }

    public IItemHandler getItemHandler(
            @NotStored BlockPos pos,
            @Nullable Direction direction
    ) {

        return discoverCapability(
                SFMWellKnownCapabilities.ITEM_HANDLER,
                pos,
                direction
        );
    }

    public void setSignText(
            BlockPos signPos,
            Component... text
    ) {

        BlockEntity blockEntity = getBlockEntity(signPos);
        if (!(blockEntity instanceof SignBlockEntity signBlockEntity)) {
            fail("Block entity was not an instance of SignBlockEntity, got " + blockEntity, signPos);
            return;
        }
        if (text.length > 4) {
            fail("Text array was too long, max length is 4, got " + text.length, signPos);
            return;
        }
        var newText = new SignText();
        for (int i = 0; i < text.length; i++) {
            newText.setMessage(i , text[i]);
        }
        signBlockEntity.setText(newText, false);
        signBlockEntity.setText(newText, true);
    }

    public IEnergyStorage getEnergyStorage(
            @NotStored BlockPos pos,
            @Nullable Direction direction
    ) {

        return discoverCapability(
                SFMWellKnownCapabilities.ENERGY,
                pos,
                direction
        );
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

        long bound = timeoutTicks - getTick();
        for (long i = getTick() + 1; i < bound; i++) {
            /// We cannot reuse the {@link Runnable} object because it is used as the key in a dict.
            runAfterDelay(
                    i, () -> {
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
                    }
            );
        }
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
