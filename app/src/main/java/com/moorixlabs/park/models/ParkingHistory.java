package com.moorixlabs.park.models;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ParkingHistory implements Serializable {
    private ParkingSession session;
    private Vehicle vehicle;
    private ParkingSpot spot;

    public ParkingHistory(ParkingSession session, Vehicle vehicle, ParkingSpot spot) {
        this.session = session;
        this.vehicle = vehicle;
        this.spot = spot;
    }

    public ParkingSession getSession() { return session; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getSpot() { return spot; }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(session.getStartTime()));
    }

    public String getFormattedStartTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(session.getStartTime()));
    }

    public String getFormattedEndTime() {
        if (session.getEndTime() == null) return "--:--";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(session.getEndTime()));
    }

    public String getFormattedDuration() {
        long totalMinutes = session.getDurationMinutes();
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format(Locale.getDefault(), "%dh %02dm", hours, minutes);
    }

    public String getFormattedCost() {
        return String.format(Locale.getDefault(), "%.2f dh", session.getTotalCost());
    }
}