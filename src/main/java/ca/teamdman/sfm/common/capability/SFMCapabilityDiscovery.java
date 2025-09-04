package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.LevelCapabilityCache;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.logging.TranslatableLogger;
import ca.teamdman.sfm.common.registry.SFMCapabilityProviderMappers;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.NotStored;
import ca.teamdman.sfm.common.util.SFMDirections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SFMCapabilityDiscovery {
    @MCVersionDependentBehaviour
    public static <CAP> @NotNull SFMBlockCapabilityResult<CAP> discoverCapabilityFromNetwork(
            CableNetwork cableNetwork,
            SFMBlockCapabilityKind<CAP> capKind,
            @NotStored BlockPos pos,
            @Nullable Direction direction,
            TranslatableLogger logger
    ) {
        LevelCapabilityCache levelCapabilityCache = cableNetwork.getLevelCapabilityCache();

        // It is a precondition to enter the cache that the capability is adjacent to a cable
        SFMBlockCapabilityResult<CAP> cached = discoverCapabilityFromCache(
                capKind,
                pos,
                direction,
                logger,
                levelCapabilityCache
        );
        if (cached.isPresent()) return cached;

        // NEED TO DISCOVER

        // any BlockPos can have labels assigned
        // we must only proceed here if there is an adjacent cable from this network
        if (!cableNetwork.isAdjacentToCable(pos)) {
            logger.warn(x -> x.accept(LocalizationKeys.LOGS_MISSING_ADJACENT_CABLE.get(pos)));
            return SFMBlockCapabilityResult.empty();
        }

        if (!(cableNetwork.getLevel() instanceof ServerLevel serverLevel)) {
            return SFMBlockCapabilityResult.empty();
        }
        SFMBlockCapabilityResult<CAP> cap = discoverCapabilityFromLevel(
                serverLevel,
                capKind,
                pos,
                direction
        );
        if (cap.isPresent()) {
            cap.addListener(x -> levelCapabilityCache.remove(pos, capKind, direction));
            levelCapabilityCache.putCapability(pos, capKind, direction, cap);
        } else {
            logger.warn(x -> x.accept(LocalizationKeys.LOGS_MISSING_CAPABILITY_PROVIDER.get(
                    pos,
                    capKind.getName(),
                    direction
            )));
        }
        return cap;
    }

    private static @NotNull <CAP> SFMBlockCapabilityResult<CAP> discoverCapabilityFromCapabilityProvider(
            SFMBlockCapabilityKind<CAP> capKind,
            @Nullable ICapabilityProvider capabilityProvider,
            @Nullable Direction direction
    ) {
        if (capabilityProvider == null) {
            return SFMBlockCapabilityResult.empty();
        }
        return new SFMBlockCapabilityResult<>(capabilityProvider.getCapability(capKind.capabilityKind(), direction));
    }

    public static boolean hasAnyCapabilityAnyDirection(
            LevelAccessor level,
            BlockPos pos
    ) {
        return SFMResourceTypes.getCapabilities().anyMatch(cap -> {
            for (Direction direction : SFMDirections.DIRECTIONS_WITH_NULL) {
                if (discoverCapabilityFromLevel(level, cap, pos, direction).isPresent()) {
                    return true;
                }
            }
            return false;
        });
    }

    public static <CAP> @NotNull SFMBlockCapabilityResult<CAP> discoverCapabilityFromLevel(
            LevelAccessor level,
            SFMBlockCapabilityKind<CAP> capKind,
            @NotStored BlockPos pos,
            @Nullable Direction direction
    ) {
        var capabilityProvider = SFMCapabilityProviderMappers.discoverCapabilityProvider(level, pos.immutable());
        if (capabilityProvider != null) {
            return discoverCapabilityFromCapabilityProvider(capKind, capabilityProvider, direction);
        } else {
            return SFMBlockCapabilityResult.empty();
        }
    }

    private static <CAP> @NotNull SFMBlockCapabilityResult<CAP> discoverCapabilityFromCache(
            SFMBlockCapabilityKind<CAP> capKind,
            @NotStored BlockPos pos,
            @Nullable Direction direction,
            TranslatableLogger logger,
            LevelCapabilityCache levelCapabilityCache
    ) {
        var found = levelCapabilityCache.getCapability(pos, capKind, direction);
        if (found != null) {
            // CACHE HIT
            if (found.isPresent()) {
                logger.trace(x -> x.accept(LocalizationKeys.LOG_CAPABILITY_CACHE_HIT.get(
                        pos,
                        capKind.getName(),
                        direction
                )));
                return found;
            } else {
                // CACHE HIT BUT STALE
                logger.error(x -> x.accept(LocalizationKeys.LOG_CAPABILITY_CACHE_HIT_INVALID.get(
                        pos,
                        capKind.getName(),
                        direction
                )));
            }
        } else {
            // CACHE MISS
            logger.trace(x -> x.accept(LocalizationKeys.LOG_CAPABILITY_CACHE_MISS.get(
                    pos,
                    capKind.getName(),
                    direction
            )));
        }
        return SFMBlockCapabilityResult.empty();
    }
}
