package com.duckduckgo.mobile.android.activity;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.Holder;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.tasks.DownloadSourceIconTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask.FeedListener;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.views.FanView;
import com.duckduckgo.mobile.android.views.MainFeedListView;
import com.duckduckgo.mobile.android.views.MainFeedListView.OnMainFeedItemLongClickListener;
import com.duckduckgo.mobile.android.views.MainFeedListView.OnMainFeedItemSelectedListener;
import com.duckduckgo.mobile.android.views.RecentSearchListView;
import com.duckduckgo.mobile.android.views.RecentSearchListView.OnRecentSearchItemSelectedListener;

public class DuckDuckGo extends Activity implements OnEditorActionListener, FeedListener, OnClickListener {

	protected final String TAG = "DuckDuckGo";
	
	DuckDuckGoContainer mDuckDuckGoContainer;
	
	private AutoCompleteTextView searchField = null;
	private ProgressBar feedProgressBar = null;
	private MainFeedListView feedView = null;
	private RecentSearchListView leftRecentView = null;
	
	ArrayAdapter<String> lRecentAdapter;
	
	private RecentSearchListView recentSearchView = null;
	
	private WebView mainWebView = null;
	private ImageButton homeSettingsButton = null;
	private LinearLayout prefLayout = null;
	private LinearLayout leftMainLayout = null;
	
	private TextView leftHomeTextView = null;
	private TextView leftSavedTextView = null;
	private TextView leftSettingsTextView = null;
	
	private SharedPreferences sharedPreferences;
	
	private FanView fan;
	
	private boolean savedState = false;
	
	ArrayList<String> listContent;
	
	private final int PREFERENCES_RESULT = 0;
			
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        
        setContentView(R.layout.twopane);
        
        fan = (FanView) findViewById(R.id.fan_view);
        fan.setViews(R.layout.main, R.layout.left_layout);
        
//        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        String countryCode = tm.getSimCountryIso();
//        String lang = getResources().getConfiguration().locale.getLanguage();
//        Log.v("COUNLANG",countryCode + " " + lang);
        
        
        if(savedInstanceState != null)
        	savedState = true;
        
        sharedPreferences = DDGApplication.getSharedPreferences();
        
