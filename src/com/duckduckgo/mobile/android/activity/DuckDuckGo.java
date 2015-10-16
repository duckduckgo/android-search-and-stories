package com.duckduckgo.mobile.android.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.RecentResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.dialogs.NewSourcesDialogBuilder;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistorySearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistoryStoryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedSearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedStoryMenuDialog;
import com.duckduckgo.mobile.android.events.AutoCompleteResultClickEvent;
import com.duckduckgo.mobile.android.events.ConfirmDialogOkEvent;
import com.duckduckgo.mobile.android.events.DisplayHomeScreenEvent;
import com.duckduckgo.mobile.android.events.DisplayScreenEvent;
import com.duckduckgo.mobile.android.events.HistoryItemLongClickEvent;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.events.ReloadEvent;
import com.duckduckgo.mobile.android.events.RemoveWebFragmentEvent;
import com.duckduckgo.mobile.android.events.RequestOpenWebPageEvent;
import com.duckduckgo.mobile.android.events.RequestSyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.ShowAutoCompleteResultsEvent;
import com.duckduckgo.mobile.android.events.StopActionEvent;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewBackPressActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewClearCacheEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewItemMenuClickEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewOpenMenuEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewReloadActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewSearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewSearchWebTermEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewShowHistoryObjectEvent;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteStoryInHistoryEvent;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteUrlInHistoryEvent;
import com.duckduckgo.mobile.android.events.externalEvents.SearchExternalEvent;
import com.duckduckgo.mobile.android.events.externalEvents.SendToExternalBrowserEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelCategoryFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelSourceFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedItemSelectedEvent;
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.feedEvents.SavedFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.RecentSearchPasteEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.SavedSearchPasteEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.SuggestionPasteEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveStoryEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveStoryEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemLongClickEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemSelectedEvent;
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
import com.duckduckgo.mobile.android.fragment.SourcesFragment;
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
import com.duckduckgo.mobile.android.util.ReadArticlesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.Sharer;
import com.duckduckgo.mobile.android.util.SuggestType;
import com.duckduckgo.mobile.android.util.TorIntegration;
import com.duckduckgo.mobile.android.views.autocomplete.BackButtonPressedEventListener;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;
import com.duckduckgo.mobile.android.views.webview.DDGWebView;
import com.squareup.otto.Subscribe;

import java.util.List;

public class DuckDuckGo extends AppCompatActivity {
	protected final String TAG = "DuckDuckGo";
    private KeyboardService keyboardService;
	private FrameLayout activityContainer;
    private FrameLayout fragmentContainer;

	private FragmentManager fragmentManager;

    private Toolbar toolbar;

	private SharedPreferences sharedPreferences;
		
	public boolean savedState = false;
    private boolean backPressed = false;
    private boolean canCommitFragmentSafely = true;
    private boolean newIntent = false;
		
	private final int PREFERENCES_RESULT = 0;

    /**
     * save feed by object or by the feed id
     * 
     * @param feedObject
     * @param pageFeedId
     */
    public void itemSaveFeed(FeedObject feedObject, String pageFeedId) {
    	if(feedObject != null) {
    		if(DDGApplication.getDB().existsAllFeedById(feedObject.getId())) {
                DDGApplication.getDB().makeItemFavorite(feedObject.getId());
    		}
    		else {
                DDGApplication.getDB().insertFavorite(feedObject);
    		}
    	}
    	else if(pageFeedId != null && pageFeedId.length() != 0){
            DDGApplication.getDB().makeItemFavorite(pageFeedId);
    	}
    }
    
    public void itemSaveSearch(String title, String url) {
    	DDGApplication.getDB().insertSavedSearch(title, url);
    }
    
