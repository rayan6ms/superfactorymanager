package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.capabilityprovidermapper.BlockEntityCapabilityProviderMapper;
import ca.teamdman.sfm.common.capabilityprovidermapper.CapabilityProviderMapper;
import ca.teamdman.sfm.common.capabilityprovidermapper.CauldronCapabilityProviderMapper;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import ca.teamdman.sfm.common.util.Stored;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

public class SFMCapabilityProviderMappers {
    public static final  ResourceLocation                                   REGISTRY_ID      = SFMResourceLocation.fromSFMPath(
            "capability_provider_mappers"
    );
    private static final DeferredRegister<CapabilityProviderMapper> REGISTERER = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    private static final Supplier<IForgeRegistry<CapabilityProviderMapper>> REGISTRY = REGISTERER.makeRegistry(() -> new RegistryBuilder<CapabilityProviderMapper>().setName(
            REGISTRY_ID));

    @SuppressWarnings("unused")
    public static final RegistryObject<BlockEntityCapabilityProviderMapper> BLOCK_ENTITY_MAPPER = REGISTERER.register(
            "block_entity",
            BlockEntityCapabilityProviderMapper::new
    );

    @SuppressWarnings("unused")
    public static final RegistryObject<CauldronCapabilityProviderMapper> CAULDRON_MAPPER = REGISTERER.register(
            "cauldron",
            CauldronCapabilityProviderMapper::new
    );

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    @MCVersionDependentBehaviour
    public static IForgeRegistry<CapabilityProviderMapper> registry() {
        return REGISTRY.get();
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

        Collection<CapabilityProviderMapper> mappers = REGISTRY.get().getValues();
        CapabilityProviderMapper beMapper = null;
        for (CapabilityProviderMapper mapper : mappers) {
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
