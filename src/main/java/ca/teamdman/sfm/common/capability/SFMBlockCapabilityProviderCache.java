package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.SFMBlockCapabilityCacheForLevel;
import ca.teamdman.sfm.common.registry.SFMBlockCapabilityProviders;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/// This cache is for reducing duplicate computation when looking up capabilities by kind.
/// The first time a capability kind is requested, we identify the {@link SFMBlockCapabilityProvider} that match and sort them by priority.
/// The capabilities for stochastic {@link ManagerBlockEntity} operations are cached in the {@link CableNetwork} using a {@link SFMBlockCapabilityCacheForLevel}.
public class SFMBlockCapabilityProviderCache {
    private static final Object2ObjectOpenHashMap<SFMBlockCapabilityKind<?>, ArrayList<SFMBlockCapabilityProvider<?>>>
            BLOCK_CAPABILITY_PROVIDERS_BY_KIND = new Object2ObjectOpenHashMap<>();

    public static <CAP> SFMBlockCapabilityResult<CAP> getCapabilityFromLevel(
            SFMBlockCapabilityKind<CAP> capKind,
            Level level,
            BlockPos pos,
            BlockState blockState,
            BlockEntity blockEntity,
            @Nullable Direction direction
    ) {
        for (var capabilityProviderMapper : getCapabilityProvidersForKindFast(capKind)) {
            var capability = capabilityProviderMapper.getCapability(
                    capKind,
                    level,
                    pos,
                    blockState,
                    blockEntity,
                    direction
            );
            if (capability.isPresent()) {
                return capability;
            }
        }
        return SFMBlockCapabilityResult.of(level.getCapability(
                capKind.capabilityKind(),
                pos,
                direction
        ));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <CAP> ArrayList<SFMBlockCapabilityProvider<CAP>> getCapabilityProvidersForKindFast(
            SFMBlockCapabilityKind<CAP> capabilityKind
    ) {
        return (ArrayList<SFMBlockCapabilityProvider<CAP>>) (ArrayList) BLOCK_CAPABILITY_PROVIDERS_BY_KIND.computeIfAbsent(
                capabilityKind,
                __ -> getCapabilityProvidersForKind(capabilityKind)
        );
    }

    private static <CAP> ArrayList<SFMBlockCapabilityProvider<?>> getCapabilityProvidersForKind(
            SFMBlockCapabilityKind<CAP> capabilityKind
    ) {
        ArrayList<SFMBlockCapabilityProvider<CAP>> rtn = new ArrayList<>();
        for (SFMBlockCapabilityProvider<?> mapper : SFMBlockCapabilityProviders.getAllProviders()) {
            if (mapper.matchesCapabilityKind(capabilityKind)) {
                // This cast is safe because we just checked the capability kind
                @SuppressWarnings("unchecked")
                SFMBlockCapabilityProvider<CAP> typedMapper = (SFMBlockCapabilityProvider<CAP>) mapper;
                rtn.add(typedMapper);
            }
        }
        return (ArrayList<SFMBlockCapabilityProvider<?>>) (ArrayList) rtn;
    }

}
