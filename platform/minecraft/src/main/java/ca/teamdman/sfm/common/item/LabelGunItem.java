package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.client.ClientLabelGunWarningHelper;
import ca.teamdman.sfm.client.handler.LabelGunKeyMappingHandler;
import ca.teamdman.sfm.client.registry.SFMKeyMappings;
import ca.teamdman.sfm.client.screen.SFMScreenChangeHelpers;
import ca.teamdman.sfm.common.label.LabelPositionHolder;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.net.ServerboundLabelGunUsePacket;
import ca.teamdman.sfm.common.registry.registration.SFMDataComponents;
import ca.teamdman.sfm.common.util.SFMItemUtils;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
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
import java.util.Locale;
import java.util.function.IntFunction;

public class LabelGunItem extends Item {
    public LabelGunItem(Properties properties) {
        super(properties);
    }

    public static void setActiveLabel(
            ItemStack stack,
            @Nullable String label
    ) {
        if (label == null || label.isEmpty()) {
            clearActiveLabel(stack);
        } else {
            LabelPositionHolder.from(stack).addReferencedLabel(label).save(stack);
            stack.set(SFMDataComponents.ACTIVE_LABEL, label);

        }
    }

    public static String getActiveLabel(ItemStack stack) {
        return stack.getOrDefault(SFMDataComponents.ACTIVE_LABEL, "");
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


    public static void clearActiveLabel(
            ItemStack gun
    ) {
        gun.remove(SFMDataComponents.ACTIVE_LABEL);
    }

    /**
     * Returns the current enum mode for the label gun item.
     */
    public static LabelGunViewMode getViewMode(ItemStack stack) {
        return stack.getOrDefault(SFMDataComponents.LABEL_GUN_VIEW_MODE, LabelGunViewMode.SHOW_ALL);
    }

    /**
     * Sets the view mode in NBT.
     */
    public static void setViewMode(ItemStack stack, LabelGunViewMode mode) {
        stack.set(SFMDataComponents.LABEL_GUN_VIEW_MODE, mode);
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
        Player player = ctx.getPlayer();
        if (level.isClientSide && player != null) {
            boolean pickBlock = SFMKeyMappings.isKeyDown(SFMKeyMappings.LABEL_GUN_PICK_BLOCK_MODIFIER_KEY);
            boolean contiguous = SFMKeyMappings.isKeyDown(SFMKeyMappings.LABEL_GUN_CONTIGUOUS_MODIFIER_KEY);
            boolean clear = SFMKeyMappings.isKeyDown(SFMKeyMappings.LABEL_GUN_CLEAR_MODIFIER_KEY);
            boolean pull = SFMKeyMappings.isKeyDown(SFMKeyMappings.LABEL_GUN_PULL_MODIFIER_KEY);
            boolean targetManager = SFMKeyMappings.isKeyDown(SFMKeyMappings.LABEL_GUN_TARGET_MANAGER_MODIFIER_KEY);
            ServerboundLabelGunUsePacket msg = new ServerboundLabelGunUsePacket(
                    ctx.getHand(),
                    ctx.getClickedPos(),
                    contiguous,
                    pickBlock,
                    clear,
                    pull,
                    targetManager
            );
            ClientLabelGunWarningHelper.sendLabelGunUsePacketFromClientWithConfirmationIfNecessary(msg, player);
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
            Item.TooltipContext pContext,
            List<Component> lines,
            TooltipFlag pTooltipFlag
    ) {
        if (SFMItemUtils.isClientAndMoreInfoKeyPressed()) {
            Options options = Minecraft.getInstance().options;
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_TOGGLE_LABEL_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(options.keyUse)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_CLEAR_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(SFMKeyMappings.LABEL_GUN_PULL_MODIFIER_KEY),
                            SFMKeyMappings.getKeyDisplay(options.keyUse)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_PULL_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(SFMKeyMappings.LABEL_GUN_PULL_MODIFIER_KEY),
                            SFMKeyMappings.getKeyDisplay(options.keyUse)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_PUSH_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(options.keyUse)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_TARGET_MANAGER_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(SFMKeyMappings.LABEL_GUN_TARGET_MANAGER_MODIFIER_KEY),
                            SFMKeyMappings.getKeyDisplay(options.keyUse)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_CONTIGUOUS_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(SFMKeyMappings.LABEL_GUN_CONTIGUOUS_MODIFIER_KEY)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_PICK_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(SFMKeyMappings.LABEL_GUN_PICK_BLOCK_MODIFIER_KEY),
                            SFMKeyMappings.getKeyDisplay(options.keyUse)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_NEXT_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(SFMKeyMappings.LABEL_GUN_NEXT_LABEL_KEY)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_PREVIOUS_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(SFMKeyMappings.LABEL_GUN_PREVIOUS_LABEL_KEY)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_SCROLL_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(SFMKeyMappings.LABEL_GUN_SCROLL_MODIFIER_KEY)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_CYCLE_VIEW_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(SFMKeyMappings.CYCLE_LABEL_VIEW_KEY)
                    ).withStyle(ChatFormatting.GRAY)
            );
            lines.add(
                    LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_GUI_REMINDER.getComponent(
                            SFMKeyMappings.getKeyDisplay(options.keyUse)
                    ).withStyle(ChatFormatting.GRAY)
            );
        } else {
            SFMItemUtils.appendMoreInfoKeyReminderTextIfOnClient(lines);
            lines.addAll(LabelPositionHolder.from(stack).asHoverText());
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        var stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            SFMScreenChangeHelpers.showLabelGunScreen(stack, hand);
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

    public enum LabelGunViewMode implements StringRepresentable {
        SHOW_ALL,
        SHOW_ONLY_ACTIVE_LABEL_AND_TARGETED_BLOCK,
        SHOW_ONLY_TARGETED_BLOCK;

        public static final Codec<LabelGunViewMode> CODEC = StringRepresentable.fromEnum(LabelGunViewMode::values);
        public static final IntFunction<LabelGunViewMode> BY_ID = ByIdMap.continuous(LabelGunViewMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, LabelGunViewMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, LabelGunViewMode::ordinal);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
