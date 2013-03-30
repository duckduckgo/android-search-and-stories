package com.duckduckgo.mobile.android.activity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.MailTo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.adapters.HistoryCursorAdapter;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.adapters.SavedFeedCursorAdapter;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.HistoryFeedMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.HistorySearchMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.MainFeedMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.SavedFeedMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.SavedSearchMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.WebViewQueryMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.WebViewStoryMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.WebViewWebPageMenuAdapter;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.Holder;
import com.duckduckgo.mobile.android.fragment.SavedFeedTabFragment;
import com.duckduckgo.mobile.android.fragment.SavedResultTabFragment;
import com.duckduckgo.mobile.android.listener.FeedListener;
import com.duckduckgo.mobile.android.listener.MimeDownloadListener;
import com.duckduckgo.mobile.android.listener.PreferenceChangeListener;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.PageTypes;
import com.duckduckgo.mobile.android.objects.SuggestObject;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.tabhost.TabHostExt;
import com.duckduckgo.mobile.android.tasks.DownloadSourceIconTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.tasks.MimeDownloadTask;
import com.duckduckgo.mobile.android.tasks.ReadableFeedTask;
import com.duckduckgo.mobile.android.tasks.ScanAppsTask;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.DDGViewPager;
import com.duckduckgo.mobile.android.util.Item;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.Sharer;
import com.duckduckgo.mobile.android.util.SuggestType;
import com.duckduckgo.mobile.android.util.builders.OptionsDialogBuilder;
import com.duckduckgo.mobile.android.views.DDGWebView;
import com.duckduckgo.mobile.android.views.HistoryListView;
import com.duckduckgo.mobile.android.views.HistoryListView.OnHistoryItemLongClickListener;
import com.duckduckgo.mobile.android.views.HistoryListView.OnHistoryItemSelectedListener;
import com.duckduckgo.mobile.android.views.MainFeedListView;
import com.duckduckgo.mobile.android.views.MainFeedListView.OnMainFeedItemLongClickListener;
import com.duckduckgo.mobile.android.views.MainFeedListView.OnMainFeedItemSelectedListener;
import com.duckduckgo.mobile.android.views.SavedSearchListView.OnSavedSearchItemLongClickListener;
import com.duckduckgo.mobile.android.views.SeekBarHint;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshMainFeedListView;

public class DuckDuckGo extends FragmentActivity implements OnEditorActionListener, FeedListener, OnClickListener {

	protected final String TAG = "DuckDuckGo";
	
	public DuckDuckGoContainer mDuckDuckGoContainer;
	
	// keeps default User-Agent for WebView
	private String mWebViewDefaultUA = null;
		
	private AutoCompleteTextView searchField = null;
	private MainFeedListView feedView = null;
	private HistoryListView leftRecentView = null;
	
	private PullToRefreshMainFeedListView mPullRefreshFeedView = null;
	
	ArrayAdapter<String> lRecentAdapter;
	
	private DDGViewPager viewPager;
	private View contentView = null;
	private View leftMenuView = null;
	
	private ViewFlipper viewFlipper = null;
	
	private HistoryListView recentSearchView = null;
	
	public DDGWebView mainWebView = null;
	private ImageButton homeSettingsButton = null;
	private ImageButton shareButton = null;
	
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
	
	// notification for "Save Recent Searches" feature awareness
	private View leftRecentHeaderView = null;
		
	private SharedPreferences sharedPreferences;
		
	private boolean savedState = false;
		
	private final int PREFERENCES_RESULT = 0;
	
	FeedObject currentFeedObject = null;
//	boolean isFeedObject = false;
	
	
	// for keeping filter source at same position
	String m_objectId = null;
	int m_itemHeight;
	int m_yOffset;
	
	boolean mScrollCancelLock = false;
	Runnable cachePrevNextTask = null, cachePrevNextHeadTask = null;
	
	// downloader for web view
	DownloadManager downloadManager;
	
	// keep prev progress in font seek bar, to make incremental changes available
	SeekBarHint fontSizeSeekBar;
	
	boolean mCleanSearchBar = false;
	
	AlertDialog.Builder cacheDialogBuilder;
	
	private TabHostExt savedTabHost = null;
	
	class TabFactory implements TabContentFactory {

		private final Context mContext;

	    /**
	     * @param context
	     */
	    public TabFactory(Context context) {
	        mContext = context;
	    }

	    /** (non-Javadoc)
	     * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
	     */
	    public View createTabContent(String tag) {
	        View v = new View(mContext);
	        v.setMinimumWidth(0);
	        v.setMinimumHeight(0);
	        return v;
	    }

	}
	
	class SourceClickListener implements OnClickListener {
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
				
				Rect r = new Rect();
				Point offset = new Point();
				feedView.getChildVisibleRect(itemParent, r, offset);						
				m_yOffset = offset.y; 
				
				String sourceType = ((AsyncImageView) v).getType(); 
				DDGControlVar.targetSource = sourceType;
										
