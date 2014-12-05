package com.duckduckgo.mobile.android.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.MultiHistoryAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.dialogs.NewSourcesDialogBuilder;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistorySearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistoryStoryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.MainFeedMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedSearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedStoryMenuDialog;
import com.duckduckgo.mobile.android.events.DismissBangPopupEvent;
import com.duckduckgo.mobile.android.events.DisplayScreenEvent;
import com.duckduckgo.mobile.android.events.HandleShareButtonClickEvent;
import com.duckduckgo.mobile.android.events.HistoryItemLongClickEvent;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.events.ReloadEvent;
import com.duckduckgo.mobile.android.events.RequestOpenWebPageEvent;
import com.duckduckgo.mobile.android.events.RequestSyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.SetMainButtonHomeEvent;
import com.duckduckgo.mobile.android.events.SetMainButtonMenuEvent;
import com.duckduckgo.mobile.android.events.StopActionEvent;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewBackPressActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewClearBrowserStateEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewClearCacheAndCookiesEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewClearCacheEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewReloadActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewSearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewSearchWebTermEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewShowHistoryObjectEvent;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteStoryInHistoryEvent;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteUrlInHistoryEvent;
import com.duckduckgo.mobile.android.events.externalEvents.SendToExternalBrowserEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelSourceFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCleanImageTaskEvent;
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.feedEvents.SavedFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.fontSizeEvents.FontSizeCancelScalingEvent;
import com.duckduckgo.mobile.android.events.fontSizeEvents.FontSizeOnProgressChangedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuChangeVisibilityEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuClearSelectEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuCloseEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuHistoryDisabledEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuHomeClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuMarkSelectedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSavedClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetAdapterEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetHomeSelectedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetRecentVisibleEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetSavedSelectedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetStoriesSelectedEvent;
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
import com.duckduckgo.mobile.android.events.searchBarEvents.SearchBarClearEvent;
import com.duckduckgo.mobile.android.events.searchBarEvents.SearchBarSetProgressEvent;
import com.duckduckgo.mobile.android.events.searchBarEvents.SearchBarSetTextEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareFeedEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareSearchEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareWebPageEvent;
import com.duckduckgo.mobile.android.fragment.DuckModeFragment;
import com.duckduckgo.mobile.android.fragment.FeedFragment;
import com.duckduckgo.mobile.android.fragment.RecentSearchFragment;
import com.duckduckgo.mobile.android.fragment.SavedFragment;
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
import com.duckduckgo.mobile.android.views.DDGDrawerLayout;
import com.duckduckgo.mobile.android.views.SeekBarHint;
import com.duckduckgo.mobile.android.views.WelcomeScreenView;
import com.duckduckgo.mobile.android.views.autocomplete.BackButtonPressedEventListener;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;
import com.duckduckgo.mobile.android.widgets.BangButtonExplanationPopup;
import com.squareup.otto.Subscribe;

//import net.hockeyapp.android.CrashManager;
//import net.hockeyapp.android.UpdateManager;

public class DuckDuckGo extends FragmentActivity implements OnClickListener {
	protected final String TAG = "DuckDuckGo";
    private KeyboardService keyboardService;

	private DDGAutoCompleteTextView searchField = null;
	//private HistoryListView leftRecentView = null;//drawer fragment

	private DDGDrawerLayout drawer;
	private View contentView = null;
	
	//private HistoryListView recentSearchView = null;//recent search fragment

	private ImageButton mainButton = null;
	private ImageButton bangButton = null;
	private ImageButton shareButton = null;

	private View mainContentView;
	private FrameLayout fragmentContainer;

	private FragmentManager fragmentManager;
	private WebFragment webFragment;
	private DuckModeFragment duckModeFragment;
	private RecentSearchFragment recentSearchFragment;
	private SavedFragment savedFragment;
	private FeedFragment feedFragment;
	
	// font scaling
	private LinearLayout fontSizeLayout = null;
	
