package com.example.clothshop.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "clothshop_session";
    private static final String KEY_AUTH_MODE = "auth_mode";
    private static final String MODE_GUEST = "guest";
    private static final String MODE_USER = "user";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setGuestMode() {
        prefs.edit().putString(KEY_AUTH_MODE, MODE_GUEST).apply();
    }

    public void setUserMode() {
        prefs.edit().putString(KEY_AUTH_MODE, MODE_USER).apply();
    }

    public boolean isGuest() {
        return MODE_GUEST.equals(prefs.getString(KEY_AUTH_MODE, ""));
    }
}
