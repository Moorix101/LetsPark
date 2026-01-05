package com.moorixlabs.park.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.moorixlabs.park.models.User;

/**
 * Pure Android utility for user data persistence
 * Saves/loads user from SharedPreferences
 */
public class UserPreferences {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_CREATED = "user_created";
    private static final String KEY_USER_LAST_LOGIN = "user_last_login";

    public static void saveUser(Context context, User user) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getFullName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhoneNumber());
        editor.putLong(KEY_USER_CREATED, user.getCreatedAt());
        editor.putLong(KEY_USER_LAST_LOGIN, user.getLastLoginAt());
        
        editor.apply();
    }

    public static User loadUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        String id = prefs.getString(KEY_USER_ID, null);
        if (id == null) return null;

        String name = prefs.getString(KEY_USER_NAME, "");
        String email = prefs.getString(KEY_USER_EMAIL, "");
        String phone = prefs.getString(KEY_USER_PHONE, "");
        long created = prefs.getLong(KEY_USER_CREATED, 0);
        long lastLogin = prefs.getLong(KEY_USER_LAST_LOGIN, 0);

        return new User(id, name, email, phone, null, created, lastLogin);
    }

    public static void clearUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public static boolean hasUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_USER_ID);
    }
}