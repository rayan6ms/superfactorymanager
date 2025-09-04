package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@MCVersionDependentBehaviour
public record SFMBlockCapabilityResult<CAP>(@Nullable CAP capability) {
    public static <CAP> SFMBlockCapabilityResult<CAP> empty() {
        return new SFMBlockCapabilityResult<>(null);
    }

    public Optional<CAP> resolve() {
        return Optional.ofNullable(capability());
    }

    public boolean isPresent() {
        return capability != null;
    }
}
