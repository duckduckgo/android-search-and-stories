package com.duckduckgo.mobile.android.activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.*;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
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
import com.duckduckgo.mobile.android.events.DisplayHomeScreenEvent;
import com.duckduckgo.mobile.android.events.DisplayScreenEvent;
import com.duckduckgo.mobile.android.events.HistoryItemLongClickEvent;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.events.OverflowButtonClickEvent;
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
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.feedEvents.SavedFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuHomeClickEvent;
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
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.Sharer;
import com.duckduckgo.mobile.android.util.SuggestType;
import com.duckduckgo.mobile.android.util.TorIntegrationProvider;
import com.duckduckgo.mobile.android.views.autocomplete.BackButtonPressedEventListener;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;
import com.duckduckgo.mobile.android.views.webview.DDGWebView;
import com.duckduckgo.mobile.android.widgets.BangButtonExplanationPopup;
import com.squareup.otto.Subscribe;

import java.util.List;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class DuckDuckGo extends ActionBarActivity/* implements OnClickListener*/ /*implements ViewTreeObserver.OnGlobalLayoutListener*/ {
	protected final String TAG = "DuckDuckGo";
    private KeyboardService keyboardService;
    private DDGAutoCompleteTextView searchField = null;
/*
	private DDGAutoCompleteTextView searchField = null;
    //private FrameLayout searchFieldContainer = null;
    private RelativeLayout searchFieldContainer = null;
    private TextView actionBarTitle;*/
	//private HistoryListView leftRecentView = null;//drawer fragment

	//private HistoryListView recentSearchView = null;//recent search fragment

	private RelativeLayout activityContainer;
    private FrameLayout fragmentContainer;

	private FragmentManager fragmentManager;

    //private DDGActionBarManager actionBar;
    private Toolbar toolbar;
/*
	public Toolbar toolbar;
	private ActionBar actionBar;
*/
	private SharedPreferences sharedPreferences;
		
	public boolean savedState = false;
    private boolean backPressed = false;
    private boolean assistAction = false;
		
	private final int PREFERENCES_RESULT = 0;
/*
    private View searchBar;
    private View dropShadowDivider;*/

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
/*
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
*/
    @Override
    protected void onStart() {
        super.onStart();
        Log.w("www", "onstart start");
		TorIntegrationProvider.getInstance(this).prepareTorSettings();
        Log.w("www", "on start end");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("aaa", "on create ---------------------------------------------------------------------");
        Log.w("www", "on create start");
        keyboardService = new KeyboardService(this);
        supportRequestWindowFeature(Window.FEATURE_PROGRESS);

        showNewSourcesDialog();

        sharedPreferences = DDGApplication.getSharedPreferences();
        
        String themeName = PreferencesManager.getThemeName(); // delete;
		int themeId = getResources().getIdentifier(themeName, "style", getPackageName());
		if(themeId != 0) {
			setTheme(themeId);
		}

		setContentView(R.layout.temp_main);
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

        activityContainer = (RelativeLayout) findViewById(R.id.activityContainer);
		fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchField = (DDGAutoCompleteTextView) toolbar.findViewById(R.id.searchEditText);
        setSupportActionBar(toolbar);
        getSupportActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean b) {
                Log.e("aaa", "menu visibility changed");
            }
        });
        //actionBar = new DDGActionBarManager(this, this, toolbar, findViewById(R.id.dropshadow_top));
        DDGActionBarManager.getInstance().init(this, this, toolbar, findViewById(R.id.dropshadow_top));
        //actionBar.init();
        initSearchField();

		fragmentManager = getSupportFragmentManager();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.e("aaa", "---------------inside on back stack changed");
                Log.e("aaa", "back stack count: " + fragmentManager.getBackStackEntryCount());
                showAllFragments();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                    if (tag != null) {
                        DDGControlVar.mDuckDuckGoContainer.currentFragmentTag = tag;
                        if (!tag.equals(WebFragment.TAG) && !DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
                            DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;
                        }
                        DDGControlVar.mDuckDuckGoContainer.currentScreen = DDGUtils.getScreenByTag(tag);
                        DDGControlVar.mDuckDuckGoContainer.webviewShowing = tag.equals(WebFragment.TAG);
                        DDGControlVar.homeScreenShowing = DDGControlVar.mDuckDuckGoContainer.currentScreen == DDGControlVar.START_SCREEN;

                        //fragmentManager.findFragmentByTag(tag).setHasOptionsMenu(DDGControlVar.homeScreenShowing || DDGControlVar.mDuckDuckGoContainer.webviewShowing);//aaa overflow temp

                        DDGActionBarManager.getInstance().updateActionBar(fragmentManager, tag);
                        if ((tag.equals(SearchFragment.TAG) || tag.equals(SearchFragment.TAG_HOME_PAGE)) && !backPressed) {
                            Log.e("aaa", "show keyboard!");
                            keyboardService.showKeyboard(getSearchField());
                            //Log.e("aaa", "[[[[[[[[[should hide the keyboard, fragmentmanager backstack, has focus?: "+getSearchField().hasFocus());
                            //keyboardService.hideKeyboard(getSearchField());
                        } else {
                            Log.e("aaa", "hide keyboard!");
                            Log.e("aaa", "search field has focus: " + searchField.hasFocus());
                            keyboardService.hideKeyboardDelayed(getSearchField());
                            //searchField.clearFocus();
                            //searchField.clearFocus();
                            //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            //imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                            //keyboardService.hideKeyboard(searchField);
                        }
                        if(false && assistAction) {
                            Log.e("aaa", "assist action clearing focus and showing keyboard");
                            getSearchField().clearFocus();
                            keyboardService.showKeyboard(getSearchField());
                        }
                        backPressed = false;
                    }
                    Log.e("aaa", "inside back stack, current tag: " + DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
                    showAllFragments();
                }
            }
        });
