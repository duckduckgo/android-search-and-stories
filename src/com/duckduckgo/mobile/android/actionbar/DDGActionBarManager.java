package com.duckduckgo.mobile.android.actionbar;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class DDGActionBarManager implements View.OnClickListener, View.OnLongClickListener {

    private Activity activity;
    private Context context;
    private KeyboardService keyboardService;

    private DDGAutoCompleteTextView searchField = null;
    private RelativeLayout searchFieldContainer = null;
    private TextView actionBarTitle;

    private ImageButton homeButton;
    private ImageButton bangbutton;

    private Toolbar toolbar;
    private ActionBar actionBar;

    private View searchBar;
    private View dropShadowDivider;

    private SCREEN screen;

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

        homeButton = (ImageButton) toolbar.findViewById(R.id.home);
        homeButton.setOnClickListener(this);
        homeButton.setOnLongClickListener(this);
        bangbutton = (ImageButton) toolbar.findViewById(R.id.bang);
        bangbutton.setOnClickListener(this);
        bangbutton.setOnLongClickListener(this);

        keyboardService = new KeyboardService(activity);
    }

    public DDGAutoCompleteTextView getSearchField() {
        return this.searchField;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.home:
                Log.e("aaa", "home button clicked");
                BusProvider.getInstance().post(new DisplayHomeScreenEvent());
                break;
            case R.id.bang:
                Log.e("aaa", "bang button clicked");
                keyboardService.showKeyboard(getSearchField());
                getSearchField().addBang();
                break;
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

        int standardMargin = (int) context.getResources().getDimension(R.dimen.actionbar_margin);
        int overflowVisibleRightMargin = 0;
        int actionButtonVisibleLeftMargin = (int) context.getResources().getDimension(R.dimen.actionbar_searchbar_left_margin_with_button);
        Log.e("aaa^^^^^^^", "standard margin: "+standardMargin+" - overflow visible right: "+overflowVisibleRightMargin+ " - action button visible left: "+actionButtonVisibleLeftMargin);

        int leftMargin , rightMargin;


        switch(screen) {
            case SCR_STORIES:
                clearSearchBar();

                showSearchField();//


                setShadow(true);
                //int standardMargin = (int) context.getResources().getDimension(R.dimen.actionbar_margin);
                //int overflowRightMargin = 0;

                leftMargin = isStartingScreen ? standardMargin : actionButtonVisibleLeftMargin;
                rightMargin = isStartingScreen ? overflowVisibleRightMargin : standardMargin;

                setActionBarMargins(leftMargin, standardMargin, rightMargin, standardMargin);

                //hasOverflowButtonVisible(isStartingScreen);

                //setActionBarMarginBottom(true);//aaa

                setHomeButton(!isStartingScreen);

                setHomeButtonMarginTop(false);

                setStandardActionBarHeight(true);
                break;
            case SCR_RECENTS:

                showSearchField();//

                setShadow(false);
                //setActionBarMarginBottom(false);//aaa

                leftMargin = isStartingScreen ? standardMargin : actionButtonVisibleLeftMargin;
                rightMargin = isStartingScreen ? overflowVisibleRightMargin : standardMargin;

                setActionBarMargins(leftMargin, standardMargin, rightMargin, 0);

                //hasOverflowButtonVisible(isStartingScreen);
                setHomeButton(!isStartingScreen);
                setHomeButtonMarginTop(true);
                setStandardActionBarHeight(false);
                break;
            case SCR_FAVORITE:

                showSearchField();

                setShadow(false);


                leftMargin = isStartingScreen ? standardMargin : actionButtonVisibleLeftMargin;
                rightMargin = isStartingScreen ? overflowVisibleRightMargin : standardMargin;

                setActionBarMargins(leftMargin, standardMargin, rightMargin, 0);

                //setActionBarMarginBottom(false);
                //hasOverflowButtonVisible(isStartingScreen);
                setHomeButton(!isStartingScreen);
                setHomeButtonMarginTop(true);
                setStandardActionBarHeight(false);
                break;
            case SCR_WEBVIEW:
                showSearchField();


                setShadow(true);

                setActionBarMargins(actionButtonVisibleLeftMargin, standardMargin, overflowVisibleRightMargin, standardMargin);

                hasOverflowButtonVisible(true);

                //setActionBarMarginBottom(true);

                //setSearchBarText(DDGControlVar.mDuckDuckGoContainer.currentUrl);
                setHomeButton(true);
                setHomeButtonMarginTop(false);
                setStandardActionBarHeight(true);
                break;
            case SCR_SEARCH:
            case SCR_SEARCH_HOME_PAGE:
                showSearchField();
                setShadow(true);

                rightMargin = screen==SCREEN.SCR_SEARCH ? standardMargin : overflowVisibleRightMargin;
                setActionBarMargins(actionButtonVisibleLeftMargin, standardMargin, rightMargin, standardMargin);

                hasOverflowButtonVisible(screen==SCREEN.SCR_SEARCH_HOME_PAGE);

                //setActionBarMarginBottom(true);
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
        toggleActionBarView(true);
    }
/*
    private void showTitle(String tag, String newTitle) {
        toggleActionBarView(true, tag, newTitle);
    }
*/
    private void showTitle() {
        toggleActionBarView(false);
    }

    private void showTitle(String tag, String title) {
        toggleActionBarView(false);
        setTitle(tag, title);
    }

    private void setTitle(String tag, String title) {
        actionBarTitle.setText(title);
        setTitleLeftMargin(tag);
    }

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
        }
    }

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
            }
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
        params.rightMargin = newRight;
        ValueAnimator leftValueAnimator = ValueAnimator.ofInt(params.leftMargin, newLeft);
        leftValueAnimator.setDuration(250);
        leftValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.leftMargin = (Integer) animation.getAnimatedValue();
                searchFieldContainer.setLayoutParams(params);
            }
        });
        ValueAnimator rightValueAnimator = ValueAnimator.ofInt(params.rightMargin, newRight);
        rightValueAnimator.setDuration(250);
        rightValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.rightMargin= (Integer) animation.getAnimatedValue();
                searchFieldContainer.setLayoutParams(params);
            }
        });
        ValueAnimator topValueAnimator = ValueAnimator.ofInt(params.topMargin, newTop);
        topValueAnimator.setDuration(250);
        topValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.topMargin= (Integer) animation.getAnimatedValue();
                searchFieldContainer.setLayoutParams(params);
            }
        });
        ValueAnimator bottomValueAnimator = ValueAnimator.ofInt(params.bottomMargin, newBottom);
        bottomValueAnimator.setDuration(250);
        bottomValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.bottomMargin= (Integer) animation.getAnimatedValue();
                searchFieldContainer.setLayoutParams(params);
            }
        });

        leftValueAnimator.start();
        rightValueAnimator.start();
        topValueAnimator.start();
        bottomValueAnimator.start();
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
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();


        //toolbar.setMinimumHeight(height);
        //toolbar.getLayoutParams().height = (int) context.getResources().getDimension(resId);

        //toolbar.getLayoutParams().height = newHeight;

        ValueAnimator heightValueAnimator = ValueAnimator.ofInt(params.height, newHeight);
        heightValueAnimator.setDuration(250);
        heightValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.height = (Integer) animation.getAnimatedValue();
                toolbar.setLayoutParams(params);
            }
        });
        heightValueAnimator.start();

    }

    private void setHomeButton(boolean visible) {

        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.actionbar_button_fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.actionbar_button_fade_out);

        if (bangbutton.getVisibility() == View.VISIBLE) {
            Log.e("aaa", "set home button: bang -> gone");
            bangbutton.setAnimation(fadeOut);
            bangbutton.setVisibility(View.GONE);
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

        if(bangbutton.getVisibility()==View.GONE) {
            Log.e("aaa", "set bang button: bang -> visible");
            bangbutton.setAnimation(fadeIn);
            bangbutton.setVisibility(View.VISIBLE);
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
