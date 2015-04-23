package com.duckduckgo.mobile.android.actionbar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.KeyboardService;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.DisplayHomeScreenEvent;
import com.duckduckgo.mobile.android.events.OverflowButtonClickEvent;
import com.duckduckgo.mobile.android.events.ShowAutoCompleteResultsEvent;
import com.duckduckgo.mobile.android.fragment.SearchFragment;
import com.duckduckgo.mobile.android.fragment.SourcesFragment;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.views.SlidingTabLayout;
import com.duckduckgo.mobile.android.views.autocomplete.BackButtonPressedEventListener;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;
import com.squareup.otto.Bus;

public final class DDGActionBarManager implements View.OnClickListener, View.OnLongClickListener {

    private Activity activity;
    private Context context;
    private KeyboardService keyboardService;

    private DDGAutoCompleteTextView searchField = null;
    private RelativeLayout searchFieldContainer = null;
    private TextView actionBarTitle;

    private ImageButton homeButton;
    private ImageButton bangButton;
    private ImageButton overflowButton;

    private ProgressBar progressBar;
    private ProgressBarAnimation progressBarAnimation = null;
    private int oldProgress = 0;
    private boolean isProgressVisible = false;
    private SlidingTabLayout slidingTabLayout;

    private boolean isTabAnimating = false;

    private Toolbar toolbar;
    private ActionBar actionBar;

    private View searchBar;
    private View dropShadowDivider;

    private SCREEN screen;

    private static final DDGActionBarManager DDG_ACTION_BAR_MANAGER = new DDGActionBarManager();

    public static DDGActionBarManager getInstance() {
        return DDG_ACTION_BAR_MANAGER;
    }

    private DDGActionBarManager() {
        // No instances.
    }
/*
    public DDGActionBarManager(Activity activity, Context context, Toolbar toolbar, View dropShadowDivider) {
        this.activity = activity;
        this.context = context;
        this.toolbar = toolbar;
        this.dropShadowDivider = dropShadowDivider;
    }
*/
    public void init(Activity activity, Context context, Toolbar toolbar, View dropShadowDivider) {

        this.activity = activity;
        this.context = context;
        this.toolbar = toolbar;
        this.dropShadowDivider = dropShadowDivider;

        searchBar = toolbar.findViewById(R.id.searchBar);
        searchFieldContainer = (RelativeLayout) toolbar.findViewById(R.id.search_container);
        actionBarTitle = (TextView) toolbar.findViewById(R.id.actionbar_title);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto_Medium.ttf");
        actionBarTitle.setTypeface(typeface);
        slidingTabLayout = (SlidingTabLayout) toolbar.findViewById(R.id.sliding_tabs);
        boolean slidingIsNull = slidingTabLayout == null;
        Log.e("aaa", "+++++++++++++ sliding is null: "+slidingIsNull);

        progressBar = (ProgressBar) toolbar.findViewById(R.id.progress_bar);

        searchField = (DDGAutoCompleteTextView) toolbar.findViewById(R.id.searchEditText);

        homeButton = (ImageButton) toolbar.findViewById(R.id.home);
        homeButton.setOnClickListener(this);
        homeButton.setOnLongClickListener(this);
        bangButton = (ImageButton) toolbar.findViewById(R.id.bang);
        bangButton.setOnClickListener(this);
        bangButton.setOnLongClickListener(this);
        overflowButton = (ImageButton) toolbar.findViewById(R.id.overflow);
        overflowButton.setOnClickListener(this);
        overflowButton.setOnLongClickListener(this);

        keyboardService = new KeyboardService(activity);
    }

    public DDGAutoCompleteTextView getSearchField() {
        return this.searchField;
    }