/*
        if(!PreferencesManager.isWelcomeShown()) {
            addWelcomeScreen();
            shouldShowBangButtonExplanation = true;
    	}
*/
        //initActionBar();
/*
        if(DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
        	setMainButtonHome();
        }
*/
		if(savedInstanceState==null) {
			displayHomeScreen();
		}

        checkForUpdates();
        Log.w("www", "on create end");
    }
/*
	private void setMainButtonHome() {
		//mainButton.setImageResource(R.drawable.ic_home);
	}

	private void setMainButtonMenu() {
		//mainButton.setImageResource(R.drawable.ic_menu);
	}
*/


    private void initSearchField() {
        //getSearchField().setAdapter(DDGControlVar.mDuckDuckGoContainer.acAdapter);
        //getSearchField().setAdapter(DDGControlVar.mDuckDuckGoContainer.tempAdapter);//aaa adapter
        getSearchField().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if(textView == getSearchField() && actionId != EditorInfo.IME_NULL) {
                    //keyboardService.hideKeyboard(getSearchField());//aaa keyboard
                    //getSearchField().dismissDropDown();
                    Log.e("aaa", "on editor action listener, must search or go to url, text is: " + getSearchField().getText().toString());
                    //removeSearchFragment();
                    searchOrGoToUrl(getSearchField().getTrimmedText());
                }
                return false;
            }
        });

        getSearchField().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //drawer.close();
                //displayScreen(SCREEN.SCR_SEARCH, true);
//				showBangButton(true);
            }
        });

        getSearchField().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//				showBangButton(hasFocus);
                String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
                Fragment f = fragmentManager.findFragmentByTag(tag);

                if(hasFocus) {
                    if(f!= null && (f.getTag().equals(SearchFragment.TAG) || f.getTag().equals(SearchFragment.TAG_HOME_PAGE))) {
                        Log.e("aaa", "DO NOT display search");
                    } else {
                        Log.e("aaa", "MUST display search");
                        displayScreen(SCREEN.SCR_SEARCH, true);
                    }
                } else {

                    Log.e("aaa", "search field has not focus, has focus: "+hasFocus);

                    //searchField.clearFocus();
                    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                    //keyboardService.hideKeyboard(getSearchField());//aaa keyboard
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

        getSearchField().setOnTouchListener(new View.OnTouchListener() {
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

                if(isFragmentVisible(SearchFragment.TAG) || isFragmentVisible(SearchFragment.TAG_HOME_PAGE)) {
                    BusProvider.getInstance().post(new ShowAutoCompleteResultsEvent(s.length() > 0));
                }
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSearchField().setCustomSelectionActionModeCallback(new android.view.ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(android.view.ActionMode mode) {

                }
            });
        }
    }


    private void initializeContainer() {
        DDGControlVar.mDuckDuckGoContainer = new DuckDuckGoContainer();

        DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
        DDGControlVar.mDuckDuckGoContainer.currentScreen = DDGControlVar.START_SCREEN;
        DDGControlVar.mDuckDuckGoContainer.currentFragmentTag = DDGUtils.getTagByScreen(DDGControlVar.mDuckDuckGoContainer.currentScreen);
        DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;

        //DDGControlVar.mDuckDuckGoContainer.stopDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.stop);
        DDGControlVar.mDuckDuckGoContainer.stopDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.cross);
