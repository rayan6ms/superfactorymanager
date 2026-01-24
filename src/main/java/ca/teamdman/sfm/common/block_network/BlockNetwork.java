package ca.teamdman.sfm.common.block_network;

import ca.teamdman.sfm.common.util.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/// A block network is a contiguous chain of blocks touching in the world.
/// Block networks are tracked by a {@link BlockNetworkManager}.
/// Methods are intentionally package-private since the block network manager needs to be kept in sync.
public class BlockNetwork<T> {
    private final Level level;

    private final BlockPosMap<T> membersByBlockPosition;

    private final ChunkPosMap<BlockPosSet> memberBlockPositionsByChunk;

    private final BiFunction<Level, BlockPos, T> memberFactory;

    public BlockNetwork(
            Level level,
            BiFunction<Level, BlockPos, @Nullable T> memberFactory
    ) {

        this.level = level;
        this.membersByBlockPosition = new BlockPosMap<>();
        this.memberBlockPositionsByChunk = new ChunkPosMap<>();
        this.memberFactory = memberFactory;
    }

    public Level level() {

        return level;
    }

    public BlockPosMap<T> members() {
        return membersByBlockPosition;
    }

    public ChunkPosMap<BlockPosSet> memberBlockPositionsByChunk() {
        return memberBlockPositionsByChunk;
    }

    @Nullable T getCandidate(BlockPos pos) {

        return this.memberFactory.apply(level, pos);
    }

    void rebuild(BlockPos start) {

        membersByBlockPosition.clear();
        memberBlockPositionsByChunk.clear();
    }


    Stream<Pair<BlockPos, T>> discoverCandidatesFromLevel(
            BlockPos start
    ) {

        return SFMStreamUtils.getRecursiveStream(
                (currentBlockPos, next, results) -> {
                    @Nullable T member = BlockNetwork.this.getCandidate(currentBlockPos);
                    if (member == null) return;

                    // Track the return value
                    results.accept(Pair.of(currentBlockPos, member));

                    // Schedule the tank neighbours for traversal
                    for (Direction d : SFMDirections.DIRECTIONS_WITHOUT_NULL) {
                        next.accept(currentBlockPos.relative(d));
                    }
                },
                start
        );
    }

    Stream<Pair<BlockPos, T>> discoverCandidatesFromSelf(
            BlockPos start
    ) {

        return SFMStreamUtils.getRecursiveStream(
                (currentBlockPos, next, results) -> {
                    // Only traverse positions that have a block entity in the cache
                    T member = membersByBlockPosition.get(currentBlockPos);
                    if (member == null) return;

                    // Track the return value
                    results.accept(Pair.of(currentBlockPos, member));

                    // Schedule the neighbour positions for traversal
                    for (Direction d : SFMDirections.DIRECTIONS_WITHOUT_NULL) {
                        next.accept(currentBlockPos.relative(d));
                    }
                },
                start
        );
    }

    void purgeChunk(ChunkPos chunkPos) {

        BlockPosSet positionsInChunk = memberBlockPositionsByChunk.get(chunkPos);
        if (positionsInChunk != null) {
            membersByBlockPosition.removeKeys(positionsInChunk);
            memberBlockPositionsByChunk.remove(chunkPos);
        }
    }

    void tryAddMember(BlockPos blockPos) {

        @Nullable T member = getCandidate(blockPos);
        if (member == null) return;
        addMember(blockPos, member);
    }

    void addMember(Pair<BlockPos, T> pair) {

        addMember(pair.getFirst(), pair.getSecond());
    }

    void addMember(
            BlockPos memberBlockPos,
            T member
    ) {

        membersByBlockPosition.put(memberBlockPos, member);
        memberBlockPositionsByChunk.computeIfAbsent(memberBlockPos, k -> new BlockPosSet()).add(memberBlockPos);
    }

    public boolean isMember(BlockPos blockPos) {

        return membersByBlockPosition.containsKey(blockPos);
    }

    @SuppressWarnings("UnusedReturnValue")
    @Nullable T removeMember(BlockPos blockPos) {

        T rtn = membersByBlockPosition.remove(blockPos);
        if (rtn != null) {
            BlockPosSet chunkMemberBlockPositions = Objects.requireNonNull(memberBlockPositionsByChunk.get(blockPos));
            chunkMemberBlockPositions.remove(blockPos);
            if (chunkMemberBlockPositions.isEmpty()) {
                memberBlockPositionsByChunk.remove(blockPos);
            }
        }
        return rtn;
    }

    /// Discover from the other network into this one.
    private void populateFromPosition(
            BlockNetwork<T> other,
            BlockPos startPos
    ) {

        other.discoverCandidatesFromSelf(startPos).forEach((pair) -> {
            BlockPos memberBlockPos = pair.getFirst();
            T member = pair.getSecond();
            addMember(memberBlockPos, member);
        });
    }

    void addAllFromOtherNetwork(BlockNetwork<T> other) {

        membersByBlockPosition.putAll(other.membersByBlockPosition);
        for (long chunkPos : other.memberBlockPositionsByChunk.keySet()) {
            BlockPosSet thisChunkPositions = this.memberBlockPositionsByChunk.computeIfAbsent(
                    chunkPos,
                    k -> new BlockPosSet()
            );
            BlockPosSet otherChunkPositions = other.memberBlockPositionsByChunk.get(chunkPos);
            assert otherChunkPositions != null;
            thisChunkPositions.addAll(otherChunkPositions);
        }
    }

    /// Determine the networks that would result from this network having the given position removed.
    List<BlockNetwork<T>> splitRemoveMember(BlockPos blockPos) {

        // Remove the position from the network
        removeMember(blockPos);

        // Prepare the list of resulting branch networks
        List<BlockNetwork<T>> branches = new ArrayList<>();

        // For each neighbour of the removed position, identify branch uniqueness
        for (Direction direction : SFMDirections.DIRECTIONS_WITHOUT_NULL) {

            // Get the neighbour position
            BlockPos neighbourPos = blockPos.relative(direction);

            // Skip if the neighbour position is not a member of this network
            if (!isMember(neighbourPos)) continue;

            // Skip if the neighbour position is a member of one of the branches we have already discovered
            boolean alreadySeen = false;
            for (BlockNetwork<T> branch : branches) {
                if (branch.isMember(neighbourPos)) {
                    alreadySeen = true;
                    break;
                }
            }
            if (alreadySeen) continue;

            // Create the new water network to hold the branch
            BlockNetwork<T> branch = new BlockNetwork<>(level, memberFactory);

            // Populate the branch from this network
            branch.populateFromPosition(this, neighbourPos);

            // Track the branch to be returned
            branches.add(branch);
        }
        return branches;
    }
}
