package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.client.ClientKeyHelpers;
import ca.teamdman.sfm.client.ClientScreenHelpers;
import ca.teamdman.sfm.client.handler.LabelGunKeyMappingHandler;
import ca.teamdman.sfm.client.registry.SFMKeyMappings;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.net.ServerboundLabelGunUsePacket;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.util.SFMItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class LabelGunItem extends Item {
    public LabelGunItem() {
        super(new Properties().stacksTo(1).tab(SFMItems.TAB));
    }

    public static void setActiveLabel(
            ItemStack stack,
            @Nullable String label
    ) {
        if (label == null || label.isEmpty()) {
            clearActiveLabel(stack);
        } else {
            LabelPositionHolder.from(stack).addReferencedLabel(label).save(stack);
            stack.getOrCreateTag().putString("sfm:active_label", label);
        }
    }

    public static void clearActiveLabel(
            ItemStack gun
    ) {
        gun.getOrCreateTag().remove("sfm:active_label");
    }

    public static String getActiveLabel(ItemStack stack) {
        //noinspection DataFlowIssue
        return !stack.hasTag() ? "" : stack.getTag().getString("sfm:active_label");
    }

    public static String getNextLabel(
            ItemStack gun,
            int change
    ) {
        var labels = LabelPositionHolder
                .from(gun)
                .labels()
                .keySet()
                .stream()
                .sorted(Comparator.naturalOrder())
                .toList();
        if (labels.isEmpty()) return "";
        var currentLabel = getActiveLabel(gun);

        int currentLabelIndex = 0;
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).equals(currentLabel)) {
                currentLabelIndex = i;
                break;
            }
        }

        int nextLabelIndex = currentLabelIndex + change;
        // ensure going negative wraps around
        nextLabelIndex = ((nextLabelIndex % labels.size()) + labels.size()) % labels.size();

        return labels.get(nextLabelIndex);
    }

    /**
     * Returns the current enum mode for the label gun item.
     */
    public static LabelGunViewMode getViewMode(ItemStack stack) {
        int ordinal = stack.getOrCreateTag().getInt("sfm:label_gun_view_mode");
        // fallback if out of bounds or missing
        if (ordinal < 0 || ordinal >= LabelGunViewMode.values().length) {
            return LabelGunViewMode.SHOW_ALL;
        }
        return LabelGunViewMode.values()[ordinal];
    }

    /**
     * Sets the view mode in NBT.
     */
    public static void setViewMode(ItemStack stack, LabelGunViewMode mode) {
        stack.getOrCreateTag().putInt("sfm:label_gun_view_mode", mode.ordinal());
    }

    public static void cycleViewMode(ItemStack stack) {
        LabelGunViewMode current = getViewMode(stack);
        int nextOrdinal = (current.ordinal() + 1) % LabelGunViewMode.values().length;
        setViewMode(stack, LabelGunViewMode.values()[nextOrdinal]);
    }

    @Override
    public InteractionResult onItemUseFirst(
            ItemStack gun,
            UseOnContext ctx
    ) {
        var level = ctx.getLevel();
        if (level.isClientSide && ctx.getPlayer() != null) {
            boolean pickBlock = ClientKeyHelpers.isKeyDownInWorld(SFMKeyMappings.LABEL_GUN_PICK_BLOCK_MODIFIER_KEY);
            SFMPackets.sendToServer(new ServerboundLabelGunUsePacket(
                    ctx.getHand(),
                    ctx.getClickedPos(),
                    Screen.hasControlDown(),
                    pickBlock,
                    ctx.getPlayer().isShiftKeyDown()
            ));
            if (pickBlock) {
                // we don't want to toggle the overlay if we're using pick-block
                LabelGunKeyMappingHandler.setExternalDebounce();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> lines,
            TooltipFlag detail
    ) {
        if (SFMItemUtils.isClientAndMoreInfoKeyPressed()) {
            Options options = Minecraft.getInstance().options;
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_1.getComponent(
                            options.keyUse.getTranslatedKeyMessage().plainCopy().withStyle(ChatFormatting.AQUA)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_2.getComponent(
                            options.keyUse.getTranslatedKeyMessage().plainCopy().withStyle(ChatFormatting.AQUA)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_3.getComponent(
                            Component.literal("Control").withStyle(ChatFormatting.AQUA)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_4.getComponent(
                            options.keyPickItem.getTranslatedKeyMessage().plainCopy().withStyle(ChatFormatting.AQUA)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_5.getComponent(
                            SFMKeyMappings.CYCLE_LABEL_VIEW_KEY
                                    .get()
                                    .getTranslatedKeyMessage()
                                    .plainCopy()
                                    .withStyle(ChatFormatting.AQUA)
                    ).withStyle(ChatFormatting.GRAY)
            );
        } else {
            SFMItemUtils.appendMoreInfoKeyReminderTextIfOnClient(lines);
        }

        lines.addAll(LabelPositionHolder.from(stack).asHoverText());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        var stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            ClientScreenHelpers.showLabelGunScreen(stack, hand);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        var name = getActiveLabel(stack);
        if (name.isEmpty()) return super.getName(stack);
        return LocalizationKeys.LABEL_GUN_ITEM_NAME_WITH_LABEL
                .getComponent(name)
                .withStyle(ChatFormatting.AQUA);
    }

    public static void clearAll(ItemStack stack) {
        LabelPositionHolder.clear(stack);
        LabelGunItem.setActiveLabel(stack, null);
    }

    public enum LabelGunViewMode {
        SHOW_ALL,
        SHOW_ONLY_ACTIVE_LABEL_AND_TARGETED_BLOCK,
        SHOW_ONLY_TARGETED_BLOCK,
    }
}
