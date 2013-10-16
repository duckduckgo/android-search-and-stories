package com.duckduckgo.mobile.android.views.webview;

import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarProgressDrawableEvent;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarSearchDrawableEvent;
import com.duckduckgo.mobile.android.fragment.WebFragment;
import com.duckduckgo.mobile.android.util.DDGControlVar;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class DDGWebChromeClient extends WebChromeClient {
	
	WebFragment fragment;
	
	public DDGWebChromeClient(WebFragment fragment) {
		this.fragment = fragment;
	}
	
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
		
		if(view.getVisibility() != View.VISIBLE) {
			return;
		}
		
		if(newProgress == 100){
			BusProvider.getInstance().post(new SearchBarSearchDrawableEvent());        			
		}
		else {
			if(!DDGControlVar.mCleanSearchBar) {
				BusProvider.getInstance().post(new SearchBarProgressDrawableEvent(newProgress*100));
			}
		}

	}
}