    public void syncAdapters() {
    	//DDGControlVar.mDuckDuckGoContainer.historyAdapter.sync();
    	BusProvider.getInstance().post(new SyncAdaptersEvent());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on create");
        canCommitFragmentSafely = true;

        keyboardService = new KeyboardService(this);

        showNewSourcesDialog();

        sharedPreferences = DDGApplication.getSharedPreferences();

		setContentView(R.layout.main);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        
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

        activityContainer = (FrameLayout) findViewById(R.id.activityContainer);
		fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        DDGActionBarManager.getInstance().init(this, this, toolbar);

        initSearchField();

		fragmentManager = getSupportFragmentManager();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.d(TAG, "Fragment Back Stack count: " + fragmentManager.getBackStackEntryCount());
                showAllFragments();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                    if (tag != null) {
                        if(!DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(tag)) {
                            DDGControlVar.mDuckDuckGoContainer.prevFragmentTag = DDGControlVar.mDuckDuckGoContainer.currentFragmentTag;
                        }
                        DDGControlVar.mDuckDuckGoContainer.currentFragmentTag = tag;
                        if (!tag.equals(WebFragment.TAG) && !DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
                            DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;
                        }
                        DDGControlVar.mDuckDuckGoContainer.currentScreen = DDGUtils.getScreenByTag(tag);
                        DDGControlVar.mDuckDuckGoContainer.webviewShowing = tag.equals(WebFragment.TAG);
                        DDGControlVar.homeScreenShowing = DDGControlVar.mDuckDuckGoContainer.currentScreen == DDGControlVar.START_SCREEN;

                        DDGActionBarManager.getInstance().updateActionBar(fragmentManager, tag, backPressed);
                        backPressed = false;
                    }
                    Log.e(TAG, "Fragment Back Stack current tag: " + DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
                    showAllFragments();
                }
            }
        });

		if(savedInstanceState==null) {
			displayHomeScreen();
		}

        // global search intent
        Intent intent = getIntent();
        processIntent(intent);
    }

    private void initSearchField() {
        getSearchField().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if(textView == getSearchField() && actionId != EditorInfo.IME_NULL) {
                    if(getSearchField().getTrimmedText()!=null && getSearchField().getTrimmedText().length()!=0) {
                        searchOrGoToUrl(getSearchField().getTrimmedText());
                    }
                }
                return false;
            }
        });

        getSearchField().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    int stackSize= fragmentManager.getBackStackEntryCount();
                    String tag = stackSize > 0? fragmentManager.getBackStackEntryAt(stackSize - 1).getName() : "";
                    Fragment f = fragmentManager.findFragmentByTag(tag);
                    if(f!= null && (f.getTag().equals(SearchFragment.TAG) || f.getTag().equals(SearchFragment.TAG_HOME_PAGE))) {
                        Log.d(TAG, "on focus change listener, DO NOT display search");
                    } else {
                        Log.d(TAG, "on focus change listener, MUST display search");
                        displayScreen(SCREEN.SCR_SEARCH, true);
                    }
                }
            }
        });

        getSearchField().setOnBackButtonPressedEventListener(new BackButtonPressedEventListener() {
            @Override
            public void onBackButtonPressed() {
                onBackPressed();
            }
        });

        // This makes a little (X) to clear the search bar.
        //DDGControlVar.mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int)Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight()/1.5));
        DDGControlVar.mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int)Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()), (int)Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight()));
        getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : DDGControlVar.mDuckDuckGoContainer.stopDrawable, null);

        getSearchField().setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    DDGControlVar.mCleanSearchBar = true;
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

                if(isFragmentVisible(SearchFragment.TAG) || isFragmentVisible(SearchFragment.TAG_HOME_PAGE)) {
                    BusProvider.getInstance().post(new ShowAutoCompleteResultsEvent(s.length() > 0));
                }
                if(DDGControlVar.isAutocompleteActive) {
                    DDGControlVar.mDuckDuckGoContainer.acAdapter.getFilter().filter(s);
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
        DDGControlVar.mDuckDuckGoContainer.currentFragmentTag = DDGUtils.getTagByScreen(DDGControlVar.mDuckDuckGoContainer.currentScreen);
        DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DDGControlVar.mDuckDuckGoContainer.stopDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.cross, getTheme());
            //DDGControlVar.mDuckDuckGoContainer.progressDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.page_progress, getTheme());
            DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.searchfield, getTheme());
        } else {
            DDGControlVar.mDuckDuckGoContainer.stopDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.cross);
            //DDGControlVar.mDuckDuckGoContainer.progressDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.page_progress);
            DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.searchfield);
        }
        DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable.setAlpha(150);

        //DDGControlVar.mDuckDuckGoContainer.historyAdapter = new MultiHistoryAdapter(this);

        DDGControlVar.mDuckDuckGoContainer.acAdapter = new AutoCompleteResultsAdapter(this);
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter = new RecentResultCursorAdapter(this, DDGApplication.getDB().getCursorSearchHistory());

        DDGControlVar.mDuckDuckGoContainer.torIntegration = new TorIntegration(this);
    }

    private void showNewSourcesDialog() {
        if(PreferencesManager.shouldShowNewSourcesDialog()){
            new NewSourcesDialogBuilder(this).show();
            PreferencesManager.newSourcesDialogWasShown();
        }
    }

    public void showAllFragments() {
        Log.d(TAG, "show all fragments");
        if(fragmentManager.getFragments()!=null && fragmentManager.getFragments().size()!=0) {
            for (Fragment fragment : fragmentManager.getFragments()) {
                if(fragment!=null) {
                    Log.d(TAG, "fragment: " + fragment.getTag() + " - visible: " + fragment.isVisible());
                }
            }
        }
    }
	
	/**
	 * Displays given screen (stories, saved, settings etc.)
	 * 
	 * @param screenToDisplay Screen to display
	 * @param clean Whether screen state (searchbar, browser etc.) states will get cleaned
	 */
	public void displayScreen(SCREEN screenToDisplay, boolean clean, boolean displayHomeScreen) {
        Log.d(TAG, "display screen: "+screenToDisplay);

        Fragment fragment = null;
        String tag = "";

		switch(screenToDisplay) {
			case SCR_STORIES:
                DDGActionBarManager.getInstance().resetScreenState();
                stopAction();

                // ensures feed refresh every time user switches to Stories screen
                DDGControlVar.hasUpdatedFeed = false;

                displayFeedCore();

                fragment = new FeedFragment();
                tag = FeedFragment.TAG;
				break;
			case SCR_RECENTS:
                DDGActionBarManager.getInstance().resetScreenState();

                fragment = new RecentsFragment();
                tag = RecentsFragment.TAG;

				break;
            case SCR_WEBVIEW:
                fragment = new WebFragment();
                tag = WebFragment.TAG;
                break;
            case SCR_SEARCH:
                fragment = new SearchFragment();
                tag = SearchFragment.TAG;

                break;
            case SCR_SEARCH_HOME_PAGE:
                DDGActionBarManager.getInstance().resetScreenState();
                fragment = new SearchFragment();
                tag = SearchFragment.TAG_HOME_PAGE;

                break;
			case SCR_FAVORITE:
                DDGActionBarManager.getInstance().resetScreenState();

                fragment = new FavoriteFragment();
                tag = FavoriteFragment.TAG;
				break;
            case SCR_ABOUT:

                fragment = new AboutFragment();
                tag = AboutFragment.TAG;
                break;
            case SCR_HELP:
                DDGActionBarManager.getInstance().resetScreenState();
                fragment = new HelpFeedbackFragment();
                tag = HelpFeedbackFragment.TAG;
                break;
            case SCR_SETTINGS:
                fragment = new PrefFragment();
                tag = PrefFragment.TAG;
                break;
            case SCR_SOURCES:
                fragment = new SourcesFragment();
                tag = SourcesFragment.TAG;
            default:
				break;
		}

        if(!tag.equals("")) {
            changeFragment(fragment, tag, displayHomeScreen);
        }
	}

    public void displayScreen(SCREEN screenToDisplay, boolean clean) {
        displayScreen(screenToDisplay, clean, false);
    }

    public void displayFirstWebScreen(String url, SESSIONTYPE sessionType) {
        if(url==null || url.length()<1) {
            return;
        }
        if(sessionType==null) sessionType = SESSIONTYPE.SESSION_BROWSE;
        Fragment fragment = WebFragment.newInstance(url, sessionType);
        String tag = WebFragment.TAG;
        changeFragment(fragment, tag);
    }
	
	private void displayHomeScreen() {
        Log.d(TAG, "display home screen");

        DDGControlVar.mDuckDuckGoContainer.currentUrl = "";
        displayScreen(DDGControlVar.START_SCREEN, true, true);
        DDGControlVar.mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_BROWSE;
	}

    private void processIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.setAction(Intent.ACTION_MAIN);
            String query = intent.getStringExtra(SearchManager.QUERY);
            DDGActionBarManager.getInstance().setSearchBarText(query);
            BusProvider.getInstance().post(new WebViewSearchWebTermEvent(query));
        }
        else if(intent.getBooleanExtra("widget", false)) {
            if(!getSearchField().getText().toString().equals("")) {
                DDGActionBarManager.getInstance().clearSearchBar();
            }
            if(!DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(SearchFragment.TAG)
                    && !DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(SearchFragment.TAG_HOME_PAGE)) {
                displayScreen(SCREEN.SCR_SEARCH, true);
            }
        }
        else if(Intent.ACTION_VIEW.equals(intent.getAction())) {
            searchOrGoToUrl(intent.getDataString());
        }
        else if(Intent.ACTION_ASSIST.equals(intent.getAction())){
            if(!DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(SearchFragment.TAG)
                    && !DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(SearchFragment.TAG_HOME_PAGE)) {
                displayScreen(SCREEN.SCR_SEARCH, true);
            }
        }
        else if(DDGControlVar.mDuckDuckGoContainer.webviewShowing){
            Fragment fragment = fragmentManager.findFragmentByTag(WebFragment.TAG);
            if(fragmentManager.findFragmentByTag(WebFragment.TAG)== null || !fragment.isVisible()) {
                displayScreen(SCREEN.SCR_WEBVIEW, false);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "on new intent: " + intent.toString());
        newIntent = true;
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TorIntegrationProvider.getInstance(this).prepareTorSettings();
        DDGControlVar.mDuckDuckGoContainer.torIntegration.prepareTorSettings();
        BusProvider.getInstance().register(this);
    }

	@Override
	public void onResume() {
		super.onResume();
        Log.d(TAG, "on resume");
		
        DDGUtils.displayStats.refreshStats(this);

		// update feeds
		// https://app.asana.com/0/2891531242889/2858723303746
		DDGControlVar.hasUpdatedFeed = false;

		if(DDGControlVar.includeAppsInSearch && !DDGControlVar.hasAppsIndexed) {
			// index installed apps
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
               new ScanAppsTask(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new ScanAppsTask(getApplicationContext()).execute();
            }
			DDGControlVar.hasAppsIndexed = true;
		}
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        canCommitFragmentSafely = true;
        if(newIntent) {
            processIntent(getIntent());
            newIntent = false;
        }
    }

	@Override
	public void onPause() {
		super.onPause();
        Log.d(TAG, "on pause");
        canCommitFragmentSafely = false;


        DDGActionBarManager.getInstance().dismissMenu();
		PreferencesManager.saveReadArticles();
		
		// XXX keep these for low memory conditions
		AppStateManager.saveAppState(sharedPreferences, DDGControlVar.mDuckDuckGoContainer, DDGControlVar.currentFeedObject);
	}
	
	@Override
	protected void onStop() {
		PreferencesManager.saveReadArticles();
		super.onStop();
        BusProvider.getInstance().unregister(this);
        Log.d(TAG, "on stop");
	}
	
	@Override
	protected void onDestroy() {
		DDGApplication.getImageCache().purge();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
        backPressed = true;
        if((DDGControlVar.mDuckDuckGoContainer.currentScreen == SCREEN.SCR_WEBVIEW
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
		else if(!isFinishing()) {
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

    private void onMenuItemClicked(MenuItem menuItem) {
        onMenuItemClicked(menuItem, null);
    }

    private void onMenuItemClicked(MenuItem menuItem, FeedObject feed) {
        switch(menuItem.getItemId()) {
            case R.id.action_stories:
                actionStories();
                break;
            case R.id.action_favorites:
                actionFavorites();
                break;
            case R.id.action_recents:
                actionRecents();
                break;
            case R.id.action_settings:
                actionSettings();
                break;
            case R.id.action_help_feedback:
                actionHelpFeedback();
                break;
            case R.id.action_add_favorite:
                itemSaveFeed(feed, null);
                syncAdapters();
                Toast.makeText(this, R.string.ToastSaveStory, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_remove_favorite:
                final long delResult = DDGApplication.getDB().makeItemUnfavorite(feed.getId());
                if(delResult != 0) {
                    syncAdapters();
                }
                Toast.makeText(this, R.string.ToastUnSaveStory, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_share:
                Sharer.shareStory(this, feed.getTitle(), feed.getUrl());
                break;
            case R.id.action_external:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(feed.getUrl()));
                DDGUtils.execIntentIfSafe(this, browserIntent);
                break;

        }

    }

    private void actionStories() {
        displayScreen(SCREEN.SCR_STORIES, true);
    }

    private void actionFavorites() {
        displayScreen(SCREEN.SCR_FAVORITE, true);
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
		DDGControlVar.mCleanSearchBar = true;
        DDGActionBarManager.getInstance().stopProgress();
    	getSearchField().setText("");

    	// This makes a little (X) to clear the search bar.
    	getSearchField().setCompoundDrawables(null, null, null, null);
	}

	public void searchOrGoToUrl(final String text, final SESSIONTYPE sessionType) {
        if(DDGControlVar.useExternalBrowser==DDGConstants.ALWAYS_INTERNAL) {
            if(fragmentManager.findFragmentByTag(WebFragment.TAG)==null) {
                displayFirstWebScreen(text, sessionType);
            } else {
                displayScreen(SCREEN.SCR_WEBVIEW, false);
                BusProvider.getInstance().post(new WebViewSearchOrGoToUrlEvent(text, sessionType));
            }

        } else {
            Fragment webFragment = fragmentManager.findFragmentByTag(WebFragment.TAG);
            if(webFragment==null) {
                webFragment = new WebFragment();
                ((WebFragment)webFragment).setContext(this);
                //fragmentManager.beginTransaction().add(fragmentContainer.getId(), webFragment, WebFragment.TAG).hide(webFragment).commit();
                //fragmentManager.executePendingTransactions();
            }
            ((WebFragment)webFragment).searchOrGoToUrl(text, sessionType);
        }
	}

	public void searchOrGoToUrl(String text) {
		searchOrGoToUrl(text, SESSIONTYPE.SESSION_BROWSE);
	}

	public void clearRecentSearch() {
        BusProvider.getInstance().post(new SyncAdaptersEvent());
	}

	/**
	 * Method that switches visibility of views for Home or Saved feed
	 */
	private void displayFeedCore() {		
    	// main view visibility changes and keep feed updated
    	DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
	}

    private void changeFragment(Fragment newFragment, String newTag) {
        changeFragment(newFragment, newTag, false);
    }

	private void changeFragment(Fragment newFragment, String newTag, boolean displayHomeScreen) {
        Log.d(TAG, "change fragment, new tag: " + newTag);
        Log.d(TAG, "new tag: " + newTag + " - current tag: " + DDGControlVar.mDuckDuckGoContainer.currentFragmentTag+" - prev tag: "+DDGControlVar.mDuckDuckGoContainer.prevFragmentTag);
        if(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(newTag) && !displayHomeScreen) {
            return;
        }

        Fragment searchFragment = fragmentManager.findFragmentByTag(SearchFragment.TAG);

        boolean backState = true;

        if(!newTag.equals(SearchFragment.TAG)) {
            if(!isFinishing() && canCommitFragmentSafely) {
                backState = fragmentManager.popBackStackImmediate(newTag, 0);
            }

            if (displayHomeScreen && fragmentManager.getBackStackEntryCount() > 1) {
                List<Fragment> fragments = fragmentManager.getFragments();
                FragmentTransaction removeTransaction = fragmentManager.beginTransaction();
                for (Fragment f : fragments) {
                    if (f != null) {
                        removeTransaction.remove(f);
                        fragmentManager.popBackStack();
                    }
                }
                if(!isFinishing()) {
                    removeTransaction.commit();
                    fragmentManager.executePendingTransactions();
                }
            }
        }



        if(newTag.equals(SearchFragment.TAG) || (!backState && fragmentManager.findFragmentByTag(newTag)==null)) {
            final Fragment currentFragment = fragmentManager.findFragmentByTag(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment f = fragmentManager.findFragmentByTag(newTag);
            if(newTag.equals(WebFragment.TAG) || newTag.equals(SourcesFragment.TAG) || newTag.equals(AboutFragment.TAG  )) {
                transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.empty, R.anim.empty, R.anim.slide_out_to_right);
            } else if(newTag.equals(PrefFragment.TAG) || newTag.equals(HelpFeedbackFragment.TAG)) {
                transaction.setCustomAnimations(R.anim.slide_in_from_bottom2, R.anim.empty, R.anim.empty, R.anim.slide_out_to_bottom2);
            } else if(newTag.equals(SearchFragment.TAG)) {
                transaction.setCustomAnimations(R.anim.slide_in_from_bottom2, R.anim.empty, R.anim.empty, R.anim.slide_out_to_bottom2);
            } else {
                transaction.setCustomAnimations(R.anim.empty_immediate, R.anim.empty, R.anim.empty_immediate, R.anim.empty_immediate);
            }
            if(true || f==null) {
                Log.d(TAG, "f==null, adding new fragment");
                transaction.add(fragmentContainer.getId(), newFragment, newTag);
            } else {
                Log.d(TAG, "f!=null, showing new fragment");
                transaction.show(f);
            }
            if(currentFragment!=null && currentFragment.isAdded()) {
                transaction.hide(currentFragment);
            }
            transaction.addToBackStack(newTag);
            if(canCommitFragmentSafely && !isFinishing()) {
                transaction.commit();
                fragmentManager.executePendingTransactions();
            }
        }
	}

    public boolean isFragmentVisible(String tag) {
        return fragmentManager.findFragmentByTag(tag)!=null && fragmentManager.findFragmentByTag(tag).isVisible();
    }

    public void feedItemSelected(FeedObject feedObject) {
        // keep a reference, so that we can reuse details while saving
        DDGControlVar.currentFeedObject = feedObject;
        DDGControlVar.mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_FEED;

        String url = feedObject.getUrl();
        if (url != null) {
            //if(!DDGApplication.getDB().existsVisibleFeedById(feedObject.getId())) {
            if(!DDGApplication.getDB().existsFavoriteFeedById(feedObject.getId())) {
                DDGApplication.getDB().insertFeedItem(feedObject);
                //BusProvider.getInstance().post(new RequestSyncAdaptersEvent());
                syncAdapters();

            } else {
                DDGApplication.getDB().insertFeedItemToHistory(feedObject.getTitle(), feedObject.getUrl(), feedObject.getType(), feedObject.getId());
                //BusProvider.getInstance().post(new RequestSyncAdaptersEvent());
                syncAdapters();
            }
            //BusProvider.getInstance().post(new RequestOpenWebPageEvent(url, SESSIONTYPE.SESSION_FEED));
            searchOrGoToUrl(url, SESSIONTYPE.SESSION_FEED);
        }
    }

    public void feedItemSelected(String feedId) {
        FeedObject feedObject = DDGApplication.getDB().selectFeedById(feedId);
        feedItemSelected(feedObject);
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
        canCommitFragmentSafely = false;
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		
		AppStateManager.recoverAppState(savedInstanceState, DDGControlVar.mDuckDuckGoContainer, DDGControlVar.currentFeedObject);
		String feedId = AppStateManager.getCurrentFeedObjectId(savedInstanceState);
		
		if(feedId != null && feedId.length() != 0) {
			FeedObject feedObject = DDGApplication.getDB().selectFeedById(feedId);
			if(feedObject != null) {
				DDGControlVar.currentFeedObject = feedObject;
			}
		}

        if(fragmentManager.getBackStackEntryCount()>1) {
            String tag = fragmentManager.getBackStackEntryAt(0).getName();
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(tag)).commit();
        }

        DDGActionBarManager.getInstance().updateActionBar(fragmentManager, DDGControlVar.mDuckDuckGoContainer.currentFragmentTag, false);
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		DDGUtils.displayStats.refreshStats(this);
		super.onConfigurationChanged(newConfig);
        DDGActionBarManager.getInstance().dismissMenu();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ( keyCode == KeyEvent.KEYCODE_MENU ) {
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	public DDGAutoCompleteTextView getSearchField() {
        return DDGActionBarManager.getInstance().getSearchField();
	}

    /**
     * Handling both MainFeedItemSelectedEvent and SavedFeedItemSelectedEvent.
     * (modify to handle independently when necessary)
     * @param event
     */
    @Subscribe
    public void onFeedItemSelected(FeedItemSelectedEvent event) {
        if(event.feedObject==null) {
            feedItemSelected(event.feedId);
        } else {
            feedItemSelected(event.feedObject);
        }
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
		itemSaveSearch(event.pageTitle, event.pageData);
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
    public void onSearchExternalEvent(SearchExternalEvent event) {
        DDGUtils.searchExternal(this, event.query);
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
        final long delResult = DDGApplication.getDB().makeItemUnfavorite(event.feedObjectId);
		if(delResult != 0) {							
			syncAdapters();
		}
		Toast.makeText(this, R.string.ToastUnSaveStory, Toast.LENGTH_SHORT).show();
	}

	@Subscribe
	public void onMainFeedItemLongClick(MainFeedItemLongClickEvent event) {
        if(toolbar.getVisibility()==View.VISIBLE) {
            toolbar.setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
        }
	}
	
	@Subscribe
	public void onSavedFeedItemLongClick(SavedFeedItemLongClickEvent event) {
        new SavedStoryMenuDialog(this, event.feedObject).show();
    }
	
	@Subscribe
	public void onHistoryItemSelected(HistoryItemSelectedEvent event) {
        if( DDGControlVar.useExternalBrowser==DDGConstants.ALWAYS_INTERNAL) {
            displayScreen(SCREEN.SCR_WEBVIEW, false);
            BusProvider.getInstance().post(new WebViewShowHistoryObjectEvent(event.historyObject));
        } else {
            WebFragment webFragment = (WebFragment) fragmentManager.findFragmentByTag(WebFragment.TAG);
            if(webFragment==null) {
                webFragment = new WebFragment();
            }
            webFragment.setContext(this);
            webFragment.showHistoryObject(event.historyObject);
        }

	}
	
	@Subscribe
	public void onHistoryItemLongClick(HistoryItemLongClickEvent event) {
        if(event.historyObject.isWebSearch()) {
            new HistorySearchMenuDialog(this, event.historyObject).show();
        }
        else{
            new HistoryStoryMenuDialog(this, event.historyObject).show();
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
	public void onRequestOpenWebPageEvent(RequestOpenWebPageEvent event) {
		searchOrGoToUrl(event.url, event.sessionType);
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
        if(!isFinishing() && canCommitFragmentSafely) {
            fragmentManager.popBackStackImmediate();
        }
    }

    @Subscribe
    public void onDisplayHomeScreenEvent(DisplayHomeScreenEvent event) {
        displayHomeScreen();
    }

    @Subscribe
    public void onAutoCompleteResultClickEvent(AutoCompleteResultClickEvent event) {
        SuggestObject suggestObject = DDGControlVar.mDuckDuckGoContainer.acAdapter.getItem(event.position );
        if (suggestObject != null) {
            SuggestType suggestType = suggestObject.getType();
            if(suggestType == SuggestType.TEXT) {
                if(PreferencesManager.getDirectQuery()){
                    String text = suggestObject.getPhrase().trim();
                    if(suggestObject.hasOnlyBangQuery()){
                        getSearchField().addTextWithTrailingSpace(suggestObject.getPhrase());
                    }else{
                        searchOrGoToUrl(text);
                    }
                }
            }
            else if(suggestType == SuggestType.APP) {
                DDGUtils.launchApp(DuckDuckGo.this, suggestObject.getSnippet());
            }
        }
    }

    @Subscribe
    public void onWebViewItemMenuClickEvent(WebViewItemMenuClickEvent event) {
        if(event.feed==null) {
            onOptionsItemSelected(event.item);
        } else {
            switch(event.item.getItemId()) {
                case R.id.action_add_favorite:
                    itemSaveFeed(event.feed, null);
                    syncAdapters();
                    Toast.makeText(this, R.string.ToastSaveStory, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_remove_favorite:
                    final long delFavResult = DDGApplication.getDB().makeItemUnfavorite(event.feed.getId());
                    if(delFavResult != 0) {
                        syncAdapters();
                    }
                    Toast.makeText(this, R.string.ToastUnSaveStory, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_remove_recent:
                    final long delRecResult = DDGApplication.getDB().deleteHistoryByFeedId(event.feed.getId());
                    if(delRecResult != 0) {
                        syncAdapters();
                    }
                    Toast.makeText(this, R.string.ToastDeleteStoryInHistory, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_share:
                    Sharer.shareStory(this, event.feed.getTitle(), event.feed.getUrl());
                    break;
                case R.id.action_external:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.feed.getUrl()));
                    DDGUtils.execIntentIfSafe(this, browserIntent);
                    break;
            }
        }
    }
}
