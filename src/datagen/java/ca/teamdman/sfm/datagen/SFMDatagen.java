package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = SFM.MOD_ID)
public class SFMDatagen {
    @SubscribeEvent
    public static void onGather(GatherDataEvent event) {
        if (!DatagenModLoader.isRunningDataGen()) return;
        if (event.includeServer()) {
            event.getGenerator().addProvider(event.includeClient(), new SFMBlockStatesAndModelsDatagen(event));
            event.getGenerator().addProvider(event.includeClient(), new SFMItemModelsDatagen(event));
            event.getGenerator().addProvider(event.includeClient(), new SFMBlockTagsDatagen(event));
            event.getGenerator().addProvider(event.includeClient(), new SFMLootTablesDatagen(event));
            event.getGenerator().addProvider(event.includeClient(), new SFMRecipesDatagen(event));
            event.getGenerator().addProvider(event.includeClient(), new SFMLanguageProviderDatagen(event));
        }
    }
}
