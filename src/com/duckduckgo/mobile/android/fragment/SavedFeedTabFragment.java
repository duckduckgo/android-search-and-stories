package com.duckduckgo.mobile.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.views.MainFeedListView;


public class SavedFeedTabFragment extends ListFragment {
	MainFeedListView savedFeedView = null;
	
	/** (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout fragmentLayout = (LinearLayout)inflater.inflate(R.layout.fragment_tab_savedfeed, container, false);
		return fragmentLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// setup for real work
		final Activity activity = getActivity();
				
		if(activity instanceof DuckDuckGo) {
    		savedFeedView = (MainFeedListView) getListView();
    		savedFeedView.setAdapter(((DuckDuckGo) activity).mDuckDuckGoContainer.savedFeedAdapter);

    		savedFeedView.setOnMainFeedItemSelectedListener(((DuckDuckGo) activity).mFeedItemSelectedListener);
    		savedFeedView.setOnMainFeedItemLongClickListener(((DuckDuckGo) activity).mSavedFeedItemLongClickListener);
		}
	}
}