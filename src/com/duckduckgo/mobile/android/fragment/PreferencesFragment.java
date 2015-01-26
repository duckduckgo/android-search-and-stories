package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;

public class PreferencesFragment extends android.support.v4.preference.PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.temp_preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //ActionBar actionBar = ((DuckDuckGo) getActivity()).getSupportActionBar();
        //actionBar.setTitle("Settings");


    }
}
