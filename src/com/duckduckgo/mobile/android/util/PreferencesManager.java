package com.duckduckgo.mobile.android.util;

import com.duckduckgo.mobile.android.DDGApplication;

import android.content.SharedPreferences;

public class PreferencesManager {
	
	/* Settings */
	public static String getThemeName() {
		return DDGApplication.getSharedPreferences().getString("themePref", "DDGDark");
	}
	
	public static boolean getReadable() {
		return DDGApplication.getSharedPreferences().getBoolean("readablePref", true);
	}
	
	public static boolean getTurnOffAutocomplete() {
		return DDGApplication.getSharedPreferences().getBoolean("turnOffAutocompletePref", false);
	}
	
	public static boolean getRecordHistory() {
		return DDGApplication.getSharedPreferences().getBoolean("recordHistoryPref", true);
	}
	
	public static boolean getDirectQuery() {
		return DDGApplication.getSharedPreferences().getBoolean("directQueryPref", true);
	}
	
	public static boolean containsSourcesetSize() {
		return DDGApplication.getSharedPreferences().contains("sourceset_size");
	}
	
	public static int getSourcesetSize() {
		return DDGApplication.getSharedPreferences().getInt("sourceset_size", 0);
	}
	
	/* Font sizes */
	
	public static float getMainFontSize(float defaultValue) {
		return DDGApplication.getSharedPreferences().getFloat("mainFontSize", defaultValue);
	}
	
	public static float getRecentFontSize(float defaultValue) {
		return DDGApplication.getSharedPreferences().getFloat("recentFontSize", defaultValue);
	}
	
	public static int getWebviewFontSize() {
		return DDGApplication.getSharedPreferences().getInt("webViewFontSize", -1);
	}
	
	public static float getLeftTitleTextSize(float defaultValue) {
		return DDGApplication.getSharedPreferences().getFloat("leftTitleTextSize", defaultValue);
	}
	
	public static int getPtrHeaderTextSize(int defaultValue) {
		return DDGApplication.getSharedPreferences().getInt("ptrHeaderTextSize", defaultValue);
	}
	
	public static int getPtrHeaderSubTextSize(int defaultValue) {
		return DDGApplication.getSharedPreferences().getInt("ptrHeaderSubTextSize", defaultValue);
	}
	
	/* Events */
	
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