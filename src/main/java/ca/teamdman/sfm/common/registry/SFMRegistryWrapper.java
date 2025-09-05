package ca.teamdman.sfm.common.registry;


import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record SFMRegistryWrapper<V>(
        @MCVersionDependentBehaviour
        IForgeRegistry<V> registry
) implements Iterable<V> {
    public @Nullable V getValue(ResourceLocation resourceTypeId) {
        return registry.getValue(resourceTypeId);
    }

    public @NotNull Set<ResourceLocation> getKeys() {
        return registry.getKeys();
    }

    public @NotNull Collection<V> getValues() {
        return registry.getValues();
    }

    public @Nullable ResourceLocation getKey(V value) {
        return registry.getKey(value);
    }

    public @NotNull Optional<ResourceKey<V>> getResourceKey(V value) {
        return registry.getResourceKey(value);
    }

    public @NotNull Set<Map.Entry<ResourceKey<V>, V>> getEntries() {
        return registry.getEntries();
    }

    @Override
    public @NotNull Iterator<V> iterator() {
        return registry.iterator();
    }
}
