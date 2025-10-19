package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.util.SFMEnvironmentUtils;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.function.Supplier;

/// The thing that registers stuff to a registry.
/// Helps reduce {@link ca.teamdman.sfm.common.util.MCVersionDependentBehaviour}
public class SFMDeferredRegister<T> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String modId;
    private final DeferredRegister<T> inner;
    private Supplier<SFMRegistryWrapper<T>> registryGetter;
    private boolean isClientOnly;

    public SFMDeferredRegister(
            ResourceKey<? extends Registry<T>> registryKey,
            String modId,
            boolean optional
    ) {
        this.registryKey = registryKey;
        this.modId = modId;
        if (optional) {
            this.inner = DeferredRegister.createOptional(registryKey, modId);
        } else {
            this.inner = DeferredRegister.create(registryKey, modId);
        }
        this.registryGetter = () -> new SFMRegistryWrapper<>(registryKey);
        if (SFMEnvironmentUtils.isInIDE()) {
            SFM.LOGGER.debug("Registering deferred register for mod='{}' {}", modId,registryKey);
        }
    }

    public static <T> SFMDeferredRegister<T> createForExistingRegistry(
            SFMRegistryWrapper<T> existingRegistry,
            String modId
    ) {
        return new SFMDeferredRegister<>(existingRegistry.getRegistryKey(), modId, false);
    }

    public static <T> SFMDeferredRegister<T> createForCustomClientRegistry(
            ResourceKey<? extends Registry<T>> registryKey,
            String modId
    ) {
        var rtn = createForCustomRegistry(registryKey, modId, true);
        rtn.isClientOnly=true;
        return rtn;
    }

    public static <T> SFMDeferredRegister<T> createForCustomRegistry(
            ResourceKey<? extends Registry<T>> registryKey,
            String modId
    ){
        return createForCustomRegistry(registryKey, modId, false);
    }

    public static <T> SFMDeferredRegister<T> createForCustomRegistry(
            ResourceKey<? extends Registry<T>> registryKey,
            String modId,
            boolean optional
    ) {
        SFMDeferredRegister<T> deferredRegister = new SFMDeferredRegister<>(registryKey, modId, optional);
        if (SFMEnvironmentUtils.isInIDE()) {
            SFM.LOGGER.debug("Creating registry for mod='{}' {}", modId,registryKey);
        }
        deferredRegister.inner.makeRegistry(() -> new RegistryBuilder<T>().setName(registryKey.location()));
        deferredRegister.registryGetter = () -> new SFMRegistryWrapper<>(registryKey);
        return deferredRegister;
    }

    public void register(IEventBus bus) {
        if (isClientOnly && !SFMEnvironmentUtils.isClient()) return;
        inner.register(bus);
    }

    public int size() {
        return inner.getEntries().size();
    }

    @SuppressWarnings("unchecked")
    public <X extends T> SFMRegistryObject<X> register(
            String name,
            Supplier<? extends X> supplier
    ) {
        RegistryObject<X> object = inner.register(name, supplier);
        return new SFMRegistryObject<>(
                (ResourceKey<? extends Registry<X>>) registryKey,
                object
        );
    }

    @SuppressWarnings("unchecked")
    public <X extends T> SFMRegistryObject<X> empty(
            String name
    ) {
        return new SFMRegistryObject<>(
                (ResourceKey<? extends Registry<X>>) registryKey,
                RegistryObject.createOptional(
                        SFMResourceLocation.fromNamespaceAndPath(modId, name),
                        inner.getRegistryKey(),
                        modId
                )
        );
    }

    public SFMRegistryWrapper<T> registry() {
        return registryGetter.get();
    }

    /// Returns the things registered by this instance.
    /// Not to be confused with getting all entries in the entire registry.
    public ArrayList<SFMRegistryObject<T>> getOurEntries() {
        var entries = inner.getEntries();
        ArrayList<SFMRegistryObject<T>> rtn = new ArrayList<>(entries.size());
        for (RegistryObject<T> entry : entries) {
            rtn.add(new SFMRegistryObject<>(registryKey, entry));
        }
        return rtn;
    }
}
