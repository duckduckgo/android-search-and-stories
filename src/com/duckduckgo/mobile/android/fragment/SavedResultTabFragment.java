package com.duckduckgo.mobile.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.objects.HistoryObject;
import com.duckduckgo.mobile.android.objects.SavedResultObject;
import com.duckduckgo.mobile.android.views.RecentSearchListView;
import com.duckduckgo.mobile.android.views.RecentSearchListView.OnHistoryItemSelectedListener;

public class SavedResultTabFragment extends Fragment {
	RecentSearchListView savedSearchView = null;
	SavedResultCursorAdapter savedSearchAdapter = null;
	
	/** (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			// no reason to try to create its view hierarchy because it won't be displayed.
            return null;
        }
		
		// setup for real work
		final Activity activity = getActivity();
		
		LinearLayout fragmentLayout = (LinearLayout)inflater.inflate(R.layout.fragment_tab_savedresult, container, false);
		
		if(activity instanceof DuckDuckGo) {	
			
    		savedSearchAdapter = new SavedResultCursorAdapter(activity, activity, DDGApplication.getDB().getCursorResultFeed());
			
			savedSearchView = (RecentSearchListView) fragmentLayout.findViewById(R.id.savedSearchItems);
			savedSearchView.setAdapter(savedSearchAdapter);
			savedSearchView.setOnHistoryItemSelectedListener(new OnHistoryItemSelectedListener() {
				
				public void onHistoryItemSelected(HistoryObject historyObject) {
					if(historyObject != null){	
						((DuckDuckGo) activity).showWebUrl(historyObject.getUrl());
					}			
				}
				
				public void onSavedResultSelected(SavedResultObject savedResultObject) {
					if(savedResultObject != null){	
						((DuckDuckGo) activity).showWebUrl(savedResultObject.getUrl());
					}			
				}
			});
		}
		
		return fragmentLayout;
	}
}