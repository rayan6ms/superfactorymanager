package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.registry.registration.SFMBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class PrintingPressBlockItem extends BlockItem {
    public PrintingPressBlockItem() {
        super(SFMBlocks.PRINTING_PRESS.get(), new Properties());
    }

    @Override
    public void appendHoverText(
            ItemStack pStack,
            TooltipContext pContext,
            List<Component> pTooltipComponents,
            TooltipFlag pTooltipFlag
    ) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        pTooltipComponents.add(LocalizationKeys.PRINTING_PRESS_TOOLTIP.getComponent().withStyle(ChatFormatting.GRAY));
    }
}
