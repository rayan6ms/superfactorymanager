package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.facade.FacadeTransparency;
import ca.teamdman.sfm.common.registry.registration.SFMBlockEntities;
import ca.teamdman.sfm.common.registry.registration.SFMBlocks;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static ca.teamdman.sfm.common.block.ToughCableFacadeBlock.getFacadedToughCableExplosionResistance;

public class ToughFancyCableFacadeBlock extends FancyCableFacadeBlock implements IFacadableBlock, EntityBlock {
    public ToughFancyCableFacadeBlock(Properties properties) {
        super(properties);
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
        return SFMBlockEntities.TOUGH_FANCY_CABLE_FACADE.get().create(blockPos, blockState);
    }

    @Override
    public ItemStack getCloneItemStack(
            @MCVersionDependentBehaviour LevelReader pLevel,
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
