package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.capability.BlockEntityCapabilityProvider;
import ca.teamdman.sfm.common.capability.CauldronBlockCapabilityProvider;
import ca.teamdman.sfm.common.capability.RedstoneSignalCapabilityProvider;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityProvider;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.Comparator;

/// This class is used in doc comments because it's a plural lol.
/// Check out {@link SFMBlockCapabilityProvider} for more information about what a Block Capability Provider is.
@SuppressWarnings({"unused"})
public class SFMGlobalBlockCapabilityProviders {
    public static final ResourceKey<Registry<SFMBlockCapabilityProvider<?>>> REGISTRY_ID
            = SFMResourceLocation.createSFMRegistryKey("capability_provider_mappers");

    private static final SFMDeferredRegister<SFMBlockCapabilityProvider<?>> REGISTERER
            = SFMDeferredRegister.createForCustomRegistry(REGISTRY_ID, SFM.MOD_ID);

    public static final SFMRegistryObject<CauldronBlockCapabilityProvider>
            CAULDRON_MAPPER = REGISTERER.register("cauldron", CauldronBlockCapabilityProvider::new);

    public static final SFMRegistryObject<BlockEntityCapabilityProvider>
            BLOCK_ENTITY = REGISTERER.register("block_entity", BlockEntityCapabilityProvider::new);

    public static final SFMRegistryObject<RedstoneSignalCapabilityProvider>
            REDSTONE = REGISTERER.register("redstone", RedstoneSignalCapabilityProvider::new);

//    public static final SFMRegistryObject<EnergyAcceptorBlockCapabilityProvider>
//            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER;

//    static {
//        if (SFMModCompat.isAE2Loaded()) {
//            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER = REGISTERER.register(
//                    "ae2/energy_acceptor",
//                    EnergyAcceptorBlockCapabilityProvider::new
//            );
////            MAPPERS.register("ae2/interface", InterfaceCapabilityProvider::new);
//        } else {
//            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER = REGISTERER.empty(
//                    "ae2/energy_acceptor"
//            );
//        }
//    }

    /// Gets all registered Block Capability Providers, sorted by priority (highest priority first).
    public static ArrayList<SFMBlockCapabilityProvider<?>> getAllProviders() {
        ArrayList<SFMBlockCapabilityProvider<?>> providers = new ArrayList<>(REGISTERER.registry().getValues());
        providers.sort(
                Comparator
                        .comparingInt((SFMBlockCapabilityProvider<?> provider) -> provider.priority())
                        .reversed()
        );
        return providers;
    }

    public static SFMRegistryWrapper<SFMBlockCapabilityProvider<?>> registry() {
        return REGISTERER.registry();
    }

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }
}
