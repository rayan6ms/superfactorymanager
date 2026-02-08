package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.registry.registration.SFMGlobalBlockCapabilityProviders;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.neoforged.neoforge.capabilities.CapabilityListenerHolder;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/// In Minecraft before 1.20.3, NeoForge uses {@code LazyOptional<T>} for the type of retrieved Capabilities.
/// In Minecraft 1.20.3 and later, {@code @Nullable T} is used instead.
/// Between Minecraft 1.20 and Minecraft 1.20.1, SFM switches from using Forge to NeoForge.
/// The package path for many classes changes in this transition.
/// To minimize entropy in the SFM codebase, we wrap the different optional types in {@link SFMBlockCapabilityResult}
/// Capabilities are retrieved by querying {@link SFMGlobalBlockCapabilityProviders} with a {@link SFMBlockCapabilityKind}
///
/// Note that we MUST hold a STRONG reference to the {@link ICapabilityInvalidationListener}
/// so that {@link CapabilityListenerHolder} doesn't drop our listener without it being called.
///
/// This class helps keep {@link MCVersionDependentBehaviour} out of other classes.
@SuppressWarnings("UnstableApiUsage") // javadoc lol
@MCVersionDependentBehaviour
public record SFMBlockCapabilityResult<CAP>(
        @Nullable CAP inner,
        Mutable<ICapabilityInvalidationListener> listener
) {

    public static <CAP> SFMBlockCapabilityResult<CAP> of(@Nullable CAP capability) {
        return new SFMBlockCapabilityResult<>(capability, new MutableObject<>());
    }

    public static <CAP> SFMBlockCapabilityResult<CAP> empty() {

        return SFMBlockCapabilityResult.of(null);
    }

    public @NotNull CAP unwrap() {
        return Objects.requireNonNull(inner);
    }

    public boolean isPresent() {
        return inner != null;
    }

    public void addInvalidationListener(ICapabilityInvalidationListener listener) {
        if (this.listener.getValue() != null) {
            throw new IllegalStateException("Listener already set, clobbering it will cause the old listener to never run!");
        }

        this.listener.setValue(listener);
    }

}
