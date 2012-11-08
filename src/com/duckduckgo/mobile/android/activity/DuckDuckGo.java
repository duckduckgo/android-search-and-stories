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
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.MainFeedAdapter;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.Holder;
import com.duckduckgo.mobile.android.listener.FeedListener;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.SuggestObject;
import com.duckduckgo.mobile.android.tasks.DownloadSourceIconTask;
import com.duckduckgo.mobile.android.tasks.MainFeedTask;
import com.duckduckgo.mobile.android.tasks.SavedFeedTask;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.Item;
import com.duckduckgo.mobile.android.util.Item.ItemType;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SuggestType;
import com.duckduckgo.mobile.android.views.DDGWebView;
import com.duckduckgo.mobile.android.views.FanView;
import com.duckduckgo.mobile.android.views.MainFeedListView;
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
	
	private FanView fan;
	
	private RecentSearchListView recentSearchView = null;
	
	private DDGWebView mainWebView = null;
	private ImageButton homeSettingsButton = null;
	private ImageButton shareButton = null;
	private LinearLayout prefLayout = null;
	private LinearLayout leftMainLayout = null;
	
	// event section is a local notification bar at the top, below searchbar
	private LinearLayout eventLayout = null;
	
	private TextView leftHomeTextView = null;
	private TextView leftSavedTextView = null;
	private TextView leftSettingsTextView = null;
	
	private TextView sourceTextView = null;
	
	private SharedPreferences sharedPreferences;
		
	private boolean savedState = false;
	
	ArrayList<String> listContent;
	
	private final int PREFERENCES_RESULT = 0;
	
	private final int CONTEXT_ITEM_SAVE = 0;
	private final int CONTEXT_ITEM_UNSAVE = 1;
	private final int CONTEXT_ITEM_SHARE = 2;
	
	Item[] shareDialogItems;
	FeedObject currentFeedObject = null;
	boolean isFeedObject = false;
			
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
        
        ArrayList<Item> dialogItems = new ArrayList<Item>();
        dialogItems.add(new Item(getResources().getString(R.string.Share), android.R.drawable.ic_menu_share, ItemType.SHARE));
        dialogItems.add(new Item(getResources().getString(R.string.Save), android.R.drawable.ic_menu_save, ItemType.SAVE));

        shareDialogItems = (Item []) dialogItems.toArray(new Item[dialogItems.size()]);
        
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
						
						eventLayout.setVisibility(View.GONE);
						sourceTextView.setText("");
						
						DDGControlVar.hasUpdatedFeed = false;
						mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(DuckDuckGo.this);
						mDuckDuckGoContainer.mainFeedTask.execute();						
					}
					else {
					
						String sourceType = ((AsyncImageView) v).getType(); 
						DDGControlVar.targetSource = sourceType;
						
						eventLayout.setVisibility(View.VISIBLE);
						sourceTextView.setText(DDGControlVar.simpleSourceMap.get(sourceType));
						
						DDGControlVar.hasUpdatedFeed = false;
						mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(DuckDuckGo.this);
						mDuckDuckGoContainer.mainFeedTask.execute();
					}
					
				}
			};
			
    		mDuckDuckGoContainer.feedAdapter = new MainFeedAdapter(this, sourceClickListener);
    		
    		mDuckDuckGoContainer.mainFeedTask = null;
    		mDuckDuckGoContainer.sourceIconTask = null;    		
    		
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
        
        if(mDuckDuckGoContainer.webviewShowing) {
        	homeSettingsButton.setImageResource(R.drawable.home_button);
        }
        
        shareButton = (ImageButton) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);
        
        // adjust visibility of share button after screen rotation
        if(mDuckDuckGoContainer.webviewShowing) {
        	shareButton.setVisibility(View.VISIBLE);
        }
        
        searchField = (AutoCompleteTextView) findViewById(R.id.searchEditText);
        searchField.setAdapter(new AutoCompleteResultsAdapter(this));
        searchField.setOnEditorActionListener(this);
        
        searchField.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// close left nav if it's open
				if(fan.isOpen()){
					fan.showMenu();
				}				
			}
		});

        searchField.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(!sharedPreferences.getBoolean("modifyQueryPref", false)){
					//Hide the keyboard and perform a search
					hideKeyboard(searchField);
					searchField.dismissDropDown();
					
					SuggestObject suggestObject = (SuggestObject)parent.getAdapter().getItem(position);
					SuggestType suggestType = suggestObject.getType();
					if (suggestObject != null) {
						if(suggestType == SuggestType.TEXT) {
							String text = suggestObject.getPhrase();
							text.trim();
							searchOrGoToUrl(text);
						}
						else if(suggestType == SuggestType.APP) {
							DDGUtils.launchApp(DuckDuckGo.this, suggestObject.getSnippet());
						}
					}		
				}
				
			}
		});

        // This makes a little (X) to clear the search bar.
        final Drawable x = getResources().getDrawable(R.drawable.stop);
        x.setBounds(0, 0, (int)Math.floor(x.getIntrinsicWidth()/1.5), (int)Math.floor(x.getIntrinsicHeight()/1.5));
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
        
        eventLayout = (LinearLayout) findViewById(R.id.eventLayout);
        sourceTextView = (TextView) findViewById(R.id.sourceText);
        
        // This makes a little (X) to cancel source filtering.
        final Drawable xc = getResources().getDrawable(R.drawable.stop);
        xc.setBounds(0, 0, xc.getIntrinsicWidth(), xc.getIntrinsicHeight());
        sourceTextView.setCompoundDrawables(null, null, xc, null);

        sourceTextView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (sourceTextView.getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > sourceTextView.getWidth() - sourceTextView.getPaddingRight() - xc.getIntrinsicWidth()) {
                	// cancel filtering
                	DDGControlVar.targetSource = null;
					
					eventLayout.setVisibility(View.GONE);
					sourceTextView.setText("");
					
					DDGControlVar.hasUpdatedFeed = false;
					mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(DuckDuckGo.this);
					mDuckDuckGoContainer.mainFeedTask.execute();
                }
                return false;
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
        registerForContextMenu(feedView);
        feedView.setOnMainFeedItemSelectedListener(new OnMainFeedItemSelectedListener() {
			public void onMainFeedItemSelected(FeedObject feedObject) {
				// close left nav if it's open
				if(fan.isOpen()){
					fan.showMenu();
				}
				
				// keep a reference, so that we can reuse details while saving
				currentFeedObject = feedObject;
				isFeedObject = true;
				
				String url = feedObject.getUrl();
				if (url != null) {
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
//        feedView.setOnMainFeedItemLongClickListener(new OnMainFeedItemLongClickListener() {
//			public void onMainFeedItemLongClick(FeedObject feedObject) {
//				Intent sendIntent = new Intent();
//				sendIntent.setAction(Intent.ACTION_SEND);
//				sendIntent.putExtra(Intent.EXTRA_TEXT, "WatrCoolr URL: "+feedObject.getUrl());
//				sendIntent.putExtra(Intent.EXTRA_SUBJECT, feedObject.getTitle());
//				sendIntent.setType("text/plain");
//				startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
//			}
//        });
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
        mainWebView = (DDGWebView) findViewById(R.id.mainWebView);
        mainWebView.getSettings().setJavaScriptEnabled(true);
        mainWebView.setWebViewClient(new WebViewClient() {
        	public boolean shouldOverrideUrlLoading(WebView view, String url) { 
        		if(!savedState) {
        			// handle mailto: and tel: links with native apps
        			if(url.startsWith("mailto:")){
                        MailTo mt = MailTo.parse(url);
                        Intent i = DDGUtils.newEmailIntent(mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                        startActivity(i);
                        return true;
                    }
        			else if(url.startsWith("tel:")){
                        Intent i = DDGUtils.newTelIntent(url);
                        startActivity(i);
                        return true;
        			}
        			else {	
        				view.loadUrl(url);
        			}        			
        		}
        		return true;
        	}
        	
        	public void onPageStarted(WebView view, String url, Bitmap favicon) {
        		super.onPageStarted(view, url, favicon);        		

        		// Omnibar like behavior.
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
        	}
        	
        	public void onPageFinished (WebView view, String url) {
        		
        		if(!mDuckDuckGoContainer.allowInHistory) {
        			mainWebView.clearHistory();
        		}
        		
        		if(mainWebView.getVisibility() != View.VISIBLE) {
        			return;
        		}
        		
				searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
        	}
        });
                
        mainWebView.setWebChromeClient(new WebChromeClient(){
        	@Override
        	public void onProgressChanged(WebView view, int newProgress) {
        		super.onProgressChanged(view, newProgress);
        		
        		if(!mDuckDuckGoContainer.allowInHistory) {
        			mainWebView.clearHistory();
        		}
        		
        		if(mainWebView.getVisibility() != View.VISIBLE) {
        			return;
        		}
        		
        		if(newProgress == 100){
        			searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
        		}
        		else {
	        		mDuckDuckGoContainer.progressDrawable.setLevel(newProgress*100);
	        		searchField.setBackgroundDrawable(mDuckDuckGoContainer.progressDrawable);
        		}

        	}
        });
        
        mainWebView.setExtraTouchListener(new View.OnTouchListener() {

        	public boolean onTouch(View v, MotionEvent event) {

        		HitTestResult hr = ((DDGWebView) v).getHitTestResult();
        		if(hr != null && hr.getExtra() != null) {            	
        			mDuckDuckGoContainer.allowInHistory = true; 
        			isFeedObject = false;
        			Log.i(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type=" + hr.getType());
        		}

        		return false;
        	}
        });

        mainWebView.setOnLongClickListener(new OnLongClickListener() {

        	@Override
        	public boolean onLongClick(View v) {
        		HitTestResult hr = ((DDGWebView) v).getHitTestResult();
        		if(hr != null && hr.getExtra() != null) {
        			Log.i(TAG, "LONG getExtra = "+ hr.getExtra() + "\t\t Type=" + hr.getType());
        			final String touchedUrl = hr.getExtra();
        			
        			AlertDialog dialog = new AlertDialog.Builder(DuckDuckGo.this).create();
        	        dialog.setTitle(getResources().getString(R.string.OpenInExternalBrowser));
        	        dialog.setMessage(getResources().getString(R.string.ConfirmExternalBrowser));
        	        dialog.setCancelable(false);
        	        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
        	            public void onClick(DialogInterface dialog, int buttonId) {
        	            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(touchedUrl));
        	            	startActivity(browserIntent);
        	            }
        	        });
        	        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
        	            public void onClick(DialogInterface dialog, int buttonId) {
        	                dialog.dismiss();
        	            }
        	        });
        	        dialog.setIcon(android.R.drawable.ic_dialog_info);
        	        dialog.show();
        		}

        		return false;
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
		mDuckDuckGoContainer.allowInHistory = false;
//		mainWebView.clearHistory();
//		mainWebView.clearView();
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

		mDuckDuckGoContainer.sourceIconTask = new DownloadSourceIconTask(getApplicationContext(), DDGApplication.getImageCache());
		mDuckDuckGoContainer.sourceIconTask.execute();
		
		if(mDuckDuckGoContainer.webviewShowing){
				feedView.setVisibility(View.GONE);
				eventLayout.setVisibility(View.GONE);
				mainWebView.setVisibility(View.VISIBLE);
		}	
		else if(!mDuckDuckGoContainer.prefShowing){
			switchScreens();
		}
		
		if(getIntent().getBooleanExtra("widget", false)) {
			searchField.requestFocus();
			showKeyboard(searchField);
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
			}
			else if(mDuckDuckGoContainer.savedFeedShowing) {
				clearSearchBar();
				clearBrowserState();
				displaySavedFeed();
			}
			else {
				// going home
				switchScreens();
			}
		}
		else if(mDuckDuckGoContainer.prefShowing){
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
		displayWebView();
		
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
		displayWebView();
		
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

		// Do not retry for SavedFeedTask, DB reply should be usable, when good or bad
		if (mDuckDuckGoContainer.savedFeedShowing && mDuckDuckGoContainer.savedFeedTask != null) {
			onFeedRetrieved(new ArrayList<FeedObject>());
			Toast.makeText(this, R.string.SavedFeedEmpty, Toast.LENGTH_LONG).show();
		}
		
		//If the mainFeedTask is null, we are currently paused
		//Otherwise, we can try again
		else if (!mDuckDuckGoContainer.savedFeedShowing && mDuckDuckGoContainer.mainFeedTask != null) {
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
            mWorkFragment.setCustomPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					// close left nav if it's open
					if(fan.isOpen()){
						fan.showMenu();
					}	
					return false;
				}
			});
            fm.beginTransaction().replace(R.id.prefFragment,
                    mWorkFragment).commit();  
        }
        
        displayPreferences();
	}
	
	public void displayPreferences(){
		feedView.setVisibility(View.GONE);
		mainWebView.setVisibility(View.GONE);
		shareButton.setVisibility(View.GONE);
		recentSearchView.setVisibility(View.GONE);
		prefLayout.setVisibility(View.VISIBLE);
		eventLayout.setVisibility(View.GONE);
		mDuckDuckGoContainer.prefShowing = true;
				
		searchField.setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
		mDuckDuckGoContainer.webviewShowing = false;
	}
	
	public void displayNewsFeed(){
		if(mDuckDuckGoContainer.savedFeedShowing) {
			mDuckDuckGoContainer.savedFeedShowing = false;
			DDGControlVar.hasUpdatedFeed = false;
		}
		recentSearchView.setVisibility(View.GONE);
		mainWebView.setVisibility(View.GONE);
		shareButton.setVisibility(View.GONE);
		prefLayout.setVisibility(View.GONE);
    	feedView.setVisibility(View.VISIBLE);
    	eventLayout.setVisibility(View.GONE);
    	keepFeedUpdated();
    	mDuckDuckGoContainer.webviewShowing = false;
		mDuckDuckGoContainer.prefShowing = false;
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_NEWS_FEED){
    		DDGControlVar.homeScreenShowing = true;
    		homeSettingsButton.setImageResource(R.drawable.menu_button);
    	}
	}
	
	public void displaySavedFeed(){
		mDuckDuckGoContainer.savedFeedShowing = true;
		DDGControlVar.hasUpdatedFeed = false;
		recentSearchView.setVisibility(View.GONE);
		mainWebView.setVisibility(View.GONE);
		shareButton.setVisibility(View.GONE);
		prefLayout.setVisibility(View.GONE);
    	feedView.setVisibility(View.VISIBLE);
    	eventLayout.setVisibility(View.GONE);
    	keepFeedUpdated();
    	mDuckDuckGoContainer.webviewShowing = false;
		mDuckDuckGoContainer.prefShowing = false;
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_NEWS_FEED){
    		DDGControlVar.homeScreenShowing = true;
    		homeSettingsButton.setImageResource(R.drawable.menu_button);
    	}
	}
	
	public void displayRecentSearch(){
		mainWebView.setVisibility(View.GONE);
		shareButton.setVisibility(View.GONE);
		prefLayout.setVisibility(View.GONE);
		feedView.setVisibility(View.GONE);
    	feedProgressBar.setVisibility(View.GONE);
    	recentSearchView.setVisibility(View.VISIBLE);
    	eventLayout.setVisibility(View.GONE);
    	mDuckDuckGoContainer.webviewShowing = false;
		mDuckDuckGoContainer.prefShowing = false;
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH){
    		DDGControlVar.homeScreenShowing = true;
    		homeSettingsButton.setImageResource(R.drawable.menu_button);
    	}
	}
	
	public void displayWebView() {
		if (!mDuckDuckGoContainer.webviewShowing) {
			feedView.setVisibility(View.GONE);
			eventLayout.setVisibility(View.GONE);
			recentSearchView.setVisibility(View.GONE);
			
			shareButton.setVisibility(View.VISIBLE);
			
			mainWebView.setVisibility(View.VISIBLE);
			mDuckDuckGoContainer.webviewShowing = true;
			mDuckDuckGoContainer.prefShowing = false;
			prefLayout.setVisibility(View.GONE);			
		}
	}
	
	public void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public void showKeyboard(View view) {
		// InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
		else if (v.equals(shareButton)) {			
			hideKeyboard(searchField);
			
			final ListAdapter adapter = new ArrayAdapter<Item>(
					this,
					android.R.layout.select_dialog_item,
					android.R.id.text1,
					shareDialogItems){
				public View getView(int position, View convertView, android.view.ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					TextView tv = (TextView)v.findViewById(android.R.id.text1);
					tv.setCompoundDrawablesWithIntrinsicBounds(shareDialogItems[position].icon, 0, 0, 0);

					//Add 10dp margin between image and text (support various screen densities)
					int dp10 = (int) (10 * getResources().getDisplayMetrics().density + 0.5f);
					tv.setCompoundDrawablePadding(dp10);

					return v;
				}
			};

			AlertDialog.Builder ab=new AlertDialog.Builder(DuckDuckGo.this);
			ab.setTitle(getResources().getString(R.string.MoreMenuTitle));
			ab.setAdapter(adapter, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					Item it = ((Item) adapter.getItem(item));
					if(it.type == Item.ItemType.SHARE) {
						String pageTitle = mainWebView.getTitle();
						String pageUrl = mainWebView.getUrl();
						
						DDGUtils.shareWebPage(DuckDuckGo.this, pageTitle, pageUrl);
					}
					else if(it.type == Item.ItemType.SAVE) {
						if(isFeedObject) {
							// browsing a feed item, we can save it as is
							DDGApplication.getDB().insert(currentFeedObject);
						}
						else {
							String pageTitle = mainWebView.getTitle();
							String pageUrl = mainWebView.getUrl();
//							Log.v(TAG,"insert regular page: " + pageTitle + " " + pageUrl);
							DDGApplication.getDB().insert(new FeedObject(pageTitle, pageUrl));
						}
					}
				}
			});
			ab.show();
		}
		else if(v.equals(leftHomeTextView)){
			fan.showMenu();
			
			mDuckDuckGoContainer.prefShowing = false;
			
			if (mDuckDuckGoContainer.webviewShowing) {

				//We are going home!
				mainWebView.clearHistory();
				mainWebView.clearView();
				clearSearchBar();
				mDuckDuckGoContainer.webviewShowing = false;					
			}
			
			switchScreens();
		}
		else if(v.equals(leftSavedTextView)){
			fan.showMenu();		
			
			displaySavedFeed();
		}
		else if(v.equals(leftSettingsTextView)){
			fan.showMenu();
			
			if(!mDuckDuckGoContainer.prefShowing){
				
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
			
			if(mDuckDuckGoContainer.savedFeedShowing) {
				mDuckDuckGoContainer.savedFeedTask = new SavedFeedTask(this);
				mDuckDuckGoContainer.savedFeedTask.execute();
			}
			else {
				mDuckDuckGoContainer.mainFeedTask = new MainFeedTask(this);
				mDuckDuckGoContainer.mainFeedTask.execute();
			}
		}
	}

    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(getResources().getString(R.string.MainFeedContextTitle));
		if(mDuckDuckGoContainer.savedFeedShowing) {
			menu.add(0, CONTEXT_ITEM_UNSAVE, 0, getResources().getString(R.string.Unsave));
		}
		else {
			menu.add(0, CONTEXT_ITEM_SAVE, 0, getResources().getString(R.string.Save));
		}
		menu.add(0, CONTEXT_ITEM_SHARE, 1, getResources().getString(R.string.Share));
	}
    
	@Override
	public boolean onContextItemSelected(MenuItem item) {	   	
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		long itemId = item.getItemId();
		int itemPosition = info.position;
		
		FeedObject feedObject = (FeedObject) feedView.getItemAtPosition(itemPosition);
    	
    	if(itemId==CONTEXT_ITEM_SAVE){
    		DDGApplication.getDB().insert(feedObject);
       	}
    	else if(itemId==CONTEXT_ITEM_UNSAVE){
    		DDGApplication.getDB().deleteById(feedObject.getId());
    		mDuckDuckGoContainer.feedAdapter.remove(feedObject);
       	}
    	else if(itemId==CONTEXT_ITEM_SHARE){
    		DDGUtils.shareWebPage(DuckDuckGo.this, feedObject.getTitle(), feedObject.getUrl());
			return true;
       	}
    	
    	return false;
	}
    
}
