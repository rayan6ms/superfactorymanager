package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.common.component.ItemStackBox;
import ca.teamdman.sfm.common.registry.SFMDataComponents;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class FormItem extends Item {
    public FormItem() {
        super(new Item.Properties());
    }

    public static ItemStack createFormFromReference(ItemStack stack) {
        var formStack = new ItemStack(SFMItems.FORM_ITEM.get());
        formStack.set(SFMDataComponents.FORM_REFERENCE, new ItemStackBox(stack));
        return formStack;
    }

    @MCVersionDependentBehaviour
    public static ItemStack getBorrowedReferenceFromForm(ItemStack stack) {
        return stack.getOrDefault(SFMDataComponents.FORM_REFERENCE, ItemStackBox.EMPTY).stack();
    }

    @MCVersionDependentBehaviour
    public static ItemStack getCopiedReferenceFromForm(ItemStack stack) {
        return getBorrowedReferenceFromForm(stack).copy();
    }

    @Override
    public void appendHoverText(
            ItemStack pStack,
            TooltipContext pContext,
            List<Component> pTooltipComponents,
            TooltipFlag pTooltipFlag
    ) {
        var reference = getBorrowedReferenceFromForm(pStack);
        if (!reference.isEmpty()) {
            pTooltipComponents.addAll(reference.getTooltipLines(pContext, null, pTooltipFlag));
        }
    }
}
