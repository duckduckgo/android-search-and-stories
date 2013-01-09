package com.duckduckgo.mobile.android.container;

import java.util.LinkedList;

import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;

import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.tasks.DownloadSourceIconTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.tasks.SavedFeedTask;

public class DuckDuckGoContainer {

	public boolean webviewShowing = false;
	public boolean prefShowing = false;
	public boolean savedFeedShowing = false;
	
	public Drawable progressDrawable, searchFieldDrawable;
	
	public ArrayAdapter<String> recentSearchAdapter = null;
	public LinkedList<String> recentSearchList = null;
	
	public MainFeedAdapter feedAdapter = null;
	public MainFeedTask mainFeedTask = null;
	public SavedFeedTask savedFeedTask = null;
	public boolean allowInHistory = false;
		
	public DownloadSourceIconTask sourceIconTask = null;
	
	public AutoCompleteResultsAdapter acAdapter = null;
	
	public DDGPagerAdapter pageAdapter = null;
}
