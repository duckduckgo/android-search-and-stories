package com.duckduckgo.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.MultiHistoryAdapter;
import com.duckduckgo.mobile.android.adapters.RecentResultCursorAdapter;
import com.duckduckgo.mobile.android.adapters.TempAutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.events.ShowAutoCompleteResultsEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewReloadActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewSearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.searchBarEvents.SearchBarSetTextEvent;
import com.duckduckgo.mobile.android.fragment.AboutFragment;
import com.duckduckgo.mobile.android.fragment.FavoriteFragment;
import com.duckduckgo.mobile.android.fragment.FeedFragment;
import com.duckduckgo.mobile.android.fragment.HelpFeedbackFragment;
import com.duckduckgo.mobile.android.fragment.PrefFragment;
import com.duckduckgo.mobile.android.fragment.RecentsFragment;
import com.duckduckgo.mobile.android.fragment.SearchFragment;
import com.duckduckgo.mobile.android.fragment.SourcesFragment;
import com.duckduckgo.mobile.android.fragment.WebFragment;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.views.autocomplete.BackButtonPressedEventListener;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;
import com.squareup.otto.Subscribe;

import java.util.List;

public class Web extends ActionBarActivity {

    private KeyboardService keyboardService;

    private DDGAutoCompleteTextView searchField = null;
    private RelativeLayout searchFieldContainer = null;
    private TextView actionBarTitle;

    private FrameLayout fragmentContainer;

    private FragmentManager fragmentManager;

    public Toolbar toolbar;
    private ActionBar actionBar;

    private SharedPreferences sharedPreferences;

    public boolean savedState = false;

