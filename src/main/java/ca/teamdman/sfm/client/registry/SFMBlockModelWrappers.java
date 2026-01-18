package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.render.CableFacadeBlockModelWrapper;
import ca.teamdman.sfm.client.render.FancyCableFacadeBlockModelWrapper;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMRegistryObject;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SFMBlockModelWrappers {
    @SubscribeEvent
    public static void onModelBakeEvent(@MCVersionDependentBehaviour ModelEvent.BakingCompleted event) {

        record FacadeModelRelationship(
                SFMRegistryObject<Block, ?> facadeBlock,

                Function<BakedModel, BakedModelWrapper<BakedModel>> modelWrapperConstructor
        ) {
        }

        // Define the known relationships
        var relationships = new FacadeModelRelationship[]{
                new FacadeModelRelationship(
                        SFMBlocks.CABLE_FACADE_BLOCK,
                        CableFacadeBlockModelWrapper::new
                ),
                new FacadeModelRelationship(
                        SFMBlocks.FANCY_CABLE_FACADE_BLOCK,
                        FancyCableFacadeBlockModelWrapper::new
                ),
                new FacadeModelRelationship(
                        SFMBlocks.TUNNELLED_CABLE_FACADE_BLOCK,
                        CableFacadeBlockModelWrapper::new
                ),
                new FacadeModelRelationship(
                        SFMBlocks.TUNNELLED_FANCY_CABLE_FACADE_BLOCK,
                        FancyCableFacadeBlockModelWrapper::new
                ),
                new FacadeModelRelationship(
                        SFMBlocks.TOUGH_CABLE_FACADE_BLOCK,
                        CableFacadeBlockModelWrapper::new
                ),
                new FacadeModelRelationship(
                        SFMBlocks.TOUGH_FANCY_CABLE_FACADE_BLOCK,
                        FancyCableFacadeBlockModelWrapper::new
                ),
                };

        // Apply the model redirection for each relationship
        Map<ResourceLocation, BakedModel> models = event.getModels();
        for (var relationship : relationships) {
            // Get the possible states for the facaded block
            ImmutableList<BlockState> possibleStates = relationship
                    .facadeBlock()
                    .get()
                    .getStateDefinition()
                    .getPossibleStates();

            // Apply the model redirection for each state
            for (BlockState state : possibleStates) {
                // Get the default model location for the state
                ModelResourceLocation stateModelLocation = BlockModelShaper.stateToModelLocation(state);

                models.computeIfPresent(
                        stateModelLocation,
                        (_location, model) -> relationship.modelWrapperConstructor().apply(model)
                );
            }
        }
    }

}
