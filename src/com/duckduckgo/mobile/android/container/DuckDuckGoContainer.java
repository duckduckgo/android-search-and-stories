package com.duckduckgo.mobile.android.container;

import java.util.Set;

import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.tasks.DownloadSourceIconTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.tasks.SavedFeedTask;

import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;

public class DuckDuckGoContainer {

	public boolean webviewShowing = false;
	public boolean prefShowing = false;
	public boolean savedFeedShowing = false;
	
	public Drawable progressDrawable, searchFieldDrawable;
	
	public ArrayAdapter<String> recentSearchAdapter = null;
	public Set<String> recentSearchSet = null;
	
	public MainFeedAdapter feedAdapter = null;
	public MainFeedTask mainFeedTask = null;
	public SavedFeedTask savedFeedTask = null;
	public boolean allowInHistory = false;
		
	public DownloadSourceIconTask sourceIconTask = null;
	
	public AutoCompleteResultsAdapter acAdapter = null;
	
	public DDGPagerAdapter pageAdapter = null;
}
