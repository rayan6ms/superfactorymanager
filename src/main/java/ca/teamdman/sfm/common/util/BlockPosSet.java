package ca.teamdman.sfm.common.util;

import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.core.BlockPos;

public record BlockPosSet(LongArraySet inner) {
    public BlockPosSet() {

        this(new LongArraySet());
    }

    /// @return {@code false} if was already an element of the set, {@code true} otherwise
    public boolean add(BlockPos pos) {

        return inner.add(pos.asLong());

    }

    public void addAll(BlockPosSet otherChunkPositions) {

        inner.addAll(otherChunkPositions.inner);

    }

    public LongIterator longIterator() {

        return inner.longIterator();
    }

    public boolean remove(long blockPos) {

        return inner.remove(blockPos);
    }

    public boolean isEmpty() {

        return inner.isEmpty();
    }

    public int size() {

        return inner.size();
    }

    public void clear() {

        inner.clear();
    }

    public boolean remove(BlockPos blockPos) {

        return inner.remove(blockPos.asLong());

    }

}
