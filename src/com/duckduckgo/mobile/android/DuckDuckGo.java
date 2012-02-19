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
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DuckDuckGo extends Activity implements OnEditorActionListener, FeedListener, OnClickListener, OnItemClickListener {

	protected final String TAG = "DuckDuckGo";
	
	private AutoCompleteTextView searchField = null;
	private ProgressBar feedProgressBar = null;
	private MainFeedListView feedView = null;
	private MainFeedAdapter feedAdapter = null;
	private MainFeedTask mainFeedTask = null;
	private WebView mainWebView = null;
	private ImageButton homeSettingsButton = null;
	
	boolean hasUpdatedFeed = false;
	
	boolean webviewShowing = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        homeSettingsButton = (ImageButton) findViewById(R.id.settingsButton);
        homeSettingsButton.setOnClickListener(this);
        
        searchField = (AutoCompleteTextView) findViewById(R.id.searchEditText);
        searchField.setAdapter(new AutoCompleteResultsAdapter(this));
        searchField.setOnEditorActionListener(this);
        searchField.setOnItemClickListener(this);
        feedAdapter = new MainFeedAdapter(this);
        feedView = (MainFeedListView) findViewById(R.id.mainFeedItems);
        feedView.setAdapter(feedAdapter);
        
        // NOTE: After loading url multiple times on the device, it may crash
        // Related to android bug report 21266 - Watch this ticket for possible resolutions
        // http://code.google.com/p/android/issues/detail?id=21266
        // Possibly also related to CSS Transforms (bug 21305)
        // http://code.google.com/p/android/issues/detail?id=21305
        mainWebView = (WebView) findViewById(R.id.mainWebView);
        mainWebView.getSettings().setJavaScriptEnabled(true);
        mainWebView.setWebViewClient(new WebViewClient() {
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		view.loadUrl(url);
        		return true;
        	}
        });
        
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
	
	@Override
	public void onBackPressed() {
		if (webviewShowing) {
			if (mainWebView.canGoBack()) {
				mainWebView.goBack();
			} else {
				feedView.setVisibility(View.VISIBLE);
				mainWebView.setVisibility(View.GONE);
				mainWebView.clearView();
				homeSettingsButton.setImageResource(R.drawable.settings_button);
				webviewShowing = false;
			}
		} else {
			super.onBackPressed();
		}
	}
	
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v == searchField) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);

			String text = searchField.getText().toString();
			text.trim();
			
			if (text.length() > 0) {
				searchWebTerm(text);
			}
		}
		
		return false;
	}
	
	public void searchWebTerm(String term) {
		if (!webviewShowing) {
			feedView.setVisibility(View.GONE);
			mainWebView.setVisibility(View.VISIBLE);
			homeSettingsButton.setImageResource(R.drawable.home_button);
			webviewShowing = true;
		}
		
		mainWebView.loadUrl(DDGConstants.SEARCH_URL + term);
	}

	public void onFeedRetrieved(List<FeedObject> feed) {
		feedProgressBar.setVisibility(View.GONE);
		feedAdapter.setList(feed);
		feedAdapter.notifyDataSetChanged();
		hasUpdatedFeed = true;
	}

	public void onClick(View v) {
		if (v.equals(homeSettingsButton)) {
			//This is our button
			if (webviewShowing) {
				//We are going home!
				feedView.setVisibility(View.VISIBLE);
				mainWebView.setVisibility(View.GONE);
				mainWebView.clearHistory();
				mainWebView.clearView();
				homeSettingsButton.setImageResource(R.drawable.settings_button);
				webviewShowing = false;
			}
		}
		
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//Hide the keyboard and perform a search
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);

		String text = (String)parent.getAdapter().getItem(position);
		if (text != null) text.trim();
		
		if (text != null && text.length() > 0) {
			searchWebTerm(text);
		}
	}
}