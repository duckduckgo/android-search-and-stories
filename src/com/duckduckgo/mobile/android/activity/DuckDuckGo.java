package com.duckduckgo.mobile.android.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.MultiHistoryAdapter;
import com.duckduckgo.mobile.android.adapters.RecentResultCursorAdapter;
import com.duckduckgo.mobile.android.adapters.TempAutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.dialogs.NewSourcesDialogBuilder;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistorySearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistoryStoryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.MainFeedMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedSearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedStoryMenuDialog;
import com.duckduckgo.mobile.android.events.AutoCompleteResultClickEvent;
import com.duckduckgo.mobile.android.events.ConfirmDialogOkEvent;
import com.duckduckgo.mobile.android.events.DismissBangPopupEvent;
import com.duckduckgo.mobile.android.events.DisplayScreenEvent;
import com.duckduckgo.mobile.android.events.HistoryItemLongClickEvent;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.events.ReloadEvent;
import com.duckduckgo.mobile.android.events.RemoveWebFragmentEvent;
import com.duckduckgo.mobile.android.events.RequestOpenWebPageEvent;
import com.duckduckgo.mobile.android.events.RequestSyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.SetMainButtonHomeEvent;
import com.duckduckgo.mobile.android.events.SetMainButtonMenuEvent;
import com.duckduckgo.mobile.android.events.ShowAutoCompleteResultsEvent;
import com.duckduckgo.mobile.android.events.StopActionEvent;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewBackPressActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewClearCacheAndCookiesEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewClearCacheEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewOpenMenuEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewReloadActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewSearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewSearchWebTermEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewShowHistoryObjectEvent;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteStoryInHistoryEvent;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteUrlInHistoryEvent;
import com.duckduckgo.mobile.android.events.externalEvents.SendToExternalBrowserEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelCategoryFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelSourceFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.feedEvents.SavedFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuChangeVisibilityEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuClearSelectEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuCloseEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuHomeClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuMarkSelectedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSavedClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetAdapterEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetRecentVisibleEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSettingsClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuStoriesClickEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.RecentSearchPasteEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.SavedSearchPasteEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.SuggestionPasteEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveStoryEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveStoryEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemLongClickEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemSelectedEvent;
import com.duckduckgo.mobile.android.events.searchBarEvents.SearchBarAddClearTextDrawable;
import com.duckduckgo.mobile.android.events.searchBarEvents.SearchBarChangeEvent;
import com.duckduckgo.mobile.android.events.searchBarEvents.SearchBarClearEvent;
import com.duckduckgo.mobile.android.events.searchBarEvents.SearchBarSetProgressEvent;
import com.duckduckgo.mobile.android.events.searchBarEvents.SearchBarSetTextEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareFeedEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareSearchEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareWebPageEvent;
import com.duckduckgo.mobile.android.fragment.AboutFragment;
import com.duckduckgo.mobile.android.fragment.FavoriteFragment;
import com.duckduckgo.mobile.android.fragment.FeedFragment;
import com.duckduckgo.mobile.android.fragment.HelpFeedbackFragment;
import com.duckduckgo.mobile.android.fragment.PrefFragment;
import com.duckduckgo.mobile.android.fragment.RecentsFragment;
import com.duckduckgo.mobile.android.fragment.SearchFragment;
import com.duckduckgo.mobile.android.fragment.WebFragment;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.SuggestObject;
import com.duckduckgo.mobile.android.tasks.ScanAppsTask;
import com.duckduckgo.mobile.android.util.AppStateManager;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.DisplayStats;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.Sharer;
import com.duckduckgo.mobile.android.util.SuggestType;
import com.duckduckgo.mobile.android.util.TorIntegrationProvider;
import com.duckduckgo.mobile.android.views.SeekBarHint;
import com.duckduckgo.mobile.android.views.WelcomeScreenView;
import com.duckduckgo.mobile.android.views.autocomplete.BackButtonPressedEventListener;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;
import com.duckduckgo.mobile.android.views.webview.DDGWebView;
import com.duckduckgo.mobile.android.widgets.BangButtonExplanationPopup;
import com.squareup.otto.Subscribe;

import java.nio.charset.Charset;
import java.util.List;

//import net.hockeyapp.android.CrashManager;
//import net.hockeyapp.android.UpdateManager;

public class DuckDuckGo extends ActionBarActivity implements OnClickListener {
	protected final String TAG = "DuckDuckGo";
    private KeyboardService keyboardService;

	private DDGAutoCompleteTextView searchField = null;
    //private FrameLayout searchFieldContainer = null;
    private RelativeLayout searchFieldContainer = null;
    private TextView actionBarTitle;
	//private HistoryListView leftRecentView = null;//drawer fragment

	//private HistoryListView recentSearchView = null;//recent search fragment

	private ImageButton mainButton = null;
	//private ImageButton bangButton = null;
	//private ImageButton shareButton = null;
    private ImageButton homebutton = null;
    private ImageButton bangButton = null;

	private FrameLayout fragmentContainer;

	private FragmentManager fragmentManager;
	private WebFragment webFragment;
	private FavoriteFragment favoriteFragment;
    private RecentsFragment recentsFragment;
	private FeedFragment feedFragment;
    private SearchFragment searchFragment;

	public Toolbar toolbar;
	private ActionBar actionBar;

	// welcome screen
	private WelcomeScreenView welcomeScreenLayout = null;
	OnClickListener welcomeCloseListener = null;
		
	private SharedPreferences sharedPreferences;
		
	public boolean savedState = false;
		
	private final int PREFERENCES_RESULT = 0;

