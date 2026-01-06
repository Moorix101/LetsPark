package com.moorixlabs.park.models;


import com.moorixlabs.park.models.ParkingSession;
import com.moorixlabs.park.models.ParkingSpot;
import com.moorixlabs.park.models.CostCalculator;
import java.util.ArrayList;
import java.util.List;

public class ParkingManager {
    private static final int TOTAL_SPOTS = 20;
    private List<ParkingSpot> spots;
    private ParkingSession activeSession;
    private double hourlyRate;

    public ParkingManager() {
        this.spots = new ArrayList<>();
        this.activeSession = null;
        this.hourlyRate = CostCalculator.getDefaultHourlyRate();
        initializeSpots();
    }

    private void initializeSpots() {
        String[] rows = {"A", "B", "C", "D"};
        int spotNumber = 1;

        for (String row : rows) {
            for (int i = 1; i <= 5; i++) {
                String label = row + i;
                spots.add(new ParkingSpot("spot_" + spotNumber, label));
                spotNumber++;
            }
        }
    }

    public List<ParkingSpot> getAllSpots() {
        return new ArrayList<>(spots);
    }

    public ParkingSpot getSpotById(String spotId) {
        for (ParkingSpot spot : spots) {
            if (spot.getSpotId().equals(spotId)) {
                return spot;
            }
        }
        return null;
    }

    public ParkingSpot getSpotByLabel(String label) {
        for (ParkingSpot spot : spots) {
            if (spot.getLabel().equals(label)) {
                return spot;
            }
        }
        return null;
    }

    public int getAvailableSpots() {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (spot.isFree()) count++;
        }
        return count;
    }

    public int getTotalSpots() {
        return TOTAL_SPOTS;
    }

    public boolean startSession(String spotId, String vehicleId) {
        if (activeSession != null) {
            return false;
        }

        ParkingSpot spot = getSpotById(spotId);
        if (spot == null || spot.isOccupied()) {
            return false;
        }

        activeSession = new ParkingSession(spotId, vehicleId);
        spot.occupy(activeSession.getSessionId());
        return true;
    }

    public ParkingSession endSession() {
        if (activeSession == null) {
            return null;
        }

        long endTime = System.currentTimeMillis();
        double cost = CostCalculator.calculateCost(
                endTime - activeSession.getStartTime(),
                hourlyRate
        );

        activeSession.endSession(endTime, cost);

        ParkingSpot spot = getSpotById(activeSession.getSpotId());
        if (spot != null) {
            spot.release();
        }

        ParkingSession completed = activeSession;
        activeSession = null;
        return completed;
    }

    public ParkingSession getActiveSession() {
        return activeSession;
    }

    public boolean hasActiveSession() {
        return activeSession != null;
    }

    public double getCurrentCost() {
        if (activeSession == null) {
            return 0.0;
        }
        return CostCalculator.calculateCostFromStart(
                activeSession.getStartTime(),
                hourlyRate
        );
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double rate) {
        this.hourlyRate = rate;
    }

    public void setSpots(List<ParkingSpot> spots) {
        this.spots = new ArrayList<>(spots);
    }

    public void setActiveSession(ParkingSession session) {
        this.activeSession = session;
    }
}