		mDuckDuckGoContainer = (DuckDuckGoContainer) getLastNonConfigurationInstance();
    	if(mDuckDuckGoContainer == null){
    		mDuckDuckGoContainer = new DuckDuckGoContainer();
    		
    		mDuckDuckGoContainer.webviewShowing = false;
    		mDuckDuckGoContainer.prefShowing = false;
    		
    		mDuckDuckGoContainer.progressDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.page_progress);
    		mDuckDuckGoContainer.searchFieldDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.searchfield);
    		mDuckDuckGoContainer.searchFieldDrawable.setAlpha(150);
    		
    		mDuckDuckGoContainer.recentSearchSet = DDGUtils.loadSet(sharedPreferences, "recentsearch");

    		mDuckDuckGoContainer.recentSearchAdapter = new ArrayAdapter<String>(this, 
            		R.layout.recentsearch_list_layout, R.id.recentSearchText, 
            		new ArrayList<String>(mDuckDuckGoContainer.recentSearchSet));
    		
    		OnClickListener sourceClickListener = new OnClickListener() {
				
				public void onClick(View v) {
					// source filtering
					
					if(DDGControlVar.targetSource != null){
						DDGControlVar.targetSource = null;
						
						DDGControlVar.hasUpdatedFeed = false;
						mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(DuckDuckGo.this);
						mDuckDuckGoContainer.mainFeedTask.execute();						
					}
					else {
					
						DDGControlVar.targetSource = ((AsyncImageView) v).getType();
						
						DDGControlVar.hasUpdatedFeed = false;
						mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(DuckDuckGo.this);
						mDuckDuckGoContainer.mainFeedTask.execute();
					}
					
				}
			};
			
    		mDuckDuckGoContainer.feedAdapter = new MainFeedAdapter(this, sourceClickListener);
    		
    		mDuckDuckGoContainer.mainFeedTask = null;
    		mDuckDuckGoContainer.sourceIconTask = null;
    		
    		mDuckDuckGoContainer.feedItemLoading = false;
    		
    		
    	}
    	
    	leftMainLayout = (LinearLayout) findViewById(R.id.LeftMainLayout);
    	
    	leftHomeTextView = (TextView) findViewById(R.id.LeftHomeTextView);
    	leftSavedTextView = (TextView) findViewById(R.id.LeftSavedTextView);
    	leftSettingsTextView = (TextView) findViewById(R.id.LeftSettingsTextView);
    	
    	int pixelValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) 30.0, getResources().getDisplayMetrics());
    	
    	Drawable caret = getResources().getDrawable(R.drawable.icon_caret_selector);
    	caret.setBounds(0, 0, pixelValue, pixelValue);
    	
    	Drawable xt = getResources().getDrawable(R.drawable.icon_home_selector);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftHomeTextView.setCompoundDrawables(xt, null, caret, null);
        
        caret = getResources().getDrawable(R.drawable.icon_caret_selector);
    	caret.setBounds(0, 0, pixelValue, pixelValue);
        
    	xt = getResources().getDrawable(R.drawable.icon_saved_selector);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftSavedTextView.setCompoundDrawables(xt, null, caret, null);
        
        caret = getResources().getDrawable(R.drawable.icon_caret_selector);
    	caret.setBounds(0, 0, pixelValue, pixelValue);
        
    	xt = getResources().getDrawable(R.drawable.icon_settings_selector);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftSettingsTextView.setCompoundDrawables(xt, null, caret, null);
    	
    	leftHomeTextView.setOnClickListener(this);
    	leftSavedTextView.setOnClickListener(this);
    	leftSettingsTextView.setOnClickListener(this);
    	
    	leftRecentView = (RecentSearchListView) findViewById(R.id.LeftRecentView);
    	
    	listContent = new ArrayList<String>();
    	for(String s : getResources().getStringArray(R.array.leftMenuDefault)) {
    		listContent.add(s);
    	}
    	
    	leftRecentView.setAdapter(mDuckDuckGoContainer.recentSearchAdapter);
    	leftRecentView.setOnRecentSearchItemSelectedListener(new OnRecentSearchItemSelectedListener() {
			
			public void onRecentSearchItemSelected(String recentQuery) {
				fan.showMenu();
				
				if(recentQuery != null){
					searchWebTerm(recentQuery);
				}				
			}
		});
        
        homeSettingsButton = (ImageButton) findViewById(R.id.settingsButton);
        homeSettingsButton.setOnClickListener(this);
        
        searchField = (AutoCompleteTextView) findViewById(R.id.searchEditText);
        searchField.setAdapter(new AutoCompleteResultsAdapter(this));
        searchField.setOnEditorActionListener(this);

        searchField.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!sharedPreferences.getBoolean("modifyQueryPref", false)){
					//Hide the keyboard and perform a search
					hideKeyboard(searchField);
					searchField.dismissDropDown();
					
					String text = (String)parent.getAdapter().getItem(position);
					if (text != null) {
						text.trim();
						searchOrGoToUrl(text);
					}		
				}
				
			}
		});

        // This makes a little (X) to clear the search bar.
        final Drawable x = getResources().getDrawable(R.drawable.stop);
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


        recentSearchView = (RecentSearchListView) findViewById(R.id.recentSearchItems);
        View header = getLayoutInflater().inflate(R.layout.recentsearch_header, null);
        recentSearchView.addHeaderView(header);
        recentSearchView.setAdapter(mDuckDuckGoContainer.recentSearchAdapter);
        recentSearchView.setOnRecentSearchItemSelectedListener(new OnRecentSearchItemSelectedListener() {
			
			public void onRecentSearchItemSelected(String recentQuery) {
				if(recentQuery != null){
					searchWebTerm(recentQuery);
				}				
			}
		});
        
        
        feedView = (MainFeedListView) findViewById(R.id.mainFeedItems);
        feedView.setAdapter(mDuckDuckGoContainer.feedAdapter);
        feedView.setOnMainFeedItemSelectedListener(new OnMainFeedItemSelectedListener() {
			public void onMainFeedItemSelected(FeedObject feedObject) {
				// close left nav if it's open
				if(fan.isOpen()){
					fan.showMenu();
				}
				
				String url = feedObject.getUrl();
				if (url != null) {
					mDuckDuckGoContainer.feedItemLoading = true;
					searchOrGoToUrl(url);
				}
				
				// record article as read
				String feedId = feedObject.getId();
				if(feedId != null){
					DDGControlVar.readArticles.add(feedId);
					mDuckDuckGoContainer.feedAdapter.notifyDataSetChanged();
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
        			mDuckDuckGoContainer.feedAdapter.scrolling = true;
        		}
        		this.firstVisibleItem = firstVisibleItem;
        	}

        	public void onScrollStateChanged(AbsListView view, int scrollState) {
        		Holder holder;
        		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
        				mDuckDuckGoContainer.feedAdapter.scrolling = false;
        				int count = view.getChildCount();
        				for(int i=0;i<count;++i){
        					View cv = view.getChildAt(i);
        					holder = (Holder) cv.getTag();
        					if(!holder.imageViewBackground.getMemCacheDrawn()){
        						mDuckDuckGoContainer.feedAdapter.getView(firstVisibleItem+i, cv, view);
        					}
        				}
        	    }
        		else {
        			mDuckDuckGoContainer.feedAdapter.scrolling = true;
        			DDGApplication.getImageDownloader().clearAllDownloads();
        		}
        		
        	}
		});
        
        // NOTE: After loading url multiple times on the device, it may crash
        // Related to android bug report 21266 - Watch this ticket for possible resolutions
        // http://code.google.com/p/android/issues/detail?id=21266
        // Possibly also related to CSS Transforms (bug 21305)
        // http://code.google.com/p/android/issues/detail?id=21305
        mainWebView = (WebView) findViewById(R.id.mainWebView);
        mainWebView.getSettings().setJavaScriptEnabled(true);
        mainWebView.setWebViewClient(new WebViewClient() {
        	public boolean shouldOverrideUrlLoading(WebView view, String url) { 
        		if(!savedState)
        			view.loadUrl(url);
        		return true;
        	}
        	
        	public void onPageStarted(WebView view, String url, Bitmap favicon) {
        		super.onPageStarted(view, url, favicon);          	
        	}
        	
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
        				
        				// disambiguations appear directly as path string
        				String path = fullURL.getPath();
        				
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
        					}
        					else if(path != null && !path.equals("/")){
            					String text = path.substring(path.lastIndexOf("/") + 1).replace("_", " ");
            					setSearchBarText(text);
            				}
        					else {
        						setSearchBarText(url);
        					}
        				}
        				else {
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
        		
        		if(mDuckDuckGoContainer.feedItemLoading){
        			mainWebView.clearHistory();
        			mDuckDuckGoContainer.feedItemLoading = false;
        		}
        		
				searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);

        	}
        });
                
        mainWebView.setWebChromeClient(new WebChromeClient(){
        	@Override
        	public void onProgressChanged(WebView view, int newProgress) {
        		super.onProgressChanged(view, newProgress);
        		
        		if(newProgress == 100){
        			searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
        		}
        		else {
	        		mDuckDuckGoContainer.progressDrawable.setLevel(newProgress*100);
	        		searchField.setBackgroundDrawable(mDuckDuckGoContainer.progressDrawable);
        		}

        	}
        });
        
        feedProgressBar = (ProgressBar) findViewById(R.id.feedLoadingProgress);
        
        prefLayout = (LinearLayout) findViewById(R.id.prefLayout);
        
    }
	
	private View buildLabel(String text) {
		    TextView result=new TextView(this);
		    result.setText(text);
		    return(result);
	}
	
	private View buildFromResource(int resId){
		return getLayoutInflater().inflate(resId, null);
	}
	
	private void clearSearchBar() {
		searchField.setText("");
    	searchField.setCompoundDrawables(null, null, null, null);
		searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
	}
	
	private void clearBrowserState() {
		mainWebView.stopLoading();
		mainWebView.clearHistory();
		mainWebView.clearView();
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
		
		clearSearchBar();
		clearBrowserState();
		
        if(DDGControlVar.START_SCREEN == SCREEN.SCR_NEWS_FEED){
        	// show recent queries on slide-out menu
//        	lMainAdapter.remove(getString(R.string.LeftRecentQueries));
        	leftMainLayout.findViewById(R.id.LeftRecentTextView).setVisibility(View.VISIBLE);
        	leftRecentView.setVisibility(View.VISIBLE);
        	
        	displayNewsFeed();
        }
        else if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH){
        	// hide recent queries from slide-out menu
//        	lMainAdapter.remove(getString(R.string.LeftRecentQueries));
//        	lMainAdapter.insert(getString(R.string.LeftRecentQueries), 0);
        	leftMainLayout.findViewById(R.id.LeftRecentTextView).setVisibility(View.GONE);
        	leftRecentView.setVisibility(View.GONE);
        	
        	displayRecentSearch();
        }
	}
	
	@Override
	public void onResume() {
		super.onResume();

		mDuckDuckGoContainer.sourceIconTask = new DownloadSourceIconTask(DDGApplication.getImageCache());
		mDuckDuckGoContainer.sourceIconTask.execute();
		
		if(mDuckDuckGoContainer.webviewShowing){
				feedView.setVisibility(View.GONE);
				mainWebView.setVisibility(View.VISIBLE);
		}	
		else if(!mDuckDuckGoContainer.prefShowing){
			switchScreens();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mDuckDuckGoContainer.mainFeedTask != null) {
			mDuckDuckGoContainer.mainFeedTask.cancel(false);
			mDuckDuckGoContainer.mainFeedTask = null;
		}
		
		String combined = "";
		for(String id : DDGControlVar.readArticles){
			combined += id + "-";
		}
		if(combined.length() != 0){
			Editor editor = sharedPreferences.edit();
			editor.putString("readarticles", combined);
			editor.commit();
		}
	}
	
	@Override
	protected void onStop() {
		String combined = "";
		for(String id : DDGControlVar.readArticles){
			combined += id + "-";
		}
		if(combined.length() != 0){
			Editor editor = sharedPreferences.edit();
			editor.putString("readarticles", combined);
			editor.commit();
		}
		
		super.onStop();
	}
	
	@Override
	public void onBackPressed() {
		if (mDuckDuckGoContainer.webviewShowing) {
			if (mainWebView.canGoBack()) {
				mainWebView.goBack();
			} else {				
				switchScreens();
				
				mainWebView.setVisibility(View.GONE);
				prefLayout.setVisibility(View.GONE);
				mDuckDuckGoContainer.webviewShowing = false;

			}
		}
		else if(mDuckDuckGoContainer.prefShowing){
			prefLayout.setVisibility(View.GONE);
			mDuckDuckGoContainer.prefShowing = false;
						
			switchScreens();
		}
		else {
			super.onBackPressed();
			DDGControlVar.hasUpdatedFeed = false;
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
			
			savedState = false;
			
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

			
			// definitely loading something in the browser - set home icon before we go
			DDGControlVar.homeScreenShowing = false;
			homeSettingsButton.setImageResource(R.drawable.home_button);
			
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
		if (!mDuckDuckGoContainer.webviewShowing) {
			feedView.setVisibility(View.GONE);
			recentSearchView.setVisibility(View.GONE);
			mainWebView.setVisibility(View.VISIBLE);
			mDuckDuckGoContainer.webviewShowing = true;
			mDuckDuckGoContainer.prefShowing = false;
			prefLayout.setVisibility(View.GONE);
		}
		
		if(!savedState){
			if(DDGControlVar.regionString == "wt-wt"){	// default
				mainWebView.loadUrl(DDGConstants.SEARCH_URL + URLEncoder.encode(term));
			}
			else {
				mainWebView.loadUrl(DDGConstants.SEARCH_URL + URLEncoder.encode(term) + "&kl=" + URLEncoder.encode(DDGControlVar.regionString));
			}
		}
		
		// save recent query if "record history" is enabled
		if(sharedPreferences.getBoolean("recordHistoryPref", false)){
			if(!mDuckDuckGoContainer.recentSearchSet.contains(term)){
				mDuckDuckGoContainer.recentSearchSet.add(term);
				mDuckDuckGoContainer.recentSearchAdapter.add(term);
				
				Set<String> recentSearchSet = DDGUtils.loadSet(sharedPreferences, "recentsearch");
				recentSearchSet.add(term);
				DDGUtils.saveSet(sharedPreferences, recentSearchSet, "recentsearch");
			}
		}
	}
	
	public void clearRecentSearch() {
		mDuckDuckGoContainer.recentSearchSet.clear();
		mDuckDuckGoContainer.recentSearchAdapter.clear();
		recentSearchView.invalidate();
	}
	
	public void showWebUrl(String url) {
		if (!mDuckDuckGoContainer.webviewShowing) {
			feedView.setVisibility(View.GONE);
			mainWebView.setVisibility(View.VISIBLE);
			mDuckDuckGoContainer.webviewShowing = true;
			mDuckDuckGoContainer.prefShowing = false;
			prefLayout.setVisibility(View.GONE);
		}
		
		if(!savedState)
			mainWebView.loadUrl(url);
	}

	public void onFeedRetrieved(List<FeedObject> feed) {
		feedProgressBar.setVisibility(View.GONE);
		mDuckDuckGoContainer.feedAdapter.scrolling = false;
		mDuckDuckGoContainer.feedAdapter.setList(feed);
		mDuckDuckGoContainer.feedAdapter.notifyDataSetChanged();
		DDGControlVar.hasUpdatedFeed = true;
	}
	
	public void onFeedRetrievalFailed() {
		//If the mainFeedTask is null, we are currently paused
		//Otherwise, we can try again
		if (mDuckDuckGoContainer.mainFeedTask != null) {
			mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(this);
			mDuckDuckGoContainer.mainFeedTask.execute();
		}
	}
	
	@TargetApi(11)
	public void showPrefFragment(){
//      	getFragmentManager().beginTransaction().replace(R.id.prefFragment,
//                new DDGPreferenceFragment()).commit();    	
      	
        FragmentManager fm = getFragmentManager();

        // Check to see if we have retained the worker fragment.
        DDGPreferenceFragment mWorkFragment = (DDGPreferenceFragment)fm.findFragmentById(R.id.prefFragment);

        // If not retained (or first time running), we need to create it.
        if (mWorkFragment == null) {
            mWorkFragment = new DDGPreferenceFragment();
            mWorkFragment.setRetainInstance(true);
            fm.beginTransaction().replace(R.id.prefFragment,
                    new DDGPreferenceFragment()).commit();  
        }
        
        displayPreferences();
	}
	
	public void displayPreferences(){
		feedView.setVisibility(View.GONE);
		mainWebView.setVisibility(View.GONE);
		recentSearchView.setVisibility(View.GONE);
		prefLayout.setVisibility(View.VISIBLE);
		mDuckDuckGoContainer.prefShowing = true;
				
		searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
		mDuckDuckGoContainer.webviewShowing = false;
	}
	
	public void displayNewsFeed(){
		recentSearchView.setVisibility(View.GONE);
		mainWebView.setVisibility(View.GONE);
		prefLayout.setVisibility(View.GONE);
    	feedView.setVisibility(View.VISIBLE);
    	keepFeedUpdated();
    	mDuckDuckGoContainer.webviewShowing = false;
    	
    	clearBrowserState();
    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_NEWS_FEED){
    		DDGControlVar.homeScreenShowing = true;
    		homeSettingsButton.setImageResource(R.drawable.menu_button);
    	}
	}
	
	public void displayRecentSearch(){
		mainWebView.setVisibility(View.GONE);
		prefLayout.setVisibility(View.GONE);
		feedView.setVisibility(View.GONE);
    	feedProgressBar.setVisibility(View.GONE);
    	recentSearchView.setVisibility(View.VISIBLE);
    	mDuckDuckGoContainer.webviewShowing = false;
    	
    	clearBrowserState();
    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH){
    		DDGControlVar.homeScreenShowing = true;
    		homeSettingsButton.setImageResource(R.drawable.menu_button);
    	}
	}
	
	public void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public void onClick(View v) {
		if (v.equals(homeSettingsButton)) {			
			hideKeyboard(searchField);
			
			if(DDGControlVar.homeScreenShowing){
				fan.showMenu();
			}
			else {
				// going home
				switchScreens();
			}
		}
		else if(v.equals(leftHomeTextView)){
			fan.showMenu();
			
			// show stories
			mDuckDuckGoContainer.prefShowing = false;
			
			if (mDuckDuckGoContainer.webviewShowing) {

				//We are going home!
				mainWebView.clearHistory();
				mainWebView.clearView();
				clearSearchBar();
				mDuckDuckGoContainer.webviewShowing = false;					
			}
			
			if(DDGControlVar.START_SCREEN != DDGControlVar.PREV_START_SCREEN){
				switchScreens();
			}
		}
		else if(v.equals(leftSavedTextView)){
			fan.showMenu();		
		}
		else if(v.equals(leftSettingsTextView)){
			fan.showMenu();
			
			if(!mDuckDuckGoContainer.prefShowing){
				
				DDGControlVar.PREV_START_SCREEN = DDGControlVar.START_SCREEN; 

				if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
			        Intent intent = new Intent(getBaseContext(), Preferences.class);
			        startActivityForResult(intent, PREFERENCES_RESULT);
				}
				else {
					showPrefFragment();
				}
			
			}
		}
		
		// action for recent queries, leave as comment here		
//		else if(v.equals(leftRecentQueriesTextView)){
//			mDuckDuckGoContainer.prefShowing = false;
//			displayRecentSearch();
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PREFERENCES_RESULT){
			if (resultCode == RESULT_OK) {
				boolean clearedHistory = data.getBooleanExtra("hasClearedHistory",false);
				if(clearedHistory){
					clearRecentSearch();
				}
			}
		}
	}
	
    @Override
    public Object onRetainNonConfigurationInstance() {
    	// return page container, holding all non-view data
    	return mDuckDuckGoContainer;
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Save the state of the WebView
		mainWebView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);

		// Restore the state of the WebView
		mainWebView.restoreState(savedInstanceState);
	}
	
	/**
	 * Refresh feed if it's not marked as updated
	 */
	private void keepFeedUpdated()
	{
		if (!DDGControlVar.hasUpdatedFeed) {
			feedProgressBar.setVisibility(View.VISIBLE);
			
			mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(this);
			mDuckDuckGoContainer.mainFeedTask.execute();
		}
	}

}