package ca.teamdman.sfm.common.registry.registration;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.event_bus.SFMSubscribeEvent;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.registry.SFMRegistryObject;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@MCVersionDependentBehaviour
public class SFMCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            SFM.MOD_ID
    );

    @SuppressWarnings("unused")
    public static final Supplier<CreativeModeTab> MAIN = CREATIVE_TABS.register(
            "main",
            () -> CreativeModeTab
                    .builder()
                    .title(LocalizationKeys.CREATIVE_TAB.getComponent())
                    .icon(() -> new ItemStack(SFMBlocks.MANAGER.get()))
                    .displayItems(SFMCreativeTabs::populateMainCreativeTab)
                    .build()
    );

    public static void register(IEventBus bus) {
        CREATIVE_TABS.register(bus);
    }


    public static void populateMainCreativeTab(
            @SuppressWarnings("unused")
            CreativeModeTab.ItemDisplayParameters params,
            CreativeModeTab.Output output
    ) {
        output.acceptAll(
                SFMItems.REGISTERER
                        .getOurEntries()
                        .stream()
                        .map(SFMRegistryObject::get)
                        .map(ItemStack::new)
                        .toList()
        );
    }
}