//    		DDGControlVar.mDuckDuckGoContainer.reloadDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.reload);
        DDGControlVar.mDuckDuckGoContainer.progressDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.page_progress);
        DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.searchfield);
        DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable.setAlpha(150);

        DDGControlVar.mDuckDuckGoContainer.historyAdapter = new MultiHistoryAdapter(this);

        DDGControlVar.mDuckDuckGoContainer.acAdapter = new AutoCompleteResultsAdapter(this);
        DDGControlVar.mDuckDuckGoContainer.tempAdapter = new TempAutoCompleteResultsAdapter(this);
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter = new RecentResultCursorAdapter(this, DDGApplication.getDB().getCursorSearchHistory());
    }

    // Assist action is better known as Google Now gesture
    private boolean isLaunchedWithAssistAction(){
        Log.e("aaa", "is launched with assist action: "+(getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_ASSIST)));
        return getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_ASSIST);
    }

    private void showNewSourcesDialog() {
        if(PreferencesManager.shouldShowNewSourcesDialog()){
            new NewSourcesDialogBuilder(this).show();
            PreferencesManager.newSourcesDialogWasShown();
        }
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

        Log.e("aaa", "inside display screen, search field has focus?: "+getSearchField().hasFocus());
        //if()

	    // control which screen is shown & configure related views
			
		switch(screenToDisplay) {
			case SCR_STORIES:
                DDGActionBarManager.getInstance().resetScreenState();
                stopAction();

                // ensures feed refresh every time user switches to Stories screen
                DDGControlVar.hasUpdatedFeed = false;

                displayFeedCore();

                //keyboardService.hideKeyboard(getSearchField());

                fragment = new FeedFragment();
                tag = FeedFragment.TAG;
				break;
			case SCR_RECENTS:
                DDGActionBarManager.getInstance().resetScreenState();

                //keyboardService.hideKeyboard(getSearchField());

                fragment = new RecentsFragment();
                tag = RecentsFragment.TAG;

				break;
            case SCR_WEBVIEW:
                DDGActionBarManager.getInstance().resetSearchBar();
                //setSearchBarText(DDGControlVar.mDuckDuckGoContainer.currentUrl);

                //keyboardService.hideKeyboard(getSearchField());

                fragment = new WebFragment();
                tag = WebFragment.TAG;
                break;
            case SCR_SEARCH:
                //resetScreenState();
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

                //keyboardService.hideKeyboard(getSearchField());

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

                //keyboardService.hideKeyboard(getSearchField());

                fragment = new PrefFragment();
                tag = PrefFragment.TAG;
                break;
            case SCR_SOURCES:
                fragment = new SourcesFragment();
                tag = SourcesFragment.TAG;
            default:
				break;
		}/*
	    if(!DDGControlVar.mDuckDuckGoContainer.webviewShowing && screenToDisplay!=SCREEN.SCR_SEARCH) {
			//DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;
			//DDGControlVar.mDuckDuckGoContainer.currentScreen = screenToDisplay;
		}

        if(tag.equals(SearchFragment.TAG)) {
            //Log.e("aaa", "not display search fragment");
            //return;
        }*/
        if(!assistAction && tag.equals(SearchFragment.TAG)) {
            delayedChangeFragment(fragment, tag);
        } else if(!tag.equals("")) {
            //assistAction = false;
            changeFragment(fragment, tag, displayHomeScreen);
        }
	}

    public void displayScreen(SCREEN screenToDisplay, boolean clean) {
        displayScreen(screenToDisplay, clean, false);
    }
	
	private void displayHomeScreen() {
        Log.e("aaa", "-------------------------display home screen");

        DDGControlVar.mDuckDuckGoContainer.currentUrl = "";
        displayScreen(DDGControlVar.START_SCREEN, true, true);
        /*aaa
		if(DDGControlVar.mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_SEARCH
				|| DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH
                || DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED_FEED
                || DDGControlVar.START_SCREEN == SCREEN.SCR_DUCKMODE) {
            keyboardService.showKeyboard(getSearchField());
		}*//*
        if(DDGControlVar.mDuckDuckGoContainer.sessionType == SESSIONTYPE.SESSION_SEARCH
                || DDGControlVar.START_SCREEN == SCREEN.SCR_SEARCH_HOME_PAGE) {
                //|| DDGControlVar.START_SCREEN == SCREEN.SCR_RECENTS
                //|| DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED) {
            //keyboardService.showKeyboard(getSearchField());
        }*/
        DDGControlVar.mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_BROWSE;
	}

	@Override
	public void onResume() {
		super.onResume();
        Log.w("www", "on resumt start");
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
        if(intent!=null) {
            Log.e("aaa", "intent!=null, action: "+intent.getAction());
            Log.e("aaa", "intent!=null, categories: "+intent.getCategories());
        } else {
            Log.e("aaa", "intent==null");
        }
        
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.e("aaa", "intent == action search");
			intent.setAction(Intent.ACTION_MAIN);
			String query = intent.getStringExtra(SearchManager.QUERY);
            DDGActionBarManager.getInstance().setSearchBarText(query);
			BusProvider.getInstance().post(new WebViewSearchWebTermEvent(query));
		}
		else if(intent.getBooleanExtra("widget", false)) {
            Log.e("aaa", "intent == widget");
            if(!getSearchField().getText().toString().equals("")) {
                DDGActionBarManager.getInstance().clearSearchBar();
            }
			//displayScreen(DDGControlVar.START_SCREEN, true);
            displayScreen(SCREEN.SCR_SEARCH, true);
            //keyboardService.showKeyboard(getSearchField());
		}
        else if(Intent.ACTION_VIEW.equals(intent.getAction())) {
            Log.e("aaa", "intent == action view");
            searchOrGoToUrl(intent.getDataString());
        }
        else if(Intent.ACTION_ASSIST.equals(intent.getAction())){
            Log.e("aaa", "intent == action assist");
            //keyboardService.showKeyboard(getSearchField());
            //if(DDGControlVar.START_SCREEN!=SCREEN.SCR_SEARCH_HOME_PAGE) {
            //actionBar.clearSearchBar();
                //displayScreen(SCREEN.SCR_SEARCH, true);
            //}
            //keyboardService.showKeyboard(getSearchField());
            //delayedChangeFragment(new SearchFragment(), SearchFragment.TAG);
            //getSearchField().requestFocus();
            //keyboardService.showKeyboard(getSearchField());
            assistAction = true;
            //actionBar.clearSearchBar();
            //keyboardService.showKeyboard(getSearchField());
            //getSearchField().requestFocus();
            //changeFragment(new SearchFragment(), SearchFragment.TAG);
            //displayScreen(SCREEN.SCR_SEARCH, true);
            Toast.makeText(this, "TO OPEN SEARCH", Toast.LENGTH_SHORT).show();
        }
		else if(DDGControlVar.mDuckDuckGoContainer.webviewShowing){
            Log.e("aaa", "intent == action search");
            //keyboardService.hideKeyboard(getSearchField());//aaa keyboard
            //todo check if fragment is visible or not
            Fragment fragment = fragmentManager.findFragmentByTag(WebFragment.TAG);
            if(fragmentManager.findFragmentByTag(WebFragment.TAG)== null || !fragment.isVisible()) {
                displayScreen(SCREEN.SCR_WEBVIEW, false);
            }
		}

        Log.e("aaa", "duckduck go on resume");

        checkForCrashes();
        Log.w("www", "on resume end");
	}

	@Override
	public void onPause() {
		super.onPause();
		
		BusProvider.getInstance().unregister(this);

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
        backPressed = true;
        Log.e("aaa", "on back pressed");/*
        if(false && DDGControlVar.START_SCREEN!=SCREEN.SCR_SEARCH && isFragmentVisible(SearchFragment.TAG)) {
            removeSearchFragment();
        }
		else */if((DDGControlVar.mDuckDuckGoContainer.currentScreen == SCREEN.SCR_WEBVIEW
				|| DDGControlVar.mDuckDuckGoContainer.webviewShowing || isFragmentVisible(WebFragment.TAG))) {
			BusProvider.getInstance().post(new WebViewBackPressActionEvent());
		}
		// main feed showing & source filter is active
		else if(DDGControlVar.mDuckDuckGoContainer.currentScreen == SCREEN.SCR_STORIES && DDGControlVar.targetSource != null){
			BusProvider.getInstance().post(new FeedCancelSourceFilterEvent());
		}
        // main feed showing & category filter is active
        else if(DDGControlVar.mDuckDuckGoContainer.currentScreen == SCREEN.SCR_STORIES && DDGControlVar.targetCategory != null) {
            BusProvider.getInstance().post(new FeedCancelCategoryFilterEvent());
        }
        else if(fragmentManager.getBackStackEntryCount()==1) {
            //fragmentManager.popBackStackImmediate();
            finish();
            //super.onBackPressed();
        }
		else {
            DDGControlVar.hasUpdatedFeed = false;
            super.onBackPressed();
		}
	}
