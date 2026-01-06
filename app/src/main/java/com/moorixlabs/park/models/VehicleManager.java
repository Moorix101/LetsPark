package com.moorixlabs.park.models;


import java.util.ArrayList;
import java.util.List;

public class VehicleManager {
    private List<Vehicle> vehicles;

    public VehicleManager() {
        this.vehicles = new ArrayList<>();
    }

    public void addVehicle(Vehicle vehicle) {
        if (vehicles.isEmpty()) {
            vehicle.setDefault(true);
        }
        vehicles.add(vehicle);
    }

    public void updateVehicle(String vehicleId, Vehicle updatedVehicle) {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equals(vehicleId)) {
                boolean wasDefault = vehicles.get(i).isDefault();
                vehicles.set(i, updatedVehicle);
                if (wasDefault) {
                    updatedVehicle.setDefault(true);
                }
                break;
            }
        }
    }

    public void deleteVehicle(String vehicleId) {
        Vehicle toRemove = null;
        boolean wasDefault = false;

        for (Vehicle v : vehicles) {
            if (v.getId().equals(vehicleId)) {
                toRemove = v;
                wasDefault = v.isDefault();
                break;
            }
        }

        if (toRemove != null) {
            vehicles.remove(toRemove);

            if (wasDefault && !vehicles.isEmpty()) {
                vehicles.get(0).setDefault(true);
            }
        }
    }

    public void setDefaultVehicle(String vehicleId) {
        for (Vehicle v : vehicles) {
            v.setDefault(false);
        }

        for (Vehicle v : vehicles) {
            if (v.getId().equals(vehicleId)) {
                v.setDefault(true);
                break;
            }
        }
    }

    public Vehicle getDefaultVehicle() {
        for (Vehicle v : vehicles) {
            if (v.isDefault()) {
                return v;
            }
        }
        return vehicles.isEmpty() ? null : vehicles.get(0);
    }

    public Vehicle getVehicleById(String vehicleId) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(vehicleId)) {
                return v;
            }
        }
        return null;
    }

    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles);
    }

    public boolean hasVehicles() {
        return !vehicles.isEmpty();
    }

    public int getVehicleCount() {
        return vehicles.size();
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = new ArrayList<>(vehicles);
    }
}