package com.duckduckgo.mobile.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.objects.history.ParentHistoryObject;
import com.duckduckgo.mobile.android.objects.history.SavedResultHistoryObject;
import com.duckduckgo.mobile.android.views.RecentSearchListView;
import com.duckduckgo.mobile.android.views.RecentSearchListView.OnHistoryItemSelectedListener;

public class SavedResultTabFragment extends Fragment {
	RecentSearchListView savedSearchView = null;
	
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
						
			savedSearchView = (RecentSearchListView) fragmentLayout.findViewById(R.id.savedSearchItems);
			savedSearchView.setAdapter(((DuckDuckGo) activity).mDuckDuckGoContainer.savedSearchAdapter);
			savedSearchView.setOnHistoryItemSelectedListener(new OnHistoryItemSelectedListener() {
				
				public void onHistoryItemSelected(ParentHistoryObject historyObject) {
					if(historyObject != null){							
						if(historyObject instanceof HistoryObject)
							((DuckDuckGo) activity).showHistoryObject((HistoryObject) historyObject);
						else if(historyObject instanceof SavedResultHistoryObject)
							((DuckDuckGo) activity).showSavedResultObject((SavedResultHistoryObject) historyObject);						
					}			
				}
			});
			
			savedSearchView.setOnHistoryItemLongClickListener(((DuckDuckGo) activity).mSavedResultLongClickListener);
			
		}
		
		return fragmentLayout;
	}
}