/*
    @Override
    public void onGlobalLayout() {
        int totalHeight = activityContainer.getRootView().getHeight();
        int visibleHeight = activityContainer.getHeight();

        boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        int statusBar = DDGUtils.getStatusBarHeight(DuckDuckGo.this);
        int navigationBar = DDGUtils.getNavigationBarHeight(DuckDuckGo.this);
        int actionBarHeight = (int) getResources().getDimension(R.dimen.actionbar_height);
        //Log.e("aaa", "status bar: "+statusBar);
        //Log.e("aaa", "navigation bar: "+navigationBar);
        totalHeight = totalHeight - statusBar - navigationBar - actionBarHeight;
        if(portrait && (totalHeight - visibleHeight) > (statusBar + navigationBar + actionBarHeight)) {
            Log.e("aaa", "keyboard open!");

            changeFragment(new SearchFragment(), SearchFragment.TAG);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
                activityContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
                activityContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
            //activityContainer.getViewTreeObserver().removeOnGlobalLayoutListener();
        }

    }*/
/*
    @Override
    public void onNewIntent(Intent intent) {
        if(intent!=null) {
            if(intent.hasExtra("home") && intent.getBooleanExtra("home", false)) {
                displayHomeScreen();
            }
        }
    }
*/
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

                //keyboardService.showKeyboard(getSearchField());

                //InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                //getSearchField().clearFocus();
                //keyboardService.hideKeyboard(getSearchField());
                //keyboardService.hideKeyboardDelayed(getSearchField());


                /*
                getSearchField().clearFocus();
                getSearchField().setFocusable(false);
                getSearchField().setFocusableInTouchMode(false);
                getSearchField().setFocusable(true);
                getSearchField().setFocusableInTouchMode(true);*/

                //getSearchField().requestFocus();
                //imm.showSoftInput(searchField, 0);
                //imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        displayScreen(SCREEN.SCR_SETTINGS, false);/*
        Intent intent = new Intent(this, TempPreferences.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_bottom2, R.anim.empty);*/
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

	/**
	 * Method that switches visibility of views for Home or Saved feed
	 */
	private void displayFeedCore() {		
    	// main view visibility changes and keep feed updated
		//shareButton.setVisibility(View.GONE);
    	DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
	}
