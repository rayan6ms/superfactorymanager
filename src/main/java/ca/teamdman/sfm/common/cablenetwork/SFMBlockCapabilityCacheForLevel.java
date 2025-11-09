package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.common.capability.SFMBlockCapabilityKind;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityResult;
import ca.teamdman.sfm.common.util.NotStored;
import ca.teamdman.sfm.common.util.SFMDirections;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/// TODO: fix invalidation not being present on 1.20.3+ lol
/// Add test.
/// See {@link net.neoforged.neoforge.capabilities.BlockCapability} and {@link net.neoforged.neoforge.capabilities.BlockCapabilityCache}
public class SFMBlockCapabilityCacheForLevel {
    // Position => Capability => Direction => LazyOptional
    // We don't use an EnumMap here for Direction because we need to support the null key
    private final Long2ObjectMap<Object2ObjectOpenHashMap<SFMBlockCapabilityKind<?>, SFMDirections.NullableDirectionEnumMap<SFMBlockCapabilityResult<?>>>> CACHE = new Long2ObjectOpenHashMap<>();

    // Chunk position => Set of Block positions
    private final Long2ObjectMap<LongArraySet> CHUNK_TO_BLOCK_POSITIONS = new Long2ObjectOpenHashMap<>();

    /// Used in 1.20.3+ for capability invalidation listening
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final Level level;

    public SFMBlockCapabilityCacheForLevel(Level level) {

        this.level = level;
    }

    public void clear() {

        CACHE.clear();
        CHUNK_TO_BLOCK_POSITIONS.clear();
    }

    public int size() {

        return CACHE
                .values()
                .stream()
                .flatMap(x -> x.values().stream())
                .mapToInt(SFMDirections.NullableDirectionEnumMap::size)
                .sum();
    }

    public void overwriteFromOther(
            @NotStored BlockPos pos,
            SFMBlockCapabilityCacheForLevel other
    ) {

        var found = other.CACHE.get(pos.asLong());
        if (found != null) {
            CACHE.put(pos.asLong(), new Object2ObjectOpenHashMap<>(found));
        }
        addToChunkMap(pos);
    }

    public <CAP> @Nullable SFMBlockCapabilityResult<CAP> getCapability(
            @NotStored BlockPos pos,
            SFMBlockCapabilityKind<CAP> capKind,
            @Nullable Direction direction
    ) {

        var capMap = CACHE.get(pos.asLong());
        if (capMap != null) {
            var dirMap = capMap.get(capKind);
            if (dirMap != null) {
                var found = dirMap.get(direction);
                if (found == null) {
                    return null;
                } else {
                    //noinspection unchecked
                    return (SFMBlockCapabilityResult<CAP>) found;
                }
            }
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void putAll(SFMBlockCapabilityCacheForLevel other) {

        for (var entry : other.CACHE.long2ObjectEntrySet()) {
            long pos = entry.getLongKey();

            var capMap = entry.getValue();
            for (var e : capMap.entrySet()) {
                SFMBlockCapabilityKind<?> capKind = e.getKey();

                var dirMap = e.getValue();
                for (Direction direction : SFMDirections.DIRECTIONS_WITHOUT_NULL) {
                    SFMBlockCapabilityResult<?> cap = dirMap.get(direction);
                    if (cap != null) {
                        putCapability(BlockPos.of(pos), (SFMBlockCapabilityKind) capKind, direction, cap);
                    }
                }
            }
        }
    }

    public Stream<BlockPos> getPositions() {

        return CACHE.keySet().longStream().mapToObj(BlockPos::of);
    }

    public void remove(
            @NotStored BlockPos pos,
            SFMBlockCapabilityKind<?> capKind,
            @Nullable Direction direction
    ) {

        // Get the (pos, ...) entry.
        var posEntry = CACHE.get(pos.asLong());
        if (posEntry == null) {
            return;
        }

        // Get the (pos, capKind, ...directions) entry.
        var capKindEntry = posEntry.get(capKind);
        if (capKindEntry == null) {
            return;
        }

        // Remove the given direction.
        capKindEntry.remove(direction);

        // We are done if there are other directions keeping the cache entry for (pos, capKind, ...) alive
        if (!capKindEntry.isEmpty()) {
            return;
        }

        // capKind in (pos, capKind, ...) is now empty, remove it.
        posEntry.remove(capKind);

        // We are done if there exists other (pos, ...) entries.
        if (!posEntry.isEmpty()) {
            return;
        }

        // pos is now empty, remove it.
        CACHE.remove(pos.asLong());
        removeFromChunkMap(pos);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public <CAP> void putCapability(
            @NotStored BlockPos posIn,
            SFMBlockCapabilityKind<CAP> capKind,
            @Nullable Direction direction,
            SFMBlockCapabilityResult<CAP> cap
    ) {
        // We can only listen for invalidation on a server level
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // Ensure the position we bind to the listener lambda is immutable.
        final BlockPos pos = posIn.immutable();

        // Get the entry for (pos, ...)
        Object2ObjectOpenHashMap<SFMBlockCapabilityKind<?>, SFMDirections.NullableDirectionEnumMap<SFMBlockCapabilityResult<?>>>
                posEntry = CACHE.computeIfAbsent(pos.asLong(), k -> new Object2ObjectOpenHashMap<>());

        // Get the entry for the (pos, capKind, ...)
        SFMDirections.NullableDirectionEnumMap<SFMBlockCapabilityResult<?>>
                capKindEntry = posEntry.computeIfAbsent(capKind, k -> new SFMDirections.NullableDirectionEnumMap<>());

        // Track the (pos, capKind, direction) entry
        capKindEntry.put(direction, cap);
        addToChunkMap(pos);

        // Create the listener to remove the entry when informed by the level to do so
        ICapabilityInvalidationListener listener = () -> {
            SFMBlockCapabilityCacheForLevel.this.remove(pos, capKind, direction);
            boolean listenerRemainsValid = false;
            return listenerRemainsValid;
        };

        // Track a strong reference to the listener in the SFMBlockCapabilityResult
        // We MUST avoid it getting garbage collected by CapabilityListenerHolder
        cap.addInvalidationListener(listener);

        // Register the listener to the level
        serverLevel.registerCapabilityListener(pos, listener);
    }

    public void bustCacheForChunk(ChunkAccess chunkAccess) {

        long chunkKey = chunkAccess.getPos().toLong();
        LongArraySet blockPositions = CHUNK_TO_BLOCK_POSITIONS.get(chunkKey);
        if (blockPositions != null) {
            for (var blockPos : blockPositions) {
                CACHE.remove(blockPos.longValue());
            }
            CHUNK_TO_BLOCK_POSITIONS.remove(chunkKey);
        }
    }

    private void addToChunkMap(@NotStored BlockPos pos) {

        ChunkPos chunkPos = new ChunkPos(pos);
        long chunkKey = chunkPos.toLong();
        long blockPos = pos.asLong();
        CHUNK_TO_BLOCK_POSITIONS.computeIfAbsent(chunkKey, k -> new LongArraySet()).add(blockPos);
    }

    private void removeFromChunkMap(@NotStored BlockPos pos) {

        ChunkPos chunkPos = new ChunkPos(pos);
        long chunkKey = chunkPos.toLong();
        long blockPos = pos.asLong();
        LongArraySet blockPosSet = CHUNK_TO_BLOCK_POSITIONS.get(chunkKey);
        if (blockPosSet != null) {
            blockPosSet.remove(blockPos);
            if (blockPosSet.isEmpty()) {
                CHUNK_TO_BLOCK_POSITIONS.remove(chunkKey);
            }
        }
    }

}
