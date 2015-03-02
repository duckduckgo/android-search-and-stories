package com.duckduckgo.mobile.android.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.KeyboardService;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.dialogs.OpenInExternalDialogBuilder;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewQueryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewStoryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewWebPageMenuDialog;
import com.duckduckgo.mobile.android.download.ContentDownloader;
import com.duckduckgo.mobile.android.events.DismissBangPopupEvent;
import com.duckduckgo.mobile.android.events.HandleShareButtonClickEvent;
import com.duckduckgo.mobile.android.events.OverflowButtonClickEvent;
import com.duckduckgo.mobile.android.events.ReadabilityFeedRetrieveSuccessEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewBackPressActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewClearBrowserStateEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewClearCacheEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewUpdateMenuNavigationEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewItemMenuClickEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewOpenMenuEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewReloadActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewSearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewSearchWebTermEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewShowHistoryObjectEvent;
import com.duckduckgo.mobile.android.events.externalEvents.SearchExternalEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedItemSelectedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuHomeClickEvent;
import com.duckduckgo.mobile.android.events.readabilityEvents.TurnReadabilityOffEvent;
import com.duckduckgo.mobile.android.events.readabilityEvents.TurnReadabilityOnEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveStoryEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveStoryEvent;
import com.duckduckgo.mobile.android.events.searchBarEvents.SearchBarSetTextEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareFeedEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareSearchEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareWebPageEvent;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.tasks.ReadableFeedTask;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.URLTYPE;
import com.duckduckgo.mobile.android.views.DDGOverflowMenu;
import com.duckduckgo.mobile.android.views.webview.DDGWebChromeClient;
import com.duckduckgo.mobile.android.views.webview.DDGWebView;
import com.duckduckgo.mobile.android.views.webview.DDGWebViewClient;
import com.squareup.otto.Subscribe;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

public class WebFragment extends Fragment {

	public static final String TAG = "web_fragment";
	public static final String URL = "url";

	private String mWebViewDefaultUA = null;
	private DDGWebView mainWebView = null;
	private ContentDownloader contentDownloader;
	private KeyboardService keyboardService;
	private View fragmentView;

	private boolean savedState = false;
	private URLTYPE urlType = URLTYPE.WEBPAGE;

    private Menu webMenu = null;
    private Menu headerMenu = null;
    private DDGOverflowMenu overflowMenu = null;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(savedInstanceState!=null) {
			savedState = true;
		}
		fragmentView = inflater.inflate(R.layout.fragment_web, container, false);
		return fragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setRetainInstance(true);
        init();

