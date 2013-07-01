package com.duckduckgo.mobile.android.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.SavedFeedCursorAdapter;
import com.duckduckgo.mobile.android.adapters.SavedResultCursorAdapter;
import com.duckduckgo.mobile.android.tabhost.TabHostExt;

public class SavedActivity extends FragmentActivity {

	private TabHostExt savedTabHost;
	
	public SavedResultCursorAdapter savedSearchAdapter;
	public SavedFeedCursorAdapter savedFeedAdapter;	
	
	private void initialiseTabHost() {
		savedTabHost = (TabHostExt) findViewById(R.id.saved_tab_host_ext);
		savedTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		savedTabHost.addDefaultTabs();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_saved_view);
        initialiseTabHost();
        if(savedInstanceState != null) {
        	savedTabHost.setCurrentTabByTag(savedInstanceState.getString("simple")); //set the tab as per the saved state
        }
	}
	
}
