package com.duckduckgo.mobile.android.activity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.Holder;
import com.duckduckgo.mobile.android.listener.FeedListener;
import com.duckduckgo.mobile.android.listener.MimeDownloadListener;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.SuggestObject;
import com.duckduckgo.mobile.android.tasks.DownloadSourceIconTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.tasks.MimeDownloadTask;
import com.duckduckgo.mobile.android.tasks.SavedFeedTask;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.Item;
import com.duckduckgo.mobile.android.util.Item.ItemType;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SuggestType;
import com.duckduckgo.mobile.android.views.DDGWebView;
import com.duckduckgo.mobile.android.views.FanView;
import com.duckduckgo.mobile.android.views.MainFeedListView;
import com.duckduckgo.mobile.android.views.MainFeedListView.OnMainFeedItemLongClickListener;
import com.duckduckgo.mobile.android.views.MainFeedListView.OnMainFeedItemSelectedListener;
import com.duckduckgo.mobile.android.views.RecentSearchListView;
import com.duckduckgo.mobile.android.views.RecentSearchListView.OnRecentSearchItemSelectedListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshMainFeedListView;

public class DuckDuckGo extends Activity implements OnEditorActionListener, FeedListener, OnClickListener {

	protected final String TAG = "DuckDuckGo";
	
	DuckDuckGoContainer mDuckDuckGoContainer;
		
	private AutoCompleteTextView searchField = null;
	private ProgressBar feedProgressBar = null;
	private MainFeedListView feedView = null;
	private RecentSearchListView leftRecentView = null;
	
	private PullToRefreshMainFeedListView mPullRefreshFeedView = null;
	
	ArrayAdapter<String> lRecentAdapter;
	ListAdapter contextAdapter;
	
	private FanView fan;
	
	private RecentSearchListView recentSearchView = null;
	
	private DDGWebView mainWebView = null;
	private ImageButton homeSettingsButton = null;
	private ImageButton shareButton = null;
	private LinearLayout prefLayout = null;
	
	private TextView leftHomeTextView = null;
	private TextView leftStoriesTextView = null;
	private TextView leftSavedTextView = null;
	private TextView leftRecentTextView = null;
	private TextView leftSettingsTextView = null;
	
	private LinearLayout leftHomeButtonLayout = null;
	private LinearLayout leftStoriesButtonLayout = null;
	private LinearLayout leftSavedButtonLayout = null;
	private LinearLayout leftSettingsButtonLayout = null;
	
	// font scaling
	private LinearLayout fontSizeLayout = null;
		
	private SharedPreferences sharedPreferences;
		
	private boolean savedState = false;
	
	ArrayList<String> listContent;
	
	private final int PREFERENCES_RESULT = 0;
	
	Item[] shareDialogItems;
	FeedObject currentFeedObject = null;
	boolean isFeedObject = false;
	
	
	// for keeping filter source at same position
	String m_objectId = null;
	int m_itemHeight;
	int m_yOffset;
	
	boolean mScrollCancelLock = false;
	Runnable cachePrevNextTask = null, cachePrevNextHeadTask = null;
	
	// downloader for web view
	DownloadManager downloadManager;
	
	// pull-to-refresh textviews
	TextView ptrPrimary = null, ptrSecondary = null;
	
	boolean mCleanSearchBar = false;
			
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        		        
        setContentView(R.layout.twopane);
        
        fan = (FanView) findViewById(R.id.fan_view);
        fan.setViews(R.layout.main, R.layout.left_layout);
        
//        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        String countryCode = tm.getSimCountryIso();
//        String lang = getResources().getConfiguration().locale.getLanguage();
//        Log.v("COUNLANG",countryCode + " " + lang);
        
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        int height = displaymetrics.heightPixels;
//        int wwidth = displaymetrics.widthPixels;
        DDGUtils.feedItemWidth = displaymetrics.widthPixels;
        
        DDGUtils.feedItemHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) 135.0, getResources().getDisplayMetrics());
        
        DDGUtils.maxItemWidthHeight = Math.max(DDGUtils.feedItemWidth, DDGUtils.feedItemHeight);
        
        if(savedInstanceState != null)
        	savedState = true;
        
        sharedPreferences = DDGApplication.getSharedPreferences();
       
        //set caching task to run after at least a news feed item loads
        // cache prev/next 3 images
//		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
	        cachePrevNextTask = new Runnable() {
				
				@Override
				public void run() {
					DDGApplication.getImageDownloader().clearQueueDownloads();
					cachePrevNextImages(3);
				}
			};
			
			// task for the list "head rendering" case
			cachePrevNextHeadTask = new Runnable() {
				
				@Override
				public void run() {
					DDGApplication.getImageDownloader().clearQueueDownloads();
					cachePrevNextImages(3);
				}
			};
