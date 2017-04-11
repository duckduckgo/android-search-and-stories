package com.duckduckgo.mobile.android.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.util.OnboardingUtils;

/**
 * Created by fgei on 4/7/17.
 */

public class InstructionDialogFragment extends DialogFragment {

    public static final String TAG = "instruction_dialog_fragment";


    public static InstructionDialogFragment newInstance() {
        return new InstructionDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_instruction_2, container, false);
        init(getContext(), rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void init(Context context, View rootView) {
        TextView instruction1TextView = (TextView) rootView.findViewById(R.id.instruction_1_text_view);
        instruction1TextView.setText(getStyledString(context, R.string.instruction_1_a, R.string.instruction_1_b));

        TextView instruction2TextView = (TextView) rootView.findViewById(R.id.instruction_2_text_view);
        instruction2TextView.setText(getStyledString(context, R.string.instruction_2_a, R.string.instruction_2_b));

        TextView instruction3TextView = (TextView) rootView.findViewById(R.id.instruction_3_text_view);
        instruction3TextView.setText(getStyledString(context, R.string.instruction_3_a, R.string.instruction_3_b));

        Button doneButton = (Button) rootView.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnboardingUtils.launchDDG(getContext());
                dismiss();
            }
        });
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
