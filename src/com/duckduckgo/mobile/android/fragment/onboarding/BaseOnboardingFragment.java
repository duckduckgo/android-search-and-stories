package com.duckduckgo.mobile.android.fragment.onboarding;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.CompatUtils;
import com.duckduckgo.mobile.android.util.DimenUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fgei on 4/4/17.
 */

public abstract class BaseOnboardingFragment extends Fragment {

    private static final String EXTRA_INDEX_POSITION = "index_position";
    private static final String EXTRA_MINI_LAYOUT = "mini_layout";

    private static final float BOTTOM_MARGIN_CONSTRAINT_PORTRAIT = 3.819f;
    private static final float BOTTOM_MARGIN_CONSTRAINT_LANDSCAPE = 4.228f;

    protected abstract int getBackgroundColor();
    protected abstract int getIcon();
    protected abstract String getTitle();
    protected abstract String getSubtitle();

    private ViewGroup backgroundFrameLayout;
    private TextView titleTextView;
    private TextView subtitleTextView;
    private ImageView iconImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                useMiniLayout() ? R.layout.viewholder_onboarding : R.layout.fragment_onboarding,
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
        if(!useMiniLayout()) {
            backgroundFrameLayout.setBackgroundColor(getBackgroundColor());
        }
        String title = getTitle();
        if(useMiniLayout()) title = title.replaceAll("\\n", " ");
        titleTextView.setText(title);
        String subtitle = getSubtitle();
        if(useMiniLayout()) subtitle = subtitle.replaceAll("\\n", " ");
        subtitleTextView.setText(subtitle);
        iconImageView.setImageResource(getIcon());

        initLayout();
    }

    protected boolean useMiniLayout() {
        return getArguments().getBoolean(EXTRA_MINI_LAYOUT);
    }

    private int getIndexPosition() {
        return getArguments().getInt(EXTRA_INDEX_POSITION);
    }

    private void initLayout() {
        if(!useMiniLayout()) {
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
    }

    protected static Bundle createArgs(int indexPosition) {
        return createArgs(indexPosition, false);/*
        Bundle args = new Bundle();
        args.putInt(EXTRA_INDEX_POSITION, indexPosition);
        return args;*/
    }

    protected static Bundle createArgs(int indexPosition, boolean miniLayout) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_INDEX_POSITION, indexPosition);
        args.putBoolean(EXTRA_MINI_LAYOUT, miniLayout);
        return args;
    }
}
