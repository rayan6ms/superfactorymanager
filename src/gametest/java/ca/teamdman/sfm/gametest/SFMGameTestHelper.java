package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityDiscovery;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityKind;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityResult;
import ca.teamdman.sfm.common.capability.SFMWellKnownCapabilities;
import ca.teamdman.sfm.common.program.IProgramHooks;
import ca.teamdman.sfm.common.util.NotStored;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.Locale;

public class SFMGameTestHelper extends GameTestHelper {
    public final SFMDelegatedTestFunction sfmTestDefinition;

    public SFMGameTestHelper(
            SFMDelegatedTestFunction sfmDelegatedTestFunction,
            GameTestHelper helper
    ) {
        super(helper.testInfo);
        this.sfmTestDefinition = sfmDelegatedTestFunction;
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
        var newText = signBlockEntity.getFrontText();
        for (int i = 0; i < text.length; i++) {
            newText = newText.setMessage(i, text[i]);
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

    public void succeedIfManagerDidThingWithoutLagging(
            ManagerBlockEntity manager,
            Runnable assertion
    ) {

        SFMGameTestMethodHelpers.assertManagerRunning(manager);
        manager.addProgramHooks(new IProgramHooks() {
            @Override
            public void onProgramDidSomething(Duration elapsed) {
                // enqueue to run inside the game test harness
                SFMGameTestHelper.this.runAfterDelay(0, () -> {
                    assertion.run();
                    SFMGameTestMethodHelpers.assertTrue(
                            elapsed.toMillis() < 80,
                            "Program took too long to run: took " + NumberFormat
                                    .getInstance(Locale.getDefault())
                                    .format(elapsed.toNanos()) + "ns"
                    );
                    SFMGameTestHelper.this.succeed();
                });
            }
        });
    }

}
