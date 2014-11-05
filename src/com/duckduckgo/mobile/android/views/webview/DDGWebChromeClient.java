package com.duckduckgo.mobile.android.views.webview;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;

import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class DDGWebChromeClient extends WebChromeClient {
	
	DuckDuckGo activity;
	FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.MATCH_PARENT);
	RelativeLayout contentView;
	View customView = null;
	FrameLayout customViewContainer;
	CustomViewCallback customViewCallback;

	public boolean isVideoPlayingFullscreen = false;

	public DDGWebChromeClient(DuckDuckGo activity, RelativeLayout contentView) {
		this.activity = activity;
		this.contentView = contentView;
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
