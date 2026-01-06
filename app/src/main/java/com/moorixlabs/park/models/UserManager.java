package com.moorixlabs.park.models;

import android.content.Context;
import com.moorixlabs.park.R;

public class UserManager {
    private User currentUser;

    public UserManager() {
        this.currentUser = null;
    }

    public boolean hasUser() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            user.updateLastLogin();
        }
    }

    public void createUser(String fullName, String email, String phoneNumber) {
        this.currentUser = new User(fullName, email, phoneNumber);
    }

    public void updateUser(String fullName, String email, String phoneNumber) {
        if (currentUser != null) {
            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setPhoneNumber(phoneNumber);
        }
    }

    public void logout() {
        this.currentUser = null;
    }

    public String getGreeting(Context context) {
        if (currentUser == null) return context.getString(R.string.greeting_welcome);
        
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);

        String timeGreeting;
        if (hour < 12) {
            timeGreeting = context.getString(R.string.greeting_morning);
        } else if (hour < 18) {
            timeGreeting = context.getString(R.string.greeting_afternoon);
        } else {
            timeGreeting = context.getString(R.string.greeting_evening);
        }

        return context.getString(R.string.greeting_format, timeGreeting, currentUser.getFirstName());
    }

    public String getWelcomeMessage() {
        if (currentUser == null) return "Welcome to Park";
        return "Welcome back, " + currentUser.getFirstName() + "!";
    }
}