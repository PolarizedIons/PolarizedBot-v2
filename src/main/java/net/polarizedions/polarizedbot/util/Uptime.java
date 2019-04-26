package net.polarizedions.polarizedbot.util;

import net.polarizedions.polarizedbot.Language;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Uptime {
    private static Instant startTime;

    public static void start() {
        startTime = Instant.now();
    }

    @NotNull
    private static String formatTime(@NotNull Duration duration) {
        long[] time = new long[5];
        time[0] = duration.toDays() / 7;
        time[1] = duration.toDays() % 7;
        time[2] = duration.toHours() % TimeUnit.DAYS.toHours(1);
        time[3] = duration.toMinutes() % TimeUnit.HOURS.toMinutes(1);
        time[4] = duration.getSeconds() % TimeUnit.MINUTES.toSeconds(1);

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < time.length; i++) {
            if (time[i] == 0 && formatted.length() == 0) {
                continue;
            }

            formatted.append(Language.get("time." + i, time[i])).append(" ");
        }

        return formatted.toString().trim();
    }

    public static String get() {
        return formatTime(Duration.between(startTime, Instant.now()));
    }
}
