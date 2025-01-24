package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.render.CableFacadeBlockModelWrapper;
import ca.teamdman.sfm.client.render.FancyCableFacadeBlockModelWrapper;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Map;

@EventBusSubscriber(modid = SFM.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SFMBlockModelWrappers {
    @SubscribeEvent
    public static void onModelBakeEvent(@MCVersionDependentBehaviour ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        for (BlockState possibleState : SFMBlocks.CABLE_FACADE_BLOCK.get().getStateDefinition().getPossibleStates()) {
            models.computeIfPresent(
                    BlockModelShaper.stateToModelLocation(possibleState),
                    (location, model) -> new CableFacadeBlockModelWrapper(model)
            );
        }
        for (BlockState possibleState : SFMBlocks.FANCY_CABLE_FACADE_BLOCK
                .get()
                .getStateDefinition()
                .getPossibleStates()
        ) {
            models.computeIfPresent(
                    BlockModelShaper.stateToModelLocation(possibleState),
                    (location, model) -> new FancyCableFacadeBlockModelWrapper(model)
            );
        }
    }
}
