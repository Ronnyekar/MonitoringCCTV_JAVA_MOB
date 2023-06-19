package com.example.testdashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    public static final String ENABLE_PING_NOTIFICATION = "ENABLE_PING_NOTIFICATION";

    private static Preferences ourInstance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
//    private Context context;

    private Preferences(Context context) {
//        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
    }

    public static Preferences getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new Preferences(context);
        }
        return ourInstance;
    }

    public void setEnablePingNotification(boolean b) {
        editor.putBoolean(ENABLE_PING_NOTIFICATION, b);
        editor.apply();
    }

    public boolean isPingNotificationEnable() {
        return sharedPreferences.getBoolean(ENABLE_PING_NOTIFICATION, true);
    }


}
