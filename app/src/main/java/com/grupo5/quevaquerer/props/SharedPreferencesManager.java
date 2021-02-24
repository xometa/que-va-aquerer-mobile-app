package com.grupo5.quevaquerer.props;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.grupo5.quevaquerer.entity.Client;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesManager {
    private static final String APP_SETTINGS_FILE = "APP_SETTINGS";

    private SharedPreferencesManager() {
    }

    private static SharedPreferences getSharedPreferences() {
        return MyApp.getContext().
                getSharedPreferences(APP_SETTINGS_FILE, Context.MODE_PRIVATE);
    }

    public static void setSomeSetValue(String clave, Client client) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        String json = new Gson().toJson(client);
        editor.putString(clave, json);
        editor.commit();
    }

    public static Client getSomeSetValue(String value) {
        String json = getSharedPreferences().getString(value, null);
        if (json == null) {
            return null;
        } else {
            Client client = new Gson().fromJson(json, Client.class);
            return client;
        }
    }

    public static void destroySharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear().apply();
    }
}
