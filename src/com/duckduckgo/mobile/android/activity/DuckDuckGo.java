package com.duckduckgo.mobile.android.activity;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebView.HitTestResult;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.adapters.MultiHistoryAdapter;
import com.duckduckgo.mobile.android.adapters.SavedFeedCursorAdapter;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.dialogs.FeedRequestFailureDialogBuilder;
import com.duckduckgo.mobile.android.dialogs.NewSourcesDialogBuilder;
import com.duckduckgo.mobile.android.dialogs.OpenInExternalDialogBuilder;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistorySearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistoryStoryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.MainFeedMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewQueryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewStoryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.WebViewWebPageMenuDialog;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.ContentDownloader;
import com.duckduckgo.mobile.android.listener.FeedListener;
import com.duckduckgo.mobile.android.listener.PreferenceChangeListener;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.SuggestObject;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.tabhost.TabHostExt;
import com.duckduckgo.mobile.android.tasks.CacheFeedTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.tasks.ReadableFeedTask;
import com.duckduckgo.mobile.android.tasks.ScanAppsTask;
import com.duckduckgo.mobile.android.util.AppStateManager;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.DDGViewPager;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.ReadArticlesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.SuggestType;
import com.duckduckgo.mobile.android.views.HistoryListView;
import com.duckduckgo.mobile.android.views.HistoryListView.OnHistoryItemLongClickListener;
import com.duckduckgo.mobile.android.views.HistoryListView.OnHistoryItemSelectedListener;
import com.duckduckgo.mobile.android.views.MainFeedListView;
import com.duckduckgo.mobile.android.views.MainFeedListView.OnMainFeedItemLongClickListener;
import com.duckduckgo.mobile.android.views.MainFeedListView.OnMainFeedItemSelectedListener;
import com.duckduckgo.mobile.android.views.SeekBarHint;
import com.duckduckgo.mobile.android.views.WelcomeScreenView;
import com.duckduckgo.mobile.android.views.autocomplete.BackButtonPressedEventListener;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;
import com.duckduckgo.mobile.android.views.webview.DDGWebChromeClient;
import com.duckduckgo.mobile.android.views.webview.DDGWebView;
import com.duckduckgo.mobile.android.views.webview.DDGWebViewClient;
import com.duckduckgo.mobile.android.widgets.BangButtonExplanationPopup;
import com.duckduckgo.mobile.android.widgets.SafeViewFlipper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshMainFeedListView;

public class DuckDuckGo extends FragmentActivity implements FeedListener, OnClickListener {
	protected final String TAG = "DuckDuckGo";
	
	public DuckDuckGoContainer mDuckDuckGoContainer;
	
	// keeps default User-Agent for WebView
	public String mWebViewDefaultUA = null;
		
	private DDGAutoCompleteTextView searchField = null;
	private MainFeedListView feedView = null;
	private HistoryListView leftRecentView = null;
	
	private PullToRefreshMainFeedListView mPullRefreshFeedView = null;
	
	private DDGViewPager viewPager;
	private View contentView = null;
	private View leftMenuView = null;
	
	private SafeViewFlipper viewFlipper = null;
	
	private HistoryListView recentSearchView = null;
	
	public DDGWebView mainWebView = null;
	private ImageButton homeSettingsButton = null;
	private ImageButton bangButton = null;
	private ImageButton shareButton = null;
	
	private TextView leftHomeTextView = null;
	private TextView leftStoriesTextView = null;
	private TextView leftSavedTextView = null;
	private TextView leftSettingsTextView = null;
	
	private LinearLayout leftHomeButtonLayout = null;
	private LinearLayout leftStoriesButtonLayout = null;
	private LinearLayout leftSavedButtonLayout = null;
	private LinearLayout leftSettingsButtonLayout = null;
	
	// font scaling
	private LinearLayout fontSizeLayout = null;
	
	// welcome screen
	private WelcomeScreenView welcomeScreenLayout = null;
	OnClickListener welcomeCloseListener = null;
		
