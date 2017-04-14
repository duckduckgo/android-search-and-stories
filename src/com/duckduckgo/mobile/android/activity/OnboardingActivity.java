package com.duckduckgo.mobile.android.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.OnboardingAdapter;
import com.duckduckgo.mobile.android.dialogs.InstructionDialogFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.EndOnboardingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoAdsFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoTrackingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.PrivacyFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.RightFragment;
import com.duckduckgo.mobile.android.util.OnboardingTransformer;
import com.duckduckgo.mobile.android.util.OnboardingUtils;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.views.pageindicator.OnboardingPageIndicator;

import java.util.Arrays;

public class OnboardingActivity extends AppCompatActivity {

    public static Intent getStartIntent(Context context) {
        return new Intent(context, OnboardingActivity.class);
    }

    private ViewGroup container;
    private ViewPager viewPager;
    private FragmentPagerAdapter adapter;
    private OnboardingPageIndicator pageIndicator;
    private Button addToHomeScreenButton;
    private Button nextButton;

    private boolean actionAddToHomeScreenPerformed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(5000);
            getWindow().setReturnTransition(fade);
        }
        setContentView(R.layout.activity_onboarding);
        initUI();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("shortcuts", "onboarding on new intent");
    }/*

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("shortcuts", "onboarding on receive create shortcut ACTIVITY");
                Toast.makeText(OnboardingActivity.this, "DuckDuckGo was added to your Homescreen", Toast.LENGTH_LONG).show();
            }
        }, new IntentFilter("com.android.launcher.permission.INSTALL_SHORTCUT"));
    }*/

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        Log.e("onboarding_lifecycle", "finish now");
    }

    private void initUI() {
        container = (ViewGroup) findViewById(R.id.activity_onboarding);
        addToHomeScreenButton = (Button) findViewById(R.id.add_to_home_screen_button);
        addToHomeScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //InstructionDialogFragment.newInstance().show(getSupportFragmentManager(), InstructionDialogFragment.TAG);
                //PreferencesManager.setHasShownOnboarding();
                addToHomepage();
            }
        });
        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextOnboardingScreen();
            }
        });

        adapter = new OnboardingAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*
                if(position == 4) {
                    pageIndicator.setVisibility(View.GONE);
                    addToHomeScreenButton.setVisibility(View.GONE);
                } else {
                    pageIndicator.setVisibility(View.VISIBLE);
                    addToHomeScreenButton.setVisibility(View.VISIBLE);
                }*/
                if(position == 4) {
                    //finish();
                    ActivityCompat.finishAfterTransition(OnboardingActivity.this);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setAdapter(adapter);

        int[] backgroundColors = new int[] {
                ContextCompat.getColor(this, PrivacyFragment.BACKGROUND_COLOR),
                ContextCompat.getColor(this, NoAdsFragment.BACKGROUND_COLOR),
                ContextCompat.getColor(this, NoTrackingFragment.BACKGROUND_COLOR),
                ContextCompat.getColor(this, RightFragment.BACKGROUND_COLOR),
                ContextCompat.getColor(this, EndOnboardingFragment.BACKGROUND_COLOR)};
        pageIndicator = (OnboardingPageIndicator) findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(viewPager, adapter.getCount() - 1);

        viewPager.setPageTransformer(false, new OnboardingTransformer(backgroundColors, pageIndicator, Arrays.asList(pageIndicator, addToHomeScreenButton, nextButton)));
    }

    private void addToHomepage() {
        actionAddToHomeScreenPerformed = true;
        //OnboardingUtils.addDDGToHomescreen(OnboardingActivity.this);
        //Toast.makeText(this, "DuckDuckGo was added to your Homescreen", Toast.LENGTH_LONG).show();
        //toggleButtons(actionAddToHomeScreenPerformed, true);
    }

    private void showNextOnboardingScreen() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        //toggleButtons();
    }

    private void toggleButtons(boolean showNextButton, boolean withAnimation) {
        if(withAnimation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(container);
        }
        addToHomeScreenButton.setVisibility(showNextButton ? View.GONE : View.VISIBLE);
        nextButton.setVisibility(showNextButton ? View.VISIBLE : View.GONE);
    }
}
