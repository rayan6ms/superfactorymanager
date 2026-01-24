package ca.teamdman.sfm.common.util;

import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnusedReturnValue")
public record BlockPosMap<T>(
        Long2ObjectOpenHashMap<T> inner
) {
    public BlockPosMap() {
        this(new Long2ObjectOpenHashMap<>());
    }

    public boolean isEmpty() {

        return inner.isEmpty();
    }

    public @Nullable T put(
            long key,
            T value
    ) {

        return inner.put(key, value);
    }

    public Long2ObjectMap.FastEntrySet<T> long2ObjectEntrySet() {

        return inner.long2ObjectEntrySet();
    }

    public T computeIfAbsent(
            long key,
            Long2ObjectFunction<? extends T> mappingFunction
    ) {

        return inner.computeIfAbsent(key, mappingFunction);
    }

    public @Nullable T get(long key) {

        return inner.get(key);
    }
    public @Nullable T get(BlockPos key) {

        return inner.get(key.asLong());
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

    public void clear() {

        inner.clear();
    }

    public ObjectCollection<T> values() {

        return inner.values();
    }

    public void putAll(BlockPosMap<T> pos2TankMap) {
        inner.putAll(pos2TankMap.inner);

    }

    public void removeKeys(BlockPosSet keys) {
        LongIterator longIterator = keys.longIterator();
        while (longIterator.hasNext()) {
            this.remove(longIterator.nextLong());
        }
    }

    public @Nullable T put(
            BlockPos blockPos,
            T member
    ) {

        return inner.put(blockPos.asLong(), member);
    }

    public @Nullable T remove(BlockPos blockPos) {

        return inner.remove(blockPos.asLong());
    }

    public boolean containsKey(BlockPos blockPos) {

        return inner.containsKey(blockPos.asLong());
    }

    public boolean removeKeys(LongSet blockPosSet) {
        return inner.keySet().removeAll(blockPosSet);

    }

}
