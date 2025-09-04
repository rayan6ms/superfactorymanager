package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.capability.BlockEntityCapabilityProviderMapper;
import ca.teamdman.sfm.common.capability.CapabilityProviderMapper;
import ca.teamdman.sfm.common.capability.CauldronCapabilityProviderMapper;
import ca.teamdman.sfm.common.capability.ae2.EnergyAcceptorCapabilityProviderMapper;
import ca.teamdman.sfm.common.compat.SFMModCompat;
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

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class SFMCapabilityProviderMappers {
    public static final ResourceLocation
            REGISTRY_ID = SFMResourceLocation.fromSFMPath("capability_provider_mappers");

    public static final @Nullable RegistryObject<EnergyAcceptorCapabilityProviderMapper>
            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER;

    private static final DeferredRegister<CapabilityProviderMapper>
            REGISTERER = DeferredRegister.create(REGISTRY_ID, SFM.MOD_ID);

    private static final Supplier<IForgeRegistry<CapabilityProviderMapper>>
            REGISTRY = REGISTERER.makeRegistry(() -> new RegistryBuilder<CapabilityProviderMapper>().setName(REGISTRY_ID));

    public static final RegistryObject<BlockEntityCapabilityProviderMapper>
            BLOCK_ENTITY_MAPPER = REGISTERER.register("block_entity", BlockEntityCapabilityProviderMapper::new);

    public static final RegistryObject<CauldronCapabilityProviderMapper>
            CAULDRON_MAPPER = REGISTERER.register("cauldron", CauldronCapabilityProviderMapper::new);

    static {
        if (SFMModCompat.isAE2Loaded()) {
            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER = REGISTERER.register(
                    "ae2/energy_acceptor",
                    EnergyAcceptorCapabilityProviderMapper::new
            );
//            MAPPERS.register("ae2/interface", InterfaceCapabilityProviderMapper::new);
        } else {
            AE2_ENERGY_ACCEPTOR_CAPABILITY_PROVIDER_MAPPER = null;
        }
    }

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    @MCVersionDependentBehaviour
    public static IForgeRegistry<CapabilityProviderMapper> registry() {
        return REGISTRY.get();
    }

    /**
     * Find a {@link CapabilityProvider} as provided by the registered capabilityKind provider mappers.
     * If multiple {@link CapabilityProviderMapper}s match, the first one is returned.
     */
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
