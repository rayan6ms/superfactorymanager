package ca.teamdman.sfm.common.capability.ae2;

import appeng.blockentity.networking.EnergyAcceptorBlockEntity;
import ca.teamdman.sfm.common.capability.CapabilityProviderMapper;
import ca.teamdman.sfm.common.capability.SFMWellKnownCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyAcceptorCapabilityProviderMapper implements CapabilityProviderMapper {
    @Override
    public @Nullable ICapabilityProvider getProviderFor(
            LevelAccessor level,
            BlockPos pos
    ) {
        if (level.getBlockEntity(pos) instanceof EnergyAcceptorBlockEntity blockEntity) {
            return new EnergyAcceptorCapabilityProvider(blockEntity);
        } else {
            return null;
        }
    }

    public static final class EnergyAcceptorCapabilityProvider implements ICapabilityProvider {
        private final LazyOptional<EnergyAcceptorEnergyStorageWrapper> energy;
        private final EnergyAcceptorBlockEntity inner;

        public EnergyAcceptorCapabilityProvider(
                EnergyAcceptorBlockEntity inner
        ) {
            this.inner = inner;
            this.energy = inner.getCapability(SFMWellKnownCapabilities.ENERGY.capability())
                    .lazyMap(EnergyAcceptorEnergyStorageWrapper::new);
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(
                @NotNull Capability<T> cap,
                @Nullable Direction side
        ) {
            if (cap == SFMWellKnownCapabilities.ENERGY.capability()) {
                return energy.cast();
            } else {
                return inner.getCapability(cap, side);
            }
        }
    }

    public record EnergyAcceptorEnergyStorageWrapper(
            IEnergyStorage inner
    ) implements IEnergyStorage{
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
