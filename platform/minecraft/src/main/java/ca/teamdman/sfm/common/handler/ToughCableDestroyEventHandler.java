package ca.teamdman.sfm.common.handler;

import ca.teamdman.sfm.common.block.ToughCableFacadeBlock;
import ca.teamdman.sfm.common.block.ToughFancyCableFacadeBlock;
import ca.teamdman.sfm.common.blockentity.IFacadeBlockEntity;
import ca.teamdman.sfm.common.event_bus.SFMSubscribeEvent;
import ca.teamdman.sfm.common.facade.FacadeData;
import ca.teamdman.sfm.common.util.SFMEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;

public class ToughCableDestroyEventHandler {
    @SFMSubscribeEvent
    public static void onDestroy(LivingDestroyBlockEvent event) {
        Block block = event.getState().getBlock();
        // If it is a tough cable block
        if (block instanceof ToughCableFacadeBlock || block instanceof ToughFancyCableFacadeBlock) {
            LivingEntity entity = event.getEntity();
            Level level = SFMEntityUtils.getLevel(entity);
            BlockPos blockPos = event.getPos();
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            // If it has a facade
            if (blockEntity instanceof IFacadeBlockEntity facadeBlockEntity) {
                FacadeData facadeData = facadeBlockEntity.getFacadeData();
                if (facadeData != null) {
                    BlockState mimickingBlockState = facadeData.facadeBlockState();
                    // If the block the facade is mimicking cannot be destroyed by the entity
                    if (!mimickingBlockState.canEntityDestroy(level, blockPos, entity)) {
                        // Cancel the destruction
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