	private SharedPreferences sharedPreferences;
		
	public boolean savedState = false;
		
	private final int PREFERENCES_RESULT = 0;
	
	public FeedObject currentFeedObject = null;

	// for keeping filter source at same position
	String m_objectId = null;
	int m_itemHeight;
	int m_yOffset;
	
	boolean mScrollCancelLock = false;
	
	// keep prev progress in font seek bar, to make incremental changes available
	SeekBarHint fontSizeSeekBar;
	
	public boolean mCleanSearchBar = false;
	
	private TabHostExt savedTabHost = null;
	
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
		
		if(ReadArticlesManager.addReadArticle(feedObject)){
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
    
    private OnMainFeedItemLongClickListener mFeedItemLongClickListener = new OnMainFeedItemLongClickListener() {
		public void onMainFeedItemLongClick(FeedObject feedObject) {
			new MainFeedMenuDialog(DuckDuckGo.this, feedObject).show();
		}
    };


    private OnHistoryItemLongClickListener mHistoryLongClickListener = new OnHistoryItemLongClickListener() {
    	@Override
    	public void onHistoryItemLongClick(HistoryObject historyObject) {
            if(historyObject.isFeedObject()) {
                new HistoryStoryMenuDialog(DuckDuckGo.this, historyObject).show();
            }
            else{
                new HistorySearchMenuDialog(DuckDuckGo.this, historyObject).show();
            }
    	}
    };
    
    private FeedListener mReadableListener = new FeedListener() {
		
		@Override
		public void onFeedRetrieved(List<FeedObject> feed, boolean fromCache) {
			if(feed.size() != 0) {
				currentFeedObject = feed.get(0);
				mDuckDuckGoContainer.lastFeedUrl = currentFeedObject.getUrl();
				mainWebView.readableAction(currentFeedObject);
			}
		}
		
		@Override
		public void onFeedRetrievalFailed() {
			// TODO Auto-generated method stub
			
		}
	};

	private ContentDownloader contentDownloader;

	private boolean shouldShowBangButtonExplanation;

	private BangButtonExplanationPopup bangButtonExplanationPopup;
    
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
    
    public void syncAdapters() {
    	mDuckDuckGoContainer.historyAdapter.sync();
		mDuckDuckGoContainer.savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
		mDuckDuckGoContainer.savedSearchAdapter.notifyDataSetChanged();
		mDuckDuckGoContainer.savedFeedAdapter.changeCursor(DDGApplication.getDB().getCursorStoryFeed());
		mDuckDuckGoContainer.savedFeedAdapter.notifyDataSetChanged();
    }
    
    /**
     * Adds welcome screen on top of content view
     * Also disables dispatching of touch events from viewPager to children views
     */
    private void addWelcomeScreen() {
    	viewPager.setDispatchTouch(false);
    	
    	if(!getResources().getBoolean(R.bool.welcomeScreen_allowLandscape)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
    	
    	// add welcome screen
        welcomeScreenLayout = new WelcomeScreenView(this);
        FrameLayout rootLayout = (FrameLayout)findViewById(android.R.id.content);
        rootLayout.addView(welcomeScreenLayout);
    	welcomeScreenLayout.setOnCloseListener(new OnClickListener() {
			@Override
			public void onClick(View v) {		
				removeWelcomeScreen();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
		});
    }
    
    /**
     * Removes welcome screen from content view
     * Also enables dispatching of touch events from viewPager
     */
    private void removeWelcomeScreen() {
    	welcomeScreenLayout.setVisibility(View.GONE);	
		viewPager.setDispatchTouch(true);					
		PreferencesManager.setWelcomeShown();
    	// remove welcome screen
		FrameLayout rootLayout = (FrameLayout)findViewById(android.R.id.content);
		rootLayout.removeView(welcomeScreenLayout);
		welcomeScreenLayout = null;
    }
    
    private void showBangButton(boolean visible){
    	homeSettingsButton.setVisibility(visible ? View.GONE: View.VISIBLE);
		bangButton.setVisibility(visible ? View.VISIBLE: View.GONE);
		if(shouldShowBangButtonExplanation && visible && welcomeScreenLayout == null){
			bangButtonExplanationPopup = BangButtonExplanationPopup.showPopup(DuckDuckGo.this, bangButton);
			shouldShowBangButtonExplanation = false;
		}
		if(!visible){
			if(bangButtonExplanationPopup!=null){
				bangButtonExplanationPopup.dismiss();
			}
		}
    }
				
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        showNewSourcesDialog();

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
        
		mDuckDuckGoContainer = (DuckDuckGoContainer) getLastCustomNonConfigurationInstance();
    	if(mDuckDuckGoContainer == null){
    		mDuckDuckGoContainer = new DuckDuckGoContainer();
    		
            mDuckDuckGoContainer.pageAdapter = new DDGPagerAdapter(this);
            
            mDuckDuckGoContainer.webviewShowing = false;
    		
    		mDuckDuckGoContainer.stopDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.stop);
//    		mDuckDuckGoContainer.reloadDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.reload);
    		mDuckDuckGoContainer.progressDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.page_progress);
    		mDuckDuckGoContainer.searchFieldDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.searchfield);
    		mDuckDuckGoContainer.searchFieldDrawable.setAlpha(150);
    		
    		mDuckDuckGoContainer.historyAdapter = new MultiHistoryAdapter(DuckDuckGo.this, this);
    		
    		SourceClickListener sourceClickListener = new SourceClickListener();			
    		mDuckDuckGoContainer.feedAdapter = new MainFeedAdapter(this, sourceClickListener);
    		
    		mDuckDuckGoContainer.mainFeedTask = null;
    		
    		mDuckDuckGoContainer.acAdapter = new AutoCompleteResultsAdapter(this);
    		
    		mDuckDuckGoContainer.savedSearchAdapter = new SavedResultCursorAdapter(DuckDuckGo.this, DuckDuckGo.this, DDGApplication.getDB().getCursorSavedSearch());    	
    		mDuckDuckGoContainer.savedFeedAdapter = new SavedFeedCursorAdapter(DuckDuckGo.this, DuckDuckGo.this, DDGApplication.getDB().getCursorStoryFeed());
    		
    	}
    	
