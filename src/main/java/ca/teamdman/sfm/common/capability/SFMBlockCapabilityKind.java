package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

/// In NeoForge for Minecraft 1.20.3, the {@code Capability<CAP>} type is replaced with {@code BlockCapability<CAP, CONTEXT>}.
/// We use {@link SFMBlockCapabilityKind} to wrap the capability kind.
/// We use {@link SFMBlockCapabilityResult} to wrap the results of capability queries.
/// This wrapper minimizes entropy in the codebase by isolating the differences in the capability kind.
///
/// This class helps keep {@link MCVersionDependentBehaviour} out of other classes.
@MCVersionDependentBehaviour
public record SFMBlockCapabilityKind<CAP>(
        BlockCapability<CAP, @Nullable Direction> capabilityKind
) {
    public String getName() {
        return capabilityKind.name().toString();
    }
}
