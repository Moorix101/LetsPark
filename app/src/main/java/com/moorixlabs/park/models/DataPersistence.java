package com.moorixlabs.park.models;


import com.moorixlabs.park.models.ParkingHistory;
import com.moorixlabs.park.models.ParkingSession;
import com.moorixlabs.park.models.ParkingSpot;
import com.moorixlabs.park.models.Vehicle;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DataPersistence {

    public static JSONObject vehicleToJson(Vehicle vehicle) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", vehicle.getId());
            json.put("name", vehicle.getName());
            json.put("type", vehicle.getType());
            json.put("plateNumber", vehicle.getPlateNumber());
            json.put("color", vehicle.getColor());
            json.put("isDefault", vehicle.isDefault());
            json.put("createdAt", vehicle.getCreatedAt());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Vehicle jsonToVehicle(JSONObject json) {
        try {
            return new Vehicle(
                    json.getString("id"),
                    json.getString("name"),
                    json.getString("type"),
                    json.getString("plateNumber"),
                    json.getString("color"),
                    json.getBoolean("isDefault"),
                    json.getLong("createdAt")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray vehicleListToJson(List<Vehicle> vehicles) {
        JSONArray array = new JSONArray();
        for (Vehicle v : vehicles) {
            array.put(vehicleToJson(v));
        }
        return array;
    }

    public static List<Vehicle> jsonToVehicleList(JSONArray array) {
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                Vehicle v = jsonToVehicle(array.getJSONObject(i));
                if (v != null) vehicles.add(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return vehicles;
    }

    public static JSONObject sessionToJson(ParkingSession session) {
        JSONObject json = new JSONObject();
        try {
            json.put("sessionId", session.getSessionId());
            json.put("spotId", session.getSpotId());
            json.put("vehicleId", session.getVehicleId());
            json.put("startTime", session.getStartTime());
            json.put("endTime", session.getEndTime());
            json.put("totalCost", session.getTotalCost());
            json.put("isActive", session.isActive());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static ParkingSession jsonToSession(JSONObject json) {
        try {
            Long endTime = json.isNull("endTime") ? null : json.getLong("endTime");
            return new ParkingSession(
                    json.getString("sessionId"),
                    json.getString("spotId"),
                    json.getString("vehicleId"),
                    json.getLong("startTime"),
                    endTime,
                    json.getDouble("totalCost"),
                    json.getBoolean("isActive")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject spotToJson(ParkingSpot spot) {
        JSONObject json = new JSONObject();
        try {
            json.put("spotId", spot.getSpotId());
            json.put("label", spot.getLabel());
            json.put("isOccupied", spot.isOccupied());
            json.put("currentSessionId", spot.getCurrentSessionId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static ParkingSpot jsonToSpot(JSONObject json) {
        try {
            ParkingSpot spot = new ParkingSpot(
                    json.getString("spotId"),
                    json.getString("label")
            );
            if (json.getBoolean("isOccupied")) {
                String sessionId = json.optString("currentSessionId", null);
                if (sessionId != null) {
                    spot.occupy(sessionId);
                }
            }
            return spot;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray spotListToJson(List<ParkingSpot> spots) {
        JSONArray array = new JSONArray();
        for (ParkingSpot s : spots) {
            array.put(spotToJson(s));
        }
        return array;
    }

    public static List<ParkingSpot> jsonToSpotList(JSONArray array) {
        List<ParkingSpot> spots = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                ParkingSpot s = jsonToSpot(array.getJSONObject(i));
                if (s != null) spots.add(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return spots;
    }
}