    private View searchBar;
    private View dropShadowDivider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);/*
        keyboardService = new KeyboardService(this);
        supportRequestWindowFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.temp_main2);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));

        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);

        fragmentManager = getSupportFragmentManager();

        DDGControlVar.mDuckDuckGoContainer = (DuckDuckGoContainer) getLastCustomNonConfigurationInstance();
        if(DDGControlVar.mDuckDuckGoContainer == null){
            initializeContainer();
        }

        initActionBar();

        updateActionBar(WebFragment.TAG);*/
        //getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        Intent intent = getIntent();
        Intent searchIntent = new Intent(this, DuckDuckGo.class);
        if(intent!=null) {
            Log.e("aaa", "intent: " + intent.toString());
            searchIntent.putExtra("assist", true);
        } else {
            Log.e("aaa", "intent is null");
        }

        searchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(searchIntent);
        //finish();

    }

    @Override
    public void onResume() {
        super.onResume();/*
        BusProvider.getInstance().register(this);

        Intent intent = getIntent();

        if(intent.hasExtra("url")) {
            String url = intent.getExtras().getString("url");
            SESSIONTYPE sessionType;
            if(intent.hasExtra("sessionType")) {
                sessionType = SESSIONTYPE.getByCode(intent.getExtras().getInt("sessionType"));
            } else {
                sessionType = SESSIONTYPE.SESSION_BROWSE;
            }
            searchOrGoToUrl(url, sessionType);
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();

        //BusProvider.getInstance().unregister(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        // return page container, holding all non-view data
        return DDGControlVar.mDuckDuckGoContainer;
    }

    private void initializeContainer() {
        DDGControlVar.mDuckDuckGoContainer = new DuckDuckGoContainer();

        DDGControlVar.mDuckDuckGoContainer.webviewShowing = false;
        DDGControlVar.mDuckDuckGoContainer.currentScreen = DDGControlVar.START_SCREEN;
        DDGControlVar.mDuckDuckGoContainer.currentFragmentTag = getTagByScreen(DDGControlVar.mDuckDuckGoContainer.currentScreen);
        DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;

        //DDGControlVar.mDuckDuckGoContainer.stopDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.stop);
        DDGControlVar.mDuckDuckGoContainer.stopDrawable = Web.this.getResources().getDrawable(R.drawable.cross);
//    		DDGControlVar.mDuckDuckGoContainer.reloadDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.reload);
        DDGControlVar.mDuckDuckGoContainer.progressDrawable = Web.this.getResources().getDrawable(R.drawable.page_progress);
        DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable = Web.this.getResources().getDrawable(R.drawable.searchfield);
        DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable.setAlpha(150);

        DDGControlVar.mDuckDuckGoContainer.historyAdapter = new MultiHistoryAdapter(this);

        DDGControlVar.mDuckDuckGoContainer.acAdapter = new AutoCompleteResultsAdapter(this);
        DDGControlVar.mDuckDuckGoContainer.tempAdapter = new TempAutoCompleteResultsAdapter(this);
        DDGControlVar.mDuckDuckGoContainer.recentResultCursorAdapter = new RecentResultCursorAdapter(this, DDGApplication.getDB().getCursorSearchHistory());
    }

    private void initActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        searchBar = toolbar.findViewById(R.id.searchBar);

        //dropShadowDivider = findViewById(R.id.dropshadow_top);//todo remove

        searchFieldContainer = (RelativeLayout) toolbar.findViewById(R.id.search_container);
        actionBarTitle = (TextView) toolbar.findViewById(R.id.actionbar_title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Medium.ttf");
        actionBarTitle.setTypeface(typeface);

        searchField = (DDGAutoCompleteTextView) toolbar.findViewById(R.id.searchEditText);
        //getSearchField().setAdapter(DDGControlVar.mDuckDuckGoContainer.acAdapter);
        //getSearchField().setAdapter(DDGControlVar.mDuckDuckGoContainer.tempAdapter);//aaa adapter
        getSearchField().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if(textView == getSearchField() && actionId != EditorInfo.IME_NULL) {
                    keyboardService.hideKeyboard(getSearchField());
                    //getSearchField().dismissDropDown();
                    Log.e("aaa", "on editor action listener, must search or go to url, text is: "+getSearchField().getText().toString());
                    //removeSearchFragment();
                    //searchOrGoToUrl(getSearchField().getTrimmedText());-------------
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
                        //displayScreen(SCREEN.SCR_SEARCH, true);-------------
                    }
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

                /*if(isFragmentVisible(SearchFragment.TAG) || isFragmentVisible(SearchFragment.TAG_HOME_PAGE)) {------------------------
                    BusProvider.getInstance().post(new ShowAutoCompleteResultsEvent(s.length() > 0));
                }*/
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

    private void showActionBarSearchField() {
        toggleActionBarView(false, "");
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

    private void hasOverflowButtonVisible(boolean visible) {
        int endMargin = 0;
        if(visible) {
            endMargin = (int)getResources().getDimension(R.dimen.actionbar_overflow_width);
        }
        toolbar.setContentInsetsRelative(0, endMargin);
        setActionBarMarginEnd(!visible);
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

    private void setHomeButton(boolean visible) {
        ImageButton homeButton = (ImageButton) toolbar.findViewById(R.id.home);
        toolbar.findViewById(R.id.bang).setVisibility(View.GONE);
        if(visible) {
            homeButton.setVisibility(View.VISIBLE);
            homeButton.setOnClickListener(new View.OnClickListener() {
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
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.findViewById(R.id.home).getLayoutParams();
        int padding = 0;
        if(visible) {
            padding = (int) getResources().getDimension(R.dimen.actionbar_margin);
        }
        params.topMargin = padding;
    }

    private void setBangButton() {
        toolbar.findViewById(R.id.home).setVisibility(View.GONE);

        ImageButton bang = (ImageButton) findViewById(R.id.bang);
        bang.setVisibility(View.VISIBLE);
        bang.setOnClickListener(new View.OnClickListener() {
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

    private void updateActionBar(String tag) {
        Log.e("aaa", "update actionbar: "+tag);
        //Log.e("aaa", "starting screen: "+DDGControlVar.START_SCREEN);
        //Log.e("aaa", "current screen: "+DDGControlVar.mDuckDuckGoContainer.currentScreen);

        //dropShadowDivider.setVisibility(View.VISIBLE);----------

        SCREEN screen = getScreenByTag(tag);
        Log.e("aaa", "update actionbar: "+tag+" - screen: "+screen+" - start screen: "+DDGControlVar.START_SCREEN);

        /*
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
        }*/

        switch(screen) {
            case SCR_WEBVIEW:
                showActionBarSearchField();
                //setActionBarShadow(true);
                hasOverflowButtonVisible(true);

                //setActionBarMarginBottom(true);

                //setSearchBarText(DDGControlVar.mDuckDuckGoContainer.currentUrl);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                //setStandardActionBarHeight(true);
                break;
            case SCR_SEARCH:
                showActionBarSearchField();
                //setActionBarShadow(true);
                hasOverflowButtonVisible(screen==SCREEN.SCR_SEARCH_HOME_PAGE);

                //setActionBarMarginBottom(true);
                setHomeButtonMarginTop(false);

                setBangButton();
                //setStandardActionBarHeight(true);
                keyboardService.showKeyboard(getSearchField());
                break;
            default:
                break;
        }

        if(tag.equals(SearchFragment.TAG) || tag.equals(SearchFragment.TAG_HOME_PAGE)) {
            //keyboardService.showKeyboard(getSearchField());
        } else {
            keyboardService.hideKeyboard(getSearchField());//todo check if ok or remove
        }
    }

    private SCREEN getScreenByTag(String tag) {
        if(tag.equals(RecentsFragment.TAG)) {
            return SCREEN.SCR_RECENTS;
        } else if(tag.equals(FavoriteFragment.TAG)) {
            return SCREEN.SCR_FAVORITE;
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
        } else if(tag.equals(SourcesFragment.TAG)) {
            return SCREEN.SCR_SOURCES;
        }
        return SCREEN.SCR_STORIES;
    }

    private String getTagByScreen(SCREEN screen) {
        switch(screen) {
            case SCR_STORIES:
                return FeedFragment.TAG;
            case SCR_RECENTS:
                return RecentsFragment.TAG;
            case SCR_FAVORITE:
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
            case SCR_SOURCES:
                return SourcesFragment.TAG;
            default:
                return FeedFragment.TAG;
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
            case SCR_WEBVIEW:
                //resetSearchBar();//aaa
                //setSearchBarText(DDGControlVar.mDuckDuckGoContainer.currentUrl);

                fragment = new WebFragment();
                tag = WebFragment.TAG;
                break;
            case SCR_SEARCH:
                //resetScreenState();
                fragment = new SearchFragment();
                tag = SearchFragment.TAG;

                break;
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
        if(!tag.equals("")) {
            changeFragment(fragment, tag, displayHomeScreen);
        }
    }

    public void displayScreen(SCREEN screenToDisplay, boolean clean) {
        displayScreen(screenToDisplay, clean, false);
    }

    private void displayHomeScreen() {
        Log.e("aaa", "-------------------------display home screen");
        Intent intent = new Intent(this, DuckDuckGo.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        //displayScreen(DDGControlVar.START_SCREEN, true, true);
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

    public DDGAutoCompleteTextView getSearchField() {
        return searchField;
    }
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

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(newTag.equals(SearchFragment.TAG) || (!backState && fragmentManager.findFragmentByTag(newTag)==null)) {
            Fragment currentFragment = fragmentManager.findFragmentByTag(DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
            if(currentFragment!=null && currentFragment.isAdded()) {// && currentFragment.isVisible()) {
                Log.e("aaa", "hiding: "+DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
                transaction.hide(currentFragment);
                //currentFragment.onHiddenChanged(true);
            } else {
                Log.e("aaa", "NOT hiding: "+DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
            }
            //transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.add(fragmentContainer.getId(), newFragment, newTag);
            if(!newFragment.isVisible()) {
                transaction.show(newFragment);
            }
            transaction.addToBackStack(newTag);
            transaction.commit();
            fragmentManager.executePendingTransactions();
        }
    }

    public void setSearchBarText(String text) {
        Log.e("aaa", "inside set search bar text: "+text);
        if(getSearchField().getText().equals(text) || DDGControlVar.mDuckDuckGoContainer.currentUrl.equals(text)) {
            Log.e("aaa", "text is already set");
            return;
        }
        Log.e("aaa text", "text is not set, changing it now");
        DDGControlVar.mDuckDuckGoContainer.currentUrl = text;
/*
        if(text.startsWith("https://")) {
            text = text.replace("https://", "");
        } else if(text.startsWith("http://")) {
            text = text.replace("http://", "");
        }
        if(text.startsWith("www.")) {
            text = text.replace("www.", "");
        }*/
        getSearchField().setFocusable(false);
        getSearchField().setFocusableInTouchMode(false);
        getSearchField().setText(DDGUtils.getUrlToDisplay(text));
        getSearchField().setFocusable(true);
        getSearchField().setFocusableInTouchMode(true);

    }

    @Subscribe
    public void onSearchBarSetTextEvent(SearchBarSetTextEvent event) {
        setSearchBarText(event.text);
    }
}