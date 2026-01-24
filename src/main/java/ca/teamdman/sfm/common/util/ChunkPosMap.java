package ca.teamdman.sfm.common.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

public class ChunkPosMap<T> {
    private final Long2ObjectMap<T> inner = new Long2ObjectOpenHashMap<>();

    public boolean isEmpty() {

        return inner.isEmpty();
    }

    public void clear() {

        inner.clear();
    }

    public @Nullable T put(
            long key,
            T value
    ) {

        return inner.put(key, value);
    }

    public @Nullable T get(ChunkAccess chunk) {

        return get(chunk.getPos());
    }

    public @Nullable T get(ChunkPos chunkPos) {

        return get(chunkPos.toLong());
    }

    public @Nullable T get(long key) {

        return inner.get(key);
    }

    public @Nullable T get(BlockPos blockPos) {

        return inner.get(ChunkPos.asLong(blockPos));
    }


    public @Nullable T remove(ChunkAccess chunk) {

        return remove(chunk.getPos());
    }

    public @Nullable T remove(ChunkPos chunkPos) {

        return remove(chunkPos.toLong());
    }

    public @Nullable T remove(long key) {

        return inner.remove(key);
    }

    public LongSet keySet() {

        return inner.keySet();
    }

    public int size() {

        return inner.size();
    }

    public boolean containsKey(long key) {

        return inner.containsKey(key);
    }

    public T computeIfAbsent(
            long key,
            Long2ObjectFunction<? extends T> mappingFunction
    ) {

        return inner.computeIfAbsent(key, mappingFunction);
    }

    public T computeIfAbsent(
            ChunkPos key,
            Long2ObjectFunction<? extends T> mappingFunction
    ) {

        return inner.computeIfAbsent(key.toLong(), mappingFunction);
    }


    public T computeIfAbsent(
            BlockPos blockPos,
            Long2ObjectFunction<? extends T> mappingFunction
    ) {

        return inner.computeIfAbsent(ChunkPos.asLong(blockPos), mappingFunction);
    }

    public @Nullable T remove(BlockPos blockPos) {

        return inner.remove(ChunkPos.asLong(blockPos));
    }

    public ObjectCollection<T> values() {

        return inner.values();
    }

}
