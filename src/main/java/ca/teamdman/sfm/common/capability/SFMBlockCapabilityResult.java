package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.common.util.NonNullConsumer;

import java.util.Optional;

@MCVersionDependentBehaviour
public record SFMBlockCapabilityResult<CAP>(LazyOptional<CAP> capability) {
    public static <CAP> SFMBlockCapabilityResult<CAP> empty() {
        return new SFMBlockCapabilityResult<>(LazyOptional.empty());
    }

    public Optional<CAP> resolve() {
        return capability.resolve();
    }

    public boolean isPresent() {
        return capability.isPresent();
    }

    public void addListener(NonNullConsumer<LazyOptional<CAP>> listener) {
        capability.addListener(listener);
    }
}
