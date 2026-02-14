package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.facade.FacadeTransparency;
import ca.teamdman.sfm.common.registry.registration.SFMBlockEntities;
import ca.teamdman.sfm.common.registry.registration.SFMBlocks;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TunnelledFancyCableFacadeBlock extends FancyCableFacadeBlock implements EntityBlock, IFacadableBlock {
    public TunnelledFancyCableFacadeBlock(Properties properties) {
        super(properties.lightLevel(LightBlock.LIGHT_EMISSION));
        registerDefaultState(
                defaultBlockState()
                        .setValue(FacadeTransparency.FACADE_TRANSPARENCY_PROPERTY, FacadeTransparency.TRANSLUCENT)
                        .setValue(LightBlock.LEVEL, 0)
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(
            BlockPos blockPos,
            BlockState blockState
    ) {
        return SFMBlockEntities.TUNNELLED_FANCY_CABLE_FACADE.get().create(blockPos, blockState);
    }

    @Override
    public ItemStack getCloneItemStack(
            @MCVersionDependentBehaviour LevelReader pLevel,
            BlockPos pPos,
            BlockState pState
    ) {
        return new ItemStack(SFMBlocks.TUNNELLED_FANCY_CABLE.get());
    }

    @Override
    public IFacadableBlock getNonFacadeBlock() {
        return SFMBlocks.TUNNELLED_FANCY_CABLE.get();
    }

    @Override
    public IFacadableBlock getFacadeBlock() {
        return SFMBlocks.TUNNELLED_FANCY_CABLE_FACADE.get();
    }
}
