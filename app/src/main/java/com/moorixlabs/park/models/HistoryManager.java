package com.moorixlabs.park.models;


import com.moorixlabs.park.models.ParkingHistory;
import com.moorixlabs.park.models.ParkingSession;
import com.moorixlabs.park.models.ParkingSpot;
import com.moorixlabs.park.models.Vehicle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Pure Java manager for parking history
 */
public class HistoryManager {
    private List<ParkingHistory> historyList;

    public HistoryManager() {
        this.historyList = new ArrayList<>();
    }

    public void addHistory(ParkingSession session, Vehicle vehicle, ParkingSpot spot) {
        ParkingHistory history = new ParkingHistory(session, vehicle, spot);
        historyList.add(history);
    }

    public List<ParkingHistory> getAllHistory() {
        // Return sorted by most recent first
        List<ParkingHistory> sorted = new ArrayList<>(historyList);
        Collections.sort(sorted, new Comparator<ParkingHistory>() {
            @Override
            public int compare(ParkingHistory h1, ParkingHistory h2) {
                return Long.compare(
                        h2.getSession().getStartTime(),
                        h1.getSession().getStartTime()
                );
            }
        });
        return sorted;
    }

    public List<ParkingHistory> getHistoryForVehicle(String vehicleId) {
        List<ParkingHistory> filtered = new ArrayList<>();
        for (ParkingHistory h : historyList) {
            if (h.getVehicle().getId().equals(vehicleId)) {
                filtered.add(h);
            }
        }
        return filtered;
    }

    public int getHistoryCount() {
        return historyList.size();
    }

    public double getTotalRevenue() {
        double total = 0.0;
        for (ParkingHistory h : historyList) {
            total += h.getSession().getTotalCost();
        }
        return total;
    }

    public void clearHistory() {
        historyList.clear();
    }

    public void setHistory(List<ParkingHistory> history) {
        this.historyList = new ArrayList<>(history);
    }
}