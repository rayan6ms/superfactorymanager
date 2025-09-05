package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.capability.BlockEntityCapabilityProvider;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityProvider;
import ca.teamdman.sfm.common.capability.ae2.EnergyAcceptorBlockCapabilityProvider;
import ca.teamdman.sfm.common.compat.SFMModCompat;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Supplier;

/// This class is used in doc comments because it's a plural lol.
/// Check out {@link SFMBlockCapabilityProvider} for more information about what a Block Capability Provider is.
@SuppressWarnings({"unused"})
public class SFMBlockCapabilityProviders {
    public static final ResourceLocation
            REGISTRY_ID = SFMResourceLocation.fromSFMPath("capability_provider_mappers");

    public static final @Nullable Supplier<EnergyAcceptorBlockCapabilityProvider>
            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER;

    private static final DeferredRegister<SFMBlockCapabilityProvider<?>>
            REGISTERER = DeferredRegister.create(REGISTRY_ID, SFM.MOD_ID);

    private static final Registry<SFMBlockCapabilityProvider<?>> REGISTRY
            = REGISTERER.makeRegistry(registryBuilder -> {});

    public static final Supplier<BlockEntityCapabilityProvider>
            BLOCK_ENTITY = REGISTERER.register("block_entity", BlockEntityCapabilityProvider::new);

    static {
        if (SFMModCompat.isAE2Loaded()) {
            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER = REGISTERER.register(
                    "ae2/energy_acceptor",
                    EnergyAcceptorBlockCapabilityProvider::new
            );
//            MAPPERS.register("ae2/interface", InterfaceCapabilityProviderMapper::new);
        } else {
            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER = null;
        }
    }

    public static ArrayList<SFMBlockCapabilityProvider<?>> getAllProviders() {
        ArrayList<SFMBlockCapabilityProvider<?>> providers = new ArrayList<>();
        registry().forEach(providers::add);
        providers.sort(
                Comparator
                        .comparingInt((SFMBlockCapabilityProvider<?> provider) -> provider.priority())
                        .reversed()
        );
        return providers;
    }

    @MCVersionDependentBehaviour
    public static SFMRegistryWrapper<SFMBlockCapabilityProvider<?>> registry() {
        return new SFMRegistryWrapper<>(REGISTRY);
    }

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }
}
