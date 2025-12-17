package com.moorixlabs.park.models;


import java.io.Serializable;
import java.util.UUID;

/**
 * Pure Java model representing a parking session
 */
public class ParkingSession implements Serializable {
    private String sessionId;
    private String spotId;
    private String vehicleId;
    private long startTime;
    private Long endTime; // Nullable for active sessions
    private double totalCost;
    private boolean isActive;

    public ParkingSession(String spotId, String vehicleId) {
        this.sessionId = UUID.randomUUID().toString();
        this.spotId = spotId;
        this.vehicleId = vehicleId;
        this.startTime = System.currentTimeMillis();
        this.endTime = null;
        this.totalCost = 0.0;
        this.isActive = true;
    }

    // Constructor for deserialization
    public ParkingSession(String sessionId, String spotId, String vehicleId,
                          long startTime, Long endTime, double totalCost, boolean isActive) {
        this.sessionId = sessionId;
        this.spotId = spotId;
        this.vehicleId = vehicleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalCost = totalCost;
        this.isActive = isActive;
    }

    // Getters
    public String getSessionId() { return sessionId; }
    public String getSpotId() { return spotId; }
    public String getVehicleId() { return vehicleId; }
    public long getStartTime() { return startTime; }
    public Long getEndTime() { return endTime; }
    public double getTotalCost() { return totalCost; }
    public boolean isActive() { return isActive; }

    // Business methods
    public void endSession(long endTime, double finalCost) {
        this.endTime = endTime;
        this.totalCost = finalCost;
        this.isActive = false;
    }

    public long getDurationMillis() {
        long end = endTime != null ? endTime : System.currentTimeMillis();
        return end - startTime;
    }

    public long getDurationSeconds() {
        return getDurationMillis() / 1000;
    }

    public long getDurationMinutes() {
        return getDurationSeconds() / 60;
    }

    public long getDurationHours() {
        return getDurationMinutes() / 60;
    }
}