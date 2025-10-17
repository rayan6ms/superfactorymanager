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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public record SFMRegistryWrapper<V>(
        @MCVersionDependentBehaviour
        Registry<V> registry
) implements Iterable<V> {
    @MCVersionDependentBehaviour
    public @Nullable V getValue(ResourceLocation resourceTypeId) {
        return registry.get(resourceTypeId);
    }

    @MCVersionDependentBehaviour
    public @NotNull Set<ResourceLocation> getKeys() {
        return registry.keySet();
    }

    public @NotNull Iterable<V> getValues() {
        return registry;
    }

    public @NotNull Stream<V> streamValues() {
        return StreamSupport.stream(registry.spliterator(), false);
    }

    public @Nullable ResourceLocation getKey(V value) {
        return registry.getKey(value);
    }

    public @NotNull Optional<ResourceKey<V>> getResourceKey(V value) {
        return registry.getResourceKey(value);
    }

    @MCVersionDependentBehaviour
    public @NotNull Set<Map.Entry<ResourceKey<V>, V>> getEntries() {
        return registry.entrySet();
    }

    @Override
    public @NotNull Iterator<V> iterator() {
        return registry.iterator();
    }
}
