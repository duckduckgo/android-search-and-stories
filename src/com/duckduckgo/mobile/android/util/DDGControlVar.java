package com.duckduckgo.mobile.android.util;

import java.util.HashSet;
import java.util.Set;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class DDGControlVar {
	
	public static SCREEN START_SCREEN = SCREEN.SCR_STORIES;	// stories
	
	public static boolean mCleanSearchBar = false;
	
	public static String lastFeedUrl = "";	
	public static SCREEN currentScreen = SCREEN.SCR_STORIES;
	public static SCREEN prevScreen = SCREEN.SCR_STORIES;
	public static FeedObject currentFeedObject = null;
	public static SESSIONTYPE sessionType = SESSIONTYPE.SESSION_BROWSE;
		
	public static boolean hasUpdatedFeed = false;
	public static String regionString = "wt-wt";	// world traveler (none) as default
	
	public static String storiesJSON = null;
	public static boolean isDefaultsChecked = false;
	public static Set<String> defaultSources = null;
	public static Set<String> userAllowedSources = null;
	public static Set<String> userDisallowedSources = null;
	
	public static String targetSource = null; 
		
	public static Set<String> readArticles = new HashSet<String>(); 
	
	public static boolean homeScreenShowing = true;
	
	public static boolean includeAppsInSearch = false;
	public static boolean alwaysUseExternalBrowser = false;
	public static boolean isAutocompleteActive = true;
	
	public static int fontProgress = DDGConstants.FONT_SEEKBAR_MID;	// over scale 0-10
	public static int fontPrevProgress = DDGConstants.FONT_SEEKBAR_MID;	// over scale 0-10
	
	public static int diff = 0;
	public static float diffPixel = 0.0f;
		
	public static boolean hasAppsIndexed = false;
	
	public static Set<String> getRequestSources() throws InterruptedException {
		Set<String> requestSources = new HashSet<String>(DDGControlVar.defaultSources);
		requestSources.removeAll(DDGControlVar.userDisallowedSources);
		requestSources.addAll(DDGControlVar.userAllowedSources);
		return requestSources;
	}
	
	public static final Object DECODE_LOCK = new Object();
}
