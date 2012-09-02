package com.duckduckgo.mobile.android.activity;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.download.Holder;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask.FeedListener;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.views.MainFeedListView;
import com.duckduckgo.mobile.android.views.MainFeedListView.OnMainFeedItemSelectedListener;
import com.duckduckgo.mobile.android.views.MainFeedListView.OnMainFeedItemLongClickListener;
import com.duckduckgo.mobile.android.views.RecentSearchListView;
import com.duckduckgo.mobile.android.views.RecentSearchListView.OnRecentSearchItemSelectedListener;

public class DuckDuckGo extends Activity implements OnEditorActionListener, FeedListener, OnClickListener, OnItemClickListener {

	protected final String TAG = "DuckDuckGo";
	
	private AutoCompleteTextView searchField = null;
	private ProgressBar feedProgressBar = null;
	private MainFeedListView feedView = null;
	private MainFeedAdapter feedAdapter = null;
	private MainFeedTask mainFeedTask = null;
	
	private RecentSearchListView recentSearchView = null;
	private ArrayAdapter<String> recentSearchAdapter = null;
	private Set<String> recentSearchSet = null;
	
	private WebView mainWebView = null;
	private ImageButton homeSettingsButton = null;
	private LinearLayout prefLayout = null;
	
	private boolean hasUpdatedFeed = false;
	private boolean webviewShowing = false;
	private boolean prefShowing = false;
	
	private Drawable progressDrawable, searchFieldDrawable;
	
	private SharedPreferences sharedPreferences;
			
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        
        setContentView(R.layout.main);
        
        sharedPreferences = DDGApplication.getSharedPreferences();
        
        homeSettingsButton = (ImageButton) findViewById(R.id.settingsButton);
        homeSettingsButton.setOnClickListener(this);
        
        searchField = (AutoCompleteTextView) findViewById(R.id.searchEditText);
        searchField.setAdapter(new AutoCompleteResultsAdapter(this));
        searchField.setOnEditorActionListener(this);
        searchField.setOnItemClickListener(this);

        // This makes a little (X) to clear the search bar.
        final Drawable x = getResources().getDrawable(android.R.drawable.presence_offline);
        x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
        searchField.setCompoundDrawables(null, null, searchField.getText().toString().equals("") ? null : x, null);

