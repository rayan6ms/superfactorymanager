package ca.teamdman.sfm.common.resourcetype;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RegistryBackedResourceType<STACK,ITEM,CAP> extends ResourceType<STACK,ITEM,CAP> {
    private final Map<ITEM, ResourceLocation> registryKeyCache = new Object2ObjectOpenHashMap<>();
    public RegistryBackedResourceType(Capability<CAP> CAPABILITY_KIND) {
        super(CAPABILITY_KIND);
    }


    @Override
    public ResourceLocation getRegistryKeyForStack(STACK stack) {
        ITEM item = getItem(stack);
        return getRegistryKeyForItem(item);
    }

    @Override
    public ResourceLocation getRegistryKeyForItem(ITEM item) {
        var found = registryKeyCache.get(item);
        if (found != null) return found;
        found = getRegistry().getKey(item);
        if (found == null) {
            throw new NullPointerException("Registry key not found for item: " + item);
        }
        registryKeyCache.put(item, found);
        return found;
    }

    @Override
    public Set<ResourceLocation> getRegistryKeys() {
        return getRegistry().keySet();
    }

    @Override
    public Collection<ITEM> getItems() {
        return getRegistry().stream().collect(Collectors.toList());
    }

    public abstract Registry<ITEM> getRegistry();

    @Override
    public @Nullable ITEM getItemFromRegistryKey(ResourceLocation location) {
        return getRegistry().get(location);
    }

    @Override
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean registryKeyExists(ResourceLocation location) {
        return getRegistry().containsKey(location);
    }

}
