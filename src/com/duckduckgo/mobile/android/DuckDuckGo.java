package com.duckduckgo.mobile.android;

import java.util.List;

import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask.FeedListener;
import com.duckduckgo.mobile.android.views.MainFeedListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DuckDuckGo extends Activity implements OnEditorActionListener, FeedListener {

	protected final String TAG = "DuckDuckGo";
	
	private AutoCompleteTextView searchField = null;
	private ProgressBar feedProgressBar = null;
	private MainFeedListView feedView = null;
	private MainFeedAdapter feedAdapter = null;
	private MainFeedTask mainFeedTask = null;
	
	boolean hasUpdatedFeed = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        searchField = (AutoCompleteTextView) findViewById(R.id.searchEditText);
        searchField.setAdapter(new AutoCompleteResultsAdapter(this, android.R.layout.simple_dropdown_item_1line));
        searchField.setOnEditorActionListener(this);
        
        feedAdapter = new MainFeedAdapter(this);
        feedView = (MainFeedListView) findViewById(R.id.mainFeedItems);
        feedView.setAdapter(feedAdapter);
        
        feedProgressBar = (ProgressBar) findViewById(R.id.feedLoadingProgress);
    }
	
	@Override
	public void onResume() {
		super.onResume();
		if (!hasUpdatedFeed) {
			mainFeedTask = new MainFeedTask(this);
			mainFeedTask.execute();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mainFeedTask != null) {
			mainFeedTask.cancel(false);
			mainFeedTask = null;
		}
	}
	
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v == searchField) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			String text = searchField.getText().toString();
			text.trim();
			
			if (text.length() > 0) {
				searchWebTerm(text);
			}
		}
		
		return false;
	}
	
	public void searchWebTerm(String term) {
		 Uri uri = Uri.parse(DDGConstants.SEARCH_URL + term);
		 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		 startActivity(intent);
	}

	public void onFeedRetrieved(List<FeedObject> feed) {
		feedProgressBar.setVisibility(View.GONE);
		feedAdapter.setList(feed);
		feedAdapter.notifyDataSetChanged();
		hasUpdatedFeed = true;
	}
}