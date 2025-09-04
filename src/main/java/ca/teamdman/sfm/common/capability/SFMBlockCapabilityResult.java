package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import org.jetbrains.annotations.NotNull;

@MCVersionDependentBehaviour
public record SFMBlockCapabilityResult<CAP>(LazyOptional<CAP> capability) {
    public static <CAP> SFMBlockCapabilityResult<CAP> empty() {
        return new SFMBlockCapabilityResult<>(LazyOptional.empty());
    }

    public @NotNull CAP unwrap() {
        return capability.orElseThrow(IllegalStateException::new);
    }

    public boolean isPresent() {
        return capability.isPresent();
    }

    public void addListener(NonNullConsumer<LazyOptional<CAP>> listener) {
        capability.addListener(listener);
    }
}
