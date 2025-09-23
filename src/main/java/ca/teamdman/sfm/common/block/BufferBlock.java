package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class BufferBlock extends BaseEntityBlock {
    public static final EnumProperty<ContainedResource> CONTAINED_RESOURCE = EnumProperty.create(
            "resource",
            ContainedResource.class
    );

    public BufferBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(getStateDefinition().any().setValue(CONTAINED_RESOURCE, ContainedResource.Item));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(
            BlockPos pPos,
            BlockState pState
    ) {
        return SFMBlockEntities.BUFFER_BLOCK_ENTITY.get().create(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(CONTAINED_RESOURCE, ContainedResource.Unknown);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(CONTAINED_RESOURCE);
    }

    public enum ContainedResource implements StringRepresentable {
        Item,
        Fluid,
        Energy,
        Chemical,
        Unknown;

        @Override
        public String getSerializedName() {
            return switch (this) {
                case Item -> "item";
                case Fluid -> "fluid";
                case Energy -> "energy";
                case Chemical -> "chemical";
                case Unknown -> "unknown";
            };
        }
    }
}
