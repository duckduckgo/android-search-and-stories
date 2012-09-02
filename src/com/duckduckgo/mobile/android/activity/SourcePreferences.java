package com.duckduckgo.mobile.android.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.SourcesAdapter;
import com.duckduckgo.mobile.android.objects.SourcesObject;
import com.duckduckgo.mobile.android.tasks.SourcesTask;
import com.duckduckgo.mobile.android.tasks.SourcesTask.SourcesListener;

public class SourcePreferences extends Activity implements SourcesListener {

	private SourcesTask sourcesTask = null;
	
	private ListView sourcesView = null;
	private SourcesAdapter sourcesAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sources);
		
		sourcesView = (ListView) findViewById(R.id.sourceItems);
		sourcesAdapter = new SourcesAdapter(this);
		sourcesView.setAdapter(sourcesAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		sourcesTask = new SourcesTask(this);
		sourcesTask.execute();
	}

	public void onSourcesRetrieved(List<SourcesObject> feed) {
		sourcesAdapter.setList(feed);
		sourcesAdapter.notifyDataSetChanged();
		
	}

	public void onSourcesRetrievalFailed() {
		//If the sourcesTask is null, we are currently paused
		//Otherwise, we can try again
		if (sourcesTask != null) {
			sourcesTask = new SourcesTask(this);
			sourcesTask.execute();
		}
		
	}
	
}
