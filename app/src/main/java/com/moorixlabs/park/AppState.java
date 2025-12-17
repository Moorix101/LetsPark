package com.moorixlabs.park;


import com.moorixlabs.park.models.HistoryManager;
import com.moorixlabs.park.models.ParkingManager;
import com.moorixlabs.park.models.VehicleManager;

/**
 * Pure Java Singleton to maintain app state across activities
 */
public class AppState {
    private static AppState instance;

    private ParkingManager parkingManager;
    private VehicleManager vehicleManager;
    private HistoryManager historyManager;

    private AppState() {
        parkingManager = new ParkingManager();
        vehicleManager = new VehicleManager();
        historyManager = new HistoryManager();
    }

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public ParkingManager getParkingManager() {
        return parkingManager;
    }

    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}