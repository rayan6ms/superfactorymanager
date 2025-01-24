package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.render.FacadeBlockColor;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = SFM.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SFMBlockColors {
    @SubscribeEvent
    public static void registerBlockColor(RegisterColorHandlersEvent.Block event) {
        FacadeBlockColor blockColor = new FacadeBlockColor();
        event.register(blockColor, SFMBlocks.CABLE_FACADE_BLOCK.get());
        event.register(blockColor, SFMBlocks.FANCY_CABLE_FACADE_BLOCK.get());
    }
}
