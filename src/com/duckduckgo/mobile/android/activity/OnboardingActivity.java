package com.duckduckgo.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.OnboardingAdapter;
import com.duckduckgo.mobile.android.dialogs.InstructionDialogFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.EndOnboardingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoAdsFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoTrackingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.PrivacyFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.RightFragment;
import com.duckduckgo.mobile.android.util.CompatUtils;
import com.duckduckgo.mobile.android.util.OnboardingHelper;
import com.duckduckgo.mobile.android.util.OnboardingTransformer;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.views.pageindicator.OnboardingPageIndicator;

import java.util.Arrays;

public class OnboardingActivity extends AppCompatActivity {

    public static Intent getStartIntent(Context context) {
        return new Intent(context, OnboardingActivity.class);
    }

    private FrameLayout activityContainer;
    private LinearLayout containerLayout;
    private ViewPager viewPager;
    private FragmentPagerAdapter adapter;
    private OnboardingPageIndicator pageIndicator;
    private Button addToHomeScreenButton;

    private OnboardingHelper onboardingHelper;
    private InstructionDialogFragment instructionDialogFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(400);
            getWindow().setReturnTransition(fade);
        }
        setContentView(R.layout.activity_onboarding);
        initUI();
    }

    @Override
    public void finish() {
        if(viewPager.getCurrentItem() >= adapter.getCount() -1)
        PreferencesManager.setHasShownOnboarding();
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private void initUI() {
        onboardingHelper = new OnboardingHelper(this);
        activityContainer = (FrameLayout) findViewById(R.id.activity_onboarding);
        containerLayout = (LinearLayout) findViewById(R.id.container_layout);
        boolean isFirefoxDefault = onboardingHelper.isDefaultBrowserFirefox();
        addToHomeScreenButton = (Button) findViewById(R.id.add_to_home_screen_button);
        addToHomeScreenButton.setText(
                String.format(getString(R.string.add_to),
                        getString(isFirefoxDefault ? R.string.browser_firefox : R.string.browser_chrome))
        );
        addToHomeScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTo();
            }
        });

        adapter = new OnboardingAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 4) {
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

        viewPager.setPageTransformer(false, new OnboardingTransformer(backgroundColors, pageIndicator, Arrays.asList(pageIndicator, addToHomeScreenButton)));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        CompatUtils.getDisplaySize(display, size);
        int width = size.x;
        int height = size.y;
        if(width > height) return;
        final float bottomRatio = 13.6f;
        final int bottomMargin = (int) (height / bottomRatio);
        containerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) containerLayout.getLayoutParams();
                params.bottomMargin = bottomMargin;
                containerLayout.setLayoutParams(params);
                CompatUtils.removeOnGlobalLayoutListener(containerLayout.getViewTreeObserver(), this);
            }
        });
        if(instructionDialogFragment == null) {
            instructionDialogFragment = InstructionDialogFragment.newInstance(
                    onboardingHelper.isDefaultBrowserFirefox()
                            ? InstructionDialogFragment.EXTRA_INSTRUCTION_FIREFOX
                            : InstructionDialogFragment.EXTRA_INSTRUCTION_CHROME);
            instructionDialogFragment.show(getSupportFragmentManager(), InstructionDialogFragment.TAG);
        }
    }

    private void addTo() {
        InstructionDialogFragment.newInstance(
                onboardingHelper.isDefaultBrowserFirefox() ? InstructionDialogFragment.EXTRA_INSTRUCTION_FIREFOX : InstructionDialogFragment.EXTRA_INSTRUCTION_CHROME)
                .show(getSupportFragmentManager(), InstructionDialogFragment.TAG);
    }
}
