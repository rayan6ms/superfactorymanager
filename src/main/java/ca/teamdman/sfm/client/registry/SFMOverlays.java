package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.overlay.LabelGunReminderOverlay;
import ca.teamdman.sfm.client.gui.overlay.NetworkToolReminderOverlay;
import net.minecraft.resources.ResourceLocation;
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
                ResourceLocation.fromNamespaceAndPath(SFM.MOD_ID, "label_gun_reminder"),
                LABEL_GUN_REMINDER_OVERLAY.get()
        );
        event.registerAbove(
                VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(SFM.MOD_ID, "network_tool_reminder"),
                NETWORK_TOOL_REMINDER_OVERLAY.get()
        );
    }
}
