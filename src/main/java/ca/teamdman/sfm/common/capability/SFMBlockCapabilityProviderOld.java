package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.util.Stored;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public interface SFMBlockCapabilityProviderOld {
    @Nullable ICapabilityProvider getProviderFor(LevelAccessor level, @Stored BlockPos pos);
}
