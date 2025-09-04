package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

@MCVersionDependentBehaviour
public record SFMBlockCapabilityWrapper<CAP>(
        BlockCapability<CAP, @Nullable Direction> capability
) {
}