/*
    private void hideSearchBarBackground() {
        TypedArray styledAttributes = getTheme().obtainStyledAttributes(R.style.DDGTheme, new int[]{R.attr.searchBarBackground});
        searchBar.setBackgroundResource(styledAttributes.getResourceId(0,0));
        dropShadowDivider.setVisibility(View.GONE);
    }*/

    private void changeFragment(Fragment newFragment, String newTag) {
        changeFragment(newFragment, newTag, false);
    }

	private void changeFragment(Fragment newFragment, String newTag, boolean displayHomeScreen) {
        Log.e("aaa", "inside changefragment, newtag: "+newTag);
        Log.e("aaa", "new tag: "+newTag+" - current tag: "+DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
        if(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag.equals(newTag) && !displayHomeScreen) {
            Log.e("aaa", "current tag == new tag, don't change fragment");
            return;
        } else {
            Log.e("aaa", "current tag != new tag, CHAnGE fragment");
        }

        //fragmentManager.executePendingTransactions();

        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);

        Fragment searchFragment = fragmentManager.findFragmentByTag(SearchFragment.TAG);
        if(searchFragment!=null && DDGControlVar.START_SCREEN!=SCREEN.SCR_SEARCH) {
            //removeSearchFragment();
        }

        boolean backState = true;

        if(!newTag.equals(SearchFragment.TAG)) {
            backState = fragmentManager.popBackStackImmediate(newTag, 0);

            if (displayHomeScreen && fragmentManager.getBackStackEntryCount() > 1) {
                List<Fragment> fragments = fragmentManager.getFragments();
                FragmentTransaction removeTransaction = fragmentManager.beginTransaction();
                for (Fragment f : fragments) {
                    if (f != null) {
                        removeTransaction.remove(f);
                        fragmentManager.popBackStack();
                    }
                }
                removeTransaction.commit();
                fragmentManager.executePendingTransactions();
            }
        }



        if(newTag.equals(SearchFragment.TAG) || (!backState && fragmentManager.findFragmentByTag(newTag)==null)) {
            Log.e("aaa", "inside main change fragment");
            final Fragment currentFragment = fragmentManager.findFragmentByTag(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
            if(currentFragment!=null && currentFragment.isAdded()) {// && currentFragment.isVisible()) {
                //transaction.hide(currentFragment);
                //currentFragment.onHiddenChanged(true);
                if(currentFragment.getTag().equals(FavoriteFragment.TAG)) {
                    //((FavoriteFragment)currentFragment).collapseTabLayout();
                }
            }


            FragmentTransaction transaction = fragmentManager.beginTransaction();
            //transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            Fragment f = fragmentManager.findFragmentByTag(newTag);
            if(newTag.equals(WebFragment.TAG) || newTag.equals(SourcesFragment.TAG) || newTag.equals(AboutFragment.TAG  )) {
                transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.empty, R.anim.empty, R.anim.slide_out_to_right);
            } else if(newTag.equals(PrefFragment.TAG) || newTag.equals(HelpFeedbackFragment.TAG)) {
                transaction.setCustomAnimations(R.anim.slide_in_from_bottom2, R.anim.empty, R.anim.empty, R.anim.slide_out_to_bottom2);
            } else if(newTag.equals(SearchFragment.TAG)) {
                transaction.setCustomAnimations(R.anim.slide_in_from_bottom2, R.anim.empty, R.anim.empty, R.anim.slide_out_to_bottom2);
                //transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.empty, R.anim.empty, R.anim.slide_out_to_right);
                //transaction.setCustomAnimations(R.anim.temp_animation, R.anim.empty, R.anim.empty, R.anim.slide_out_to_bottom2);
            } else {
                transaction.setCustomAnimations(R.anim.empty_immediate, R.anim.empty, R.anim.empty_immediate, R.anim.empty_immediate);
            }
            if(true || f==null) {
                Log.e("aaa", "f==null, adding new fragment");
                transaction.add(fragmentContainer.getId(), newFragment, newTag);
            } else {
                Log.e("aaa", "f!=null, showing new fragment");
                transaction.show(f);
            }
            if(currentFragment!=null /*&& !currentFragment.getTag().equals(SearchFragment.TAG)*/ && currentFragment.isAdded()) {// && currentFragment.isVisible()) {
                transaction.hide(currentFragment);
            }
            transaction.addToBackStack(newTag);
            transaction.commit();

            if(newTag.equals(WebFragment.TAG)) {
                fragmentManager.executePendingTransactions();
            }
            //fragmentManager.executePendingTransactions();


        }
	}

    private void delayedChangeFragment(final Fragment f, final String tag) {
        Log.e("aaa", "inside delayed change fragment");

        fragmentManager.executePendingTransactions();

        final ViewTreeObserver observer = activityContainer.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int totalHeight = activityContainer.getRootView().getHeight();
                int visibleHeight = activityContainer.getHeight();
                int totalHeight2;

                boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

                int statusBar = DDGUtils.getStatusBarHeight(DuckDuckGo.this);
                int navigationBar = DDGUtils.getNavigationBarHeight(DuckDuckGo.this);
                int actionBarHeight = (int) getResources().getDimension(R.dimen.actionbar_height);
                //Log.e("aaa", "status bar: "+statusBar);
                //Log.e("aaa", "navigation bar: "+navigationBar);
                totalHeight2 = totalHeight - statusBar - navigationBar - actionBarHeight;
                visibleHeight = visibleHeight - actionBarHeight;
                /*
                Log.e("aaa", "status bar: "+statusBar);
                Log.e("aaa", "navigation bar: "+navigationBar);
                Log.e("aaa", "actionbar height: "+actionBarHeight);
                Log.e("aaa", "total height: "+totalHeight);
                Log.e("aaa", "total height 2: "+totalHeight2);
                Log.e("aaa", "visible height: "+visibleHeight);
                Log.e("aaa", "total - visible: "+(totalHeight-visibleHeight));
                Log.e("aaa", "status + navigation + actionbar: "+(statusBar + navigationBar + actionBarHeight));*/
                //if(portrait && (totalHeight - visibleHeight) > (statusBar + navigationBar + actionBarHeight)) {
                //if((assistAction || totalHeight2>visibleHeight)) {
                if((totalHeight2>visibleHeight)) {
                    Log.e("aaa", "keyboard open!");
                    //changeFragment(f, tag);
/*
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    Fragment oldFragment = fragmentManager.findFragmentByTag(tag);

                    transaction.setCustomAnimations(R.anim.slide_in_from_bottom2, R.anim.empty, R.anim.empty, R.anim.slide_out_to_bottom2);

                    //transaction.setCustomAnimations(R.anim.temp_animation, R.anim.empty, R.anim.empty, R.anim.slide_out_to_right);
                    if(oldFragment==null || !oldFragment.isAdded()) {
                        transaction.add(fragmentContainer.getId(), f, tag);
                    } else {
                        transaction.show(oldFragment);
                    }
                    transaction.addToBackStack(tag);
                    transaction.commit();
*/
                    //assistAction = false;
                    changeFragment(f, tag);

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN) {
                        observer.removeOnGlobalLayoutListener(this);
                    } else {
                        observer.removeGlobalOnLayoutListener(this);
                    }

                    //activityContainer.getViewTreeObserver().removeOnGlobalLayoutListener();
                } else {
                    Log.e("aaa", "keyboard close!");
                }
            }
        });

        /*
        activityContainer.getViewTreeObserver().addOnGlobalLayoutListener(this);

        activityContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int totalHeight = activityContainer.getRootView().getHeight();
                int visibleHeight = activityContainer.getHeight();

                boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

                int statusBar = DDGUtils.getStatusBarHeight(DuckDuckGo.this);
                int navigationBar = DDGUtils.getNavigationBarHeight(DuckDuckGo.this);
                int actionBarHeight = (int) getResources().getDimension(R.dimen.actionbar_height);
                //Log.e("aaa", "status bar: "+statusBar);
                //Log.e("aaa", "navigation bar: "+navigationBar);
                totalHeight = totalHeight - statusBar - navigationBar - actionBarHeight;
                if(portrait && (totalHeight - visibleHeight) > (statusBar + navigationBar + actionBarHeight)) {
                    Log.e("aaa", "keyboard open!");
                    changeFragment(f, tag);
                    //activityContainer.getViewTreeObserver().removeOnGlobalLayoutListener();
                }

            }
        });*/
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

        DDGActionBarManager.getInstance().updateActionBar(fragmentManager, DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
    }
