package ca.teamdman.sfm.common.registry;


import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record SFMRegistryWrapper<V>(
        @MCVersionDependentBehaviour
        Registry<V> registry
) implements Iterable<V> {
    public @Nullable V getValue(ResourceLocation resourceTypeId) {
        return registry.get(resourceTypeId);
    }

    public @NotNull Set<ResourceLocation> getKeys() {
        return registry.keySet();
    }

    public @NotNull Iterable<V> getValues() {
        return registry;
    }

    public @Nullable ResourceLocation getKey(V value) {
        return registry.getKey(value);
    }

    public @NotNull Optional<ResourceKey<V>> getResourceKey(V value) {
        return registry.getResourceKey(value);
    }

    public @NotNull Set<Map.Entry<ResourceKey<V>, V>> getEntries() {
        return registry.entrySet();
    }

    @Override
    public @NotNull Iterator<V> iterator() {
        return registry.iterator();
    }
}
