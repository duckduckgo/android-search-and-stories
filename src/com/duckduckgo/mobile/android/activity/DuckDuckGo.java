package com.duckduckgo.mobile.android.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.dialogs.NewSourcesDialogBuilder;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistorySearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistoryStoryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.MainFeedMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedSearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedStoryMenuDialog;
import com.duckduckgo.mobile.android.events.AddWelcomeScreenEvent;
import com.duckduckgo.mobile.android.events.AfterSwitchPostEvent;
import com.duckduckgo.mobile.android.events.CleanFeedDownloadsEvent;
import com.duckduckgo.mobile.android.events.DisplayScreenEvent;
import com.duckduckgo.mobile.android.events.HistoryItemLongClickEvent;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.events.ReloadEvent;
import com.duckduckgo.mobile.android.events.ResetScreenStateEvent;
import com.duckduckgo.mobile.android.events.SearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.SearchWebTermEvent;
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
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarClearEvent;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarClickEvent;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarSetTextEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareFeedEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareSearchEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareWebPageEvent;
import com.duckduckgo.mobile.android.fragment.FeedFragment;
import com.duckduckgo.mobile.android.fragment.RecentSearchFragment;
import com.duckduckgo.mobile.android.fragment.SavedMainFragment;
import com.duckduckgo.mobile.android.fragment.WebFragment;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.SuggestObject;
import com.duckduckgo.mobile.android.tasks.ScanAppsTask;
import com.duckduckgo.mobile.android.util.AppStateManager;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.DisplayStats;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.Sharer;
import com.duckduckgo.mobile.android.util.SuggestType;
import com.duckduckgo.mobile.android.util.TorIntegrationProvider;
import com.duckduckgo.mobile.android.views.WelcomeScreenView;
import com.duckduckgo.mobile.android.views.autocomplete.BackButtonPressedEventListener;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;
import com.duckduckgo.mobile.android.widgets.BangButtonExplanationPopup;
import com.squareup.otto.Subscribe;

public class DuckDuckGo extends ActionBarActivity {
	protected final String TAG = "DuckDuckGo";
	
	ActionBarDrawerToggle mDrawerToggle;
	DrawerLayout mDrawerLayout;
	View leftView;
					
	private SharedPreferences sharedPreferences;
				
	private final int PREFERENCES_RESULT = 0;
	
	// welcome screen
	private WelcomeScreenView welcomeScreenLayout = null;
	OnClickListener welcomeCloseListener = null;
	
	private KeyboardService keyboardService;
	
	public Drawable progressDrawable, searchFieldDrawable;
	public Drawable stopDrawable;
			
	public AutoCompleteResultsAdapter acAdapter = null;
	private DDGAutoCompleteTextView searchField = null;
	
	private boolean shouldShowBangButtonExplanation;
	private BangButtonExplanationPopup bangButtonExplanationPopup;

	public DDGAutoCompleteTextView getSearchField() {
		return searchField;
	}
	
