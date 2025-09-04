package ca.teamdman.sfm.common.capabilityprovidermapper.ae2;

import appeng.blockentity.networking.EnergyAcceptorBlockEntity;
import ca.teamdman.sfm.common.capabilityprovidermapper.CapabilityProviderMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
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
            this.energy = inner.getCapability(ForgeCapabilities.ENERGY)
                    .lazyMap(EnergyAcceptorEnergyStorageWrapper::new);
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(
                @NotNull Capability<T> cap,
                @Nullable Direction side
        ) {
            if (cap == ForgeCapabilities.ENERGY) {
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
