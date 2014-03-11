package com.duckduckgo.mobile.android.util;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.Theme;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;

public class PreferencesManager {
	
	private static int WELCOME_VERSION = 1;
	private static boolean NEW_SOURCES = true;
	private static boolean sourcesWereMigratedRightNow = false;
	
	private static float defMainFontSize;
	private static float defRecentFontSize;
	private static float defLeftTitleTextSize;
	
	private static int defWebViewFontSize;
	private static int defPtrHeaderSize;
	private static int defPtrSubHeaderSize;
	
	
	/* Settings */
	
	public static String getThemeName() {
		return DDGApplication.getSharedPreferences().getString("themePref", "DDGDark");
	}
	
	/**
	 * Returns active SCREEN by considering the Duck Mode checkbox
	 * @return active SCREEN
	 */
	public static SCREEN getActiveStartScreen() {
		boolean isDuckMode = DDGApplication.getSharedPreferences().getBoolean("duckModePref", false);
		if(isDuckMode) {
			return SCREEN.SCR_DUCKMODE;
		}
		else {
			String startScreenCode = DDGApplication.getSharedPreferences().getString("startScreenPref", "0");
	        return SCREEN.getByCode(Integer.valueOf(startScreenCode));
	    }
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
	
	public static boolean getEnableTor(){
		return DDGApplication.getSharedPreferences().getBoolean("enableTor", false);
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
	
	public static boolean containsSourceSetSize() {
		return DDGApplication.getSharedPreferences().contains("sourceset_size");
	}
	
	public static boolean isWelcomeShown() {
		return DDGApplication.getSharedPreferences().getInt("welcomeShown", 0) == WELCOME_VERSION;
	}
	
	public static void setWelcomeShown() {
		Editor editor = DDGApplication.getSharedPreferences().edit();
		editor.putInt("welcomeShown", WELCOME_VERSION);
		editor.commit();
	}
	
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
	
	/* Text sizes */
	public static float getMainFontSize() {
		return DDGApplication.getSharedPreferences().getFloat("mainFontSize", defMainFontSize);
	}
	
	public static float getRecentFontSize() {
		return DDGApplication.getSharedPreferences().getFloat("recentFontSize", defRecentFontSize);
	}
	
	public static int getWebviewFontSize() {
		return DDGApplication.getSharedPreferences().getInt("webViewFontSize", defWebViewFontSize);
	}
	
	public static float getLeftTitleTextSize() {
		return DDGApplication.getSharedPreferences().getFloat("leftTitleTextSize", defLeftTitleTextSize);
	}
	
	public static int getPtrHeaderTextSize() {
		return DDGApplication.getSharedPreferences().getInt("ptrHeaderTextSize", defPtrHeaderSize);
	}
	
	public static int getPtrHeaderSubTextSize() {
		return DDGApplication.getSharedPreferences().getInt("ptrHeaderSubTextSize", defPtrSubHeaderSize);
	}
	
	public static void setWebViewFontDefault(int fontSize) {
		defWebViewFontSize = fontSize;
	}
	
	public static void setPtrHeaderFontDefaults(int headerFontSize, int subHeaderFontSize) {
		defPtrHeaderSize = headerFontSize;
		defPtrSubHeaderSize = subHeaderFontSize;
	}
	
    /*
     * Sets default font sizes from current theme attributes
     */
    public static void setFontDefaultsFromTheme(Context context) {
    	Theme theme = context.getTheme();
    	DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    	
    	TypedValue tmpTypedValue = new TypedValue(); 
    	theme.resolveAttribute(R.attr.leftButtonTextSize, tmpTypedValue, true);
    	// XXX getDimension returns in PIXELS !
    	defLeftTitleTextSize = tmpTypedValue.getDimension(displayMetrics);
    	
    	theme.resolveAttribute(R.attr.mainTextSize, tmpTypedValue, true);
    	defMainFontSize = tmpTypedValue.getDimension(displayMetrics);
    	
    	theme.resolveAttribute(R.attr.recentTextSize, tmpTypedValue, true);
    	defRecentFontSize = tmpTypedValue.getDimension(displayMetrics);
    }
	
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
    	if(key.equals("duckModePref") || key.equals("startScreenPref")){
    		DDGControlVar.START_SCREEN = getActiveStartScreen();
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
}