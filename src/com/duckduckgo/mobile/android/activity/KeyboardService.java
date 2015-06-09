package com.duckduckgo.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class KeyboardService {
    private final Activity activity;

    public KeyboardService(Activity activity) {
        this.activity = activity;
    }

    public void hideKeyboardDelayed(final View view){
        Log.e("aaa", "hide keyboard delayed");
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideKeyboard(view);
            }
        }, 100);
    }

    public void hideKeyboardB(final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                hideKeyboard(view);
            }
        });
    }

    public void hideKeyboard(final View view) {
        Log.e("aaa", "hide keyboard");
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(final View view) {
        Log.e("aaa", "show keyboard");
        view.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }
}