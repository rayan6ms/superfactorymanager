package ca.teamdman.sfm.common.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

public class DebugStickItem extends Item {
    public DebugStickItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (Items.NETHERITE_AXE.useOn(pContext).consumesAction()) return InteractionResult.SUCCESS;
        if (Items.SHEARS.useOn(pContext).consumesAction()) return InteractionResult.SUCCESS;
        if (Items.NETHERITE_HOE.useOn(pContext).consumesAction()) return InteractionResult.SUCCESS;
        return super.useOn(pContext);
    }

    @Override
    public boolean canPerformAction(
            ItemStack stack,
            ToolAction toolAction
    ) {
        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return true;
    }
}
