package ca.teamdman.sfm.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/// A pointer to something that is registered in a registry.
/// Helps reduce {@link ca.teamdman.sfm.common.util.MCVersionDependentBehaviour}
public class SFMRegistryObject<T> implements Supplier<T> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final RegistryObject<? extends T> inner;

    public SFMRegistryObject(
            ResourceKey<? extends Registry<T>> registryKey,
            RegistryObject<? extends T> object
    ) {
        this.registryKey = registryKey;
        this.inner = object;
    }

    public Optional<ResourceKey<T>> getId() {
        return getRegistry().getResourceKey(inner.get());
    }

    public @Nullable String getPath() {
        return getId().map(ResourceKey::location).map(ResourceLocation::getPath).orElse(null);
    }

    public SFMRegistryWrapper<T> getRegistry() {
        return new SFMRegistryWrapper<>(registryKey);
    }

    @Override
    public T get() {
        return inner.get();
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof SFMRegistryObject<?> that)) return false;

        return registryKey.equals(that.registryKey) && inner.equals(that.inner);
    }

    @Override
    public int hashCode() {
        int result = registryKey.hashCode();
        result = 31 * result + inner.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SFMRegistryObject{" +
               "registryKey=" + registryKey +
               ", inner=" + inner +
               '}';
    }
}
