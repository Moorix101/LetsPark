package com.moorixlabs.park.models;

import java.io.Serializable;
import java.util.UUID;

/**
 * Pure Java model representing a user account
 */
public class User implements Serializable {
    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String profileImageUrl; // For future: base64 or path
    private long createdAt;
    private long lastLoginAt;

    // Constructor for new user
    public User(String fullName, String email, String phoneNumber) {
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = null;
        this.createdAt = System.currentTimeMillis();
        this.lastLoginAt = System.currentTimeMillis();
    }

    // Constructor for deserialization
    public User(String id, String fullName, String email, String phoneNumber,
                String profileImageUrl, long createdAt, long lastLoginAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    // Getters
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public long getCreatedAt() { return createdAt; }
    public long getLastLoginAt() { return lastLoginAt; }

    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void updateLastLogin() { this.lastLoginAt = System.currentTimeMillis(); }

    // Get first name only
    public String getFirstName() {
        if (fullName == null || fullName.isEmpty()) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    // Get initials for avatar
    public String getInitials() {
        if (fullName == null || fullName.isEmpty()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    @Override
    public String toString() {
        return fullName + " (" + email + ")";
    }
}
