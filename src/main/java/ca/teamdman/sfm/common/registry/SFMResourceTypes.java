package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.resourcetype.FluidResourceType;
import ca.teamdman.sfm.common.resourcetype.ForgeEnergyResourceType;
import ca.teamdman.sfm.common.resourcetype.ItemResourceType;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class SFMResourceTypes {
    public static final ResourceKey<Registry<ResourceType<?, ?, ?>>> REGISTRY_ID = ResourceKey.createRegistryKey(new ResourceLocation(
            SFM.MOD_ID,
            "resource_type"
    ));

    private static final DeferredRegister<ResourceType<?, ?, ?>> TYPES = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    public static final Registry<ResourceType<?, ?, ?>> DEFERRED_TYPES = TYPES.makeRegistry(
            registryBuilder->{});
    public static final Supplier<ResourceType<ItemStack, Item, IItemHandler>> ITEM = TYPES.register(
            "item",
            ItemResourceType::new
    );
    public static final Supplier<ResourceType<FluidStack, Fluid, IFluidHandler>> FLUID = TYPES.register(
            "fluid",
            FluidResourceType::new
    );
    public static final Supplier<ResourceType<Integer, Class<Integer>, IEnergyStorage>> FORGE_ENERGY = TYPES.register(
            "forge_energy",
            ForgeEnergyResourceType::new
    );
    private static final Int2ObjectArrayMap<ResourceType<?, ?, ?>> DEFERRED_TYPES_BY_ID = new Int2ObjectArrayMap<>();

//    static {
//        if (SFMModCompat.isMekanismLoaded()) {
//            SFMMekanismCompat.registerResourceTypes(TYPES);
//        }
//    }

    public static int getResourceTypeCount() {
        return TYPES.getEntries().size();
    }

    public static @Nullable ResourceType<?, ?, ?> fastLookup(
            String resourceTypeNamespace,
            String resourceTypeName
    ) {
        return DEFERRED_TYPES_BY_ID.computeIfAbsent(
                resourceTypeNamespace.hashCode() ^ resourceTypeName.hashCode(),
                i -> DEFERRED_TYPES.get(new ResourceLocation(resourceTypeNamespace, resourceTypeName))
        );
    }

    public static Stream<BlockCapability<?, @Nullable Direction>> getCapabilities() {
        return TYPES.getEntries().stream().map(Supplier::get).map(resourceType -> resourceType.CAPABILITY_KIND);
    }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
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
