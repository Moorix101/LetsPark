package com.moorixlabs.park.models;


import java.io.Serializable;

public class ParkingSpot implements Serializable {
    private String spotId;
    private String label;
    private boolean isOccupied;
    private String currentSessionId;

    public ParkingSpot(String spotId, String label) {
        this.spotId = spotId;
        this.label = label;
        this.isOccupied = false;
        this.currentSessionId = null;
    }

    public String getSpotId() { return spotId; }
    public String getLabel() { return label; }
    public boolean isOccupied() { return isOccupied; }
    public boolean isFree() { return !isOccupied; }
    public String getCurrentSessionId() { return currentSessionId; }

    public void occupy(String sessionId) {
        this.isOccupied = true;
        this.currentSessionId = sessionId;
    }

    public void release() {
        this.isOccupied = false;
        this.currentSessionId = null;
    }

    @Override
    public String toString() {
        return label + " (" + (isOccupied ? "Occupied" : "Free") + ")";
    }
}