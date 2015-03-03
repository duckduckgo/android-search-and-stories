package com.duckduckgo.mobile.android.views.webview;

import java.util.HashSet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;

import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.DisplayScreenEvent;
import com.duckduckgo.mobile.android.events.RemoveWebFragmentEvent;
import com.duckduckgo.mobile.android.events.StopActionEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.PreferencesManager;

public class DDGWebView extends WebView {

    public static final String ABOUT_BLANK = "about:blank";
    public boolean isReadable = false;
	private boolean forceOriginalFormat = false;
		
	private HashSet<String> readableList = new HashSet<String>();
	
	private DDGWebViewClient webViewClient = null;
	private DDGWebChromeClient webChromeClient = null;
	private Activity activity;
	
	public boolean readableBackState = false;
    public boolean readableForwardState = false;
	public boolean loadingReadableBack = false;
	
	public boolean shouldClearHistory = false;

	public String mWebViewDefaultUA = null;
	
	public DDGWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		attrSet = attrs;
		mWebViewDefaultUA = getSettings().getUserAgentString();
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

	public void setWebChromeClient(DDGWebChromeClient client) {
		webChromeClient = client;
		super.setWebChromeClient(client);
	}

	public DDGWebChromeClient getWebChromeClient() {
		return webChromeClient;
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

    public void setUserAgentString(String url) {
        if(url.contains("duckduckgo.com")) {
            getSettings().setUserAgentString(DDGConstants.USER_AGENT);
        } else {
            getSettings().setUserAgentString(mWebViewDefaultUA);
        }
    }

    @Override
    public void loadUrl(String url) {
        setUserAgentString(url);
        super.loadUrl(url);
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
		loadDataWithBaseURL(feedObject.getUrl(), feedObject.getHtml(), "text/html", "UTF-8", feedObject.getUrl());
		forceOriginalFormat = false;
//		clearHistory();
	}
	
	public void readableActionBack(FeedObject feedObject) {
		readableBackState = true;
		goBack();
	}

    public void readableActonForward(FeedObject feedObject) {
        readableForwardState = true;
        goForward();
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
        readableForwardState = false;
		loadingReadableBack = false;
	}
	
	public void forceOriginal() {
		isReadable = false;
		forceOriginalFormat = true;
	}
	
	public void clearBrowserState() {		
		stopLoading();
		clearHistory();
        clearViewReliably();
        shouldClearHistory = true;
		
		clearReadabilityState();
	}

    /**
     * The clearView method was deprecated in API level 18. Use this instead
     * See https://developer.android.com/reference/android/webkit/WebView.html#clearView%28%29
     */
    private void clearViewReliably() {
        loadUrl(ABOUT_BLANK);
    }

    public void setParentActivity(Activity activity) {
		this.activity = activity;
	}
	
	public void backPressAction(boolean toHomeScreen) {
		WebBackForwardList history = copyBackForwardList();
		int lastIndex = history.getCurrentIndex();

        if(lastIndex > 0) {
			WebHistoryItem prevItem = history.getItemAtIndex(lastIndex-1);

			if(webChromeClient.isVideoPlayingFullscreen) {
				hideCustomView();
			} else if(prevItem != null) {
				String prevUrl = prevItem.getUrl();
                if(ABOUT_BLANK.equals(prevUrl)){
                    goBackOrForward(-2);
                    if(lastIndex > 0){
                        if(toHomeScreen)
						BusProvider.getInstance().post(new RemoveWebFragmentEvent());//DisplayScreenEvent(DDGControlVar.mDuckDuckGoContainer.prevScreen, false));
                    }
                    return;
                }
				if(readableList.contains(prevUrl) && canDoReadability(prevUrl) && DDGControlVar.currentFeedObject != null) {
//					readableAction(activity.currentFeedObject);
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
		else if(toHomeScreen) {
			BusProvider.getInstance().post(new RemoveWebFragmentEvent());//DisplayScreenEvent(DDGControlVar.mDuckDuckGoContainer.prevScreen, true));
			BusProvider.getInstance().post(new StopActionEvent());
		}
	}

    public void forwardPressAction() {
        WebBackForwardList history = copyBackForwardList();
        int lastIndex = history.getCurrentIndex();

        if(lastIndex < history.getSize()) {
            WebHistoryItem nextItem = history.getItemAtIndex(lastIndex+1);

            if(nextItem!=null) {
                String nextUrl = nextItem.getUrl();
                if(ABOUT_BLANK.equals(nextUrl)){
                    goBackOrForward(2);
                } else {
                    goForward();
                }
            }
        }
    }

	private boolean canDoReadability(String articleUrl) {
		return PreferencesManager.getReadable() 
				&& !isOriginalRequired()
				&& articleUrl != null
				&& articleUrl.length() != 0;
	}

    public static void clearCookies() {
        CookieManager.getInstance().removeAllCookie();
    }

    public static void recordCookies(boolean newState) {
        CookieManager.getInstance().setAcceptCookie(newState);
    }

    public static boolean hasCookies() {
        return CookieManager.getInstance().hasCookies();
    }

    public static boolean isRecordingCookies() {
        return CookieManager.getInstance().acceptCookie();
    }

    public void clearCache() {
        clearCache(true);
    }

	public void hideCustomView() {
		webChromeClient.onHideCustomView();
	}

	public boolean isVideoPlayingFullscreen() {
		return webChromeClient.isVideoPlayingFullscreen;
	}

}
