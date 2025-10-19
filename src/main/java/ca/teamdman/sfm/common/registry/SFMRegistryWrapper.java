package ca.teamdman.sfm.common.registry;


import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/// Helps reduce {@link ca.teamdman.sfm.common.util.MCVersionDependentBehaviour}
public final class SFMRegistryWrapper<T> implements Iterable<T> {
    private @Nullable @MCVersionDependentBehaviour Registry<T> maybeInner;
    private final ResourceKey<? extends Registry<T>> registryKey;

    public SFMRegistryWrapper(
            @MCVersionDependentBehaviour
            Registry<T> inner
    ) {
        this.maybeInner = inner;
        this.registryKey = inner.key();
    }

    public SFMRegistryWrapper(ResourceKey<? extends Registry<T>> registryKey) {
        this.maybeInner = null;
        this.registryKey = registryKey;
    }

    @MCVersionDependentBehaviour
    public @Nullable T getValue(ResourceLocation resourceTypeId) {
        return getInner().get(resourceTypeId);
    }

    @MCVersionDependentBehaviour
    public Set<ResourceLocation> getKeys() {
        return getInner().keySet();
    }

    public Iterable<T> iterValues() {
        return getInner();
    }

    public Collection<T> getValues() {
        return streamValues().collect(Collectors.toList());
    }

    public Stream<T> streamValues() {
        return StreamSupport.stream(getInner().spliterator(), false);
    }

    public @Nullable ResourceLocation getKey(T value) {
        return getInner().getKey(value);
    }

    public Optional<ResourceKey<T>> getResourceKey(T value) {
        return getInner().getResourceKey(value);
    }

    @MCVersionDependentBehaviour
    public Set<Map.Entry<ResourceKey<T>, T>> getEntries() {
        return getInner().entrySet();
    }

    @Override
    public Iterator<T> iterator() {
        return getInner().iterator();
    }

    public ResourceKey<? extends Registry<T>> getRegistryKey() {
        return registryKey;
    }

    public boolean containsKey(ResourceLocation location) {
        return getInner().containsKey(location);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public @MCVersionDependentBehaviour Registry<T> getInner() {
        if (maybeInner == null) {
            maybeInner = (Registry<T>) BuiltInRegistries.REGISTRY.get((ResourceKey) registryKey);
        }
        return maybeInner;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SFMRegistryWrapper) obj;
        return Objects.equals(this.getInner(), that.getInner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInner());
    }

    @Override
    public String toString() {
        return "SFMRegistryWrapper[" +
               "inner=" + maybeInner + ']';
    }

}
