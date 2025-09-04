package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.neoforged.neoforge.common.capabilities.Capability;

@MCVersionDependentBehaviour
public record SFMBlockCapabilityWrapper<CAP>(
        Capability<CAP> capability
) {
}
