package com.duckduckgo.mobile.android.fragment;


import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView.HitTestResult;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.KeyboardService;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.dialogs.OpenInExternalDialogBuilder;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewQueryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewStoryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewWebPageMenuDialog;
import com.duckduckgo.mobile.android.download.ContentDownloader;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.events.ReloadEvent;
import com.duckduckgo.mobile.android.events.ResetScreenStateEvent;
import com.duckduckgo.mobile.android.events.SearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.SearchWebTermEvent;
import com.duckduckgo.mobile.android.events.ShareButtonClickEvent;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.WebViewBackPressEvent;
import com.duckduckgo.mobile.android.events.WebViewResetEvent;
import com.duckduckgo.mobile.android.events.externalEvents.SearchExternalEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedItemSelectedEvent;
import com.duckduckgo.mobile.android.events.fontEvents.FontSizeCancelEvent;
import com.duckduckgo.mobile.android.events.fontEvents.FontSizeChangeEvent;
import com.duckduckgo.mobile.android.events.readabilityEvents.ReadabilityFeedRetrieveSuccessEvent;
import com.duckduckgo.mobile.android.events.readabilityEvents.TurnReadabilityOffEvent;
import com.duckduckgo.mobile.android.events.readabilityEvents.TurnReadabilityOnEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemSelectedEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.tasks.ReadableFeedTask;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.views.webview.DDGWebChromeClient;
import com.duckduckgo.mobile.android.views.webview.DDGWebView;
import com.duckduckgo.mobile.android.views.webview.DDGWebViewClient;
import com.squareup.otto.Subscribe;

public class WebFragment extends Fragment {
	
	protected final String TAG = "WebFragment";
	
	private View contentView;
	
	// keeps default User-Agent for WebView
	public String mWebViewDefaultUA = null;
	
	public DDGWebView mainWebView = null;
	private ContentDownloader contentDownloader;
	
	public boolean savedState = false;
	
	private KeyboardService keyboardService;
	
