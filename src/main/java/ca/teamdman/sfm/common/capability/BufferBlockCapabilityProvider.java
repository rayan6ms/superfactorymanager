package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.blockentity.BufferBlockEntity;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import org.jetbrains.annotations.Nullable;


/// Version-agnostic way to retrieve the contents of a {@link BufferBlockEntity}.
public class BufferBlockCapabilityProvider implements SFMBlockCapabilityProvider {
    @SuppressWarnings("Convert2Lambda")
    @Override
    public @Nullable IBlockCapabilityProvider<?, @Nullable Direction> createForKind(SFMBlockCapabilityKind<?> capabilityKind) {
        return new IBlockCapabilityProvider<>() {
            @Override
            public @Nullable Object getCapability(
                    Level level,
                    BlockPos pos,
                    BlockState state,
                    @Nullable BlockEntity blockEntity,
                    Direction context
            ) {
                if (!(blockEntity instanceof BufferBlockEntity bufferBlockEntity)) return null;
                ResourceType<?, ?, ?> resourceType = capabilityKind.getResourceType();
                if (resourceType == null) return null;
                return bufferBlockEntity.getContents().getCapability(resourceType);
            }
        };
    }
}