//		}
		// TODO caching prev/next images requires API Level 11 for now, because of  executeOnExecutor
		// in  ImageDownloader.queueUrls() , task.executeOnExecutor(this.executor, url);
		// implement cache prev/next for devices below API level 11
		// may need to use a modified copy of AsyncTask class to achieve this 
        
        ArrayList<Item> dialogItems = new ArrayList<Item>();
        dialogItems.add(new Item(getResources().getString(R.string.Share), android.R.drawable.ic_menu_share, ItemType.SHARE));
        dialogItems.add(new Item(getResources().getString(R.string.Save), android.R.drawable.ic_menu_save, ItemType.SAVE));
        dialogItems.add(new Item(getResources().getString(R.string.OpenInExternalBrowser), android.R.drawable.ic_menu_rotate, ItemType.EXTERNAL));

        shareDialogItems = (Item []) dialogItems.toArray(new Item[dialogItems.size()]);
        
		mDuckDuckGoContainer = (DuckDuckGoContainer) getLastNonConfigurationInstance();
    	if(mDuckDuckGoContainer == null){
    		mDuckDuckGoContainer = new DuckDuckGoContainer();
    		
    		mDuckDuckGoContainer.webviewShowing = false;
    		mDuckDuckGoContainer.prefShowing = false;
    		
    		mDuckDuckGoContainer.progressDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.page_progress);
    		mDuckDuckGoContainer.searchFieldDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.searchfield);
    		mDuckDuckGoContainer.searchFieldDrawable.setAlpha(150);
    		
    		mDuckDuckGoContainer.recentSearchSet = DDGUtils.loadSet(sharedPreferences, "recentsearch");

    		mDuckDuckGoContainer.recentSearchAdapter = new ArrayAdapter<String>(this, 
            		R.layout.recentsearch_list_layout, R.id.recentSearchText, 
            		new ArrayList<String>(mDuckDuckGoContainer.recentSearchSet));
    		
    		OnClickListener sourceClickListener = new OnClickListener() {
				
				public void onClick(View v) {
					// source filtering
					
					if(DDGControlVar.targetSource != null){
						cancelSourceFilter();
					}
					else {
					
						View itemParent = (View) v.getParent().getParent();
						int pos = feedView.getPositionForView(itemParent);
						m_objectId = ((FeedObject) feedView.getItemAtPosition(pos)).getId();
						m_itemHeight = itemParent.getHeight();
						Log.v(TAG, "POS " + pos);
						
						Rect r = new Rect();
						Point offset = new Point();
						feedView.getChildVisibleRect(itemParent, r, offset);						
						m_yOffset = offset.y; 
						Log.v(TAG,"OFFSET " + offset.y);
						
						String sourceType = ((AsyncImageView) v).getType(); 
						DDGControlVar.targetSource = sourceType;
												
						DDGControlVar.hasUpdatedFeed = false;
						mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(DuckDuckGo.this, DuckDuckGo.this);
						mDuckDuckGoContainer.mainFeedTask.execute();
					}
					
				}
			};
			
    		DDGControlVar.hasUpdatedFeed = false;
    		mDuckDuckGoContainer.feedAdapter = new MainFeedAdapter(this, sourceClickListener);
    		
    		mDuckDuckGoContainer.mainFeedTask = null;
    		mDuckDuckGoContainer.sourceIconTask = null;    		
    		
    	}
    	    	
		contextAdapter = new ArrayAdapter<Item>(
				this,
				android.R.layout.select_dialog_item,
				android.R.id.text1,
				shareDialogItems){
			public View getView(int position, View convertView, android.view.ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				TextView tv = (TextView)v.findViewById(android.R.id.text1);
				tv.setCompoundDrawablesWithIntrinsicBounds(shareDialogItems[position].icon, 0, 0, 0);

				//Add 10dp margin between image and text (support various screen densities)
				int dp10 = (int) (10 * getResources().getDisplayMetrics().density + 0.5f);
				tv.setCompoundDrawablePadding(dp10);

				return v;
			}
		};
    	    	
    	leftHomeTextView = (TextView) findViewById(R.id.LeftHomeTextView);
    	leftStoriesTextView = (TextView) findViewById(R.id.LeftStoriesTextView);
    	leftSavedTextView = (TextView) findViewById(R.id.LeftSavedTextView);
    	leftRecentTextView = (TextView) findViewById(R.id.LeftRecentTextView);
    	leftSettingsTextView = (TextView) findViewById(R.id.LeftSettingsTextView);
    	
    	
    	leftHomeButtonLayout = (LinearLayout) findViewById(R.id.LeftHomeButtonLayout);
    	leftStoriesButtonLayout = (LinearLayout) findViewById(R.id.LeftStoriesButtonLayout);
    	leftSavedButtonLayout = (LinearLayout) findViewById(R.id.LeftSavedButtonLayout);
    	leftSettingsButtonLayout = (LinearLayout) findViewById(R.id.LeftSettingsButtonLayout);
    	
    	
    	int pixelValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) 30.0, getResources().getDisplayMetrics());
    	
    	Drawable caret = getResources().getDrawable(R.drawable.icon_caret_selector);
    	caret.setBounds(0, 0, pixelValue, pixelValue);
    	
    	Drawable xt = getResources().getDrawable(R.drawable.icon_home_selector);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftHomeTextView.setCompoundDrawables(xt, null, caret, null);

        caret = getResources().getDrawable(R.drawable.icon_caret_selector);
    	caret.setBounds(0, 0, pixelValue, pixelValue);
        
    	xt = getResources().getDrawable(R.drawable.icon_stories_selector);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftStoriesTextView.setCompoundDrawables(xt, null, caret, null);        
        
        caret = getResources().getDrawable(R.drawable.icon_caret_selector);
    	caret.setBounds(0, 0, pixelValue, pixelValue);
        
    	xt = getResources().getDrawable(R.drawable.icon_saved_selector);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftSavedTextView.setCompoundDrawables(xt, null, caret, null);
        
        caret = getResources().getDrawable(R.drawable.icon_caret_selector);
    	caret.setBounds(0, 0, pixelValue, pixelValue);
        
    	xt = getResources().getDrawable(R.drawable.icon_settings_selector);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftSettingsTextView.setCompoundDrawables(xt, null, caret, null);
    	
    	leftHomeTextView.setOnClickListener(this);
    	leftStoriesTextView.setOnClickListener(this);
    	leftSavedTextView.setOnClickListener(this);
    	leftSettingsTextView.setOnClickListener(this);
    	
    	leftRecentView = (RecentSearchListView) findViewById(R.id.LeftRecentView);
    	
    	listContent = new ArrayList<String>();
    	for(String s : getResources().getStringArray(R.array.leftMenuDefault)) {
    		listContent.add(s);
    	}
    	
    	leftRecentView.setAdapter(mDuckDuckGoContainer.recentSearchAdapter);
    	leftRecentView.setOnRecentSearchItemSelectedListener(new OnRecentSearchItemSelectedListener() {
			
			public void onRecentSearchItemSelected(String recentQuery) {
				fan.showMenu();
				
				if(recentQuery != null){				
					searchWebTerm(recentQuery);
				}				
			}
		});
        
        homeSettingsButton = (ImageButton) findViewById(R.id.settingsButton);
        homeSettingsButton.setOnClickListener(this);
        
        if(mDuckDuckGoContainer.webviewShowing) {
        	homeSettingsButton.setImageResource(R.drawable.home_button);
        }
        
        shareButton = (ImageButton) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);
        
        // adjust visibility of share button after screen rotation
        if(mDuckDuckGoContainer.webviewShowing) {
        	shareButton.setVisibility(View.VISIBLE);
        }
        
        searchField = (AutoCompleteTextView) findViewById(R.id.searchEditText);
        searchField.setAdapter(new AutoCompleteResultsAdapter(this));
        searchField.setOnEditorActionListener(this);
        
        searchField.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// close left nav if it's open
				if(fan.isOpen()){
					fan.showMenu();
				}				
			}
		});

        searchField.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!sharedPreferences.getBoolean("modifyQueryPref", false)){
					//Hide the keyboard and perform a search
					hideKeyboard(searchField);
					searchField.dismissDropDown();
					
					SuggestObject suggestObject = (SuggestObject)parent.getAdapter().getItem(position);
					SuggestType suggestType = suggestObject.getType();
					if (suggestObject != null) {
						if(suggestType == SuggestType.TEXT) {
							String text = suggestObject.getPhrase();
							text.trim();
							searchOrGoToUrl(text);
						}
						else if(suggestType == SuggestType.APP) {
							DDGUtils.launchApp(DuckDuckGo.this, suggestObject.getSnippet());
						}
					}		
				}
				
			}
		});

        // This makes a little (X) to clear the search bar.
        final Drawable x = getResources().getDrawable(R.drawable.stop);
        x.setBounds(0, 0, (int)Math.floor(x.getIntrinsicWidth()/1.5), (int)Math.floor(x.getIntrinsicHeight()/1.5));
        searchField.setCompoundDrawables(null, null, searchField.getText().toString().equals("") ? null : x, null);

        searchField.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (searchField.getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > searchField.getWidth() - searchField.getPaddingRight() - x.getIntrinsicWidth()) {
                	searchField.setText("");
                	searchField.setCompoundDrawables(null, null, null, null);
                	
                	mCleanSearchBar = true;
                	searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
                }
                return false;
            }

        });

        searchField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	searchField.setCompoundDrawables(null, null, searchField.getText().toString().equals("") ? null : x, null);
            }

            public void afterTextChanged(Editable arg0) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        recentSearchView = (RecentSearchListView) findViewById(R.id.recentSearchItems);
        View header = getLayoutInflater().inflate(R.layout.recentsearch_header, null);
        recentSearchView.addHeaderView(header);
        recentSearchView.setAdapter(mDuckDuckGoContainer.recentSearchAdapter);
        recentSearchView.setOnRecentSearchItemSelectedListener(new OnRecentSearchItemSelectedListener() {
			
			public void onRecentSearchItemSelected(String recentQuery) {
				if(recentQuery != null){
					searchWebTerm(recentQuery);
				}				
			}
		});
        
        
		mPullRefreshFeedView = (PullToRefreshMainFeedListView) findViewById(R.id.mainFeedItems);
		ptrPrimary = (TextView) mPullRefreshFeedView.findViewById(R.id.pull_to_refresh_text);
		ptrSecondary = (TextView) mPullRefreshFeedView.findViewById(R.id.pull_to_refresh_sub_text);
		ptrPrimary.setTextSize(DDGControlVar.mainTextSize+2);
		ptrSecondary.setTextSize(DDGControlVar.mainTextSize);
		mPullRefreshFeedView.setHeaderText(DDGControlVar.mainTextSize+2);
		mPullRefreshFeedView.setHeaderSubText(DDGControlVar.mainTextSize);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshFeedView.setOnRefreshListener(new OnRefreshListener<MainFeedListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<MainFeedListView> refreshView) {
				mPullRefreshFeedView.setLastUpdatedLabel(DateUtils.formatDateTime(getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));

				// refresh the list
				mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(DuckDuckGo.this, DuckDuckGo.this);
				mDuckDuckGoContainer.mainFeedTask.execute();
			}
		});     
        		
		feedView = mPullRefreshFeedView.getRefreshableView();
        feedView.setAdapter(mDuckDuckGoContainer.feedAdapter);
        // context and LayoutParams for this cache task (to instantiate AsyncImageViews) will be set in feedView
        feedView.setAfterRenderTask(cachePrevNextHeadTask);
        feedView.setOnMainFeedItemSelectedListener(new OnMainFeedItemSelectedListener() {
			public void onMainFeedItemSelected(FeedObject feedObject) {
				// close left nav if it's open
				if(fan.isOpen()){
					fan.showMenu();
				}
				
				// keep a reference, so that we can reuse details while saving
				currentFeedObject = feedObject;
				isFeedObject = true;
				
				String url = feedObject.getUrl();
				if (url != null) {
					searchOrGoToUrl(url);
				}
				
				// record article as read
				String feedId = feedObject.getId();
				if(feedId != null){
					DDGControlVar.readArticles.add(feedId);
					mDuckDuckGoContainer.feedAdapter.notifyDataSetChanged();
				}
			}
        });
        feedView.setOnMainFeedItemLongClickListener(new OnMainFeedItemLongClickListener() {
			public void onMainFeedItemLongClick(FeedObject feedObject) {
				final String pageTitle = feedObject.getTitle();
				final String pageUrl = feedObject.getUrl();
				final FeedObject fObject = feedObject;
				
				// FIXME unify this code as one, extend DialogInterface.OnClickListener
				// to initialize with pageTitle, pageUrl and feedObject
				AlertDialog.Builder ab=new AlertDialog.Builder(DuckDuckGo.this);
				ab.setTitle(getResources().getString(R.string.MoreMenuTitle));
				ab.setAdapter(contextAdapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						Item it = ((Item) contextAdapter.getItem(item));
						if(it.type == Item.ItemType.SHARE) {
							DDGUtils.shareWebPage(DuckDuckGo.this, pageTitle, pageUrl);
						}
						else if(it.type == Item.ItemType.SAVE) {
							DDGApplication.getDB().insert(fObject);
						}
						else if(it.type == Item.ItemType.EXTERNAL) {
	    	            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl));
	    	            	startActivity(browserIntent);
						}
					}
				});
				ab.show();
			}
        });
        feedView.setOnScrollListener(new OnScrollListener() {
			
        	int firstVisibleItem;
        	
        	public void onScroll(AbsListView view, int firstVisibleItem,
        			int visibleItemCount, int totalItemCount) {
        		if(visibleItemCount > 0){
        			mDuckDuckGoContainer.feedAdapter.scrolling = true;
        		}
        	}

        	public void onScrollStateChanged(AbsListView view, int scrollState) {
        		Holder holder = null;
        		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
        			mScrollCancelLock = false;
        				mDuckDuckGoContainer.feedAdapter.scrolling = false;
        				final int count = feedView.getChildCount();        
        				firstVisibleItem = feedView.getFirstVisiblePosition()-feedView.getHeaderViewsCount();
        				
        				for(int i=0;i<count;++i){
        					View cv = feedView.getChildAt(i);
        					
        					if(cv instanceof FrameLayout)
        						continue;
        					
        					holder = (Holder) cv.getTag();
        					
        					if(!holder.imageViewBackground.getMemCacheDrawn()){
        						mDuckDuckGoContainer.feedAdapter.getView(firstVisibleItem+i, cv, view);
        					}
        				}
        				
        				if(holder != null) {
	        				// cache prev/next 3 images
	        				if(cachePrevNextTask != null) {
	        					cachePrevNextTask.run();		
	        				}
        				}
        	    }
        		else {
        			mDuckDuckGoContainer.feedAdapter.scrolling = true;        			
        			// clear all downloads related with visible views        			
        			// check if this is part of smooth scroll event after source filtering
        			if(!mScrollCancelLock) {
        				DDGApplication.getImageDownloader().clearQueueDownloads();
        				DDGApplication.getImageDownloader().clearVisibleDownloads();
        			}
        		}
        		
        	}
		});
        
        // NOTE: After loading url multiple times on the device, it may crash
        // Related to android bug report 21266 - Watch this ticket for possible resolutions
        // http://code.google.com/p/android/issues/detail?id=21266
        // Possibly also related to CSS Transforms (bug 21305)
        // http://code.google.com/p/android/issues/detail?id=21305
        mainWebView = (DDGWebView) findViewById(R.id.mainWebView);
        mainWebView.getSettings().setJavaScriptEnabled(true);
        mainWebView.setWebViewClient(new WebViewClient() {
        	public boolean shouldOverrideUrlLoading(WebView view, String url) { 
        		if(!savedState) {
        			// handle mailto: and tel: links with native apps
        			if(url.startsWith("mailto:")){
                        MailTo mt = MailTo.parse(url);
                        Intent i = DDGUtils.newEmailIntent(mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                        startActivity(i);
                        return true;
                    }
        			else if(url.startsWith("tel:")){
                        Intent i = DDGUtils.newTelIntent(url);
                        startActivity(i);
                        return true;
        			}
        			else {	
//        				view.loadUrl(url, DDGNetworkConstants.extraHeaders);
        				view.loadUrl(url);
        			}        			
        		}
        		return true;
        	}
        	
        	@SuppressLint("NewApi")
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
        		super.onPageStarted(view, url, favicon);      
        		
        		// Omnibar like behavior.
        		if (url.contains("duckduckgo.com")) {
        			// FIXME api level
        	        mainWebView.getSettings().setSupportZoom(true);
        	        mainWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        	        mainWebView.getSettings().setBuiltInZoomControls(false);
        	        mainWebView.getSettings().setUseWideViewPort(false);
        	        mainWebView.getSettings().setLoadWithOverviewMode(false);
        	        
        	        mainWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        	        mainWebView.getSettings().setPluginsEnabled(true); 

        	        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            	        mainWebView.getSettings().setEnableSmoothTransition(false);
            	        mainWebView.getSettings().setDisplayZoomControls(false);
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
            					setSearchBarText(realText);
        					}
        					else if(path != null && !path.equals("/")){
            					String text = path.substring(path.lastIndexOf("/") + 1).replace("_", " ");
            					setSearchBarText(text);
            				}
        					else {
        						setSearchBarText(url);
        					}
        				}
        				else {
        					setSearchBarText(url);
        				}
        			} else {
        				//Just in case...
        				setSearchBarText(url);
        			}
        		} else {
        			//This isn't duckduck go...
        			// FIXME api level
        	        mainWebView.getSettings().setSupportZoom(true);
        	        mainWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        	        mainWebView.getSettings().setBuiltInZoomControls(true);
        	        mainWebView.getSettings().setUseWideViewPort(true);
        	        mainWebView.getSettings().setLoadWithOverviewMode(true);
        	        mainWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        	        mainWebView.getSettings().setPluginsEnabled(true); 
        	        
        	        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            	        mainWebView.getSettings().setEnableSmoothTransition(true);
            	        mainWebView.getSettings().setDisplayZoomControls(false);
        	        }
        	        
        			setSearchBarText(url);
        		}
        	}
        	
        	public void onPageFinished (WebView view, String url) {
        		
        		mCleanSearchBar = false;
        		
        		if(!mDuckDuckGoContainer.allowInHistory) {
        			mainWebView.clearHistory();
        		}
        		
        		if(mainWebView.getVisibility() != View.VISIBLE) {
        			return;
        		}
        		
				searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
        	}
        });
                
        mainWebView.setWebChromeClient(new WebChromeClient(){
        	@Override
        	public void onProgressChanged(WebView view, int newProgress) {
        		super.onProgressChanged(view, newProgress);
        		
        		if(!mDuckDuckGoContainer.allowInHistory) {
        			mainWebView.clearHistory();
        		}
        		
        		if(mainWebView.getVisibility() != View.VISIBLE) {
        			return;
        		}
        		
        		if(newProgress == 100){
        			searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
        		}
        		else {
        			if(!mCleanSearchBar) {
        				mDuckDuckGoContainer.progressDrawable.setLevel(newProgress*100);
        				searchField.setBackgroundDrawable(mDuckDuckGoContainer.progressDrawable);
        			}
        		}

        	}
        });
        
        mainWebView.setExtraTouchListener(new View.OnTouchListener() {

        	public boolean onTouch(View v, MotionEvent event) {

        		HitTestResult hr = ((DDGWebView) v).getHitTestResult();
        		if(hr != null && hr.getExtra() != null) {            	
        			mDuckDuckGoContainer.allowInHistory = true; 
        			isFeedObject = false;
        			Log.i(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type=" + hr.getType());
        		}

        		return false;
        	}
        });

        mainWebView.setOnLongClickListener(new OnLongClickListener() {

        	@Override
        	public boolean onLongClick(View v) {
        		HitTestResult hr = ((DDGWebView) v).getHitTestResult();
        		if(hr != null && hr.getExtra() != null) {
        			Log.i(TAG, "LONG getExtra = "+ hr.getExtra() + "\t\t Type=" + hr.getType());
        			final String touchedUrl = hr.getExtra();
        			
        			AlertDialog dialog = new AlertDialog.Builder(DuckDuckGo.this).create();
        	        dialog.setTitle(getResources().getString(R.string.OpenInExternalBrowser));
        	        dialog.setMessage(getResources().getString(R.string.ConfirmExternalBrowser));
        	        dialog.setCancelable(false);
        	        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
        	            public void onClick(DialogInterface dialog, int buttonId) {
        	            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(touchedUrl));
        	            	startActivity(browserIntent);
        	            }
        	        });
        	        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
        	            public void onClick(DialogInterface dialog, int buttonId) {
        	                dialog.dismiss();
        	            }
        	        });
        	        dialog.setIcon(android.R.drawable.ic_dialog_info);
        	        dialog.show();
        		}

        		return false;
        	}
        });
        
        mainWebView.setDownloadListener(new DownloadListener() { 
            public void onDownloadStart(String url, String userAgent, 
                    String contentDisposition, String mimetype, 
                    long contentLength) { 
            	
            	DuckDuckGo.this.downloadContent(url, mimetype);
            } 
        }); 
        
        feedProgressBar = (ProgressBar) findViewById(R.id.feedLoadingProgress);
        
        prefLayout = (LinearLayout) findViewById(R.id.prefLayout);
        
        fontSizeLayout = (LinearLayout) findViewById(R.id.fontSeekLayout);
        
        SeekBar fontSizeSeekBar = (SeekBar) findViewById(R.id.fontSizeSeekBar);
        DDGControlVar.mainTextSize = sharedPreferences.getInt("mainFontSize", 14);
        fontSizeSeekBar.setProgress(DDGControlVar.mainTextSize);
        fontSizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {		
				DDGControlVar.mainTextSize = progress;
				mDuckDuckGoContainer.feedAdapter.notifyDataSetInvalidated();
				
				// adjust Pull-to-Refresh
				ptrPrimary.setTextSize(DDGControlVar.mainTextSize+2);
				ptrSecondary.setTextSize(DDGControlVar.mainTextSize);
				mPullRefreshFeedView.setHeaderText(DDGControlVar.mainTextSize+2);
				mPullRefreshFeedView.setHeaderSubText(DDGControlVar.mainTextSize);
			}
		});
        
        Button fontSizeApplyButton = (Button) findViewById(R.id.fontSizeApplyButton);
        fontSizeApplyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// save adjusted text size
				Editor editor = sharedPreferences.edit();
				editor.putInt("mainFontSize", DDGControlVar.mainTextSize);
				editor.commit();
				
				// adjust Pull-to-Refresh
				ptrPrimary.setTextSize(DDGControlVar.mainTextSize+2);
				ptrSecondary.setTextSize(DDGControlVar.mainTextSize);
				mPullRefreshFeedView.setHeaderText(DDGControlVar.mainTextSize+2);
				mPullRefreshFeedView.setHeaderSubText(DDGControlVar.mainTextSize);
				
				DDGControlVar.prevMainTextSize = 0;				
				fontSizeLayout.setVisibility(View.GONE);
			}
		});
		
    }	
	
	/**
	 * Cache previous/next N images
	 */
	private void cachePrevNextImages(int nImages) {
		// download/cache invisible feed items from -N to +N 
		int firstPos = feedView.getFirstVisiblePosition()-feedView.getHeaderViewsCount();
		int lastPos = feedView.getLastVisiblePosition()-feedView.getHeaderViewsCount();
		ArrayList<String> imageUrls = new ArrayList<String>();
		int startIndex = firstPos - nImages;
		int endIndex = lastPos + nImages;
		int totalCount = mDuckDuckGoContainer.feedAdapter.getCount();
		startIndex = (startIndex > 0) ? startIndex : 0;
		endIndex = (endIndex < totalCount) ? endIndex : (totalCount-1);
		// up
		for(int i=startIndex;i<firstPos;++i) {
			imageUrls.add(mDuckDuckGoContainer.feedAdapter.getItem(i).getImageUrl());
		}
		// down
		if(lastPos != totalCount-1) {
			for(int i=lastPos+1;i<=endIndex;++i) {
				imageUrls.add(mDuckDuckGoContainer.feedAdapter.getItem(i).getImageUrl());
			}
		}
		
		DDGApplication.getImageDownloader().queueUrls(getApplicationContext(), imageUrls);
	}
	
	/**
	 * Cancels source filter applied with source icon click from feed item
	 */
	public void cancelSourceFilter() {
		DDGControlVar.targetSource = null;		
		DDGControlVar.hasUpdatedFeed = false;
		mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(DuckDuckGo.this, DuckDuckGo.this);
		mDuckDuckGoContainer.mainFeedTask.execute();
	}
	
	private View buildLabel(String text) {
		    TextView result=new TextView(this);
		    result.setText(text);
		    return(result);
	}
	
	private View buildFromResource(int resId){
		return getLayoutInflater().inflate(resId, null);
	}
	
	private void clearSearchBar() {
		searchField.setText("");
    	searchField.setCompoundDrawables(null, null, null, null);
		searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
	}
	
	private void clearBrowserState() {		
		mainWebView.stopLoading();
		mDuckDuckGoContainer.allowInHistory = false;
//		mainWebView.clearHistory();
//		mainWebView.clearView();
	}
	
	public void setSearchBarText(String text) {
		searchField.setFocusable(false);
		searchField.setFocusableInTouchMode(false);
		searchField.setText(text);            
		searchField.setFocusable(true);
		searchField.setFocusableInTouchMode(true);
	}
	
	private void switchScreens(){
        // control which start screen is shown & configure related views
		
		mDuckDuckGoContainer.feedAdapter.scrolling = false;
		clearSearchBar();
		clearBrowserState();
		
		if(DDGControlVar.prevMainTextSize != 0) {
			fontSizeLayout.setVisibility(View.VISIBLE);
		}
		
        if(DDGControlVar.START_SCREEN == SCREEN.SCR_STORIES){
        	// show recent queries on slide-out menu
//        	lMainAdapter.remove(getString(R.string.LeftRecentQueries));
        	
    		// left side menu visibility changes
        	leftRecentTextView.setVisibility(View.VISIBLE);
        	leftRecentView.setVisibility(View.VISIBLE);	
        	leftSavedButtonLayout.setVisibility(View.VISIBLE);
        	leftStoriesButtonLayout.setVisibility(View.GONE);
        	
        	displayNewsFeed();
        }
        else if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH){
        	// hide recent queries from slide-out menu
//        	lMainAdapter.remove(getString(R.string.LeftRecentQueries));
//        	lMainAdapter.insert(getString(R.string.LeftRecentQueries), 0); 
        	
    		// left side menu visibility changes
        	leftRecentTextView.setVisibility(View.GONE);
        	leftRecentView.setVisibility(View.GONE);
        	leftSavedButtonLayout.setVisibility(View.VISIBLE);
        	leftStoriesButtonLayout.setVisibility(View.VISIBLE);
        	
        	displayRecentSearch();
        }
        else if(DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED_FEED){
    		// left side menu visibility changes
        	leftRecentTextView.setVisibility(View.VISIBLE);
        	leftRecentView.setVisibility(View.VISIBLE);
        	leftSavedButtonLayout.setVisibility(View.GONE);
        	leftStoriesButtonLayout.setVisibility(View.VISIBLE);
        	
        	displaySavedFeed();
        }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
			initDownloadManager();
		}
		
		// global search intent
        Intent intent = getIntent(); 
        
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			searchField.setText(query);
			searchWebTerm(query);
		}
		else {
			// not executed on global search for quick response
			mDuckDuckGoContainer.sourceIconTask = new DownloadSourceIconTask(getApplicationContext(), DDGApplication.getImageCache());
			mDuckDuckGoContainer.sourceIconTask.execute();
		
			if(mDuckDuckGoContainer.webviewShowing){
					mPullRefreshFeedView.setVisibility(View.GONE);
					mainWebView.setVisibility(View.VISIBLE);
			}	
			else if(!mDuckDuckGoContainer.prefShowing){
				switchScreens();
			}
			
			if(intent.getBooleanExtra("widget", false)) {
				searchField.requestFocus();
				showKeyboard(searchField);
			}
		
		}
		
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mDuckDuckGoContainer.mainFeedTask != null) {
			mDuckDuckGoContainer.mainFeedTask.cancel(false);
			mDuckDuckGoContainer.mainFeedTask = null;
		}
		
		String combined = "";
		for(String id : DDGControlVar.readArticles){
			combined += id + "-";
		}
		if(combined.length() != 0){
			Editor editor = sharedPreferences.edit();
			editor.putString("readarticles", combined);
			editor.commit();
		}		
	}
	
	@Override
	protected void onStop() {
		String combined = "";
		for(String id : DDGControlVar.readArticles){
			combined += id + "-";
		}
		if(combined.length() != 0){
			Editor editor = sharedPreferences.edit();
			editor.putString("readarticles", combined);
			editor.commit();
		}
				
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		DDGApplication.getImageDownloader().clearAllDownloads();
		DDGApplication.getImageCache().purge();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		if (mDuckDuckGoContainer.webviewShowing) {
			if (mainWebView.canGoBack()) {
				mainWebView.goBack();
			}
			else if(mDuckDuckGoContainer.savedFeedShowing) {
				clearSearchBar();
				clearBrowserState();
				displaySavedFeed();
			}
			else {
				// going home
				switchScreens();
			}
		}
		else if(mDuckDuckGoContainer.prefShowing){
			switchScreens();
		}
		else if(fontSizeLayout.getVisibility() != View.GONE) {
			DDGControlVar.mainTextSize = DDGControlVar.prevMainTextSize;
			mDuckDuckGoContainer.feedAdapter.notifyDataSetInvalidated();
			ptrPrimary.setTextSize(DDGControlVar.mainTextSize+2);
			ptrSecondary.setTextSize(DDGControlVar.mainTextSize+2);
			mPullRefreshFeedView.setHeaderText(DDGControlVar.mainTextSize+2);
			mPullRefreshFeedView.setHeaderSubText(DDGControlVar.mainTextSize);
			DDGControlVar.prevMainTextSize = 0;
			fontSizeLayout.setVisibility(View.GONE);
		}
		// main feed showing & source filter is active
		else if(DDGControlVar.targetSource != null){
			cancelSourceFilter();
		}
		else {
			DDGControlVar.hasUpdatedFeed = false;
			super.onBackPressed();
		}
	}
	
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v == searchField) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
			searchField.dismissDropDown();

			String text = searchField.getText().toString();
			text.trim();
			
			searchOrGoToUrl(text);
		}
		
		return false;
	}
	
	public void searchOrGoToUrl(String text) {
				
		if (text.length() > 0) {
			
			savedState = false;
			
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
	
	public void searchWebTerm(String term) {
		if(DDGControlVar.alwaysUseExternalBrowser) {
			String url;
			if(DDGControlVar.regionString == "wt-wt"){	// default
				url = DDGConstants.SEARCH_URL + URLEncoder.encode(term);
			}
			else {
				url = DDGConstants.SEARCH_URL + URLEncoder.encode(term) + "&kl=" + URLEncoder.encode(DDGControlVar.regionString);
			}
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        	startActivity(browserIntent);
        	return;
		}
		
		displayWebView();
		
		if(!savedState){
			if(DDGControlVar.regionString == "wt-wt"){	// default
				mainWebView.loadUrl(DDGConstants.SEARCH_URL + URLEncoder.encode(term));
			}
			else {
				mainWebView.loadUrl(DDGConstants.SEARCH_URL + URLEncoder.encode(term) + "&kl=" + URLEncoder.encode(DDGControlVar.regionString));
			}
		}
		
		// save recent query if "record history" is enabled
		if(sharedPreferences.getBoolean("recordHistoryPref", false)){
			if(!mDuckDuckGoContainer.recentSearchSet.contains(term)){
				mDuckDuckGoContainer.recentSearchSet.add(term);
				mDuckDuckGoContainer.recentSearchAdapter.add(term);
				
				Set<String> recentSearchSet = DDGUtils.loadSet(sharedPreferences, "recentsearch");
				recentSearchSet.add(term);
				DDGUtils.saveSet(sharedPreferences, recentSearchSet, "recentsearch");
			}
		}
	}
	
	public void clearRecentSearch() {
		mDuckDuckGoContainer.recentSearchSet.clear();
		mDuckDuckGoContainer.recentSearchAdapter.clear();
		recentSearchView.invalidate();
	}
	
	public void showWebUrl(String url) {
		if(DDGControlVar.alwaysUseExternalBrowser) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        	startActivity(browserIntent);
        	return;
		}
		
		displayWebView();
		
		if(!savedState)
//			mainWebView.loadUrl(url, DDGNetworkConstants.extraHeaders);
			mainWebView.loadUrl(url);
	}

	public void onFeedRetrieved(List<FeedObject> feed) {
		feedProgressBar.setVisibility(View.GONE);
		mDuckDuckGoContainer.feedAdapter.scrolling = false;
		mDuckDuckGoContainer.feedAdapter.addData(feed);
		mDuckDuckGoContainer.feedAdapter.notifyDataSetChanged();
		
		// update pull-to-refresh header to reflect task completion
		mPullRefreshFeedView.onRefreshComplete();
				
		DDGControlVar.hasUpdatedFeed = true;
		
		// do this upon filter completion
		if(DDGControlVar.targetSource != null && m_objectId != null) {
			int nPos = feedView.getSelectionPosById(m_objectId);
			mScrollCancelLock = true;
			Log.v(TAG, "nPOS " + nPos);
			
//			if(android.os.Build.VERSION.SDK_INT >= 11) {
//				feedView.smoothScrollToPositionFromTop(nPos, m_yOffset);
//			}
//			else {
				feedView.setSelectionFromTop(nPos,m_yOffset);
//			}
			
			// mark for blink animation (as a visual cue after list update)
			mDuckDuckGoContainer.feedAdapter.mark(nPos - feedView.getHeaderViewsCount());
		}
		else {
			// scroll triggers pre-caching for source filtering case
			// this is for the static, no-scroll case
			feedView.enableAfterRender();
		}
	}
	
	public void onFeedRetrievalFailed() {

		// Do not retry for SavedFeedTask, DB reply should be usable, when good or bad
		if (mDuckDuckGoContainer.savedFeedShowing && mDuckDuckGoContainer.savedFeedTask != null) {
			onFeedRetrieved(new ArrayList<FeedObject>());
			Toast.makeText(this, R.string.SavedFeedEmpty, Toast.LENGTH_LONG).show();
		}
		
		//If the mainFeedTask is null, we are currently paused
		//Otherwise, we can try again
		else if (!mDuckDuckGoContainer.savedFeedShowing && mDuckDuckGoContainer.mainFeedTask != null) {
			mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(DuckDuckGo.this, this);
			mDuckDuckGoContainer.mainFeedTask.execute();
		}
	}
	
	@TargetApi(11)
	public void showPrefFragment(){
//      	getFragmentManager().beginTransaction().replace(R.id.prefFragment,
//                new DDGPreferenceFragment()).commit();    	
      	
        FragmentManager fm = getFragmentManager();

        // Check to see if we have retained the worker fragment.
        DDGPreferenceFragment mWorkFragment = (DDGPreferenceFragment)fm.findFragmentById(R.id.prefFragment);

        // If not retained (or first time running), we need to create it.
        if (mWorkFragment == null) {
            mWorkFragment = new DDGPreferenceFragment();
            mWorkFragment.setRetainInstance(true);
            mWorkFragment.setCustomPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					// close left nav if it's open
					if(fan.isOpen()){
						fan.showMenu();
					}
					
					if(preference.getKey().equals("mainFontSizePref")) {
						DDGControlVar.prevMainTextSize = DDGControlVar.mainTextSize;
						prefLayout.setVisibility(View.GONE);
						switchScreens();
					}
					
					return false;
				}
			});
            fm.beginTransaction().replace(R.id.prefFragment,
                    mWorkFragment).commit();  
        }
        
        displayPreferences();
	}
	
	private void clearLeftSelect() {
		leftHomeTextView.setSelected(false);
		leftSavedTextView.setSelected(false);
		leftSettingsTextView.setSelected(false);
		leftStoriesTextView.setSelected(false);
	}
	
	public void displayPreferences(){
		mPullRefreshFeedView.setVisibility(View.GONE);
		mainWebView.setVisibility(View.GONE);
		shareButton.setVisibility(View.GONE);
		recentSearchView.setVisibility(View.GONE);
		prefLayout.setVisibility(View.VISIBLE);
		mDuckDuckGoContainer.prefShowing = true;
				
		searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
		mDuckDuckGoContainer.webviewShowing = false;
		
		clearLeftSelect();
		leftSettingsTextView.setSelected(true);
	}
	
	/**
	 * Method that switches visibility of views for Home or Saved feed
	 */
	private void displayFeedCore() {
    	// main view visibility changes and keep feed updated
		recentSearchView.setVisibility(View.GONE);
		mainWebView.setVisibility(View.GONE);
		shareButton.setVisibility(View.GONE);
		prefLayout.setVisibility(View.GONE);
		mPullRefreshFeedView.setVisibility(View.VISIBLE);
    	keepFeedUpdated();
    	mDuckDuckGoContainer.webviewShowing = false;
		mDuckDuckGoContainer.prefShowing = false;
	}
	
	public void displayNewsFeed(){
		if(mDuckDuckGoContainer.savedFeedShowing) {
			mDuckDuckGoContainer.savedFeedShowing = false;
			DDGControlVar.hasUpdatedFeed = false;
		}
		
		displayFeedCore();
		clearLeftSelect();
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_STORIES){
    		DDGControlVar.homeScreenShowing = true;
    		homeSettingsButton.setImageResource(R.drawable.menu_button);
			leftHomeTextView.setSelected(true);
    	}
    	else {
			leftStoriesTextView.setSelected(true);
    	}
	}
	
	public void displaySavedFeed(){
		mDuckDuckGoContainer.savedFeedShowing = true;
		DDGControlVar.hasUpdatedFeed = false;
		
		displayFeedCore();
		clearLeftSelect();
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED_FEED){
    		DDGControlVar.homeScreenShowing = true;
    		homeSettingsButton.setImageResource(R.drawable.menu_button);
			leftHomeTextView.setSelected(true);
    	}
    	else {
			leftSavedTextView.setSelected(true);
    	}
	}
	
	public void displayRecentSearch(){    	
    	// main view visibility changes
		mainWebView.setVisibility(View.GONE);
		shareButton.setVisibility(View.GONE);
		prefLayout.setVisibility(View.GONE);
		mPullRefreshFeedView.setVisibility(View.GONE);
    	feedProgressBar.setVisibility(View.GONE);
    	recentSearchView.setVisibility(View.VISIBLE);
    	mDuckDuckGoContainer.webviewShowing = false;
		mDuckDuckGoContainer.prefShowing = false;
		
		clearLeftSelect();
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH){
    		DDGControlVar.homeScreenShowing = true;
    		homeSettingsButton.setImageResource(R.drawable.menu_button);
    		leftHomeTextView.setSelected(true);
    	}
	}
	
	public void displayWebView() {
		// loading something in the browser - set home icon
		DDGControlVar.homeScreenShowing = false;
		homeSettingsButton.setImageResource(R.drawable.home_button);	
		
		if (!mDuckDuckGoContainer.webviewShowing) {
			mPullRefreshFeedView.setVisibility(View.GONE);
			recentSearchView.setVisibility(View.GONE);
			
			shareButton.setVisibility(View.VISIBLE);
			
			mainWebView.setVisibility(View.VISIBLE);
			mDuckDuckGoContainer.webviewShowing = true;
			mDuckDuckGoContainer.prefShowing = false;
			prefLayout.setVisibility(View.GONE);			
		}
	}
	
	public void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public void showKeyboard(View view) {
		// InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	public void onClick(View v) {
		if (v.equals(homeSettingsButton)) {			
			hideKeyboard(searchField);
			
			if(DDGControlVar.homeScreenShowing){
				fan.showMenu();
			}
			else {
				// going home
				switchScreens();
			}
		}
		else if (v.equals(shareButton)) {			
			hideKeyboard(searchField);

			AlertDialog.Builder ab=new AlertDialog.Builder(DuckDuckGo.this);
			ab.setTitle(getResources().getString(R.string.MoreMenuTitle));
			ab.setAdapter(contextAdapter, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					Item it = ((Item) contextAdapter.getItem(item));
					if(it.type == Item.ItemType.SHARE) {
						String pageTitle = mainWebView.getTitle();
						String pageUrl = mainWebView.getUrl();
						
						DDGUtils.shareWebPage(DuckDuckGo.this, pageTitle, pageUrl);
					}
					else if(it.type == Item.ItemType.SAVE) {
						if(isFeedObject) {
							// browsing a feed item, we can save it as is
							DDGApplication.getDB().insert(currentFeedObject);
						}
						else {
							String pageTitle = mainWebView.getTitle();
							String pageUrl = mainWebView.getUrl();
//							Log.v(TAG,"insert regular page: " + pageTitle + " " + pageUrl);
							DDGApplication.getDB().insert(new FeedObject(pageTitle, pageUrl));
						}
					}
					else if(it.type == Item.ItemType.EXTERNAL) {
    	            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mainWebView.getUrl()));
    	            	startActivity(browserIntent);
					}
				}
			});
			ab.show();
		}
		else if(v.equals(leftHomeTextView)){
			fan.showMenu();
			
			mDuckDuckGoContainer.prefShowing = false;
			
			if (mDuckDuckGoContainer.webviewShowing) {

				//We are going home!
				mainWebView.clearHistory();
				mainWebView.clearView();
				clearSearchBar();
				mDuckDuckGoContainer.webviewShowing = false;					
			}
			
			switchScreens();
		}
		else if(v.equals(leftStoriesTextView)){
			fan.showMenu();			
			displayNewsFeed();
		}
		else if(v.equals(leftSavedTextView)){
			fan.showMenu();			
			displaySavedFeed();
		}
		else if(v.equals(leftSettingsTextView)){
			fan.showMenu();
			
			if(!mDuckDuckGoContainer.prefShowing){
				
				if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
			        Intent intent = new Intent(getBaseContext(), Preferences.class);
			        startActivityForResult(intent, PREFERENCES_RESULT);
				}
				else {
					showPrefFragment();
				}
			
			}
		}
		
		// action for recent queries, leave as comment here		
