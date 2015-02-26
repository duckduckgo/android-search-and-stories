package com.duckduckgo.mobile.android.actionbar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.KeyboardService;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.DisplayHomeScreenEvent;
import com.duckduckgo.mobile.android.events.ShowAutoCompleteResultsEvent;
import com.duckduckgo.mobile.android.fragment.SearchFragment;
import com.duckduckgo.mobile.android.fragment.SourcesFragment;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.views.autocomplete.BackButtonPressedEventListener;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;

public class DDGActionBarManager {

    private Activity activity;
    private Context context;
    private KeyboardService keyboardService;

    private DDGAutoCompleteTextView searchField = null;
    private RelativeLayout searchFieldContainer = null;
    private TextView actionBarTitle;

    private Toolbar toolbar;
    private ActionBar actionBar;

    private View searchBar;
    private View dropShadowDivider;

    public DDGActionBarManager(Activity activity, Context context, Toolbar toolbar, View dropShadowDivider) {
        this.activity = activity;
        this.context = context;
        this.toolbar = toolbar;
        this.dropShadowDivider = dropShadowDivider;
    }

    public void init() {
        searchBar = toolbar.findViewById(R.id.searchBar);
        searchFieldContainer = (RelativeLayout) toolbar.findViewById(R.id.search_container);
        actionBarTitle = (TextView) toolbar.findViewById(R.id.actionbar_title);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Medium.ttf");
        actionBarTitle.setTypeface(typeface);

        searchField = (DDGAutoCompleteTextView) toolbar.findViewById(R.id.searchEditText);
        keyboardService = new KeyboardService(activity);
    }

    public DDGAutoCompleteTextView getSearchField() {
        return this.searchField;
    }

