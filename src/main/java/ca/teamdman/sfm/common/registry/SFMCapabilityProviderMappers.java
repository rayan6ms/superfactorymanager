package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.capability.BlockEntityCapabilityProviderMapper;
import ca.teamdman.sfm.common.capability.CapabilityProviderMapper;
import ca.teamdman.sfm.common.capability.CauldronCapabilityProviderMapper;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import ca.teamdman.sfm.common.util.Stored;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.capabilities.CapabilityProvider;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class SFMCapabilityProviderMappers {
    public static final ResourceKey<Registry<CapabilityProviderMapper>> REGISTRY_ID = ResourceKey.createRegistryKey(
            SFMResourceLocation.fromSFMPath(
                    "capability_provider_mappers"
            ));
//    public static final @Nullable Supplier<EnergyAcceptorCapabilityProviderMapper>
//            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER;
    private static final DeferredRegister<CapabilityProviderMapper> REGISTERER = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    public static final Registry<CapabilityProviderMapper> REGISTRY = REGISTERER.makeRegistry(registryBuilder -> {
    });
    @SuppressWarnings("unused")
    public static final Supplier<BlockEntityCapabilityProviderMapper> BLOCK_ENTITY_MAPPER = REGISTERER.register(
            "block_entity",
            BlockEntityCapabilityProviderMapper::new
    );
    @SuppressWarnings("unused")
    public static final Supplier<CauldronCapabilityProviderMapper> CAULDRON_MAPPER = REGISTERER.register(
            "cauldron",
            CauldronCapabilityProviderMapper::new
    );

//    static {
//        if (SFMModCompat.isAE2Loaded()) {
//            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER = REGISTERER.register(
//                    "ae2/energy_acceptor",
//                    EnergyAcceptorCapabilityProviderMapper::new
//            );
////            MAPPERS.register("ae2/interface", InterfaceCapabilityProviderMapper::new);
//        } else {
//            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER = null;
//        }
//    }

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    @MCVersionDependentBehaviour
    public static Registry<CapabilityProviderMapper> registry() {
        return REGISTRY;
    }

    /**
     * Find a {@link CapabilityProvider} as provided by the registered capabilityKind provider mappers.
     * If multiple {@link CapabilityProviderMapper}s match, the first one is returned.
     */
    public static @Nullable ICapabilityProvider discoverCapabilityProvider(
            LevelAccessor level,
            @Stored BlockPos pos
    ) {
        var mappers = REGISTRY.entrySet();
        CapabilityProviderMapper beMapper = null;
        for (var entry : mappers) {
            CapabilityProviderMapper mapper = entry.getValue();
            if (mapper instanceof BlockEntityCapabilityProviderMapper) {
                beMapper = mapper;
                continue;
            }
            ICapabilityProvider capabilityProvider = mapper.getProviderFor(level, pos);
            if (capabilityProvider != null) {
                return capabilityProvider;
            }
        }

        return beMapper != null ? beMapper.getProviderFor(level, pos) : null;
    }
}
