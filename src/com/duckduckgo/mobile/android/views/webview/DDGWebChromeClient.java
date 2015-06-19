package com.duckduckgo.mobile.android.views.webview;

import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.fragment.WebFragment;
import com.duckduckgo.mobile.android.util.DDGControlVar;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

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

        if(!DDGControlVar.mCleanSearchBar) {
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
                try {
                    customViewContainer.removeView(customView);
                } catch(NullPointerException e) {
                    e.printStackTrace();
                }
				customView.setVisibility(View.GONE);
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
