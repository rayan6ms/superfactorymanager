package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.common.capability.IRedstoneSignalStorage;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityKind;
import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class SFMCapabilities {
    public static final SFMBlockCapabilityKind<IRedstoneSignalStorage> REDSTONE_HANDLER = new SFMBlockCapabilityKind<>(
            BlockCapability.createSided(
                    SFMResourceLocation.fromSFMPath("redstone_storage"),
                    IRedstoneSignalStorage.class
            ));
}