/*
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
*/
    public boolean isFragmentVisible(String tag) {
        return fragmentManager.findFragmentByTag(tag)!=null && fragmentManager.findFragmentByTag(tag).isVisible();
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
		
		if(feedId != null && feedId.length() != 0) {
			FeedObject feedObject = DDGApplication.getDB().selectFeedById(feedId);
			if(feedObject != null) {
				DDGControlVar.currentFeedObject = feedObject;
			}
		}

        //fragmentManager.findFragmentByTag(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag).setHasOptionsMenu(DDGControlVar.homeScreenShowing || DDGControlVar.mDuckDuckGoContainer.webviewShowing);//aaa overflow temp

        if(fragmentManager.getBackStackEntryCount()>1) {
            String tag = fragmentManager.getBackStackEntryAt(0).getName();
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(tag)).commit();
        }

        DDGActionBarManager.getInstance().updateActionBar(fragmentManager, DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		DDGUtils.displayStats.refreshStats(this);
		super.onConfigurationChanged(newConfig);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ( keyCode == KeyEvent.KEYCODE_MENU ) {
			//drawer.close();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	public DDGAutoCompleteTextView getSearchField() {
		return searchField;
	}

    private void checkForCrashes() {
        if(DDGApplication.isIsReleaseBuild())
            return;
        CrashManager.register(this, DDGConstants.HOCKEY_APP_ID);
    }

    private void checkForUpdates() {
        if(DDGApplication.isIsReleaseBuild())
            return;
        UpdateManager.register(this, DDGConstants.HOCKEY_APP_ID);
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
        Log.e("aaa", "on send to external browser event: "+event.url);
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.url));
		DDGUtils.execIntentIfSafe(this, browserIntent);
	}

    @Subscribe
    public void onSearchExternalEvent(SearchExternalEvent event) {
        DDGUtils.searchExternal(this, event.query );
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
//        MainFeedMenuDialog dialog = new MainFeedMenuDialog(this, event.feedObject);
//        dialog.setpadd
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Menu").setMessage("dialog message").create().show();
        //builder.setItems(new CharSequence[] {"ciao", "due", "tre"}, null).create().show();
        //builder.setSingleChoiceItems()
	}
	
	@Subscribe
	public void onSavedFeedItemLongClick(SavedFeedItemLongClickEvent event) {
        new SavedStoryMenuDialog(this, event.feedObject).show();
    }
	
	@Subscribe
	public void onHistoryItemSelected(HistoryItemSelectedEvent event) {


        //keyboardService.hideKeyboard(getSearchField());//aaa keyboard
		//if(!webFragment.isVisible()) {
            displayScreen(SCREEN.SCR_WEBVIEW, false);
		//}
		BusProvider.getInstance().post(new WebViewShowHistoryObjectEvent(event.historyObject));
	}
	
	@Subscribe
	public void onHistoryItemLongClick(HistoryItemLongClickEvent event) {//to both recent search fragment AND left recent
        //Log.e("aaa", "history")
        if(event.historyObject.isWebSearch()) {
            Log.e("aaa", "history seearch");
            new HistorySearchMenuDialog(this, event.historyObject).show();
        }
        else{
            Log.e("aaa", "history feed");
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
        keyboardService.showKeyboard(getSearchField());//aaa keyboard
	}

    @Subscribe
	public void onSuggestionPaste(SuggestionPasteEvent event) {
        getSearchField().pasteQuery(event.query);
	}
	
	@Subscribe
	public void onSavedSearchPaste(SavedSearchPasteEvent event) {
        getSearchField().pasteQuery(event.query);
        keyboardService.showKeyboard(getSearchField());//aaa keyboard
	}

	@Subscribe
	public void onDisplayScreenEvent(DisplayScreenEvent event) {
        displayScreen(event.screenToDisplay, event.clean);
	}

	@Subscribe
	public void onSearchBarClearEvent(SearchBarClearEvent event) {
        DDGActionBarManager.getInstance().clearSearchBar();
	}

	@Subscribe
	public void onSearchBarSetTextEvent(SearchBarSetTextEvent event) {
        DDGActionBarManager.getInstance().setSearchBarText(event.text);
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
		//handleLeftHomeTextViewClick();
	}

	@Subscribe
	public void onDismissBangPopupEvent(DismissBangPopupEvent event) {
//		if(bangButtonExplanationPopup!=null){
//			bangButtonExplanationPopup.dismiss();
//		}
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
    public void onDisplayHomeScreenEvent(DisplayHomeScreenEvent event) {
        displayHomeScreen();
    }
/*
    @Subscribe
    public void onOverflowButtonClickEvent(OverflowButtonClickEvent event) {

    }
*/
    @Subscribe
    public void onAutoCompleteResultClickEvent(AutoCompleteResultClickEvent event) {
        SuggestObject suggestObject = DDGControlVar.mDuckDuckGoContainer.tempAdapter.getItem(event.position );
        if (suggestObject != null) {
            SuggestType suggestType = suggestObject.getType();
            if(suggestType == SuggestType.TEXT) {
                if(PreferencesManager.getDirectQuery()){
                    String text = suggestObject.getPhrase().trim();
                    if(suggestObject.hasOnlyBangQuery()){
                        getSearchField().addTextWithTrailingSpace(suggestObject.getPhrase());
                    }else{
                        //keyboardService.hideKeyboard(getSearchField());//aaa keyboard
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
        onOptionsItemSelected(event.item);
    }
}
