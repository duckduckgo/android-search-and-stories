package com.duckduckgo.mobile.android.views.webview;

import java.util.Stack;

import com.duckduckgo.mobile.android.objects.FeedObject;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;

public class DDGWebView extends WebView {
	
	OnTouchListener extraTouchListener;
	public boolean isReadable = false;
	public Stack<Boolean> stackReadable;
	private boolean forceOriginalFormat = false;
	public boolean allowInHistory = false; 
	
	private DDGWebViewClient webViewClient = null;
	
	public DDGWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		attrSet = attrs;
		stackReadable = new Stack<Boolean>();
	}

	public boolean is_gone=true;
	public AttributeSet attrSet = null;
	
	public void setWebViewClient(DDGWebViewClient client) {
		webViewClient = client;
		super.setWebViewClient(client);
	}
	
	public DDGWebViewClient getWebViewClient() {
		return webViewClient;
	}
	
	public void setIsReadable(boolean isReadable) {
		this.stackReadable.push(isReadable);
		this.isReadable = isReadable;
	}
	
	public void onWindowVisibilityChanged(int visibility)
	       {super.onWindowVisibilityChanged(visibility);
	        if (visibility==View.GONE)
	           {try
	                {WebView.class.getMethod("onPause").invoke(this);//stop flash
	                }
	            catch (Exception e) {}
	            this.pauseTimers();
	            this.is_gone=true;
	           }
	        else if (visibility==View.VISIBLE)
	             {try
	                  {WebView.class.getMethod("onResume").invoke(this);//resume flash
	                  }
	              catch (Exception e) {}
	              this.resumeTimers();
	              this.is_gone=false;
	             }
	       }
	
	public AttributeSet getAttributes() {
		return attrSet;
	}
	
	public void stopView() {
		try
        {WebView.class.getMethod("onPause").invoke(this);//stop flash
        }
	    catch (Exception e) {}
	    this.pauseTimers();
	}
	
	public void resumeView() {
		try
        {WebView.class.getMethod("onResume").invoke(this);//resume flash
        }
	    catch (Exception e) {}
	    this.resumeTimers();
	}
	
	@Override
	public WebBackForwardList saveState(Bundle outState) {
		outState.putBoolean("isReadable", isReadable);
		return super.saveState(outState);
	}
	
	@Override
	public WebBackForwardList restoreState(Bundle inState) {
		isReadable = inState.getBoolean("isReadable");
		return super.restoreState(inState);
	}
	
//	public void onDetachedFromWindow()
//	       {//this will be trigger when back key pressed, not when home key pressed
//	        if (this.is_gone)
//	           {try
//	               {this.destroy();
//	               }
//	            catch (Exception e) {}
//	           }
//	       }

	
	public void readableAction(FeedObject feedObject) {
		if(!isReadable)
			setIsReadable(true);
		allowInHistory = true;
		loadDataWithBaseURL(feedObject.getUrl(), feedObject.getHtml(), "text/html", "utf8", feedObject.getUrl());
		forceOriginalFormat = false;
	}
	
	/**
	 * Whether original format of the web page is required (no readability)
	 * @return true if original format is required, false otherwise 
	 */
	public boolean isOriginalRequired() {
		return forceOriginalFormat;
	}
	
	public void resetReadabilityState() {
		setIsReadable(false);
		forceOriginalFormat = false;
	}
	
	public void clearReadabilityState() {
		isReadable = false;
		stackReadable.clear();
		forceOriginalFormat = false;
	}
	
	public void clearBrowserState() {		
		stopLoading();
		allowInHistory = false;
//		clearHistory();
		clearView();
		getWebViewClient().resetAnchorUrl();
		
		clearReadabilityState();
	}
	
}
