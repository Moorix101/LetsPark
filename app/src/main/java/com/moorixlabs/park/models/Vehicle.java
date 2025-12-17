package com.moorixlabs.park.models;


import java.io.Serializable;
import java.util.UUID;

/**
 * Pure Java model representing a vehicle
 */
public class Vehicle implements Serializable {
    private String id;
    private String name;
    private String type;
    private String plateNumber;
    private String color;
    private boolean isDefault;
    private long createdAt;

    public Vehicle(String name, String type, String plateNumber, String color) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.plateNumber = plateNumber;
        this.color = color;
        this.isDefault = false;
        this.createdAt = System.currentTimeMillis();
    }

    // Constructor for deserialization
    public Vehicle(String id, String name, String type, String plateNumber,
                   String color, boolean isDefault, long createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.plateNumber = plateNumber;
        this.color = color;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getPlateNumber() { return plateNumber; }
    public String getColor() { return color; }
    public boolean isDefault() { return isDefault; }
    public long getCreatedAt() { return createdAt; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public void setColor(String color) { this.color = color; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public String getDisplayName() {
        return name + " (" + plateNumber + ")";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}