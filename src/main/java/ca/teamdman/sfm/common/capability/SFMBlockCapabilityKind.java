package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.neoforged.neoforge.common.capabilities.Capability;

@MCVersionDependentBehaviour
public record SFMBlockCapabilityKind<CAP>(
        Capability<CAP> capabilityKind
) {
    public String getName() {
        return capabilityKind.getName();
    }
}
