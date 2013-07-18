package com.duckduckgo.mobile.android.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.AutoCompleteResultsAdapter;
import com.duckduckgo.mobile.android.adapters.DDGPagerAdapter;
import com.duckduckgo.mobile.android.adapters.MultiHistoryAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.dialogs.NewSourcesDialogBuilder;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistorySearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.HistoryStoryMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.MainFeedMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedSearchMenuDialog;
import com.duckduckgo.mobile.android.dialogs.menuDialogs.SavedStoryMenuDialog;
import com.duckduckgo.mobile.android.events.CleanFeedDownloadsEvent;
import com.duckduckgo.mobile.android.events.DisplayScreenEvent;
import com.duckduckgo.mobile.android.events.FeedUpdateRequestEvent;
import com.duckduckgo.mobile.android.events.FontSizeCancelEvent;
import com.duckduckgo.mobile.android.events.FontSizeChangeEvent;
import com.duckduckgo.mobile.android.events.HistoryItemLongClickEvent;
import com.duckduckgo.mobile.android.events.ReloadEvent;
import com.duckduckgo.mobile.android.events.ResetScreenStateEvent;
import com.duckduckgo.mobile.android.events.SearchBarProgressDrawableEvent;
import com.duckduckgo.mobile.android.events.SearchBarSearchDrawableEvent;
import com.duckduckgo.mobile.android.events.SearchOrGoToUrlEvent;
import com.duckduckgo.mobile.android.events.SearchWebTermEvent;
import com.duckduckgo.mobile.android.events.ShareButtonClickEvent;
import com.duckduckgo.mobile.android.events.SourceFilterCancelEvent;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.WebViewBackPressEvent;
import com.duckduckgo.mobile.android.events.WebViewResetEvent;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteStoryInHistoryEvent;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteUrlInHistoryEvent;
import com.duckduckgo.mobile.android.events.externalEvents.SendToExternalBrowserEvent;
import com.duckduckgo.mobile.android.events.feedEvents.FeedItemSelectedEvent;
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.feedEvents.SavedFeedItemLongClickEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.RecentSearchPasteEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.SavedSearchPasteEvent;
import com.duckduckgo.mobile.android.events.pasteEvents.SuggestionPasteEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.SaveStoryEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveSearchEvent;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveStoryEvent;
import com.duckduckgo.mobile.android.events.savedSearchEvents.SavedSearchItemLongClickEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareFeedEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareSearchEvent;
import com.duckduckgo.mobile.android.events.shareEvents.ShareWebPageEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.SuggestObject;
import com.duckduckgo.mobile.android.tasks.ScanAppsTask;
import com.duckduckgo.mobile.android.util.AppStateManager;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.DDGViewPager;
import com.duckduckgo.mobile.android.util.DisplayStats;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.util.SESSIONTYPE;
import com.duckduckgo.mobile.android.util.Sharer;
import com.duckduckgo.mobile.android.util.SuggestType;
import com.duckduckgo.mobile.android.util.TorIntegrationProvider;
import com.duckduckgo.mobile.android.views.HistoryListView;
import com.duckduckgo.mobile.android.views.SeekBarHint;
import com.duckduckgo.mobile.android.views.WelcomeScreenView;
import com.duckduckgo.mobile.android.views.autocomplete.BackButtonPressedEventListener;
import com.duckduckgo.mobile.android.views.autocomplete.DDGAutoCompleteTextView;
import com.duckduckgo.mobile.android.widgets.BangButtonExplanationPopup;
import com.duckduckgo.mobile.android.widgets.SafeViewFlipper;
import com.squareup.otto.Subscribe;

public class DuckDuckGo extends FragmentActivity implements OnClickListener {
	protected final String TAG = "DuckDuckGo";
    private KeyboardService keyboardService;

    public DuckDuckGoContainer mDuckDuckGoContainer;

	private DDGAutoCompleteTextView searchField = null;
	private HistoryListView leftRecentView = null;
		
	private DDGViewPager viewPager;
	private View contentView = null;
	private View leftMenuView = null;
	
	private SafeViewFlipper viewFlipper = null;
			
	private ImageButton homeSettingsButton = null;
	private ImageButton bangButton = null;
	private ImageButton shareButton = null;
	
