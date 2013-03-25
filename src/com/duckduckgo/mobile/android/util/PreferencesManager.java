package com.duckduckgo.mobile.android.util;

import android.content.SharedPreferences;

public class PreferencesManager {
    public static void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("startScreenPref")){
            DDGControlVar.START_SCREEN = SCREEN.getByCode(Integer.valueOf(sharedPreferences.getString(key, "0")));
        }
        else if(key.equals("regionPref")){
            DDGControlVar.regionString = sharedPreferences.getString(key, "wt-wt");
        }
        else if(key.equals("appSearchPref")){
            DDGControlVar.includeAppsInSearch = sharedPreferences.getBoolean(key, false);
        }
        else if(key.equals("externalBrowserPref")){
            DDGControlVar.alwaysUseExternalBrowser = sharedPreferences.getBoolean(key, false);
        }
        else if(key.equals("turnOffAutocompletePref")){
            DDGControlVar.isAutocompleteActive = !sharedPreferences.getBoolean(key, false);
        }
    }
}