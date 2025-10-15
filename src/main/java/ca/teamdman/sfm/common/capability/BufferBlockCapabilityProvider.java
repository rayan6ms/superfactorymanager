package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.blockentity.BufferBlockEntity;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


/// Version-agnostic way to retrieve the contents of a {@link BufferBlockEntity}.
public class BufferBlockCapabilityProvider<STACK, ITEM, CAP> implements @MCVersionDependentBehaviour SFMBlockCapabilityProvider<CAP> {
    private final ResourceType<STACK, ITEM, CAP> resourceType;

    public BufferBlockCapabilityProvider(
            ResourceType<STACK, ITEM, CAP> resourceType
    ) {
        this.resourceType = resourceType;
    }

    @Override
    public boolean matchesCapabilityKind(SFMBlockCapabilityKind<?> capabilityKind) {
        return resourceType.matchesCapabilityKind(capabilityKind);
    }

    @Override
    public SFMBlockCapabilityResult<CAP> getCapability(
            SFMBlockCapabilityKind<CAP> capabilityKind,
            LevelAccessor level,
            BlockPos pos,
            BlockState state,
            @Nullable BlockEntity blockEntity,
            @Nullable Direction direction
    ) {
        if (!(blockEntity instanceof BufferBlockEntity bufferBlockEntity)) return SFMBlockCapabilityResult.empty();
        return bufferBlockEntity.getContents().getCapability(resourceType);
    }
}
