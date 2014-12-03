package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.tabhost.TabHostExt;

public class SavedFragment extends Fragment {

	public static final String TAG = "saved_fragment";

	private TabHostExt savedTabHost = null;

	private View fragmentView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BusProvider.getInstance().register(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		BusProvider.getInstance().unregister(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setRetainInstance(true);
		fragmentView = inflater.inflate(R.layout.fragment_saved, container, false);
		init();
		return fragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState!=null) {
			savedTabHost.setCurrentTabByTag(savedInstanceState.getString("tag"));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tag", savedTabHost.getCurrentTabTag());
	}

	public void init() {
		savedTabHost= (TabHostExt) fragmentView.findViewById(R.id.savedTabHost);
		savedTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
		savedTabHost.addDefaultTabs();
	}
}