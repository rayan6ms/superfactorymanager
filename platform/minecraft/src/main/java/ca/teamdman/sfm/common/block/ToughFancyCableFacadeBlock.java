package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.registry.registration.SFMBlockEntities;
import ca.teamdman.sfm.common.registry.registration.SFMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static ca.teamdman.sfm.common.block.ToughCableFacadeBlock.getFacadedToughCableExplosionResistance;

public class ToughFancyCableFacadeBlock extends FancyCableFacadeBlock implements IFacadableBlock, net.minecraft.world.level.block.EntityBlock {
    public ToughFancyCableFacadeBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                defaultBlockState()
                        .setValue(ca.teamdman.sfm.common.facade.FacadeTransparency.FACADE_TRANSPARENCY_PROPERTY, ca.teamdman.sfm.common.facade.FacadeTransparency.TRANSLUCENT)
                        .setValue(net.minecraft.world.level.block.LightBlock.LEVEL, 0)
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(
            BlockPos blockPos,
            BlockState blockState
    ) {
        return SFMBlockEntities.TOUGH_FANCY_CABLE_FACADE.get().create(blockPos, blockState);
    }

    @Override
    public ItemStack getCloneItemStack(
            BlockGetter pLevel,
            BlockPos pPos,
            BlockState pState
    ) {
        return new ItemStack(SFMBlocks.TOUGH_FANCY_CABLE.get());
    }

    @Override
    public IFacadableBlock getNonFacadeBlock() {
        return SFMBlocks.TOUGH_FANCY_CABLE.get();
    }

    @Override
    public IFacadableBlock getFacadeBlock() {
        return SFMBlocks.TOUGH_FANCY_CABLE_FACADE.get();
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getExplosionResistance(
            BlockState state,
            BlockGetter world,
            BlockPos pos,
            Explosion explosion
    ) {

        return getFacadedToughCableExplosionResistance(world, pos, super.getExplosionResistance());
    }

}
