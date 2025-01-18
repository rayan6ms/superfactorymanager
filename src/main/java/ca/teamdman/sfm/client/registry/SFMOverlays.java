package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.LabelGunReminderOverlay;
import ca.teamdman.sfm.client.gui.NetworkToolReminderOverlay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.common.util.Lazy;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = SFM.MOD_ID, value = Dist.CLIENT)
public class SFMOverlays {
    public static final Lazy<LabelGunReminderOverlay> LABEL_GUN_REMINDER_OVERLAY = Lazy.of(LabelGunReminderOverlay::new);
    public static final Lazy<NetworkToolReminderOverlay> NETWORK_TOOL_REMINDER_OVERLAY = Lazy.of(NetworkToolReminderOverlay::new);

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(
                VanillaGuiOverlay.HOTBAR.id(),
                new ResourceLocation(SFM.MOD_ID, "label_gun_reminder"),
                LABEL_GUN_REMINDER_OVERLAY.get()
        );
        event.registerAbove(
                VanillaGuiOverlay.HOTBAR.id(),
                new ResourceLocation(SFM.MOD_ID, "network_tool_reminder"),
                NETWORK_TOOL_REMINDER_OVERLAY.get()
        );
    }
}
