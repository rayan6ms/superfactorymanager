package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.render.PrintingPressBlockEntityRenderer;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = SFM.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SFMBlockEntityRenderers {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                SFMBlockEntities.PRINTING_PRESS_BLOCK_ENTITY.get(),
                PrintingPressBlockEntityRenderer::new
        );
    }
}
