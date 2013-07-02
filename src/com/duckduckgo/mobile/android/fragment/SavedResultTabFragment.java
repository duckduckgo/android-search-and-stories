package com.duckduckgo.mobile.android.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SavedSearchItemSelectedEvent;
import com.duckduckgo.mobile.android.views.SavedSearchListView;

public class SavedResultTabFragment extends ListFragment {
	SavedSearchListView savedSearchView = null;
	
	/** (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout fragmentLayout = (LinearLayout)inflater.inflate(R.layout.fragment_tab_savedresult, container, false);
		return fragmentLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// setup for real work
		final Activity activity = getActivity();

		if(activity instanceof DuckDuckGo) {
			savedSearchView = (SavedSearchListView) getListView();
			savedSearchView.setDivider(null);
			savedSearchView.setAdapter(((DuckDuckGo) activity).mDuckDuckGoContainer.savedSearchAdapter);
		}
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
}