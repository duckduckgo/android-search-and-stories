package com.duckduckgo.mobile.android.container;

import android.graphics.drawable.Drawable;

import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.adapters.MultiHistoryAdapter;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;

public class DuckDuckGoContainer {
	
	public boolean webviewShowing = false;
	
	public SESSIONTYPE sessionType = SESSIONTYPE.SESSION_BROWSE;
	public String lastFeedUrl = "";
	
	public SCREEN currentScreen = SCREEN.SCR_STORIES;
	public SCREEN prevScreen = SCREEN.SCR_STORIES;
	
	public Drawable progressDrawable, searchFieldDrawable;
	public Drawable stopDrawable;
	
	public MultiHistoryAdapter historyAdapter = null;
	
	public MainFeedAdapter feedAdapter = null;
	public MainFeedTask mainFeedTask = null;
	
	public AutoCompleteResultsAdapter acAdapter = null;
	
	public DDGPagerAdapter pageAdapter = null;	
}
