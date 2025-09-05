package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityKind;
import ca.teamdman.sfm.common.compat.SFMModCompat;
import ca.teamdman.sfm.common.resourcetype.FluidResourceType;
import ca.teamdman.sfm.common.resourcetype.ForgeEnergyResourceType;
import ca.teamdman.sfm.common.resourcetype.ItemResourceType;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class SFMResourceTypes {
    public static final ResourceLocation REGISTRY_ID = SFMResourceLocation.fromSFMPath("resource_type");
    private static final DeferredRegister<ResourceType<?, ?, ?>> REGISTERER = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    private static final Supplier<IForgeRegistry<ResourceType<?, ?, ?>>> REGISTRY = REGISTERER.makeRegistry(
            () -> new RegistryBuilder<ResourceType<?, ?, ?>>().setName(REGISTRY_ID));
    public static final RegistryObject<ResourceType<ItemStack, Item, IItemHandler>> ITEM = REGISTERER.register(
            "item",
            ItemResourceType::new
    );
    public static final RegistryObject<ResourceType<FluidStack, Fluid, IFluidHandler>> FLUID = REGISTERER.register(
            "fluid",
            FluidResourceType::new
    );
    public static final RegistryObject<ResourceType<Integer, Class<Integer>, IEnergyStorage>> FORGE_ENERGY = REGISTERER.register(
            "forge_energy",
            ForgeEnergyResourceType::new
    );
    private static final Object2ObjectOpenHashMap<ResourceLocation, ResourceType<?, ?, ?>> DEFERRED_TYPES_BY_ID = new Object2ObjectOpenHashMap<>();

    static {
        if (SFMModCompat.isMekanismLoaded()) {
//            SFMMekanismCompat.registerResourceTypes(REGISTERER);
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

    public static Stream<SFMBlockCapabilityKind<?>> getCapabilities() {
        return REGISTERER.getEntries().stream().map(RegistryObject::get).map(resourceType -> resourceType.CAPABILITY_KIND);
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
