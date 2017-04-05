package com.duckduckgo.mobile.android.fragment.onboarding;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;

/**
 * Created by fgei on 4/4/17.
 */

public abstract class BaseOnboardingFragment extends Fragment {

    private static final String EXTRA_INDEX_POSITION = "index_position";
    private static final String EXTRA_MINI_LAYOUT = "mini_layout";

    private FrameLayout backgroundFrameLayout;
    private TextView titleTextView;
    private TextView subtitleTextView;
    private ImageView iconImageView;

    protected abstract int getBackgroundColor();
    protected abstract int getIcon();
    protected abstract String getTitle();
    protected abstract String getSubtitle();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                useMiniLayout() ? R.layout.viewholder_onboarding : R.layout.fragment_onboarding,
                container,
                false);
        backgroundFrameLayout = (FrameLayout) rootView;
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
        titleTextView.setText(getTitle());
        subtitleTextView.setText(getSubtitle());
        iconImageView.setImageResource(getIcon());
    }

    private boolean useMiniLayout() {
        return getArguments().getBoolean(EXTRA_MINI_LAYOUT);
    }

    private int getIndexPosition() {
        return getArguments().getInt(EXTRA_INDEX_POSITION);
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