    	// always refresh on start
    	DDGControlVar.hasUpdatedFeed = false;    	
    	
        viewPager = (DDGViewPager) findViewById(R.id.mainpager);
        viewPager.setAdapter(mDuckDuckGoContainer.pageAdapter);
        viewPager.setCurrentItem(1);
        
        
    	if(!PreferencesManager.isWelcomeShown()) {            
            addWelcomeScreen();
            shouldShowBangButtonExplanation = true;
    	}
        
        leftMenuView = mDuckDuckGoContainer.pageAdapter.getPageView(0);
        contentView = mDuckDuckGoContainer.pageAdapter.getPageView(1);    
        
		// XXX Step 2: Setup TabHost
		initialiseTabHost();
		if (savedInstanceState != null) {
            savedTabHost.setCurrentTabByTag(savedInstanceState.getString("simple")); //set the tab as per the saved state
		}
        
        viewFlipper = (SafeViewFlipper) contentView.findViewById(R.id.ViewFlipperMain);
    	    	
    	leftHomeTextView = (TextView) leftMenuView.findViewById(R.id.LeftHomeTextView);
    	leftStoriesTextView = (TextView) leftMenuView.findViewById(R.id.LeftStoriesTextView);
    	leftSavedTextView = (TextView) leftMenuView.findViewById(R.id.LeftSavedTextView);
    	leftSettingsTextView = (TextView) leftMenuView.findViewById(R.id.LeftSettingsTextView);
    	
