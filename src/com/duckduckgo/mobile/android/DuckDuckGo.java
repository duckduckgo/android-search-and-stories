package com.duckduckgo.mobile.android;

import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DuckDuckGo extends Activity implements OnEditorActionListener {

	protected final String TAG = "DuckDuckGo";
	
	private AutoCompleteTextView searchField = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        searchField = (AutoCompleteTextView) findViewById(R.id.searchEditText);
        searchField.setAdapter(new AutoCompleteResultsAdapter(this, android.R.layout.simple_dropdown_item_1line));
        searchField.setOnEditorActionListener(this);
        
        //TODO: Get the main ListView and fill it with feed data through an Asynchronous task (maybe a service that can return results every X amount of time?)
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
}