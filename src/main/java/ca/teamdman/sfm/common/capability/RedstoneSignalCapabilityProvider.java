package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import org.jetbrains.annotations.Nullable;

/// In NeoForge for Minecraft 1.20.3, the way capabilities are discovered changed.
/// See {@link SFMBlockCapabilityProvider} for more information.
/// This is the fallback provider for the "built-in" behaviour provided by the modding framework.
public class RedstoneSignalCapabilityProvider implements SFMBlockCapabilityProvider {
    @SuppressWarnings("Convert2Lambda")
    @Override
    public @Nullable IBlockCapabilityProvider<?, @Nullable Direction> createForKind(SFMBlockCapabilityKind<?> capabilityKind) {
        if (!capabilityKind.matchesResourceType(SFMResourceTypes.REDSTONE.get())) return null;
        return new IBlockCapabilityProvider<>() {
            @Override
            public @Nullable Object getCapability(
                    Level level,
                    BlockPos pos,
                    BlockState state,
                    @Nullable BlockEntity blockEntity,
                    Direction context
            ) {
                try {
                    // Wrap in try-catch since getSignal doesn't explicitly allow the null direction
                    int signal = state.getSignal(level, pos, context);
                    return new RedstoneSignalStorage(signal, 15);
                } catch (Throwable t) {
                    return null;
                }
            }
        };
    }
}
