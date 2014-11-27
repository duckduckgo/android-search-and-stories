package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.tabhost.TabHostExt;

public class SavedFragment extends Fragment {

	public static final String TAG = "saved_fragment";

	private TabHostExt savedTabHost = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BusProvider.getInstance().register(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_saved, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	public void init() {
		savedTabHost = (TabHostExt) getView().findViewById(android.R.id.tabhost);
		savedTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
		savedTabHost.addDefaultTabs();
	}
}