	// welcome screen
	private WelcomeScreenView welcomeScreenLayout = null;
	OnClickListener welcomeCloseListener = null;
		
	private SharedPreferences sharedPreferences;
		
	public boolean savedState = false;
		
	private final int PREFERENCES_RESULT = 0;

	// keep prev progress in font seek bar, to make incremental changes available
	SeekBarHint fontSizeSeekBar;

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
		drawer.lockDrawer();

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

		drawer.unlockDrawer();
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
        keyboardService = new KeyboardService(this);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        showNewSourcesDialog();

        sharedPreferences = DDGApplication.getSharedPreferences();
        
        String themeName = PreferencesManager.getThemeName();
		int themeId = getResources().getIdentifier(themeName, "style", getPackageName());
		if(themeId != 0) {
			setTheme(themeId);
		}
		
		PreferencesManager.setFontDefaultsFromTheme(this);
        		        
        //setContentView(R.layout.drawer);
		setContentView(R.layout.main_temp);
        
        DDGUtils.displayStats = new DisplayStats(this);        
        
        if(savedInstanceState != null)
        	savedState = true;
        
        DDGControlVar.isAutocompleteActive = !PreferencesManager.getTurnOffAutocomplete();
        // always refresh on start
        DDGControlVar.hasUpdatedFeed = false;
        DDGControlVar.mDuckDuckGoContainer = (DuckDuckGoContainer) getLastCustomNonConfigurationInstance();
    	if(DDGControlVar.mDuckDuckGoContainer == null){
            initializeContainer();
    	}

		mainContentView = (View) findViewById(R.id.activityContainer);

		drawer = (DDGDrawerLayout) findViewById(R.id.mainDrawer);

		fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
		initFragments();

		fragmentManager = getSupportFragmentManager();

		drawer.setLeftMenuView(fragmentManager.findFragmentById(R.id.drawerFragment));
		contentView = (View) findViewById(R.id.mainView);

        if(!PreferencesManager.isWelcomeShown()) {
            addWelcomeScreen();
            shouldShowBangButtonExplanation = true;
    	}