				DDGControlVar.hasUpdatedFeed = false;
				keepFeedUpdated();
			}
			
		}
	}
	
	private void feedItemSelected(FeedObject feedObject) {
		// keep a reference, so that we can reuse details while saving
		currentFeedObject = feedObject;
		mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_FEED;		
		
		String url = feedObject.getUrl();
		if (url != null) {
			if(!DDGApplication.getDB().existsVisibleFeedById(feedObject.getId())) {
				DDGApplication.getDB().insertFeedItem(feedObject);
				syncAdapters();			
			}
			searchOrGoToUrl(url, SESSIONTYPE.SESSION_FEED);
		}
		
		// record article as read
		String feedId = feedObject.getId();
		if(feedId != null){
			DDGControlVar.readArticles.add(feedId);
			mDuckDuckGoContainer.feedAdapter.notifyDataSetChanged();
		}
	}
	
	private void feedItemSelected(String feedId) {
		FeedObject feedObject = DDGApplication.getDB().selectFeedById(feedId);
		feedItemSelected(feedObject);
	}
	
	
	public OnMainFeedItemSelectedListener mFeedItemSelectedListener = new OnMainFeedItemSelectedListener() {
		public void onMainFeedItemSelected(FeedObject feedObject) {
			// close left nav if it's open
			if(viewPager.isLeftMenuOpen()){
				viewPager.setCurrentItem(1);
			}
			
			feedItemSelected(feedObject);
		}
    };
    
    public OnMainFeedItemLongClickListener mFeedItemLongClickListener = new OnMainFeedItemLongClickListener() {
		public void onMainFeedItemLongClick(FeedObject feedObject) {	
			
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DuckDuckGo.this);
			alertBuilder.setTitle(R.string.StoryOptionsTitle);
			
			final PageMenuContextAdapter contextAdapter = new MainFeedMenuAdapter(DuckDuckGo.this, android.R.layout.select_dialog_item, 
					android.R.id.text1, "mainfeed", feedObject);
			
			alertBuilder.setAdapter(contextAdapter, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					Item clickedItem = ((Item) contextAdapter.getItem(item));
					clickedItem.ActionToExecute.Execute();
				}
			});
			alertBuilder.show();
		}
    };
    
    public OnMainFeedItemLongClickListener mSavedFeedItemLongClickListener = new OnMainFeedItemLongClickListener() {
		public void onMainFeedItemLongClick(FeedObject feedObject) {
			
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DuckDuckGo.this);
			alertBuilder.setTitle(R.string.StoryOptionsTitle);
			
			final PageMenuContextAdapter contextAdapter = new SavedFeedMenuAdapter(DuckDuckGo.this, android.R.layout.select_dialog_item, android.R.id.text1, "savedfeed", feedObject);
			
			alertBuilder.setAdapter(contextAdapter, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					Item clickedItem = ((Item) contextAdapter.getItem(item));
					clickedItem.ActionToExecute.Execute();
				}
			});
			alertBuilder .show();
		}
    };
    
    
    public OnSavedSearchItemLongClickListener mSavedSearchLongClickListener = new OnSavedSearchItemLongClickListener() {
    	@Override
    	public void onSavedSearchItemLongClick(final String query) {
    		    
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DuckDuckGo.this);
			alertDialogBuilder.setTitle(R.string.SearchOptionsTitle);
						
			final PageMenuContextAdapter contextAdapter = new SavedSearchMenuAdapter(DuckDuckGo.this, android.R.layout.select_dialog_item, android.R.id.text1, "savedsearch", query);
			
			alertDialogBuilder.setAdapter(contextAdapter, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					Item clickedItem = ((Item) contextAdapter.getItem(item));
					clickedItem.ActionToExecute.Execute();
				}
			});
			alertDialogBuilder.show();
    	}
    };
    
    
    public OnHistoryItemLongClickListener mHistoryLongClickListener = new OnHistoryItemLongClickListener() {
    	@Override
    	public void onHistoryItemLongClick(HistoryObject historyObject) {
			final String pageFeedId = historyObject.getFeedId();
			final String pageType = historyObject.getType();
    
			OptionsDialogBuilder alertDialogBuilder;
						
			final PageMenuContextAdapter contextAdapter;
			if(pageType.startsWith("F") && pageFeedId != null && pageFeedId.length() != 0) {
				contextAdapter = new HistoryFeedMenuAdapter(DuckDuckGo.this, android.R.layout.select_dialog_item, android.R.id.text1, historyObject);
				alertDialogBuilder = new OptionsDialogBuilder(DuckDuckGo.this, contextAdapter, R.string.StoryOptionsTitle);
			}
			else{
				contextAdapter = new HistorySearchMenuAdapter(DuckDuckGo.this, android.R.layout.select_dialog_item, android.R.id.text1, historyObject);
				alertDialogBuilder = new OptionsDialogBuilder(DuckDuckGo.this, contextAdapter, R.string.SearchOptionsTitle);
			}
			alertDialogBuilder.show();
    	}
    };
    
    private FeedListener mReadableListener = new FeedListener() {
		
		@Override
		public void onFeedRetrieved(List<FeedObject> feed, boolean fromCache) {
			if(feed.size() != 0) {
				currentFeedObject = feed.get(0);
				mDuckDuckGoContainer.lastFeedUrl = currentFeedObject.getUrl();
				readableAction(currentFeedObject);
			}
		}
		
		@Override
		public void onFeedRetrievalFailed() {
			// TODO Auto-generated method stub
			
		}
	};
	
	private void readableAction(FeedObject feedObject) {		
		mainWebView.isReadable = true;
		mainWebView.loadDataWithBaseURL(feedObject.getUrl(), feedObject.getHtml(), "text/html", "utf8", feedObject.getUrl());
		mDuckDuckGoContainer.forceOriginalFormat = false;
	}
    
    /**
     * save feed by object or by the feed id
     * 
     * @param feedObject
     * @param pageFeedId
     */
    public void itemSaveFeed(FeedObject feedObject, String pageFeedId) {
    	if(feedObject != null) {
    		if(DDGApplication.getDB().existsAllFeedById(feedObject.getId())) {
    			DDGApplication.getDB().makeItemVisible(feedObject.getId());
    		}
    		else {
    			DDGApplication.getDB().insertVisible(feedObject);
    		}
    	}
    	else if(pageFeedId != null && pageFeedId.length() != 0){
    		DDGApplication.getDB().makeItemVisible(pageFeedId);
    	}
    }
    
    public void itemSaveSearch(String query) {
    	DDGApplication.getDB().insertSavedSearch(query);
    }
    
    public void syncHistoryAdapters() {
    	mDuckDuckGoContainer.historyAdapter.changeCursor(DDGApplication.getDB().getCursorHistory());
		mDuckDuckGoContainer.historyAdapter.notifyDataSetChanged();
    	mDuckDuckGoContainer.recentSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSearchHistory());
		mDuckDuckGoContainer.recentSearchAdapter.notifyDataSetChanged();
    }
    
    public void syncAdapters() {
    	syncHistoryAdapters();
		mDuckDuckGoContainer.savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
		mDuckDuckGoContainer.savedSearchAdapter.notifyDataSetChanged();
		mDuckDuckGoContainer.savedFeedAdapter.changeCursor(DDGApplication.getDB().getCursorStoryFeed());
		mDuckDuckGoContainer.savedFeedAdapter.notifyDataSetChanged();
    }
				
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        
        sharedPreferences = DDGApplication.getSharedPreferences();
        
        String themeName = PreferencesManager.getThemeName();
		int themeId = getResources().getIdentifier(themeName, "style", getPackageName());
		if(themeId != 0) {
			setTheme(themeId);
		}
        		        
        setContentView(R.layout.pager);
          
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        DDGUtils.feedItemWidth = displaymetrics.widthPixels;
        
        DDGUtils.feedItemHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) 135.0, getResources().getDisplayMetrics());
        
        DDGUtils.maxItemWidthHeight = Math.max(DDGUtils.feedItemWidth, DDGUtils.feedItemHeight);
        
        if(savedInstanceState != null)
        	savedState = true;
        
        DDGControlVar.isAutocompleteActive = !PreferencesManager.getTurnOffAutocomplete();
               
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
        
		mDuckDuckGoContainer = (DuckDuckGoContainer) getLastCustomNonConfigurationInstance();
    	if(mDuckDuckGoContainer == null){
    		mDuckDuckGoContainer = new DuckDuckGoContainer();
    		
            mDuckDuckGoContainer.pageAdapter = new DDGPagerAdapter(this);
            
            mDuckDuckGoContainer.webviewShowing = false;
            mDuckDuckGoContainer.forceOriginalFormat = false;
    		
    		mDuckDuckGoContainer.stopDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.stop);
//    		mDuckDuckGoContainer.reloadDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.reload);
    		mDuckDuckGoContainer.progressDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.page_progress);
    		mDuckDuckGoContainer.searchFieldDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.searchfield);
    		mDuckDuckGoContainer.searchFieldDrawable.setAlpha(150);
    		    		
    		mDuckDuckGoContainer.recentSearchAdapter = new HistoryCursorAdapter(DuckDuckGo.this, DuckDuckGo.this, DDGApplication.getDB().getCursorSearchHistory());    		
    		mDuckDuckGoContainer.historyAdapter = new HistoryCursorAdapter(DuckDuckGo.this, DuckDuckGo.this, DDGApplication.getDB().getCursorHistory());
    		
    		SourceClickListener sourceClickListener = new SourceClickListener();			
    		mDuckDuckGoContainer.feedAdapter = new MainFeedAdapter(this, sourceClickListener);
    		
    		mDuckDuckGoContainer.mainFeedTask = null;
    		mDuckDuckGoContainer.sourceIconTask = null;    		
    		
    		mDuckDuckGoContainer.acAdapter = new AutoCompleteResultsAdapter(this);
    		
    		mDuckDuckGoContainer.savedSearchAdapter = new SavedResultCursorAdapter(DuckDuckGo.this, DuckDuckGo.this, DDGApplication.getDB().getCursorSavedSearch());    	
    		mDuckDuckGoContainer.savedFeedAdapter = new SavedFeedCursorAdapter(DuckDuckGo.this, DuckDuckGo.this, DDGApplication.getDB().getCursorStoryFeed());
    		
    	}
    	
    	// always refresh on start
    	DDGControlVar.hasUpdatedFeed = false;
    	
        viewPager = (DDGViewPager) findViewById(R.id.mainpager);
        viewPager.setAdapter(mDuckDuckGoContainer.pageAdapter);
        viewPager.setCurrentItem(1);
        
        leftMenuView = mDuckDuckGoContainer.pageAdapter.getPageView(0);
        contentView = mDuckDuckGoContainer.pageAdapter.getPageView(1);    
        
		// XXX Step 2: Setup TabHost
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
            savedTabHost.setCurrentTabByTag(savedInstanceState.getString("simple")); //set the tab as per the saved state
		}
        
        viewFlipper = (ViewFlipper) contentView.findViewById(R.id.ViewFlipperMain);
        // viewFlipper.setDisplayedChild(SCREEN.SCR_STORIES.getFlipOrder());
