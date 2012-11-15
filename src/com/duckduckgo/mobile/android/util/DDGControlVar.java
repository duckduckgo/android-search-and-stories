package com.duckduckgo.mobile.android.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DDGControlVar {
	
	public static SCREEN START_SCREEN = SCREEN.SCR_NEWS_FEED;	// news feed
	
	public static boolean hasUpdatedFeed = false;
	public static String regionString = "wt-wt";	// world traveler (none) as default
	
	public static String sourceJSON = null;
	public static Map<String,String> simpleSourceMap = null;
	public static Set<String> defaultSourceSet = null;
	
	public static String targetSource = null; 
	
	public static boolean useDefaultSources = true;
	public static boolean hasUpdatedSources = false;
	
	public static Set<String> readArticles = new HashSet<String>(); 
	
	public static boolean homeScreenShowing = true;
	
	public static boolean includeAppsInSearch = false;
	
	public static TaskCompleteSignal taskCompleteSignal = new TaskCompleteSignal();
}
