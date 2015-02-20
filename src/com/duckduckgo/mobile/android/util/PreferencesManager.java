package com.duckduckgo.mobile.android.util;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.Theme;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.views.webview.DDGWebView;

public class PreferencesManager {
	
	private static int WELCOME_VERSION = 1;
	private static boolean NEW_SOURCES = true;
	private static boolean sourcesWereMigratedRightNow = false;
	
	/* Settings */
	
	public static String getThemeName() {
		return DDGApplication.getSharedPreferences().getString("themePref", "DDGDark");
	}
	
	/**
	 * Returns active SCREEN by considering the Duck Mode checkbox
	 * @return active SCREEN
	 */
	public static SCREEN getActiveStartScreen() {
        String startScreenCode = DDGApplication.getSharedPreferences().getString("startScreenPref", "0");
        return SCREEN.getByCode(Integer.valueOf(startScreenCode));
	}
    public static String getRegion() {
		return DDGApplication.getSharedPreferences().getString("regionPref", "wt-wt");
	}
	
	public static boolean getReadable() {
		return DDGApplication.getSharedPreferences().getBoolean("readablePref", true);
	}
	
	public static boolean getEnableTor(){
		return DDGApplication.getSharedPreferences().getBoolean("enableTor", false);
	}

    public static int getUseExternalBrowser() {
        return Integer.valueOf(DDGApplication.getSharedPreferences().getString("useExternalBrowserPref", "0"));
    }
	
	public static boolean getAutocomplete() {
		return DDGApplication.getSharedPreferences().getBoolean("autocompletePref", false);
	}
	
	public static boolean getRecordHistory() {
		return DDGApplication.getSharedPreferences().getBoolean("recordHistoryPref", true);
	}

    public static boolean getRecordCookies() {
        return DDGApplication.getSharedPreferences().getBoolean("recordCookiesPref", true);
    }
	
	public static boolean getDirectQuery() {
		return DDGApplication.getSharedPreferences().getBoolean("directQueryPref", true);
	}
	
	public static boolean containsSourceSetSize() {
		return DDGApplication.getSharedPreferences().contains("sourceset_size");
	}
	/*
	public static boolean isWelcomeShown() {
		return DDGApplication.getSharedPreferences().getInt("welcomeShown", 0) == WELCOME_VERSION;
	}
	
	public static void setWelcomeShown() {
		Editor editor = DDGApplication.getSharedPreferences().edit();
		editor.putInt("welcomeShown", WELCOME_VERSION);
		editor.commit();
	}*/
	/*
	public static boolean isFontSliderVisible() {
		return DDGApplication.getSharedPreferences().getBoolean("fontSliderVisible", false);
	}
	
	public static void setFontSliderVisibility(boolean visible) {
		Editor editor = DDGApplication.getSharedPreferences().edit();
		editor.putBoolean("fontSliderVisible", visible);
		editor.commit();
	}

	public static int getFontPrevProgress(int defaultValue) {
		return DDGApplication.getSharedPreferences().getInt("fontPrevProgress", defaultValue);
	}
*/

	public static int getAppVersionCode() {
		return DDGApplication.getSharedPreferences().getInt("appVersionCode", 0);
	}
	
	public static void saveAppVersionCode(int appVersionCode) {
		Editor editor = DDGApplication.getSharedPreferences().edit();
		editor.putInt("appVersionCode", appVersionCode);
		editor.commit();
	}
	
	public static boolean saveDefaultSources(Set<String> sources) {
		return DDGUtils.saveSet(DDGApplication.getSharedPreferences(), sources, "defaultsources");
	}
	
	public static Set<String> getDefaultSources() {
		return DDGUtils.loadSet(DDGApplication.getSharedPreferences(), "defaultsources");
	}
	
	public static void clearValues() {
		Editor editor = DDGApplication.getSharedPreferences().edit();
		editor.putInt("fontPrevProgress", DDGConstants.FONT_SEEKBAR_MID);
		editor.remove("mainFontSize");
		editor.remove("recentFontSize");
		editor.remove("webViewFontSize");
		editor.remove("ptrHeaderTextSize");
		editor.remove("ptrHeaderSubTextSize");
		editor.remove("leftTitleTextSize");
		editor.commit();
	}
	
	private static boolean shouldMigrateSources() {
		return DDGApplication.getSharedPreferences().contains("sourceset_size");
	}
	
