package com.duckduckgo.mobile.android.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.util.Log;
import android.view.ContextMenu;
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
import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.activity.KeyboardService;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewQueryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewStoryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewWebPageMenuDialog;
import com.duckduckgo.mobile.android.download.ContentDownloader;
import com.duckduckgo.mobile.android.events.HandleShareButtonClickEvent;
import com.duckduckgo.mobile.android.events.OverflowButtonClickEvent;
import com.duckduckgo.mobile.android.events.ReadabilityFeedRetrieveSuccessEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewBackPressActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewClearBrowserStateEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewClearCacheEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewOnPageStarted;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewUpdateMenuNavigationEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewItemMenuClickEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewOpenMenuEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewReloadActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewSearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewSearchWebTermEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewShowHistoryObjectEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedItemSelectedEvent;
import com.duckduckgo.mobile.android.events.readabilityEvents.TurnReadabilityOffEvent;
import com.duckduckgo.mobile.android.events.readabilityEvents.TurnReadabilityOnEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveStoryEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveStoryEvent;
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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class WebFragment extends Fragment {

	public static final String TAG = "web_fragment";
	public static final String URL = "url";
    public static final String SESSION_TYPE = "session_type";
	private static final int ITEM_ID_SAVE_IMAGE = 0;
	private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private Context context = null;

	private String mWebViewDefaultUA = null;
	private DDGWebView mainWebView = null;
	private ContentDownloader contentDownloader;
	private KeyboardService keyboardService;
	private View fragmentView;

	private boolean savedState = false;
	private URLTYPE urlType = URLTYPE.WEBPAGE;

    private Menu webMenu = null;
    private Menu headerMenu = null;
    private Menu mainMenu = null;
    private DDGOverflowMenu overflowMenu = null;

    private ReadableFeedTask readableFeedTask;

    public static WebFragment newInstance(String url, SESSIONTYPE sessionType) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putInt(SESSION_TYPE, sessionType.getCode());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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
        context = getActivity();
        init();

		// Restore the state of the WebView
		if(savedInstanceState!=null) {
			mainWebView.restoreState(savedInstanceState);
			urlType = URLTYPE.getByCode(savedInstanceState.getInt("url_type"));
		}
		if (isDownloadImagesSupported()) {
			registerForContextMenu(mainWebView);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		WebView.HitTestResult hitTestResult = mainWebView.getHitTestResult();
		if (isDownloadImagesSupported() && isImage(hitTestResult)) {
			menu.setHeaderTitle(hitTestResult.getExtra());
			menu.add(0, ITEM_ID_SAVE_IMAGE, 0, R.string.save_image_context_menu_action);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == ITEM_ID_SAVE_IMAGE && isDownloadImagesSupported()) {
			if (hasWriteExternalStoragePermission()) {
				scheduleImageDownload();
			} else {
				requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
						REQUEST_WRITE_EXTERNAL_STORAGE);
			}
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			scheduleImageDownload();
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void scheduleImageDownload() {
		String imageUrl = mainWebView.getHitTestResult().getExtra();
		contentDownloader.downloadImage(imageUrl);
	}

	private boolean hasWriteExternalStoragePermission() {
		int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
		return permission == PackageManager.PERMISSION_GRANTED;
	}

	private boolean isImage(WebView.HitTestResult hitTestResult) {
		int type = hitTestResult.getType();
		return type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE;
	}

	private boolean isDownloadImagesSupported() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& contentDownloader.isDownloadManagerEnabled();
	}

	@Override
    public void onStart() {
        super.onStart();
        DDGControlVar.mDuckDuckGoContainer.torIntegration.prepareTorSettings();
    }

    @Override
    public void onResume() {
        super.onResume();
        //setHasOptionsMenu(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissMenu();
        if(readableFeedTask!=null) {
            readableFeedTask.cancel(true);
            readableFeedTask = null;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            DDGActionBarManager.getInstance().setSearchBarText(mainWebView.getUrl());
            mainWebView.getSettings().setJavaScriptEnabled(PreferencesManager.getEnableJavascript());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dismissMenu();
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
        headerMenu = new MenuBuilder(context);
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
        //MenuItem reloadItem = menu.findItem(R.id.action_reload);
        //reloadItem.setVisible(true);
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
				String url = mainWebView.getUrl();
				if(url==null) {
					url = "";
				}
				if(DDGApplication.getDB().isSavedSearch(url)) {
					saveItem.setVisible(false);
					deleteItem.setVisible(true);
				} else {
					saveItem.setVisible(true);
					deleteItem.setVisible(false);
				}
				break;
            default:
                saveItem.setVisible(false);
                deleteItem.setVisible(false);
                break;
        }
        webMenu = menu;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        HashMap<Integer, Boolean> newStates;
		switch(item.getItemId()) {
            case R.id.action_reload:
                actionReload();
                overflowMenu.dismiss();
                return true;
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
                newStates = new HashMap<Integer, Boolean>();
                newStates.put(R.id.action_back, mainWebView.canGoBack());
                newStates.put(R.id.action_forward, mainWebView.canGoForward());
                BusProvider.getInstance().post(new WebViewUpdateMenuNavigationEvent(newStates));
                return true;
            case R.id.action_forward:
                mainWebView.forwardPressAction();
                newStates = new HashMap<Integer, Boolean>();
                newStates.put(R.id.action_back, mainWebView.canGoBack());
                newStates.put(R.id.action_forward, mainWebView.canGoForward());
                BusProvider.getInstance().post(new WebViewUpdateMenuNavigationEvent(newStates));
                return true;/*
            case R.id.action_close:
                overflowMenu.dismiss();*/
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void init() {
		keyboardService = new KeyboardService(getActivity());
		mainWebView = (DDGWebView) fragmentView.findViewById(R.id.fragmentMainWebView);
		mainWebView.setParentActivity(getActivity());
		mainWebView.getSettings().setJavaScriptEnabled(PreferencesManager.getEnableJavascript());
        Log.e("javascript_enabled", PreferencesManager.getEnableJavascript()+"");
        DDGWebView.recordCookies(PreferencesManager.getRecordCookies());
		DDGNetworkConstants.setWebView(mainWebView);

		// get default User-Agent string for reuse later
		mWebViewDefaultUA = mainWebView.getSettings().getUserAgentString();

		mainWebView.setWebViewClient(new DDGWebViewClient(getActivity(), this));

        View hideContent = getActivity().findViewById(R.id.main_container);
        ViewGroup showContent = (ViewGroup) getActivity().findViewById(R.id.fullscreen_video_container);
        mainWebView.setWebChromeClient(new DDGWebChromeClient(getActivity(), hideContent, showContent));

		contentDownloader = new ContentDownloader(getActivity());

		mainWebView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent,
										String contentDisposition, String mimetype,
										long contentLength) {

				contentDownloader.downloadContent(url, mimetype);
			}
		});

        //temporary fix until next appcompat release
        //https://code.google.com/p/android/issues/detail?id=80434
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR2) {
            mainWebView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            mainWebView.setLongClickable(false);
        }

        webMenu = new MenuBuilder(getActivity());
        getActivity().getMenuInflater().inflate(R.menu.feed, webMenu);
        headerMenu = new MenuBuilder(getActivity());
        getActivity().getMenuInflater().inflate(R.menu.web_navigation, headerMenu);
        mainMenu = new MenuBuilder(getActivity());
        getActivity().getMenuInflater().inflate(R.menu.main, mainMenu);

        Bundle args = getArguments();

        if(args!=null) {
            String url = null;
            if(args.containsKey(URL)) url = args.getString(URL);
            SESSIONTYPE sessionType = SESSIONTYPE.SESSION_BROWSE;
            if(args.containsKey(SESSION_TYPE)) sessionType = SESSIONTYPE.getByCode(args.getInt(SESSION_TYPE));

            if(url!=null) {
                searchOrGoToUrl(url, sessionType);
            }
        }
	}

	public boolean getSavedState() {
		return savedState;
	}

	public void searchOrGoToUrl(String text) {
		searchOrGoToUrl(text, SESSIONTYPE.SESSION_BROWSE);
	}

	public void searchOrGoToUrl(String text, SESSIONTYPE sessionType) {
        DDGControlVar.mCleanSearchBar = false;
		savedState = false;

		DDGControlVar.mDuckDuckGoContainer.sessionType = sessionType;

		if(DDGControlVar.mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_FEED) {
			showFeed(DDGControlVar.currentFeedObject);
			return;
		}

		if (text!=null && text.length() > 0) {
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

			if (searchAsUrl == null && !DDGUtils.isValidIpAddress(text)) {
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

	public void searchWebTerm(String term) {
		DDGControlVar.mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_SEARCH;

		DDGApplication.getDB().insertRecentSearch(term);
		//DDGControlVar.mDuckDuckGoContainer.historyAdapter.sync();

		if(DDGControlVar.useExternalBrowser == DDGConstants.ALWAYS_EXTERNAL) {
			DDGUtils.searchExternal(context, term);
			return;
		}

		urlType = URLTYPE.SERP;

		if(!savedState){
            String baseUrl;
			if(DDGControlVar.regionString.equals("wt-wt")){	// default
                if(PreferencesManager.getEnableJavascript()) {
                    baseUrl = DDGConstants.SEARCH_URL;
                } else {
                    baseUrl = DDGConstants.SEARCH_URL_JAVASCRIPT_DISABLED;
                }
                mainWebView.loadUrl(baseUrl + URLEncoder.encode(term));
			}
			else {
                if(PreferencesManager.getEnableJavascript()) {
                    baseUrl = DDGConstants.SEARCH_URL;
                } else {
                    baseUrl = DDGConstants.SEARCH_URL_JAVASCRIPT_DISABLED;
                }
                mainWebView.loadUrl(baseUrl + URLEncoder.encode(term) + "&kl=" + URLEncoder.encode(DDGControlVar.regionString));
			}
		}
	}

	public void showHistoryObject(HistoryObject historyObject) {
		if(historyObject.isWebSearch()) {
			searchWebTerm(historyObject.getData());
		}
		else if(historyObject.isFeedObject()) {
			DDGApplication.getDB().insertHistoryObject(historyObject);
			//DDGControlVar.mDuckDuckGoContainer.historyAdapter.sync();
			String feedId = historyObject.getFeedId();
			if(feedId != null) {
				BusProvider.getInstance().post(new FeedItemSelectedEvent(feedId));
			}
		}
		else {
			DDGApplication.getDB().insertHistoryObject(historyObject);
			//DDGControlVar.mDuckDuckGoContainer.historyAdapter.sync();

			showWebUrl(historyObject.getUrl());
		}
	}

	public void showWebUrl(String url) {
		if(DDGControlVar.useExternalBrowser == DDGConstants.ALWAYS_EXTERNAL) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			DDGUtils.execIntentIfSafe(context, browserIntent);
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
        Log.e("show_feed", "DDGControlVar.useExternalBrowser == DDGConstants.ALWAYS_INTERNAL: "+(DDGControlVar.useExternalBrowser == DDGConstants.ALWAYS_INTERNAL));
        Log.e("show_feed", "PreferencesManager.getReadable(): "+(PreferencesManager.getReadable()));
        Log.e("show_feed", "mainWebView != null: "+(mainWebView!=null));
        Log.e("show_feed", "!mainWebView.isOriginalRequired(): "+((mainWebView==null)?"null":!mainWebView.isOriginalRequired()));
        Log.e("show_feed", "feedObject != null: "+(feedObject!=null));
        Log.e("show_feed", "feedObject.getArticleUrl() != null: "+((feedObject!=null)?feedObject.getArticleUrl():"null"));
        if(feedObject==null) return;
		if(!savedState) {
			if(DDGControlVar.useExternalBrowser == DDGConstants.ALWAYS_INTERNAL
					&& PreferencesManager.getReadable()
					&& !mainWebView.isOriginalRequired()
                    && feedObject.getArticleUrl()!=null
					&& feedObject.getArticleUrl().length() != 0) {
				urlType = URLTYPE.FEED;

                if(readableFeedTask!=null) {
                    readableFeedTask.cancel(true);
                    readableFeedTask = null;
                }
                readableFeedTask = new ReadableFeedTask(feedObject);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    readableFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    readableFeedTask.execute();
                }
			}
			else {
				showWebUrl(feedObject.getUrl());
			}
		}
	}

	private boolean isStorySessionOrStoryUrl() {
        String originalUrl = null;
        try {
            originalUrl = mainWebView.getOriginalUrl();
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
		return DDGControlVar.mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_FEED
				||
				( DDGControlVar.mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_BROWSE
                        && originalUrl!=null
						&& DDGControlVar.mDuckDuckGoContainer.lastFeedUrl.equals(originalUrl)
				);
	}

	private void handleShareButtonClick() {
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
				new WebViewStoryMenuDialog(context, DDGControlVar.currentFeedObject, mainWebView.isReadable).show();//
			}
		}
		else if(DDGUtils.isSerpUrl(webViewUrl)) {
			new WebViewQueryMenuDialog(context, webViewUrl).show();
		}
		else {
			new WebViewWebPageMenuDialog(context, webViewUrl).show();
		}
	}

	private void actionShare() {
		String webViewUrl = mainWebView.getUrl();
		if(webViewUrl==null) {
			webViewUrl = "";
		}
		switch(urlType) {
			case FEED:
                if(DDGControlVar.currentFeedObject!=null &&DDGControlVar.currentFeedObject.getTitle()!=null && DDGControlVar.currentFeedObject.getUrl()!=null) {
                    BusProvider.getInstance().post(new ShareFeedEvent(DDGControlVar.currentFeedObject.getTitle(), DDGControlVar.currentFeedObject.getUrl()));
                }
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
            if(DDGControlVar.currentFeedObject==null) return;
            readableFeedTask = new ReadableFeedTask(DDGControlVar.currentFeedObject);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                readableFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                readableFeedTask.execute();
            }
		}
	}

	private void actionSave() {
		switch(urlType) {
			case FEED:
				if(DDGControlVar.currentFeedObject==null) return;
				BusProvider.getInstance().post(new SaveStoryEvent(DDGControlVar.currentFeedObject));
				break;
			case SERP:
				String query = mainWebView.getUrl();
				if(query==null) return;
				BusProvider.getInstance().post(new SaveSearchEvent(DDGUtils.getQueryIfSerp(query)));
				break;
			case WEBPAGE:
				String url = mainWebView.getUrl();
				String title = mainWebView.getTitle();
				if(url==null || url.equals("")) return;
				if(title==null || title.equals("")) title = url;
				BusProvider.getInstance().post(new SaveSearchEvent(title, url));
				break;
		}
	}

	private void actionDelete() {
		switch(urlType) {
			case FEED:
				if(DDGControlVar.currentFeedObject==null) return;
				BusProvider.getInstance().post(new UnSaveStoryEvent(DDGControlVar.currentFeedObject.getId()));
				break;
			case SERP:
				BusProvider.getInstance().post(new UnSaveSearchEvent(DDGUtils.getQueryIfSerp(mainWebView.getUrl())));
				break;
			case WEBPAGE:
				BusProvider.getInstance().post(new UnSaveSearchEvent(mainWebView.getUrl()));
				break;
		}
		if(urlType==URLTYPE.FEED) {
		} else if(urlType==URLTYPE.SERP) {
		}
	}

	private void actionTurnReadabilityOff() {
		String webViewUrl = mainWebView.getUrl();
		mainWebView.forceOriginal();
		showWebUrl(webViewUrl);
	}

    public void setContext(Context context) {
       if(this.context==null) {
           this.context = context;
       }
    }

	private void actionTurnReadabilityOn() {
        if(readableFeedTask!=null) {
            readableFeedTask.cancel(true);
            readableFeedTask=null;
        }
        readableFeedTask = new ReadableFeedTask(DDGControlVar.currentFeedObject);
	}

    private void dismissMenu() {
        if(overflowMenu!=null && overflowMenu.isShowing()) {
            overflowMenu.dismiss();
        }
    }

	@Subscribe
	public void onWebViewClearBrowserStateEvent(WebViewClearBrowserStateEvent event) {
        mainWebView.clearBrowserState();
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

	@Subscribe
	public void onTurnReadabilityOffEvent(TurnReadabilityOffEvent event) {
		mainWebView.forceOriginal();
		showWebUrl(event.url);
	}

	@Subscribe
	public void onTurnReadabilityOnEvent(TurnReadabilityOnEvent event) {
        if(readableFeedTask!=null) {
            readableFeedTask.cancel(true);
        }
        readableFeedTask = new ReadableFeedTask(event.feedObject);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            readableFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            readableFeedTask.execute();
        }
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
            overflowMenu.setMenu(mainMenu, true);
            overflowMenu.show(event.anchorView);

        }
    }

    @Subscribe
    public void onOverflowButtonClickEvent(OverflowButtonClickEvent event) {
        if(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(getTag()) && webMenu!=null) {
            if(overflowMenu!=null && overflowMenu.isShowing()) {
                return;
            }

            onPrepareOptionsMenu(webMenu);

            overflowMenu = new DDGOverflowMenu(getActivity());
            overflowMenu.setHeaderMenu(headerMenu);
            overflowMenu.setMenu(webMenu);
            overflowMenu.setMenu(mainMenu, true);
            overflowMenu.show(event.anchor);
        }
    }

	@Subscribe
	public void onWebViewOnPageStarted(WebViewOnPageStarted event) {
		if(DDGControlVar.currentFeedObject!=null && DDGControlVar.currentFeedObject.getUrl()!=null
				&& DDGControlVar.currentFeedObject.getUrl().equals(event.url)) {
			urlType = URLTYPE.FEED;
		} else if(DDGUtils.isSerpUrl(event.url)) {
			urlType = URLTYPE.SERP;
		} else {
			urlType = URLTYPE.WEBPAGE;
		}
	}

}
