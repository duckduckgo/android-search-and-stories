package com.duckduckgo.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.actionbar.DDGActionBarManager;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.ConfirmDialogOkEvent;
import com.duckduckgo.mobile.android.events.DisplayHomeScreenEvent;
import com.duckduckgo.mobile.android.events.DisplayScreenEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewBackPressActionEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewClearCacheEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelCategoryFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelSourceFilterEvent;
import com.duckduckgo.mobile.android.fragment.AboutFragment;
import com.duckduckgo.mobile.android.fragment.FavoriteFragment;
import com.duckduckgo.mobile.android.fragment.FeedFragment;
import com.duckduckgo.mobile.android.fragment.HelpFeedbackFragment;
import com.duckduckgo.mobile.android.fragment.PrefFragment;
import com.duckduckgo.mobile.android.fragment.RecentsFragment;
import com.duckduckgo.mobile.android.fragment.SearchFragment;
import com.duckduckgo.mobile.android.fragment.SourcesFragment;
import com.duckduckgo.mobile.android.fragment.WebFragment;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.TorIntegrationProvider;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;
import com.duckduckgo.mobile.android.views.webview.DDGWebView;
import com.squareup.otto.Subscribe;

import java.util.List;

public class TempPreferences extends ActionBarActivity {

    private FrameLayout fragmentContainer;

    private FragmentManager fragmentManager;

    private DDGActionBarManager actionBar;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_main);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));

        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //actionBar = new DDGActionBarManager(this, this, toolbar, findViewById(R.id.dropshadow_top));
        //actionBar.init();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.e("aaa", "---------------inside on back stack changed");
                Log.e("aaa", "back stack count: "+fragmentManager.getBackStackEntryCount());
                showAllFragments();
                if(fragmentManager.getBackStackEntryCount() > 0) {
                    String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                    if(tag!=null) {/*
                        DDGControlVar.mDuckDuckGoContainer.currentFragmentTag = tag;
                        if(!tag.equals(WebFragment.TAG) && !DDGControlVar.mDuckDuckGoContainer.webviewShowing) {
                            DDGControlVar.mDuckDuckGoContainer.prevScreen = DDGControlVar.mDuckDuckGoContainer.currentScreen;
                        }
                        DDGControlVar.mDuckDuckGoContainer.currentScreen = DDGUtils.getScreenByTag(tag);
                        DDGControlVar.mDuckDuckGoContainer.webviewShowing = tag.equals(WebFragment.TAG);
                        DDGControlVar.homeScreenShowing = DDGControlVar.mDuckDuckGoContainer.currentScreen == DDGControlVar.START_SCREEN;

                        fragmentManager.findFragmentByTag(tag).setHasOptionsMenu(DDGControlVar.homeScreenShowing || DDGControlVar.mDuckDuckGoContainer.webviewShowing);
                        */

                        //actionBar.updateActionBar(fragmentManager, tag);
                    }
                    Log.e("aaa", "inside back stack, current tag: "+DDGControlVar.mDuckDuckGoContainer.currentFragmentTag);
                    showAllFragments();
                }
            }
        });

        if(savedInstanceState==null) {
            displaySettingsHomeScreen();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        TorIntegrationProvider.getInstance(this).prepareTorSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onBackPressed() {
        Log.e("aaa", "on back pressed");
        if(fragmentManager.getBackStackEntryCount()==1) {
            //fragmentManager.popBackStackImmediate();
            Log.e("aaa", "on back pressed finish");
            finish();
            overridePendingTransition(R.anim.empty, R.anim.slide_out_to_bottom2);
            //super.onBackPressed();
        }
        else {
            Log.e("aaa", "on back pressed super");
            super.onBackPressed();
        }
    }

    private void displaySettingsHomeScreen() {
        Log.e("aaa", "-------------------------display settings home screen");

        displayScreen(SCREEN.SCR_SETTINGS, true, true);
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
            case SCR_ABOUT:

                fragment = new AboutFragment();
                tag = AboutFragment.TAG;
                break;
            case SCR_HELP:
                //actionBar.resetScreenState();
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

    private void changeFragment(Fragment newFragment, String newTag, boolean displayHomeScreen) {
        Log.e("aaa", "inside changefragment, newtag: " + newTag);
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
                //transaction.hide(currentFragment);
                //currentFragment.onHiddenChanged(true);
            }
            //transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            Fragment f = fragmentManager.findFragmentByTag(newTag);
            if(newTag.equals(WebFragment.TAG) || newTag.equals(SourcesFragment.TAG) || newTag.equals(AboutFragment.TAG  )) {
                transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.empty, R.anim.empty, R.anim.slide_out_to_right);
            } else if(newTag.equals(PrefFragment.TAG) || newTag.equals(HelpFeedbackFragment.TAG)) {
                transaction.setCustomAnimations(R.anim.slide_in_from_bottom2, R.anim.empty, R.anim.empty, R.anim.slide_out_to_bottom);
            }
            if(f==null) {
                transaction.add(fragmentContainer.getId(), newFragment, newTag);
            } else {
                transaction.show(newFragment);
            }
            if(currentFragment!=null && currentFragment.isAdded()) {// && currentFragment.isVisible()) {
                transaction.hide(currentFragment);
            }
            transaction.addToBackStack(newTag);
            transaction.commit();
            fragmentManager.executePendingTransactions();
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

    public void clearRecentSearch() {
        DDGControlVar.mDuckDuckGoContainer.historyAdapter.sync();
    }

    @Subscribe
    public void onDisplayScreenEvent(DisplayScreenEvent event) {
        displayScreen(event.screenToDisplay, event.clean);
    }

    @Subscribe
    public void onConfirmDialogOkEvent(ConfirmDialogOkEvent event) {
        switch(event.action) {
            case DDGConstants.CONFIRM_CLEAR_HISTORY:
                DDGApplication.getDB().deleteHistory();
                //clearRecentSearch();// todo
                break;
            case DDGConstants.CONFIRM_CLEAR_COOKIES:
                DDGWebView.clearCookies();
                break;
            case DDGConstants.CONFIRM_CLEAR_WEB_CACHE:
                BusProvider.getInstance().post(new WebViewClearCacheEvent());// todo
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onDisplayHomeScreenEvent(DisplayHomeScreenEvent event) {

    /*
        Intent intent = new Intent(this, DuckDuckGo.class);
        intent.putExtra("home", true);
        startActivity(new Intent(this, DuckDuckGo.class));
        finish();
        overridePendingTransition(R.anim.slide_out_to_bottom2, R.anim.empty);*/
        finish();
        overridePendingTransition(R.anim.empty, R.anim.slide_out_to_bottom2);
    }
}