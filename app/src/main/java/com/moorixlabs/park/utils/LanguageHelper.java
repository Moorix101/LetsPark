package com.moorixlabs.park.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

public class LanguageHelper {

    private static final String PREF_NAME = "AppSettings";
    private static final String KEY_LANGUAGE = "language";

    public static void loadLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString(KEY_LANGUAGE, "");
        if (!language.isEmpty()) {
            setLocale(context, language);
        }
    }

    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale); // Support RTL

        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Save preference
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();
    }

    public static String getCurrentLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, "fr");
    }
}