package com.duckduckgo.mobile.android.container;

import android.graphics.drawable.Drawable;

import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.adapters.MultiHistoryAdapter;

public class DuckDuckGoContainer {		
	
	public Drawable progressDrawable, searchFieldDrawable;
	public Drawable stopDrawable;
			
	public AutoCompleteResultsAdapter acAdapter = null;
	
	public DDGPagerAdapter pageAdapter = null;	
}