    private View searchBar;
    private View dropShadowDivider;

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
    	DDGControlVar.mDuckDuckGoContainer.historyAdapter.sync();
    	BusProvider.getInstance().post(new SyncAdaptersEvent());
    }
    
    /**
     * Adds welcome screen on top of content view
     * Also disables dispatching of touch events from viewPager to children views
     */
    private void addWelcomeScreen() {

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

		PreferencesManager.setWelcomeShown();
    	// remove welcome screen
		FrameLayout rootLayout = (FrameLayout)findViewById(android.R.id.content);
		rootLayout.removeView(welcomeScreenLayout);
		welcomeScreenLayout = null;
    }

    private void showBangButton(boolean visible){
    	mainButton.setVisibility(visible ? View.GONE: View.VISIBLE);
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
    protected void onStart() {
        super.onStart();
		TorIntegrationProvider.getInstance(this).prepareTorSettings();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("aaa", "on create ---------------------------------------------------------------------");
        keyboardService = new KeyboardService(this);
        supportRequestWindowFeature(Window.FEATURE_PROGRESS);

        showNewSourcesDialog();

        sharedPreferences = DDGApplication.getSharedPreferences();
        
        String themeName = PreferencesManager.getThemeName();
		int themeId = getResources().getIdentifier(themeName, "style", getPackageName());
		if(themeId != 0) {
			setTheme(themeId);
		}
        		        
        //setContentView(R.layout.drawer);
		setContentView(R.layout.main_temp);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        //getWindow().setBackgroundDrawableResource(R.drawable.standard_window_background);
        
        DDGUtils.displayStats = new DisplayStats(this);

        if(savedInstanceState != null)
        	savedState = true;
        
        DDGControlVar.isAutocompleteActive = PreferencesManager.getAutocomplete();
        // always refresh on start
        DDGControlVar.hasUpdatedFeed = false;
        DDGControlVar.mDuckDuckGoContainer = (DuckDuckGoContainer) getLastCustomNonConfigurationInstance();
    	if(DDGControlVar.mDuckDuckGoContainer == null){
            initializeContainer();
    	}

		fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);

		fragmentManager = getSupportFragmentManager();
        initFragments();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.e("aaa", "---------------inside on back stack changed");
                Log.e("aaa", "back stack count: "+fragmentManager.getBackStackEntryCount());
                if(fragmentManager.getBackStackEntryCount() > 0) {
                    String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                    if(tag!=null) {
                        DDGControlVar.mDuckDuckGoContainer.currentFragmentTag = tag;
                        if(!tag.equals(WebFragment.TAG) && !DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
                            DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;
                        }
                        DDGControlVar.mDuckDuckGoContainer.currentScreen = getScreenByTag(tag);
                        DDGControlVar.mDuckDuckGoContainer.webviewShowing = tag.equals(WebFragment.TAG);
                        DDGControlVar.homeScreenShowing = DDGControlVar.mDuckDuckGoContainer.currentScreen == DDGControlVar.START_SCREEN;

                        updateActionBar(tag);
                    }
                    showAllFragments();
                }
            }
        });

        if(!PreferencesManager.isWelcomeShown()) {
            addWelcomeScreen();
            shouldShowBangButtonExplanation = true;
    	}

        initActionBar();

        if(DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
        	setMainButtonHome();
        }

		if(savedInstanceState==null) {
			displayHomeScreen();
		}

        checkForUpdates();
    }

	private void setMainButtonHome() {
		//mainButton.setImageResource(R.drawable.ic_home);
	}

	private void setMainButtonMenu() {
		//mainButton.setImageResource(R.drawable.ic_menu);
	}

    private void updateActionBar(String tag) {
        Log.e("aaa", "update actionbar: "+tag);
        //Log.e("aaa", "starting screen: "+DDGControlVar.START_SCREEN);
        //Log.e("aaa", "current screen: "+DDGControlVar.mDuckDuckGoContainer.currentScreen);

        dropShadowDivider.setVisibility(View.VISIBLE);

        SCREEN screen = getScreenByTag(tag);
        Log.e("aaa", "update actionbar: "+tag+" - screen: "+screen+" - start screen: "+DDGControlVar.START_SCREEN);

        boolean isStartingScreen = DDGControlVar.START_SCREEN==screen;
        if(!tag.equals(SearchFragment.TAG)) {
            Fragment searchFragment = fragmentManager.findFragmentByTag(SearchFragment.TAG);
            if(searchFragment==null || !searchFragment.isVisible()) {
                Log.e("aaa", "ERROR 9 - aka 1, tag: " + tag);
                //getSearchField().clearFocus();

                getSearchField().setFocusable(false);
                getSearchField().setFocusableInTouchMode(false);
                //getSearchField().setText(text);
                getSearchField().setFocusable(true);
                getSearchField().setFocusableInTouchMode(true);
            }
        }

        switch(screen) {
            case SCR_STORIES:
                clearSearchBar();
                showActionBarSearchField();
                setActionBarShadow(true);
                hasOverflowButtonVisible(isStartingScreen);

                setActionBarMarginBottom(true);

                setHomeButton(!isStartingScreen);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
                break;
            case SCR_RECENTS:
                showActionBarSearchField();
                setActionBarShadow(false);
                setActionBarMarginBottom(false);
                hasOverflowButtonVisible(isStartingScreen);
                setHomeButton(!isStartingScreen);
                setHomeButtonMarginTop(true);
                setStandardActionBarHeight(false);
                break;
            case SCR_SAVED:
                showActionBarSearchField();
                setActionBarShadow(false);
                setActionBarMarginBottom(false);
                hasOverflowButtonVisible(isStartingScreen);
                setHomeButton(!isStartingScreen);
                setHomeButtonMarginTop(true);
                setStandardActionBarHeight(false);
                break;
            case SCR_WEBVIEW:
                showActionBarSearchField();
                setActionBarShadow(true);
                hasOverflowButtonVisible(true);

                setActionBarMarginBottom(true);

                //setSearchBarText(DDGControlVar.mDuckDuckGoContainer.currentUrl);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
                break;
            case SCR_SEARCH:
            case SCR_SEARCH_HOME_PAGE:
                showActionBarSearchField();
                setActionBarShadow(true);
                hasOverflowButtonVisible(screen==SCREEN.SCR_SEARCH_HOME_PAGE);

                setActionBarMarginBottom(true);
                setHomeButtonMarginTop(false);

                setBangButton();
                setStandardActionBarHeight(true);
                break;
            case SCR_ABOUT:
                showActionBarTitle(getResources().getString(R.string.about));
                setActionBarShadow(true);
                hasOverflowButtonVisible(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
                break;
            case SCR_HELP:
                showActionBarTitle(getResources().getString(R.string.help_feedback));
                setActionBarShadow(true);
                hasOverflowButtonVisible(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
                break;
            case SCR_SETTINGS:
                showActionBarTitle(getResources().getString(R.string.settings));
                setActionBarShadow(true);
                hasOverflowButtonVisible(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
                break;
            default:
                break;
        }

        if(!tag.equals(SearchFragment.TAG) && !tag.equals(SearchFragment.TAG_HOME_PAGE)) {
            keyboardService.hideKeyboard(getSearchField());
        }
    }

    private SCREEN getScreenByTag(String tag) {
        if(tag.equals(RecentsFragment.TAG)) {
            return SCREEN.SCR_RECENTS;
        } else if(tag.equals(FavoriteFragment.TAG)) {
            return SCREEN.SCR_SAVED;
        } else if(tag.equals(WebFragment.TAG)) {
            return SCREEN.SCR_WEBVIEW;
        } else if(tag.equals(SearchFragment.TAG)) {
            return SCREEN.SCR_SEARCH;
        } else if(tag.equals(AboutFragment.TAG)) {
            return SCREEN.SCR_ABOUT;
        } else if(tag.equals(HelpFeedbackFragment.TAG)) {
            return SCREEN.SCR_HELP;
        } else if(tag.equals(PrefFragment.TAG)) {
            return SCREEN.SCR_SETTINGS;
        } else  if(tag.equals(SearchFragment.TAG_HOME_PAGE)) {
            return SCREEN.SCR_SEARCH_HOME_PAGE;
        }
        return SCREEN.SCR_STORIES;
    }

    private String getTagByScreen(SCREEN screen) {
        switch(screen) {
            case SCR_STORIES:
                return FeedFragment.TAG;
            case SCR_RECENTS:
                return RecentsFragment.TAG;
            case SCR_SAVED:
                return FavoriteFragment.TAG;
            case SCR_WEBVIEW:
                return WebFragment.TAG;
            case SCR_SEARCH:
                return SearchFragment.TAG;
            case SCR_ABOUT:
                return AboutFragment.TAG;
            case SCR_HELP:
                return HelpFeedbackFragment.TAG;
            case SCR_SETTINGS:
                return PrefFragment.TAG;
            case SCR_SEARCH_HOME_PAGE:
                return SearchFragment.TAG_HOME_PAGE;
            default:
                return FeedFragment.TAG;
        }
    }

    private void showActionBarSearchField() {
        toggleActionBarView(false, "");
    }

    private void showActionBarTitle(String newTitle) {
        toggleActionBarView(true, newTitle);
    }

    private void toggleActionBarView(boolean showTitle, String newTitle) {
        if(showTitle) {
            searchFieldContainer.setVisibility(View.GONE);
            actionBarTitle.setVisibility(View.VISIBLE);
            actionBarTitle.setText(newTitle);
        } else {
            searchFieldContainer.setVisibility(View.VISIBLE);
            actionBarTitle.setVisibility(View.GONE);
        }
    }

	private void initActionBar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);

		View actionBarView = LayoutInflater.from(this).inflate(R.layout.temp_actionbar, null);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		actionBar.setCustomView(actionBarView, params);

		searchBar = actionBar.getCustomView().findViewById(R.id.searchBar);
		dropShadowDivider = findViewById(R.id.dropshadow_top);//todo remove
        //searchFieldContainer = (FrameLayout) actionBar.getCustomView().findViewById(R.id.search_container);
        searchFieldContainer = (RelativeLayout) actionBar.getCustomView().findViewById(R.id.search_container);
        actionBarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Medium.ttf");
        actionBarTitle.setTypeface(typeface);

		searchField = (DDGAutoCompleteTextView) actionBar.getCustomView().findViewById(R.id.searchEditText);
		//getSearchField().setAdapter(DDGControlVar.mDuckDuckGoContainer.acAdapter);
        //getSearchField().setAdapter(DDGControlVar.mDuckDuckGoContainer.tempAdapter);//aaa adapter
		getSearchField().setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				if(textView == getSearchField() && actionId != EditorInfo.IME_NULL) {
					keyboardService.hideKeyboard(getSearchField());
					//getSearchField().dismissDropDown();
                    Log.e("aaa", "on editor action listener, must search or go to url, text is: "+getSearchField().getText().toString());
                    //removeSearchFragment();
					searchOrGoToUrl(getSearchField().getTrimmedText());
				}
				return false;
			}
		});

		getSearchField().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//drawer.close();
                //displayScreen(SCREEN.SCR_SEARCH, true);