    	leftHomeTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftStoriesTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftSavedTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftSettingsTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);    	
    	
    	
    	TypedValue tmpTypedValue = new TypedValue(); 
    	getTheme().resolveAttribute(R.attr.leftButtonTextSize, tmpTypedValue, true);
    	// XXX getDimension returns in PIXELS !
    	float defLeftTitleTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.leftTitleTextSize = PreferencesManager.getLeftTitleTextSize(defLeftTitleTextSize);
    	
    	leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
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
		
		leftRecentView.setDivider(null);
    	leftRecentView.setAdapter(mDuckDuckGoContainer.historyAdapter);
    	
    	// "Save Recents" not enabled notification click listener
    	leftRecentView.setOnHeaderClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewPager.switchPage();		
				displayScreen(SCREEN.SCR_SETTINGS, false);
			}
		});
    	leftRecentView.setOnHistoryItemSelectedListener(new OnHistoryItemSelectedListener() {
			
			public void onHistoryItemSelected(HistoryObject historyObject) {
				if(viewPager.isLeftMenuOpen()){
					viewPager.setCurrentItem(1);
				}
				
				if(historyObject != null){
					showHistoryObject(historyObject);
				}				
			}
		});
    	leftRecentView.setOnHistoryItemLongClickListener(mHistoryLongClickListener);
        
        homeSettingsButton = (ImageButton) contentView.findViewById(R.id.settingsButton);
        homeSettingsButton.setOnClickListener(this);
        bangButton = (ImageButton)contentView.findViewById(R.id.bangButton);
        bangButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getSearchField().addBang();				
			}
		});
        
        if(mDuckDuckGoContainer.webviewShowing) {
        	homeSettingsButton.setImageResource(R.drawable.home_button);
        }
        
        shareButton = (ImageButton) contentView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);
        
        // adjust visibility of share button after screen rotation
        if(mDuckDuckGoContainer.webviewShowing) {
        	shareButton.setVisibility(View.VISIBLE);
        }
        
        searchField = (DDGAutoCompleteTextView) contentView.findViewById(R.id.searchEditText);
        getSearchField().setAdapter(mDuckDuckGoContainer.acAdapter);
        getSearchField().setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				if(textView == getSearchField()) {
					hideKeyboard(getSearchField());
					getSearchField().dismissDropDown();
					searchOrGoToUrl(getSearchField().getTrimmedText());
				}
				return false;
			}
		});
        
        getSearchField().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// close left n	av if it's open
				if(viewPager.isLeftMenuOpen()){
					viewPager.setCurrentItem(1);
				}
				showBangButton(true);
			}
		});
        getSearchField().setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				showBangButton(hasFocus);
			}
		});
        
        getSearchField().setOnBackButtonPressedEventListener(new BackButtonPressedEventListener() {
			@Override
			public void onBackButtonPressed() {
				if(!getSearchField().isPopupShowing()){
					showBangButton(false);
				}
			}
        });

        getSearchField().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(PreferencesManager.getDirectQuery()){
					//Hide the keyboard and perform a search
					getSearchField().dismissDropDown();
					
					SuggestObject suggestObject = mDuckDuckGoContainer.acAdapter.getItem(position);
					SuggestType suggestType = suggestObject.getType();
					if (suggestObject != null) {
						if(suggestType == SuggestType.TEXT) {
							String text = suggestObject.getPhrase().trim();
							if(suggestObject.hasOnlyBangQuery()){
								getSearchField().addTextWithTrailingSpace(suggestObject.getPhrase());
							}else{
								hideKeyboard(getSearchField());
								searchOrGoToUrl(text);	
							}
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
        getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : mDuckDuckGoContainer.stopDrawable, null);

        getSearchField().setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
            	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    				mCleanSearchBar = true;
                	getSearchField().setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
                }
            	
                if (getSearchField().getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > getSearchField().getWidth() - getSearchField().getPaddingRight() - mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()) {
                	if(getSearchField().getCompoundDrawables()[2] == mDuckDuckGoContainer.stopDrawable) {
	                	stopAction();
                	}
                	else {
                		reloadAction();
                	}
                }
                return false;
            }

        });

        getSearchField().addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : mDuckDuckGoContainer.stopDrawable, null);
            }

            public void afterTextChanged(Editable arg0) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        recentSearchView = (HistoryListView) contentView.findViewById(R.id.recentSearchItems);
        recentSearchView.setDivider(null);
        recentSearchView.setAdapter(mDuckDuckGoContainer.historyAdapter.getRecentSearchAdapter());
        recentSearchView.setOnHistoryItemSelectedListener(new OnHistoryItemSelectedListener() {
			
			public void onHistoryItemSelected(HistoryObject historyObject) {
				if(viewPager.isLeftMenuOpen()){
					viewPager.setCurrentItem(1);
				}
				
				if(historyObject != null){
					showHistoryObject(historyObject);
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
        feedView.setOnMainFeedItemSelectedListener(mFeedItemSelectedListener);
        feedView.setOnMainFeedItemLongClickListener(mFeedItemLongClickListener);
        
        // NOTE: After loading url multiple times on the device, it may crash
        // Related to android bug report 21266 - Watch this ticket for possible resolutions
        // http://code.google.com/p/android/issues/detail?id=21266
        // Possibly also related to CSS Transforms (bug 21305)
        // http://code.google.com/p/android/issues/detail?id=21305
        mainWebView = (DDGWebView) contentView.findViewById(R.id.mainWebView);
        mainWebView.setParentActivity(DuckDuckGo.this);
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
        
        mainWebView.setWebViewClient(new DDGWebViewClient(DuckDuckGo.this));            
        mainWebView.setWebChromeClient(new DDGWebChromeClient(DuckDuckGo.this));
        
        mainWebView.setOnLongClickListener(new OnLongClickListener() {

        	@Override
        	public boolean onLongClick(View v) {
        		HitTestResult hitTestResult = ((DDGWebView) v).getHitTestResult();
        		if(hitTestResult != null && hitTestResult.getExtra() != null) {
        			Log.i(TAG, "LONG getExtra = "+ hitTestResult.getExtra() + "\t\t Type=" + hitTestResult.getType());
        			final String touchedUrl = hitTestResult.getExtra();

                    new OpenInExternalDialogBuilder(DuckDuckGo.this, touchedUrl).show();
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
					fontSizeSeekBar.setExtraText(getResources().getString(R.string.Default));
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
				
				PreferencesManager.saveAdjustedTextSizes();
				
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
        displayHomeScreen();
        
        checkForAssistAction();
    }

	// Assist action is better known as Google Now gesture
	private void checkForAssistAction() {
		if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_ASSIST)) {
			showKeyboard(getSearchField());
		}
	}

    private void showNewSourcesDialog() {
        if(PreferencesManager.shouldShowNewSourcesDialog()){
            new NewSourcesDialogBuilder(this).show();
            PreferencesManager.newSourcesDialogWasShown();
        }
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
	
	private void clearSearchBar() {
		getSearchField().setText("");
    	getSearchField().setCompoundDrawables(null, null, null, null);
		getSearchField().setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
	}
	
	public void setSearchBarText(String text) {
		getSearchField().setFocusable(false);
		getSearchField().setFocusableInTouchMode(false);
		getSearchField().setText(text);            
		getSearchField().setFocusable(true);
		getSearchField().setFocusableInTouchMode(true);
	}
	
	private void resetScreenState() {
		clearSearchBar();
		mainWebView.clearBrowserState();
		currentFeedObject = null;
		mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_BROWSE;
	}
	
	private void cancelFontScaling() {
		fontSizeSeekBar.setExtraText(null);
		DDGControlVar.mainTextSize = DDGControlVar.prevMainTextSize;
		DDGControlVar.recentTextSize = DDGControlVar.prevRecentTextSize;
		DDGControlVar.webViewTextSize = DDGControlVar.prevWebViewTextSize;
		DDGControlVar.leftTitleTextSize = DDGControlVar.prevLeftTitleTextSize;
		mDuckDuckGoContainer.feedAdapter.notifyDataSetInvalidated();
		mDuckDuckGoContainer.historyAdapter.notifyDataSetInvalidated();
		
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
    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftMenuView.invalidate();
	}
	
	/**
	 * Displays given screen (stories, saved, settings etc.)
	 * 
	 * @param screenToDisplay Screen to display
	 * @param clean Whether screen state (searchbar, browser etc.) states will get cleaned
	 */
	public void displayScreen(SCREEN screenToDisplay, boolean clean) {
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
			showKeyboard(getSearchField());
		}
        mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_BROWSE;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// lock button etc. can cause MainFeedTask results to be useless for the Activity
		// which is restarted (onPostExecute becomes invalid for the new Activity instance)
		// ensure we refresh in such cases
		keepFeedUpdated();
		
		// update feeds
		// https://app.asana.com/0/2891531242889/2858723303746
		DDGControlVar.hasUpdatedFeed = false;
		
		// check autocomplete 
		if(!DDGControlVar.isAutocompleteActive) {
			getSearchField().setAdapter(null);
		}
		else {
	        getSearchField().setAdapter(mDuckDuckGoContainer.acAdapter);
		}
		
		if(DDGControlVar.includeAppsInSearch && !DDGControlVar.hasAppsIndexed) {
			// index installed apps
			new ScanAppsTask(getApplicationContext()).execute();
			DDGControlVar.hasAppsIndexed = true;
		}
		contentDownloader = new ContentDownloader(this);
		// global search intent
        Intent intent = getIntent(); 
        
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			intent.setAction(Intent.ACTION_MAIN);
			String query = intent.getStringExtra(SearchManager.QUERY);
			setSearchBarText(query);
			searchWebTerm(query);
		}
		else if(intent.getBooleanExtra("widget", false)) {
			viewFlipper.setDisplayedChild(DDGControlVar.START_SCREEN.getFlipOrder());
			showKeyboard(getSearchField());
		}
		else if(mDuckDuckGoContainer.webviewShowing){
			shareButton.setVisibility(View.VISIBLE);
			viewFlipper.setDisplayedChild(SCREEN.SCR_WEBVIEW.getFlipOrder());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mDuckDuckGoContainer.mainFeedTask != null) {
			mDuckDuckGoContainer.mainFeedTask.cancel(false);
			mDuckDuckGoContainer.mainFeedTask = null;
		}
		
		PreferencesManager.saveReadArticles();
		
		// XXX keep these for low memory conditions
		AppStateManager.saveAppState(sharedPreferences, mDuckDuckGoContainer, mainWebView, currentFeedObject);
	}
	
	@Override
	protected void onStop() {
		PreferencesManager.saveReadArticles();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
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
			mainWebView.backPressAction();
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
	
	public void reloadAction() {
		mCleanSearchBar = false;
        mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int)Math.floor(mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight()/1.5));
		getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : mDuckDuckGoContainer.stopDrawable, null);
		
		if(!mainWebView.isReadable)
			mainWebView.reload(); 
		else {
			new ReadableFeedTask(mReadableListener, currentFeedObject).execute();
		}
	}
	
	private void stopAction() {
		mCleanSearchBar = true;
    	getSearchField().setText("");

    	// This makes a little (X) to clear the search bar.
    	getSearchField().setCompoundDrawables(null, null, null, null);
    	getSearchField().setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
	}
	
	public void searchOrGoToUrl(String text) {
		searchOrGoToUrl(text, SESSIONTYPE.SESSION_BROWSE);
	}
	
	public void searchOrGoToUrl(String text, SESSIONTYPE sessionType) {
		hideKeyboard(mainWebView);
		savedState = false;
		if(bangButtonExplanationPopup!=null){
			bangButtonExplanationPopup.dismiss();
		}
		
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
		
		DDGApplication.getDB().insertRecentSearch(term);
		mDuckDuckGoContainer.historyAdapter.sync();
		
		if(DDGControlVar.alwaysUseExternalBrowser) {
			searchExternal(term);
        	return;
		}
		
		displayWebView();
		
		if(!savedState){
			if(DDGControlVar.regionString.equals("wt-wt")){	// default
				mainWebView.loadUrl(DDGConstants.SEARCH_URL + URLEncoder.encode(term));
			}
			else {
				mainWebView.loadUrl(DDGConstants.SEARCH_URL + URLEncoder.encode(term) + "&kl=" + URLEncoder.encode(DDGControlVar.regionString));
			}
		}		
	}
	
	public void clearRecentSearch() {
		mDuckDuckGoContainer.historyAdapter.sync();
	}
	
	public void showHistoryObject(HistoryObject historyObject) {
//		mainWebView.clearBrowserState();
		
		if(historyObject.isWebSearch()) {
			searchWebTerm(historyObject.getData());
		}
		else if(historyObject.isFeedObject()) {
			DDGApplication.getDB().insertHistoryObject(historyObject);
			mDuckDuckGoContainer.historyAdapter.sync();
			String feedId = historyObject.getFeedId();
			if(feedId != null) {
				feedItemSelected(feedId);
			}
		}
		else {
			DDGApplication.getDB().insertHistoryObject(historyObject);
			mDuckDuckGoContainer.historyAdapter.sync();
			showWebUrl(historyObject.getUrl());
		}		
	}
	
	public void showWebUrl(String url) {
		if(DDGControlVar.alwaysUseExternalBrowser) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        	startActivity(browserIntent);
        	return;
		}
		
		if(mDuckDuckGoContainer.currentScreen != SCREEN.SCR_WEBVIEW){
			displayWebView();
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
				if(mDuckDuckGoContainer.currentScreen != SCREEN.SCR_WEBVIEW) {
					displayWebView();
				}
				new ReadableFeedTask(mReadableListener, feedObject).execute();
			}
			else {
				showWebUrl(feedObject.getUrl());
			}
		}
	}

	public void onFeedRetrieved(List<FeedObject> feed, boolean fromCache) {
		if(!fromCache) {
			synchronized(mDuckDuckGoContainer.feedAdapter) {
				mDuckDuckGoContainer.feedAdapter.clear();
			}
		}
		
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
		//Otherwise, we can ask the user to try again
		if (mDuckDuckGoContainer.currentScreen != SCREEN.SCR_SAVED_FEED && mDuckDuckGoContainer.mainFeedTask != null) {
			new FeedRequestFailureDialogBuilder(this).show();
		}
	}
	
	@TargetApi(11)
	public void showPrefFragment(){
        FragmentManager fragmentManager = getFragmentManager();

        // Check to see if we have retained the worker fragment.
        DDGPreferenceFragment mWorkFragment = (DDGPreferenceFragment)fragmentManager.findFragmentById(R.id.prefFragment);
        // If not retained (or first time running), we need to create it.
        if (mWorkFragment == null) {
            mWorkFragment = new DDGPreferenceFragment();
            mWorkFragment.setRetainInstance(false);
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
						leftRecentView.displayRecordHistoryDisabled();
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
	        				getSearchField().setAdapter(null);
	        			}
	        			else {
	        		        getSearchField().setAdapter(mDuckDuckGoContainer.acAdapter);
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
            fragmentManager.beginTransaction().replace(R.id.prefFragment,
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
			feedView.cleanImageTasks();
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
        	leftRecentView.setVisibility(View.VISIBLE);
    	}
    	else {
        	leftRecentView.setVisibility(View.GONE);
    	}
	}

	/**
	 * helper method to control visibility states etc. of other views in DuckDuckGo activity
	 */
	public void makePreferencesVisible(){		
		viewFlipper.setDisplayedChild(SCREEN.SCR_SETTINGS.getFlipOrder());
		shareButton.setVisibility(View.GONE);
				
		getSearchField().setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
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
		
		// left side menu visibility changes
		changeLeftMenuVisibility(SCREEN.SCR_STORIES);
    	
    	// adjust "not recording" indicator
		leftRecentView.displayRecordHistoryDisabled();
    	
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
    	
		shareButton.setVisibility(View.GONE);
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
	
	public void showKeyboard(final View view) {
		view.post(new Runnable() {
			@Override
			public void run() {
				view.requestFocus();
				getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);

				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
			}
		});
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
		else if(view.equals(leftSettingsTextView)){
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
		hideKeyboard(getSearchField());
		
		if(DDGControlVar.homeScreenShowing){
			viewPager.switchPage();
		}
		else {
			// going home
			displayHomeScreen();
		}
	}

	private void handleShareButtonClick() {
		hideKeyboard(getSearchField());

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
            mDuckDuckGoContainer.lastFeedUrl = webViewUrl;
			new WebViewStoryMenuDialog(this, currentFeedObject, mainWebView.isReadable).show();
		}						
		else if(DDGUtils.isSerpUrl(webViewUrl)) {
            new WebViewQueryMenuDialog(this, webViewUrl).show();
		}
		else {
			new WebViewWebPageMenuDialog(this, webViewUrl).show();
		}
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
    
	@Override
	protected void onSaveInstanceState(Bundle outState)	{
		AppStateManager.saveAppState(outState, mDuckDuckGoContainer, mainWebView, currentFeedObject);					
		super.onSaveInstanceState(outState);

		// Save the state of the WebView
		mainWebView.saveState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		
		AppStateManager.recoverAppState(savedInstanceState, mDuckDuckGoContainer, mainWebView, currentFeedObject);
		String feedId = AppStateManager.getCurrentFeedObjectId(savedInstanceState);
		
		clearLeftSelect();
		markLeftSelect(mDuckDuckGoContainer.currentScreen);
		
		// Restore the state of the WebView
    	if(mDuckDuckGoContainer.webviewShowing) {
    		mainWebView.restoreState(savedInstanceState);
    	}
		
		Log.v(TAG, "feedId: " + feedId);
		
		if(feedId != null && feedId.length() != 0) {
			FeedObject feedObject = DDGApplication.getDB().selectFeedById(feedId);
			if(feedObject != null) {
				currentFeedObject = feedObject;
			}
		}			
		
		if(mDuckDuckGoContainer.webviewShowing) {
			return;
		}
		
		// arbitrary choice to not display Settings on comeback
    	if(mDuckDuckGoContainer.currentScreen == SCREEN.SCR_SETTINGS) {
    		displayHomeScreen();
    	}
    	else {
			displayScreen(mDuckDuckGoContainer.currentScreen, true);
    	}
	}
	
	private void markLeftSelect(SCREEN current){
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
	public void keepFeedUpdated(){
		if (!DDGControlVar.hasUpdatedFeed) {
			if(DDGControlVar.userAllowedSources.isEmpty() && !DDGControlVar.userDisallowedSources.isEmpty()) {
				// respect user choice of empty source list: show nothing
				onFeedRetrieved(new ArrayList<FeedObject>(), true);
			}
			else {				
				// cache
				CacheFeedTask cacheTask = new CacheFeedTask(this);
			
				// for HTTP request
				mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(mPullRefreshFeedView.getRefreshableView(), this);
				
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
		if(welcomeScreenLayout != null) {
			removeWelcomeScreen();
			addWelcomeScreen();
		}
		super.onConfigurationChanged(newConfig);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ( keyCode == KeyEvent.KEYCODE_MENU ) {
	    	viewPager.switchPage();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * set searchbar text, close left-menu (if open), show keyboard and focus on searchbar
	 * pre-search actions combined
	 */
	public void preSearch(String query) {
		setSearchBarText(query);
		showKeyboard(getSearchField());
		viewPager.setCurrentItem(1);
	}
	
	/**
	 * Step 2: Setup TabHost
	 */
	private void initialiseTabHost() {
		savedTabHost = (TabHostExt) contentView.findViewById(android.R.id.tabhost);
		savedTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		savedTabHost.addDefaultTabs();
	}

	public DDGAutoCompleteTextView getSearchField() {
		return searchField;
	}
}