	private View getHomeButton() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                return findViewById(android.R.id.home);
        }
        return findViewById(R.id.home);
	}
	
    // Assist action is better known as Google Now gesture
	private boolean isLaunchedWithAssistAction(){
		return getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_ASSIST);
	}
	
	public void clearSearchBar() {
		getSearchField().setText("");
    	getSearchField().setCompoundDrawables(null, null, null, null);
		getSearchField().setBackgroundDrawable(searchFieldDrawable);
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
		BusProvider.getInstance().post(new ResetScreenStateEvent());		
		DDGControlVar.sessionType = SESSIONTYPE.SESSION_BROWSE;
	}
	
	private void showBangButton(boolean visible){
		if(shouldShowBangButtonExplanation && visible && PreferencesManager.isWelcomeShown()){
			ImageView homeSettingsButton = (ImageView) getHomeButton();
			bangButtonExplanationPopup = BangButtonExplanationPopup.showPopup(this, homeSettingsButton);
			shouldShowBangButtonExplanation = false;
		}
		if(!visible){
			if(bangButtonExplanationPopup!=null){
				bangButtonExplanationPopup.dismiss();
			}
		}
    }
	
	private void stopAction() {
		DDGControlVar.mCleanSearchBar = true;
    	getSearchField().setText("");

    	// This makes a little (X) to clear the search bar.
    	getSearchField().setCompoundDrawables(null, null, null, null);
    	getSearchField().setBackgroundDrawable(searchFieldDrawable);
	}
	
    @Override
    protected void onStart() {
        super.onStart();
        TorIntegrationProvider.getInstance(this).prepareTorSettings();
    }
    
    private void initialiseSearchbar() {
    	stopDrawable = getResources().getDrawable(R.drawable.stop);
//    	reloadDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.reload);
        progressDrawable = getResources().getDrawable(R.drawable.page_progress);
        searchFieldDrawable = getResources().getDrawable(R.drawable.searchfield);
        searchFieldDrawable.setAlpha(150);
        
        if(!PreferencesManager.isWelcomeShown()) {
            BusProvider.getInstance().post(new AddWelcomeScreenEvent());
            shouldShowBangButtonExplanation = true;
    	}
        
        acAdapter = new AutoCompleteResultsAdapter(this);
        
        searchField = (DDGAutoCompleteTextView) getSupportActionBar()
        		.getCustomView().findViewById(R.id.searchEditText);
        getSearchField().setAdapter(acAdapter);
        getSearchField().setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				if(textView == getSearchField() && actionId != EditorInfo.IME_NULL) {
                    keyboardService.hideKeyboard(getSearchField());
    				getSearchField().dismissDropDown();
    				BusProvider.getInstance().post(new AfterSwitchPostEvent(SCREEN.SCR_WEBVIEW, 
    						new SearchOrGoToUrlEvent(getSearchField().getTrimmedText())));
    			}
    			return false;
    		}
    	});
        
        getSearchField().setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			BusProvider.getInstance().post(new SearchBarClickEvent());
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
    			if(getSearchField().isPopupShowing()){
                    getSearchField().dismissDropDown();
    			}
                showBangButton(false);
    		}
        });

        getSearchField().setOnItemClickListener(new OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			if(PreferencesManager.getDirectQuery()){
    				//Hide the keyboard and perform a search
    				getSearchField().dismissDropDown();
    				
    				SuggestObject suggestObject = acAdapter.getItem(position);
    				SuggestType suggestType = suggestObject.getType();
    				if (suggestObject != null) {
    					if(suggestType == SuggestType.TEXT) {
    						String text = suggestObject.getPhrase().trim();
    						if(suggestObject.hasOnlyBangQuery()){
    							getSearchField().addTextWithTrailingSpace(suggestObject.getPhrase());
    						}else{
                                keyboardService.hideKeyboard(getSearchField());
    							BusProvider.getInstance().post(new AfterSwitchPostEvent(SCREEN.SCR_WEBVIEW, new SearchOrGoToUrlEvent(text)));
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
        stopDrawable.setBounds(0, 0, (int)Math.floor(stopDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(stopDrawable.getIntrinsicHeight()/1.5));
        getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : stopDrawable, null);

        getSearchField().setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
            	if (event.getAction() == MotionEvent.ACTION_DOWN) {
            		DDGControlVar.mCleanSearchBar = true;
                	getSearchField().setBackgroundDrawable(searchFieldDrawable);
                }
            	
                if (getSearchField().getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > getSearchField().getWidth() - getSearchField().getPaddingRight() - stopDrawable.getIntrinsicWidth()) {
                	if(getSearchField().getCompoundDrawables()[2] == stopDrawable) {
	                	stopAction();
                	}
                	else {
                		BusProvider.getInstance().post(new ReloadEvent());
                	}
                }
                return false;
            }

        });

        getSearchField().addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : stopDrawable, null);
            }

            public void afterTextChanged(Editable arg0) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });  
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keyboardService = new KeyboardService(this);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.search_edit);
        
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        initialiseSearchbar();
        
        showNewSourcesDialog();

        sharedPreferences = DDGApplication.getSharedPreferences();
        
        String themeName = PreferencesManager.getThemeName();
		int themeId = getResources().getIdentifier(themeName, "style", getPackageName());
		if(themeId != 0) {
			setTheme(themeId);
		}
        		        
        setContentView(R.layout.activity_main);       
        
        DDGUtils.displayStats = new DisplayStats(this);
        
        DDGControlVar.isAutocompleteActive = !PreferencesManager.getTurnOffAutocomplete();
        // always refresh on start
        DDGControlVar.hasUpdatedFeed = false;
            	
        leftView = findViewById(R.id.LeftRelativeLayout);
        
        switchFragments(DDGControlVar.START_SCREEN);
        
    	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    	
    	mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {
 
            public void onDrawerClosed(View view) {
                // TODO Auto-generated method stub
                super.onDrawerClosed(view);
            }
 
            public void onDrawerOpened(View drawerView) {
                // TODO Auto-generated method stub
                super.onDrawerOpened(drawerView);
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);    	    	
                
        TypedValue tmpTypedValue = new TypedValue();
    	getTheme().resolveAttribute(R.attr.mainTextSize, tmpTypedValue, true);
    	float defMainTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.mainTextSize = PreferencesManager.getMainFontSize(defMainTextSize);
    	
    	getTheme().resolveAttribute(R.attr.recentTextSize, tmpTypedValue, true);
    	float defRecentTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.recentTextSize = PreferencesManager.getRecentFontSize(defRecentTextSize);                  
    }

    private void showNewSourcesDialog() {
        if(PreferencesManager.shouldShowNewSourcesDialog()){
            new NewSourcesDialogBuilder(this).show();
            PreferencesManager.newSourcesDialogWasShown();
        }
    }			
    
