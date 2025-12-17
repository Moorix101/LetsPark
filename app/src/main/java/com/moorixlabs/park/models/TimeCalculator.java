package com.moorixlabs.park.models;


import java.util.Locale;

/**
 * Pure Java utility for time calculations
 */
public class TimeCalculator {

    public static long millisecondsToSeconds(long millis) {
        return millis / 1000;
    }

    public static long millisecondsToMinutes(long millis) {
        return millis / (1000 * 60);
    }

    public static long millisecondsToHours(long millis) {
        return millis / (1000 * 60 * 60);
    }

    public static String formatDuration(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String formatShortDuration(long millis) {
        long totalMinutes = millis / (1000 * 60);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%dh %02dm", hours, minutes);
        } else {
            return String.format(Locale.getDefault(), "%d min", minutes);
        }
    }

    public static double getElapsedHours(long startTimeMillis, long endTimeMillis) {
        long durationMillis = endTimeMillis - startTimeMillis;
        return durationMillis / (1000.0 * 60.0 * 60.0);
    }

    public static double getElapsedHoursFromNow(long startTimeMillis) {
        return getElapsedHours(startTimeMillis, System.currentTimeMillis());
    }
}
