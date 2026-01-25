package ca.teamdman.sfm.common.block_network;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.util.*;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;
import java.util.function.BiFunction;

/// A data structure for handling networks of blocks governed by contiguous-touching rules in a level.
///
/// Because this area deals with both block positions and chunk positions, variables should be named to clearly indicate which is being used.
/// E.g., `memberPos` should be `memberBlockPos`
///
public class BlockNetworkManager<T> {
    private final Map<Level, BlockPosMap<BlockNetwork<T>>> networksByLevelBlockPos = new Object2ObjectOpenHashMap<>();

    private final Map<Level, ChunkPosMap<List<BlockNetwork<T>>>> networksByLevelChunk = new Object2ObjectOpenHashMap<>(); // todo: convert to identity hash set

    private final Map<Level, List<BlockNetwork<T>>> networksByLevel = new Object2ObjectOpenHashMap<>();

    private final BiFunction<Level, BlockPos, T> memberFactory;

    public BlockNetworkManager(
            BiFunction<Level, BlockPos, T> memberFactory
    ) {

        this.memberFactory = memberFactory;
    }

    public @Nullable BlockNetwork<T> getNetwork(
            Level level,
            BlockPos blockPos
    ) {

        BlockPosMap<BlockNetwork<T>> blockPosMap = networksByLevelBlockPos.get(level);
        if (blockPosMap == null) return null;
        return blockPosMap.get(blockPos);
    }

    public void clearChunk(
            Level level,
            ChunkPos chunkPos
    ) {

        ChunkPosMap<List<BlockNetwork<T>>> levelChunkPosMap = networksByLevelChunk.get(level);
        if (levelChunkPosMap == null) return;
        List<BlockNetwork<T>> networksForChunk = levelChunkPosMap.get(chunkPos);
        if (networksForChunk == null) return;
        for (BlockNetwork<T> network : networksForChunk) {
            network.purgeChunk(chunkPos);
        }
    }

    public @Nullable BlockNetwork<T> getOrRegisterNetworkFromMemberPosition(
            Level level,
            BlockPos memberBlockPos
    ) {
        // Return existing network if one present.
        @Nullable BlockNetwork<T> existing = getNetwork(level, memberBlockPos);
        if (existing != null) return existing;

        // No network exists for this position yet.
        // We will update an adjacent network if one exists to include this position.
        // Otherwise, we will create a new network.

        // Ensure the position is a valid member
        @Nullable T member = memberFactory.apply(level, memberBlockPos);
        if (member == null) return null;

        // Scan neighbours to find networks and unclaimed members
        ArrayDeque<BlockPos> unclaimedNeighbourPositions = new ArrayDeque<>(6);
        List<BlockNetwork<T>> neighbouringNetworks = new ArrayList<>();

        BlockPos.MutableBlockPos neighbourBlockPosition = new BlockPos.MutableBlockPos();
        for (Direction direction : SFMDirections.DIRECTIONS_WITHOUT_NULL) {
            neighbourBlockPosition.set(memberBlockPos).move(direction);
            @Nullable BlockNetwork<T> neighbourNetwork = getNetwork(level, neighbourBlockPosition);
            if (neighbourNetwork == null) {
                /// We do not need to check if this neighbour block is a valid candidate.
                /// {@link BlockNetwork#discoverCandidatesFromLevel(BlockPos)} will check for us.
                unclaimedNeighbourPositions.add(neighbourBlockPosition.immutable());
            } else {
                neighbouringNetworks.add(neighbourNetwork);
            }
        }

        // Determine the result network
        BlockNetwork<T> resultNetwork;
        Iterator<BlockNetwork<T>> neighbouringNetworkIterator = neighbouringNetworks.iterator();
        if (neighbouringNetworkIterator.hasNext()) {
            // A neighbour network exists, it will absorb the other networks
            resultNetwork = neighbouringNetworkIterator.next();
            // Update membership
            resultNetwork.addMember(memberBlockPos, member);
            // Update tracking
            trackMemberBlockPosForNetwork(memberBlockPos, resultNetwork);
        } else {
            // No neighbouring networks exist, we construct a new network
            resultNetwork = new BlockNetwork<>(level, memberFactory);
            // Apply discovery; the candidates include the member position itself
            resultNetwork.discoverCandidatesFromLevel(memberBlockPos).forEach(pair -> {
                // Update membership
                resultNetwork.addMember(pair);
                // Update tracking
                trackMemberBlockPosForNetwork(pair.getFirst(), resultNetwork);
            });
        }

        // Merge any neighbouring networks
        while (neighbouringNetworkIterator.hasNext()) {
            // Get the old network
            BlockNetwork<T> oldNetwork = neighbouringNetworkIterator.next();
            // Merge the old network into the result network
            resultNetwork.addAllFromOtherNetwork(oldNetwork);
            // Update tracking
            trackNetworkTransfer(oldNetwork, resultNetwork);
        }

        // Add any unclaimed members
        for (BlockPos unclaimedNeighbourPosition : unclaimedNeighbourPositions) {
            resultNetwork.discoverCandidatesFromLevel(unclaimedNeighbourPosition).forEach(pair -> {
                // Update membership
                resultNetwork.addMember(pair);
                // Update tracking
                trackMemberBlockPosForNetwork(pair.getFirst(), resultNetwork);
            });
        }

        printChangeDiagnostics();
        return resultNetwork;
    }

