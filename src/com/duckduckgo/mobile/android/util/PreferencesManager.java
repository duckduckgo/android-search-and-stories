package com.duckduckgo.mobile.android.util;

import java.util.HashSet;
import java.util.Set;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.dialogs.NewSourcesDialogBuilder;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesManager {
	
	private static int WELCOME_VERSION = 0;
	private static boolean NEW_SOURCES = true;
	private static boolean sourcesWereMigratedRightNow = false;
	
	/* Settings */
	
	public static String getThemeName() {
		return DDGApplication.getSharedPreferences().getString("themePref", "DDGDark");
	}
	
	public static String getStartScreen() {
		return DDGApplication.getSharedPreferences().getString("startScreenPref", "0");
	}
	
	public static String getRegion() {
		return DDGApplication.getSharedPreferences().getString("regionPref", "wt-wt");
	}
	
	public static boolean getReadable() {
		return DDGApplication.getSharedPreferences().getBoolean("readablePref", true);
	}
	
	public static boolean getExternalBrowser() {
		return DDGApplication.getSharedPreferences().getBoolean("externalBrowserPref", false);
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
	
	public static boolean isWelcomeShown() {
		return DDGApplication.getSharedPreferences().getInt("welcomeShown", 0) == WELCOME_VERSION;
	}
	
	public static void setWelcomeShown() {
		Editor editor = DDGApplication.getSharedPreferences().edit();
		editor.putInt("welcomeShown", WELCOME_VERSION);
		editor.commit();
	}
	
	public static int getFontPrevProgress(int defaultValue) {
		return DDGApplication.getSharedPreferences().getInt("fontPrevProgress", defaultValue);
	}
	
	/* Text sizes */
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
	
	public static int getAppVersionCode() {
		return DDGApplication.getSharedPreferences().getInt("appVersionCode", 0);
	}
	
	public static void saveAppVersionCode(int appVersionCode) {
		Editor editor = DDGApplication.getSharedPreferences().edit();
		editor.putInt("appVersionCode", appVersionCode);
		editor.commit();
	}
	
	public static void saveDefaultSources(Set<String> sources) {
		DDGUtils.saveSet(DDGApplication.getSharedPreferences(), sources, "defaultsources");
	}
	
	public static Set<String> getDefaultSources() {
		return DDGUtils.loadSet(DDGApplication.getSharedPreferences(), "defaultsources");
	}
	
	public static void saveAdjustedTextSizes() {
		Editor editor = DDGApplication.getSharedPreferences().edit();
		editor.putInt("fontPrevProgress", DDGControlVar.fontPrevProgress);
		editor.putFloat("mainFontSize", DDGControlVar.mainTextSize);
		editor.putFloat("recentFontSize", DDGControlVar.recentTextSize);
		editor.putInt("webViewFontSize", DDGControlVar.webViewTextSize);
		editor.putInt("ptrHeaderTextSize", DDGControlVar.ptrHeaderSize);
		editor.putInt("ptrHeaderSubTextSize", DDGControlVar.ptrSubHeaderSize);
		editor.putFloat("leftTitleTextSize", DDGControlVar.leftTitleTextSize);
		editor.commit();
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
    
    /* Collections */
    public static String getReadArticles() {
		return DDGApplication.getSharedPreferences().getString("readarticles", null);
	}
    
    public static void saveReadArticles(String combinedArticles) {
    	Editor editor = DDGApplication.getSharedPreferences().edit();
		editor.putString("readarticles", combinedArticles);
		editor.commit();
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
}