package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.LevelCapabilityCache;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.logging.TranslatableLogger;
import ca.teamdman.sfm.common.registry.SFMCapabilityProviderMappers;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import ca.teamdman.sfm.common.util.NotStored;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SFMCapabilityDiscovery {
    @MCVersionDependentBehaviour
    public static <CAP> @NotNull LazyOptional<CAP> discoverCapabilityFromNetwork(
            CableNetwork cableNetwork,
            Capability<CAP> capKind,
            @NotStored BlockPos pos,
            @Nullable Direction direction,
            TranslatableLogger logger
    ) {
        LevelCapabilityCache levelCapabilityCache = cableNetwork.getLevelCapabilityCache();

        // It is a precondition to enter the cache that the capability is adjacent to a cable
        LazyOptional<CAP> cached = discoverCapabilityFromCache(
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
            return LazyOptional.empty();
        }

        return discoverCapabilityFromLevel(cableNetwork.getLevel(), capKind, pos, direction, logger, levelCapabilityCache);
    }

    private static <CAP> @Nullable LazyOptional<CAP> discoverCapabilityFromCache(
            Capability<CAP> capKind,
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
            logger.trace(x -> x.accept(LocalizationKeys.LOG_CAPABILITY_CACHE_MISS.get(pos, capKind.getName(), direction)));
        }
        return null;
    }

    private static <CAP> @NotNull LazyOptional<CAP> discoverCapabilityFromLevel(
            Level level,
            Capability<CAP> capKind,
            @NotStored BlockPos pos,
            @Nullable Direction direction,
            TranslatableLogger logger,
            LevelCapabilityCache levelCapabilityCache
    ) {
        var capabilityProvider = SFMCapabilityProviderMappers.discoverCapabilityProvider(level, pos.immutable());
        if (capabilityProvider != null) {
            var cap = capabilityProvider.getCapability(capKind, direction);
            if (cap.isPresent()) {
                levelCapabilityCache.putCapability(pos, capKind, direction, cap);
                cap.addListener(x -> levelCapabilityCache.remove(pos, capKind, direction));
            } else {
                logger.warn(x -> x.accept(LocalizationKeys.LOGS_EMPTY_CAPABILITY.get(pos, capKind.getName(), direction)));
            }
            return cap;
        } else {
            logger.warn(x -> x.accept(LocalizationKeys.LOGS_MISSING_CAPABILITY_PROVIDER.get(
                    pos,
                    capKind.getName(),
                    direction
            )));
            return LazyOptional.empty();
        }
    }
}