    public SlidingTabLayout getSlidingTabLayout() {
        return this.slidingTabLayout;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.home:
                Log.e("aaa", "home button clicked");
                stopProgress();
                setProgressBarVisible(false);
                BusProvider.getInstance().post(new DisplayHomeScreenEvent());
                break;
            case R.id.bang:
                Log.e("aaa", "bang button clicked");
                stopProgress();
                setProgressBarVisible(false);
                //keyboardService.showKeyboard(getSearchField());//aaa keyboard
                getSearchField().addBang();
                break;
            case R.id.overflow:
                BusProvider.getInstance().post(new OverflowButtonClickEvent(toolbar));
                //BusProvider.getInstance().post(new OverflowButtonClickEvent(toolbar));
            default:
                break;
        }

    }

    @Override
    public boolean onLongClick(View v) {
        switch(v.getId()) {
            case R.id.home:
                Log.e("aaa", "home long click");
                Toast.makeText(context, "Home", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.bang:
                Log.e("aaa", "bang long click");
                Toast.makeText(context, "Bang", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    public void updateActionBar(FragmentManager fragmentManager, String tag) {
        Log.e("aaa", "update actionbar: "+tag);
        //Log.e("aaa", "starting screen: "+DDGControlVar.START_SCREEN);
        //Log.e("aaa", "current screen: "+DDGControlVar.mDuckDuckGoContainer.currentScreen);

        dropShadowDivider.setVisibility(View.VISIBLE);

        SCREEN screen = DDGUtils.getScreenByTag(tag);
        this.screen = screen;
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


                /*
                searchField.setFocusable(false);
                searchField.setFocusableInTouchMode(false);
                //getSearchField().setText(text);

                searchField.setFocusable(true);
                searchField.setFocusableInTouchMode(true);*/
            }
        }

        //keyboardService.hideKeyboard(searchField);

        int standardMargin = (int) context.getResources().getDimension(R.dimen.actionbar_margin);
        int overflowVisibleRightMargin = 0;
        int actionButtonVisibleLeftMargin = (int) context.getResources().getDimension(R.dimen.actionbar_searchbar_left_margin_with_button);
        overflowVisibleRightMargin = actionButtonVisibleLeftMargin;
        Log.e("aaa^^^^^^^", "standard margin: "+standardMargin+" - overflow visible right: "+overflowVisibleRightMargin+ " - action button visible left: "+actionButtonVisibleLeftMargin);

        int leftMargin , rightMargin;

        //if(slidingTabLayout.getVisibility()==V)


        switch(screen) {
            case SCR_STORIES:
                clearSearchBar();
                /*
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                */
                //keyboardService.hideKeyboard(searchField);
                //searchField.clearFocus();

                showSearchField();//


                //setShadow(true);
                //int standardMargin = (int) context.getResources().getDimension(R.dimen.actionbar_margin);
                //int overflowRightMargin = 0;

                leftMargin = isStartingScreen ? standardMargin : actionButtonVisibleLeftMargin;
                rightMargin = (true||isStartingScreen) ? overflowVisibleRightMargin : standardMargin;

                setActionBarMargins(leftMargin, standardMargin, rightMargin, standardMargin);

                //hasOverflowButtonVisible(isStartingScreen);

                //setActionBarMarginBottom(true);//aaa

                setHomeButton(!isStartingScreen);
                //setOverflowButton(isStartingScreen);
                setOverflowButton(true);
                setOverflowButtonMarginTop(false);

                setHomeButtonMarginTop(false);

                setStandardActionBarHeight(true);

                setTabLayout(false);

                setProgressBarVisible(false);
                break;
            case SCR_RECENTS:

               clearSearchBar();

                showSearchField();

                //setShadow(false);

                leftMargin = isStartingScreen ? standardMargin : actionButtonVisibleLeftMargin;
                rightMargin = (true||isStartingScreen) ? overflowVisibleRightMargin : standardMargin;

                setActionBarMargins(leftMargin, standardMargin, rightMargin, /*0);//*/ standardMargin);

                setOverflowButton(true);
                //setOverflowButtonMarginTop(true);
                setOverflowButtonMarginTop(false);

                setHomeButton(!isStartingScreen);
                //setHomeButtonMarginTop(true);
                setHomeButtonMarginTop(false);

                //setStandardActionBarHeight(false);
                setStandardActionBarHeight(true);

                //setTabLayout(true);

                setProgressBarVisible(false);
                break;
            case SCR_FAVORITE:
                clearSearchBar();

                showSearchField();
                //searchField.clearFocus();

                //setShadow(false);


                leftMargin = isStartingScreen ? standardMargin : actionButtonVisibleLeftMargin;
                rightMargin = (true||isStartingScreen) ? overflowVisibleRightMargin : standardMargin;

                setActionBarMargins(leftMargin, standardMargin, rightMargin, /*0*/standardMargin);

                //setActionBarMarginBottom(false);
                //hasOverflowButtonVisible(isStartingScreen);

                //setOverflowButton(isStartingScreen);
                setOverflowButton(true);
                //setOverflowButtonMarginTop(true);
                setOverflowButtonMarginTop(false);

                setHomeButton(!isStartingScreen);
                //setHomeButtonMarginTop(true);
                setHomeButtonMarginTop(false);

                //setStandardActionBarHeight(false);
                setStandardActionBarHeight(true);

                //setTabLayout(true);

                setProgressBarVisible(false);
                break;
            case SCR_WEBVIEW:
                showSearchField();

                //searchField.clearFocus();

                //setShadow(true);

                setActionBarMargins(actionButtonVisibleLeftMargin, standardMargin, overflowVisibleRightMargin, standardMargin);

                hasOverflowButtonVisible(true);

                //setActionBarMarginBottom(true);

                //setSearchBarText(DDGControlVar.mDuckDuckGoContainer.currentUrl);

                setOverflowButton(true);
                setOverflowButtonMarginTop(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);

                setTabLayout(false);

                setProgressBarVisible(true);
                break;
            case SCR_SEARCH:
            case SCR_SEARCH_HOME_PAGE:
                showSearchField();
                //setShadow(true);

                rightMargin = false&&screen==SCREEN.SCR_SEARCH ? standardMargin : overflowVisibleRightMargin;
                setActionBarMargins(actionButtonVisibleLeftMargin, standardMargin, rightMargin, standardMargin);

                hasOverflowButtonVisible(true||screen==SCREEN.SCR_SEARCH_HOME_PAGE);

                //setActionBarMarginBottom(true);
                setHomeButtonMarginTop(false);
                setOverflowButton(true||    screen == SCREEN.SCR_SEARCH_HOME_PAGE);
                setOverflowButtonMarginTop(false);

                setBangButton();
                setStandardActionBarHeight(true);
                //keyboardService.showKeyboard(getSearchField());//aaa keyboard

                setTabLayout(false);

                setProgressBarVisible(false);
                break;
            case SCR_ABOUT:
                showTitle(tag, context.getResources().getString(R.string.about));
                //setShadow(true);
                hasOverflowButtonVisible(false);
                setOverflowButton(false);
                setOverflowButtonMarginTop(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);

                setTabLayout(false);

                setProgressBarVisible(false);
                break;
            case SCR_HELP:
                showTitle(tag, context.getResources().getString(R.string.help_feedback));
                //setShadow(true);
                hasOverflowButtonVisible(false);
                setOverflowButton(false);
                setOverflowButtonMarginTop(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);

                setTabLayout(false);

                setProgressBarVisible(false);
                break;
            case SCR_SETTINGS:

                //
                // searchField.clearFocus();

                showTitle(tag, context.getResources().getString(R.string.settings));
                //setShadow(true);
                hasOverflowButtonVisible(false);
                setOverflowButton(false);
                setOverflowButtonMarginTop(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);

                setTabLayout(false);

                setProgressBarVisible(false);
                break;
            case SCR_SOURCES:
                showTitle(tag, context.getResources().getString(R.string.change_sources));
                //setShadow(true);
                hasOverflowButtonVisible(false);
                setOverflowButton(false);
                setOverflowButtonMarginTop(false);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);

                setTabLayout(false);

                setProgressBarVisible(false);
            default:
                break;
        }

        if(!tag.equals(SearchFragment.TAG) && !tag.equals(SearchFragment.TAG_HOME_PAGE)) {
            keyboardService.hideKeyboardDelayed(searchField);
        }

        if(tag.equals(SearchFragment.TAG) || tag.equals(SearchFragment.TAG_HOME_PAGE)) {
            Log.e("aaa", "]]]]]]]]]new tag == search fragment, nothing here");
            //keyboardService.showKeyboard(getSearchField());
        } else {
            Log.e("aaa", "]]]]]]]]]]new tag != search fragment should hide the keyboard");
            //keyboardService.hideKeyboard(getSearchField());//todo check if ok or remove
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


    public void toggleProgressBarVisibility(boolean visible, boolean withAnimation) {
        View progressBarContainer = toolbar.findViewById(R.id.progress_container);
        if((!visible && progressBarContainer.getVisibility()==View.GONE) || (visible && progressBarContainer.getVisibility()==View.VISIBLE)) {
            return;
        }

        if(withAnimation) {
            int resId = visible ? R.anim.show_progressbar : R.anim.hide_progressbar;
            Animation animation = AnimationUtils.loadAnimation(context, resId);
            progressBarContainer.setAnimation(animation);
        }
        progressBarContainer.setVisibility(visible ? View.VISIBLE : View.GONE);

    }

    public void setProgressBarVisible(boolean visible) {
        isProgressVisible = visible;

        if(!visible) {
            toggleProgressBarVisibility(false, false);
        } else {
            toggleProgressBarVisibility(true, true);
        }
    }

    public void setProgress(int newProgress) {
        Log.e("aaa", "set progress, new progress: "+newProgress);
        Log.e("aaa", "is progress visible: "+isProgressVisible);
        if(!isProgressVisible) {
            return;
        }
        if(newProgress<oldProgress) {
            return;
        }

        toggleProgressBarVisibility(true, true);

        progressBarAnimation = new ProgressBarAnimation(progressBar, oldProgress, newProgress);
        progressBarAnimation.setDuration(500);
        progressBar.startAnimation(progressBarAnimation);
        oldProgress = newProgress;

        if(oldProgress>=100) {
            oldProgress = 0;
            toggleProgressBarVisibility(false, true);
        }
    }

    public void stopProgress() {
        //Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.actionbar_button_fade_out);
        //progressBar.startAnimation(fadeOut);
        //progressBar.setVisibility(View.GONE);
        progressBar.clearAnimation();
        progressBar.setProgress(0);
        oldProgress = 0;
    }

    private void showSearchField() {
        toggleActionBarView(true);
    }

    private void showTitle(String tag, String title) {
        toggleActionBarView(false);
        setTitle(tag, title);
    }

    private void setTitle(String tag, String title) {
        actionBarTitle.setText(title);
        //setTitleLeftMargin(tag);
    }
/*
    private void setTitleLeftMargin(String tag) {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionBarTitle.getLayoutParams();
        int resId = tag.equals(SourcesFragment.TAG) ? R.dimen.actionbar_sources_title_left_margin : R.dimen.actionbar_title_left_margin;
        final int currentLeftMargin = params.leftMargin;
        final int newLeftMargin = (int) context.getResources().getDimension(resId);
        if(currentLeftMargin!=newLeftMargin) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ValueAnimator valueAnimator = ValueAnimator.ofInt(currentLeftMargin, newLeftMargin);
                valueAnimator.setDuration(250);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionBarTitle.getLayoutParams();
                        params.leftMargin = (Integer) animation.getAnimatedValue();
                        actionBarTitle.setLayoutParams(params);
                    }
                });
                valueAnimator.start();
            } else {
                params.leftMargin = newLeftMargin;
            }
        }
    }
*/
    private void toggleActionBarView(boolean searchVisible) {
        searchFieldContainer.setVisibility(searchVisible ? View.VISIBLE : View.GONE);
        actionBarTitle.setVisibility(searchVisible ? View.GONE : View.VISIBLE);
    }

    private void toggleActionBarView(boolean showTitle, String tag, String newTitle) {
        if(showTitle) {
            searchFieldContainer.setVisibility(View.GONE);
            actionBarTitle.setVisibility(View.VISIBLE);
            actionBarTitle.setText(newTitle);
            final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionBarTitle.getLayoutParams();
            int resId = tag.equals(SourcesFragment.TAG) ? R.dimen.actionbar_sources_title_left_margin : R.dimen.actionbar_title_left_margin;
            final int currentLeftMargin = params.leftMargin;
            final int newLeftMargin = (int) context.getResources().getDimension(resId);
/*
            if(currentLeftMargin!=newLeftMargin) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(currentLeftMargin, newLeftMargin);
                    valueAnimator.setDuration(250);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) actionBarTitle.getLayoutParams();
                            params.leftMargin = (Integer) animation.getAnimatedValue();
                            actionBarTitle.setLayoutParams(params);
                        }
                    });
                    valueAnimator.start();
                } else {
                    params.leftMargin = newLeftMargin;
                }
            }
            */
/*
            if(currentLeftMargin!=newLeftMargin) {
                Animation animation = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        //super.applyTransformation(interpolatedTime, t);
                        if(currentLeftMargin>newLeftMargin) {
                            params.leftMargin = currentLeftMargin - ((int) (newLeftMargin * interpolatedTime));
                        } else {
                            params.leftMargin = currentLeftMargin + ((int) (newLeftMargin * interpolatedTime));
                        }
                        actionBarTitle.setLayoutParams(params);
                    }
                };
                animation.setDuration(250);
                actionBarTitle.startAnimation(animation);
            }
*/
            //params.leftMargin = leftMargin;
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
        //toolbar.setContentInsetsRelative(0, endMargin);
        //setActionBarMarginEnd(!visible);//aaa
    }

    private void setActionBarMargins(int newLeft, int newTop, int newRight, int newBottom) {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchFieldContainer.getLayoutParams();
        //params.setMargins(newLeft, newTop, newRight, newBottom);
        //params.rightMargin = newRight;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            ValueAnimator leftValueAnimator = ValueAnimator.ofInt(params.leftMargin, newLeft);
            leftValueAnimator.setDuration(250);
            leftValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    params.leftMargin = (Integer) animation.getAnimatedValue();
                    searchFieldContainer.setLayoutParams(params);
                }
            });
            Log.e("aaa", "set margins old right: " + params.rightMargin + " - new right: " + newRight);
            ValueAnimator rightValueAnimator = ValueAnimator.ofInt(params.rightMargin, newRight);
            rightValueAnimator.setDuration(250);
            rightValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    params.rightMargin = (Integer) animation.getAnimatedValue();
                    searchFieldContainer.setLayoutParams(params);
                }
            });
            ValueAnimator topValueAnimator = ValueAnimator.ofInt(params.topMargin, newTop);
            topValueAnimator.setDuration(250);
            topValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    params.topMargin = (Integer) animation.getAnimatedValue();
                    searchFieldContainer.setLayoutParams(params);
                }
            });
            ValueAnimator bottomValueAnimator = ValueAnimator.ofInt(params.bottomMargin, newBottom);
            bottomValueAnimator.setDuration(250);
            bottomValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    params.bottomMargin = (Integer) animation.getAnimatedValue();
                    searchFieldContainer.setLayoutParams(params);
                }
            });

            leftValueAnimator.start();
            rightValueAnimator.start();
            topValueAnimator.start();
            bottomValueAnimator.start();
        } else {
            params.setMargins(newLeft, newTop, newRight, newBottom);
        }
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
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchFieldContainer.getLayoutParams();
        int resId = visible ? R.dimen.actionbar_searchbar_left_margin_with_button : R.dimen.actionbar_margin;
        final int newLeftMargin = (int) context.getResources().getDimension(resId);
        final int currentLeftMargin = params.leftMargin;
        Log.e("aaa+++++", "set action bar margin start, currentLeft: "+currentLeftMargin+" newLeft: "+newLeftMargin);

        params.leftMargin = newLeftMargin;
