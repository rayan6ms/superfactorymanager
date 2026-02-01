package ca.teamdman.sfm.common.blockentity;

import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TunnelledCableBlockEntity extends BlockEntity {
    public TunnelledCableBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SFMBlockEntities.TUNNELLED_CABLE_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
