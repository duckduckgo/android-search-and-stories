package com.duckduckgo.mobile.android.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.SourcesAdapter;
import com.duckduckgo.mobile.android.container.SourcePreferencesContainer;
import com.duckduckgo.mobile.android.objects.SourcesObject;
import com.duckduckgo.mobile.android.tasks.SourcesTask;
import com.duckduckgo.mobile.android.tasks.SourcesTask.SourcesListener;

public class SourcePreferences extends Activity implements SourcesListener {

	SourcePreferencesContainer sourcePrefContainer;
	
	private ListView sourcesView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sources);
		
		sourcePrefContainer = (SourcePreferencesContainer) getLastNonConfigurationInstance();
		if(sourcePrefContainer == null){
			sourcePrefContainer = new SourcePreferencesContainer();
			sourcePrefContainer.sourcesAdapter = new SourcesAdapter(this);
		}
		
		sourcesView = (ListView) findViewById(R.id.sourceItems);
		sourcesView.setAdapter(sourcePrefContainer.sourcesAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		sourcePrefContainer.sourcesTask = new SourcesTask(this);
		sourcePrefContainer.sourcesTask.execute();
	}

	public void onSourcesRetrieved(List<SourcesObject> feed) {
		sourcePrefContainer.sourcesAdapter.setList(feed);
		sourcePrefContainer.sourcesAdapter.notifyDataSetChanged();
		
	}

	public void onSourcesRetrievalFailed() {
		//If the sourcesTask is null, we are currently paused
		//Otherwise, we can try again
		if (sourcePrefContainer.sourcesTask != null) {
			sourcePrefContainer.sourcesTask = new SourcesTask(this);
			sourcePrefContainer.sourcesTask.execute();
		}
		
	}
	
	@Override
    public Object onRetainNonConfigurationInstance() {
    	// return page container, holding all non-view data
    	return sourcePrefContainer;
    }
	
}