//				showBangButton(true);
			}
		});
		getSearchField().setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
//				showBangButton(hasFocus);
                Log.e("aaa", "get search field, focus: "+hasFocus);
                if(hasFocus) {
                    //displayScreen(SCREEN.SCR_SEARCH, false);
                    addSearchFragment();
                }
			}
		});

		getSearchField().setOnBackButtonPressedEventListener(new BackButtonPressedEventListener() {
			@Override
			public void onBackButtonPressed() {/*
				if(getSearchField().isPopupShowing()){
					getSearchField().dismissDropDown();
				}
//				showBangButton(false);*/
                Log.e("aaa", "GET SEARCH FIELD on back button pressed event");
			}
		});
/*
		getSearchField().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getSearchField().dismissDropDown();

				//SuggestObject suggestObject = DDGControlVar.mDuckDuckGoContainer.acAdapter.getItem(position);
                SuggestObject suggestObject = DDGControlVar.mDuckDuckGoContainer.tempAdapter.getItem(position);
				if (suggestObject != null) {
					SuggestType suggestType = suggestObject.getType();
					if(suggestType == SuggestType.TEXT) {
						if(PreferencesManager.getDirectQuery()){
							String text = suggestObject.getPhrase().trim();
							if(suggestObject.hasOnlyBangQuery()){
								getSearchField().addTextWithTrailingSpace(suggestObject.getPhrase());
							}else{
                                Log.e("aaa", "=========== ERROR - 1");
								keyboardService.hideKeyboard(getSearchField());
								searchOrGoToUrl(text);
							}
						}
					}
					else if(suggestType == SuggestType.APP) {
						DDGUtils.launchApp(DuckDuckGo.this, suggestObject.getSnippet());
					}
				}
			}
		});
*/
		// This makes a little (X) to clear the search bar.
		//DDGControlVar.mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int)Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight()/1.5));
        DDGControlVar.mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int)Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()), (int)Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight()));
		getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : DDGControlVar.mDuckDuckGoContainer.stopDrawable, null);

		getSearchField().setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					DDGControlVar.mCleanSearchBar = true;
					//getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable);//aaa
				}

				if (getSearchField().getCompoundDrawables()[2] == null) {
					return false;
				}
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}
				if (event.getX() > getSearchField().getWidth() - getSearchField().getPaddingRight() - DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()) {
					if(getSearchField().getCompoundDrawables()[2] == DDGControlVar.mDuckDuckGoContainer.stopDrawable) {
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
				getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : DDGControlVar.mDuckDuckGoContainer.stopDrawable, null);

                BusProvider.getInstance().post(new ShowAutoCompleteResultsEvent(s.length()>0));
                if(DDGControlVar.isAutocompleteActive) {
                    DDGControlVar.mDuckDuckGoContainer.tempAdapter.getFilter().filter(s);
                } else {
                    DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter.getFilter().filter(s);
                }
			}

			public void afterTextChanged(Editable arg0) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
	}

    private void initializeContainer() {
        DDGControlVar.mDuckDuckGoContainer = new DuckDuckGoContainer();

        DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
        DDGControlVar.mDuckDuckGoContainer.currentScreen = DDGControlVar.START_SCREEN;
        DDGControlVar.mDuckDuckGoContainer.currentFragmentTag = getTagByScreen(DDGControlVar.mDuckDuckGoContainer.currentScreen);
        DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;

        //DDGControlVar.mDuckDuckGoContainer.stopDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.stop);
        DDGControlVar.mDuckDuckGoContainer.stopDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.cross);
