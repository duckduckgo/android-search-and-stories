package com.duckduckgo.mobile.android.views.webview;

import java.util.HashSet;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;

import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.DisplayScreenEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.PreferencesManager;

public class DDGWebView extends WebView {
	
	public boolean isReadable = false;
	private boolean forceOriginalFormat = false;
		
	private HashSet<String> readableList = new HashSet<String>();
	
	private DDGWebViewClient webViewClient = null;
	
	public boolean readableBackState = false;
	public boolean loadingReadableBack = false;
	
	public boolean shouldClearHistory = false;
	
	public DDGWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		attrSet = attrs;
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
		readableList.add(feedObject.getUrl());
		loadDataWithBaseURL(feedObject.getUrl(), feedObject.getHtml(), "text/html", "utf8", feedObject.getUrl());
		forceOriginalFormat = false;
//		clearHistory();
	}
	
	public void readableActionBack(FeedObject feedObject) {
		readableBackState = true;
		goBack();
	}
	
	/**
	 * Whether original format of the web page is required (no readability)
	 * @return true if original format is required, false otherwise 
	 */
	public boolean isOriginalRequired() {
		return forceOriginalFormat;
	}
	
	public void clearReadabilityState() {
		isReadable = false;
		forceOriginalFormat = false;
		
		readableBackState = false;
		loadingReadableBack = false;
	}
	
	public void forceOriginal() {
		isReadable = false;
		forceOriginalFormat = true;
	}
	
	public void clearBrowserState() {		
		stopLoading();
		clearHistory();
		clearView();
		shouldClearHistory = true;
		
		clearReadabilityState();
	}
	
	public void backPressAction() {		
		WebBackForwardList history = copyBackForwardList();
		int lastIndex = history.getCurrentIndex();
		
		if(lastIndex > 0) {
			WebHistoryItem prevItem = history.getItemAtIndex(lastIndex-1);
			
			if(prevItem != null) {
				String prevUrl = prevItem.getUrl();
				if(readableList.contains(prevUrl) && canDoReadability(prevUrl) && DDGControlVar.currentFeedObject != null) {
					readableActionBack(DDGControlVar.currentFeedObject);
				}
				else {
					goBack();
				}
			}
			else {
				goBack();
			}
		}
		else {
			BusProvider.getInstance().post(new DisplayScreenEvent(DDGControlVar.prevScreen));
		}
	}
	
	private boolean canDoReadability(String articleUrl) {
		return PreferencesManager.getReadable() 
				&& !isOriginalRequired()
				&& articleUrl != null
				&& articleUrl.length() != 0;
	}
	
}