	private TextView leftHomeTextView = null;
	private TextView leftStoriesTextView = null;
	private TextView leftSavedTextView = null;
	private TextView leftSettingsTextView = null;
	
	private LinearLayout leftHomeButtonLayout = null;
	private LinearLayout leftStoriesButtonLayout = null;
	private LinearLayout leftSavedButtonLayout = null;
	private LinearLayout leftSettingsButtonLayout = null;
	
	// font scaling
	private LinearLayout fontSizeLayout = null;
	
	// welcome screen
	private WelcomeScreenView welcomeScreenLayout = null;
	OnClickListener welcomeCloseListener = null;
		
	private SharedPreferences sharedPreferences;
				
	private final int PREFERENCES_RESULT = 0;
			
	// keep prev progress in font seek bar, to make incremental changes available
	SeekBarHint fontSizeSeekBar;
	
	private boolean shouldShowBangButtonExplanation;

	private BangButtonExplanationPopup bangButtonExplanationPopup;    
    
    public void syncAdapters() {    	
    	BusProvider.getInstance().post(new SyncAdaptersEvent());
    }
    
    /**
     * Adds welcome screen on top of content view
     * Also disables dispatching of touch events from viewPager to children views
     */
    private void addWelcomeScreen() {
    	viewPager.setDispatchTouch(false);
    	
    	if(!getResources().getBoolean(R.bool.welcomeScreen_allowLandscape)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
    	
    	// add welcome screen
        welcomeScreenLayout = new WelcomeScreenView(this);
        FrameLayout rootLayout = (FrameLayout)findViewById(android.R.id.content);
        rootLayout.addView(welcomeScreenLayout);
    	welcomeScreenLayout.setOnCloseListener(new OnClickListener() {
			@Override
			public void onClick(View v) {		
				removeWelcomeScreen();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
		});
    }
    
    /**
     * Removes welcome screen from content view
     * Also enables dispatching of touch events from viewPager
     */
    private void removeWelcomeScreen() {
    	welcomeScreenLayout.setVisibility(View.GONE);	
		viewPager.setDispatchTouch(true);					
		PreferencesManager.setWelcomeShown();
    	// remove welcome screen
		FrameLayout rootLayout = (FrameLayout)findViewById(android.R.id.content);
		rootLayout.removeView(welcomeScreenLayout);
		welcomeScreenLayout = null;
    }
    
    private void showBangButton(boolean visible){
    	homeSettingsButton.setVisibility(visible ? View.GONE: View.VISIBLE);
		bangButton.setVisibility(visible ? View.VISIBLE: View.GONE);
		if(shouldShowBangButtonExplanation && visible && welcomeScreenLayout == null){
			bangButtonExplanationPopup = BangButtonExplanationPopup.showPopup(DuckDuckGo.this, bangButton);
			shouldShowBangButtonExplanation = false;
		}
		if(!visible){
			if(bangButtonExplanationPopup!=null){
				bangButtonExplanationPopup.dismiss();
			}
		}
    }

    @Override
    protected void onStart() {
        super.onStart();
        TorIntegrationProvider.getInstance(this).prepareTorSettings();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keyboardService = new KeyboardService(this);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        showNewSourcesDialog();

        sharedPreferences = DDGApplication.getSharedPreferences();
        
        String themeName = PreferencesManager.getThemeName();
		int themeId = getResources().getIdentifier(themeName, "style", getPackageName());
		if(themeId != 0) {
			setTheme(themeId);
		}
        		        
        setContentView(R.layout.pager);       
        
        DDGUtils.displayStats = new DisplayStats(this);
        
        DDGControlVar.isAutocompleteActive = !PreferencesManager.getTurnOffAutocomplete();
        // always refresh on start
        DDGControlVar.hasUpdatedFeed = false;
        mDuckDuckGoContainer = (DuckDuckGoContainer) getLastCustomNonConfigurationInstance();
    	if(mDuckDuckGoContainer == null){
            initializeContainer();
    	}
    	

    	
        viewPager = (DDGViewPager) findViewById(R.id.mainpager);
        viewPager.setAdapter(mDuckDuckGoContainer.pageAdapter);
        viewPager.hideMenu();


        if(!PreferencesManager.isWelcomeShown()) {
            addWelcomeScreen();
            shouldShowBangButtonExplanation = true;
    	}
        
        leftMenuView = mDuckDuckGoContainer.pageAdapter.getPageView(0);
        contentView = mDuckDuckGoContainer.pageAdapter.getPageView(1);    
        
        viewFlipper = (SafeViewFlipper) contentView.findViewById(R.id.ViewFlipperMain);
    	    	
    	leftHomeTextView = (TextView) leftMenuView.findViewById(R.id.LeftHomeTextView);
    	leftStoriesTextView = (TextView) leftMenuView.findViewById(R.id.LeftStoriesTextView);
    	leftSavedTextView = (TextView) leftMenuView.findViewById(R.id.LeftSavedTextView);
    	leftSettingsTextView = (TextView) leftMenuView.findViewById(R.id.LeftSettingsTextView);
    	
    	leftHomeTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftStoriesTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftSavedTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftSettingsTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);    	
    	
    	
    	TypedValue tmpTypedValue = new TypedValue(); 
    	getTheme().resolveAttribute(R.attr.leftButtonTextSize, tmpTypedValue, true);
    	// XXX getDimension returns in PIXELS !
    	float defLeftTitleTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.leftTitleTextSize = PreferencesManager.getLeftTitleTextSize(defLeftTitleTextSize);
    	
    	leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize); 
    	    	
    	leftHomeButtonLayout = (LinearLayout) leftMenuView.findViewById(R.id.LeftHomeButtonLayout);
    	leftStoriesButtonLayout = (LinearLayout) leftMenuView.findViewById(R.id.LeftStoriesButtonLayout);
    	leftSavedButtonLayout = (LinearLayout) leftMenuView.findViewById(R.id.LeftSavedButtonLayout);
    	leftSettingsButtonLayout = (LinearLayout) leftMenuView.findViewById(R.id.LeftSettingsButtonLayout);
    	
    	
    	int pixelValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) 20.0, getResources().getDisplayMetrics());
    	
    	TypedValue typedValue = new TypedValue(); 
    	getTheme().resolveAttribute(R.attr.leftDrawableHome, typedValue, true);
    	
    	Drawable xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftHomeTextView.setCompoundDrawables(xt, null, null, null);
        
        getTheme().resolveAttribute(R.attr.leftDrawableStories, typedValue, true);
    	xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftStoriesTextView.setCompoundDrawables(xt, null, null, null);        
        
        getTheme().resolveAttribute(R.attr.leftDrawableSaved, typedValue, true);
    	xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftSavedTextView.setCompoundDrawables(xt, null, null, null);
        
        getTheme().resolveAttribute(R.attr.leftDrawableSettings, typedValue, true);
    	xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftSettingsTextView.setCompoundDrawables(xt, null, null, null);
    	
    	leftHomeTextView.setOnClickListener(this);
    	leftStoriesTextView.setOnClickListener(this);
    	leftSavedTextView.setOnClickListener(this);
    	leftSettingsTextView.setOnClickListener(this);
    	
    	leftRecentView = (HistoryListView) leftMenuView.findViewById(R.id.LeftRecentView);
		
		leftRecentView.setDivider(null);
    	leftRecentView.setAdapter(mDuckDuckGoContainer.historyAdapter);
    	
    	// "Save Recents" not enabled notification click listener
    	leftRecentView.setOnHeaderClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewPager.switchPage();		
				displaySettings();
			}
		});
        
        homeSettingsButton = (ImageButton) contentView.findViewById(R.id.settingsButton);
        homeSettingsButton.setOnClickListener(this);
        bangButton = (ImageButton)contentView.findViewById(R.id.bangButton);
        bangButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getSearchField().addBang();				
			}
		});
        
        if(DDGControlVar.webviewShowing) {
        	homeSettingsButton.setImageResource(R.drawable.home_button);
        }
        
        shareButton = (ImageButton) contentView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);
        
        // adjust visibility of share button after screen rotation
        if(DDGControlVar.webviewShowing) {
        	shareButton.setVisibility(View.VISIBLE);
        }
        
        searchField = (DDGAutoCompleteTextView) contentView.findViewById(R.id.searchEditText);
        getSearchField().setAdapter(mDuckDuckGoContainer.acAdapter);
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
				// close left n	av if it's open
				if(viewPager.isLeftMenuOpen()){
                    viewPager.hideMenu();
                }
				showBangButton(true);
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
					
					SuggestObject suggestObject = mDuckDuckGoContainer.acAdapter.getItem(position);
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
							DDGUtils.launchApp(DuckDuckGo.this, suggestObject.getSnippet());
						}
					}
				}
			}
		});

        // This makes a little (X) to clear the search bar.
        mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int)Math.floor(mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()/1.5), (int)Math.floor(mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight()/1.5));
        getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : mDuckDuckGoContainer.stopDrawable, null);

        getSearchField().setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
            	if (event.getAction() == MotionEvent.ACTION_DOWN) {
            		DDGControlVar.mCleanSearchBar = true;
                	getSearchField().setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
                }
            	
                if (getSearchField().getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > getSearchField().getWidth() - getSearchField().getPaddingRight() - mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth()) {
                	if(getSearchField().getCompoundDrawables()[2] == mDuckDuckGoContainer.stopDrawable) {
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
            	getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : mDuckDuckGoContainer.stopDrawable, null);
            }

            public void afterTextChanged(Editable arg0) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });                        
                        
        fontSizeLayout = (LinearLayout) contentView.findViewById(R.id.fontSeekLayout);
        
        fontSizeSeekBar = (SeekBarHint) contentView.findViewById(R.id.fontSizeSeekBar);
        
    	getTheme().resolveAttribute(R.attr.mainTextSize, tmpTypedValue, true);
    	float defMainTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.mainTextSize = PreferencesManager.getMainFontSize(defMainTextSize);
    	
    	getTheme().resolveAttribute(R.attr.recentTextSize, tmpTypedValue, true);
    	float defRecentTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.recentTextSize = PreferencesManager.getRecentFontSize(defRecentTextSize);
        
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
				
				DDGControlVar.leftTitleTextSize = DDGControlVar.prevLeftTitleTextSize + diffPixel;
				
				mDuckDuckGoContainer.historyAdapter.notifyDataSetInvalidated();
				
				BusProvider.getInstance().post(new FontSizeChangeEvent(diff, diffPixel));
				
				leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		    	leftMenuView.invalidate();
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

    private void initializeContainer() {
        mDuckDuckGoContainer = new DuckDuckGoContainer();

        mDuckDuckGoContainer.pageAdapter = new DDGPagerAdapter(this);

        mDuckDuckGoContainer.stopDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.stop);
