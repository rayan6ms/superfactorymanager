package ca.teamdman.sfm.client.render;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registry.registration.SFMItems;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid= SFM.MOD_ID, bus= EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FormItemExtensions implements IClientItemExtensions {
    private final BlockEntityWithoutLevelRenderer RENDERER = new FormItemRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return RENDERER;
    }

    @MCVersionDependentBehaviour // 1.21 this replaces FormItem#initializeClient
    @SubscribeEvent
    public static void register(RegisterClientExtensionsEvent event) {
        event.registerItem(new FormItemExtensions(), SFMItems.FORM.get());
    }
}