    public void onMemberRemovedFromLevel(
            Level level,
            BlockPos memberBlockPos
    ) {

        // Identify the network associated with the member position
        BlockNetwork<T> oldNetwork = getNetwork(level, memberBlockPos);
        if (oldNetwork == null) return;

        // Untrack the position as the member has been removed from the level
        untrackMemberFromNetwork(memberBlockPos, oldNetwork);

        // Identify the networks that result from the removal of the position
        List<BlockNetwork<T>> resultingNetworks = oldNetwork.splitRemoveMember(memberBlockPos);

        // Track the resulting networks
        for (BlockNetwork<T> newNetwork : resultingNetworks) {
            trackNetworkTransfer(oldNetwork, newNetwork);
        }

        // The old network should no longer be tracked as all lookup table entries
        // have been clobbered by the split after untracking the removed member position
        if (SFMEnvironmentUtils.isInIDE()) {
            assertNetworkForgotten(oldNetwork);
        }
    }

    public void assertNetworkForgotten(BlockNetwork<T> network) {

        Level level = network.level();

        // Check the level lookup
        if (networksByLevel.getOrDefault(level, Collections.emptyList()).contains(network)) {
            SFM.LOGGER.error("Network still tracked in level lookup");
        }

        // Check the chunk lookup
        if (networksByLevelChunk
                .getOrDefault(level, new ChunkPosMap<>())
                .values()
                .stream()
                .anyMatch(list -> list.contains(network))) {
            SFM.LOGGER.error("Network still tracked in chunk lookup");
        }

        // Check the block position lookup
        if (networksByLevelBlockPos.getOrDefault(level, new BlockPosMap<>()).values().contains(network)) {
            SFM.LOGGER.error("Network still tracked in block position lookup");
        }
    }

    public void clearLevel(Level level) {

        networksByLevelBlockPos.remove(level);
        networksByLevelChunk.remove(level);
        networksByLevel.remove(level);
    }

    public void clear() {

        networksByLevelBlockPos.clear();
        networksByLevelChunk.clear();
        networksByLevel.clear();
    }

    public void printDebugInfo() {

        SFM.LOGGER.info("=== BlockNetworkManager Debug Info ===");

        SFM.LOGGER.info("Networks by Level:");
        for (Map.Entry<Level, List<BlockNetwork<T>>> entry : networksByLevel.entrySet()) {
            Level level = entry.getKey();
            List<BlockNetwork<T>> networks = entry.getValue();
            SFM.LOGGER.info("  Level {}: {} networks", level.dimension().location(), networks.size());
            for (int i = 0; i < networks.size(); i++) {
                BlockNetwork<T> network = networks.get(i);
                SFM.LOGGER.info("    Network {} @ {}: {} members", i, System.identityHashCode(network), network.members().size());
            }
        }

        SFM.LOGGER.info("Networks by Level Position:");
        for (Map.Entry<Level, BlockPosMap<BlockNetwork<T>>> entry : networksByLevelBlockPos.entrySet()) {
            Level level = entry.getKey();
            BlockPosMap<BlockNetwork<T>> positionMap = entry.getValue();
            SFM.LOGGER.info("  Level {}: {} tracked positions", level.dimension().location(), positionMap.size());
        }

        SFM.LOGGER.info("Networks by Level Chunk:");
        for (Map.Entry<Level, ChunkPosMap<List<BlockNetwork<T>>>> entry : networksByLevelChunk.entrySet()) {
            Level level = entry.getKey();
            ChunkPosMap<List<BlockNetwork<T>>> chunkMap = entry.getValue();
            SFM.LOGGER.info("  Level {}: {} chunks", level.dimension().location(), chunkMap.size());
            for (Map.Entry<Long, List<BlockNetwork<T>>> chunkEntry : chunkMap.entrySet()) {
                ChunkPos chunkPos = new ChunkPos(chunkEntry.getKey());
                List<BlockNetwork<T>> networksInChunk = chunkEntry.getValue();
                SFM.LOGGER.info("    Chunk [{}, {}]: {} networks", chunkPos.x, chunkPos.z, networksInChunk.size());
            }
        }

        SFM.LOGGER.info("=== End BlockNetworkManager Debug Info ===");
    }

