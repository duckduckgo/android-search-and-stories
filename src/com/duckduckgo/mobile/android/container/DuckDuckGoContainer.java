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
	
	public Drawable progressDrawable, searchFieldDrawable;
	public Drawable stopDrawable;
			
	public MultiHistoryAdapter historyAdapter = null;
	public AutoCompleteResultsAdapter acAdapter = null;
	
	public DDGPagerAdapter pageAdapter = null;	
}