//        viewFlipper.setDisplayedChild(SCREEN.SCR_SAVED_FEED.getFlipOrder());       
        		
		cacheDialogBuilder = new AlertDialog.Builder(this);
		// Add the buttons
		cacheDialogBuilder.setTitle(R.string.ErrorFeedTitle);
		cacheDialogBuilder.setMessage(R.string.ErrorFeedDetail);
		cacheDialogBuilder.setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		           }
		       });
		cacheDialogBuilder.setNegativeButton(R.string.Retry, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   DDGControlVar.hasUpdatedFeed = false;
		        	   keepFeedUpdated();
		           }
		       });
    	    	
    	leftHomeTextView = (TextView) leftMenuView.findViewById(R.id.LeftHomeTextView);
    	leftStoriesTextView = (TextView) leftMenuView.findViewById(R.id.LeftStoriesTextView);
    	leftSavedTextView = (TextView) leftMenuView.findViewById(R.id.LeftSavedTextView);
    	leftRecentTextView = (TextView) leftMenuView.findViewById(R.id.LeftRecentTextView);
    	leftSettingsTextView = (TextView) leftMenuView.findViewById(R.id.LeftSettingsTextView);
    	
    	leftHomeTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftStoriesTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftSavedTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftRecentTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftSettingsTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);    	
    	
    	
    	TypedValue tmpTypedValue = new TypedValue(); 
    	getTheme().resolveAttribute(R.attr.leftButtonTextSize, tmpTypedValue, true);
    	// XXX getDimension returns in PIXELS !
    	float defLeftTitleTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.leftTitleTextSize = PreferencesManager.getLeftTitleTextSize(defLeftTitleTextSize);
    	
    	leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
//    	leftRecentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize); 
    	    	
    	leftHomeButtonLayout = (LinearLayout) leftMenuView.findViewById(R.id.LeftHomeButtonLayout);
    	leftStoriesButtonLayout = (LinearLayout) leftMenuView.findViewById(R.id.LeftStoriesButtonLayout);
    	leftSavedButtonLayout = (LinearLayout) leftMenuView.findViewById(R.id.LeftSavedButtonLayout);
    	leftSettingsButtonLayout = (LinearLayout) leftMenuView.findViewById(R.id.LeftSettingsButtonLayout);
    	
    	
    	int pixelValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) 20.0, getResources().getDisplayMetrics());
    	
    	TypedValue typedValue = new TypedValue(); 
    	getTheme().resolveAttribute(R.attr.leftDrawableHome, typedValue, true);
    	
    	Drawable xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftHomeTextView.setCompoundDrawables(xt, null, null, null);
        
        getTheme().resolveAttribute(R.attr.leftDrawableStories, typedValue, true);
    	xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftStoriesTextView.setCompoundDrawables(xt, null, null, null);        
        
        getTheme().resolveAttribute(R.attr.leftDrawableSaved, typedValue, true);
    	xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftSavedTextView.setCompoundDrawables(xt, null, null, null);
        
        getTheme().resolveAttribute(R.attr.leftDrawableSettings, typedValue, true);
    	xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftSettingsTextView.setCompoundDrawables(xt, null, null, null);
    	
    	leftHomeTextView.setOnClickListener(this);
    	leftStoriesTextView.setOnClickListener(this);
    	leftSavedTextView.setOnClickListener(this);
    	leftSettingsTextView.setOnClickListener(this);
    	
    	leftRecentView = (HistoryListView) leftMenuView.findViewById(R.id.LeftRecentView);
    	
		leftRecentHeaderView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.recentsearch_notrecording_layout, null, false);
		leftRecentHeaderView.setOnClickListener(this);
    	
		leftRecentView.setDivider(null);
    	leftRecentView.setAdapter(mDuckDuckGoContainer.historyAdapter);
    	leftRecentView.setOnHistoryItemSelectedListener(new OnHistoryItemSelectedListener() {
			
			public void onHistoryItemSelected(HistoryObject historyObject) {
				if(viewPager.isLeftMenuOpen()){
					viewPager.setCurrentItem(1);
				}
				
				if(historyObject != null){
					showHistoryObject((HistoryObject) historyObject);
				}				
			}
		});
    	leftRecentView.setOnHistoryItemLongClickListener(mHistoryLongClickListener);
        
        homeSettingsButton = (ImageButton) contentView.findViewById(R.id.settingsButton);
        homeSettingsButton.setOnClickListener(this);
        
        if(mDuckDuckGoContainer.webviewShowing) {
        	homeSettingsButton.setImageResource(R.drawable.home_button);
        }
        
        shareButton = (ImageButton) contentView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);
        
        // adjust visibility of share button after screen rotation
        if(mDuckDuckGoContainer.webviewShowing) {
        	shareButton.setVisibility(View.VISIBLE);
        }
        
        searchField = (AutoCompleteTextView) contentView.findViewById(R.id.searchEditText);
        searchField.setAdapter(mDuckDuckGoContainer.acAdapter);
        searchField.setOnEditorActionListener(this);
        
        searchField.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// close left nav if it's open
				if(viewPager.isLeftMenuOpen()){
					viewPager.setCurrentItem(1);
				}				
			}
		});

        searchField.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(PreferencesManager.getDirectQuery()){
					//Hide the keyboard and perform a search
					hideKeyboard(searchField);
					searchField.dismissDropDown();
					
					SuggestObject suggestObject = mDuckDuckGoContainer.acAdapter.getItem(position);
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
        mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int)Math.floor(mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight()/1.5));
        searchField.setCompoundDrawables(null, null, searchField.getText().toString().equals("") ? null : mDuckDuckGoContainer.stopDrawable, null);

        searchField.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
            	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    				mCleanSearchBar = true;
                	searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
                }
            	
                if (searchField.getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > searchField.getWidth() - searchField.getPaddingRight() - mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()) {
                	if(searchField.getCompoundDrawables()[2] == mDuckDuckGoContainer.stopDrawable) {
	                	stopAction();
                	}
                	else {
                		reloadAction();
                	}
                }
                return false;
            }

        });

        searchField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	searchField.setCompoundDrawables(null, null, searchField.getText().toString().equals("") ? null : mDuckDuckGoContainer.stopDrawable, null);
            }

            public void afterTextChanged(Editable arg0) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        recentSearchView = (HistoryListView) contentView.findViewById(R.id.recentSearchItems);
        recentSearchView.setDivider(null);
        recentSearchView.setAdapter(mDuckDuckGoContainer.recentSearchAdapter);
        recentSearchView.setOnHistoryItemSelectedListener(new OnHistoryItemSelectedListener() {
			
			public void onHistoryItemSelected(HistoryObject historyObject) {
				if(viewPager.isLeftMenuOpen()){
					viewPager.setCurrentItem(1);
				}
				
				if(historyObject != null){
					showHistoryObject((HistoryObject) historyObject);
				}				
			}
		});
        recentSearchView.setOnHistoryItemLongClickListener(mHistoryLongClickListener);
        
        
		mPullRefreshFeedView = (PullToRefreshMainFeedListView) contentView.findViewById(R.id.mainFeedItems);
		DDGControlVar.ptrHeaderSize = PreferencesManager.getPtrHeaderTextSize(mPullRefreshFeedView.getHeaderTextSize());
		DDGControlVar.ptrSubHeaderSize = PreferencesManager.getPtrHeaderSubTextSize(mPullRefreshFeedView.getHeaderSubTextSize());
		
		mPullRefreshFeedView.setHeaderTextSize(DDGControlVar.ptrHeaderSize);
		mPullRefreshFeedView.setHeaderSubTextSize(DDGControlVar.ptrSubHeaderSize);
		
		// set Loading... font
		mPullRefreshFeedView.setLoadingTextSize(DDGControlVar.ptrHeaderSize);
		mPullRefreshFeedView.setLoadingSubTextSize(DDGControlVar.ptrSubHeaderSize);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshFeedView.setOnRefreshListener(new OnRefreshListener<MainFeedListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<MainFeedListView> refreshView) {
				mPullRefreshFeedView.setLastUpdatedLabel(DateUtils.formatDateTime(getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));

				// refresh the list
				DDGControlVar.hasUpdatedFeed = false;
				keepFeedUpdated();
			}
		});     
        		
		feedView = mPullRefreshFeedView.getRefreshableView();
        feedView.setAdapter(mDuckDuckGoContainer.feedAdapter);
        // context and LayoutParams for this cache task (to instantiate AsyncImageViews) will be set in feedView
        feedView.setAfterRenderTask(cachePrevNextHeadTask);
        feedView.setOnMainFeedItemSelectedListener(mFeedItemSelectedListener);
        feedView.setOnMainFeedItemLongClickListener(mFeedItemLongClickListener);
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
        			else if(url.startsWith("file:///android_asset/webkit/")){
        				return false;
        			}
        			else if(!(url.startsWith("http:") || url.startsWith("https:"))) {
        				// custom handling, there can be a related app
        				try {
        					Intent customIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        					startActivity(customIntent);
        					return true;
        				}
        				catch(ActivityNotFoundException ae) {
        					// no related app, inform and still try to load in browser
        					Toast.makeText(DuckDuckGo.this, "No related app found!", Toast.LENGTH_LONG).show();
//        					view.loadUrl(url);
        				}
        			}
        		}
        		return false;
        	}
        	
        	@SuppressLint("NewApi")
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
        		super.onPageStarted(view, url, favicon);      
        		
        		if(url.equals(mDuckDuckGoContainer.lastFeedUrl)) {
        			mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_FEED;
        			
        			if(mainWebView.isReadable)
        				mainWebView.clearHistory();
        		}
    			        		
        		// Omnibar like behavior.
        		if (url.contains("duckduckgo.com")) {
        			mainWebView.getSettings().setUserAgentString(DDGConstants.USER_AGENT);
        			
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
        			mainWebView.getSettings().setUserAgentString(mWebViewDefaultUA);
        			// This is a bit strange, but necessary to load Twitter in the app
        			//TODO: Figure out a better way, it has something to do with JS with errors
        			if (url.contains("twitter.com")) {
        				mainWebView.getSettings().setUserAgentString(DDGConstants.USER_AGENT);
        			}
        			
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
        		super.onPageFinished(view, url);     
        		
        		mCleanSearchBar = false;
        		
        		if(!mDuckDuckGoContainer.allowInHistory) {
        			mainWebView.clearHistory();
        		}
        		
        		if(mainWebView.getVisibility() != View.VISIBLE) {
        			return;
        		}
        		
				searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
				
//				// This makes a little (X) to clear the search bar.
//				mDuckDuckGoContainer.reloadDrawable.setBounds(0, 0, (int)Math.floor(mDuckDuckGoContainer.reloadDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(mDuckDuckGoContainer.reloadDrawable.getIntrinsicHeight()/1.5));
//		        searchField.setCompoundDrawables(null, null, mDuckDuckGoContainer.reloadDrawable, null);
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
        			mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_BROWSE;
        			resetReadabilityState();
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
                        
        fontSizeLayout = (LinearLayout) contentView.findViewById(R.id.fontSeekLayout);
        
        fontSizeSeekBar = (SeekBarHint) contentView.findViewById(R.id.fontSizeSeekBar);
        
    	getTheme().resolveAttribute(R.attr.mainTextSize, tmpTypedValue, true);
    	float defMainTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.mainTextSize = PreferencesManager.getMainFontSize(defMainTextSize);
    	
    	getTheme().resolveAttribute(R.attr.recentTextSize, tmpTypedValue, true);
    	float defRecentTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.recentTextSize = PreferencesManager.getRecentFontSize(defRecentTextSize);
        
        fontSizeSeekBar.setProgress(DDGControlVar.fontPrevProgress);
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
				if(!fromUser) return;
								
				int diff = progress - DDGControlVar.fontPrevProgress;
				float diffPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 
		                (float) diff, getResources().getDisplayMetrics());
				// set thumb text
				if(diff == 0) {
					fontSizeSeekBar.setExtraText(getResources().getString(R.string.NoChange));
				}
				else if(progress == DDGConstants.FONT_SEEKBAR_MID) {
					fontSizeSeekBar.setExtraText(getResources().getString(R.string.Defaults));
				}
				else if(progress > DDGConstants.FONT_SEEKBAR_MID) {
					fontSizeSeekBar.setExtraText("+" + (progress-DDGConstants.FONT_SEEKBAR_MID));
				}
				else {
					fontSizeSeekBar.setExtraText(String.valueOf((progress-DDGConstants.FONT_SEEKBAR_MID)));
				}
				DDGControlVar.fontProgress = progress;
				DDGControlVar.mainTextSize = DDGControlVar.prevMainTextSize + diffPixel;
				mDuckDuckGoContainer.feedAdapter.notifyDataSetInvalidated();
				
				DDGControlVar.recentTextSize = DDGControlVar.prevRecentTextSize + diffPixel;
				mDuckDuckGoContainer.historyAdapter.notifyDataSetInvalidated();
				mDuckDuckGoContainer.recentSearchAdapter.notifyDataSetInvalidated();
				
				DDGControlVar.ptrHeaderSize = DDGControlVar.prevPtrHeaderSize + diff;
				DDGControlVar.ptrSubHeaderSize = DDGControlVar.prevPtrSubHeaderSize + diff;
				
				// adjust Pull-to-Refresh
				mPullRefreshFeedView.setHeaderTextSize(DDGControlVar.ptrHeaderSize);
				mPullRefreshFeedView.setHeaderSubTextSize(DDGControlVar.ptrSubHeaderSize);
				
				// set Loading... font
				mPullRefreshFeedView.setLoadingTextSize(DDGControlVar.ptrHeaderSize);
				mPullRefreshFeedView.setLoadingSubTextSize(DDGControlVar.ptrSubHeaderSize);
				
				DDGControlVar.webViewTextSize = DDGControlVar.prevWebViewTextSize + diff;
				mainWebView.getSettings().setDefaultFontSize(DDGControlVar.webViewTextSize);
				
				DDGControlVar.leftTitleTextSize = DDGControlVar.prevLeftTitleTextSize + diffPixel;
				
				leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
