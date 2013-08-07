package com.duckduckgo.mobile.android.fragment;

import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.SavedFeedCursorAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.feedEvents.SavedFeedItemSelectedEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.views.MainFeedListView;
import com.squareup.otto.Subscribe;


public class SavedFeedTabFragment extends ListFragment {
	MainFeedListView savedFeedView;
	SavedFeedCursorAdapter savedFeedAdapter;	
	
	/** (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout fragmentLayout = (LinearLayout)inflater.inflate(R.layout.fragment_tab_savedfeed, container, false);
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
		
		savedFeedView = (MainFeedListView) getListView();
		savedFeedAdapter = new SavedFeedCursorAdapter(getActivity(), DDGApplication.getDB().getCursorStoryFeed());
		savedFeedView.setAdapter(savedFeedAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Object item = getListView().getAdapter().getItem(position);
		FeedObject obj = null;
		if(item instanceof FeedObject) {
			obj = (FeedObject) item;
		}
		else if(item instanceof SQLiteCursor) {
			obj = new FeedObject(((SQLiteCursor) item));
		}
		
		if (obj != null) {
			BusProvider.getInstance().post(new SavedFeedItemSelectedEvent(obj));
		}
	}
	
	@Subscribe
	public void onSyncAdapters(SyncAdaptersEvent event) {
		savedFeedAdapter.changeCursor(DDGApplication.getDB().getCursorStoryFeed());
		savedFeedAdapter.notifyDataSetChanged();
	}
}