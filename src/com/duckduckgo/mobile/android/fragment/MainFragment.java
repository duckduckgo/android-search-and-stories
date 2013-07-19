package com.duckduckgo.mobile.android.fragment;


import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.KeyboardService;
import com.duckduckgo.mobile.android.activity.Preferences;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.AddWelcomeScreenEvent;
import com.duckduckgo.mobile.android.events.CleanFeedDownloadsEvent;
import com.duckduckgo.mobile.android.events.DisplayScreenEvent;
import com.duckduckgo.mobile.android.events.HideKeyboardEvent;
import com.duckduckgo.mobile.android.events.HistoryItemSelectedEvent;
import com.duckduckgo.mobile.android.events.HomeButtonClickEvent;
import com.duckduckgo.mobile.android.events.RecentHeaderClickEvent;
import com.duckduckgo.mobile.android.events.ReloadEvent;
import com.duckduckgo.mobile.android.events.ResetScreenStateEvent;
import com.duckduckgo.mobile.android.events.SearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.SearchWebTermEvent;
import com.duckduckgo.mobile.android.events.ShareButtonClickEvent;
import com.duckduckgo.mobile.android.events.UpdateVisibilityEvent;
import com.duckduckgo.mobile.android.events.WebViewResetEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedUpdateRequestEvent;
import com.duckduckgo.mobile.android.events.fontEvents.FontSizeCancelEvent;
import com.duckduckgo.mobile.android.events.fontEvents.FontSizeChangeEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftHomeButtonClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftSavedButtonClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftSettingsButtonClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftStoriesButtonClickEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.RecentSearchPasteEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.SavedSearchPasteEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.SuggestionPasteEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemSelectedEvent;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarClickEvent;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarProgressDrawableEvent;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarSearchDrawableEvent;
import com.duckduckgo.mobile.android.events.searchbarEvents.SearchBarSetTextEvent;
import com.duckduckgo.mobile.android.objects.SuggestObject;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.SuggestType;
import com.duckduckgo.mobile.android.views.SeekBarHint;
import com.duckduckgo.mobile.android.views.autocomplete.BackButtonPressedEventListener;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;
import com.duckduckgo.mobile.android.widgets.BangButtonExplanationPopup;
import com.duckduckgo.mobile.android.widgets.SafeViewFlipper;
import com.squareup.otto.Subscribe;

public class MainFragment extends Fragment {
	
	private View contentView;
	
	private KeyboardService keyboardService;
	
	public Drawable progressDrawable, searchFieldDrawable;
	public Drawable stopDrawable;
			
	public AutoCompleteResultsAdapter acAdapter = null;
	private DDGAutoCompleteTextView searchField = null;
	
	private SafeViewFlipper viewFlipper = null;
	
	private ImageButton homeSettingsButton = null;
	private ImageButton bangButton = null;
	private ImageButton shareButton = null;
	
	// font scaling
	private LinearLayout fontSizeLayout = null;	
						
	private final int PREFERENCES_RESULT = 0;
			
	// keep prev progress in font seek bar, to make incremental changes available
	SeekBarHint fontSizeSeekBar;
	
	private boolean shouldShowBangButtonExplanation;

	private BangButtonExplanationPopup bangButtonExplanationPopup;
		
	public DDGAutoCompleteTextView getSearchField() {
		return searchField;
	}
	
