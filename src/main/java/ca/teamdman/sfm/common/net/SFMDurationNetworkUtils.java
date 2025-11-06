package ca.teamdman.sfm.common.net;

import net.minecraft.network.FriendlyByteBuf;

import java.time.Duration;

public class SFMDurationNetworkUtils {
    public static void writeDurationArray(
            Duration[] durations,
            FriendlyByteBuf friendlyByteBuf
    ) {

        long[] tickTimeNanos = new long[durations.length];
        for (int i = 0; i < tickTimeNanos.length; i++) {
            tickTimeNanos[i] = durations[i].toNanos();
        }
        friendlyByteBuf.writeLongArray(tickTimeNanos);
    }

    public static Duration[] readDurationArray(long[] tickTimes) {
        Duration[] durations = new Duration[tickTimes.length];
        for (int i = 0; i < tickTimes.length; i++) {
            durations[i] = Duration.ofNanos(tickTimes[i]);
        }
        return durations;
    }

}
