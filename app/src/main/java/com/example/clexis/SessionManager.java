package com.example.clexis;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String token,String userId) {
        editor.putString("TOKEN", token);
        editor.putString("USERID", userId);
        editor.putBoolean("LOGGED_IN", true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean("LOGGED_IN", false);
    }

    public String getToken() {
        return prefs.getString("TOKEN", null);
    }
    public String getId() {
        return prefs.getString("USERID", null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
