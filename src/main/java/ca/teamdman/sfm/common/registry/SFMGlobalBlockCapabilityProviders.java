package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.capability.*;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Supplier;

/// Check out {@link SFMBlockCapabilityProvider} for more information about what a Block Capability Provider is.
///
/// These are the {@link IBlockCapabilityProvider} that SFM registers to the world, which lets other mods see them.
@SuppressWarnings({"unused"})
public class SFMGlobalBlockCapabilityProviders {
    public static final ResourceLocation
            REGISTRY_ID = SFMResourceLocation.fromSFMPath("capability_provider_mappers");

//    public static final @Nullable RegistryObject<EnergyAcceptorBlockCapabilityProvider>
//            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER;

    private static final DeferredRegister<SFMBlockCapabilityProvider>
            REGISTERER = DeferredRegister.create(REGISTRY_ID, SFM.MOD_ID);

    private static final Registry<SFMBlockCapabilityProvider> REGISTRY
            = REGISTERER.makeRegistry(registryBuilder -> {
    });

    public static final Supplier<BlockEntityCapabilityProvider>
            BLOCK_ENTITY = REGISTERER.register("block_entity", BlockEntityCapabilityProvider::new);

    public static final Supplier<RedstoneSignalCapabilityProvider>
            REDSTONE = REGISTERER.register("redstone", RedstoneSignalCapabilityProvider::new);

    private static final Object2ObjectOpenHashMap<SFMBlockCapabilityKind<?>, ArrayList<IBlockCapabilityProvider<?, @Nullable Direction>>>
            BLOCK_CAPABILITY_PROVIDERS_BY_KIND = new Object2ObjectOpenHashMap<>();

    static {
//        if (SFMModCompat.isAE2Loaded()) {
//            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER = REGISTERER.register(
//                    "ae2/energy_acceptor",
//                    EnergyAcceptorBlockCapabilityProvider::new
//            );
////            MAPPERS.register("ae2/interface", InterfaceCapabilityProvider::new);
//        } else {
//            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER = null;
//        }
    }

    /// Highest priority first.
    public static ArrayList<SFMBlockCapabilityProvider> getProvidersInPriorityOrder() {
        ArrayList<SFMBlockCapabilityProvider> providers = new ArrayList<>();
        registry().forEach(providers::add);
        providers.sort(
                Comparator
                        .comparingInt(SFMBlockCapabilityProvider::priority)
                        .reversed()
        );
        return providers;
    }

    private static void rebuildProviderCache() {
        BLOCK_CAPABILITY_PROVIDERS_BY_KIND.clear();
        // The cache is empty. We will now populate it for each known capability kind.
        // This is done only once, the first time this method is called.
        for (SFMBlockCapabilityProvider provider : getProvidersInPriorityOrder()) {
            for (SFMBlockCapabilityKind<?> discoveryKind : (Iterable<SFMBlockCapabilityKind<?>>) SFMWellKnownCapabilities.streamCapabilities()::iterator) {
                IBlockCapabilityProvider<?, @Nullable Direction> providerForKind = provider.createForKind(discoveryKind);
                if (providerForKind == null) {
                    continue;
                }
                BLOCK_CAPABILITY_PROVIDERS_BY_KIND.computeIfAbsent(
                        discoveryKind,
                        __ -> new ArrayList<>()
                ).add(providerForKind);
            }
        }
        BLOCK_CAPABILITY_PROVIDERS_BY_KIND.values().forEach(ArrayList::trimToSize);
        BLOCK_CAPABILITY_PROVIDERS_BY_KIND.trim();
    }

    public static <STACK, ITEM, CAP> ArrayList<IBlockCapabilityProvider<CAP, @Nullable Direction>> getProvidersForKind(
            SFMBlockCapabilityKind<CAP> kind
    ) {
        if (BLOCK_CAPABILITY_PROVIDERS_BY_KIND.isEmpty()) {
            rebuildProviderCache();
        }

        //noinspection unchecked,rawtypes
        return (ArrayList) BLOCK_CAPABILITY_PROVIDERS_BY_KIND.getOrDefault(kind, new ArrayList<>());
    }

    @MCVersionDependentBehaviour
    public static SFMRegistryWrapper<SFMBlockCapabilityProvider> registry() {
        return new SFMRegistryWrapper<>(REGISTRY);
    }

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }
}
