package ca.teamdman.sfm.common.blockentity;

import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TunnelledManagerBlockEntity extends ManagerBlockEntity {
    public TunnelledManagerBlockEntity(
            BlockPos blockPos,
            BlockState blockState
    ) {
        super(SFMBlockEntities.TUNNELLED_MANAGER_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
