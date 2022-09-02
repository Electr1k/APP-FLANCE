package com.example.flance20.model;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Sessions {

    private final SharedPreferences prefs;

    public Sessions(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setSession(String sessionID, String name, String surname, String email) {
        prefs.edit().putString("sessionID", sessionID).commit();
        prefs.edit().putString("name", name).commit();
        prefs.edit().putString("surname", surname).commit();
        prefs.edit().putString("email", email).commit();
    }
    public void deleteSession(){
        prefs.edit().clear().commit();
    }
    public String getSession() {
        String id = prefs.getString("sessionID", "");
        System.out.println('"' + id +'"');
        if (id == "{}" || id == null || id == "") return null;
        else return id;
    }
    public String[] getUser() {
        return new String[]{prefs.getString("name", ""), prefs.getString("surname", ""), prefs.getString("email", "")};
    }
}

