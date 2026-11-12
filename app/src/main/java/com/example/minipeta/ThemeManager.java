package com.example.minipeta;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    private static final String PREFS_NAME = "SearchItThemePrefs";
    private static final String THEME_KEY = "selected_theme";

    public static final String LIGHT = "light";
    public static final String DARK = "dark";
    public static final String SYSTEM = "system";

    public static void applyTheme(Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String selectedTheme =
                preferences.getString(THEME_KEY, SYSTEM);

        switch (selectedTheme) {

            case LIGHT:
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                );
                break;

            case DARK:
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                );
                break;

            default:
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                );
                break;
        }
    }

    public static void setTheme(Context context, String theme) {

        SharedPreferences preferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        preferences.edit()
                .putString(THEME_KEY, theme)
                .apply();

        applyTheme(context);
    }

    public static String getTheme(Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return preferences.getString(THEME_KEY, SYSTEM);
    }
}