        mainButton = (ImageButton) contentView.findViewById(R.id.settingsButton);
        mainButton.setOnClickListener(this);
        bangButton = (ImageButton)contentView.findViewById(R.id.bangButton);
        bangButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getSearchField().addBang();				
			}
		});
        
        if(DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
        	setMainButtonHome();
        }
        
        shareButton = (ImageButton) contentView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);
        
        // adjust visibility of share button after screen rotation
        if(DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
        	shareButton.setVisibility(View.VISIBLE);
        }
        initSearchBar();

		initFontSizeLayout();

		if(savedInstanceState==null) {
			displayHomeScreen();
		}

        checkForUpdates();
    }

	private void setMainButtonHome() {
		mainButton.setImageResource(R.drawable.ic_home);
	}

	private void setMainButtonMenu() {
		mainButton.setImageResource(R.drawable.ic_menu);
	}

	private void initSearchBar() {
		searchBar = contentView.findViewById(R.id.searchBar);
		dropShadowDivider = contentView.findViewById(R.id.dropshadow_top);
		searchField = (DDGAutoCompleteTextView) contentView.findViewById(R.id.searchEditText);
		getSearchField().setAdapter(DDGControlVar.mDuckDuckGoContainer.acAdapter);
		getSearchField().setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				if(textView == getSearchField() && actionId != EditorInfo.IME_NULL) {
					keyboardService.hideKeyboard(getSearchField());
					getSearchField().dismissDropDown();
					searchOrGoToUrl(getSearchField().getTrimmedText());
				}
				return false;
			}
		});

		getSearchField().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawer.close();
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
				if(getSearchField().isPopupShowing()){
					getSearchField().dismissDropDown();
				}
				showBangButton(false);
			}
		});

		getSearchField().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getSearchField().dismissDropDown();

				SuggestObject suggestObject = DDGControlVar.mDuckDuckGoContainer.acAdapter.getItem(position);
				if (suggestObject != null) {
					SuggestType suggestType = suggestObject.getType();
					if(suggestType == SuggestType.TEXT) {
						if(PreferencesManager.getDirectQuery()){
							String text = suggestObject.getPhrase().trim();
							if(suggestObject.hasOnlyBangQuery()){
								getSearchField().addTextWithTrailingSpace(suggestObject.getPhrase());
							}else{
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

		// This makes a little (X) to clear the search bar.
		DDGControlVar.mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int)Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight()/1.5));
		getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : DDGControlVar.mDuckDuckGoContainer.stopDrawable, null);

		getSearchField().setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					DDGControlVar.mCleanSearchBar = true;
					getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable);
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
			}

			public void afterTextChanged(Editable arg0) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
	}

	private void initFontSizeLayout() {
		fontSizeLayout = (LinearLayout) findViewById(R.id.fontSeekLayout);

		fontSizeSeekBar = (SeekBarHint) findViewById(R.id.fontSizeSeekBar);

		DDGControlVar.mainTextSize = PreferencesManager.getMainFontSize();

		DDGControlVar.recentTextSize = PreferencesManager.getRecentFontSize();

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
				DDGControlVar.mainTextSize = PreferencesManager.getMainFontSize() + diffPixel;

				DDGControlVar.recentTextSize = PreferencesManager.getRecentFontSize() + diffPixel;

				DDGControlVar.ptrHeaderSize = PreferencesManager.getPtrHeaderTextSize() + diff;
				DDGControlVar.ptrSubHeaderSize = PreferencesManager.getPtrHeaderSubTextSize() + diff;

				DDGControlVar.webViewTextSize = PreferencesManager.getWebviewFontSize() + diff;

				DDGControlVar.leftTitleTextSize = PreferencesManager.getLeftTitleTextSize() + diffPixel;

				BusProvider.getInstance().post(new FontSizeOnProgressChangedEvent());
			}
		});

		Button fontSizeApplyButton = (Button) findViewById(R.id.fontSizeApplyButton);
		fontSizeApplyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DDGControlVar.fontPrevProgress = DDGControlVar.fontProgress;
				fontSizeSeekBar.setExtraText(null);

				PreferencesManager.saveAdjustedTextSizes();

				closeFontSlider();
			}
		});

		Button fontSizeCancelButton = (Button) findViewById(R.id.fontSizeCancelButton);
		fontSizeCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelFontScaling();
			}
		});
	}

    private void initializeContainer() {
        DDGControlVar.mDuckDuckGoContainer = new DuckDuckGoContainer();

        DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;

        DDGControlVar.mDuckDuckGoContainer.stopDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.stop);
