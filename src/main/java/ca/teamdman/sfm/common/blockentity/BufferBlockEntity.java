package ca.teamdman.sfm.common.blockentity;

import ca.teamdman.sfm.common.capability.BufferBlockCapabilityProvider;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityKind;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityResult;
import ca.teamdman.sfm.common.capability.SFMWellKnownCapabilities;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BufferBlockEntity extends BlockEntity {
    private final BufferBlockEntityContents contents = new BufferBlockEntityContents();

    public BufferBlockEntity(
            BlockPos pPos,
            BlockState pBlockState
    ) {
        super(SFMBlockEntities.BUFFER_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(
            @NotNull Capability<T> cap,
            @Nullable Direction side
    ) {
        SFMBlockCapabilityKind<T> capKind = new SFMBlockCapabilityKind<>(cap);
        ResourceType<?, ?, T> resourceType = SFMWellKnownCapabilities.getResourceTypeForCapability(capKind);
        BufferBlockCapabilityProvider<?, ?, T> provider = new BufferBlockCapabilityProvider<>(resourceType);
        SFMBlockCapabilityResult<T> found = provider.getCapability(
                capKind,
                level,
                getBlockPos(),
                getBlockState(),
                this,
                side
        );
        return found.capability();
    }

    public BufferBlockEntityContents getContents() {
        return contents;
    }
}
