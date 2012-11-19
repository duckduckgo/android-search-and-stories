package com.duckduckgo.mobile.android.tasks;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;

public abstract class CacheTask implements Runnable {
	public Context context;
	public LayoutParams params;
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public void setLayoutParams(LayoutParams params) {
		this.params = params;
	}
}