    private void printChangeDiagnostics() {

        boolean enabled = false;
        if (!enabled) return;
        if (!SFMEnvironmentUtils.isInIDE()) return;
        SFM.LOGGER.info("Network lookup changed");
        SFM.LOGGER.info("NETWORKS_BY_LEVEL:");
        for (Map.Entry<Level, List<BlockNetwork<T>>> entry : networksByLevel.entrySet()) {
            Level level = entry.getKey();
            List<BlockNetwork<T>> networks = entry.getValue();
            SFM.LOGGER.debug("Level {} has {} networks", level, networks.size());
            StringBuilder builder = new StringBuilder();
            for (BlockNetwork<T> network : networks) {
                builder.append(network.members().size()).append(" members; ");
            }
            SFM.LOGGER.debug(builder.toString());
        }
        SFM.LOGGER.info("NETWORKS_BY_CABLE_POSITION:");
        for (Map.Entry<Level, BlockPosMap<BlockNetwork<T>>> entry : networksByLevelBlockPos.entrySet()) {
            Level level = entry.getKey();
            BlockPosMap<BlockNetwork<T>> networksByCablePosition = entry.getValue();
            SFM.LOGGER.debug("Level {} has {} cables", level, networksByCablePosition.size());
        }
    }

    private void trackMemberBlockPosForNetwork(
            BlockPos memberPos,
            BlockNetwork<T> network
    ) {

        trackMemberBlockPosForNetwork(memberPos.asLong(), network);
    }

    private void trackMemberBlockPosForNetwork(
            long memberBlockPos,
            BlockNetwork<T> network
    ) {

        // Get the level
        Level level = network.level();

        // Update the position lookup
        BlockPosMap<BlockNetwork<T>> networksByBlockPos = networksByLevelBlockPos.computeIfAbsent(
                level,
                k -> new BlockPosMap<>()
        );
        networksByBlockPos.put(memberBlockPos, network);

        // Update the chunk lookup
        ChunkPosMap<List<BlockNetwork<T>>> networksByChunkPos = networksByLevelChunk.computeIfAbsent(
                level,
                k -> new ChunkPosMap<>()
        );
        networksByChunkPos.computeIfAbsent(memberBlockPos, k -> new ArrayList<>()).add(network);

        // Update the level lookup
        networksByLevel.computeIfAbsent(level, k -> new ArrayList<>()).add(network);
    }

    @SuppressWarnings("resource")
    private void trackNetworkTransfer(
            BlockNetwork<T> oldNetwork,
            BlockNetwork<T> newNetwork
    ) {
        // Get the level
        Level level = oldNetwork.level();
        if (newNetwork.level() != level)
            throw new IllegalStateException("Cannot transfer network ownership across levels");

        // Update the position lookup
        BlockPosMap<BlockNetwork<T>> networksByLevelBlockPosition = networksByLevelBlockPos.computeIfAbsent(
                level,
                k -> new BlockPosMap<>()
        );
        LongSet oldNetworkMemberBlockPositions = oldNetwork.members().keySet();
        LongIterator oldNetworkMemberBlockPositionIterator = oldNetworkMemberBlockPositions.longIterator();
        while (oldNetworkMemberBlockPositionIterator.hasNext()) {
            long oldNetworkMemberBlockPos = oldNetworkMemberBlockPositionIterator.nextLong();
            // Clobber the old entries
            networksByLevelBlockPosition.put(oldNetworkMemberBlockPos, newNetwork);
        }

        // Update the chunk lookup
        ChunkPosMap<List<BlockNetwork<T>>> networksByLevelChunk = this.networksByLevelChunk.computeIfAbsent(
                level,
                k -> new ChunkPosMap<>()
        );
        ChunkPosMap<BlockPosSet> oldNetworkMemberBlockPositionsByChunk = oldNetwork.memberBlockPositionsByChunk();
        for (long chunkPos : oldNetworkMemberBlockPositionsByChunk.keySet()) {
            List<BlockNetwork<T>> networksInChunk = networksByLevelChunk.computeIfAbsent(
                    chunkPos,
                    k -> new ArrayList<>()
            );
            // Remove the old network
            networksInChunk.remove(oldNetwork);
            // Add the new network
            networksInChunk.add(newNetwork);
        }

        // Update the level lookup
        List<BlockNetwork<T>> networksInLevel = this.networksByLevel.computeIfAbsent(level, k -> new ArrayList<>());
        // Remove the old network
        networksInLevel.remove(oldNetwork);
        // Add the new network
        networksInLevel.add(newNetwork);
    }