//    private boolean isDrawerOpen() {
//    	return mDrawerLayout.isDrawerOpen(leftView);
//    }
//    
//    private void closeDrawerIfOpen() {
//    	if(isDrawerOpen()) {
//    		mDrawerLayout.closeDrawer(leftView);
//    	}
//    }
//    
//    private void switchDrawer() {
//    	if(isDrawerOpen()) {
//    		mDrawerLayout.closeDrawer(leftView);
//    	}
//    	else {
//    		mDrawerLayout.openDrawer(leftView);
//    	}
//    }

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
		
		// check autocomplete 
		if(!DDGControlVar.isAutocompleteActive) {
			getSearchField().setAdapter(null);
		}
		else {
			getSearchField().setAdapter(acAdapter);
		}
		
		
		// global search intent
        Intent intent = getIntent(); 
        
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			intent.setAction(Intent.ACTION_MAIN);
			String query = intent.getStringExtra(SearchManager.QUERY);
			setSearchBarText(query);
			BusProvider.getInstance().post(new SearchWebTermEvent(query));
		}
		else if(intent.getBooleanExtra("widget", false)) {
			switchFragments(DDGControlVar.START_SCREEN);
            keyboardService.showKeyboard(getSearchField());
		}
//		else if(isWebViewShowing()){
//			viewFlipper.setDisplayedChild(SCREEN.SCR_WEBVIEW.getFlipOrder());
//		}
		else if(isLaunchedWithAssistAction()){
			keyboardService.showKeyboard(getSearchField());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		
		BusProvider.getInstance().unregister(this);

		PreferencesManager.saveReadArticles();
		
		// XXX keep these for low memory conditions
		AppStateManager.saveAppState(sharedPreferences, DDGControlVar.currentFeedObject);
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
//		if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
//			mDrawerLayout.closeDrawer(leftView);
//		}
		if (DDGControlVar.currentScreen == SCREEN.SCR_WEBVIEW) {
			BusProvider.getInstance().post(new WebViewBackPressEvent());
			return;
		}
//		else if(mainFragment.inFontChangeMode()) {
//			BusProvider.getInstance().post(new FontSizeCancelEvent());
//		}
		// main feed showing & source filter is active
		if(DDGControlVar.targetSource != null){
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
                    BusProvider.getInstance().post(new AfterSwitchPostEvent(SCREEN.SCR_WEBVIEW, 
                    		new SearchOrGoToUrlEvent(getString(R.string.OrbotCheckSite))));
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
	protected void onSaveInstanceState(Bundle outState)	{
		AppStateManager.saveAppState(outState, DDGControlVar.currentFeedObject);					
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		
		AppStateManager.recoverAppState(savedInstanceState, DDGControlVar.currentFeedObject);
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
		
		mDrawerToggle.onConfigurationChanged(newConfig);
		super.onConfigurationChanged(newConfig);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ( keyCode == KeyEvent.KEYCODE_MENU ) {
//	    	switchDrawer();
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
		String type = event.historyObject.getType();
		if(type.equals("R")) {
			setSearchBarText(event.historyObject.getData());
		}
		else {
			setSearchBarText(event.historyObject.getUrl());
		}
	}
	
	@Subscribe
	public void onFeedItemSelected(FeedItemSelectedEvent event) {
		String url = event.feedObject.getUrl();
		if(url != null) {
			setSearchBarText(url);
		}
		else {
			clearSearchBar();
		}
	}
	
	@Subscribe
	public void onRecentSearchPaste(RecentSearchPasteEvent event) {
		setSearchBarText(event.query);
	}

    @Subscribe
	public void onSuggestionPaste(SuggestionPasteEvent event) {
    	setSearchBarText(event.query);
	}
	
	@Subscribe
	public void onSavedSearchPaste(SavedSearchPasteEvent event) {
		setSearchBarText(event.query);
	}
//	
//	@Subscribe
//	public void onRecentHeaderClick(RecentHeaderClickEvent event) {
//		switchDrawer();	
//	}
//	
//	
//	@Subscribe
//	public void onHomeButtonClick(HomeButtonClickEvent event) {
//		if(DDGControlVar.homeScreenShowing)
//			switchDrawer();
//	}
	
	public static int getContentViewCompat() {
	    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ?
	               android.R.id.content : R.id.action_bar_activity_content;
	}
	
	private void addWelcomeScreen() {
//    	viewPager.setDispatchTouch(false);
    	
    	if(!getResources().getBoolean(R.bool.welcomeScreen_allowLandscape)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
    	
    	// add welcome screen
        welcomeScreenLayout = new WelcomeScreenView(this);
        FrameLayout rootLayout = (FrameLayout) findViewById(getContentViewCompat());
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
//		viewPager.setDispatchTouch(true);		
		PreferencesManager.setWelcomeShown();
    	// remove welcome screen
		FrameLayout rootLayout = (FrameLayout) findViewById(getContentViewCompat());
		rootLayout.removeView(welcomeScreenLayout);
		welcomeScreenLayout = null;
	}
    	
	@Subscribe
	public void onAddWelcomeScreen(AddWelcomeScreenEvent event) {
		addWelcomeScreen();
	}
	
//	@Subscribe
//	public void onSearchBarClick(SearchBarClickEvent event) {
//		closeDrawerIfOpen();
//	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) { 
			return true; 
		}
		return false;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar, menu);
		return true;
	}
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    
	public void switchFragments(SCREEN screen) {
		FragmentManager fragmentManager = getSupportFragmentManager();		
		Fragment mWorkFragment = null;
		
		switch(screen) {
			case SCR_STORIES:
				mWorkFragment = new FeedFragment();
				break;
			case SCR_SAVED_FEED:
				mWorkFragment = new SavedMainFragment();
				break;
			case SCR_RECENT_SEARCH:
				mWorkFragment = new RecentSearchFragment();
				break;
			case SCR_WEBVIEW:
				mWorkFragment = new WebFragment();
				break;
		}
			
		if(mWorkFragment != null) {			
			fragmentManager.beginTransaction().replace(R.id.content_fragment,
	                mWorkFragment).commit();
			fragmentManager.executePendingTransactions();
			DDGControlVar.prevScreen = DDGControlVar.currentScreen;
			DDGControlVar.currentScreen = screen;
		}
		
	}
	
	@Subscribe
	public void onLeftHomeButtonClick(LeftHomeButtonClickEvent event) {
		switchFragments(DDGControlVar.START_SCREEN);
	}
	
	@Subscribe
	public void onLeftStoriesButtonClick(LeftStoriesButtonClickEvent event) {
		switchFragments(SCREEN.SCR_STORIES);
	}
	
	@Subscribe
	public void onLeftSavedButtonClick(LeftSavedButtonClickEvent event) {
		switchFragments(SCREEN.SCR_SAVED_FEED);
	}
	
	@Subscribe
	public void onLeftSettingsButtonClick(LeftSettingsButtonClickEvent event) {
		BusProvider.getInstance().post(new CleanFeedDownloadsEvent());
	    Intent intent = new Intent(this, Preferences.class);
	    startActivityForResult(intent, PREFERENCES_RESULT);
	}
	
	@Subscribe
	public void onDisplayScreenEvent(DisplayScreenEvent event) {
		switchFragments(event.screenToDisplay);
	}
	
	@Subscribe
	public void onAfterSwitchPost(AfterSwitchPostEvent event) {
		switchFragments(event.screenToDisplay);
		BusProvider.getInstance().post(event.postEvent);
	}
    
	@Subscribe
	public void onSearchBarSetText(SearchBarSetTextEvent event) {
		setSearchBarText(event.text);
	}
	
	@Subscribe
	public void onSearchBarClear(SearchBarClearEvent event) {
		clearSearchBar();
	}
	
}