//		else if(v.equals(leftRecentQueriesTextView)){
//			mDuckDuckGoContainer.prefShowing = false;
//			displayRecentSearch();
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PREFERENCES_RESULT){
			if (resultCode == RESULT_OK) {
				boolean clearedHistory = data.getBooleanExtra("hasClearedHistory",false);
				if(clearedHistory){
					clearRecentSearch();
				}
			}
		}
	}
	
    @Override
    public Object onRetainNonConfigurationInstance() {
    	// return page container, holding all non-view data
    	return mDuckDuckGoContainer;
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Save the state of the WebView
		mainWebView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);

		// Restore the state of the WebView
		mainWebView.restoreState(savedInstanceState);
	}
	
	/**
	 * Refresh feed if it's not marked as updated
	 */
	private void keepFeedUpdated()
	{
		if (!DDGControlVar.hasUpdatedFeed) {
			feedProgressBar.setVisibility(View.VISIBLE);
			
			if(mDuckDuckGoContainer.savedFeedShowing) {
				mDuckDuckGoContainer.savedFeedTask = new SavedFeedTask(this);
				mDuckDuckGoContainer.savedFeedTask.execute();
			}
			else {
				mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(DuckDuckGo.this, this);
				mDuckDuckGoContainer.mainFeedTask.execute();
			}
		}
	}
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		mDuckDuckGoContainer.feedAdapter.scrolling = false;
		super.onConfigurationChanged(newConfig);
	}	
	
	@SuppressLint("NewApi")
	private void downloadContent(final String url, final String mimeType) {
		// use mimeType to figure out an extension for temporary file
		int idxSlash = mimeType.indexOf('/') + 1;
		String ext = "tmp";
		if(idxSlash != -1) {
			ext = mimeType.substring(idxSlash);
		}
		String fileName = "down." + ext;
		
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
			Uri uri = Uri.parse(url);
			DownloadManager.Request r = new DownloadManager.Request(uri);

			// This put the download in the same Download dir the browser uses
			// r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

			// When downloading music and videos they will be listed in the player
			// (Seems to be available since Honeycomb only)
			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
				r.allowScanningByMediaScanner();

				// Notify user when download is completed
				// (Seems to be available since Honeycomb only)
				r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			}

			// Start download
			downloadManager.enqueue(r);
		}
		else {
			// manual download for devices below GINGERBREAD
			
			// TODO AsyncTask here
			
			MimeDownloadListener mimeListener = new MimeDownloadListener() {
				
				@Override
				public void onDownloadFailed() {
					// TODO Fail gracefully here... inform the user about failed download!
					Toast.makeText(DuckDuckGo.this, R.string.ErrorDownloadFailed, Toast.LENGTH_LONG).show();
				}
				
				@Override
				public void onDownloadComplete(String filePath) {
					// intent to view content
					Intent viewIntent = new Intent(Intent.ACTION_VIEW); 
					File file = new File(filePath);
					viewIntent.setDataAndType(Uri.fromFile(file), mimeType); 
					viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						startActivity(viewIntent);
					}
					catch(ActivityNotFoundException e) {	
						Toast.makeText(DuckDuckGo.this, R.string.ErrorActivityNotFound, Toast.LENGTH_LONG).show();
					}					
				}
			};
			
			MimeDownloadTask mimeTask = new MimeDownloadTask(mimeListener, url, fileName);
			mimeTask.execute();
		}		
	}
	
	@TargetApi(11)
	private void initDownloadManager() {
		downloadManager = (DownloadManager) getSystemService(DuckDuckGo.DOWNLOAD_SERVICE);        
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ( keyCode == KeyEvent.KEYCODE_MENU ) {
	        fan.showMenu();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