	private void initialise() {
		keyboardService = new KeyboardService(getActivity());
		contentDownloader = new ContentDownloader(getActivity());
		
        // NOTE: After loading url multiple times on the device, it may crash
        // Related to android bug report 21266 - Watch this ticket for possible resolutions
        // http://code.google.com/p/android/issues/detail?id=21266
        // Possibly also related to CSS Transforms (bug 21305)
        // http://code.google.com/p/android/issues/detail?id=21305
        mainWebView = (DDGWebView) contentView.findViewById(R.id.mainWebView);
        mainWebView.getSettings().setJavaScriptEnabled(true);
        
        
        // get default User-Agent string for reuse later
        mWebViewDefaultUA = mainWebView.getSettings().getUserAgentString();
        
        // read and configure web view font size
        if(DDGControlVar.webViewTextSize == -1) {
        	DDGControlVar.webViewTextSize = PreferencesManager.getWebviewFontSize();
        }
        
        if(DDGControlVar.webViewTextSize != -1) {
            mainWebView.getSettings().setDefaultFontSize(DDGControlVar.webViewTextSize);
        }
        else {
        	DDGControlVar.webViewTextSize = mainWebView.getSettings().getDefaultFontSize();
        }
        
        mainWebView.setWebViewClient(new DDGWebViewClient(this));            
        mainWebView.setWebChromeClient(new DDGWebChromeClient(this));
        
        mainWebView.setOnLongClickListener(new OnLongClickListener() {

        	@Override
        	public boolean onLongClick(View v) {
        		HitTestResult hitTestResult = ((DDGWebView) v).getHitTestResult();
        		if(hitTestResult != null && hitTestResult.getExtra() != null) {
        			Log.i(TAG, "LONG getExtra = "+ hitTestResult.getExtra() + "\t\t Type=" + hitTestResult.getType());
        			final String touchedUrl = hitTestResult.getExtra();

                    new OpenInExternalDialogBuilder(getActivity(), touchedUrl).show();
        		}

        		return false;
        	}
        });
        
        mainWebView.setDownloadListener(new DownloadListener() { 
            public void onDownloadStart(String url, String userAgent, 
                    String contentDisposition, String mimetype, 
                    long contentLength) { 
            	
            	contentDownloader.downloadContent(url, mimetype);
            } 
        }); 
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
		setRetainInstance(true);
        contentView = inflater.inflate(R.layout.web_fragment_view, container, false);
        if(savedInstanceState != null)
        	savedState = true;
		initialise();
        return contentView;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		BusProvider.getInstance().register(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		BusProvider.getInstance().unregister(this);
	}
	
	
	public void searchOrGoToUrl(String text) {
		searchOrGoToUrl(text, SESSIONTYPE.SESSION_BROWSE);
	}
	
	public void searchOrGoToUrl(String text, SESSIONTYPE sessionType) {
        keyboardService.hideKeyboard(mainWebView);
		savedState = false;
		
		DDGControlVar.sessionType = sessionType;
		
		if(DDGControlVar.sessionType == SESSIONTYPE.SESSION_FEED) {   
			showFeed(DDGControlVar.currentFeedObject);
			return;
		}
				
		if (text.length() > 0) {						
			URL searchAsUrl = null;
			String modifiedText = null;
			try {
				searchAsUrl = new URL(text);
				searchAsUrl.toURI();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
				searchAsUrl = null;
			}
			
			if (searchAsUrl == null) {
				modifiedText = "http://" + text;
				try {
					searchAsUrl = new URL(modifiedText);
					searchAsUrl.toURI();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
					searchAsUrl = null;
				}
			}			
			
			//We use the . check to determine if this is a single word or not... 
			//if it doesn't contain a . plus domain (2 more characters) it won't be a URL, even if it's valid, like http://test
			if (searchAsUrl != null) {
				if (modifiedText != null) {
					//Show the modified url text
					if (modifiedText.contains(".") && modifiedText.length() > (modifiedText.indexOf(".") + 2)) {
						showWebUrl(modifiedText);
					} else {
						searchWebTerm(text);
					}
				} else {
					if (text.contains(".") && text.length() > (text.indexOf(".") + 2)) {
						//Show the url text
						showWebUrl(text);
					} else {
						searchWebTerm(text);
					}
				}
			} else {
				searchWebTerm(text);
			}
		}
	}
	
	public void searchExternal(String term) {
		String url;
		if(DDGControlVar.regionString == "wt-wt"){	// default
			url = DDGConstants.SEARCH_URL.replace("ko=-1&", "") + URLEncoder.encode(term);
		}
		else {
			url = DDGConstants.SEARCH_URL.replace("ko=-1&", "") + URLEncoder.encode(term) + "&kl=" + URLEncoder.encode(DDGControlVar.regionString);
		}
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    	startActivity(browserIntent);
	}	
	
	public void searchWebTerm(String term) {
		DDGControlVar.sessionType = SESSIONTYPE.SESSION_SEARCH;
		
		DDGApplication.getDB().insertRecentSearch(term);
		BusProvider.getInstance().post(new SyncAdaptersEvent());
		
		if(DDGControlVar.alwaysUseExternalBrowser) {
			searchExternal(term);
        	return;
		}
				
		if(!savedState){
			if(DDGControlVar.regionString.equals("wt-wt")){	// default
				mainWebView.loadUrl(DDGConstants.SEARCH_URL + URLEncoder.encode(term));
			}
			else {
				mainWebView.loadUrl(DDGConstants.SEARCH_URL + URLEncoder.encode(term) + "&kl=" + URLEncoder.encode(DDGControlVar.regionString));
			}
		}		
	}
	
	
	public void showHistoryObject(HistoryObject historyObject) {
		if(historyObject.isWebSearch()) {
			searchWebTerm(historyObject.getData());
		}
		else if(historyObject.isFeedObject()) {
			DDGApplication.getDB().insertHistoryObject(historyObject);
			BusProvider.getInstance().post(new SyncAdaptersEvent());
			String feedId = historyObject.getFeedId();
			if(feedId != null) {
				BusProvider.getInstance().post(new FeedItemSelectedEvent(feedId));
			}
		}
		else {
			DDGApplication.getDB().insertHistoryObject(historyObject);
			BusProvider.getInstance().post(new SyncAdaptersEvent());
			showWebUrl(historyObject.getUrl());
		}		
	}
	
	public void showWebUrl(String url) {
		if(DDGControlVar.alwaysUseExternalBrowser) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			DDGUtils.execIntentIfSafe(getActivity(), browserIntent);
        	return;
		}
		
		if(!savedState) {			
			mainWebView.setIsReadable(false);
			mainWebView.loadUrl(url);
		}
	}
	
