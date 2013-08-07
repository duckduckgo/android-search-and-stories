package com.duckduckgo.mobile.android.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemSelectedEvent;
import com.duckduckgo.mobile.android.views.SavedSearchListView;
import com.squareup.otto.Subscribe;

public class SavedResultTabFragment extends ListFragment {
	SavedSearchListView savedSearchView;
	SavedResultCursorAdapter savedSearchAdapter;
	
	/** (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout fragmentLayout = (LinearLayout)inflater.inflate(R.layout.fragment_tab_savedresult, container, false);
		setRetainInstance(true);
		BusProvider.getInstance().register(this);
		return fragmentLayout;
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
		savedSearchView.setDivider(null);
		savedSearchAdapter = new SavedResultCursorAdapter(getActivity(), DDGApplication.getDB().getCursorSavedSearch());
		savedSearchView.setAdapter(savedSearchAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Object adapter = getListView().getAdapter();
		Cursor c = null;
		
		if(adapter instanceof SavedResultCursorAdapter) {
			c = (Cursor) ((SavedResultCursorAdapter) adapter).getItem(position);
			String query = c.getString(c.getColumnIndex("query"));
			if(query != null){
				BusProvider.getInstance().post(new SavedSearchItemSelectedEvent(query));				
			}
		}
	}
	
	@Subscribe
	public void onSyncAdapters(SyncAdaptersEvent event) {
		savedSearchAdapter.changeCursor(DDGApplication.getDB().getCursorSavedSearch());
		savedSearchAdapter.notifyDataSetChanged();
	}
}