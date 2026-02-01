package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.common.capability.IRedstoneSignalStorage;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityKind;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.neoforged.neoforge.capabilities.BlockCapability;

/// 1.19.2 -> RegisterCapabilitiesEvent event listener method
/// 1.20.3 -> BlockCapability.createSided static field
@MCVersionDependentBehaviour
public class SFMCapabilities {
    public static final SFMBlockCapabilityKind<IRedstoneSignalStorage> REDSTONE_HANDLER = new SFMBlockCapabilityKind<>(
            BlockCapability.createSided(
                    SFMResourceLocation.fromSFMPath("redstone_storage"),
                    IRedstoneSignalStorage.class
            ));
}