//    		DDGControlVar.mDuckDuckGoContainer.reloadDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.reload);
        DDGControlVar.mDuckDuckGoContainer.progressDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.page_progress);
        DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.searchfield);
        DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable.setAlpha(150);

        DDGControlVar.mDuckDuckGoContainer.historyAdapter = new MultiHistoryAdapter(this);
		BusProvider.getInstance().post(new LeftMenuSetAdapterEvent());

        DDGControlVar.mDuckDuckGoContainer.acAdapter = new AutoCompleteResultsAdapter(this);
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
		getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable);
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
		DDGControlVar.currentFeedObject = null;
		DDGControlVar.mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_BROWSE;
        resetSearchBar();
	}

    private void resetSearchBar() {
        searchBar.setBackgroundResource(R.color.topbar_background);
        dropShadowDivider.setVisibility(View.VISIBLE);
    }

    private void closeFontSlider() {
		fontSizeLayout.setVisibility(View.GONE);
		fontSizeSeekBar.setProgress(DDGControlVar.fontPrevProgress);
		PreferencesManager.setFontSliderVisibility(false);
	}
	
	private void cancelFontScaling() {
		fontSizeSeekBar.setExtraText(null);
		DDGControlVar.mainTextSize = PreferencesManager.getMainFontSize();
		DDGControlVar.recentTextSize = PreferencesManager.getRecentFontSize();
		DDGControlVar.webViewTextSize = PreferencesManager.getWebviewFontSize();
		DDGControlVar.leftTitleTextSize = PreferencesManager.getLeftTitleTextSize();
		closeFontSlider();

		BusProvider.getInstance().post(new FontSizeCancelScalingEvent());
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
			
			if(PreferencesManager.isFontSliderVisible()) {
				fontSizeLayout.setVisibility(View.VISIBLE);
			}
			
			switch(screenToDisplay) {
				case SCR_STORIES:
					displayNewsFeed();
					break;
				case SCR_RECENT_SEARCH:
					displayRecentSearch();
					break;
				case SCR_DUCKMODE:
					displayDuckMode();
					break;
				case SCR_SAVED_FEED:
					displaySavedFeed();
					break;
				default:
					break;
			}
			
			if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH &&
					!screenToDisplay.equals(SCREEN.SCR_RECENT_SEARCH)){
				BusProvider.getInstance().post(new LeftMenuSetRecentVisibleEvent());
			}
	        if(!DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
				DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;
				DDGControlVar.mDuckDuckGoContainer.currentScreen = screenToDisplay;
			}
	}
	
	private void displayHomeScreen() {
		displayScreen(DDGControlVar.START_SCREEN, true);
        
		if(DDGControlVar.mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_SEARCH
				|| DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH
                || DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED_FEED
                || DDGControlVar.START_SCREEN == SCREEN.SCR_DUCKMODE) {
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
			getSearchField().setAdapter(null);
		}
		else {
	        getSearchField().setAdapter(DDGControlVar.mDuckDuckGoContainer.acAdapter);
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
		else if(DDGControlVar.mDuckDuckGoContainer.webviewShowing){
            keyboardService.hideKeyboard(getSearchField());
			shareButton.setVisibility(View.VISIBLE);
			displayScreen(SCREEN.SCR_WEBVIEW, false);
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
		if(drawer.isOpen()) {
			drawer.close();
		}
		else if(fontSizeLayout.getVisibility() != View.GONE) {
			cancelFontScaling();
		}
		//else if (DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
		else if(DDGControlVar.mDuckDuckGoContainer.currentScreen == SCREEN.SCR_WEBVIEW
				|| DDGControlVar.mDuckDuckGoContainer.webviewShowing
				|| webFragment.isVisible()) {
			BusProvider.getInstance().post(new WebViewBackPressActionEvent());
		}
		// main feed showing & source filter is active
		else if(DDGControlVar.targetSource != null){
			BusProvider.getInstance().post(new FeedCancelSourceFilterEvent());
		}
		else {
			DDGControlVar.hasUpdatedFeed = false;
			super.onBackPressed();
		}
	}
	
	public void reloadAction() {
		DDGControlVar.mCleanSearchBar = false;
        DDGControlVar.mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int) Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth() / 1.5), (int) Math.floor(DDGControlVar.mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight() / 1.5));
		getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : DDGControlVar.mDuckDuckGoContainer.stopDrawable, null);

		BusProvider.getInstance().post(new WebViewReloadActionEvent());
	}
	
	private void stopAction() {
		DDGControlVar.mCleanSearchBar = true;
    	getSearchField().setText("");

    	// This makes a little (X) to clear the search bar.
    	getSearchField().setCompoundDrawables(null, null, null, null);
    	getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable);
	}

	public void searchOrGoToUrl(String text, SESSIONTYPE sessionType) {
		displayWebView();
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
		BusProvider.getInstance().post(new FeedCleanImageTaskEvent());
		Intent intent = new Intent(getBaseContext(), Preferences.class);
		startActivityForResult(intent, PREFERENCES_RESULT);
	}

	/**
	 * Method that switches visibility of views for Home or Saved feed
	 */
	private void displayFeedCore() {		
    	// main view visibility changes and keep feed updated
		shareButton.setVisibility(View.GONE);
    	DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
	}
	
	public void displayNewsFeed(){
		resetScreenState();
		stopAction();
		
		// left side menu visibility changes
		BusProvider.getInstance().post(new LeftMenuChangeVisibilityEvent());
    	
    	// adjust "not recording" indicator
		BusProvider.getInstance().post(new LeftMenuHistoryDisabledEvent());
    	
    	// ensures feed refresh every time user switches to Stories screen
    	DDGControlVar.hasUpdatedFeed = false;
		
		displayFeedCore();
		clearLeftSelect();

		if(!feedFragment.isVisible()) {
			changeFragment(feedFragment, FeedFragment.TAG);
		}
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_STORIES){
    		DDGControlVar.homeScreenShowing = true;
    		setMainButtonMenu();
			BusProvider.getInstance().post(new LeftMenuSetHomeSelectedEvent(true));

    	}
    	else {
    		DDGControlVar.homeScreenShowing = false;
			BusProvider.getInstance().post(new LeftMenuSetStoriesSelectedEvent(true));
    	}
	}
	
	public void displaySavedFeed(){
		resetScreenState();
		
		// left side menu visibility changes
		BusProvider.getInstance().post(new LeftMenuChangeVisibilityEvent());
    	
		shareButton.setVisibility(View.GONE);
    	DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
		clearLeftSelect();

		if(!savedFragment.isVisible()) {
			savedFragment = new SavedFragment();
			changeFragment(savedFragment, SavedFragment.TAG);
		}
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED_FEED){
    		DDGControlVar.homeScreenShowing = true;
    		setMainButtonMenu();
			BusProvider.getInstance().post(new LeftMenuSetHomeSelectedEvent(true));
    	}
    	else {
    		DDGControlVar.homeScreenShowing = false;
			BusProvider.getInstance().post(new LeftMenuSetSavedSelectedEvent(true));
    	}
	}
	
	public void displayRecentSearch(){  
		resetScreenState(); 
		
		// left side menu visibility changes
		BusProvider.getInstance().post(new LeftMenuChangeVisibilityEvent());
		
    	// main view visibility changes
		shareButton.setVisibility(View.GONE);
    	DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
		
		clearLeftSelect();

		if(!recentSearchFragment.isVisible()) {
			changeFragment(recentSearchFragment, RecentSearchFragment.TAG);
		}
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH){
    		DDGControlVar.homeScreenShowing = true;
    		setMainButtonMenu();
			BusProvider.getInstance().post(new LeftMenuSetHomeSelectedEvent(true));
    	}
    	else {
    		DDGControlVar.homeScreenShowing = false;
    	}
	}
	
	public void displayDuckMode(){  
		resetScreenState(); 
		
		// left side menu visibility changes
		BusProvider.getInstance().post(new LeftMenuChangeVisibilityEvent());
		
    	// main view visibility changes
		shareButton.setVisibility(View.GONE);
    	DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
		
		clearLeftSelect();

		if(!duckModeFragment.isVisible()) {
			changeFragment(duckModeFragment, DuckModeFragment.TAG);
		}
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_DUCKMODE){
    		DDGControlVar.homeScreenShowing = true;
    		setMainButtonMenu();
			BusProvider.getInstance().post(new LeftMenuSetHomeSelectedEvent(true));
            hideSearchBarBackground();
    	}
    	else {
    		DDGControlVar.homeScreenShowing = false;
    	}
	}

    private void hideSearchBarBackground() {
        TypedArray styledAttributes = getTheme().obtainStyledAttributes(R.style.DDGTheme, new int[]{R.attr.searchBarBackground});
        searchBar.setBackgroundResource(styledAttributes.getResourceId(0,0));
        dropShadowDivider.setVisibility(View.GONE);
    }

    public void displayWebView() {
		// loading something in the browser - set home icon
		DDGControlVar.homeScreenShowing = false;
		setMainButtonHome();
        resetSearchBar();
		if (!DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
			shareButton.setVisibility(View.VISIBLE);

			if(!webFragment.isVisible()) {
				webFragment = new WebFragment();
				changeFragment(webFragment, WebFragment.TAG);

				DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;
				DDGControlVar.mDuckDuckGoContainer.currentScreen = SCREEN.SCR_WEBVIEW;
			}

			DDGControlVar.mDuckDuckGoContainer.webviewShowing = true;
		}
	}

	private void changeFragment(Fragment newFragment, String newTag) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		Fragment currentFragment = fragmentManager.findFragmentByTag(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);

		if(currentFragment==null) {
			transaction.replace(fragmentContainer.getId(), newFragment, newTag);
		} else if(currentFragment==feedFragment) {
			transaction.hide(currentFragment);
			transaction.add(fragmentContainer.getId(), newFragment, newTag);
		} else if(newFragment==feedFragment) {
			if(newFragment.isAdded()) {
				transaction.remove(currentFragment);
				transaction.show(newFragment);
			} else {
				transaction.replace(fragmentContainer.getId(), newFragment, newTag);
			}
		} else {
			transaction.remove(currentFragment);
			transaction.add(fragmentContainer.getId(), newFragment, newTag);
		}

		transaction.commit();
		fragmentManager.executePendingTransactions();
		DDGControlVar.mDuckDuckGoContainer.currentFragmentTag = newTag;

	}

    public void onClick(View view) {
		if (view.equals(mainButton)) {
			handleHomeSettingsButtonClick();
		}
		else if (view.equals(shareButton)) {
			BusProvider.getInstance().post(new HandleShareButtonClickEvent());
		}
	}

	private void handleLeftHomeTextViewClick() {
		drawer.close();
					
		if (DDGControlVar.mDuckDuckGoContainer.webviewShowing) {

			//We are going home!
			//mainWebView.clearHistory();
			//mainWebView.clearView();
			clearSearchBar();
			DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
		}
		
		displayHomeScreen();
	}

	private void handleHomeSettingsButtonClick() {
        keyboardService.hideKeyboard(getSearchField());
		
		if(DDGControlVar.homeScreenShowing){
			drawer.open();
		}
		else {
			// going home
			displayHomeScreen();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                
                if(PreferencesManager.isFontSliderVisible()) {
    				fontSizeLayout.setVisibility(View.VISIBLE);
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
			drawer.close();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	private void initFragments() {
		duckModeFragment = new DuckModeFragment();
		webFragment = new WebFragment();
		recentSearchFragment = new RecentSearchFragment();
		savedFragment = new SavedFragment();
		feedFragment = new FeedFragment();

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
		drawer.close();
        keyboardService.hideKeyboard(getSearchField());
		if(!webFragment.isVisible()) {
			displayWebView();
			//displayScreen(SCREEN.SCR_WEBVIEW, false);
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
		drawer.close();
        getSearchField().pasteQuery(event.query);
        keyboardService.showKeyboard(getSearchField());
	}

    @Subscribe
	public void onSuggestionPaste(SuggestionPasteEvent event) {
		drawer.close();
        getSearchField().pasteQuery(event.query);
	}
	
	@Subscribe
	public void onSavedSearchPaste(SavedSearchPasteEvent event) {
		drawer.close();
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
		getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable);
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
		drawer.close();
		displayScreen(SCREEN.SCR_STORIES, false);
	}

	@Subscribe
	public void onLeftMenuSavedClickEvent(LeftMenuSavedClickEvent event) {
		drawer.close();
		displayScreen(SCREEN.SCR_SAVED_FEED, false);
	}

	@Subscribe
	public void onLeftMenuSettingsClickEvent(LeftMenuSettingsClickEvent event) {
		drawer.close();
		displaySettings();
	}

	@Subscribe
	public void onLeftMenuCloseEvent(LeftMenuCloseEvent event) {
		drawer.close();
	}

	@Subscribe
	public void onDismissBangPopupEvent(DismissBangPopupEvent event) {
		if(bangButtonExplanationPopup!=null){
			bangButtonExplanationPopup.dismiss();
		}
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
}
