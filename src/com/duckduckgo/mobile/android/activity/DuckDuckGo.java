package com.duckduckgo.mobile.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.dialogs.NewSourcesDialogBuilder;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistorySearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistoryStoryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.MainFeedMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedSearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedStoryMenuDialog;
import com.duckduckgo.mobile.android.events.AddWelcomeScreenEvent;
import com.duckduckgo.mobile.android.events.HistoryItemLongClickEvent;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.events.HomeButtonClickEvent;
import com.duckduckgo.mobile.android.events.RecentHeaderClickEvent;
import com.duckduckgo.mobile.android.events.SearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.SourceFilterCancelEvent;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.UpdateVisibilityEvent;
import com.duckduckgo.mobile.android.events.WebViewBackPressEvent;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteStoryInHistoryEvent;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteUrlInHistoryEvent;
import com.duckduckgo.mobile.android.events.externalEvents.SendToExternalBrowserEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedItemSelectedEvent;
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.feedEvents.SavedFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.fontEvents.FontSizeCancelEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftHomeButtonClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftSavedButtonClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftSettingsButtonClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftStoriesButtonClickEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.RecentSearchPasteEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.SavedSearchPasteEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.SuggestionPasteEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveStoryEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveStoryEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemLongClickEvent;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarClickEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareFeedEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareSearchEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareWebPageEvent;
import com.duckduckgo.mobile.android.fragment.MainFragment;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.tasks.ScanAppsTask;
import com.duckduckgo.mobile.android.util.AppStateManager;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.DDGViewPager;
import com.duckduckgo.mobile.android.util.DisplayStats;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.Sharer;
import com.duckduckgo.mobile.android.util.TorIntegrationProvider;
import com.duckduckgo.mobile.android.views.WelcomeScreenView;
import com.squareup.otto.Subscribe;

public class DuckDuckGo extends SherlockFragmentActivity {
	protected final String TAG = "DuckDuckGo";
	private DuckDuckGoContainer mDuckDuckGoContainer;
		
	private DDGViewPager viewPager;
	private MainFragment mainFragment;
		
	private SharedPreferences sharedPreferences;
				
	private final int PREFERENCES_RESULT = 0;
	
	// welcome screen
	private WelcomeScreenView welcomeScreenLayout = null;
	OnClickListener welcomeCloseListener = null;

    @Override
    protected void onStart() {
        super.onStart();
        TorIntegrationProvider.getInstance(this).prepareTorSettings();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.search_edit);
        
        showNewSourcesDialog();

        sharedPreferences = DDGApplication.getSharedPreferences();
        
        String themeName = PreferencesManager.getThemeName();
		int themeId = getResources().getIdentifier(themeName, "style", getPackageName());
		if(themeId != 0) {
			setTheme(themeId);
		}
        		        
        setContentView(R.layout.pager);       
        
        DDGUtils.displayStats = new DisplayStats(this);
        
        DDGControlVar.isAutocompleteActive = !PreferencesManager.getTurnOffAutocomplete();
        // always refresh on start
        DDGControlVar.hasUpdatedFeed = false;
        mDuckDuckGoContainer = (DuckDuckGoContainer) getLastCustomNonConfigurationInstance();
    	if(mDuckDuckGoContainer == null){
            initializeContainer();
    	}
    	    	
        viewPager = (DDGViewPager) findViewById(R.id.mainpager);
        viewPager.setAdapter(mDuckDuckGoContainer.pageAdapter);
        viewPager.hideMenu();
        
        mainFragment = (MainFragment) mDuckDuckGoContainer.pageAdapter.getItem(1);
        
        TypedValue tmpTypedValue = new TypedValue();
    	getTheme().resolveAttribute(R.attr.mainTextSize, tmpTypedValue, true);
    	float defMainTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.mainTextSize = PreferencesManager.getMainFontSize(defMainTextSize);
    	
