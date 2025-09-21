package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@MCVersionDependentBehaviour
@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SFMCreativeTabs {
    @SuppressWarnings("NotNullFieldNotInitialized")
    public static CreativeModeTab TAB;


    public static class SFMCreativeModeTab {
        public static final String DISPLAY_NAME_TRANSLATION_KEY = "item_group." + SFM.MOD_ID;
        public static final Component DISPLAY_NAME = Component.translatable(DISPLAY_NAME_TRANSLATION_KEY);
    }

    @SubscribeEvent
    public static void onRegister(CreativeModeTabEvent.Register event) {
        TAB = event.registerCreativeModeTab(
                SFMResourceLocation.fromSFMPath("main"),
                builder ->
                        // Set name of tab to display
                        builder.title(SFMCreativeModeTab.DISPLAY_NAME)
                                // Set icon of creative tab
                                .icon(() -> new ItemStack(SFMBlocks.MANAGER_BLOCK.get()))
                                // Add default items to tab
                                .displayItems((params, output) -> output.acceptAll(SFMItems.ITEMS
                                                                                           .getEntries()
                                                                                           .stream()
                                                                                           .map(RegistryObject::get)
                                                                                           .map(ItemStack::new)
                                                                                           .toList()))
        );
    }
}
