package com.duckduckgo.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.OnboardingAdapter;
import com.duckduckgo.mobile.android.dialogs.InstructionDialogFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.EndOnboardingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoAdsFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoTrackingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.PrivacyFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.RightFragment;
import com.duckduckgo.mobile.android.util.OnboardingTransformer;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.views.pageindicator.OnboardingPageIndicator;

import java.util.Arrays;

public class OnboardingActivity extends AppCompatActivity {

    public static Intent getStartIntent(Context context) {
        return new Intent(context, OnboardingActivity.class);
    }

    private ViewPager viewPager;
    private FragmentPagerAdapter adapter;
    private OnboardingPageIndicator pageIndicator;
    private Button instructionButton;

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
    public void onBackPressed() {
        if(viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            return;
        }
        super.onBackPressed();
    }

    private void initUI() {
        instructionButton = (Button) findViewById(R.id.instruction_button);
        instructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstructionDialogFragment.newInstance().show(getSupportFragmentManager(), InstructionDialogFragment.TAG);
                PreferencesManager.setHasShownOnboarding();
            }
        });

        adapter = new OnboardingAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 4) {
                    pageIndicator.setVisibility(View.GONE);
                    instructionButton.setVisibility(View.GONE);
                } else {
                    pageIndicator.setVisibility(View.VISIBLE);
                    instructionButton.setVisibility(View.VISIBLE);
                }
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

        viewPager.setPageTransformer(false, new OnboardingTransformer(backgroundColors, pageIndicator, Arrays.asList(pageIndicator, instructionButton)));
    }
}