    private void untrackNetwork(BlockNetwork<T> network) {

        Level level = network.level();

        // Remove the block position entries
        BlockPosMap<BlockNetwork<T>> networksByBlockPos = networksByLevelBlockPos.get(level);
        networksByBlockPos.removeKeys(network.members().keySet());
        if (networksByBlockPos.isEmpty()) networksByLevelBlockPos.remove(level);

        // Remove the chunk entries
        ChunkPosMap<List<BlockNetwork<T>>> networksByChunkPos = networksByLevelChunk.get(level);
        ChunkPosMap<BlockPosSet> networkChunkPositions = network.memberBlockPositionsByChunk();

        networkChunkPositions.keySet().forEach((chunkPos) -> {
            List<BlockNetwork<T>> networksInChunk = networksByChunkPos.get(chunkPos);
            if (networksInChunk != null) {
                networksInChunk.remove(network);
                if (networksInChunk.isEmpty()) {
                    networksByChunkPos.remove(chunkPos);
                }
            }
        });

        // Remove the level entries
        List<BlockNetwork<T>> levelNetworks = networksByLevel.get(level);
        if (levelNetworks != null) {
            levelNetworks.remove(network);
            if (levelNetworks.isEmpty()) networksByLevel.remove(level);
        }

    }


    /// Remove the lookup table entries for the given position.
    /// This DOES NOT perform network splitting!
    private void untrackMemberFromNetwork(
            BlockPos memberBlockPos,
            @UnknownNullability BlockNetwork<T> network
    ) {
        Level level = network.level();
        long memberBlockPosLong = memberBlockPos.asLong();
        ChunkPos memberChunkPos = new ChunkPos(memberBlockPosLong);

        // Remove the member from the network
        network.removeMember(memberBlockPos);

        // Remove the block pos from the position lookup
        BlockPosMap<BlockNetwork<T>> networksByBlockPos = networksByLevelBlockPos.get(level);
        if (networksByBlockPos != null) {
            networksByBlockPos.remove(memberBlockPosLong);
            if (networksByBlockPos.isEmpty()) {
                networksByLevelBlockPos.remove(level);
            }
        }

        // Check if the network still uses the chunk
        if (!network.usesChunk(memberChunkPos)) {
            // Remove the chunk pos from the chunk pos lookup
            ChunkPosMap<List<BlockNetwork<T>>> networksByChunkPos = networksByLevelChunk.get(level);
            if (networksByChunkPos != null) {
                List<BlockNetwork<T>> listOfNetworksInChunk = networksByChunkPos.get(memberBlockPosLong);
                if (listOfNetworksInChunk != null) {
                    listOfNetworksInChunk.remove(network);
                    if (listOfNetworksInChunk.isEmpty()) {
                        networksByChunkPos.remove(memberBlockPosLong);
                    }
                }
                if (networksByChunkPos.isEmpty()) {
                    networksByLevelChunk.remove(level);
                }
            }
        }

        // Remove the network from the level if it is now empty
        if (network.isEmpty()) {
            List<BlockNetwork<T>> networksInLevel = networksByLevel.get(level);
            if (networksInLevel != null) {
                networksInLevel.remove(network);
                if (networksInLevel.isEmpty()) {
                    networksByLevel.remove(level);
                }
            }
        }

    }

}
