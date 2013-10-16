package com.duckduckgo.mobile.android.views.webview;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.dialogs.SSLCertificateDialog;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarClearEvent;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarSearchDrawableEvent;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarSetTextEvent;
import com.duckduckgo.mobile.android.fragment.WebFragment;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;

public class DDGWebViewClient extends WebViewClient {
	private boolean mLoaded = false;
	
	WebFragment fragment;
	
	public DDGWebViewClient(WebFragment fragment) {
		this.fragment = fragment;

	}
	 	
	private void clickedAnchorAction(DDGWebView view) {
		DDGControlVar.sessionType = SESSIONTYPE.SESSION_BROWSE;
	}
	        	        	        	
	public boolean shouldOverrideUrlLoading(WebView view, String url) { 
		// Log.i(TAG, "shouldOverrideUrl  " + url);
		
		if(!fragment.savedState && mLoaded) {			
			// handle mailto: and tel: links with native apps
			if(url.startsWith("mailto:")){
                MailTo mt = MailTo.parse(url);
                Intent i = DDGUtils.newEmailIntent(mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                fragment.startActivity(i);
                return true;
            }
			else if(url.startsWith("tel:")){
                Intent i = DDGUtils.newTelIntent(url);
                fragment.startActivity(i);
                return true;
			}
			else if(url.startsWith("file:///android_asset/webkit/")){
				return false;
			}
			else if(!(url.startsWith("http:") || url.startsWith("https:"))) {
				// custom handling, there can be a related app
				Intent customIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				DDGUtils.execIntentIfSafe(fragment.getActivity(), customIntent);
				return true;
			}
			else {
				clickedAnchorAction((DDGWebView) view);
			}
		}
		return false;
	}
	
	@SuppressLint("NewApi")
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		if(url.equals(DDGWebView.ABOUT_BLANK)){
			BusProvider.getInstance().post(new SearchBarClearEvent());
			return;
		}
		mLoaded = false;
        view.getSettings().setDomStorageEnabled(true);
        view.getSettings().setPluginState(PluginState.ON_DEMAND);
		
		DDGWebView wv = ((DDGWebView) view);
		if(wv.loadingReadableBack) {
			wv.stopLoading();	
			return;
		}
				        		
		if(url.equals(DDGControlVar.lastFeedUrl)) {
			DDGControlVar.sessionType = SESSIONTYPE.SESSION_FEED;
		}

		// Omnibar like behavior.
		if (url.contains("duckduckgo.com")) {
			view.getSettings().setUserAgentString(DDGConstants.USER_AGENT);
			
	        view.getSettings().setSupportZoom(true);
	        view.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
	        view.getSettings().setBuiltInZoomControls(false);
	        view.getSettings().setUseWideViewPort(false);
	        view.getSettings().setLoadWithOverviewMode(false);
	        
	        view.getSettings().setPluginState(WebSettings.PluginState.ON);
	        view.getSettings().setPluginsEnabled(true); 

	        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
    	        view.getSettings().setEnableSmoothTransition(false);
    	        view.getSettings().setDisplayZoomControls(false);
	        }
	        
			URL fullURL = null;
			try {
				fullURL = new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			if (fullURL != null) {
				//Okay, it's a valid url, which we already knew...
				
				// disambiguations appear directly as path string
				String path = fullURL.getPath();
				
				String query = fullURL.getQuery();
				if (query != null) {
					//Get the actual query string now...
					int index = query.indexOf("q=");
					if (index != -1) {
    					String text = query.substring(query.indexOf("q=") + 2);
    					if (text.contains("&")) {
    						text = text.substring(0, text.indexOf("&"));
    					}
    					String realText = URLDecoder.decode(text);
    					 BusProvider.getInstance().post(new SearchBarSetTextEvent(realText));
					}
					else if(path != null && !path.equals("/")){
    					String text = path.substring(path.lastIndexOf("/") + 1).replace("_", " ");
    					 BusProvider.getInstance().post(new SearchBarSetTextEvent(text));
    				}
					else {
						 BusProvider.getInstance().post(new SearchBarSetTextEvent(url));
					}
				}
				else {
					 BusProvider.getInstance().post(new SearchBarSetTextEvent(url));
				}
			} else {
				//Just in case...
				 BusProvider.getInstance().post(new SearchBarSetTextEvent(url));
			}
		} else {
			//This isn't duckduck go...
			view.getSettings().setUserAgentString(fragment.mWebViewDefaultUA);
			// This is a bit strange, but necessary to load Twitter in the app
			//TODO: Figure out a better way, it has something to do with JS with errors
			if (url.contains("twitter.com")) {
				view.getSettings().setUserAgentString(DDGConstants.USER_AGENT);
			}
			
	        view.getSettings().setSupportZoom(true);
	        view.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
	        view.getSettings().setBuiltInZoomControls(true);
	        view.getSettings().setUseWideViewPort(true);
	        view.getSettings().setLoadWithOverviewMode(true);
	        view.getSettings().setPluginState(WebSettings.PluginState.ON);
	        view.getSettings().setPluginsEnabled(true);
	        
	        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
    	        view.getSettings().setEnableSmoothTransition(true);
    	        view.getSettings().setDisplayZoomControls(false);
	        }
	        BusProvider.getInstance().post(new SearchBarSetTextEvent(url));
		}			
	}
	
	public void onPageFinished (WebView view, String url) {
		super.onPageFinished(view, url);
		
		mLoaded = true;
		
		DDGControlVar.mCleanSearchBar = false;
		
		if(view.getVisibility() != View.VISIBLE) {
			return;
		}
		
		BusProvider.getInstance().post(new SearchBarSearchDrawableEvent());
		
//		// This makes a little (X) to clear the search bar.
//		mDuckDuckGoContainer.reloadDrawable.setBounds(0, 0, (int)Math.floor(mDuckDuckGoContainer.reloadDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(mDuckDuckGoContainer.reloadDrawable.getIntrinsicHeight()/1.5));
//        searchField.setCompoundDrawables(null, null, mDuckDuckGoContainer.reloadDrawable, null);
		
		DDGWebView wv = ((DDGWebView) view);
		
		if(wv.readableBackState) {
			wv.readableBackState = false;
			if(wv.canGoBack()) {
				wv.loadingReadableBack = true;
				wv.goBack();
			}
			else {
				wv.shouldClearHistory = true;
				wv.readableAction(DDGControlVar.currentFeedObject);
			}
		}
		else if(wv.loadingReadableBack) {
			wv.loadingReadableBack = false;
			wv.readableAction(DDGControlVar.currentFeedObject);
		}				
		
		if(wv.shouldClearHistory) {
			wv.clearHistory();
			wv.shouldClearHistory = false;
		}
        wv.requestFocus();
	}
	
	@Override
	public void doUpdateVisitedHistory(WebView view, String url,
			boolean isReload) {
		DDGWebView wv = ((DDGWebView) view);
		if(wv.shouldClearHistory) {
			wv.clearHistory();
		}
		super.doUpdateVisitedHistory(view, url, isReload);
	}
	
	@Override
	public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
		if(Build.VERSION.SDK_INT == Build.VERSION_CODES.FROYO) {
			new SSLCertificateDialog(view.getContext(), handler, error).show();
			return;
		}
		super.onReceivedSslError(view, handler, error);
	}

}
