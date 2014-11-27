package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.R;

public class DuckModeFragment extends Fragment {

	public static final String TAG = "duckmode_fragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_duckmode, container, false);
	}
}
