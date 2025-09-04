package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@MCVersionDependentBehaviour
public record SFMBlockCapabilityResult<CAP>(@Nullable CAP capability) {
    public static <CAP> SFMBlockCapabilityResult<CAP> empty() {
        return new SFMBlockCapabilityResult<>(null);
    }

    public @NotNull CAP unwrap() {
        return Objects.requireNonNull(capability);
    }

    public boolean isPresent() {
        return capability != null;
    }
}