//    		DDGControlVar.mDuckDuckGoContainer.reloadDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.reload);
        DDGControlVar.mDuckDuckGoContainer.progressDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.page_progress);
        DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.searchfield);
        DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable.setAlpha(150);

        DDGControlVar.mDuckDuckGoContainer.historyAdapter = new MultiHistoryAdapter(this);
		BusProvider.getInstance().post(new LeftMenuSetAdapterEvent());

        DDGControlVar.mDuckDuckGoContainer.acAdapter = new AutoCompleteResultsAdapter(this);
        DDGControlVar.mDuckDuckGoContainer.tempAdapter = new TempAutoCompleteResultsAdapter(this);
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter = new RecentResultCursorAdapter(this, DDGApplication.getDB().getCursorSearchHistory());
    }

    // Assist action is better known as Google Now gesture
    private boolean isLaunchedWithAssistAction(){
        return getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_ASSIST);
    }

    private void showNewSourcesDialog() {
        if(PreferencesManager.shouldShowNewSourcesDialog()){
            new NewSourcesDialogBuilder(this).show();
            PreferencesManager.newSourcesDialogWasShown();
        }
    }
	
	public void clearSearchBar() {
		getSearchField().setText("");
    	getSearchField().setCompoundDrawables(null, null, null, null);
		//getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable);//aaa
	}
	
	public void setSearchBarText(String text) {
        DDGControlVar.mDuckDuckGoContainer.currentUrl = text;
        if(text.startsWith("https://")) {
            text = text.replace("https://", "");
        } else if(text.startsWith("http://")) {
            text = text.replace("http://", "");
        }
        if(text.startsWith("www.")) {
            text = text.replace("www.", "");
        }
		getSearchField().setFocusable(false);
		getSearchField().setFocusableInTouchMode(false);
		getSearchField().setText(text);
		getSearchField().setFocusable(true);
		getSearchField().setFocusableInTouchMode(true);
	}
	
	private void resetScreenState() {
		clearSearchBar();
		DDGControlVar.currentFeedObject = null;
		DDGControlVar.mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_BROWSE;
        resetSearchBar();
	}

    private void resetSearchBar() {
        //searchBar.setBackgroundResource(R.color.topbar_background);
        dropShadowDivider.setVisibility(View.VISIBLE);
    }

    public void showAllFragments() {
        Log.e("aaa", "show all fragments");
        if(fragmentManager.getFragments()!=null && fragmentManager.getFragments().size()!=0) {
            for (Fragment tempfragment : fragmentManager.getFragments()) {
                if(tempfragment!=null) {
                    Log.e("aaa", "fragment: " + tempfragment.getTag() + " - visible: " + tempfragment.isVisible());
                }
            }
        } else {
            Log.e("aaa", "all fragments == null");
        }
    }
	
	/**
	 * Displays given screen (stories, saved, settings etc.)
	 * 
	 * @param screenToDisplay Screen to display
	 * @param clean Whether screen state (searchbar, browser etc.) states will get cleaned
	 */
	public void displayScreen(SCREEN screenToDisplay, boolean clean, boolean displayHomeScreen) {
        Log.e("aaa", "-------- display screen: "+screenToDisplay);
        // asd display

        Fragment fragment = null;
        String tag = "";

        if(clean) {
			//resetScreenState();
		}
		
	    // control which screen is shown & configure related views
			
		switch(screenToDisplay) {
			case SCR_STORIES:
                resetScreenState();
                stopAction();

                // ensures feed refresh every time user switches to Stories screen
                DDGControlVar.hasUpdatedFeed = false;

                displayFeedCore();
                clearLeftSelect();

                fragment = new FeedFragment();
                tag = FeedFragment.TAG;

				//displayNewsFeed();
				break;
			case SCR_RECENTS:
                resetScreenState();
                clearLeftSelect();

                fragment = new RecentsFragment();
                tag = RecentsFragment.TAG;

                //displayRecents();
				break;
            case SCR_WEBVIEW:
                setMainButtonHome();
                resetSearchBar();
                //setSearchBarText(DDGControlVar.mDuckDuckGoContainer.currentUrl);

                fragment = new WebFragment();
                tag = WebFragment.TAG;
                //displayWebView();
                break;
            case SCR_SEARCH_HOME_PAGE:
                resetScreenState();
                fragment = new SearchFragment();
                tag = SearchFragment.TAG_HOME_PAGE;
                //displaySearch();

                break;
			case SCR_SAVED:
                resetScreenState();
                clearLeftSelect();

                fragment = new FavoriteFragment();
                tag = FavoriteFragment.TAG;
				//displaySaved();
				break;
            case SCR_ABOUT:

                fragment = new AboutFragment();
                tag = AboutFragment.TAG;
                //displayAbout();
                break;
            case SCR_HELP:
                resetScreenState();
                fragment = new HelpFeedbackFragment();
                tag = HelpFeedbackFragment.TAG;
                //displayHelp();
                break;
            case SCR_SETTINGS:
                fragment = new PrefFragment();
                tag = PrefFragment.TAG;
                //displaySettings();
                break;
            default:
				break;
		}

        if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENTS &&
                !screenToDisplay.equals(SCREEN.SCR_RECENTS)){
            BusProvider.getInstance().post(new LeftMenuSetRecentVisibleEvent());
        }
	    if(!DDGControlVar.mDuckDuckGoContainer.webviewShowing && screenToDisplay!=SCREEN.SCR_SEARCH) {
			//DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;
			//DDGControlVar.mDuckDuckGoContainer.currentScreen = screenToDisplay;
		}

        if(!tag.equals("")) {
            changeFragment(fragment, tag, displayHomeScreen);
        }
	}

    public void displayScreen(SCREEN screenToDisplay, boolean clean) {
        displayScreen(screenToDisplay, clean, false);
    }
	
	private void displayHomeScreen() {
        Log.e("aaa", "-------------------------display home screen");

        displayScreen(DDGControlVar.START_SCREEN, true, true);
        /*aaa
		if(DDGControlVar.mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_SEARCH
				|| DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH
                || DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED_FEED
                || DDGControlVar.START_SCREEN == SCREEN.SCR_DUCKMODE) {
            keyboardService.showKeyboard(getSearchField());
		}*/
        if(DDGControlVar.mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_SEARCH
                || DDGControlVar.START_SCREEN == SCREEN.SCR_SEARCH_HOME_PAGE) {
                //|| DDGControlVar.START_SCREEN == SCREEN.SCR_RECENTS
                //|| DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED) {
            keyboardService.showKeyboard(getSearchField());
        }
        DDGControlVar.mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_BROWSE;
	}

	@Override
	public void onResume() {
		super.onResume();
		BusProvider.getInstance().register(this);
		
        DDGUtils.displayStats.refreshStats(this);

		// update feeds
		// https://app.asana.com/0/2891531242889/2858723303746
		DDGControlVar.hasUpdatedFeed = false;
		
		// check autocomplete 
		if(!DDGControlVar.isAutocompleteActive) {
			//getSearchField().setAdapter(null);
		}
		else {
	        //getSearchField().setAdapter(DDGControlVar.mDuckDuckGoContainer.acAdapter);
            //getSearchField().setAdapter(DDGControlVar.mDuckDuckGoContainer.tempAdapter);//aaa adapter
		}
		
		if(DDGControlVar.includeAppsInSearch && !DDGControlVar.hasAppsIndexed) {
			// index installed apps
			new ScanAppsTask(getApplicationContext()).execute();
			DDGControlVar.hasAppsIndexed = true;
		}

		// global search intent
        Intent intent = getIntent(); 
        
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			intent.setAction(Intent.ACTION_MAIN);
			String query = intent.getStringExtra(SearchManager.QUERY);
			setSearchBarText(query);
			BusProvider.getInstance().post(new WebViewSearchWebTermEvent(query));
		}
		else if(intent.getBooleanExtra("widget", false)) {
            if(!getSearchField().getText().toString().equals("")) {
                clearSearchBar();
            }
			displayScreen(DDGControlVar.START_SCREEN, true);
            keyboardService.showKeyboard(getSearchField());
		}
        else if(Intent.ACTION_VIEW.equals(intent.getAction())) {
            searchOrGoToUrl(intent.getDataString());
        }
		else if(DDGControlVar.mDuckDuckGoContainer.webviewShowing){
            keyboardService.hideKeyboard(getSearchField());
            //todo check if fragment is visible or not
            Fragment fragment = fragmentManager.findFragmentByTag(WebFragment.TAG);
            if(fragmentManager.findFragmentByTag(WebFragment.TAG)== null || !fragment.isVisible()) {
                displayScreen(SCREEN.SCR_WEBVIEW, false);
            }
		}
        else if(isLaunchedWithAssistAction()){
            keyboardService.showKeyboard(getSearchField());
        }

        checkForCrashes();
	}

	@Override
	public void onPause() {
		super.onPause();
		
		BusProvider.getInstance().unregister(this);

        if(DDGControlVar.mustClearCacheAndCookies) {
			BusProvider.getInstance().post(new WebViewClearCacheAndCookiesEvent());
            DDGControlVar.mustClearCacheAndCookies = false;
        }

		PreferencesManager.saveReadArticles();
		
		// XXX keep these for low memory conditions
		AppStateManager.saveAppState(sharedPreferences, DDGControlVar.mDuckDuckGoContainer, DDGControlVar.currentFeedObject);
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
        Log.e("aaa", "on back pressed");
        if(DDGControlVar.START_SCREEN!=SCREEN.SCR_SEARCH && isFragmentVisible(SearchFragment.TAG)) {
            removeSearchFragment();
        }
		else if((DDGControlVar.mDuckDuckGoContainer.currentScreen == SCREEN.SCR_WEBVIEW
				|| DDGControlVar.mDuckDuckGoContainer.webviewShowing || isFragmentVisible(WebFragment.TAG))) {
			BusProvider.getInstance().post(new WebViewBackPressActionEvent());
		}
		// main feed showing & source filter is active
		else if(DDGControlVar.targetSource != null){
			BusProvider.getInstance().post(new FeedCancelSourceFilterEvent());
		}
        // main feed showing & category filter is active
        else if(DDGControlVar.targetCategory != null) {
            BusProvider.getInstance().post(new FeedCancelCategoryFilterEvent());
        }
        else if(fragmentManager.getBackStackEntryCount()==1) {
            finish();
        }
		else {
			DDGControlVar.hasUpdatedFeed = false;
			super.onBackPressed();
		}
	}

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {

        if(DDGControlVar.mDuckDuckGoContainer.webviewShowing && fragmentManager.findFragmentByTag(WebFragment.TAG)!=null && fragmentManager.findFragmentByTag(WebFragment.TAG).isVisible()) {
            if(openingMenu!=null) {
                openingMenu.close();
                BusProvider.getInstance().post(new WebViewOpenMenuEvent(toolbar));
            }
            return false;
        }
        return super.onMenuOpened(featureId, menu);
    }

    private Menu openingMenu = null;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.openingMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_stories:
                actionStories();
                return true;
            case R.id.action_favorites:
                actionFavorites();
                return true;
            case R.id.action_recents:
                actionRecents();
                return true;
            case R.id.action_settings:
                actionSettings();
                return true;
            case R.id.action_help_feedback:
                actionHelpFeedback();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setHomeButton(boolean visible) {
        ImageButton homeButton = (ImageButton) actionBar.getCustomView().findViewById(R.id.home);
        actionBar.getCustomView().findViewById(R.id.bang).setVisibility(View.GONE);
        if(visible) {
            homeButton.setVisibility(View.VISIBLE);
            homeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayHomeScreen();
                }
            });
        } else {
            homeButton.setVisibility(View.GONE);
        }
        setActionBarMarginStart(!visible);
    }

    private void setHomeButtonMarginTop(boolean visible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionBar.getCustomView().findViewById(R.id.home).getLayoutParams();
        int padding = 0;
        if(visible) {
            padding = (int) getResources().getDimension(R.dimen.actionbar_margin);
        }
        params.topMargin = padding;
    }

    private void setBangButton() {
        actionBar.getCustomView().findViewById(R.id.home).setVisibility(View.GONE);

        ImageButton bang = (ImageButton) findViewById(R.id.bang);
        bang.setVisibility(View.VISIBLE);
        bang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                //Toast.makeText(DuckDuckGo.this, "TO DO", Toast.LENGTH_SHORT).show();
                keyboardService.showKeyboard(getSearchField());
                getSearchField().addBang();
            }
        });
        setActionBarMarginStart(false);
    }

    private void hasOverflowButtonVisible(boolean visible) {
        int endMargin = 0;
        if(visible) {
            endMargin = (int)getResources().getDimension(R.dimen.actionbar_overflow_width);
        }
        toolbar.setContentInsetsRelative(0, endMargin);
        setActionBarMarginEnd(!visible);
    }

    private void setActionBarMarginBottom(boolean visible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchFieldContainer.getLayoutParams();
        int margin = 0;
        if(visible) {
            margin = (int) getResources().getDimension(R.dimen.actionbar_margin);
        }
        params.bottomMargin = margin;
    }

    private void setActionBarMarginStart(boolean visible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchFieldContainer.getLayoutParams();
        int margin = 0;
        if(visible) {
            margin = (int) getResources().getDimension(R.dimen.actionbar_margin);
        }
        params.leftMargin = margin;
    }

    private void setActionBarMarginEnd(boolean visible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchFieldContainer.getLayoutParams();
        int margin = 0;
        if(visible) {
            margin = (int) getResources().getDimension(R.dimen.actionbar_margin);
        }
        params.rightMargin = margin;
    }

    private void setActionBarShadow(boolean visible) {
        if(visible) {
            dropShadowDivider.setVisibility(View.VISIBLE);
        } else {
            dropShadowDivider.setVisibility(View.GONE);
        }
    }

    private void setStandardActionBarHeight(boolean normal) {
        int height = 0;
        if(normal) {
            height = (int) getResources().getDimension(R.dimen.actionbar_height);
        } else {
            height = (int) getResources().getDimension(R.dimen.actionbar_height_low);
        }
        //toolbar.setMinimumHeight(height);
        toolbar.getLayoutParams().height = height;
    }

    private void actionStories() {
        displayScreen(SCREEN.SCR_STORIES, true);
    }

    private void actionFavorites() {
        displayScreen(SCREEN.SCR_SAVED, true);
    }

    private void actionRecents() {
        displayScreen(SCREEN.SCR_RECENTS, true);
    }

    private void actionHelpFeedback(){
        displayScreen(SCREEN.SCR_HELP, false);
    }

    private void actionSettings() {
        displayScreen(SCREEN.SCR_SETTINGS, false);
    }

	public void reloadAction() {
		DDGControlVar.mCleanSearchBar = false;
        DDGControlVar.mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int) Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth() / 1.5), (int) Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight() / 1.5));
		getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : DDGControlVar.mDuckDuckGoContainer.stopDrawable, null);

		BusProvider.getInstance().post(new WebViewReloadActionEvent());
	}

	private void stopAction() {
        //Log.e("aaa", "stop action");
		DDGControlVar.mCleanSearchBar = true;
    	getSearchField().setText("");

    	// This makes a little (X) to clear the search bar.
    	getSearchField().setCompoundDrawables(null, null, null, null);
    	//getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable);//aaa
	}

	public void searchOrGoToUrl(String text, SESSIONTYPE sessionType) {
        displayScreen(SCREEN.SCR_WEBVIEW, false);
		BusProvider.getInstance().post(new WebViewSearchOrGoToUrlEvent(text, sessionType));
	}

	public void searchOrGoToUrl(String text) {
		searchOrGoToUrl(text, SESSIONTYPE.SESSION_BROWSE);
	}

	public void clearRecentSearch() {
		DDGControlVar.mDuckDuckGoContainer.historyAdapter.sync();
	}

	private void clearLeftSelect() {
		BusProvider.getInstance().post(new LeftMenuClearSelectEvent());
	}
	
	/**
	 * main method that triggers display of Preferences screen or fragment
	 */
	private void displaySettings() {
        //Log.e("aaa", "display settings");
        DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;

        changeFragment(new PrefFragment(), PrefFragment.TAG);
	}

	/**
	 * Method that switches visibility of views for Home or Saved feed
	 */
	private void displayFeedCore() {		
    	// main view visibility changes and keep feed updated
		//shareButton.setVisibility(View.GONE);
    	DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
	}
	
	public void displayNewsFeed(){
		resetScreenState();
		stopAction();
		
		// left side menu visibility changes
		//BusProvider.getInstance().post(new LeftMenuChangeVisibilityEvent());
    	
    	// adjust "not recording" indicator
		//BusProvider.getInstance().post(new LeftMenuHistoryDisabledEvent());
    	
    	// ensures feed refresh every time user switches to Stories screen
    	DDGControlVar.hasUpdatedFeed = false;
		
		displayFeedCore();
		clearLeftSelect();

		if(feedFragment==null) {
			feedFragment = new FeedFragment();
		}
		if(!feedFragment.isVisible()) {
			//changeFragment(feedFragment, FeedFragment.TAG);
		}
        changeFragment(new FeedFragment(), FeedFragment.TAG);

    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_STORIES){
    		//DDGControlVar.homeScreenShowing = true;

    	}
    	else {
    		//DDGControlVar.homeScreenShowing = false;
        }
	}
	
	public void displaySaved(){
		resetScreenState();
		
		// left side menu visibility changes
		//BusProvider.getInstance().post(new LeftMenuChangeVisibilityEvent());//aaa
    	
		//shareButton.setVisibility(View.GONE);
    	DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
		clearLeftSelect();

		//savedFragment = new SavedFragment();

		if(!favoriteFragment.isVisible()) {
			//changeFragment(favoriteFragment, FavoriteFragment.TAG);
            //changeFragment(savedFragment, StoredItemsFragment.SAVED_TAG);
		}
        changeFragment(new FavoriteFragment(), FavoriteFragment.TAG);
    	//aaa
    	//if(DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED_FEED){
        if(DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED){
    		//DDGControlVar.homeScreenShowing = true;
    		//setMainButtonMenu();//aaa
			//BusProvider.getInstance().post(new LeftMenuSetHomeSelectedEvent(true));//aaa
    	}
    	else {
    		//DDGControlVar.homeScreenShowing = false;
			//BusProvider.getInstance().post(new LeftMenuSetSavedSelectedEvent(true));//aaa
            //setHomeButton();//aaa
    	}
	}
	
	public void displayRecents(){
		resetScreenState(); 
		
		// left side menu visibility changes
		BusProvider.getInstance().post(new LeftMenuChangeVisibilityEvent());
		
    	// main view visibility changes
		//shareButton.setVisibility(View.GONE);
    	DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
		
		clearLeftSelect();

        if(recentsFragment==null) {
            recentsFragment = new RecentsFragment();
        }

        if(!recentsFragment.isVisible()) {
            //changeFragment(recentsFragment, RecentsFragment.TAG);
        }
        changeFragment(new RecentsFragment(), RecentsFragment.TAG);
    	//aaa
    	//if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH){
        if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENTS){
    		//DDGControlVar.homeScreenShowing = true;
    		//setMainButtonMenu();//aaa
			//BusProvider.getInstance().post(new LeftMenuSetHomeSelectedEvent(true));//aaa
    	}
    	else {
    		//DDGControlVar.homeScreenShowing = false;
            //setHomeButton();//aaa
    	}
	}

    public void displaySearch() {
        //resetScreenState();
        //DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;

        //changeFragment(searchFragment, SearchFragment.TAG);
        if(!searchFragment.isVisible()) {
            searchFragment = new SearchFragment();
            if(false && DDGControlVar.START_SCREEN!=SCREEN.SCR_SEARCH) {
                //addFragment(searchFragment, SearchFragment.TAG);
            } else {
                //changeFragment(searchFragment, SearchFragment.TAG);
            }
        }
        changeFragment(new SearchFragment(), SearchFragment.TAG);
    }

    public void displayAbout() {
        resetScreenState();//aaa to check
        DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;

        changeFragment(new AboutFragment(), AboutFragment.TAG);
    }

    public void displayHelp() {
        resetScreenState();//aaa to check
        DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;

        changeFragment(new HelpFeedbackFragment(), HelpFeedbackFragment.TAG);
    }

    private void hideSearchBarBackground() {
        TypedArray styledAttributes = getTheme().obtainStyledAttributes(R.style.DDGTheme, new int[]{R.attr.searchBarBackground});
        searchBar.setBackgroundResource(styledAttributes.getResourceId(0,0));
        dropShadowDivider.setVisibility(View.GONE);
    }

    public void displayWebView() {
		// loading something in the browser - set home icon
		//DDGControlVar.homeScreenShowing = false;
		setMainButtonHome();
        resetSearchBar();/*
        if(searchFragment.isVisible()) {
            removeFragment(searchFragment, SearchFragment.TAG);
        }*/
		if (true || !DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
			//shareButton.setVisibility(View.VISIBLE);

			webFragment = new WebFragment();

			if(!webFragment.isVisible()) {
				changeFragment(webFragment, WebFragment.TAG);

				//DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;
				//DDGControlVar.mDuckDuckGoContainer.currentScreen = SCREEN.SCR_WEBVIEW;
			}

			//DDGControlVar.mDuckDuckGoContainer.webviewShowing = true;
		}
	}

    private void changeFragment(Fragment newFragment, String newTag) {
        changeFragment(newFragment, newTag, false);
    }

	private void changeFragment(Fragment newFragment, String newTag, boolean displayHomeScreen) {
        Fragment searchFragment = fragmentManager.findFragmentByTag(SearchFragment.TAG);
        if(searchFragment!=null && DDGControlVar.START_SCREEN!=SCREEN.SCR_SEARCH) {
            removeSearchFragment();
        }

        boolean backState = fragmentManager.popBackStackImmediate(newTag, 0);

        if(displayHomeScreen && fragmentManager.getBackStackEntryCount()>1) {
            List<Fragment> fragments = fragmentManager.getFragments();
            FragmentTransaction removeTransaction = fragmentManager.beginTransaction();
            for(Fragment f : fragments) {
                if(f!=null) {
                    removeTransaction.remove(f);
                    fragmentManager.popBackStack();
                }
            }
            removeTransaction.commit();
            fragmentManager.executePendingTransactions();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(!backState && fragmentManager.findFragmentByTag(newTag)==null) {
            Fragment currentFragment = fragmentManager.findFragmentByTag(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
            if(currentFragment!=null && currentFragment.isAdded() && currentFragment.isVisible()) {
                transaction.hide(currentFragment);
            }
            transaction.add(fragmentContainer.getId(), newFragment, newTag);
            if(!newFragment.isVisible()) {
                transaction.show(newFragment);
            }
            transaction.addToBackStack(newTag);
            transaction.commit();
            fragmentManager.executePendingTransactions();
        }
	}

    public void removeSearchFragment() {
        Fragment searchFragment = fragmentManager.findFragmentByTag(SearchFragment.TAG);
        Fragment currentFragment = fragmentManager.findFragmentByTag(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
        if(searchFragment!=null) {
            FragmentTransaction transiction = fragmentManager.beginTransaction();
            transiction.remove(searchFragment);
            if(currentFragment!=null && !currentFragment.isVisible()) {
                transiction.show(currentFragment);
            }
            transiction.commit();
            fragmentManager.executePendingTransactions();
        }

        updateActionBar(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
    }

    public void addSearchFragment() {
        Log.e("aaa", "inside add search fragment");
        Log.e("aaa", "current tag: "+DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
        Fragment searchFragment = fragmentManager.findFragmentByTag(SearchFragment.TAG);
        Fragment searchFragmentHomePage = fragmentManager.findFragmentByTag(SearchFragment.TAG_HOME_PAGE);
        Fragment currentFragment = fragmentManager.findFragmentByTag(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);

        if(searchFragmentHomePage!=null && (fragmentManager.getBackStackEntryAt(0).getName().equals(SearchFragment.TAG_HOME_PAGE) && fragmentManager.getBackStackEntryCount()>1)) {
            //return;
        }

        if(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(SearchFragment.TAG_HOME_PAGE)) {
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(searchFragment==null) {
            transaction.add(fragmentContainer.getId(), new SearchFragment(), SearchFragment.TAG);
        } else if(!searchFragment.isVisible()) {
            transaction.show(searchFragment);
        }
        if(currentFragment!=null && currentFragment.isVisible()) {
            transaction.hide(currentFragment);
        }
        transaction.commit();
        fragmentManager.executePendingTransactions();
        updateActionBar(SearchFragment.TAG);
    }

    public boolean isFragmentVisible(String tag) {
        return fragmentManager.findFragmentByTag(tag)!=null && fragmentManager.findFragmentByTag(tag).isVisible();
    }

    public void onClick(View view) {//aaa toremove
		if (view.equals(mainButton)) {
			handleHomeSettingsButtonClick();
		}/*
		else if (view.equals(shareButton)) {
			BusProvider.getInstance().post(new HandleShareButtonClickEvent());
		}*/
	}

	private void handleLeftHomeTextViewClick() {//aaa to remove
					
		if (DDGControlVar.mDuckDuckGoContainer.webviewShowing) {

			//We are going home!
			//mainWebView.clearHistory();
			//mainWebView.clearView();
			clearSearchBar();
			DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
		}
		
		displayHomeScreen();
	}

	private void handleHomeSettingsButtonClick() {//aaa to remove
        keyboardService.hideKeyboard(getSearchField());
		
		if(DDGControlVar.homeScreenShowing){
			//drawer.open();
		}
		else {
			// going home
			displayHomeScreen();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {//aaa to remove
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PREFERENCES_RESULT){
			if (resultCode == RESULT_OK) {
                boolean clearWebCache = data.getBooleanExtra("mustClearWebCache", false);
                if(clearWebCache){
					BusProvider.getInstance().post(new WebViewClearCacheEvent());
                }
				boolean clearedHistory = data.getBooleanExtra("hasClearedHistory",false);
				if(clearedHistory){
					clearRecentSearch();
				}
                boolean startOrbotCheck = data.getBooleanExtra("startOrbotCheck",false);
                if(startOrbotCheck){
                    searchOrGoToUrl(getString(R.string.OrbotCheckSite));
                }
                boolean switchTheme = data.getBooleanExtra("switchTheme", false);
                if(switchTheme){
                    Intent intent = new Intent(getApplicationContext(), DuckDuckGo.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                
                if(DDGControlVar.homeScreenShowing) {
                	displayHomeScreen();
                }
			}
		}
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
	       // return page container, holding all non-view data
	       return DDGControlVar.mDuckDuckGoContainer;
	}
    
	@Override
	protected void onSaveInstanceState(Bundle outState)	{
		AppStateManager.saveAppState(outState, DDGControlVar.mDuckDuckGoContainer, DDGControlVar.currentFeedObject);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		
		AppStateManager.recoverAppState(savedInstanceState, DDGControlVar.mDuckDuckGoContainer, DDGControlVar.currentFeedObject);
		String feedId = AppStateManager.getCurrentFeedObjectId(savedInstanceState);
		
		clearLeftSelect();

		BusProvider.getInstance().post(new LeftMenuMarkSelectedEvent(DDGControlVar.mDuckDuckGoContainer.currentScreen));
		
		if(feedId != null && feedId.length() != 0) {
			FeedObject feedObject = DDGApplication.getDB().selectFeedById(feedId);
			if(feedObject != null) {
				DDGControlVar.currentFeedObject = feedObject;
			}
		}

        updateActionBar(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);

		if(DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
			return;
		}
		
		displayScreen(DDGControlVar.mDuckDuckGoContainer.currentScreen, true);
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
			//drawer.close();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	private void initFragments() {
		webFragment = new WebFragment();
		favoriteFragment = new FavoriteFragment();
        recentsFragment = new RecentsFragment();
        searchFragment = new SearchFragment();
        Fragment fragment = null;
        fragment = fragmentManager.findFragmentByTag(FeedFragment.TAG);
        if(fragment==null) {
            feedFragment = new FeedFragment();
        } else {
            feedFragment = (FeedFragment) fragment;
        }

	}

	public DDGAutoCompleteTextView getSearchField() {
		return searchField;
	}

    private void checkForCrashes() {
        if(DDGApplication.isIsReleaseBuild())
            return;
        //CrashManager.register(this, DDGConstants.HOCKEY_APP_ID);
    }

    private void checkForUpdates() {
        if(DDGApplication.isIsReleaseBuild())
            return;
        //UpdateManager.register(this, DDGConstants.HOCKEY_APP_ID);
    }

	@Subscribe
	public void onDeleteStoryInHistoryEvent(DeleteStoryInHistoryEvent event) {//left menu
		final long delResult = DDGApplication.getDB().deleteHistoryByFeedId(event.feedObjectId);
		if(delResult != 0) {							
			syncAdapters();
		}
		Toast.makeText(this, R.string.ToastDeleteStoryInHistory, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onDeleteUrlInHistoryEvent(DeleteUrlInHistoryEvent event) {//left menu
		final long delHistory = DDGApplication.getDB().deleteHistoryByDataUrl(event.pageData, event.pageUrl);				
		if(delHistory != 0) {							
			syncAdapters();
		}	
		Toast.makeText(this, R.string.ToastDeleteUrlInHistory, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onReloadEvent(ReloadEvent event) {
		reloadAction();
	}
	
	@Subscribe
	public void onSaveSearchEvent(SaveSearchEvent event) {
		itemSaveSearch(event.pageData);
		syncAdapters();
		Toast.makeText(this, R.string.ToastSaveSearch, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onSaveStoryEvent(SaveStoryEvent event) {
		itemSaveFeed(event.feedObject, null);
		syncAdapters();
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
	public void onShareWebPageEvent(ShareWebPageEvent event) {//web fragment
		Sharer.shareWebPage(this, event.url, event.url);
	}
	
	@Subscribe
	public void onUnSaveSearchEvent(UnSaveSearchEvent event) {
		final long delHistory = DDGApplication.getDB().deleteSavedSearch(event.query);
		if(delHistory != 0) {							
			syncAdapters();
		}	
		Toast.makeText(this, R.string.ToastUnSaveSearch, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onUnSaveStoryEvent(UnSaveStoryEvent event) {
		final long delResult = DDGApplication.getDB().makeItemHidden(event.feedObjectId);
		if(delResult != 0) {							
			syncAdapters();
		}
		Toast.makeText(this, R.string.ToastUnSaveStory, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Handling both MainFeedItemSelectedEvent and SavedFeedItemSelectedEvent.
	 * (modify to handle independently when necessary)
	 * @param event
	 *//* feed fragment
	@Subscribe
	public void onFeedItemSelected(FeedItemSelectedEvent event) {//
		// close left nav if it's open
		/*if(drawer.isDrawerOpen(leftMenuView)) {
			drawer.closeDrawer(leftMenuView);
		}*//*
		drawer.close();
		displayWebView();
		//feedFragment.feedItemSelected(event.feedObject);
	}*/

	@Subscribe
	public void onMainFeedItemLongClick(MainFeedItemLongClickEvent event) {
		new MainFeedMenuDialog(this, event.feedObject).show();
	}
	
	@Subscribe
	public void onSavedFeedItemLongClick(SavedFeedItemLongClickEvent event) {
        new SavedStoryMenuDialog(this, event.feedObject).show();
    }
	
	@Subscribe
	public void onHistoryItemSelected(HistoryItemSelectedEvent event) {
        Log.e("aaa", "====== ERROR 7 - on history item selected");
        keyboardService.hideKeyboard(getSearchField());
		if(!webFragment.isVisible()) {
            displayScreen(SCREEN.SCR_WEBVIEW, false);
		}
		BusProvider.getInstance().post(new WebViewShowHistoryObjectEvent(event.historyObject));
	}
	
	@Subscribe
	public void onHistoryItemLongClick(HistoryItemLongClickEvent event) {//to both recent search fragment AND left recent
        if(event.historyObject.isFeedObject()) {
            new HistoryStoryMenuDialog(this, event.historyObject).show();
        }
        else{
            new HistorySearchMenuDialog(this, event.historyObject).show();
        }
	}

    @Subscribe
    public void onSavedSearchItemSelected(SavedSearchItemSelectedEvent event) {
        searchOrGoToUrl(event.query);
        syncAdapters();
    }

	@Subscribe
	public void onSavedSearchItemLongClick(SavedSearchItemLongClickEvent event) {
		new SavedSearchMenuDialog(this, event.query).show();
	}

	@Subscribe
	public void onRecentSearchPaste(RecentSearchPasteEvent event) {
        getSearchField().pasteQuery(event.query);
        keyboardService.showKeyboard(getSearchField());
	}

    @Subscribe
	public void onSuggestionPaste(SuggestionPasteEvent event) {
        getSearchField().pasteQuery(event.query);
	}
	
	@Subscribe
	public void onSavedSearchPaste(SavedSearchPasteEvent event) {
        getSearchField().pasteQuery(event.query);
        keyboardService.showKeyboard(getSearchField());
	}

	@Subscribe
	public void onDisplayScreenEvent(DisplayScreenEvent event) {
        displayScreen(event.screenToDisplay, event.clean);
	}

	@Subscribe
	public void onSearchBarClearEvent(SearchBarClearEvent event) {
		clearSearchBar();
	}

	@Subscribe
	public void onSearchBarSetTextEvent(SearchBarSetTextEvent event) {
		setSearchBarText(event.text);
	}

	@Subscribe
	public void onSearchBarAddClearTextDrawable(SearchBarAddClearTextDrawable event) {
		//getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable);//aaa
	}

	@Subscribe
	public void onSearchBarSetProgressEvent(SearchBarSetProgressEvent event) {
		DDGControlVar.mDuckDuckGoContainer.progressDrawable.setLevel(event.newProgress);
		getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.progressDrawable);
	}

	@Subscribe
	public void onRequestOpenWebPageEvent(RequestOpenWebPageEvent event) {
		searchOrGoToUrl(event.url, event.sessionType);
	}

	@Subscribe
	public void onLeftMenuHomeClickEvent(LeftMenuHomeClickEvent event) {
		handleLeftHomeTextViewClick();
	}

	@Subscribe
	public void onLeftMenuStoriesClickEvent(LeftMenuStoriesClickEvent event) {
		displayScreen(SCREEN.SCR_STORIES, false);
	}

	@Subscribe
	public void onLeftMenuSavedClickEvent(LeftMenuSavedClickEvent event) {
        displayScreen(SCREEN.SCR_SAVED, false);
	}

	@Subscribe
	public void onLeftMenuSettingsClickEvent(LeftMenuSettingsClickEvent event) {
		displaySettings();
	}

	@Subscribe
	public void onLeftMenuCloseEvent(LeftMenuCloseEvent event) {
	}

	@Subscribe
	public void onDismissBangPopupEvent(DismissBangPopupEvent event) {
//		if(bangButtonExplanationPopup!=null){
//			bangButtonExplanationPopup.dismiss();
//		}
	}

	@Subscribe
	public void onSetMainButtonHomeEvent(SetMainButtonHomeEvent event) {
		setMainButtonHome();
	}

	@Subscribe
	public void onSetMainButtonMenuEvent(SetMainButtonMenuEvent event) {
		setMainButtonMenu();
	}

	@Subscribe
	public void onStopActionEvent(StopActionEvent event) {
		stopAction();
	}

	@Subscribe
	public void onRequestSyncAdaptersEvent(RequestSyncAdaptersEvent event) {
		syncAdapters();
	}

    @Subscribe
    public void onConfirmDialogOkEvent(ConfirmDialogOkEvent event) {
        switch(event.action) {
            case DDGConstants.CONFIRM_CLEAR_HISTORY:
                DDGApplication.getDB().deleteHistory();
                clearRecentSearch();
                break;
            case DDGConstants.CONFIRM_CLEAR_COOKIES:
                DDGWebView.clearCookies();
                break;
            case DDGConstants.CONFIRM_CLEAR_WEB_CACHE:
                BusProvider.getInstance().post(new WebViewClearCacheEvent());
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onRemoveWebFragmentEvent(RemoveWebFragmentEvent event) {
        //Log.e("aaa", "remove web fragment");
        fragmentManager.popBackStackImmediate();
    }

    @Subscribe
    public void onSearchBarChangeEvent(SearchBarChangeEvent event) {
        //changeSearchBar(event.screen);
    }

    @Subscribe
    public void onTestEvent(AutoCompleteResultClickEvent event) {
        SuggestObject suggestObject = DDGControlVar.mDuckDuckGoContainer.tempAdapter.getItem(event.position );
        if (suggestObject != null) {
            SuggestType suggestType = suggestObject.getType();
            if(suggestType == SuggestType.TEXT) {
                if(PreferencesManager.getDirectQuery()){
                    String text = suggestObject.getPhrase().trim();
                    if(suggestObject.hasOnlyBangQuery()){
                        getSearchField().addTextWithTrailingSpace(suggestObject.getPhrase());
                    }else{
                        Log.e("aaa", "======= ERROR 8 - on test event");
                        keyboardService.hideKeyboard(getSearchField());
                        searchOrGoToUrl(text);
                    }
                }
            }
            else if(suggestType == SuggestType.APP) {
                DDGUtils.launchApp(DuckDuckGo.this, suggestObject.getSnippet());
            }
        }

    }
}
