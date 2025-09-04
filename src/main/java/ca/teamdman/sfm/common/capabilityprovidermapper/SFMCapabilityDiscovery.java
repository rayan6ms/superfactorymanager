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
        // we assume that if there is a cache entry that it is adjacent to a cable
        LevelCapabilityCache levelCapabilityCache = cableNetwork.getLevelCapabilityCache();
        // we assume that if there is a cache entry that it is adjacent to a cable
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

        // NEED TO DISCOVER

        // any BlockPos can have labels assigned
        // we must only proceed here if there is an adjacent cable from this network
        if (!cableNetwork.isAdjacentToCable(pos)) {
            logger.warn(x -> x.accept(LocalizationKeys.LOGS_MISSING_ADJACENT_CABLE.get(pos)));
            return null;
        }
        var cap = cableNetwork.getLevel().getCapability(capKind, pos, direction);
        if (cap != null) {
            if (!(cableNetwork.getLevel() instanceof ServerLevel serverLevel)) {
                return null;
            }
            found = BlockCapabilityCache.<CAP, @Nullable Direction>create(
                    capKind,
                    serverLevel,
                    pos,
                    direction,
                    () -> true,
                    () ->  levelCapabilityCache.remove(pos, capKind, direction)
            );
            levelCapabilityCache.putCapability(pos, capKind, direction, found);
        } else {
            logger.warn(x -> x.accept(LocalizationKeys.LOGS_EMPTY_CAPABILITY.get(pos, capKind.name(), direction)));
        }
        return cap;
    }
}
