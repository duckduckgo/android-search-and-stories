package com.duckduckgo.mobile.android.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.objects.FeedObject;

public class AppStateManager {
	public static  void saveAppState(SharedPreferences prefs, DuckDuckGoContainer duckDuckGoContainer,
			FeedObject currentFeedObject) {
		Editor editor = prefs.edit();
		editor.putBoolean("homeScreenShowing", DDGControlVar.homeScreenShowing);
		editor.putInt("currentScreen", DDGControlVar.currentScreen.ordinal());
		editor.putInt("prevScreen", DDGControlVar.prevScreen.ordinal());
		editor.putInt("sessionType", DDGControlVar.sessionType.ordinal());
		if(currentFeedObject != null) {
			editor.putString("currentFeedObjectId", currentFeedObject.getId());
		}
		editor.commit();
	}
	
	public static void saveAppState(Bundle bundle, DuckDuckGoContainer duckDuckGoContainer,
			FeedObject currentFeedObject) {
		bundle.putBoolean("homeScreenShowing", DDGControlVar.homeScreenShowing);
		bundle.putInt("currentScreen", DDGControlVar.currentScreen.ordinal());
		bundle.putInt("prevScreen", DDGControlVar.prevScreen.ordinal());
		bundle.putInt("sessionType", DDGControlVar.sessionType.ordinal());
		if(currentFeedObject != null) {
			bundle.putString("currentFeedObjectId", currentFeedObject.getId());
		}
	}
	
	public static void recoverAppState(Object state, DuckDuckGoContainer duckDuckGoContainer,
			FeedObject currentFeedObject) {
		Bundle bundle = null; 
		SharedPreferences prefs = null; 
		
		
		if(state instanceof Bundle) {
			bundle = (Bundle) state;
			
			DDGControlVar.homeScreenShowing = bundle.getBoolean("homeScreenShowing");
			DDGControlVar.currentScreen = SCREEN.getByCode(bundle.getInt("currentScreen"));
			DDGControlVar.prevScreen = SCREEN.getByCode(bundle.getInt("prevScreen"));
			DDGControlVar.sessionType = SESSIONTYPE.getByCode(bundle.getInt("sessionType"));
		}
		// do we ever get here?
		else if(state instanceof SharedPreferences) {
			prefs = (SharedPreferences) state;
			
			DDGControlVar.homeScreenShowing = prefs.getBoolean("homeScreenShowing", false);
			DDGControlVar.currentScreen = SCREEN.getByCode(prefs.getInt("currentScreen", SCREEN.SCR_STORIES.getCode()));
			DDGControlVar.prevScreen = SCREEN.getByCode(prefs.getInt("prevScreen", SCREEN.SCR_STORIES.getCode()));
			DDGControlVar.sessionType = SESSIONTYPE.getByCode(prefs.getInt("sessionType", SESSIONTYPE.SESSION_BROWSE.getCode()));
		}
	}
	
	public static String getCurrentFeedObjectId(Object state){
		if(state instanceof Bundle) {
			return ((Bundle) state).getString("currentFeedObjectId");
		}
		else if(state instanceof SharedPreferences) {
			return ((SharedPreferences) state).getString("currentFeedObjectId", null);
		}
		return "";
	}
}