	public static void migrateAllowedSources() {
		SharedPreferences prefs = DDGApplication.getSharedPreferences();
		if(PreferencesManager.shouldMigrateSources()) {
			Set<String> oldAllowed = DDGUtils.loadSet(prefs, "sourceset");
			DDGUtils.deleteSet(prefs, "sourceset");			
			saveUserAllowedSources(oldAllowed);
			
			Set<String> cachedSources = DDGUtils.getCachedSources();
			// XXX cachedSources is not expected to be null during a migration
			// since before APP VERSION_CODE 43, source response is always cached 
			if(cachedSources != null) {
				Set<String> oldDisallowed = new HashSet<String>(cachedSources);
				oldDisallowed.removeAll(oldAllowed);
				saveUserDisallowedSources(oldDisallowed);
			}
			sourcesWereMigratedRightNow = true;
		}		
	}
	
	public static boolean shouldShowNewSourcesDialog(){
		return NEW_SOURCES && sourcesWereMigratedRightNow;
	}
	
	public static void newSourcesDialogWasShown(){
		NEW_SOURCES = false;
	}
	
	/* Events */
    public static void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.e("aaa", "Preference manager on shared preference changed");
    	if(key.equals("startScreenPref")){
    		DDGControlVar.START_SCREEN = getActiveStartScreen();
            Log.e("aaa", "start screen: "+DDGControlVar.START_SCREEN);
        }
    	else if(key.equals("regionPref")){
            Log.e("aaa", "preferences manager, region pref");
            DDGControlVar.regionString = sharedPreferences.getString(key, "wt-wt");
            Log.e("aaa", "control var is: "+DDGControlVar.regionString);
        }
        else if(key.equals("appSearchPref")){
            DDGControlVar.includeAppsInSearch = sharedPreferences.getBoolean(key, false);
        }
        else if(key.equals("useExternalBrowserPref")){
            DDGControlVar.useExternalBrowser = Integer.valueOf(sharedPreferences.getString(key, "0"));
        }
        else if(key.equals("autocompletePref")){
            Log.e("aaa", "turn off autocomplete");
            DDGControlVar.isAutocompleteActive = sharedPreferences.getBoolean(key, false);
            Log.e("aaa", "is autocomplete active: "+DDGControlVar.isAutocompleteActive);
        }
        else if(key.equals("autoUpdatePref")){
            DDGControlVar.automaticFeedUpdate = sharedPreferences.getBoolean(key, true);
        }
        else if(key.equals("recordCookiesPref")) {
            DDGWebView.recordCookies(sharedPreferences.getBoolean(key, true));
        }
    }
    
    /* Collections */
    public static String getReadArticles() {
		return DDGApplication.getSharedPreferences().getString("readarticles", null);
	}
    
    public static void saveReadArticles() {
    	String combinedStringForReadArticles = ReadArticlesManager.getCombinedStringForReadArticles();
    	if(combinedStringForReadArticles.length() != 0){
	    	Editor editor = DDGApplication.getSharedPreferences().edit();
			editor.putString("readarticles", combinedStringForReadArticles);
			editor.commit();
    	}
	}
    
    /* User sources */
    public static Set<String> getUserAllowedSources() {
		return DDGUtils.loadSet(DDGApplication.getSharedPreferences(), "allowset");
	}
    
    public static boolean saveUserAllowedSources(Set<String> userSources) {
		return DDGUtils.saveSet(DDGApplication.getSharedPreferences(), userSources, "allowset");
	}
    
    public static Set<String> getUserDisallowedSources() {
		return DDGUtils.loadSet(DDGApplication.getSharedPreferences(), "disallowset");
	}
    
    public static boolean saveUserDisallowedSources(Set<String> userSources) {
		return DDGUtils.saveSet(DDGApplication.getSharedPreferences(), userSources, "disallowset");
	}

    public static boolean getAutomaticFeedUpdate() {
      return DDGApplication.getSharedPreferences().getBoolean("autoUpdatePref", true);
  }

	public static void setAutomaticFeedUpdate(boolean automaticFeedUpdate) {
			Editor editor = DDGApplication.getSharedPreferences().edit();
			editor.putBoolean("autoUpdatePref", automaticFeedUpdate);
			editor.commit();
  }
/*
    public static void setLastClearCacheAndCookies(long currentTime) {
        Editor editor = DDGApplication.getSharedPreferences().edit();
        editor.putLong("lastClearCacheAndCookies", currentTime);
        editor.commit();
    }

    public static long getLastClearCacheAndCookies() {
        return DDGApplication.getSharedPreferences().getLong("lastClearCacheAndCookies", 0l);
    }*/
}