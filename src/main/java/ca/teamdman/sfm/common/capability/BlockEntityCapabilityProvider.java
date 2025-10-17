package ca.teamdman.sfm.common.capability;

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
public class BlockEntityCapabilityProvider implements SFMBlockCapabilityProvider {
    @SuppressWarnings("Convert2Lambda")
    @Override
    public @Nullable IBlockCapabilityProvider<?, @Nullable Direction> createForKind(SFMBlockCapabilityKind<?> capabilityKind) {
        return new IBlockCapabilityProvider<>() {
            @Override
            public @Nullable Object getCapability(
                    Level level,
                    BlockPos pos,
                    BlockState state,
                    @Nullable BlockEntity blockEntity,
                    Direction context
            ) {
                return level.getCapability(
                        capabilityKind.capabilityKind(),
                        pos,
                        state,
                        blockEntity,
                        context
                );
            }
        };
    }

    @Override
    public int priority() {
        return -100; // check this one last
    }
}
