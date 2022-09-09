package com.example.flance20.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    // Настройки приложения и пользователя
    private final SharedPreferences prefs; // SharedPreferences для менеджера

    public Settings(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    // Сеттер для ngrok url
    public void setNgrokUrl(String domain){
        String url = prefs.getString("url", "");
        if (!(url == "{}" || url == null || url == "")){
            prefs.edit().remove("url").commit();
        }
        url = "https://"+ domain +".eu.ngrok.io";
        System.out.println(url);
        prefs.edit().putString("url", url).commit();
    }
    // Геттер для ngrok url
    public String getNgrokUrl() {
        String url = prefs.getString("url", "");
        if (url == "{}" || url == null || url == "") return null;

        else return url;
    }

    // Сеттер сессии юзера
    public void setSession(String sessionID, String name, String surname, String email) {
        prefs.edit().putString("sessionID", sessionID).commit();
        prefs.edit().putString("name", name).commit();
        prefs.edit().putString("surname", surname).commit();
        prefs.edit().putString("email", email).commit();
    }
    // Удаление сессии
    public void deleteSession(){
        prefs.edit().remove("sessionID").commit();
        prefs.edit().remove("name").commit();
        prefs.edit().remove("surname").commit();
        prefs.edit().remove("email").commit();
    }
    // Получения сессии (куки)
    public String getSession() {
        String id = prefs.getString("sessionID", "");
        System.out.println('"' + id +'"');
        if (id == "{}" || id == null || id == "") return null;
        else return id;
    }
    // Геттер получения инфы о юезре
    public String[] getUser() {
        return new String[]{prefs.getString("name", ""), prefs.getString("surname", ""), prefs.getString("email", "")};
    }
}