//		    	leftRecentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		    	leftMenuView.invalidate();
			}
		});
        
        Button fontSizeApplyButton = (Button) contentView.findViewById(R.id.fontSizeApplyButton);
        fontSizeApplyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DDGControlVar.fontPrevProgress = DDGControlVar.fontProgress;
				fontSizeSeekBar.setExtraText(null);
				
				// save adjusted text size
				Editor editor = sharedPreferences.edit();
				editor.putInt("fontPrevProgress", DDGControlVar.fontPrevProgress);
				editor.putFloat("mainFontSize", DDGControlVar.mainTextSize);
				editor.putFloat("recentFontSize", DDGControlVar.recentTextSize);
				editor.putInt("webViewFontSize", DDGControlVar.webViewTextSize);
				editor.putInt("ptrHeaderTextSize", DDGControlVar.ptrHeaderSize);
				editor.putInt("ptrHeaderSubTextSize", DDGControlVar.ptrSubHeaderSize);
				editor.putFloat("leftTitleTextSize", DDGControlVar.leftTitleTextSize);
				editor.commit();
				
				DDGControlVar.prevMainTextSize = 0;
				DDGControlVar.prevRecentTextSize = 0;
				DDGControlVar.prevWebViewTextSize = -1;
				DDGControlVar.prevPtrHeaderSize = 0;
				DDGControlVar.prevPtrSubHeaderSize = 0;
				DDGControlVar.prevLeftTitleTextSize = 0;
				fontSizeLayout.setVisibility(View.GONE);
				fontSizeSeekBar.setProgress(DDGControlVar.fontPrevProgress);
			}
		});
        
        Button fontSizeCancelButton = (Button) contentView.findViewById(R.id.fontSizeCancelButton);
        fontSizeCancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelFontScaling();
			}
		});
		
        
        
        // show Home screen
        displayHomeScreen();
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
		mDuckDuckGoContainer.feedAdapter.unmark();
		DDGControlVar.hasUpdatedFeed = false;
		keepFeedUpdated();
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
		mainWebView.clearView();
		
		resetReadabilityState();
	}
	
	public void setSearchBarText(String text) {
		searchField.setFocusable(false);
		searchField.setFocusableInTouchMode(false);
		searchField.setText(text);            
		searchField.setFocusable(true);
		searchField.setFocusableInTouchMode(true);
	}
	
	private void resetScreenState() {
		mDuckDuckGoContainer.feedAdapter.scrolling = false;
		clearSearchBar();
		clearBrowserState();
	}
	
	private void cancelFontScaling() {
		fontSizeSeekBar.setExtraText(null);
		DDGControlVar.mainTextSize = DDGControlVar.prevMainTextSize;
		DDGControlVar.recentTextSize = DDGControlVar.prevRecentTextSize;
		DDGControlVar.webViewTextSize = DDGControlVar.prevWebViewTextSize;
		DDGControlVar.leftTitleTextSize = DDGControlVar.prevLeftTitleTextSize;
		mDuckDuckGoContainer.feedAdapter.notifyDataSetInvalidated();
		mDuckDuckGoContainer.historyAdapter.notifyDataSetInvalidated();
		mDuckDuckGoContainer.recentSearchAdapter.notifyDataSetInvalidated();
		
		mPullRefreshFeedView.setHeaderTextSize(DDGControlVar.prevPtrHeaderSize);
		mPullRefreshFeedView.setHeaderSubTextSize(DDGControlVar.prevPtrSubHeaderSize);
		
		// set Loading... font
		mPullRefreshFeedView.setLoadingTextSize(DDGControlVar.prevPtrHeaderSize);
		mPullRefreshFeedView.setLoadingSubTextSize(DDGControlVar.prevPtrSubHeaderSize);
		
		mainWebView.getSettings().setDefaultFontSize(DDGControlVar.webViewTextSize);
		DDGControlVar.prevMainTextSize = 0;
		DDGControlVar.prevRecentTextSize = 0;
		DDGControlVar.prevWebViewTextSize = -1;
		DDGControlVar.prevPtrHeaderSize = 0;
		DDGControlVar.prevPtrSubHeaderSize = 0;
		DDGControlVar.prevLeftTitleTextSize = 0;
		fontSizeLayout.setVisibility(View.GONE);
		fontSizeSeekBar.setProgress(DDGControlVar.fontPrevProgress);
		
		leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
//    	leftRecentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftMenuView.invalidate();
	}
	
	/**
	 * Displays given screen (stories, saved, settings etc.)
	 * 
	 * @param screenToDisplay Screen to display
	 * @param clean Whether screen state (searchbar, browser etc.) states will get cleaned
	 */
	private void displayScreen(SCREEN screenToDisplay, boolean clean) {
			if(clean) {
				resetScreenState();
			}
		
	        // control which screen is shown & configure related views
			
			if(DDGControlVar.prevMainTextSize != 0) {
				fontSizeLayout.setVisibility(View.VISIBLE);
			}
			
			switch(screenToDisplay) {
				case SCR_STORIES:
					displayNewsFeed();
					break;
				case SCR_RECENT_SEARCH:
					displayRecentSearch();
					break;
				case SCR_SAVED_FEED:
					displaySavedFeed();
					break;
				case SCR_SETTINGS:
					displaySettings();
					break;
				default:
					break;
			}
			
			if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH &&
					!screenToDisplay.equals(SCREEN.SCR_RECENT_SEARCH)){
				leftRecentTextView.setVisibility(View.VISIBLE);
	        	leftRecentView.setVisibility(View.VISIBLE);
			}
	        
			mDuckDuckGoContainer.prevScreen = mDuckDuckGoContainer.currentScreen;
	        mDuckDuckGoContainer.currentScreen = screenToDisplay;	        			
	}
	
	private void displayHomeScreen() {
		displayScreen(DDGControlVar.START_SCREEN, true);
        
		if(mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_SEARCH
				|| DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH || DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED_FEED) {
			// previous screen was a SERP
			showKeyboard(searchField);
		}
        mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_BROWSE;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// update feeds
		// https://app.asana.com/0/2891531242889/2858723303746
		DDGControlVar.hasUpdatedFeed = false;
		if(mDuckDuckGoContainer.feedAdapter != null) {
			mDuckDuckGoContainer.feedAdapter.scrolling = false;
		}
		
		// check autocomplete 
		if(!DDGControlVar.isAutocompleteActive) {
			searchField.setAdapter(null);
		}
		else {
	        searchField.setAdapter(mDuckDuckGoContainer.acAdapter);
		}
		
		if(DDGControlVar.includeAppsInSearch && !DDGControlVar.hasAppsIndexed) {
			// index installed apps
			new ScanAppsTask(getApplicationContext()).execute();
			DDGControlVar.hasAppsIndexed = true;
		}
		
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
			initDownloadManager();
		}
		
		// global search intent
        Intent intent = getIntent(); 
        
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			setSearchBarText(query);
			searchWebTerm(query);
		}
		else {
			// not executed on global search for quick response
			mDuckDuckGoContainer.sourceIconTask = new DownloadSourceIconTask(getApplicationContext(), DDGApplication.getImageCache());
			mDuckDuckGoContainer.sourceIconTask.execute();
		
			if(intent.getBooleanExtra("widget", false)) {
				viewFlipper.setDisplayedChild(DDGControlVar.START_SCREEN.getFlipOrder());
				showKeyboard(searchField);
			}
			else if(mDuckDuckGoContainer.webviewShowing){
				shareButton.setVisibility(View.VISIBLE);
				viewFlipper.setDisplayedChild(SCREEN.SCR_WEBVIEW.getFlipOrder());
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
		
		// XXX keep these for low memory conditions
		saveAppState(sharedPreferences);
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
		// close left nav if it's open
		if(viewPager.isLeftMenuOpen()){
			viewPager.setCurrentItem(SCREEN.SCR_STORIES.getFlipOrder());
		}
		else if (mDuckDuckGoContainer.webviewShowing) {
			if (mainWebView.canGoBack()) {
				
				// XXX  ****** Beware : ugly hack to avoid displaying a readability page twice ***** 
				WebBackForwardList history = mainWebView.copyBackForwardList();
				int currentIndex = history.getCurrentIndex();
				WebHistoryItem prevItem = history.getItemAtIndex(currentIndex-1);				
				
				if(prevItem.getOriginalUrl().equals(mDuckDuckGoContainer.lastFeedUrl)
						&& canDoReadability(currentFeedObject)) {									
					readableAction(currentFeedObject);
				}
				// **********************************************************************************
				else {
					mainWebView.goBack();
				}
			}
			else {
				displayScreen(mDuckDuckGoContainer.currentScreen, true);
			}
		}
		else if(mDuckDuckGoContainer.currentScreen == SCREEN.SCR_SETTINGS){
			// go back to where we left of
			displayScreen(mDuckDuckGoContainer.prevScreen, false);
		}
		else if(fontSizeLayout.getVisibility() != View.GONE) {
			cancelFontScaling();
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
	
	public void reloadAction() {
		mCleanSearchBar = false;
//		mainWebView.resumeView();
        mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int)Math.floor(mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight()/1.5));
		searchField.setCompoundDrawables(null, null, searchField.getText().toString().equals("") ? null : mDuckDuckGoContainer.stopDrawable, null);
		
		if(!mainWebView.isReadable)
			mainWebView.reload(); 
		else {
			new ReadableFeedTask(mReadableListener, currentFeedObject).execute();
		}
	}
	
	private void stopAction() {
		mCleanSearchBar = true;
//		clearBrowserState();
//		mainWebView.stopView();
		
    	searchField.setText("");
    	
//    	// This makes a little (X) to clear the search bar.
//    	mDuckDuckGoContainer.reloadDrawable.setBounds(0, 0, (int)Math.floor(mDuckDuckGoContainer.reloadDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(mDuckDuckGoContainer.reloadDrawable.getIntrinsicHeight()/1.5));
//        searchField.setCompoundDrawables(null, null, mDuckDuckGoContainer.reloadDrawable, null);
    	searchField.setCompoundDrawables(null, null, null, null);
    	   
    	searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
	}
	
	public void searchOrGoToUrl(String text) {
		searchOrGoToUrl(text, SESSIONTYPE.SESSION_BROWSE);
	}
	
	public void searchOrGoToUrl(String text, SESSIONTYPE sessionType) {
		
//		mainWebView.resumeView();
		
		hideKeyboard(mainWebView);
		clearBrowserState();
		savedState = false;
		
		mDuckDuckGoContainer.sessionType = sessionType;
		
		if(mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_FEED) {   
			showFeed(currentFeedObject);
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
		mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_SEARCH;
		
		// save recent query if "record history" is enabled
		if(PreferencesManager.getRecordHistory()){
				Log.v(TAG, "Search: " + term);		
				DDGApplication.getDB().insertRecentSearch(term);
				syncHistoryAdapters();
		}
		
		if(DDGControlVar.alwaysUseExternalBrowser) {
			searchExternal(term);
        	return;
		}
		
		displayWebView();
		
		if(!savedState){
			resetReadabilityState();
			
			if(DDGControlVar.regionString == "wt-wt"){	// default
				mainWebView.loadUrl(DDGConstants.SEARCH_URL + URLEncoder.encode(term));
			}
			else {
				mainWebView.loadUrl(DDGConstants.SEARCH_URL + URLEncoder.encode(term) + "&kl=" + URLEncoder.encode(DDGControlVar.regionString));
			}
		}		
	}
	
	public void clearRecentSearch() {
		syncHistoryAdapters();
	}
	
	public void showHistoryObject(HistoryObject object) {
		if(object.getType().equals("R")) {
			searchWebTerm(object.getData());
		}
		else if(object.getType().equals("F")) {
			DDGApplication.getDB().insertHistoryObject(object);
			syncHistoryAdapters();
			String feedId = object.getFeedId();
			if(feedId != null) {
				feedItemSelected(feedId);
			}
		}
		else {
			DDGApplication.getDB().insertHistoryObject(object);
			syncHistoryAdapters();
			showWebUrl(object.getUrl());
		}		
	}
	
	public void showWebUrl(String url) {
		if(DDGControlVar.alwaysUseExternalBrowser) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        	startActivity(browserIntent);
        	return;
		}
		
		if(mDuckDuckGoContainer.currentScreen != SCREEN.SCR_WEBVIEW)
			displayWebView();
		
		if(!savedState) {
			resetReadabilityState();
			
//			mainWebView.loadUrl(url, DDGNetworkConstants.extraHeaders);
			mainWebView.loadUrl(url);
		}
	}
	
	public void showFeed(FeedObject feedObject) {
		if(!savedState) {
			if(!DDGControlVar.alwaysUseExternalBrowser
					&& PreferencesManager.getReadable()
					&& !mDuckDuckGoContainer.forceOriginalFormat
					&& feedObject.getArticleUrl().length() != 0) {
				
				if(mDuckDuckGoContainer.currentScreen != SCREEN.SCR_WEBVIEW)
					displayWebView();
				
				mainWebView.isReadable = true;
				new ReadableFeedTask(mReadableListener, feedObject).execute();
			}
			else {
				showWebUrl(feedObject.getUrl());
			}
		}
	}
	
	private void resetReadabilityState() {
		mainWebView.isReadable = false;
		mDuckDuckGoContainer.forceOriginalFormat = false;
	}
	
	private boolean canDoReadability(FeedObject feedObject) {
		return PreferencesManager.getReadable() 
				&& !mDuckDuckGoContainer.forceOriginalFormat
				&& feedObject.getArticleUrl().length() != 0;
	}

	public void onFeedRetrieved(List<FeedObject> feed, boolean fromCache) {
		
		if(!fromCache) {
			synchronized(mDuckDuckGoContainer.feedAdapter) {
				DDGApplication.getImageDownloader().clearAllDownloads();				
				mDuckDuckGoContainer.feedAdapter.clear();
			}
		}
		
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
			
//			if(android.os.Build.VERSION.SDK_INT >= 11) {
//				feedView.smoothScrollToPositionFromTop(nPos, m_yOffset);
//			}
//			else {
				feedView.setSelectionFromTop(nPos,m_yOffset);
//			}
			
			// mark for blink animation (as a visual cue after list update)
			mDuckDuckGoContainer.feedAdapter.mark(m_objectId);
		}
		else {
			// scroll triggers pre-caching for source filtering case
			// this is for the static, no-scroll case
			feedView.enableAfterRender();
		}
	}
	
	public void onFeedRetrievalFailed() {

		//If the mainFeedTask is null, we are currently paused
		//Otherwise, we can try again
		if (mDuckDuckGoContainer.currentScreen != SCREEN.SCR_SAVED_FEED && mDuckDuckGoContainer.mainFeedTask != null) {
			
			// Create the AlertDialog
			AlertDialog failDialog = cacheDialogBuilder.create();
			failDialog.show();
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
					if(viewPager.isLeftMenuOpen()){
						viewPager.setCurrentItem(1);
					}
					
					if(preference.getKey().equals("mainFontSizePref")) {
						DDGControlVar.prevMainTextSize = DDGControlVar.mainTextSize;
						DDGControlVar.prevRecentTextSize = DDGControlVar.recentTextSize;
						DDGControlVar.prevWebViewTextSize = DDGControlVar.webViewTextSize;
						DDGControlVar.prevPtrHeaderSize = DDGControlVar.ptrHeaderSize;
						DDGControlVar.prevPtrSubHeaderSize = DDGControlVar.ptrSubHeaderSize;
						DDGControlVar.prevLeftTitleTextSize = DDGControlVar.leftTitleTextSize;
						displayHomeScreen();
					}
					else if(preference.getKey().equals("recordHistoryPref")){
						displayRecordHistoryDisabled();
					}
					
					return false;
				}
			});
            mWorkFragment.setCustomPreferenceChangeListener(new PreferenceChangeListener() {
            	@Override
            	public void onPreferenceChange(String key) {
            		if(key.equals("themePref")){
            			String themeName = PreferencesManager.getThemeName();
            			int themeId = getResources().getIdentifier(themeName, "style", getPackageName());
            			if(themeId != 0) {
            				Intent intent = new Intent(getApplicationContext(), DuckDuckGo.class);
            				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            				startActivity(intent);
            			}
            		}
            		else if(key.equals("turnOffAutocompletePref")) {
	        			// check autocomplete 
	        			if(!DDGControlVar.isAutocompleteActive) {
	        				searchField.setAdapter(null);
	        			}
	        			else {
	        		        searchField.setAdapter(mDuckDuckGoContainer.acAdapter);
	        			}
            		}
            		else if(key.equals("appSearchPref")) {
            			if(DDGControlVar.includeAppsInSearch && !DDGControlVar.hasAppsIndexed) {
            				// index installed apps
            				new ScanAppsTask(getApplicationContext()).execute();
            				DDGControlVar.hasAppsIndexed = true;
            			}
            		}
            	}
            });
            fm.beginTransaction().replace(R.id.prefFragment,
                    mWorkFragment).commit();  
        }
        
        makePreferencesVisible();
	}
	
	private void clearLeftSelect() {
		leftHomeTextView.setSelected(false);
		leftSavedTextView.setSelected(false);
		leftSettingsTextView.setSelected(false);
		leftStoriesTextView.setSelected(false);
	}
	
	/**
	 * main method that triggers display of Preferences screen or fragment
	 */
	private void displaySettings() {
		if(!((mDuckDuckGoContainer.currentScreen == SCREEN.SCR_SETTINGS))){					
			if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
		        Intent intent = new Intent(getBaseContext(), Preferences.class);
		        startActivityForResult(intent, PREFERENCES_RESULT);
			}
			else {
				showPrefFragment();
			}
		
		}
	}
	
	
	/** 
	 * change button visibility in left-side navigation menu
	 * according to screen
	 */
	private void changeLeftMenuVisibility(SCREEN screen) {		
		// stories button
		if(DDGControlVar.START_SCREEN != SCREEN.SCR_STORIES) {
			leftStoriesButtonLayout.setVisibility(View.VISIBLE);
		}
		else {
	    	leftStoriesButtonLayout.setVisibility(View.GONE);
		}
		
		// saved button
		if(DDGControlVar.START_SCREEN != SCREEN.SCR_SAVED_FEED) {
			leftSavedButtonLayout.setVisibility(View.VISIBLE);
		}
		else {
			leftSavedButtonLayout.setVisibility(View.GONE);
		}
    	
		// recent search button
    	if(DDGControlVar.START_SCREEN != SCREEN.SCR_RECENT_SEARCH) {
    		leftRecentTextView.setVisibility(View.VISIBLE);
        	leftRecentView.setVisibility(View.VISIBLE);
    	}
    	else {
    		leftRecentTextView.setVisibility(View.GONE);
        	leftRecentView.setVisibility(View.GONE);
    	}

	}
	
	
	/**
	 * helper method to control visibility states etc. of other views in DuckDuckGo activity
	 */
	public void makePreferencesVisible(){		
		viewFlipper.setDisplayedChild(SCREEN.SCR_SETTINGS.getFlipOrder());
		shareButton.setVisibility(View.GONE);
				
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
		viewFlipper.setDisplayedChild(SCREEN.SCR_STORIES.getFlipOrder());
		shareButton.setVisibility(View.GONE);
    	keepFeedUpdated();
    	mDuckDuckGoContainer.webviewShowing = false;
	}
	
	public void displayNewsFeed(){
		resetScreenState();
		
    	// show recent queries on slide-out menu
//    	lMainAdapter.remove(getString(R.string.LeftRecentQueries));
    	
		// left side menu visibility changes
		changeLeftMenuVisibility(SCREEN.SCR_STORIES);
    	
    	// adjust "not recording" indicator
    	displayRecordHistoryDisabled();
    	
    	// ensures feed refresh every time user switches to Stories screen
    	DDGControlVar.hasUpdatedFeed = false;
		
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
		resetScreenState();
		
		// left side menu visibility changes
		changeLeftMenuVisibility(SCREEN.SCR_SAVED_FEED);
    	
    	viewFlipper.setDisplayedChild(SCREEN.SCR_SAVED_FEED.getFlipOrder());
    	
    	mDuckDuckGoContainer.webviewShowing = false;
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
		resetScreenState(); 
		
		// left side menu visibility changes
		changeLeftMenuVisibility(SCREEN.SCR_RECENT_SEARCH);
		
    	// main view visibility changes
		shareButton.setVisibility(View.GONE);
		viewFlipper.setDisplayedChild(SCREEN.SCR_RECENT_SEARCH.getFlipOrder());
    	mDuckDuckGoContainer.webviewShowing = false;
		
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
			shareButton.setVisibility(View.VISIBLE);
			viewFlipper.setDisplayedChild(SCREEN.SCR_WEBVIEW.getFlipOrder());
			
			mDuckDuckGoContainer.webviewShowing = true;
		}
	}
	
	public void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public void showKeyboard(View view) {
		view.requestFocus();
		
		 getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//		 imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		 imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		 
	}
	
	public void onClick(View view) {
		if (view.equals(homeSettingsButton)) {			
			handleHomeSettingsButtonClick();
		}
		else if (view.equals(shareButton)) {			
			handleShareButtonClick();
		}
		else if(view.equals(leftHomeTextView)){
			handleLeftHomeTextViewClick();
		}
		else if(view.equals(leftStoriesTextView)){
			viewPager.switchPage();		
			displayScreen(SCREEN.SCR_STORIES, false);
		}
		else if(view.equals(leftSavedTextView)){
			viewPager.switchPage();		
			displayScreen(SCREEN.SCR_SAVED_FEED, false);
		}
		else if(view.equals(leftSettingsTextView) || view.equals(leftRecentHeaderView)){
			viewPager.switchPage();		
			displayScreen(SCREEN.SCR_SETTINGS, false);
		}
	}

	private void handleLeftHomeTextViewClick() {
		viewPager.switchPage();
					
		if (mDuckDuckGoContainer.webviewShowing) {

			//We are going home!
			mainWebView.clearHistory();
			mainWebView.clearView();
			clearSearchBar();
			mDuckDuckGoContainer.webviewShowing = false;					
		}
		
		displayHomeScreen();
	}

	private void handleHomeSettingsButtonClick() {
		hideKeyboard(searchField);
		
		if(DDGControlVar.homeScreenShowing){
			viewPager.switchPage();
		}
		else {
			// going home
			displayHomeScreen();
		}
	}

	private void handleShareButtonClick() {
			hideKeyboard(searchField);
						
			boolean isPageSaved;
			
			final String alertTitle;
			final String pageTitle;
			final String pageUrl;
			final String pageType;
			
			// XXX should make Page Options button disabled if the page is not loaded yet
			// url = null case
			String webViewUrl = mainWebView.getOriginalUrl();
			if(webViewUrl == null)
				webViewUrl = "";
			
			final String query = DDGUtils.getQueryIfSerp(webViewUrl);
			final PageMenuContextAdapter contextAdapter;
			
			// direct displaying after feed item is clicked
			// the rest will arrive as SESSION_BROWSE
			// so we should save this feed item with target redirected URL
			if(isStorySessionOrStoryUrl()
			  ) {
				alertTitle = getString(R.string.StoryOptionsTitle);
				pageUrl = currentFeedObject.getUrl();				
				isPageSaved = DDGApplication.getDB().isSaved(currentFeedObject.getId());
				
				if(currentFeedObject.hasPossibleReadability())
					pageType = PageTypes.StoryWithReadability;
				else
					pageType = PageTypes.StoryWithoutReadability;
				
				mDuckDuckGoContainer.lastFeedUrl = webViewUrl;
				contextAdapter  = new WebViewStoryMenuAdapter(DuckDuckGo.this, android.R.layout.select_dialog_item, android.R.id.text1,
						currentFeedObject, isPageSaved, mainWebView.isReadable);
				
			}						
			else if(query != null) {
				alertTitle = getString(R.string.SearchOptionsTitle);
				pageUrl = webViewUrl;
				pageType = PageTypes.Query;
				isPageSaved = DDGApplication.getDB().isSavedSearch(query);
				contextAdapter  = new WebViewQueryMenuAdapter(DuckDuckGo.this, android.R.layout.select_dialog_item, android.R.id.text1,
						query, isPageSaved);
			}
			else {
				// in case it's not a query or feed item
				alertTitle = getString(R.string.PageOptionsTitle);
				pageUrl = webViewUrl;
				pageType = PageTypes.WebPage;
				isPageSaved = false;
				contextAdapter = new WebViewWebPageMenuAdapter(DuckDuckGo.this, android.R.layout.select_dialog_item, android.R.id.text1, pageUrl);
			}
			
			
			AlertDialog.Builder alertDialogBuilder =new AlertDialog.Builder(DuckDuckGo.this);
			alertDialogBuilder.setTitle(alertTitle);
			
			alertDialogBuilder.setAdapter(contextAdapter, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					Item clickedItem = ((Item) contextAdapter.getItem(item));
					clickedItem.ActionToExecute.Execute();
				}

				
			});
			alertDialogBuilder.show();
	}
	
	public void launchReadableFeedTask(FeedObject feedObject) {
		new ReadableFeedTask(mReadableListener, feedObject).execute();
	}

	private boolean isStorySessionOrStoryUrl() {
		return mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_FEED
				|| 
				( mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_BROWSE 
					&& mDuckDuckGoContainer.lastFeedUrl.equals(mainWebView.getOriginalUrl()) 
				);
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
	public Object onRetainCustomNonConfigurationInstance() {
	       // return page container, holding all non-view data
	       return mDuckDuckGoContainer;
	}
	
	private void saveAppState(SharedPreferences prefs) {
		Editor editor = prefs.edit();
		editor.putBoolean("homeScreenShowing", DDGControlVar.homeScreenShowing);
		editor.putBoolean("webviewShowing", mDuckDuckGoContainer.webviewShowing);
		editor.putInt("currentScreen", mDuckDuckGoContainer.currentScreen.ordinal());
		editor.putInt("prevScreen", mDuckDuckGoContainer.prevScreen.ordinal());
		editor.putBoolean("allowInHistory", mDuckDuckGoContainer.allowInHistory);
		editor.putInt("sessionType", mDuckDuckGoContainer.sessionType.ordinal());
		if(currentFeedObject != null) {
			editor.putString("currentFeedObjectId", currentFeedObject.getId());
		}
		editor.commit();
	}
	
	private void saveAppState(Bundle bundle) {
		bundle.putBoolean("homeScreenShowing", DDGControlVar.homeScreenShowing);
		bundle.putBoolean("webviewShowing", mDuckDuckGoContainer.webviewShowing);
		bundle.putInt("currentScreen", mDuckDuckGoContainer.currentScreen.ordinal());
		bundle.putInt("prevScreen", mDuckDuckGoContainer.prevScreen.ordinal());
		bundle.putBoolean("allowInHistory", mDuckDuckGoContainer.allowInHistory);
		bundle.putInt("sessionType", mDuckDuckGoContainer.sessionType.ordinal());
		if(currentFeedObject != null) {
			bundle.putString("currentFeedObjectId", currentFeedObject.getId());
		}
	}
	
	private void recoverAppState(Object state) {
		Bundle bundle = null; 
		SharedPreferences prefs = null; 
		
		String feedId = null; 
		
		if(state instanceof Bundle) {
			bundle = (Bundle) state;
			
			DDGControlVar.homeScreenShowing = bundle.getBoolean("homeScreenShowing");
			mDuckDuckGoContainer.webviewShowing = bundle.getBoolean("webviewShowing");
			mDuckDuckGoContainer.currentScreen = SCREEN.getByCode(bundle.getInt("currentScreen"));
			mDuckDuckGoContainer.prevScreen = SCREEN.getByCode(bundle.getInt("prevScreen"));
			mDuckDuckGoContainer.allowInHistory = bundle.getBoolean("allowInHistory");
			mDuckDuckGoContainer.sessionType = SESSIONTYPE.getByCode(bundle.getInt("sessionType"));
			feedId = bundle.getString("currentFeedObjectId");
			
			clearLeftSelect();
			markLeftSelect(mDuckDuckGoContainer.currentScreen);
			
			// Restore the state of the WebView
	    	if(mDuckDuckGoContainer.webviewShowing) {
	    		mainWebView.restoreState(bundle);
	    	}
		}
		else if(state instanceof SharedPreferences) {
			prefs = (SharedPreferences) state;
			
			DDGControlVar.homeScreenShowing = prefs.getBoolean("homeScreenShowing", false);
			mDuckDuckGoContainer.webviewShowing = prefs.getBoolean("webviewShowing", false);
			mDuckDuckGoContainer.currentScreen = SCREEN.getByCode(prefs.getInt("currentScreen", SCREEN.SCR_STORIES.getCode()));
			mDuckDuckGoContainer.prevScreen = SCREEN.getByCode(prefs.getInt("prevScreen", SCREEN.SCR_STORIES.getCode()));
			mDuckDuckGoContainer.allowInHistory = prefs.getBoolean("allowInHistory", false);
			mDuckDuckGoContainer.sessionType = SESSIONTYPE.getByCode(prefs.getInt("sessionType", SESSIONTYPE.SESSION_BROWSE.getCode()));
			feedId = prefs.getString("currentFeedObjectId", null);
			
			clearLeftSelect();
			markLeftSelect(mDuckDuckGoContainer.currentScreen);			
		}
		
		Log.v(TAG, "feedId: " + feedId);
		
		if(feedId != null && feedId.length() != 0) {
			FeedObject feedObject = DDGApplication.getDB().selectFeedById(feedId);
			if(feedObject != null)
				currentFeedObject = feedObject;
			
//			if(mDuckDuckGoContainer.webviewShowing && mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_FEED) {
//				showFeed(currentFeedObject);
//			}
			
		}			
		
		if(mDuckDuckGoContainer.webviewShowing)
			return;
		
		// arbitrary choice to not display Settings on comeback
    	if(mDuckDuckGoContainer.currentScreen == SCREEN.SCR_SETTINGS) {
    		displayHomeScreen();
    	}
    	else {
			displayScreen(mDuckDuckGoContainer.currentScreen, true);
    	}
	}
    
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		saveAppState(outState);					
		super.onSaveInstanceState(outState);

		// Save the state of the WebView
		mainWebView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		
		recoverAppState(savedInstanceState);
		
	}
	
	private void markLeftSelect(SCREEN current) {
		if(DDGControlVar.START_SCREEN == current) {
			leftHomeTextView.setSelected(true);
			
			if(mDuckDuckGoContainer.webviewShowing){
	    		homeSettingsButton.setImageResource(R.drawable.home_button);
			}
			else {
	    		homeSettingsButton.setImageResource(R.drawable.menu_button);
			}
		}
		else {
    		homeSettingsButton.setImageResource(R.drawable.home_button);
			switch(current) {
				case SCR_STORIES:
					leftStoriesTextView.setSelected(true);
					break;
				case SCR_SAVED_FEED:
					leftSavedTextView.setSelected(true);
					break;
				case SCR_RECENT_SEARCH:
					leftRecentTextView.setSelected(true);
					break;
				case SCR_SETTINGS:
					leftSettingsTextView.setSelected(true);
					break;
			}
		}
	}
	
	/**
	 * Refresh feed if it's not marked as updated
	 */
	@SuppressLint("NewApi")
	private void keepFeedUpdated()
	{
		if (!DDGControlVar.hasUpdatedFeed) {
			if(PreferencesManager.containsSourcesetSize() && PreferencesManager.getSourcesetSize() == 0) {
				// respect user choice of empty source list: show nothing
				onFeedRetrieved(new ArrayList<FeedObject>(), true);
			}
			else {
				// cache
				MainFeedTask cacheTask = new MainFeedTask(DuckDuckGo.this, this, true);
			
				// for HTTP request
				mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(DuckDuckGo.this, this);
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					cacheTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					mDuckDuckGoContainer.mainFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
				else {
					cacheTask.execute();
					mDuckDuckGoContainer.mainFeedTask.execute();
				}
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
	    	viewPager.switchPage();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(DDGUtils.feedItemWidth, DDGUtils.feedItemHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable!=null) 
            bgDrawable.draw(canvas);
        else 
            canvas.drawColor(Color.TRANSPARENT);
        view.draw(canvas);
        return returnedBitmap;
    }
	
	/**
	 * Displays "not recording" indicator in left-menu if Save Searches option is disabled  
	 */
	public void displayRecordHistoryDisabled() {
		if(PreferencesManager.getRecordHistory()) {
			// user changed the setting, got it
    		if(leftRecentView.findViewById(leftRecentHeaderView.getId()) != null) {
        		leftRecentView.removeHeaderView(leftRecentHeaderView);
    		}
    	}
    	else {
    		if(leftRecentView.findViewById(leftRecentHeaderView.getId()) == null) {
    			leftRecentView.setAdapter(null);
    			leftRecentView.addHeaderView(leftRecentHeaderView);
    			leftRecentView.setAdapter(mDuckDuckGoContainer.historyAdapter);
    		}
    	}
	}
	
	/**
	 * set searchbar text, close left-menu (if open), show keyboard and focus on searchbar
	 * pre-search actions combined
	 */
	public void preSearch(String query) {
		setSearchBarText(query);
		showKeyboard(searchField);
		viewPager.setCurrentItem(1);
	}

	
	/**
	 * Step 2: Setup TabHost
	 */
	private void initialiseTabHost(Bundle args) {
		savedTabHost = (TabHostExt) contentView.findViewById(android.R.id.tabhost);
		savedTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		        
		addTab(savedTabHost, getResources().getString(R.string.SavedSearches), SavedResultTabFragment.class);
		addTab(savedTabHost, getResources().getString(R.string.SavedStories), SavedFeedTabFragment.class);
	}
	
	private void addTab(TabHostExt tabHost, String label, Class<?> intentClass) {
		Intent intent = new Intent(this, intentClass);
		TabHostExt.TabSpec spec = (TabHostExt.TabSpec) tabHost.newTabSpec(label);

		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, tabHost.getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(label);

		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec, intentClass, null);
	}

}
