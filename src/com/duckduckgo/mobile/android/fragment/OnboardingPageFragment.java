package com.duckduckgo.mobile.android.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.CompatUtils;
import com.duckduckgo.mobile.android.util.OnboardingPageConfiguration;

/**
 * Created by fgei on 4/4/17.
 */

public class OnboardingPageFragment extends Fragment {

    private static final String EXTRA_INDEX_POSITION = "index_position";
    private static final String EXTRA_PAGE_CONFIGURATION = "page_configuration";

    private static final float BOTTOM_MARGIN_CONSTRAINT_PORTRAIT = 3.819f;
    private static final float BOTTOM_MARGIN_CONSTRAINT_LANDSCAPE = 4.228f;

    public static OnboardingPageFragment newInstance(OnboardingPageConfiguration onboardingPageConfiguration, int indexPosition) {
        OnboardingPageFragment f = new OnboardingPageFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_PAGE_CONFIGURATION, onboardingPageConfiguration);
        args.putInt(EXTRA_INDEX_POSITION, indexPosition);
        f.setArguments(args);
        return f;
    }

    private OnboardingPageConfiguration onboardingPageConfiguration;

    private ViewGroup backgroundFrameLayout;
    private TextView titleTextView;
    private TextView subtitleTextView;
    private ImageView iconImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardingPageConfiguration = getOnboardingPageConfiguration();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_onboarding,
                container,
                false);
        backgroundFrameLayout = (ViewGroup) rootView;
        titleTextView = (TextView) rootView.findViewById(R.id.title_text_view);
        subtitleTextView = (TextView) rootView.findViewById(R.id.subtitle_text_view);
        iconImageView = (ImageView) rootView.findViewById(R.id.icon_image_view);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backgroundFrameLayout.setTag(getIndexPosition());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backgroundFrameLayout.setBackgroundColor(getBackgroundColor());
        if(!onboardingPageConfiguration.hasContent) return;
        String title = getTitle();
        titleTextView.setText(title);
        String subtitle = getSubtitle();
        subtitleTextView.setText(subtitle);
        iconImageView.setImageResource(getIcon());
        initLayout();
    }

    private int getIndexPosition() {
        return getArguments().getInt(EXTRA_INDEX_POSITION);
    }

    private OnboardingPageConfiguration getOnboardingPageConfiguration() {
        return getArguments().getParcelable(EXTRA_PAGE_CONFIGURATION);

    }

    private void initLayout() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        CompatUtils.getDisplaySize(display, size);
        int width = size.x;
        int height = size.y;
        final FrameLayout containerLayout = (FrameLayout) backgroundFrameLayout.findViewById(R.id.container_layout);
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) containerLayout.getLayoutParams();

        boolean isPortrait = width < height;
        //float portraitBottomRatio = 3.819f;
        //float landscapeBottomRatio = 4.228f;
        float bottomMarginConstraint = isPortrait ? BOTTOM_MARGIN_CONSTRAINT_PORTRAIT : BOTTOM_MARGIN_CONSTRAINT_LANDSCAPE;
        final int bottomMargin = (int) (height / bottomMarginConstraint);
        final int topMargin = height - (int) (height / 1.278);
        containerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CompatUtils.removeOnGlobalLayoutListener(containerLayout.getViewTreeObserver(), this);
                params.bottomMargin = bottomMargin;
                containerLayout.setLayoutParams(params);
            }
        });
    }

    public String getTitle() {
        if(!onboardingPageConfiguration.hasContent) return "";
        return getString(onboardingPageConfiguration.title);
    }

    public String getSubtitle() {
        if(!onboardingPageConfiguration.hasContent) return "";
        return getString(onboardingPageConfiguration.subtitle);
    }

    @DrawableRes
    public int getIcon() {
        if(!onboardingPageConfiguration.hasContent) return 0;
        return onboardingPageConfiguration.icon;
    }

    @ColorInt
    public int getBackgroundColor() {
        return ContextCompat.getColor(getContext(), onboardingPageConfiguration.backgroundColor);
    }
}