//    		mDuckDuckGoContainer.reloadDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.reload);
        mDuckDuckGoContainer.progressDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.page_progress);
        mDuckDuckGoContainer.searchFieldDrawable = DuckDuckGo.this.getResources().getDrawable(R.drawable.searchfield);
        mDuckDuckGoContainer.searchFieldDrawable.setAlpha(150);

        mDuckDuckGoContainer.historyAdapter = new MultiHistoryAdapter(this);
        
        mDuckDuckGoContainer.acAdapter = new AutoCompleteResultsAdapter(this);
    }

    // Assist action is better known as Google Now gesture
	private void checkForAssistAction() {
		if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_ASSIST)) {
            keyboardService.toggleKeyboard(getSearchField());
		}
	}

    private void showNewSourcesDialog() {
        if(PreferencesManager.shouldShowNewSourcesDialog()){
            new NewSourcesDialogBuilder(this).show();
            PreferencesManager.newSourcesDialogWasShown();
        }
    }	
	
	private void clearSearchBar() {
		getSearchField().setText("");
    	getSearchField().setCompoundDrawables(null, null, null, null);
		getSearchField().setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
	}
	
	public void setSearchBarText(String text) {
		getSearchField().setFocusable(false);
		getSearchField().setFocusableInTouchMode(false);
		getSearchField().setText(text);
		getSearchField().setFocusable(true);
		getSearchField().setFocusableInTouchMode(true);
	}
	
	private void resetScreenState() {		
		clearSearchBar();		
		BusProvider.getInstance().post(new ResetScreenStateEvent());		
		DDGControlVar.sessionType = SESSIONTYPE.SESSION_BROWSE;
	}
	
	private void cancelFontScaling() {
		fontSizeSeekBar.setExtraText(null);
		BusProvider.getInstance().post(new FontSizeCancelEvent());
				
		DDGControlVar.leftTitleTextSize = DDGControlVar.prevLeftTitleTextSize;
		mDuckDuckGoContainer.historyAdapter.notifyDataSetInvalidated();
		
		DDGControlVar.prevLeftTitleTextSize = 0;
		fontSizeLayout.setVisibility(View.GONE);
		fontSizeSeekBar.setProgress(DDGControlVar.fontPrevProgress);
		
		leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftMenuView.invalidate();
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
			
			if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH &&
					!screenToDisplay.equals(SCREEN.SCR_RECENT_SEARCH)){
	        	leftRecentView.setVisibility(View.VISIBLE);
			}
	        
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
		BusProvider.getInstance().register(this);
		
        DDGUtils.displayStats.refreshStats(this);
		
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
	        getSearchField().setAdapter(mDuckDuckGoContainer.acAdapter);
		}
		
		if(DDGControlVar.includeAppsInSearch && !DDGControlVar.hasAppsIndexed) {
			// index installed apps
			new ScanAppsTask(getApplicationContext()).execute();
			DDGControlVar.hasAppsIndexed = true;
		}
		
		// global search intent
        Intent intent = getIntent(); 
        
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
		else if(DDGControlVar.webviewShowing){
			shareButton.setVisibility(View.VISIBLE);
			viewFlipper.setDisplayedChild(SCREEN.SCR_WEBVIEW.getFlipOrder());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		
		BusProvider.getInstance().unregister(this);

		PreferencesManager.saveReadArticles();
		
		// XXX keep these for low memory conditions
		AppStateManager.saveAppState(sharedPreferences, mDuckDuckGoContainer, DDGControlVar.currentFeedObject);
	}
	
	@Override
	protected void onStop() {
		PreferencesManager.saveReadArticles();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		DDGApplication.getImageCache().purge();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		// close left nav if it's open
		if(viewPager.isLeftMenuOpen()){
			viewPager.setCurrentItem(SCREEN.SCR_STORIES.getFlipOrder());
		}
		else if (DDGControlVar.webviewShowing) {
			BusProvider.getInstance().post(new WebViewBackPressEvent());
		}
		else if(fontSizeLayout.getVisibility() != View.GONE) {
			cancelFontScaling();
		}
		// main feed showing & source filter is active
		else if(DDGControlVar.targetSource != null){
			BusProvider.getInstance().post(new SourceFilterCancelEvent());
		}
		else {
			DDGControlVar.hasUpdatedFeed = false;
			super.onBackPressed();
		}
	}
	
	private void stopAction() {
		DDGControlVar.mCleanSearchBar = true;
    	getSearchField().setText("");

    	// This makes a little (X) to clear the search bar.
    	getSearchField().setCompoundDrawables(null, null, null, null);
    	getSearchField().setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
	}	
	
	public void clearRecentSearch() {
		mDuckDuckGoContainer.historyAdapter.sync();
	}	
	
	private void clearLeftSelect() {
		leftHomeTextView.setSelected(false);
		leftSavedTextView.setSelected(false);
		leftSettingsTextView.setSelected(false);
		leftStoriesTextView.setSelected(false);
	}
	
	/**
	 * main method that triggers display of Preferences screen or fragment
	 */
    private void displaySettings() {
       BusProvider.getInstance().post(new CleanFeedDownloadsEvent());
       Intent intent = new Intent(getBaseContext(), Preferences.class);
       startActivityForResult(intent, PREFERENCES_RESULT);
    }

	/** 
	 * change button visibility in left-side navigation menu
	 * according to screen
	 */
	private void changeLeftMenuVisibility() {
		// stories button
		if(DDGControlVar.START_SCREEN != SCREEN.SCR_STORIES) {
			leftStoriesButtonLayout.setVisibility(View.VISIBLE);
		}
		else {
	    	leftStoriesButtonLayout.setVisibility(View.GONE);
		}
		
		// saved button
		if(DDGControlVar.START_SCREEN != SCREEN.SCR_SAVED_FEED) {
			leftSavedButtonLayout.setVisibility(View.VISIBLE);
		}
		else {
			leftSavedButtonLayout.setVisibility(View.GONE);
		}
    	
		// recent search button
    	if(DDGControlVar.START_SCREEN != SCREEN.SCR_RECENT_SEARCH) {
        	leftRecentView.setVisibility(View.VISIBLE);
    	}
    	else {
        	leftRecentView.setVisibility(View.GONE);
    	}
	}


	/**
	 * Method that switches visibility of views for Home or Saved feed
	 */
	private void displayFeedCore() {		
    	// main view visibility changes and keep feed updated
		viewFlipper.setDisplayedChild(SCREEN.SCR_STORIES.getFlipOrder());
		shareButton.setVisibility(View.GONE);
		DDGControlVar.webviewShowing = false;
	}
	
	public void displayNewsFeed(){
		resetScreenState();
		
		// left side menu visibility changes
		changeLeftMenuVisibility();
    	
    	// adjust "not recording" indicator
		leftRecentView.displayRecordHistoryDisabled();
    	
    	// ensures feed refresh every time user switches to Stories screen
    	DDGControlVar.hasUpdatedFeed = false;
		
		displayFeedCore();
		clearLeftSelect();
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_STORIES){
    		DDGControlVar.homeScreenShowing = true;
    		homeSettingsButton.setImageResource(R.drawable.menu_button);
			leftHomeTextView.setSelected(true);
    	}
    	else {
			leftStoriesTextView.setSelected(true);
    	}
	}
	
	public void displaySavedFeed(){
		resetScreenState();
		
		// left side menu visibility changes
		changeLeftMenuVisibility();
    	
		shareButton.setVisibility(View.GONE);
		
    	switchFragments(SCREEN.SCR_SAVED_FEED);
    	
    	DDGControlVar.webviewShowing = false;
		clearLeftSelect();
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_SAVED_FEED){
    		DDGControlVar.homeScreenShowing = true;
    		homeSettingsButton.setImageResource(R.drawable.menu_button);
			leftHomeTextView.setSelected(true);
    	}
    	else {
			leftSavedTextView.setSelected(true);
    	}
	}
	
	public void displayRecentSearch(){  
		resetScreenState(); 
		
		// left side menu visibility changes
		changeLeftMenuVisibility();
		
    	// main view visibility changes
		shareButton.setVisibility(View.GONE);
		switchFragments(SCREEN.SCR_RECENT_SEARCH);
		DDGControlVar.webviewShowing = false;
		
		clearLeftSelect();
    	    	
    	if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH){
    		DDGControlVar.homeScreenShowing = true;
    		homeSettingsButton.setImageResource(R.drawable.menu_button);
    		leftHomeTextView.setSelected(true);
    	}
	}
	
	public void displayWebView() {		
		// loading something in the browser - set home icon
		DDGControlVar.homeScreenShowing = false;
		homeSettingsButton.setImageResource(R.drawable.home_button);	
		
		shareButton.setVisibility(View.VISIBLE);
		switchFragments(SCREEN.SCR_WEBVIEW);			
		DDGControlVar.webviewShowing = true;
	}
	
	public void onClick(View view) {
		if (view.equals(homeSettingsButton)) {			
			handleHomeSettingsButtonClick();
		}
		else if (view.equals(shareButton)) {			
			BusProvider.getInstance().post(new ShareButtonClickEvent());
		}
		else if(view.equals(leftHomeTextView)){
			handleLeftHomeTextViewClick();
		}
		else if(view.equals(leftStoriesTextView)){
			viewPager.switchPage();		
			displayScreen(SCREEN.SCR_STORIES, false);
		}
		else if(view.equals(leftSavedTextView)){
			viewPager.switchPage();		
			displayScreen(SCREEN.SCR_SAVED_FEED, false);
		}
		else if(view.equals(leftSettingsTextView)){
			viewPager.switchPage();
            displaySettings();
		}
	}

	private void handleLeftHomeTextViewClick() {
		viewPager.switchPage();
					
		if (DDGControlVar.webviewShowing) {

			//We are going home!
			BusProvider.getInstance().post(new WebViewResetEvent());
			clearSearchBar();
			DDGControlVar.webviewShowing = false;					
		}
		
		displayHomeScreen();
	}

	private void handleHomeSettingsButtonClick() {
        keyboardService.hideKeyboard(getSearchField());
		
		if(DDGControlVar.homeScreenShowing){
			viewPager.switchPage();
		}
		else {
			// going home
			displayHomeScreen();
		}
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
                boolean startOrbotCheck = data.getBooleanExtra("startOrbotCheck",false);
                if(startOrbotCheck){
                    BusProvider.getInstance().post(new SearchOrGoToUrlEvent(getString(R.string.OrbotCheckSite)));
                }
                boolean switchTheme = data.getBooleanExtra("switchTheme", false);
                if(switchTheme){
                    Intent intent = new Intent(getApplicationContext(), DuckDuckGo.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
			}
		}
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
	       // return page container, holding all non-view data
	       return mDuckDuckGoContainer;
	}
    
	@Override
	protected void onSaveInstanceState(Bundle outState)	{
		AppStateManager.saveAppState(outState, mDuckDuckGoContainer, DDGControlVar.currentFeedObject);					
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		
		AppStateManager.recoverAppState(savedInstanceState, mDuckDuckGoContainer, DDGControlVar.currentFeedObject);
		String feedId = AppStateManager.getCurrentFeedObjectId(savedInstanceState);
		
		clearLeftSelect();
		markLeftSelect(DDGControlVar.currentScreen);		
		
		Log.v(TAG, "feedId: " + feedId);
		
		if(feedId != null && feedId.length() != 0) {
			FeedObject feedObject = DDGApplication.getDB().selectFeedById(feedId);
			if(feedObject != null) {
				DDGControlVar.currentFeedObject = feedObject;
			}
		}			
		
		if(DDGControlVar.webviewShowing) {
			return;
		}
		

		displayScreen(DDGControlVar.currentScreen, true);
	}
	
	private void markLeftSelect(SCREEN current){
		if(DDGControlVar.START_SCREEN == current) {
			leftHomeTextView.setSelected(true);
			
			if(DDGControlVar.webviewShowing){
	    		homeSettingsButton.setImageResource(R.drawable.home_button);
			}
			else {
	    		homeSettingsButton.setImageResource(R.drawable.menu_button);
			}
		}
		else {
    		homeSettingsButton.setImageResource(R.drawable.home_button);
			switch(current) {
				case SCR_STORIES:
					leftStoriesTextView.setSelected(true);
					break;
				case SCR_SAVED_FEED:
					leftSavedTextView.setSelected(true);
					break;
			}
		}
	}	
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		DDGUtils.displayStats.refreshStats(this);
		
		if(welcomeScreenLayout != null) {
			removeWelcomeScreen();
			addWelcomeScreen();
		}
		super.onConfigurationChanged(newConfig);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ( keyCode == KeyEvent.KEYCODE_MENU ) {
	    	viewPager.switchPage();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	public DDGAutoCompleteTextView getSearchField() {
		return searchField;
	}
	
	@Subscribe
	public void onDeleteStoryInHistoryEvent(DeleteStoryInHistoryEvent event) {
		final long delResult = DDGApplication.getDB().deleteHistoryByFeedId(event.feedObjectId);
		if(delResult != 0) {							
			syncAdapters();
		}
		Toast.makeText(this, R.string.ToastDeleteStoryInHistory, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onDeleteUrlInHistoryEvent(DeleteUrlInHistoryEvent event) {
		final long delHistory = DDGApplication.getDB().deleteHistoryByDataUrl(event.pageData, event.pageUrl);				
		if(delHistory != 0) {							
			syncAdapters();
		}	
		Toast.makeText(this, R.string.ToastDeleteUrlInHistory, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onReloadEvent(ReloadEvent event) {
		DDGControlVar.mCleanSearchBar = false;
        mDuckDuckGoContainer.stopDrawable.setBounds(0, 0, (int) Math.floor(mDuckDuckGoContainer.stopDrawable.getIntrinsicWidth() / 1.5), (int) Math.floor(mDuckDuckGoContainer.stopDrawable.getIntrinsicHeight() / 1.5));
		getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : mDuckDuckGoContainer.stopDrawable, null);
	}
	
	@Subscribe
	public void onSaveSearchEvent(SaveSearchEvent event) {
		DDGUtils.itemSaveSearch(event.pageData);
		syncAdapters();
		Toast.makeText(this, R.string.ToastSaveSearch, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onSaveStoryEvent(SaveStoryEvent event) {
		DDGUtils.itemSaveFeed(event.feedObject, null);
		syncAdapters();
		Toast.makeText(this, R.string.ToastSaveStory, Toast.LENGTH_SHORT).show();
	}	
	
	@Subscribe
	public void onSendToExternalBrowserEvent(SendToExternalBrowserEvent event) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.url));
		DDGUtils.execIntentIfSafe(this, browserIntent);
	}
	
	@Subscribe
	public void onShareFeedEvent(ShareFeedEvent event) {
		Sharer.shareStory(this, event.title, event.url);
	}
	
	@Subscribe
	public void onShareSearchEvent(ShareSearchEvent event) {
		Sharer.shareSearch(this, event.query);
	}
	
	@Subscribe
	public void onShareWebPageEvent(ShareWebPageEvent event) {
		Sharer.shareWebPage(this, event.url, event.url);
	}	
	
	@Subscribe
	public void onUnSaveSearchEvent(UnSaveSearchEvent event) {
		final long delHistory = DDGApplication.getDB().deleteSavedSearch(event.query);
		if(delHistory != 0) {							
			syncAdapters();
		}	
		Toast.makeText(this, R.string.ToastUnSaveSearch, Toast.LENGTH_SHORT).show();
	}
	
	@Subscribe
	public void onUnSaveStoryEvent(UnSaveStoryEvent event) {
		final long delResult = DDGApplication.getDB().makeItemHidden(event.feedObjectId);
		if(delResult != 0) {							
			syncAdapters();
		}
		Toast.makeText(this, R.string.ToastUnSaveStory, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Handling both MainFeedItemSelectedEvent and SavedFeedItemSelectedEvent.
	 * (modify to handle independently when necessary)
	 * @param event
	 */
	@Subscribe
	public void onFeedItemSelected(FeedItemSelectedEvent event) {
		// close left nav if it's open
		if(viewPager.isLeftMenuOpen()){
            viewPager.hideMenu();
        }
	}
	
	@Subscribe
	public void onMainFeedItemLongClick(MainFeedItemLongClickEvent event) {
		new MainFeedMenuDialog(DuckDuckGo.this, event.feedObject).show();
	}
	
	@Subscribe
	public void onSavedFeedItemLongClick(SavedFeedItemLongClickEvent event) {
        new SavedStoryMenuDialog(DuckDuckGo.this, event.feedObject).show();
    }
	
	@Subscribe
	public void onHistoryItemLongClick(HistoryItemLongClickEvent event) {
        if(event.historyObject.isFeedObject()) {
            new HistoryStoryMenuDialog(DuckDuckGo.this, event.historyObject).show();
        }
        else{
            new HistorySearchMenuDialog(DuckDuckGo.this, event.historyObject).show();
        }
	}
	
	@Subscribe
	public void onSavedSearchItemLongClick(SavedSearchItemLongClickEvent event) {
		new SavedSearchMenuDialog(this, event.query).show();
	}

	@Subscribe
	public void onRecentSearchPaste(RecentSearchPasteEvent event) {
        viewPager.hideMenu();
        getSearchField().pasteQuery(event.query);
        keyboardService.showKeyboard(getSearchField());
	}

    @Subscribe
	public void onSuggestionPaste(SuggestionPasteEvent event) {
        viewPager.hideMenu();
        getSearchField().pasteQuery(event.query);
	}
	
	@Subscribe
	public void onSavedSearchPaste(SavedSearchPasteEvent event) {
        viewPager.hideMenu();
        getSearchField().pasteQuery(event.query);
        keyboardService.showKeyboard(getSearchField());
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
	public void onSyncAdapters(SyncAdaptersEvent event) {
		mDuckDuckGoContainer.historyAdapter.sync();
	}
	
	@Subscribe
	public void onDisplayScreen(DisplayScreenEvent event) {
		displayScreen(event.screenToDisplay, event.clean);
	}
	
	@Subscribe
	public void onSearchBarSearchDrawable(SearchBarSearchDrawableEvent event) {
		getSearchField().setBackgroundDrawable(mDuckDuckGoContainer.searchFieldDrawable);
	}
	
	@Subscribe
	public void onSearchBarProgressDrawable(SearchBarProgressDrawableEvent event) {
		mDuckDuckGoContainer.progressDrawable.setLevel(event.level);
		getSearchField().setBackgroundDrawable(mDuckDuckGoContainer.progressDrawable);
	}
}