    // Assist action is better known as Google Now gesture
	private void checkForAssistAction() {
		if (getActivity().getIntent() != null 
				&& getActivity().getIntent().getAction() != null 
				&& getActivity().getIntent().getAction().equals(Intent.ACTION_ASSIST)) {
            keyboardService.toggleKeyboard(getSearchField());
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initialise();
	}
	
	private void initialise() {
        keyboardService = new KeyboardService(getActivity());
        
        if(!PreferencesManager.isWelcomeShown()) {
            BusProvider.getInstance().post(new AddWelcomeScreenEvent());
            shouldShowBangButtonExplanation = true;
    	}
        
        
        stopDrawable = getResources().getDrawable(R.drawable.stop);
//    	reloadDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.reload);
        progressDrawable = getResources().getDrawable(R.drawable.page_progress);
        searchFieldDrawable = getResources().getDrawable(R.drawable.searchfield);
        searchFieldDrawable.setAlpha(150);
        
        acAdapter = new AutoCompleteResultsAdapter(getActivity());
        
        
        viewFlipper = (SafeViewFlipper) contentView.findViewById(R.id.ViewFlipperMain);    	    	

        
        homeSettingsButton = (ImageButton) contentView.findViewById(R.id.settingsButton);
        homeSettingsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BusProvider.getInstance().post(new HomeButtonClickEvent());
			}
		});
        bangButton = (ImageButton)contentView.findViewById(R.id.bangButton);
        bangButton.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			getSearchField().addBang();				
    		}
    	});
        
        if(isWebViewShowing()) {
        	homeSettingsButton.setImageResource(R.drawable.home_button);
        }
        
        shareButton = (ImageButton) contentView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BusProvider.getInstance().post(new ShareButtonClickEvent());
			}
		});
        
        // adjust visibility of share button after screen rotation
        if(isWebViewShowing()) {
        	shareButton.setVisibility(View.VISIBLE);
        }
        
        searchField = (DDGAutoCompleteTextView) contentView.findViewById(R.id.searchEditText);
        getSearchField().setAdapter(acAdapter);
        getSearchField().setOnEditorActionListener(new OnEditorActionListener() {
    		@Override
    		public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
    			if(textView == getSearchField()) {
                    keyboardService.hideKeyboard(getSearchField());
    				getSearchField().dismissDropDown();
    				BusProvider.getInstance().post(new SearchOrGoToUrlEvent(getSearchField().getTrimmedText()));
    			}
    			return false;
    		}
    	});
        
        getSearchField().setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			BusProvider.getInstance().post(new SearchBarClickEvent());
    		}
    	});
        getSearchField().setOnFocusChangeListener(new OnFocusChangeListener() {
    		@Override
    		public void onFocusChange(View v, boolean hasFocus) {
    			showBangButton(hasFocus);
    		}
    	});
        
        getSearchField().setOnBackButtonPressedEventListener(new BackButtonPressedEventListener() {
    		@Override
    		public void onBackButtonPressed() {
    			if(getSearchField().isPopupShowing()){
                    getSearchField().dismissDropDown();
    			}
                showBangButton(false);
    		}
        });

        getSearchField().setOnItemClickListener(new OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			if(PreferencesManager.getDirectQuery()){
    				//Hide the keyboard and perform a search
    				getSearchField().dismissDropDown();
    				
    				SuggestObject suggestObject = acAdapter.getItem(position);
    				SuggestType suggestType = suggestObject.getType();
    				if (suggestObject != null) {
    					if(suggestType == SuggestType.TEXT) {
    						String text = suggestObject.getPhrase().trim();
    						if(suggestObject.hasOnlyBangQuery()){
    							getSearchField().addTextWithTrailingSpace(suggestObject.getPhrase());
    						}else{
                                keyboardService.hideKeyboard(getSearchField());
    							BusProvider.getInstance().post(new SearchOrGoToUrlEvent(text));
    						}
    					}
    					else if(suggestType == SuggestType.APP) {
    						DDGUtils.launchApp(getActivity(), suggestObject.getSnippet());
    					}
    				}
    			}
    		}
    	});
        
        
        
        // This makes a little (X) to clear the search bar.
        stopDrawable.setBounds(0, 0, (int)Math.floor(stopDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(stopDrawable.getIntrinsicHeight()/1.5));
        getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : stopDrawable, null);

        getSearchField().setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
            	if (event.getAction() == MotionEvent.ACTION_DOWN) {
            		DDGControlVar.mCleanSearchBar = true;
                	getSearchField().setBackgroundDrawable(searchFieldDrawable);
                }
            	
                if (getSearchField().getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > getSearchField().getWidth() - getSearchField().getPaddingRight() - stopDrawable.getIntrinsicWidth()) {
                	if(getSearchField().getCompoundDrawables()[2] == stopDrawable) {
	                	stopAction();
                	}
                	else {
                		BusProvider.getInstance().post(new ReloadEvent());
                	}
                }
                return false;
            }

        });

        getSearchField().addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : stopDrawable, null);
            }

            public void afterTextChanged(Editable arg0) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });                        
                        
        fontSizeLayout = (LinearLayout) contentView.findViewById(R.id.fontSeekLayout);
        
        fontSizeSeekBar = (SeekBarHint) contentView.findViewById(R.id.fontSizeSeekBar);
        
        
        
        fontSizeSeekBar.setProgress(DDGControlVar.fontPrevProgress);
        fontSizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {		
				if(!fromUser) return;
								
				int diff = progress - DDGControlVar.fontPrevProgress;
				float diffPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 
		                (float) diff, getResources().getDisplayMetrics());
				// set thumb text
				if(diff == 0) {
					fontSizeSeekBar.setExtraText(getResources().getString(R.string.NoChange));
				}
				else if(progress == DDGConstants.FONT_SEEKBAR_MID) {
					fontSizeSeekBar.setExtraText(getResources().getString(R.string.Default));
				}
				else if(progress > DDGConstants.FONT_SEEKBAR_MID) {
					fontSizeSeekBar.setExtraText("+" + (progress-DDGConstants.FONT_SEEKBAR_MID));
				}
				else {
					fontSizeSeekBar.setExtraText(String.valueOf((progress-DDGConstants.FONT_SEEKBAR_MID)));
				}
				DDGControlVar.fontProgress = progress;				
				
				BusProvider.getInstance().post(new FontSizeChangeEvent(diff, diffPixel));
			}
		});
        
        Button fontSizeApplyButton = (Button) contentView.findViewById(R.id.fontSizeApplyButton);
        fontSizeApplyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DDGControlVar.fontPrevProgress = DDGControlVar.fontProgress;
				fontSizeSeekBar.setExtraText(null);
				
				PreferencesManager.saveAdjustedTextSizes();
				
				DDGControlVar.prevMainTextSize = 0;
				DDGControlVar.prevRecentTextSize = 0;
				DDGControlVar.prevWebViewTextSize = -1;
				DDGControlVar.prevPtrHeaderSize = 0;
				DDGControlVar.prevPtrSubHeaderSize = 0;
				DDGControlVar.prevLeftTitleTextSize = 0;
				fontSizeLayout.setVisibility(View.GONE);
				fontSizeSeekBar.setProgress(DDGControlVar.fontPrevProgress);
			}
		});
        
        
        Button fontSizeCancelButton = (Button) contentView.findViewById(R.id.fontSizeCancelButton);
        fontSizeCancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelFontScaling();
			}
		});
        
        
        displayHomeScreen();
        
        checkForAssistAction();

	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
		setRetainInstance(true);
        contentView = inflater.inflate(R.layout.main, container, false);
        return contentView;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		BusProvider.getInstance().register(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		BusProvider.getInstance().unregister(this);
	}
    
	private void clearSearchBar() {
		getSearchField().setText("");
    	getSearchField().setCompoundDrawables(null, null, null, null);
		getSearchField().setBackgroundDrawable(searchFieldDrawable);
	}
	
	public void setSearchBarText(String text) {
		getSearchField().setFocusable(false);
		getSearchField().setFocusableInTouchMode(false);
		getSearchField().setText(text);
		getSearchField().setFocusable(true);
		getSearchField().setFocusableInTouchMode(true);
	}
    
	private void cancelFontScaling() {
		fontSizeSeekBar.setExtraText(null);
		BusProvider.getInstance().post(new FontSizeCancelEvent());				
		fontSizeLayout.setVisibility(View.GONE);
		fontSizeSeekBar.setProgress(DDGControlVar.fontPrevProgress);				
	}  
    
    private void showBangButton(boolean visible){
    	homeSettingsButton.setVisibility(visible ? View.GONE: View.VISIBLE);
		bangButton.setVisibility(visible ? View.VISIBLE: View.GONE);
		if(shouldShowBangButtonExplanation && visible && PreferencesManager.isWelcomeShown()){
			bangButtonExplanationPopup = BangButtonExplanationPopup.showPopup(this, bangButton);
			shouldShowBangButtonExplanation = false;
		}
		if(!visible){
			if(bangButtonExplanationPopup!=null){
				bangButtonExplanationPopup.dismiss();
			}
		}
    }
    
	private void resetScreenState() {		
		clearSearchBar();		
		BusProvider.getInstance().post(new ResetScreenStateEvent());		
		DDGControlVar.sessionType = SESSIONTYPE.SESSION_BROWSE;
	}
	
	
	/**
	 * Displays given screen (stories, saved, settings etc.)
	 * 
	 * @param screenToDisplay Screen to display
	 * @param clean Whether screen state (searchbar, browser etc.) states will get cleaned
	 */
	public void displayScreen(SCREEN screenToDisplay, boolean clean) {
			if(clean) {
				resetScreenState();
			}
		
	        // control which screen is shown & configure related views
			
			if(DDGControlVar.prevMainTextSize != 0) {
				fontSizeLayout.setVisibility(View.VISIBLE);
			}
			
			switch(screenToDisplay) {
				case SCR_STORIES:
					displayNewsFeed();
					break;
				case SCR_RECENT_SEARCH:
					displayRecentSearch();
					break;
				case SCR_SAVED_FEED:
					displaySavedFeed();
					break;
				default:
					break;
			}
			
			BusProvider.getInstance().post(new UpdateVisibilityEvent(screenToDisplay));
	        
			DDGControlVar.prevScreen = DDGControlVar.currentScreen;
			DDGControlVar.currentScreen = screenToDisplay;	        			
	}
	
	private void displayHomeScreen() {
		displayScreen(DDGControlVar.START_SCREEN, true);
        
		if(DDGControlVar.sessionType == SESSIONTYPE.SESSION_SEARCH
				|| DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH || DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED_FEED) {
			// previous screen was a SERP
            keyboardService.toggleKeyboard(getSearchField());
		}
		DDGControlVar.sessionType = SESSIONTYPE.SESSION_BROWSE;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// lock button etc. can cause MainFeedTask results to be useless for the Activity
		// which is restarted (onPostExecute becomes invalid for the new Activity instance)
		// ensure we refresh in such cases

        BusProvider.getInstance().post(new FeedUpdateRequestEvent());
		
		// update feeds
		// https://app.asana.com/0/2891531242889/2858723303746
		DDGControlVar.hasUpdatedFeed = false;
		
		// check autocomplete 
		if(!DDGControlVar.isAutocompleteActive) {
			getSearchField().setAdapter(null);
		}
		else {
			getSearchField().setAdapter(acAdapter);
		}
		
		
		// global search intent
        Intent intent = getActivity().getIntent(); 
        
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			intent.setAction(Intent.ACTION_MAIN);
			String query = intent.getStringExtra(SearchManager.QUERY);
			setSearchBarText(query);
			BusProvider.getInstance().post(new SearchWebTermEvent(query));
		}
		else if(intent.getBooleanExtra("widget", false)) {
			viewFlipper.setDisplayedChild(DDGControlVar.START_SCREEN.getFlipOrder());
            keyboardService.showKeyboard(getSearchField());
		}
		else if(isWebViewShowing()){
			shareButton.setVisibility(View.VISIBLE);
			viewFlipper.setDisplayedChild(SCREEN.SCR_WEBVIEW.getFlipOrder());
		}
	}
	
	
	private void stopAction() {
		DDGControlVar.mCleanSearchBar = true;
    	getSearchField().setText("");

    	// This makes a little (X) to clear the search bar.
    	getSearchField().setCompoundDrawables(null, null, null, null);
    	getSearchField().setBackgroundDrawable(searchFieldDrawable);
	}
	
	/**
	 * main method that triggers display of Preferences screen or fragment
	 */
    private void displaySettings() {
       BusProvider.getInstance().post(new CleanFeedDownloadsEvent());
       Intent intent = new Intent(getActivity(), Preferences.class);
       startActivityForResult(intent, PREFERENCES_RESULT);
    }

	/**
	 * Method that switches visibility of views for Home or Saved feed
	 */
	private void displayFeedCore() {		
		switchFragments(SCREEN.SCR_STORIES);
		shareButton.setVisibility(View.GONE);
	}
	
	public void displayNewsFeed(){
		resetScreenState();    	
    	
    	// ensures feed refresh every time user switches to Stories screen
    	DDGControlVar.hasUpdatedFeed = false;
		
		displayFeedCore();
	}
	
	public void displaySavedFeed(){
		resetScreenState();
    	
		shareButton.setVisibility(View.GONE);
		
    	switchFragments(SCREEN.SCR_SAVED_FEED);
	}
	
	public void displayRecentSearch(){  
		resetScreenState(); 
		
    	// main view visibility changes
		shareButton.setVisibility(View.GONE);
		switchFragments(SCREEN.SCR_RECENT_SEARCH);		
	}
	
	public void displayWebView() {		
		// loading something in the browser - set home icon
		DDGControlVar.homeScreenShowing = false;
		homeSettingsButton.setImageResource(R.drawable.home_button);	
		
		shareButton.setVisibility(View.VISIBLE);
		switchFragments(SCREEN.SCR_WEBVIEW);			
	}
	
	public void switchFragments(SCREEN screen) {
//		FragmentManager fragmentManager = getSupportFragmentManager();
//		
//		Fragment mWorkFragment = null;
//		
//		switch(screen) {
//			case SCR_SAVED_FEED:
//				mWorkFragment = new SavedMainFragment();
//				break;
//			case SCR_RECENT_SEARCH:
//				mWorkFragment = new RecentSearchFragment();
//				break;
//		}
//			
//		if(mWorkFragment != null) {
//			mWorkFragment.setRetainInstance(true);
//			fragmentManager.beginTransaction().replace(R.id.placeholderFragment,
//	                mWorkFragment).commit();
//			
////			viewFlipper.setDisplayedChild(SCREEN.SCR_RECENT_SEARCH.getFlipOrder());
//
//		}
		
		viewFlipper.setDisplayedChild(screen.getFlipOrder());

	}
	
	public boolean isWebViewShowing() {
		int no = viewFlipper.getDisplayedChild();
		if(no == SCREEN.SCR_WEBVIEW.getFlipOrder())
			return true;
		return false;
	}
	
	public boolean inFontChangeMode() {
		return fontSizeLayout.getVisibility() != View.GONE;
	}
	
	@Subscribe
	public void onHomeButtonClick(HomeButtonClickEvent event) {
        keyboardService.hideKeyboard(getSearchField());
		
		if(!DDGControlVar.homeScreenShowing){
			displayHomeScreen();
		}
	}
	
	
	@Subscribe
	public void onRecentSearchPaste(RecentSearchPasteEvent event) {
        getSearchField().pasteQuery(event.query);
        keyboardService.showKeyboard(getSearchField());
	}

    @Subscribe
	public void onSuggestionPaste(SuggestionPasteEvent event) {
        getSearchField().pasteQuery(event.query);
	}
	
	@Subscribe
	public void onSavedSearchPaste(SavedSearchPasteEvent event) {
        getSearchField().pasteQuery(event.query);
        keyboardService.showKeyboard(getSearchField());
	}
	
	@Subscribe
	public void onSavedSearchItemSelected(SavedSearchItemSelectedEvent event) {
		if(DDGControlVar.currentScreen != SCREEN.SCR_WEBVIEW) {
			displayWebView();
		}
	}
	
	@Subscribe
	public void onHistoryItemSelected(HistoryItemSelectedEvent event) {
		if(DDGControlVar.currentScreen != SCREEN.SCR_WEBVIEW) {
			displayWebView();
		}
	}
	
	@Subscribe
	public void onSearchOrGoToUrl(SearchOrGoToUrlEvent event) {
		if(DDGControlVar.currentScreen != SCREEN.SCR_WEBVIEW) {
			displayWebView();
		}
		
		if(bangButtonExplanationPopup!=null){
			bangButtonExplanationPopup.dismiss();
		}
	}
	
	@Subscribe
	public void onDisplayScreen(DisplayScreenEvent event) {
		displayScreen(event.screenToDisplay, event.clean);
	}
	
	@Subscribe
	public void onSearchBarSearchDrawable(SearchBarSearchDrawableEvent event) {
		getSearchField().setBackgroundDrawable(searchFieldDrawable);
	}
	
	@Subscribe
	public void onSearchBarProgressDrawable(SearchBarProgressDrawableEvent event) {
		progressDrawable.setLevel(event.level);
		getSearchField().setBackgroundDrawable(progressDrawable);
	}
	
	@Subscribe
	public void onSearchBarSetText(SearchBarSetTextEvent event) {
		setSearchBarText(event.text);
	}
	
	@Subscribe
	public void onRecentHeaderClick(RecentHeaderClickEvent event) {
		displaySettings();
	}
	
	@Subscribe
	public void onLeftHomeButtonClick(LeftHomeButtonClickEvent event) {					
		if (isWebViewShowing()) {

			//We are going home!
			BusProvider.getInstance().post(new WebViewResetEvent());
			clearSearchBar();
		}
		
		displayHomeScreen();
	}
	
	@Subscribe
	public void onLeftStoriesButtonClick(LeftStoriesButtonClickEvent event) {
		displayScreen(SCREEN.SCR_STORIES, false);
	}
	
	@Subscribe
	public void onLeftSavedButtonClick(LeftSavedButtonClickEvent event) {
		displayScreen(SCREEN.SCR_SAVED_FEED, false);
	}
	
	@Subscribe
	public void onLeftSettingsButtonClick(LeftSettingsButtonClickEvent event) {
        displaySettings();
	}
	
	@Subscribe
	public void onUpdateVisibility(UpdateVisibilityEvent event) {
		if(DDGControlVar.START_SCREEN == event.screen) {			
			DDGControlVar.homeScreenShowing = true;
			if(isWebViewShowing()){
	    		homeSettingsButton.setImageResource(R.drawable.home_button);
			}
			else {
	    		homeSettingsButton.setImageResource(R.drawable.menu_button);
			}
		}
		else {
    		homeSettingsButton.setImageResource(R.drawable.home_button);
		}
	}
	
		
	@Subscribe
	public void onReloadEvent(ReloadEvent event) {
		DDGControlVar.mCleanSearchBar = false;
        stopDrawable.setBounds(0, 0, (int) Math.floor(stopDrawable.getIntrinsicWidth() / 1.5), (int) Math.floor(stopDrawable.getIntrinsicHeight() / 1.5));
		getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : stopDrawable, null);
	}	
    
    @Subscribe
    public void onSearchBarClick(SearchBarClickEvent event) {
    	showBangButton(true);
    }
    
    @Subscribe
    public void onHideKeyboard(HideKeyboardEvent event) {
    	keyboardService.hideKeyboard(getSearchField());
    }
}
