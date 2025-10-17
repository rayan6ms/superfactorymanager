package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.compat.SFMMekanismCompat;
import ca.teamdman.sfm.common.compat.SFMModCompat;
import ca.teamdman.sfm.common.resourcetype.*;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SFMResourceTypes {
    public static final ResourceLocation REGISTRY_ID = SFMResourceLocation.fromSFMPath("resource_type");

    private static final DeferredRegister<ResourceType<?, ?, ?>> REGISTERER = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );

    private static final Supplier<IForgeRegistry<ResourceType<?, ?, ?>>> REGISTRY = REGISTERER.makeRegistry(
            () -> new RegistryBuilder<ResourceType<?, ?, ?>>().setName(REGISTRY_ID));

    public static final RegistryObject<ItemResourceType> ITEM = REGISTERER.register(
            "item",
            ItemResourceType::new
    );

    public static final RegistryObject<FluidResourceType> FLUID = REGISTERER.register(
            "fluid",
            FluidResourceType::new
    );

    public static final RegistryObject<ForgeEnergyResourceType> FORGE_ENERGY = REGISTERER.register(
            "forge_energy",
            ForgeEnergyResourceType::new
    );

    public static final RegistryObject<RedstoneResourceType> REDSTONE = REGISTERER.register(
            "redstone",
            RedstoneResourceType::new
    );


    private static final Object2ObjectOpenHashMap<ResourceLocation, ResourceType<?, ?, ?>> DEFERRED_TYPES_BY_ID = new Object2ObjectOpenHashMap<>();

    static {
        if (SFMModCompat.isMekanismLoaded()) {
            SFMMekanismCompat.registerResourceTypes(REGISTERER);
        }
    }

    public static int getResourceTypeCount() {
        return REGISTERER.getEntries().size();
    }

    public static @Nullable ResourceType<?, ?, ?> fastLookup(
            ResourceLocation resourceTypeId
    ) {
        return DEFERRED_TYPES_BY_ID.computeIfAbsent(
                resourceTypeId,
                i -> registry().getValue(resourceTypeId)
        );
    }

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    @MCVersionDependentBehaviour
    public static SFMRegistryWrapper<ResourceType<?, ?, ?>> registry() {
        return new SFMRegistryWrapper<>(REGISTRY.get());
    }

    /* TODO: add support for new resource types
     * - mekanism heat
     * - botania mana
     * - ars nouveau source
     * - flux plugs
     * - PNC pressure
     * - PNC heat
     * - nature's aura aura
     * - create rotation
     */
}
