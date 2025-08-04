package ca.teamdman.sfm.client.handler;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.screen.text_editor.SFMTextEditScreenV2;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OverlayHider {
    @SubscribeEvent
    public static void onTryOverlay(RenderGuiEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof SFMTextEditScreenV2) {
            event.setCanceled(true);
        }
    }
}
