package com.duckduckgo.mobile.android.views.webview;

import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.searchBarEvents.SearchBarSetProgressEvent;
import com.duckduckgo.mobile.android.fragment.WebFragment;
import com.duckduckgo.mobile.android.util.DDGControlVar;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class DDGWebChromeClient extends WebChromeClient {
	
	Activity activity;
	WebFragment fragment;
	FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.MATCH_PARENT);
	View contentView;
	View customView = null;
	FrameLayout customViewContainer;
	CustomViewCallback customViewCallback;

	public boolean isVideoPlayingFullscreen = false;

	public DDGWebChromeClient(WebFragment fragment, View contentView) {
		this.fragment = fragment;
		this.contentView = contentView;
		activity = fragment.getActivity();
	}
	
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
		
		if(view.getVisibility() != View.VISIBLE) {
			return;
		}
        Log.e("aaa", "new progress: "+newProgress);

        if(newProgress == 100){
			//activity.getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable);
		}
		else {
			if(!DDGControlVar.mCleanSearchBar) {
				//DDGControlVar.mDuckDuckGoContainer.progressDrawable.setLevel(newProgress*100);
				//BusProvider.getInstance().post(new SearchBarSetProgressEvent(newProgress*100));//aaa - blue bar loading page
				//activity.getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.progressDrawable);
			}
		}

        if(!DDGControlVar.mCleanSearchBar/* && !DDGControlVar.pageLoaded*/) {

            DDGActionBarManager.getInstance().setProgress(newProgress);
        }
	}

	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		if (customView!=null) {
			callback.onCustomViewHidden();
			return;
		}
		contentView.setVisibility(View.GONE);
		customViewContainer = new FrameLayout(activity);
		customViewContainer.setLayoutParams(layoutParams);
		customViewContainer.addView(view);
		customView = view;
		customViewCallback = callback;
		customViewContainer.setVisibility(View.VISIBLE);
		isVideoPlayingFullscreen = true;
		activity.setContentView(customViewContainer);

	}

	@Override
	public void onHideCustomView() {
		isVideoPlayingFullscreen = false;
		if (customViewContainer==null) {
			return;
		} else {
			if (customView!=null) {
				customView.setVisibility(View.GONE);
				customViewContainer.removeView(customView);
				customView = null;
				customViewContainer.setVisibility(View.GONE);
				customViewCallback.onCustomViewHidden();
			}
			contentView.setVisibility(View.VISIBLE);
			activity.setContentView(contentView);
		}
	}

	public boolean isVideoPLayingFullscreen() {
		return isVideoPlayingFullscreen;
	}


	private void lockScreenOrientation() {
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	private void unlockScreenOrientaton() {
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}


}