	public void showFeed(FeedObject feedObject) {
		if(!savedState) {
			if(!DDGControlVar.alwaysUseExternalBrowser
					&& PreferencesManager.getReadable()
					&& !mainWebView.isOriginalRequired()
					&& feedObject.getArticleUrl().length() != 0) {
				launchReadableFeedTask(feedObject);
			}
			else {
				showWebUrl(feedObject.getUrl());
			}
		}
	}
	
	
	private void handleShareButtonClick() {
        keyboardService.hideKeyboard(mainWebView);

		// XXX should make Page Options button disabled if the page is not loaded yet
		// url = null case
		String webViewUrl = mainWebView.getUrl();
		if(webViewUrl == null){
			webViewUrl = "";
		}
		
		// direct displaying after feed item is clicked
		// the rest will arrive as SESSION_BROWSE
		// so we should save this feed item with target redirected URL
		if(isStorySessionOrStoryUrl()) {
            DDGControlVar.lastFeedUrl = webViewUrl;
            if(DDGControlVar.currentFeedObject != null) {
            	new WebViewStoryMenuDialog(getActivity(), DDGControlVar.currentFeedObject, mainWebView.isReadable).show();
            }
		}						
		else if(DDGUtils.isSerpUrl(webViewUrl)) {
            new WebViewQueryMenuDialog(getActivity(), webViewUrl).show();
		}
		else {
			new WebViewWebPageMenuDialog(getActivity(), webViewUrl).show();
		}
	}
	
	public void launchReadableFeedTask(FeedObject feedObject) {
		new ReadableFeedTask(feedObject).execute();
	}

	private boolean isStorySessionOrStoryUrl() {
		return DDGControlVar.sessionType == SESSIONTYPE.SESSION_FEED
				|| 
				( DDGControlVar.sessionType == SESSIONTYPE.SESSION_BROWSE 
					&& DDGControlVar.lastFeedUrl.equals(mainWebView.getOriginalUrl()) 
				);
	}
	
	
	@Subscribe
	public void onFontSizeChange(FontSizeChangeEvent event) {
		DDGControlVar.webViewTextSize = DDGControlVar.prevWebViewTextSize + event.diff;
		mainWebView.getSettings().setDefaultFontSize(DDGControlVar.webViewTextSize);
	}
	
	public void onFontSizeCancel(FontSizeCancelEvent event) {
		DDGControlVar.webViewTextSize = DDGControlVar.prevWebViewTextSize;
		mainWebView.getSettings().setDefaultFontSize(DDGControlVar.webViewTextSize);
		DDGControlVar.prevWebViewTextSize = -1;
	}
	
	@Subscribe
	public void onResetScreen(ResetScreenStateEvent event) {
		mainWebView.clearBrowserState();
	}		
	
	@Subscribe
	public void onReadabilityFeedRetrieveSuccessEvent(ReadabilityFeedRetrieveSuccessEvent event) {
		if(event.feed.size() != 0) {
			DDGControlVar.currentFeedObject = event.feed.get(0);
			DDGControlVar.lastFeedUrl = DDGControlVar.currentFeedObject.getUrl();
			mainWebView.readableAction(DDGControlVar.currentFeedObject);
		}
	}
	
	@Subscribe
	public void onTurnReadabilityOffEvent(TurnReadabilityOffEvent event) {
		mainWebView.forceOriginal();
		showWebUrl(event.url);
	}
	
	@Subscribe
	public void onTurnReadabilityOnEvent(TurnReadabilityOnEvent event) {
		launchReadableFeedTask(event.feedObject);
	}
	
	
	
	@Subscribe
	public void onHistoryItemSelected(HistoryItemSelectedEvent event) {
		showHistoryObject(event.historyObject);
	}
	
	@Subscribe
	public void onSavedSearchItemSelected(SavedSearchItemSelectedEvent event) {
		searchWebTerm(event.query);	
		DDGUtils.itemSaveSearch(event.query);
		BusProvider.getInstance().post(new SyncAdaptersEvent());
	}

	@Subscribe
	public void onSearchExternalEvent(SearchExternalEvent event) {
		searchExternal(event.query);
	}
	
	@Subscribe
	public void onSearchOrGoToUrl(SearchOrGoToUrlEvent event) {
		searchOrGoToUrl(event.url, event.sessionType);
	}
	
	@Subscribe
	public void onSearchWebTerm(SearchWebTermEvent event) {
		searchWebTerm(event.term);
	}
	
	@Subscribe
	public void onShareButtonClick(ShareButtonClickEvent event) {
		handleShareButtonClick();
	}
	
	@Subscribe
	public void onWebViewReset(WebViewResetEvent event) {
		mainWebView.clearHistory();
		mainWebView.clearView();
	}
	
	@Subscribe
	public void onReload(ReloadEvent event) {
		if(!mainWebView.isReadable)
			mainWebView.reload(); 
		else {
			launchReadableFeedTask(DDGControlVar.currentFeedObject);
		}
	}
	
	@Subscribe
	public void onWebViewBackPress(WebViewBackPressEvent event) {
		mainWebView.backPressAction();
	}
	
}
