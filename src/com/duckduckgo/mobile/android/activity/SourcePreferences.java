package com.duckduckgo.mobile.android.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.SourcesAdapter;
import com.duckduckgo.mobile.android.container.SourcePreferencesContainer;
import com.duckduckgo.mobile.android.objects.SourcesObject;
import com.duckduckgo.mobile.android.tasks.SourcesTask;
import com.duckduckgo.mobile.android.tasks.SourcesTask.SourcesListener;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;

public class SourcePreferences extends Activity implements SourcesListener {

    public SourcesTask sourcesTask = null;
    public SourcesAdapter sourcesAdapter = null;
	
	private ListView sourcesView = null;
	private Button defaultButton = null;
	private Button suggestSourceButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sources);
		
		SourcePreferencesContainer sourcePrefContainer = (SourcePreferencesContainer) getLastNonConfigurationInstance();
		if(sourcePrefContainer == null){
			sourcesAdapter = new SourcesAdapter(this);
		}
        else{
            sourcesAdapter = sourcePrefContainer.sourcesAdapter;
        }

		sourcesView = (ListView) findViewById(R.id.sourceItems);
        sourcesView.addHeaderView(createHeaderView());
        sourcesView.addFooterView(createFooterView());
		sourcesView.addFooterView(createSuggestSourceButton());

		sourcesView.setAdapter(sourcesAdapter);

        defaultButton = (Button) findViewById(R.id.sourceDefaultButton);
		defaultButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), R.string.DefaultsSetToast, Toast.LENGTH_SHORT).show();

				DDGUtils.deleteSet(DDGApplication.getSharedPreferences(), "sourceset");
				DDGControlVar.hasUpdatedFeed = false;
				
				// copy defaults				
				DDGControlVar.userAllowedSources.clear();
				DDGControlVar.userDisallowedSources.clear();
				
				// reset source set of underlying list adapter
				sourcesAdapter.notifyDataSetChanged();
				sourcesView.invalidateViews();
			}
		});
		
		suggestSourceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				Intent intent = DDGUtils.newEmailIntent(SourcePreferences.this.getResources().getString(R.string.FeedbackTo), 
						SourcePreferences.this.getResources().getString(R.string.FeedbackSubject), getString(R.string.SuggestedSources), "");
		        startActivity(Intent.createChooser(intent, getResources().getString(R.string.SelectAppToSend)));
			}
		});
	}

    private View createHeaderView() {
        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.preference_category_summary, null, false);
        TextView titleTv = ((TextView) headerView.findViewById(android.R.id.title));
        titleTv.setText(R.string.WaterCoolerSources);
        TextView summaryTv = ((TextView) headerView.findViewById(android.R.id.summary));
        summaryTv.setText(R.string.SummaryWaterCooler);
        return headerView;
    }

    private View createFooterView() {
        View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.default_button_layout, null, false);
        return footerView;
    }

    private View createSuggestSourceButton() {
        View footerView;
        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.default_button_layout, null, false);
        footerView.findViewById(R.id.sourceDefaultButton).setId(R.id.suggestSourceButton);
        suggestSourceButton = (Button) footerView.findViewById(R.id.suggestSourceButton);
        suggestSourceButton.setText(R.string.SuggestSource);
        return footerView;
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
	
	@Override
    public Object onRetainNonConfigurationInstance() {
    	// return page container, holding all non-view data
        SourcePreferencesContainer sourcePreferencesContainer = new SourcePreferencesContainer();
        sourcePreferencesContainer.sourcesAdapter = sourcesAdapter;
        sourcePreferencesContainer.sourcesTask = sourcesTask;
        return sourcePreferencesContainer;
    }
	
}