        searchField.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (searchField.getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > searchField.getWidth() - searchField.getPaddingRight() - x.getIntrinsicWidth()) {
                	searchField.setText("");
                	searchField.setCompoundDrawables(null, null, null, null);
                }
                return false;
            }

        });

        searchField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	searchField.setCompoundDrawables(null, null, searchField.getText().toString().equals("") ? null : x, null);
            }

            public void afterTextChanged(Editable arg0) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        recentSearchSet = DDGUtils.loadSet(sharedPreferences, "recentsearch");

        recentSearchAdapter = new ArrayAdapter<String>(this, 
        		R.layout.recentsearch_list_layout, R.id.recentSearchText, 
        		new ArrayList<String>(recentSearchSet));
        recentSearchView = (RecentSearchListView) findViewById(R.id.recentSearchItems);
        View header = getLayoutInflater().inflate(R.layout.recentsearch_header, null);
        recentSearchView.addHeaderView(header);
        recentSearchView.setAdapter(recentSearchAdapter);
        recentSearchView.setOnRecentSearchItemSelectedListener(new OnRecentSearchItemSelectedListener() {
			
			public void onRecentSearchItemSelected(String recentQuery) {
				if(recentQuery != null){
					searchWebTerm(recentQuery);
				}				
			}
		});
        
        
        feedAdapter = new MainFeedAdapter(this);
        feedView = (MainFeedListView) findViewById(R.id.mainFeedItems);
        feedView.setAdapter(feedAdapter);
        feedView.setOnMainFeedItemSelectedListener(new OnMainFeedItemSelectedListener() {
			public void onMainFeedItemSelected(FeedObject feedObject) {
				String url = feedObject.getUrl();
				if (url != null) {
					searchOrGoToUrl(url);
				}
			}
        });
        feedView.setOnMainFeedItemLongClickListener(new OnMainFeedItemLongClickListener() {
			public void onMainFeedItemLongClick(FeedObject feedObject) {
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, "WatrCoolr URL: "+feedObject.getUrl());
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, feedObject.getTitle());
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
			}
        });
        feedView.setOnScrollListener(new OnScrollListener() {
			
        	int firstVisibleItem;
        	
        	public void onScroll(AbsListView view, int firstVisibleItem,
        			int visibleItemCount, int totalItemCount) {
        		if(visibleItemCount > 0){
        			feedAdapter.scrolling = true;
        		}
        		this.firstVisibleItem = firstVisibleItem;
        	}

        	public void onScrollStateChanged(AbsListView view, int scrollState) {
        		Holder holder;
        		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
        				feedAdapter.scrolling = false;
        				int count = view.getChildCount();
        				for(int i=0;i<count;++i){
        					View cv = view.getChildAt(i);
        					holder = (Holder) cv.getTag();
        					if(!holder.imageViewBackground.getMemCacheDrawn()){
        						feedAdapter.getView(firstVisibleItem+i, cv, view);
        					}
        				}
        	    }
        		else {
        			feedAdapter.scrolling = true;
        			DDGApplication.getImageDownloader().clearAllDownloads();
        		}
        		
        	}
		});
        
        progressDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.page_progress);
        searchFieldDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.searchfield);
        searchFieldDrawable.setAlpha(150);
        
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
        	
        	@Override
        	public void onPageStarted(WebView view, String url, Bitmap favicon) {
        		super.onPageStarted(view, url, favicon);          	
        	}
        	
        	@Override
        	public void onPageFinished (WebView view, String url) {
        		        		
        		if (url.contains("duckduckgo.com")) {
        			// FIXME api level
        	        mainWebView.getSettings().setSupportZoom(true);
        	        mainWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        	        //mainWebView.getSettings().setEnableSmoothTransition(false);
        	        mainWebView.getSettings().setBuiltInZoomControls(false);
        	        //mainWebView.getSettings().setDisplayZoomControls(false);
        	        mainWebView.getSettings().setUseWideViewPort(false);
        	        mainWebView.getSettings().setLoadWithOverviewMode(false);
        	        //mainWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        	        //mainWebView.getSettings().setPluginsEnabled(true); 
        			URL fullURL = null;
        			try {
						fullURL = new URL(url);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}

        			if (fullURL != null) {
        				//Okay, it's a valid url, which we already knew...
        				String query = fullURL.getQuery();
        				if (query != null) {
        					//Get the actual query string now...
        					int index = query.indexOf("q=");
        					if (index != -1) {
            					String text = query.substring(query.indexOf("q=") + 2);
            					if (text.contains("&")) {
            						text = text.substring(0, text.indexOf("&"));
            					}
            					String realText = URLDecoder.decode(text);
            					setSearchBarText(realText);
        					} else {
        						setSearchBarText(url);
        					}
        				} else {
        					setSearchBarText(url);
        				}
        			} else {
        				//Just in case...
        				setSearchBarText(url);
        			}
        		} else {
        			//This isn't duckduck go...
        			// FIXME api level
        	        mainWebView.getSettings().setSupportZoom(true);
        	        mainWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        	        //mainWebView.getSettings().setEnableSmoothTransition(true);
        	        mainWebView.getSettings().setBuiltInZoomControls(true);
        	        //mainWebView.getSettings().setDisplayZoomControls(false);
        	        mainWebView.getSettings().setUseWideViewPort(true);
        	        mainWebView.getSettings().setLoadWithOverviewMode(true);
        	        //mainWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        	        //mainWebView.getSettings().setPluginsEnabled(true); 
        			setSearchBarText(url);
        		}
        		
				searchField.setBackgroundDrawable(searchFieldDrawable);

        	}
        });
                
        mainWebView.setWebChromeClient(new WebChromeClient(){
        	@Override
        	public void onProgressChanged(WebView view, int newProgress) {
        		super.onProgressChanged(view, newProgress);
        		
        		if(newProgress == 100){
        			searchField.setBackgroundDrawable(searchFieldDrawable);
        		}
        		else {
	        		progressDrawable.setLevel(newProgress*100);
	        		searchField.setBackgroundDrawable(progressDrawable);
        		}

        	}
        });
        
        feedProgressBar = (ProgressBar) findViewById(R.id.feedLoadingProgress);
        
        prefLayout = (LinearLayout) findViewById(R.id.prefLayout);
        
    }
	
	public void setSearchBarText(String text) {
		searchField.setFocusable(false);
		searchField.setFocusableInTouchMode(false);
		searchField.setText(text);            
		searchField.setFocusable(true);
		searchField.setFocusableInTouchMode(true);
	}
	
	private void switchScreens(){
        // control which start screen is shown & configure related views
        if(DDGControlVar.START_SCREEN == SCREEN.SCR_NEWS_FEED){
        	recentSearchView.setVisibility(View.GONE);
        	feedView.setVisibility(View.VISIBLE);
        	if(!hasUpdatedFeed){
        		feedProgressBar.setVisibility(View.VISIBLE);
        	}
        }
        else if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH){
        	feedView.setVisibility(View.GONE);
        	feedProgressBar.setVisibility(View.GONE);
        	recentSearchView.setVisibility(View.VISIBLE);
        }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		switchScreens();
		
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
				mainWebView.stopLoading();
				
				switchScreens();
				
				mainWebView.setVisibility(View.GONE);
				prefLayout.setVisibility(View.GONE);
				mainWebView.clearView();
				homeSettingsButton.setImageResource(R.drawable.settings_button);
				webviewShowing = false;
				searchField.setText("");
				
				searchField.setBackgroundDrawable(searchFieldDrawable);

			}
		}
		else if(prefShowing){
			prefLayout.setVisibility(View.GONE);
			prefShowing = false;
			
			homeSettingsButton.setImageResource(R.drawable.settings_button);
			
			switchScreens();
		}
		else {
			super.onBackPressed();
		}
	}
	
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v == searchField) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
			searchField.dismissDropDown();

			String text = searchField.getText().toString();
			text.trim();
			
			searchOrGoToUrl(text);
		}
		
		return false;
	}
	
	public void searchOrGoToUrl(String text) {
		if (text.length() > 0) {
			URL searchAsUrl = null;
			String modifiedText = null;

			try {
				searchAsUrl = new URL(text);
				searchAsUrl.toURI();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
				searchAsUrl = null;
			}
			
			if (searchAsUrl == null) {
				modifiedText = "http://" + text;
				try {
					searchAsUrl = new URL(modifiedText);
					searchAsUrl.toURI();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
					searchAsUrl = null;
				}
			}

			//We use the . check to determine if this is a single word or not... 
			//if it doesn't contain a . plus domain (2 more characters) it won't be a URL, even if it's valid, like http://test
			if (searchAsUrl != null) {
				if (modifiedText != null) {
					//Show the modified url text
					if (modifiedText.contains(".") && modifiedText.length() > (modifiedText.indexOf(".") + 2)) {
						showWebUrl(modifiedText);
					} else {
						searchWebTerm(text);
					}
				} else {
					if (text.contains(".") && text.length() > (text.indexOf(".") + 2)) {
						//Show the url text
						showWebUrl(text);
					} else {
						searchWebTerm(text);
					}
				}
			} else {
				searchWebTerm(text);
			}
		}
	}
	
	public void searchWebTerm(String term) {
		if (!webviewShowing) {
			feedView.setVisibility(View.GONE);
			recentSearchView.setVisibility(View.GONE);
			mainWebView.setVisibility(View.VISIBLE);
			homeSettingsButton.setImageResource(R.drawable.home_button);
			webviewShowing = true;
		}
		
		mainWebView.loadUrl(DDGConstants.SEARCH_URL + URLEncoder.encode(term));
		
		// save recent query if "record history" is enabled
		if(sharedPreferences.getBoolean("recordHistoryPref", false)){
			if(!recentSearchSet.contains(term)){
				recentSearchSet.add(term);
				recentSearchAdapter.add(term);
				
				Set<String> recentSearchSet = DDGUtils.loadSet(sharedPreferences, "recentsearch");
				recentSearchSet.add(term);
				DDGUtils.saveSet(sharedPreferences, recentSearchSet, "recentsearch");
			}
		}
	}
	
	public void clearRecentSearch() {
		recentSearchSet.clear();
		recentSearchAdapter.clear();
		recentSearchView.invalidate();
	}
	
	public void showWebUrl(String url) {
		if (!webviewShowing) {
			feedView.setVisibility(View.GONE);
			mainWebView.setVisibility(View.VISIBLE);
			homeSettingsButton.setImageResource(R.drawable.home_button);
			webviewShowing = true;
		}
		
		mainWebView.loadUrl(url);
	}

	public void onFeedRetrieved(List<FeedObject> feed) {
		feedProgressBar.setVisibility(View.GONE);
		feedAdapter.setList(feed);
		feedAdapter.notifyDataSetChanged();
		hasUpdatedFeed = true;
	}
	
	public void onFeedRetrievalFailed() {
		//If the mainFeedTask is null, we are currently paused
		//Otherwise, we can try again
		if (mainFeedTask != null) {
			mainFeedTask = new MainFeedTask(this);
			mainFeedTask.execute();
		}
	}
	
	@TargetApi(11)
	private void showPrefFragment(){
      	getFragmentManager().beginTransaction().replace(R.id.prefFragment,
                new DDGPreferenceFragment()).commit();
	}

	public void onClick(View v) {
		if (v.equals(homeSettingsButton)) {
			//This is our button
			if (webviewShowing) {
				//We are going home!
				switchScreens();
				mainWebView.setVisibility(View.GONE);
				prefLayout.setVisibility(View.GONE);
				mainWebView.clearHistory();
				mainWebView.clearView();
				homeSettingsButton.setImageResource(R.drawable.settings_button);
				searchField.setText("");
				webviewShowing = false;
				
				searchField.setBackgroundDrawable(searchFieldDrawable);
			}
			else {
				// test this part
				
				if(!prefShowing){

					if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
				        Intent intent = new Intent(getBaseContext(), Preferences.class);
				        startActivity(intent);
					}
					else {
						showPrefFragment();
						
						feedView.setVisibility(View.GONE);
						mainWebView.setVisibility(View.GONE);
						recentSearchView.setVisibility(View.GONE);
						prefLayout.setVisibility(View.VISIBLE);
						prefShowing = true;
						
						homeSettingsButton.setImageResource(R.drawable.home_button);
						
						searchField.setBackgroundDrawable(searchFieldDrawable);
					}
				
				}
				else {
					// we are in preference screen, pressed home
					
					prefLayout.setVisibility(View.GONE);
					prefShowing = false;
					
					homeSettingsButton.setImageResource(R.drawable.settings_button);
					
					switchScreens();
				}

			}
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//Hide the keyboard and perform a search
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
		searchField.dismissDropDown();
		
		String text = (String)parent.getAdapter().getItem(position);
		if (text != null) {
			text.trim();
			searchOrGoToUrl(text);
		}
	}

}