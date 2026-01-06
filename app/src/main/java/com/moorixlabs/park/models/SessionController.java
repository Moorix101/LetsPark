package com.moorixlabs.park.models;


import com.moorixlabs.park.models.HistoryManager;
import com.moorixlabs.park.models.ParkingManager;

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
        if (parkingManager.hasActiveSession()) {
            return new SessionStartResult(false, "err_session_already_active");
        }

        if (!vehicleManager.hasVehicles()) {
            return new SessionStartResult(false, "err_no_vehicles");
        }

        Vehicle vehicle = vehicleManager.getDefaultVehicle();
        if (vehicle == null) {
            return new SessionStartResult(false, "err_no_default_vehicle");
        }

        ParkingSpot spot = parkingManager.getSpotById(spotId);
        if (spot == null) {
            return new SessionStartResult(false, "err_invalid_spot");
        }
        if (spot.isOccupied()) {
            return new SessionStartResult(false, "err_spot_occupied");
        }

        boolean success = parkingManager.startSession(spotId, vehicle.getId());
        if (success) {
            return new SessionStartResult(true, "msg_session_started");
        } else {
            return new SessionStartResult(false, "err_failed_start");
        }
    }

    public SessionEndResult endSession() {
        if (!parkingManager.hasActiveSession()) {
            return new SessionEndResult(false, "err_no_active_session", null);
        }

        ParkingSession activeSession = parkingManager.getActiveSession();
        Vehicle vehicle = vehicleManager.getVehicleById(activeSession.getVehicleId());
        ParkingSpot spot = parkingManager.getSpotById(activeSession.getSpotId());

        ParkingSession completedSession = parkingManager.endSession();

        if (completedSession != null && vehicle != null && spot != null) {
            historyManager.addHistory(completedSession, vehicle, spot);
            return new SessionEndResult(true, "msg_session_completed", completedSession);
        }

        return new SessionEndResult(false, "err_failed_end", null);
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

    public static class SessionStartResult {
        private boolean success;
        private String messageKey;

        public SessionStartResult(boolean success, String messageKey) {
            this.success = success;
            this.messageKey = messageKey;
        }

        public boolean isSuccess() { return success; }
        public String getMessageKey() { return messageKey; }
    }

    public static class SessionEndResult {
        private boolean success;
        private String messageKey;
        private ParkingSession session;

        public SessionEndResult(boolean success, String messageKey, ParkingSession session) {
            this.success = success;
            this.messageKey = messageKey;
            this.session = session;
        }

        public boolean isSuccess() { return success; }
        public String getMessageKey() { return messageKey; }
        public ParkingSession getSession() { return session; }
    }
}