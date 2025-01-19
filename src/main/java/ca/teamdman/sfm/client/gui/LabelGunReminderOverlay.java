package ca.teamdman.sfm.client.gui;

import ca.teamdman.sfm.client.registry.SFMKeyMappings;
import ca.teamdman.sfm.common.config.SFMConfig;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.SFMHandUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

public class LabelGunReminderOverlay implements IGuiOverlay {
    @Override
    public void render(
            ExtendedGui gui,
            GuiGraphics guiGraphics,
            float partialTick,
            int screenWidth,
            int screenHeight
    ) {
        Minecraft minecraft = gui.getMinecraft();
        if (minecraft.options.hideGui) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        if (!shouldRender(minecraft)) {
            return;
        }
        Font font = minecraft.font;
        var reminder = LocalizationKeys.LABEL_GUN_LABEL_VIEW_REMINDER.getComponent(
                SFMKeyMappings.TOGGLE_LABEL_VIEW_KEY
                        .get()
                        .getTranslatedKeyMessage().plainCopy().withStyle(ChatFormatting.YELLOW)
        );
        int reminderWidth = font.width(reminder);
        int x = screenWidth / 2 - reminderWidth / 2;
        int y = 20;
        guiGraphics.drawString(
                font,
                reminder,
                x,
                y,
                FastColor.ARGB32.color(255, 172, 208, 255)
        );
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean shouldRender(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null) return false;
        if (!SFMConfig.CLIENT.showLabelGunReminderOverlay.get()) return false;
        ItemStack labelGun = SFMHandUtils.getItemInEitherHand(player, SFMItems.LABEL_GUN_ITEM.get());
        if (labelGun.isEmpty()) return false;
        return LabelGunItem.getOnlyShowActiveLabel(labelGun);
    }
}
