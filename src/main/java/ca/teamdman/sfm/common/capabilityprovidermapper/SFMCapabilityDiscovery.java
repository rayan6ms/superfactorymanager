package ca.teamdman.sfm.common.capabilityprovidermapper;

import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.LevelCapabilityCache;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.logging.TranslatableLogger;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.NotStored;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.Nullable;

public class SFMCapabilityDiscovery {
    @MCVersionDependentBehaviour
    public static <CAP> @Nullable CAP getCapability(
            CableNetwork cableNetwork,
            BlockCapability<CAP, @Nullable Direction> capKind,
            @NotStored BlockPos pos,
            @Nullable Direction direction,
            TranslatableLogger logger
    ) {
        LevelCapabilityCache levelCapabilityCache = cableNetwork.getLevelCapabilityCache();
        // we assume that if there is a cache entry that it is adjacent to a cable

        // It is a precondition to enter the cache that the capability is adjacent to a cable
        @Nullable CAP cached = discoverCapabilityFromCache(
                capKind,
                pos,
                direction,
                logger,
                levelCapabilityCache
        );
        if (cached != null) return cached;

        // NEED TO DISCOVER

        // any BlockPos can have labels assigned
        // we must only proceed here if there is an adjacent cable from this network
        if (!cableNetwork.isAdjacentToCable(pos)) {
            logger.warn(x -> x.accept(LocalizationKeys.LOGS_MISSING_ADJACENT_CABLE.get(pos)));
            return null;
        }

        return discoverCapabilityFromLevel(cableNetwork.getLevel(), capKind, pos, direction, logger, levelCapabilityCache);
    }

    private static <CAP> @Nullable CAP discoverCapabilityFromCache(
            BlockCapability<CAP, @Nullable Direction> capKind,
            @NotStored BlockPos pos,
            @Nullable Direction direction,
            TranslatableLogger logger,
            LevelCapabilityCache levelCapabilityCache
    ) {
        var found = levelCapabilityCache.getCapability(pos, capKind, direction);
        if (found != null) {
            CAP cap = found.getCapability();
            if (cap != null) {
                // CACHE HIT
                logger.trace(x -> x.accept(LocalizationKeys.LOG_CAPABILITY_CACHE_HIT.get(
                        pos,
                        capKind.name(),
                        direction
                )));
                return cap;
            } else {
                // CACHE HIT BUT STALE
                logger.trace(x -> x.accept(LocalizationKeys.LOG_CAPABILITY_CACHE_HIT_INVALID.get(
                        pos,
                        capKind.name(),
                        direction
                )));
            }
        } else {
            // CACHE MISS
            logger.trace(x -> x.accept(LocalizationKeys.LOG_CAPABILITY_CACHE_MISS.get(pos, capKind.name(), direction)));
        }
        return null;
    }

    private static <CAP> @Nullable CAP discoverCapabilityFromLevel(
            Level level,
            BlockCapability<CAP, @Nullable Direction> capKind,
            @NotStored BlockPos pos,
            @Nullable Direction direction,
            TranslatableLogger logger,
            LevelCapabilityCache levelCapabilityCache
    ) {
        var cap = level.getCapability(capKind, pos, direction);
        if (cap != null) {
            if (!(level instanceof ServerLevel serverLevel)) {
                return null;
            }
            var found = BlockCapabilityCache.<CAP, @Nullable Direction>create(
                    capKind,
                    serverLevel,
                    pos,
                    direction,
                    () -> true,
                    () ->  levelCapabilityCache.remove(pos, capKind, direction)
            );
            levelCapabilityCache.putCapability(pos, capKind, direction, found);
            return cap;
        } else {
            logger.warn(x -> x.accept(LocalizationKeys.LOGS_EMPTY_CAPABILITY.get(pos, capKind.name(), direction)));
            return null;
        }

//        // old
//        var capabilityProvider = SFMCapabilityProviderMappers.discoverCapabilityProvider(level, pos.immutable());
//        if (capabilityProvider != null) {
//            var cap = capabilityProvider.getCapability(capKind, direction);
//            if (cap.isPresent()) {
//                levelCapabilityCache.putCapability(pos, capKind, direction, cap);
//                cap.addListener(x -> levelCapabilityCache.remove(pos, capKind, direction));
//            } else {
//                logger.warn(x -> x.accept(LocalizationKeys.LOGS_EMPTY_CAPABILITY.get(pos, capKind.getName(), direction)));
//            }
//            return cap;
//        } else {
//            logger.warn(x -> x.accept(LocalizationKeys.LOGS_MISSING_CAPABILITY_PROVIDER.get(
//                    pos,
//                    capKind.getName(),
//                    direction
//            )));
//            return LazyOptional.empty();
//        }
    }
}
