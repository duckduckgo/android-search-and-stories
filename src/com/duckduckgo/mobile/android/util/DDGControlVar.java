package com.duckduckgo.mobile.android.util;

import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.objects.FeedObject;

import java.util.HashSet;
import java.util.Set;

public class DDGControlVar {
	
	public static SCREEN START_SCREEN = SCREEN.SCR_STORIES;	// stories
	
	public static boolean hasUpdatedFeed = false;
	public static String regionString = "wt-wt";	// world traveler (none) as default
	
	public static String storiesJSON = null;
	public static boolean isDefaultsChecked = false;
	public static Set<String> defaultSources = null;
	public static Set<String> userAllowedSources = null;
	public static Set<String> userDisallowedSources = null;
	
	public static String targetSource = null;
    public static String targetCategory = null;
		
	public static Set<String> readArticles = new HashSet<String>(); 
	
	public static boolean homeScreenShowing = true;
	
	public static boolean includeAppsInSearch = false;
    public static int useExternalBrowser = DDGConstants.ALWAYS_INTERNAL;
	public static boolean isAutocompleteActive = false;
	public static boolean automaticFeedUpdate = true;
	public static boolean changedSources = false;

	public static DuckDuckGoContainer mDuckDuckGoContainer;

	public static FeedObject currentFeedObject = null;
	public static boolean mCleanSearchBar = false;
    public static boolean pageLoaded = false;
		
	public static boolean hasAppsIndexed = false;
	
	public static Set<String> getRequestSources() throws InterruptedException {
		Set<String> requestSources = new HashSet<String>(DDGControlVar.defaultSources);
		requestSources.removeAll(DDGControlVar.userDisallowedSources);
		requestSources.addAll(DDGControlVar.userAllowedSources);
		return requestSources;
	}
	
	public static final Object DECODE_LOCK = new Object();
}
