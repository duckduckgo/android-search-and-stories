package com.duckduckgo.mobile.android.views.webview;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class DDGWebChromeClient extends WebChromeClient {
	
	DuckDuckGo activity;
	
	public DDGWebChromeClient(DuckDuckGo activity) {
		this.activity = activity;
	}
	
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
		
		if(view.getVisibility() != View.VISIBLE) {
			return;
		}
		
		if(newProgress == 100){
			activity.getSearchField().setBackgroundDrawable(activity.mDuckDuckGoContainer.searchFieldDrawable);        			
		}
		else {
			if(!activity.mCleanSearchBar) {
				activity.mDuckDuckGoContainer.progressDrawable.setLevel(newProgress*100);
				activity.getSearchField().setBackgroundDrawable(activity.mDuckDuckGoContainer.progressDrawable);
			}
		}

	}
}