    public void updateActionBar(FragmentManager fragmentManager, String tag) {
        Log.e("aaa", "update actionbar: "+tag);
        //Log.e("aaa", "starting screen: "+DDGControlVar.START_SCREEN);
        //Log.e("aaa", "current screen: "+DDGControlVar.mDuckDuckGoContainer.currentScreen);

        dropShadowDivider.setVisibility(View.VISIBLE);

        SCREEN screen = DDGUtils.getScreenByTag(tag);
        Log.e("aaa", "update actionbar: "+tag+" - screen: "+screen+" - start screen: "+DDGControlVar.START_SCREEN);

        boolean isStartingScreen = DDGControlVar.START_SCREEN==screen;
        if(!tag.equals(SearchFragment.TAG)) {
            Fragment searchFragment = fragmentManager.findFragmentByTag(SearchFragment.TAG);
            if(searchFragment==null || !searchFragment.isVisible()) {
                Log.e("aaa", "ERROR 9 - aka 1, tag: " + tag);
                //getSearchField().clearFocus();

                /*
                getSearchField().setFocusable(false);
                getSearchField().setFocusableInTouchMode(false);
                //getSearchField().setText(text);
                getSearchField().setFocusable(true);
                getSearchField().setFocusableInTouchMode(true);
                */

                searchField.setFocusable(false);
                searchField.setFocusableInTouchMode(false);
                //getSearchField().setText(text);
                searchField.setFocusable(true);
                searchField.setFocusableInTouchMode(true);
            }
        }

        switch(screen) {
            case SCR_STORIES:
                clearSearchBar();
                showSearchField();
                setShadow(true);
                hasOverflowButtonVisible(isStartingScreen);

                setActionBarMarginBottom(true);

                setHomeButton(!isStartingScreen);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
                break;
            case SCR_RECENTS:
                showSearchField();
                setShadow(false);
                setActionBarMarginBottom(false);
                hasOverflowButtonVisible(isStartingScreen);
                setHomeButton(!isStartingScreen);
                setHomeButtonMarginTop(true);
                setStandardActionBarHeight(false);
                break;
            case SCR_FAVORITE:
                showSearchField();
                setShadow(false);
                setActionBarMarginBottom(false);
                hasOverflowButtonVisible(isStartingScreen);
                setHomeButton(!isStartingScreen);
                setHomeButtonMarginTop(true);
                setStandardActionBarHeight(false);
                break;
            case SCR_WEBVIEW:
                showSearchField();
                setShadow(true);
                hasOverflowButtonVisible(true);

                setActionBarMarginBottom(true);

                //setSearchBarText(DDGControlVar.mDuckDuckGoContainer.currentUrl);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
                break;
            case SCR_SEARCH:
            case SCR_SEARCH_HOME_PAGE:
                showSearchField();
                setShadow(true);
                hasOverflowButtonVisible(screen==SCREEN.SCR_SEARCH_HOME_PAGE);

                setActionBarMarginBottom(true);
                setHomeButtonMarginTop(false);

                setBangButton();
                setStandardActionBarHeight(true);
                keyboardService.showKeyboard(getSearchField());
                break;
            case SCR_ABOUT:
                showTitle(tag, context.getResources().getString(R.string.about));
                setShadow(true);
                hasOverflowButtonVisible(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
                break;
            case SCR_HELP:
                showTitle(tag, context.getResources().getString(R.string.help_feedback));
                setShadow(true);
                hasOverflowButtonVisible(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
                break;
            case SCR_SETTINGS:
                showTitle(tag, context.getResources().getString(R.string.settings));
                setShadow(true);
                hasOverflowButtonVisible(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
                break;
            case SCR_SOURCES:
                showTitle(tag, context.getResources().getString(R.string.change_sources));
                setShadow(true);
                hasOverflowButtonVisible(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
            default:
                break;
        }

        if(tag.equals(SearchFragment.TAG) || tag.equals(SearchFragment.TAG_HOME_PAGE)) {
            //keyboardService.showKeyboard(getSearchField());
        } else {
            keyboardService.hideKeyboard(getSearchField());//todo check if ok or remove
        }
    }

    public void clearSearchBar() {/*
        getSearchField().setText("");
        getSearchField().setCompoundDrawables(null, null, null, null);
        //getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable);//aaa
        */
        searchField.setText("");
        searchField.setCompoundDrawables(null, null, null, null);
        //getSearchField().setBackgroundDrawable(DDGControlVar.mDuckDuckGoContainer.searchFieldDrawable);//aaa
    }

    private void showSearchField() {
        toggleActionBarView(false, "", "");
    }

    private void showTitle(String tag, String newTitle) {
        toggleActionBarView(true, tag, newTitle);
    }

    private void toggleActionBarView(boolean showTitle, String tag, String newTitle) {
        if(showTitle) {
            searchFieldContainer.setVisibility(View.GONE);
            actionBarTitle.setVisibility(View.VISIBLE);
            actionBarTitle.setText(newTitle);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionBarTitle.getLayoutParams();
            int resId = R.dimen.actionbar_title_left_margin;
            if(tag.equals(SourcesFragment.TAG)) {
                resId = R.dimen.actionbar_sources_title_left_margin;
            }
            int leftMargin = (int) context.getResources().getDimension(resId);
            params.leftMargin = leftMargin;
        } else {
            searchFieldContainer.setVisibility(View.VISIBLE);
            actionBarTitle.setVisibility(View.GONE);
        }
    }

    private void setShadow(boolean visible) {
        dropShadowDivider.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void hasOverflowButtonVisible(boolean visible) {
        int endMargin = 0;
        if(visible) {
            endMargin = (int) context.getResources().getDimension(R.dimen.actionbar_overflow_width);
        }
        toolbar.setContentInsetsRelative(0, endMargin);
        setActionBarMarginEnd(!visible);
    }

    private void setActionBarMarginBottom(boolean visible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchFieldContainer.getLayoutParams();
        int margin = 0;
        if(visible) {
            margin = (int) context.getResources().getDimension(R.dimen.actionbar_margin);
        }
        params.bottomMargin = margin;
    }

    private void setActionBarMarginStart(boolean visible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchFieldContainer.getLayoutParams();
        int margin = 0;
        if(visible) {
            margin = (int) context.getResources().getDimension(R.dimen.actionbar_margin);
        }
        params.leftMargin = margin;
    }

    private void setActionBarMarginEnd(boolean visible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchFieldContainer.getLayoutParams();
        int margin = 0;
        if(visible) {
            margin = (int) context.getResources().getDimension(R.dimen.actionbar_margin);
        }
        params.rightMargin = margin;
    }

    private void setStandardActionBarHeight(boolean normal) {
        //int height = 0;
        int resId;
        if(normal) {
            resId = R.dimen.actionbar_height;
            //height = (int) context.getResources().getDimension(R.dimen.actionbar_height);
        } else {
            resId = R.dimen.actionbar_height_low;
            //height = (int) context.getResources().getDimension(R.dimen.actionbar_height_low);
        }
        //toolbar.setMinimumHeight(height);
        toolbar.getLayoutParams().height = (int) context.getResources().getDimension(resId);;
    }

    private void setHomeButton(boolean visible) {
        ImageButton homeButton = (ImageButton) toolbar.findViewById(R.id.home);
        toolbar.findViewById(R.id.bang).setVisibility(View.GONE);
        if(visible) {
            homeButton.setVisibility(View.VISIBLE);
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //displayHomeScreen();
                    BusProvider.getInstance().post(new DisplayHomeScreenEvent());
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
            padding = (int) context.getResources().getDimension(R.dimen.actionbar_margin);
        }
        params.topMargin = padding;
    }

    private void setBangButton() {
        toolbar.findViewById(R.id.home).setVisibility(View.GONE);

        ImageButton bang = (ImageButton) toolbar.findViewById(R.id.bang);
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

    public void setSearchBarText(String text) {
        Log.e("aaa", "inside set search bar text: "+text);
        if(getSearchField().getText().toString().equals(text) || DDGControlVar.mDuckDuckGoContainer.currentUrl.equals(text)) {
            Log.e("aaa", "text is already set");
            //return;
        }
        if(DDGControlVar.homeScreenShowing) {
            DDGControlVar.mDuckDuckGoContainer.currentUrl = "";
            return;
        }
        Log.e("aaa", "text is not set, changing it now");
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
        searchField.setFocusable(false);
        searchField.setFocusableInTouchMode(false);
        searchField.setText(DDGUtils.getUrlToDisplay(text));
        searchField.setFocusable(true);
        searchField.setFocusableInTouchMode(true);

    }

    public void resetScreenState() {
        clearSearchBar();
        DDGControlVar.currentFeedObject = null;
        DDGControlVar.mDuckDuckGoContainer.sessionType = SESSIONTYPE.SESSION_BROWSE;
        resetSearchBar();
    }

    public void resetSearchBar() {
        //searchBar.setBackgroundResource(R.color.topbar_background);
        dropShadowDivider.setVisibility(View.VISIBLE);
    }

}
