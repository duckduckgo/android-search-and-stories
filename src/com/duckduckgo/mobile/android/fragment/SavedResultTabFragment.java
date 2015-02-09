package com.duckduckgo.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.views.SavedSearchListView;
import com.squareup.otto.Subscribe;

public class SavedResultTabFragment extends ListFragment {

	public static final String TAG = "saved_result_tab_fragment";
	private SavedSearchListView savedSearchView;
	private SavedResultCursorAdapter savedSearchAdapter;

    private View fragmentView = null;
	
	/** (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_tab_favoriteresult, container, false);
		//setRetainInstance(true);
		BusProvider.getInstance().register(this);
        return fragmentView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		BusProvider.getInstance().unregister(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		savedSearchView = (SavedSearchListView) getListView();
		savedSearchAdapter = new SavedResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSavedSearch());
		savedSearchView.setAdapter(savedSearchAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        savedSearchView.onItemClick(l, v, position, id);
	}

	@Subscribe
	public void onSyncAdapters(SyncAdaptersEvent event) {
		savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
		savedSearchAdapter.notifyDataSetChanged();
	}
}