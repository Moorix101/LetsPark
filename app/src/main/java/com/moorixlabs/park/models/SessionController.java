package com.moorixlabs.park.models;


import com.moorixlabs.park.models.HistoryManager;
import com.moorixlabs.park.models.ParkingManager;

/**
 * Pure Java controller for session operations
 */
public class SessionController {
    private ParkingManager parkingManager;
    private VehicleManager vehicleManager;
    private HistoryManager historyManager;

    public SessionController(ParkingManager parkingManager,
                             VehicleManager vehicleManager,
                             HistoryManager historyManager) {
        this.parkingManager = parkingManager;
        this.vehicleManager = vehicleManager;
        this.historyManager = historyManager;
    }

    public SessionStartResult startSession(String spotId) {
        // Check if there's already an active session
        if (parkingManager.hasActiveSession()) {
            return new SessionStartResult(false, "Active session already exists");
        }

        // Check if user has vehicles
        if (!vehicleManager.hasVehicles()) {
            return new SessionStartResult(false, "Please add a vehicle first");
        }

        // Get default vehicle
        Vehicle vehicle = vehicleManager.getDefaultVehicle();
        if (vehicle == null) {
            return new SessionStartResult(false, "No default vehicle found");
        }

        // Check if spot is available
        ParkingSpot spot = parkingManager.getSpotById(spotId);
        if (spot == null) {
            return new SessionStartResult(false, "Invalid spot");
        }
        if (spot.isOccupied()) {
            return new SessionStartResult(false, "Spot is already occupied");
        }

        // Start the session
        boolean success = parkingManager.startSession(spotId, vehicle.getId());
        if (success) {
            return new SessionStartResult(true, "Session started successfully");
        } else {
            return new SessionStartResult(false, "Failed to start session");
        }
    }

    public SessionEndResult endSession() {
        if (!parkingManager.hasActiveSession()) {
            return new SessionEndResult(false, "No active session", null);
        }

        ParkingSession activeSession = parkingManager.getActiveSession();
        Vehicle vehicle = vehicleManager.getVehicleById(activeSession.getVehicleId());
        ParkingSpot spot = parkingManager.getSpotById(activeSession.getSpotId());

        // End the session
        ParkingSession completedSession = parkingManager.endSession();

        if (completedSession != null && vehicle != null && spot != null) {
            // Add to history
            historyManager.addHistory(completedSession, vehicle, spot);
            return new SessionEndResult(true, "Session ended successfully", completedSession);
        }

        return new SessionEndResult(false, "Failed to end session", null);
    }

    public ParkingSession getActiveSession() {
        return parkingManager.getActiveSession();
    }

    public boolean hasActiveSession() {
        return parkingManager.hasActiveSession();
    }

    public Vehicle getActiveSessionVehicle() {
        if (!parkingManager.hasActiveSession()) {
            return null;
        }
        String vehicleId = parkingManager.getActiveSession().getVehicleId();
        return vehicleManager.getVehicleById(vehicleId);
    }

    public ParkingSpot getActiveSessionSpot() {
        if (!parkingManager.hasActiveSession()) {
            return null;
        }
        String spotId = parkingManager.getActiveSession().getSpotId();
        return parkingManager.getSpotById(spotId);
    }

    // Result classes
    public static class SessionStartResult {
        private boolean success;
        private String message;

        public SessionStartResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    public static class SessionEndResult {
        private boolean success;
        private String message;
        private ParkingSession session;

        public SessionEndResult(boolean success, String message, ParkingSession session) {
            this.success = success;
            this.message = message;
            this.session = session;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public ParkingSession getSession() { return session; }
    }
}