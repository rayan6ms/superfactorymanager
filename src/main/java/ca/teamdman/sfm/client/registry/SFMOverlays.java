package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.overlay.LabelGunReminderOverlay;
import ca.teamdman.sfm.client.overlay.NetworkToolReminderOverlay;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.util.Lazy;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = SFM.MOD_ID, value = Dist.CLIENT)
public class SFMOverlays {
    public static final Lazy<LabelGunReminderOverlay> LABEL_GUN_REMINDER_OVERLAY = Lazy.of(LabelGunReminderOverlay::new);
    public static final Lazy<NetworkToolReminderOverlay> NETWORK_TOOL_REMINDER_OVERLAY = Lazy.of(NetworkToolReminderOverlay::new);

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.HOTBAR,
                SFMResourceLocation.fromSFMPath("label_gun_reminder"),
                LABEL_GUN_REMINDER_OVERLAY.get()
        );
        event.registerAbove(
                VanillaGuiLayers.HOTBAR,
                SFMResourceLocation.fromSFMPath( "network_tool_reminder"),
                NETWORK_TOOL_REMINDER_OVERLAY.get()
        );
    }
}
