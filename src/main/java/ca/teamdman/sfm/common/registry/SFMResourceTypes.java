package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityKind;
import ca.teamdman.sfm.common.resourcetype.*;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class SFMResourceTypes {
    public static final ResourceKey<Registry<ResourceType<?, ?, ?>>> REGISTRY_ID
            = SFMResourceLocation.createSFMRegistryKey("resource_type");

    private static final SFMDeferredRegister<ResourceType<?, ?, ?>> REGISTERER
            = SFMDeferredRegister.createForCustomRegistry(REGISTRY_ID, SFM.MOD_ID);

    public static final SFMRegistryObject<ItemResourceType> ITEM
            = REGISTERER.register("item", ItemResourceType::new);

    public static final SFMRegistryObject<FluidResourceType> FLUID
            = REGISTERER.register("fluid", FluidResourceType::new);

    public static final SFMRegistryObject<ForgeEnergyResourceType> FORGE_ENERGY
            = REGISTERER.register("forge_energy", ForgeEnergyResourceType::new);

    public static final SFMRegistryObject<RedstoneResourceType> REDSTONE
            = REGISTERER.register("redstone", RedstoneResourceType::new);

    private static final Object2ObjectOpenHashMap<ResourceLocation, ResourceType<?, ?, ?>> DEFERRED_TYPES_BY_ID
            = new Object2ObjectOpenHashMap<>();

//    static {
//        if (SFMModCompat.isMekanismLoaded()) {
//            SFMMekanismCompat.registerResourceTypes(REGISTERER);
//        }
//    }

    public static int getResourceTypeCount() {
        return REGISTERER.size();
    }

    public static @Nullable ResourceType<?, ?, ?> fastLookup(
            ResourceLocation resourceTypeId
    ) {
        return DEFERRED_TYPES_BY_ID.computeIfAbsent(
                resourceTypeId,
                i -> registry().getValue(resourceTypeId)
        );
    }

    public static Stream<SFMBlockCapabilityKind<?>> getCapabilities() {
        return REGISTERER.registry()
                .streamValues()
                .map(resourceType -> resourceType.CAPABILITY_KIND);
    }

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    public static SFMRegistryWrapper<ResourceType<?, ?, ?>> registry() {
        return REGISTERER.registry();
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
