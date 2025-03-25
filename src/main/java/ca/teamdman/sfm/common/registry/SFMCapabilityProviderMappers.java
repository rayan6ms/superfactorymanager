package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.capabilityprovidermapper.BlockEntityCapabilityProviderMapper;
import ca.teamdman.sfm.common.capabilityprovidermapper.CapabilityProviderMapper;
import ca.teamdman.sfm.common.capabilityprovidermapper.CauldronCapabilityProviderMapper;
import ca.teamdman.sfm.common.util.Stored;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SFMCapabilityProviderMappers {
    public static final ResourceKey<Registry<CapabilityProviderMapper>> REGISTRY_ID = ResourceKey.createRegistryKey(new ResourceLocation(
            SFM.MOD_ID,
            "capability_provider_mappers"
    ));
    private static final DeferredRegister<CapabilityProviderMapper> MAPPERS = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    public static final Registry<CapabilityProviderMapper> DEFERRED_MAPPERS = MAPPERS.makeRegistry(registryBuilder->{});

    @SuppressWarnings("unused")
    public static final Supplier<BlockEntityCapabilityProviderMapper> BLOCK_ENTITY_MAPPER = MAPPERS.register(
            "block_entity",
            BlockEntityCapabilityProviderMapper::new
    );

    @SuppressWarnings("unused")
    public static final Supplier<CauldronCapabilityProviderMapper> CAULDRON_MAPPER = MAPPERS.register(
            "cauldron",
            CauldronCapabilityProviderMapper::new
    );

    public static void register(IEventBus bus) {
        MAPPERS.register(bus);
    }

//    static {
//        if (SFMModCompat.isAE2Loaded()) {
//            MAPPERS.register("ae2/interface", InterfaceCapabilityProviderMapper::new);
//        }
//    }

    /**
     * Find a {@link CapabilityProvider} as provided by the registered capability provider mappers.
     * If multiple {@link CapabilityProviderMapper}s match, the first one is returned.
     */
    @SuppressWarnings("UnstableApiUsage") // for the javadoc lol
    public static @Nullable ICapabilityProvider discoverCapabilityProvider(
            Level level,
            @Stored BlockPos pos
    ) {
        if (!level.isLoaded(pos)) return null;

        var mappers = DEFERRED_MAPPERS.entrySet();
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
