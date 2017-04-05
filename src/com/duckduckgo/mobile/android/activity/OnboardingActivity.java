package com.duckduckgo.mobile.android.activity;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.OnboardingAdapter;
import com.duckduckgo.mobile.android.fragment.onboarding.NoAdsFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.NoTrackingFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.PrivacyFragment;
import com.duckduckgo.mobile.android.fragment.onboarding.RightFragment;
import com.duckduckgo.mobile.android.util.OnboardingTransformer;
import com.duckduckgo.mobile.android.views.PageIndicator;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FragmentPagerAdapter adapter;
    private PageIndicator pageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new OnboardingAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        int[] backgroundColors = new int[] {
                ContextCompat.getColor(this, PrivacyFragment.BACKGROUND_COLOR),
                ContextCompat.getColor(this, NoAdsFragment.BACKGROUND_COLOR),
                ContextCompat.getColor(this, NoTrackingFragment.BACKGROUND_COLOR),
                ContextCompat.getColor(this, RightFragment.BACKGROUND_COLOR)};
        pageIndicator = (PageIndicator) findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(viewPager);
        viewPager.setPageTransformer(false, new OnboardingTransformer(backgroundColors, pageIndicator));
        Button instructionButton = (Button) findViewById(R.id.instruction_button);
        instructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OnboardingActivity.this)
                        .setTitle("Instruction")
                        .setMessage("Placeholder text")
                        .setPositiveButton("Done", null);
                builder.create().show();
            }
        });
    }
}
