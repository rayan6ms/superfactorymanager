package ca.teamdman.sfm.common.capability.ae2;

import ca.teamdman.sfm.common.capability.SFMBlockCapabilityKind;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityProvider;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityResult;
import ca.teamdman.sfm.common.capability.SFMWellKnownCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class EnergyAcceptorCapabilityProviderMapper implements SFMBlockCapabilityProvider<IEnergyStorage> {
    @Override
    public boolean matchesCapabilityKind(SFMBlockCapabilityKind<?> capabilityKind) {
        return SFMWellKnownCapabilities.ENERGY.equals(capabilityKind);
    }

    @Override
    public SFMBlockCapabilityResult<IEnergyStorage> getCapability(
            SFMBlockCapabilityKind<IEnergyStorage> capabilityKind,
            Level level,
            BlockPos pos,
            BlockState state,
            @Nullable BlockEntity blockEntity,
            @Nullable Direction direction
    ) {
        IEnergyStorage energyStorage = level.getCapability(
                capabilityKind.capabilityKind(),
                pos,
                state,
                blockEntity,
                direction
        );
        if (energyStorage == null) return SFMBlockCapabilityResult.empty();
        return SFMBlockCapabilityResult.of(new EnergyAcceptorEnergyStorageWrapper(energyStorage));
    }

    public record EnergyAcceptorEnergyStorageWrapper(
            IEnergyStorage inner
    ) implements IEnergyStorage {
        @Override
        public int receiveEnergy(
                int maxReceive,
                boolean simulate
        ) {
            return inner.receiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(
                int maxExtract,
                boolean simulate
        ) {
            return inner.extractEnergy(maxExtract, simulate);
        }

        @Override
        public int getEnergyStored() {
            return inner.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            // #322: AE always reports zero, we want SFM to be able to insert energy
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return inner.canExtract();
        }

        @Override
        public boolean canReceive() {
            return inner.canReceive();
        }
    }
}
