package ca.teamdman.sfm.common.capability.ae2;

import appeng.blockentity.networking.EnergyAcceptorBlockEntity;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityKind;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityProvider;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityResult;
import ca.teamdman.sfm.common.capability.SFMWellKnownCapabilities;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyAcceptorBlockCapabilityProvider implements SFMBlockCapabilityProvider<IEnergyStorage> {
    @Override
    public boolean matchesCapabilityKind(SFMBlockCapabilityKind<?> capabilityKind) {
        return SFMWellKnownCapabilities.ENERGY.equals(capabilityKind);
    }

    @MCVersionDependentBehaviour
    @Override
    public SFMBlockCapabilityResult<IEnergyStorage> getCapability(
            SFMBlockCapabilityKind<IEnergyStorage> capabilityKind,
            LevelAccessor level,
            BlockPos pos,
            BlockState state,
            @Nullable BlockEntity blockEntity,
            @Nullable Direction direction
    ) {
        if (blockEntity instanceof EnergyAcceptorBlockEntity energyAcceptor) {
            return SFMBlockCapabilityResult.of(
                    energyAcceptor.getCapability(SFMWellKnownCapabilities.ENERGY.capabilityKind())
                            .lazyMap(EnergyAcceptorEnergyStorageWrapper::new)

            );
        } else {
            return SFMBlockCapabilityResult.empty();
        }
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
