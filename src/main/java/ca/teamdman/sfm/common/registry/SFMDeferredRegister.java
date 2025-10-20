package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Supplier;

/// The thing that registers stuff to a registry.
/// Helps reduce {@link ca.teamdman.sfm.common.util.MCVersionDependentBehaviour}.
/// Can be acquired using {@link SFMDeferredRegisterBuilder}
@MCVersionDependentBehaviour
public class SFMDeferredRegister<T> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final @Nullable DeferredRegister<T> inner;
    private SFMRegistryWrapper<T> registryWrapper;

    public SFMDeferredRegister(
            ResourceKey<? extends Registry<T>> registryKey,
            @Nullable DeferredRegister<T> inner
    ) {
        this.registryKey = registryKey;
        this.registryWrapper = new SFMRegistryWrapper<>(registryKey);
        this.inner = inner;
    }

    public void register(IEventBus bus) {
        if (inner == null) return;
        inner.register(bus);
    }

    public int size() {
        return inner.getEntries().size();
    }

    public String namespace() {
        return registryKey.location().getNamespace();
    }

    @SuppressWarnings({"unchecked", "Convert2Diamond"})
    public <I extends T> SFMRegistryObject<T, I> register(
            String name,
            Supplier<? extends I> supplier
    ) {
        if (inner == null) {
            return registerEmpty(SFMResourceLocation.fromNamespaceAndPath(namespace(), name));
        }
        RegistryObject<? extends I> object = inner.register(name, supplier);
        return new SFMRegistryObject<T,I>(
                (ResourceKey<? extends Registry<I>>) registryKey,
                object
        );
    }

    @SuppressWarnings({"unchecked", "Convert2Diamond"})
    public <I extends T> SFMRegistryObject<T, I> registerEmpty(
            ResourceLocation id
    ) {
        return new SFMRegistryObject<T, I>(
                (ResourceKey<? extends Registry<I>>) registryKey,
                null
        );
    }

    public SFMRegistryWrapper<T> registry() {
        return registryWrapper;
    }

    /// Returns the things registered by this instance.
    /// Not to be confused with getting all entries in the entire registry.
    public ArrayList<SFMRegistryObject<T, ? extends T>> getOurEntries() {
        if (inner == null) {
            SFM.LOGGER.warn("Attempted to get entries from a registry that was not created via DeferredRegisterBuilder! namespace={}", this.namespace());
            return new ArrayList<>();
        }
        var entries = inner.getEntries();
        ArrayList<SFMRegistryObject<T, ? extends T>> rtn = new ArrayList<>(entries.size());
        for (RegistryObject<T> entry : entries) {
            rtn.add(new SFMRegistryObject<>(registryKey, entry));
        }
        return rtn;
    }
}
