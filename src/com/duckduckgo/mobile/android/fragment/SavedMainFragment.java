package com.duckduckgo.mobile.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.tabhost.TabHostExt;

public class SavedMainFragment extends Fragment {
	private TabHostExt savedTabHost;
	private View contentView;
	
	private void initialiseTabHost() {
		savedTabHost = (TabHostExt) contentView.findViewById(R.id.saved_tab_host_ext);
		savedTabHost.setup(getActivity(), getFragmentManager(), R.id.realtabcontent);
		savedTabHost.addDefaultTabs();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        contentView = inflater.inflate(R.layout.tab_saved_view, container, false);
        initialiseTabHost();
		if(savedInstanceState != null) {
			savedTabHost.setCurrentTabByTag(savedInstanceState.getString("simple")); //set the tab as per the saved state
		}
        return contentView;
    }
	
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
}