/*
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);

                //if(currentLeftMargin>newLeftMargin) {
                    //params.leftMargin = ((int) (newLeftMargin * interpolatedTime));
                params.setMargins(((int) (newLeftMargin * interpolatedTime)), params.topMargin, params.rightMargin, params.rightMargin);
                //} else {
                    //params.leftMargin = currentLeftMargin + ((int) (newLeftMargin * interpolatedTime));
                //}
                searchField.setLayoutParams(params);
            }
        };
        animation.setDuration(250);
        searchField.startAnimation(animation);
        */
    }

    private void setActionBarMarginEnd(boolean visible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchFieldContainer.getLayoutParams();
        int margin = visible ? (int) context.getResources().getDimension(R.dimen.actionbar_margin) : 0;
        params.rightMargin = margin;
    }

    private void setStandardActionBarHeight(boolean normal) {
        //int height = 0;
        int resId = normal ? R.dimen.actionbar_height : R.dimen.actionbar_height_low;
        int newHeight = (int) context.getResources().getDimension(resId);
        //final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) searchBar.getLayoutParams();


        //toolbar.setMinimumHeight(height);
        //toolbar.getLayoutParams().height = (int) context.getResources().getDimension(resId);

        //toolbar.getLayoutParams().height = newHeight;

        if(false && Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {/*
            ValueAnimator heightValueAnimator = ValueAnimator.ofInt(params.height, newHeight);
            heightValueAnimator.setDuration(250);
            heightValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    params.height = (Integer) animation.getAnimatedValue();
                    //toolbar.setLayoutParams(params);
                    searchBar.setLayoutParams(params);
                }
            });
            heightValueAnimator.start();*/
        } else {
            params.height = newHeight;
        }

    }

    private void setHomeButton(boolean visible) {

        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.actionbar_button_fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.actionbar_button_fade_out);

        if (bangButton.getVisibility() == View.VISIBLE) {
            Log.e("aaa", "set home button: bang -> gone");
            bangButton.setAnimation(fadeOut);
            bangButton.setVisibility(View.GONE);
        }

        if(visible) {
            if (homeButton.getVisibility() == View.GONE) {
                Log.e("aaa", "set home button: home -> visible");
                homeButton.setAnimation(fadeIn);
                homeButton.setVisibility(View.VISIBLE);
            }
        } else if(homeButton.getVisibility() == View.VISIBLE) {
            Log.e("aaa", "set home button: home -> gone");
            homeButton.setAnimation(fadeOut);
            homeButton.setVisibility(View.GONE);
        }

        //setActionBarMarginStart(visible);//aaa

        /*

        ImageButton homeButton = (ImageButton) toolbar.findViewById(R.id.home);
        toolbar.findViewById(R.id.bang).setVisibility(View.GONE);
        if(visible) {
            homeButton.setVisibility(View.VISIBLE);
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //displayHomeScreen();

                    Log.e("aaa", "home button click!");
                    //BusProvider.getInstance().post(new DisplayHomeScreenEvent());
                }
            });
        } else {
            homeButton.setVisibility(View.GONE);
        }*/
    }

    private void setHomeButtonMarginTop(boolean visible) {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.findViewById(R.id.home).getLayoutParams();
        int padding = visible ? (int) context.getResources().getDimension(R.dimen.actionbar_margin) : 0;
        /*
        if(visible) {
            padding = (int) context.getResources().getDimension(R.dimen.actionbar_margin);
        }*/
        /*
        ValueAnimator topAnimator = ValueAnimator.ofInt(params.topMargin, padding);
        topAnimator.setDuration(250);
        topAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.topMargin = (Integer) animation.getAnimatedValue();
                toolbar.setLayoutParams(params);
            }
        });
        topAnimator.start();
*/
        params.topMargin = padding;
    }

    private void setBangButton() {

        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.actionbar_button_fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.actionbar_button_fade_out);

        if(homeButton.getVisibility()==View.VISIBLE) {
            Log.e("aaa", "set bang button: home -> gone");
            homeButton.setAnimation(fadeOut);
            homeButton.setVisibility(View.GONE);
        }

        if(bangButton.getVisibility()==View.GONE) {
            Log.e("aaa", "set bang button: bang -> visible");
            bangButton.setAnimation(fadeIn);
            bangButton.setVisibility(View.VISIBLE);
        }


        /*
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
        });*/
        //setActionBarMarginStart(true);//aaa
    }

    public void setOverflowButton(boolean visible) {
        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.actionbar_button_fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.actionbar_button_fade_out);
        if(visible) {
            if(overflowButton.getVisibility()==View.GONE) {
                overflowButton.setAnimation(fadeIn);
                overflowButton.setVisibility(View.VISIBLE);
            }
        } else {
            if(overflowButton.getVisibility()==View.VISIBLE) {
                overflowButton.setAnimation(fadeOut);
                overflowButton.setVisibility(View.GONE);
            }
        }
    }

    private void setOverflowButtonMarginTop(boolean visible) {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.findViewById(R.id.overflow).getLayoutParams();
        int padding = visible ? (int) context.getResources().getDimension(R.dimen.actionbar_margin) : 0;
        /*
        if(visible) {
            padding = (int) context.getResources().getDimension(R.dimen.actionbar_margin);
        }*/
        /*
        ValueAnimator topAnimator = ValueAnimator.ofInt(params.topMargin, padding);
        topAnimator.setDuration(250);
        topAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.topMargin = (Integer) animation.getAnimatedValue();
                toolbar.setLayoutParams(params);
            }
        });
        topAnimator.start();
*/
        params.topMargin = padding;
    }

    public void showTabLayout() {
        setTabLayout(true);
    }

    private void setTabLayout(boolean visible) {
        boolean gone = slidingTabLayout.getVisibility()==View.GONE;
        Log.e("aaa", "set tab layout, visible: "+visible+" - sliding tab visibility==gone: "+gone);
        if(visible) {
            if(slidingTabLayout.getVisibility()==View.GONE) {
                expandView(slidingTabLayout);
                Log.e("aaa", "must expand tab layout");
            } else {
                Log.e("aaa", "should expand tab layout but is visible");
            }
        } else {
            if(slidingTabLayout.getVisibility()==View.VISIBLE) {
                //collapseTabLayout();
                collapseView(slidingTabLayout);
                Log.e("aaa", "must collapse tab layout");
            } else {
                Log.e("aaa", "should collapse tab layout but is gone");
            }
        }
    }

    public boolean isTabAnimating() {
        return isTabAnimating;
    }

    public void tryToShowTab() {
        if(!isTabAnimating && slidingTabLayout.getVisibility()!=View.VISIBLE) {
            expandView(slidingTabLayout);
        }
    }

    public void tryToHideTab() {
        if(!isTabAnimating && slidingTabLayout.getVisibility()!=View.GONE) {
            collapseView(slidingTabLayout);
        }
    }

    public void expandView(final View view) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        final int standardMargin = (int) context.getResources().getDimension(R.dimen.actionbar_height);
        final int tabHeight = (int) context.getResources().getDimension(R.dimen.actionbar_tab_height2);
        final int actualMargin = (int) standardMargin - tabHeight;

        //view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                /*
                view.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);*/
                ((FrameLayout.LayoutParams)view.getLayoutParams()).topMargin = interpolatedTime == 1
                        ? standardMargin
                        : (int) (actualMargin + (tabHeight * interpolatedTime));
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(250);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isTabAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isTabAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(a);

        /*
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        int oldMargin = params.topMargin;
        int newMargin = (int) context.getResources().getDimension(R.dimen.actionbar_height);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(oldMargin, newMargin);
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.topMargin = (Integer) animation.getAnimatedValue();
            }
        });
        */
    }

    public void collapseView(final View view) {
        Log.e("aaa", "collapse view");
        final int initialHeight = view.getMeasuredHeight();
        Log.e("aaa", "initial height: "+initialHeight);

        /*
        TranslateAnimation tAnimation = new TranslateAnimation(0, 0, 0, (-1*initialHeight));
        tAnimation.setDuration(250);
        //tAnimation.willChangeBounds();
        tAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //view.startAnimation(tAnimation);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0, -initialHeight);
        animator.setDuration(2500);
        //animator.start();
        */


        final int standardMargin = (int) context.getResources().getDimension(R.dimen.actionbar_height);
        final int tabHeight = (int) context.getResources().getDimension(R.dimen.actionbar_tab_height2);
        final int actualMargin = (int) standardMargin - tabHeight;

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    view.setVisibility(View.GONE);
                }else{
                    Log.e("aaa", "interpolated time: "+interpolatedTime+" - new height: "+(initialHeight - (int)(initialHeight * interpolatedTime)));
                    //view.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    //view.setTranslationY(-1*((int)(initialHeight * interpolatedTime)));

                    ((FrameLayout.LayoutParams)view.getLayoutParams()).topMargin = standardMargin -(int)(tabHeight * interpolatedTime);

                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(250);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isTabAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isTabAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(a);
        //view.setVisibility(View.GONE);
        view.getParent().requestLayout();
    }

    public void setSearchBarText(String text) {
        if(text==null) {
            text = "";
        }
        Log.e("aaa text", "inside set search bar text: "+text);
        if(getSearchField().getText().toString().equals(text) || DDGControlVar.mDuckDuckGoContainer.currentUrl.equals(text)) {
            Log.e("aaa text", "text is already set");
            //return;
        }
        if(DDGControlVar.homeScreenShowing) {
            DDGControlVar.mDuckDuckGoContainer.currentUrl = "";
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

    public class ProgressBarAnimation extends Animation {
        private ProgressBar progressBar;
        private float from;
        private float  to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
            progressBar.setProgress(0);
            //progressBar.setVisibility(View.VISIBLE);
        }
/*
        public float getLastValue() {
            return progressBar.getProgress();
        }
*/
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
            if(value>=100) {
                //Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.actionbar_button_fade_out);
                //progressBar.startAnimation(fadeOut);
                //progressBar.setVisibility(View.GONE);
                //progressBar.setProgress(0);
            }
        }

    }

}