		// Restore the state of the WebView
		if(savedInstanceState!=null) {
			mainWebView.restoreState(savedInstanceState);
			urlType = URLTYPE.getByCode(savedInstanceState.getInt("url_type"));
		}
	}

    @Override
    public void onResume() {
        super.onResume();
        //setHasOptionsMenu(true);//aaa temp
        setHasOptionsMenu(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //setHasOptionsMenu(!hidden);
        setHasOptionsMenu(false);
        Log.e("aaa", "WEB fragment on hidden changed, HIDDEN: "+hidden+" - url: "+mainWebView.getUrl());
        if(!hidden) {
            BusProvider.getInstance().post(new SearchBarSetTextEvent(mainWebView.getUrl()));
        }
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("url_type", urlType.getCode());
		// Save the state of the WebView
		mainWebView.saveState(outState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        headerMenu = new MenuBuilder(getActivity());
        inflater.inflate(R.menu.web_navigation, headerMenu);
		inflater.inflate(R.menu.feed, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(headerMenu!=null) {
            MenuItem backItem = headerMenu.findItem(R.id.action_back);
            MenuItem forwardItem = headerMenu.findItem(R.id.action_forward);
            backItem.setEnabled(mainWebView.canGoBack());
            forwardItem.setEnabled(mainWebView.canGoForward());
        }
        if(menu==null) {
            return;
        }
        MenuItem saveItem = menu.findItem(R.id.action_add_favorite);
        MenuItem deleteItem = menu.findItem(R.id.action_remove_favorite);
        switch(urlType) {
            case FEED:
                if(DDGControlVar.currentFeedObject!=null) {
                    if(DDGControlVar.currentFeedObject.isSaved()) {
                        saveItem.setVisible(false);
                        deleteItem.setVisible(true);
                    } else {
                        saveItem.setVisible(true);
                        deleteItem.setVisible(false);
                    }
                } else {
                    saveItem.setVisible(false);
                    deleteItem.setVisible(false);
                }
                break;
            case SERP:
                String webViewUrl = mainWebView.getUrl();
                if(webViewUrl==null) {
                    webViewUrl = "";
                }
                String query = DDGUtils.getQueryIfSerp(webViewUrl);
                if(DDGApplication.getDB().isSavedSearch(query)) {
                    saveItem.setVisible(false);
                    deleteItem.setVisible(true);
                } else {
                    saveItem.setVisible(true);
                    deleteItem.setVisible(false);
                }
                break;
            case WEBPAGE:
            default:
                saveItem.setVisible(false);
                deleteItem.setVisible(false);
                break;
        }
        webMenu = menu;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
            case R.id.action_add_favorite:
                actionSave();
                return true;
            case R.id.action_remove_favorite:
                actionDelete();
                return true;
            case R.id.action_share:
				actionShare();
				return true;
            case R.id.action_external:
                actionExternalBrowser();
                return true;
            case R.id.action_back:
                mainWebView.backPressAction(false);
                if(!mainWebView.canGoBack()) {
                    BusProvider.getInstance().post(new WebViewUpdateMenuNavigationEvent(R.id.action_back, R.id.action_forward));
                }
                return true;
            case R.id.action_forward:
                mainWebView.forwardPressAction();
                if(!mainWebView.canGoForward()) {
                    BusProvider.getInstance().post(new WebViewUpdateMenuNavigationEvent(R.id.action_forward, R.id.action_back));
                }
                return true;
            case R.id.action_close:
                overflowMenu.dismiss();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void init() {
		keyboardService = new KeyboardService(getActivity());
		mainWebView = (DDGWebView) fragmentView.findViewById(R.id.fragmentMainWebView);
		mainWebView.setParentActivity(getActivity());
		mainWebView.getSettings().setJavaScriptEnabled(true);
		DDGWebView.recordCookies(PreferencesManager.getRecordCookies());
		DDGNetworkConstants.setWebView(mainWebView);

		// get default User-Agent string for reuse later
		mWebViewDefaultUA = mainWebView.getSettings().getUserAgentString();

		mainWebView.setWebViewClient(new DDGWebViewClient(this));
		View container = getActivity().findViewById(R.id.activityContainer);
		mainWebView.setWebChromeClient(new DDGWebChromeClient(this, container));
/*
		mainWebView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				WebView.HitTestResult hitTestResult = ((DDGWebView) v).getHitTestResult();
				if(hitTestResult != null && hitTestResult.getExtra() != null) {
					Log.i(TAG, "LONG getExtra = " + hitTestResult.getExtra() + "\t\t Type=" + hitTestResult.getType());
					final String touchedUrl = hitTestResult.getExtra();

					new OpenInExternalDialogBuilder(getActivity(), touchedUrl).show();
                    //new
				}

				return false;
			}
		});
*/
		contentDownloader = new ContentDownloader(getActivity());

		mainWebView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent,
										String contentDisposition, String mimetype,
										long contentLength) {

				contentDownloader.downloadContent(url, mimetype);
			}
		});

        webMenu = new MenuBuilder(getActivity());
        getActivity().getMenuInflater().inflate(R.menu.feed, webMenu);
        headerMenu = new MenuBuilder(getActivity());
        getActivity().getMenuInflater().inflate(R.menu.web_navigation, headerMenu);
	}

	public boolean getSavedState() {
		return savedState;
	}

	public void searchOrGoToUrl(String text) {
		searchOrGoToUrl(text, SESSIONTYPE.SESSION_BROWSE);
	}

	public void searchOrGoToUrl(String text, SESSIONTYPE sessionType) {
		keyboardService.hideKeyboard(mainWebView);
		savedState = false;

		BusProvider.getInstance().post(new DismissBangPopupEvent());

		DDGControlVar.mDuckDuckGoContainer.sessionType = sessionType;

		if(DDGControlVar.mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_FEED) {
			showFeed(DDGControlVar.currentFeedObject);
			return;
		}

		if (text.length() > 0) {
			java.net.URL searchAsUrl = null;
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

			if (searchAsUrl == null && !InetAddressUtils.isIPv4Address(text) && !InetAddressUtils.isIPv6Address(text)) {
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
/*
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
*/
	public void searchWebTerm(String term) {
		DDGControlVar.mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_SEARCH;

		DDGApplication.getDB().insertRecentSearch(term);
		DDGControlVar.mDuckDuckGoContainer.historyAdapter.sync();

		if(DDGControlVar.useExternalBrowser == DDGConstants.ALWAYS_EXTERNAL) {
			DDGUtils.searchExternal(getActivity(), term);
			return;
		}

		urlType = URLTYPE.SERP;

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
			DDGControlVar.mDuckDuckGoContainer.historyAdapter.sync();
			String feedId = historyObject.getFeedId();
			if(feedId != null) {
				BusProvider.getInstance().post(new FeedItemSelectedEvent(feedId));
			}
		}
		else {
			DDGApplication.getDB().insertHistoryObject(historyObject);
			DDGControlVar.mDuckDuckGoContainer.historyAdapter.sync();

			showWebUrl(historyObject.getUrl());
		}
	}

	public void showWebUrl(String url) {
		if(DDGControlVar.useExternalBrowser == DDGConstants.EXTERNAL_EXCEPT_SEARCHES
				|| DDGControlVar.useExternalBrowser == DDGConstants.ALWAYS_EXTERNAL) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			DDGUtils.execIntentIfSafe(getActivity(), browserIntent);
			return;
		}

		if(isStorySessionOrStoryUrl()) {
			DDGControlVar.mDuckDuckGoContainer.lastFeedUrl = url;
			if(DDGControlVar.currentFeedObject != null) {
				urlType = URLTYPE.FEED;
			}
		} else if(DDGUtils.isSerpUrl(url)) {
			urlType = URLTYPE.SERP;
		} else {
			urlType = URLTYPE.WEBPAGE;
		}

		if(!savedState) {
			mainWebView.setIsReadable(false);
			mainWebView.loadUrl(url);
		}
	}

	public void showFeed(FeedObject feedObject) {
		if(!savedState) {
			if(DDGControlVar.useExternalBrowser == DDGConstants.ALWAYS_INTERNAL
					&& PreferencesManager.getReadable()
					&& !mainWebView.isOriginalRequired()
					&& feedObject.getArticleUrl().length() != 0) {
				urlType = URLTYPE.FEED;
				new ReadableFeedTask(feedObject).execute();
			}
			else {
				showWebUrl(feedObject.getUrl());
			}
		}
	}

	private boolean isStorySessionOrStoryUrl() {
		return DDGControlVar.mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_FEED
				||
				( DDGControlVar.mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_BROWSE
						&& DDGControlVar.mDuckDuckGoContainer.lastFeedUrl.equals(mainWebView.getOriginalUrl())
				);
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
			DDGControlVar.mDuckDuckGoContainer.lastFeedUrl = webViewUrl;
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

	private void actionShare() {
		String webViewUrl = mainWebView.getUrl();
		if(webViewUrl==null) {
			webViewUrl = "";
		}
		switch(urlType) {
			case FEED:
				BusProvider.getInstance().post(new ShareFeedEvent(DDGControlVar.currentFeedObject.getTitle(), DDGControlVar.currentFeedObject.getUrl()));
				break;
			case SERP:
				BusProvider.getInstance().post(new ShareSearchEvent(webViewUrl));
				break;
			case WEBPAGE:
				BusProvider.getInstance().post(new ShareWebPageEvent(webViewUrl, webViewUrl));
				break;
			default:
				break;
		}
	}

	private void actionExternalBrowser() {
		String webViewUrl = mainWebView.getUrl();
		if(webViewUrl==null) {
			webViewUrl = "";
		}
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webViewUrl));
		DDGUtils.execIntentIfSafe(getActivity(), browserIntent);
	}

	private void actionReload() {
		if(!mainWebView.isReadable)
			mainWebView.reload();
		else {
			new ReadableFeedTask(DDGControlVar.currentFeedObject).execute();
		}
	}

	private void actionSave() {
		if(urlType==URLTYPE.FEED) {
			BusProvider.getInstance().post(new SaveStoryEvent(DDGControlVar.currentFeedObject));
		} else if(urlType==URLTYPE.SERP) {
			BusProvider.getInstance().post(new SaveSearchEvent(DDGUtils.getQueryIfSerp(mainWebView.getUrl())));
		}
	}

	private void actionDelete() {
		if(urlType==URLTYPE.FEED) {
			BusProvider.getInstance().post(new UnSaveStoryEvent(DDGControlVar.currentFeedObject.getId()));
		} else if(urlType==URLTYPE.SERP) {
			BusProvider.getInstance().post(new UnSaveSearchEvent(DDGUtils.getQueryIfSerp(mainWebView.getUrl())));
		}
	}

	private void actionTurnReadabilityOff() {
		String webViewUrl = mainWebView.getUrl();
		mainWebView.forceOriginal();
		showWebUrl(webViewUrl);
	}

	private void actionTurnReadabilityOn() {
		new ReadableFeedTask(DDGControlVar.currentFeedObject);
	}

	@Subscribe
	public void onWebViewClearBrowserStateEvent(WebViewClearBrowserStateEvent event) {
		//if(mainWebView!=null) {
			mainWebView.clearBrowserState();
		//}
	}

	@Subscribe
	public void onWebViewClearCacheEvent(WebViewClearCacheEvent event) {
		mainWebView.clearCache();
	}

	@Subscribe
	public void onWebViewReloadActionEvent(WebViewReloadActionEvent event) {
		actionReload();
	}

	@Subscribe
	public void onWebViewBackPressActionEvent(WebViewBackPressActionEvent event) {
		mainWebView.backPressAction(true);
	}
/*
	@Subscribe
	public void onSearchExternalEvent(SearchExternalEvent event) {
		searchExternal(event.query);
	}
*/
	@Subscribe
	public void onTurnReadabilityOffEvent(TurnReadabilityOffEvent event) {
		mainWebView.forceOriginal();
		showWebUrl(event.url);
	}

	@Subscribe
	public void onTurnReadabilityOnEvent(TurnReadabilityOnEvent event) {
		new ReadableFeedTask(event.feedObject).execute();
	}


	@Subscribe
	public void onReadabilityFeedRetrieveSuccessEvent(ReadabilityFeedRetrieveSuccessEvent event) {
		if(event.feed.size() != 0) {
			DDGControlVar.currentFeedObject = event.feed.get(0);
			DDGControlVar.mDuckDuckGoContainer.lastFeedUrl = DDGControlVar.currentFeedObject.getUrl();
			mainWebView.readableAction(DDGControlVar.currentFeedObject);
		}
	}

	@Subscribe
	public void onWebViewShowHistoryObjectEvent(WebViewShowHistoryObjectEvent event) {
		showHistoryObject(event.historyObject);
	}

	@Subscribe
	public void onLeftMenuHomeClickEvent(LeftMenuHomeClickEvent event) {
		mainWebView.clearHistory();
		mainWebView.clearView();
	}

	@Subscribe
	public void onWebViewSearchOrGoToUrlEvent(WebViewSearchOrGoToUrlEvent event) {
		searchOrGoToUrl(event.text, event.sessionType);
	}

	@Subscribe
	public void onWebViewSearchWebTermEvent(WebViewSearchWebTermEvent event) {
		searchWebTerm(event.term);
	}

	@Subscribe
	public void onHandleShareButtonClickEvent(HandleShareButtonClickEvent event) {
		handleShareButtonClick();
	}

    @Subscribe
    public void onWebViewItemMenuClickEvent(WebViewItemMenuClickEvent event) {
        onOptionsItemSelected(event.item);
    }

    @Subscribe
    public void onWebViewOpenMenuEvent(WebViewOpenMenuEvent event) {
        if(webMenu!=null) {

            onPrepareOptionsMenu(webMenu);

            if(overflowMenu!=null && overflowMenu.isShowing()) {
                return;
            }

            overflowMenu = new DDGOverflowMenu(getActivity());
            overflowMenu.setHeaderMenu(headerMenu);
            overflowMenu.setMenu(webMenu);
            overflowMenu.show(event.anchorView);

        }
    }

    @Subscribe
    public void onOverflowButtonClickEvent(OverflowButtonClickEvent event) {
        if(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(getTag()) && webMenu!=null) {
            //feedMenu.findItem(R.id.action_stories).setEnabled(false);
            if(overflowMenu!=null && overflowMenu.isShowing()) {
                return;
            }

            onPrepareOptionsMenu(webMenu);

            overflowMenu = new DDGOverflowMenu(getActivity());
            overflowMenu.setHeaderMenu(headerMenu);
            overflowMenu.setMenu(webMenu);
            overflowMenu.show(event.anchor);

            Log.e("aaa", "shuld open feed menu now, feed menu != null");
        } else {
            Log.e("aaa", "shuld open feed menu now, feed menu == null");
        }
    }

}