    	getTheme().resolveAttribute(R.attr.recentTextSize, tmpTypedValue, true);
    	float defRecentTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.recentTextSize = PreferencesManager.getRecentFontSize(defRecentTextSize);                  
    }

    private void initializeContainer() {
        mDuckDuckGoContainer = new DuckDuckGoContainer();
        mDuckDuckGoContainer.pageAdapter = new DDGPagerAdapter(getSupportFragmentManager());
    }

    private void showNewSourcesDialog() {
        if(PreferencesManager.shouldShowNewSourcesDialog()){
            new NewSourcesDialogBuilder(this).show();
            PreferencesManager.newSourcesDialogWasShown();
        }
    }			

	@Override
	public void onResume() {
		super.onResume();
		BusProvider.getInstance().register(this);
		
        DDGUtils.displayStats.refreshStats(this);		
		
		if(DDGControlVar.includeAppsInSearch && !DDGControlVar.hasAppsIndexed) {
			// index installed apps
			new ScanAppsTask(getApplicationContext()).execute();
			DDGControlVar.hasAppsIndexed = true;
		}		
	}

	@Override
	public void onPause() {
		super.onPause();
		
		BusProvider.getInstance().unregister(this);

		PreferencesManager.saveReadArticles();
		
		// XXX keep these for low memory conditions
		AppStateManager.saveAppState(sharedPreferences, mDuckDuckGoContainer, DDGControlVar.currentFeedObject);
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
		else if (mainFragment.isWebViewShowing()) {
			BusProvider.getInstance().post(new WebViewBackPressEvent());
		}
		else if(mainFragment.inFontChangeMode()) {
			BusProvider.getInstance().post(new FontSizeCancelEvent());
		}
		// main feed showing & source filter is active
		else if(DDGControlVar.targetSource != null){
			BusProvider.getInstance().post(new SourceFilterCancelEvent());
		}
		else {
			DDGControlVar.hasUpdatedFeed = false;
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PREFERENCES_RESULT){
			if (resultCode == RESULT_OK) {
				boolean clearedHistory = data.getBooleanExtra("hasClearedHistory",false);
				if(clearedHistory){
					BusProvider.getInstance().post(new SyncAdaptersEvent());
				}
                boolean startOrbotCheck = data.getBooleanExtra("startOrbotCheck",false);
                if(startOrbotCheck){
                    BusProvider.getInstance().post(new SearchOrGoToUrlEvent(getString(R.string.OrbotCheckSite)));
                }
                boolean switchTheme = data.getBooleanExtra("switchTheme", false);
                if(switchTheme){
                    Intent intent = new Intent(getApplicationContext(), DuckDuckGo.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
		AppStateManager.saveAppState(outState, mDuckDuckGoContainer, DDGControlVar.currentFeedObject);					
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		
		AppStateManager.recoverAppState(savedInstanceState, mDuckDuckGoContainer, DDGControlVar.currentFeedObject);
		String feedId = AppStateManager.getCurrentFeedObjectId(savedInstanceState);
		
		BusProvider.getInstance().post(new UpdateVisibilityEvent(DDGControlVar.currentScreen));
		
		Log.v(TAG, "feedId: " + feedId);
		
		if(feedId != null && feedId.length() != 0) {
			FeedObject feedObject = DDGApplication.getDB().selectFeedById(feedId);
			if(feedObject != null) {
				DDGControlVar.currentFeedObject = feedObject;
			}
		}
	}	
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		DDGUtils.displayStats.refreshStats(this);
		
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
	
	@Subscribe
	public void onDeleteStoryInHistoryEvent(DeleteStoryInHistoryEvent event) {
		final long delResult = DDGApplication.getDB().deleteHistoryByFeedId(event.feedObjectId);
		if(delResult != 0) {							
			BusProvider.getInstance().post(new SyncAdaptersEvent());
		}
		Toast.makeText(this, R.string.ToastDeleteStoryInHistory, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onDeleteUrlInHistoryEvent(DeleteUrlInHistoryEvent event) {
		final long delHistory = DDGApplication.getDB().deleteHistoryByDataUrl(event.pageData, event.pageUrl);				
		if(delHistory != 0) {							
			BusProvider.getInstance().post(new SyncAdaptersEvent());
		}	
		Toast.makeText(this, R.string.ToastDeleteUrlInHistory, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onSaveSearchEvent(SaveSearchEvent event) {
		DDGUtils.itemSaveSearch(event.pageData);
		BusProvider.getInstance().post(new SyncAdaptersEvent());
		Toast.makeText(this, R.string.ToastSaveSearch, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onSaveStoryEvent(SaveStoryEvent event) {
		DDGUtils.itemSaveFeed(event.feedObject, null);
		BusProvider.getInstance().post(new SyncAdaptersEvent());
		Toast.makeText(this, R.string.ToastSaveStory, Toast.LENGTH_SHORT).show();
	}	
	
	@Subscribe
	public void onSendToExternalBrowserEvent(SendToExternalBrowserEvent event) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.url));
		DDGUtils.execIntentIfSafe(this, browserIntent);
	}
	
	@Subscribe
	public void onShareFeedEvent(ShareFeedEvent event) {
		Sharer.shareStory(this, event.title, event.url);
	}
	
	@Subscribe
	public void onShareSearchEvent(ShareSearchEvent event) {
		Sharer.shareSearch(this, event.query);
	}
	
	@Subscribe
	public void onShareWebPageEvent(ShareWebPageEvent event) {
		Sharer.shareWebPage(this, event.url, event.url);
	}	
	
	@Subscribe
	public void onUnSaveSearchEvent(UnSaveSearchEvent event) {
		final long delHistory = DDGApplication.getDB().deleteSavedSearch(event.query);
		if(delHistory != 0) {							
			BusProvider.getInstance().post(new SyncAdaptersEvent());
		}	
		Toast.makeText(this, R.string.ToastUnSaveSearch, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onUnSaveStoryEvent(UnSaveStoryEvent event) {
		final long delResult = DDGApplication.getDB().makeItemHidden(event.feedObjectId);
		if(delResult != 0) {							
			BusProvider.getInstance().post(new SyncAdaptersEvent());
		}
		Toast.makeText(this, R.string.ToastUnSaveStory, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Handling both MainFeedItemSelectedEvent and SavedFeedItemSelectedEvent.
	 * (modify to handle independently when necessary)
	 * @param event
	 */
	@Subscribe
	public void onFeedItemSelected(FeedItemSelectedEvent event) {
		// close left nav if it's open
		if(viewPager.isLeftMenuOpen()){
            viewPager.hideMenu();
        }
	}
	
	@Subscribe
	public void onMainFeedItemLongClick(MainFeedItemLongClickEvent event) {
		new MainFeedMenuDialog(DuckDuckGo.this, event.feedObject).show();
	}
	
	@Subscribe
	public void onSavedFeedItemLongClick(SavedFeedItemLongClickEvent event) {
        new SavedStoryMenuDialog(DuckDuckGo.this, event.feedObject).show();
    }	
	
	@Subscribe
	public void onHistoryItemLongClick(HistoryItemLongClickEvent event) {
        if(event.historyObject.isFeedObject()) {
            new HistoryStoryMenuDialog(DuckDuckGo.this, event.historyObject).show();
        }
        else{
            new HistorySearchMenuDialog(DuckDuckGo.this, event.historyObject).show();
        }
	}
	
	@Subscribe
	public void onSavedSearchItemLongClick(SavedSearchItemLongClickEvent event) {
		new SavedSearchMenuDialog(this, event.query).show();
	}

	@Subscribe
	public void onHistoryItemSelected(HistoryItemSelectedEvent event) {
		viewPager.hideMenu();
	}
	
	@Subscribe
	public void onRecentSearchPaste(RecentSearchPasteEvent event) {
        viewPager.hideMenu();
	}

    @Subscribe
	public void onSuggestionPaste(SuggestionPasteEvent event) {
        viewPager.hideMenu();
	}
	
	@Subscribe
	public void onSavedSearchPaste(SavedSearchPasteEvent event) {
        viewPager.hideMenu();
	}
	
	@Subscribe
	public void onRecentHeaderClick(RecentHeaderClickEvent event) {
		viewPager.switchPage();		
	}
	
	@Subscribe
	public void onLeftHomeButtonClick(LeftHomeButtonClickEvent event) {
		viewPager.switchPage();
	}
	
	@Subscribe
	public void onLeftStoriesButtonClick(LeftStoriesButtonClickEvent event) {
		viewPager.switchPage();		
	}
	
	@Subscribe
	public void onLeftSavedButtonClick(LeftSavedButtonClickEvent event) {
		viewPager.switchPage();		
	}
	
	@Subscribe
	public void onLeftSettingsButtonClick(LeftSettingsButtonClickEvent event) {
		viewPager.switchPage();
	}
	
	@Subscribe
	public void onHomeButtonClick(HomeButtonClickEvent event) {
		if(DDGControlVar.homeScreenShowing)
			viewPager.switchPage();
	}
	
	private void addWelcomeScreen() {
    	viewPager.setDispatchTouch(false);
    	
    	if(!getResources().getBoolean(R.bool.welcomeScreen_allowLandscape)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
    	
    	// add welcome screen
        welcomeScreenLayout = new WelcomeScreenView(this);
        FrameLayout rootLayout = (FrameLayout) findViewById(android.R.id.content);
        rootLayout.addView(welcomeScreenLayout);
    	welcomeScreenLayout.setOnCloseListener(new OnClickListener() {
			@Override
			public void onClick(View v) {		
				removeWelcomeScreen();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
		});
	}
	
    public void removeWelcomeScreen() {
    	welcomeScreenLayout.setVisibility(View.GONE);
		viewPager.setDispatchTouch(true);		
		PreferencesManager.setWelcomeShown();
    	// remove welcome screen
		FrameLayout rootLayout = (FrameLayout) findViewById(android.R.id.content);
		rootLayout.removeView(welcomeScreenLayout);
		welcomeScreenLayout = null;
	}
    	
	@Subscribe
	public void onAddWelcomeScreen(AddWelcomeScreenEvent event) {
		addWelcomeScreen();
	}
	
	@Subscribe
	public void onSearchBarClick(SearchBarClickEvent event) {
		// close left n	av if it's open
		if(viewPager.isLeftMenuOpen()){
            viewPager.hideMenu();
        }
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.actionbar, menu);
		return true;
	}
	
}
