package com.duckduckgo.mobile.android.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.OnboardingHelper;

/**
 * Created by fgei on 4/7/17.
 */

public class InstructionDialogFragment extends AppCompatDialogFragment {

    public static final String TAG = "instruction_dialog_fragment";

    public static InstructionDialogFragment newInstance(int instructionType) {
        return newInstance(instructionType, false);
    }

    public static InstructionDialogFragment newInstance(int instructionType, boolean disableDismissButton) {
        InstructionDialogFragment fragment = new InstructionDialogFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_INSTRUCTION_TYPE, instructionType);
        args.putBoolean(EXTRA_DISABLE_DISMISS_BUTTON, disableDismissButton);
        fragment.setArguments(args);
        return fragment;
    }

    public static final int EXTRA_INSTRUCTION_FIREFOX = 0;
    public static final int EXTRA_INSTRUCTION_CHROME = 1;

    private static final String EXTRA_INSTRUCTION_TYPE = "instruction_type";
    private static final String EXTRA_DISABLE_DISMISS_BUTTON = "disable_dismiss_button";

    private static final int INITIAL_DISABLE_TIME = 5000;

    private View firefoxInstructionContainer, chromeInstructionContainer;
    private View toggleInstructionContainer;
    private TextView toggleInstructionTextView;
    private ImageView toggleInstructionImageView;
    private TextView titleTextView;
    private TextView doneButton;
    private ViewGroup transitionRoot;

    private OnboardingHelper onboardingHelper;

    private boolean isInstructionChromeType = true;
    private boolean disableDismissButton = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        Bundle args = getArguments();
        if(args.containsKey(EXTRA_INSTRUCTION_TYPE)) {
            isInstructionChromeType = args.getInt(EXTRA_INSTRUCTION_TYPE) == EXTRA_INSTRUCTION_CHROME;
        }
        if(args.containsKey(EXTRA_DISABLE_DISMISS_BUTTON)) {
            disableDismissButton = args.getBoolean(EXTRA_DISABLE_DISMISS_BUTTON);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_onboarding_instruction, container, false);
        init(getContext(), rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(disableDismissButton && savedInstanceState == null) {
            disableViewForTime(doneButton, INITIAL_DISABLE_TIME );
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void init(Context context, final View rootView) {
        onboardingHelper = new OnboardingHelper(context);
        transitionRoot = (ViewGroup) rootView;
        TextView instruction1TextView = (TextView) rootView.findViewById(R.id.instruction_1_text_view);
        instruction1TextView.setText(getStyledString(getContext(), R.string.instruction_firefox_1_a, R.string.instruction_firefox_1_b));

        TextView instruction2TextView = (TextView) rootView.findViewById(R.id.instruction_2_text_view);
        instruction2TextView.setText(getStyledString(getContext(), R.string.instruction_firefox_2_a, R.string.instruction_firefox_2_b));

        TextView instruction3TextView = (TextView) rootView.findViewById(R.id.instruction_3_text_view);
        instruction3TextView.setText(getStyledString(getContext(), R.string.instruction_firefox_3_a, R.string.instruction_firefox_3_b));

        TextView instruction4TextView = (TextView) rootView.findViewById(R.id.instruction_4_text_view);
        instruction4TextView.setText(getStyledString(getContext(), R.string.instruction_firefox_4_a, R.string.instruction_firefox_4_b));

        doneButton = (TextView) rootView.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        firefoxInstructionContainer = rootView.findViewById(R.id.instruction_container);
        chromeInstructionContainer = rootView.findViewById(R.id.add_to_home_screen_button);
        chromeInstructionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onboardingHelper.addToHomeScreen();
                dismiss();
            }
        });
        toggleInstructionImageView = (ImageView) rootView.findViewById(R.id.add_to_image_view);
        toggleInstructionTextView = (TextView) rootView.findViewById(R.id.add_to_text_view);
        toggleInstructionContainer = rootView.findViewById(R.id.add_to_container);
        toggleInstructionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInstructionChromeType = !isInstructionChromeType;
                initInstructionType(isInstructionChromeType, true);
            }
        });
        titleTextView = (TextView) rootView.findViewById(R.id.instruction_title_text_view);
        initInstructionType(isInstructionChromeType, false);
    }

    private void initInstructionType(boolean isChromeType, boolean withAnimation) {
        if(withAnimation) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(transitionRoot);
            }
        }
        chromeInstructionContainer.setVisibility(isChromeType ? View.VISIBLE : View.GONE);
        firefoxInstructionContainer.setVisibility(isChromeType ? View.GONE : View.VISIBLE);
        toggleInstructionImageView.setImageResource(isChromeType ? R.drawable.firefox: R.drawable.chrome);
        toggleInstructionTextView.setText(
                String.format(
                        getString(R.string.add_to),
                        getString(isChromeType ? R.string.browser_firefox : R.string.browser_chrome)));
        titleTextView.setText(
                String.format(getString(R.string.add_to),
                        getString(isChromeType ? R.string.browser_chrome : R.string.browser_firefox)));
    }

    private void disableViewForTime(final TextView textView, int millis) {
        final int textColor = textView.getCurrentTextColor();
        textView.setEnabled(false);
        textView.setTextColor(Color.GRAY);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setEnabled(true);
                textView.setTextColor(textColor);
            }
        }, millis);
    }

    private static SpannableStringBuilder getStyledString(Context context, int textResId, int textBoldResId) {
        String text = context.getString(textResId);
        String textPrimary = context.getString(textBoldResId);
        SpannableStringBuilder builder = new SpannableStringBuilder(text + " " + textPrimary);
        int startPrimaryIndex = text.length() + 1;
        int endPrimaryIndex = startPrimaryIndex + textPrimary.length();
        builder.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.onboarding_primary_text)),
                startPrimaryIndex,
                endPrimaryIndex,
                0);
        builder.setSpan(new StyleSpan(Typeface.BOLD), startPrimaryIndex, endPrimaryIndex, 0);
        return builder;
    }
}
