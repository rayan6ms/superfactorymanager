package ca.teamdman.sfm.common.resourcetype;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public abstract class ScalarResourceType extends ResourceType<Integer, Class<Integer>, IEnergyStorage> {
    public final ResourceLocation registryKey;

    public ScalarResourceType(
            Capability<IEnergyStorage> CAPABILITY_KIND,
            ResourceLocation registryKey
    ) {
        super(CAPABILITY_KIND);
        this.registryKey = registryKey;
    }

    @Override
    public long getAmount(Integer integer) {
        return integer;
    }

    @Override
    public Integer getStackInSlot(
            IEnergyStorage iEnergyStorage,
            int slot
    ) {
        return iEnergyStorage.getEnergyStored();
    }

    @Override
    public Stream<ResourceLocation> getTagsForStack(Integer integer) {
        return Stream.empty();
    }

    @Override
    public long getMaxStackSize(Integer integer) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getMaxStackSizeForSlot(
            IEnergyStorage iEnergyStorage,
            int slot
    ) {
        int maxStackSize = iEnergyStorage.getMaxEnergyStored();
        if (maxStackSize == Integer.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return maxStackSize;
    }

    @Override
    public boolean isEmpty(Integer stack) {
        return stack == 0;
    }

    @Override
    public Integer getEmptyStack() {
        return 0;
    }

    @Override
    public ResourceLocation getRegistryKeyForStack(Integer stack) {
        return registryKey;
    }

    @Override
    public ResourceLocation getRegistryKeyForItem(Class<Integer> item) {
        return registryKey;
    }

    @Override
    public @Nullable Class<Integer> getItemFromRegistryKey(ResourceLocation location) {
        if (location.equals(registryKey)) {
            return Integer.class;
        }
        return null;
    }

    @Override
    public Set<ResourceLocation> getRegistryKeys() {
        return Set.of(registryKey);
    }

    @Override
    public Collection<Class<Integer>> getItems() {
        return List.of(Integer.class);
    }

    @Override
    public boolean registryKeyExists(ResourceLocation location) {
        return location.equals(registryKey);
    }

    @Override
    public Class<Integer> getItem(Integer stack) {
        return Integer.class;
    }

    @Override
    public Integer copy(Integer stack) {
        return stack;
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof Integer;
    }

    @Override
    protected Integer setCount(
            Integer stack,
            long amount
    ) {
        return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
    }
}
