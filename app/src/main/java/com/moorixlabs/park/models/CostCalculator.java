package com.moorixlabs.park.models;


import java.util.Locale;

/**
 * Pure Java utility for cost calculations
 */
public class CostCalculator {
    private static final double DEFAULT_HOURLY_RATE = 10.0; // dh per hour

    public static double calculateCost(long durationMillis, double hourlyRate) {
        double hours = durationMillis / (1000.0 * 60.0 * 60.0);
        return hours * hourlyRate;
    }

    public static double calculateCost(long durationMillis) {
        return calculateCost(durationMillis, DEFAULT_HOURLY_RATE);
    }

    public static double calculateCostFromStart(long startTimeMillis, double hourlyRate) {
        long currentTime = System.currentTimeMillis();
        return calculateCost(currentTime - startTimeMillis, hourlyRate);
    }

    public static double calculateCostFromStart(long startTimeMillis) {
        return calculateCostFromStart(startTimeMillis, DEFAULT_HOURLY_RATE);
    }

    public static String formatCost(double cost) {
        return String.format(Locale.getDefault(), "%.2f dh", cost);
    }

    public static String formatHourlyRate(double rate) {
        return String.format(Locale.getDefault(), "%.0f dh/hour", rate);
    }

    public static double getDefaultHourlyRate() {
        return DEFAULT_HOURLY_RATE;
    }
}