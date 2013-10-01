package com.duckduckgo.mobile.android.fragment;


import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.MultiHistoryAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.RecentHeaderClickEvent;
import com.duckduckgo.mobile.android.events.ResetScreenStateEvent;
import com.duckduckgo.mobile.android.events.SyncAdaptersEvent;
import com.duckduckgo.mobile.android.events.UpdateVisibilityEvent;
import com.duckduckgo.mobile.android.events.fontEvents.FontSizeCancelEvent;
import com.duckduckgo.mobile.android.events.fontEvents.FontSizeChangeEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftHomeButtonClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftSavedButtonClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftSettingsButtonClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuButtonEvents.LeftStoriesButtonClickEvent;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.views.HistoryListView;
import com.squareup.otto.Subscribe;

public class LeftFragment extends Fragment {
	
	private View contentView;
	
	private MultiHistoryAdapter historyAdapter;
	private HistoryListView leftRecentView = null;
	
	private TextView leftHomeTextView = null;
	private TextView leftStoriesTextView = null;
	private TextView leftSavedTextView = null;
	private TextView leftSettingsTextView = null;
	
	private LinearLayout leftHomeButtonLayout = null;
	private LinearLayout leftStoriesButtonLayout = null;
	private LinearLayout leftSavedButtonLayout = null;
	private LinearLayout leftSettingsButtonLayout = null;
	
	/**
	 * Resolve drawable associated with attribute value from
	 * theme and set it as left drawable to the TextView
	 * 
	 * @param theme
	 * @param view
	 * @param attributeId
	 * @param sizeInDp
	 */
	private void setLeftDrawable(Theme theme, TextView view, int attributeId, int sizeInDp) {
		TypedValue typedValue = new TypedValue(); 
    	theme.resolveAttribute(attributeId, typedValue, true);
    	
    	int pixelValue = DDGUtils.dpToPixel(getResources().getDisplayMetrics(), sizeInDp);
    	
    	Drawable xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        view.setCompoundDrawables(xt, null, null, null);
	}
	
	private void initialise() {
		
		Theme theme = getActivity().getTheme();
		
		historyAdapter = new MultiHistoryAdapter(getActivity());
		
    	leftHomeTextView = (TextView) contentView.findViewById(R.id.LeftHomeTextView);
    	leftStoriesTextView = (TextView) contentView.findViewById(R.id.LeftStoriesTextView);
    	leftSavedTextView = (TextView) contentView.findViewById(R.id.LeftSavedTextView);
    	leftSettingsTextView = (TextView) contentView.findViewById(R.id.LeftSettingsTextView);
    	
    	leftHomeTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftStoriesTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftSavedTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
    	leftSettingsTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);    	
    	
    	
    	float leftTitleTextSize = PreferencesManager.getLeftTitleTextSize() + DDGControlVar.diffPixel;
    	
    	leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTitleTextSize);
    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTitleTextSize);
    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTitleTextSize);
    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTitleTextSize); 
    	    	
    	leftHomeButtonLayout = (LinearLayout) contentView.findViewById(R.id.LeftHomeButtonLayout);
    	leftStoriesButtonLayout = (LinearLayout) contentView.findViewById(R.id.LeftStoriesButtonLayout);
    	leftSavedButtonLayout = (LinearLayout) contentView.findViewById(R.id.LeftSavedButtonLayout);
    	leftSettingsButtonLayout = (LinearLayout) contentView.findViewById(R.id.LeftSettingsButtonLayout);
    	
        setLeftDrawable(theme, leftHomeTextView, R.attr.leftDrawableHome, 20);        
        setLeftDrawable(theme, leftStoriesTextView, R.attr.leftDrawableStories, 20);
        setLeftDrawable(theme, leftSavedTextView, R.attr.leftDrawableSaved, 20);
        setLeftDrawable(theme, leftSettingsTextView, R.attr.leftDrawableSettings, 20);    	
        
    	leftHomeTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BusProvider.getInstance().post(new LeftHomeButtonClickEvent());			
			}
		});
    	
    	leftStoriesTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BusProvider.getInstance().post(new LeftStoriesButtonClickEvent());
			}
		});
    	leftSavedTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BusProvider.getInstance().post(new LeftSavedButtonClickEvent());
			}
		});
    	leftSettingsTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BusProvider.getInstance().post(new LeftSettingsButtonClickEvent());
			}
		});
    	
    	leftRecentView = (HistoryListView) contentView.findViewById(R.id.LeftRecentView);
		
		leftRecentView.setDivider(null);
    	leftRecentView.setAdapter(historyAdapter);
    	
    	// "Save Recents" not enabled notification click listener
    	leftRecentView.setOnHeaderClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BusProvider.getInstance().post(new RecentHeaderClickEvent());
			}
		});
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        contentView = inflater.inflate(R.layout.left_layout, container, false);
		initialise();
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
	
	private void clearLeftSelect() {
		leftHomeTextView.setSelected(false);
		leftSavedTextView.setSelected(false);
		leftSettingsTextView.setSelected(false);
		leftStoriesTextView.setSelected(false);
	}
	
	private void markLeftSelect(SCREEN current){
		if(DDGControlVar.START_SCREEN == current) {
			leftHomeTextView.setSelected(true);
		}
		else {
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
		
	@Subscribe
	public void onFontSizeChange(FontSizeChangeEvent event) {
		historyAdapter.notifyDataSetInvalidated();
		
		float updatedSize = PreferencesManager.getLeftTitleTextSize() + event.diffPixel;
		leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, updatedSize);
    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, updatedSize);
    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, updatedSize);
    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, updatedSize);
    	contentView.invalidate();
	}
		
	@Subscribe
	public void onFontSizeCancel(FontSizeCancelEvent event) {
		historyAdapter.notifyDataSetInvalidated();		
		
		leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, PreferencesManager.getLeftTitleTextSize());
    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, PreferencesManager.getLeftTitleTextSize());
    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, PreferencesManager.getLeftTitleTextSize());
    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, PreferencesManager.getLeftTitleTextSize());
    	contentView.invalidate();
	}
	
	@Subscribe
	public void onUpdateVisibility(UpdateVisibilityEvent event) {
		clearLeftSelect();
		markLeftSelect(event.screen);
		
		if(event.screen == SCREEN.SCR_STORIES) {
	    	// adjust "not recording" indicator
			leftRecentView.displayRecordHistoryDisabled();
		}
		
		if(DDGControlVar.START_SCREEN == SCREEN.SCR_RECENT_SEARCH &&
				event.screen != SCREEN.SCR_RECENT_SEARCH) {
        	leftRecentView.setVisibility(View.VISIBLE);
		}
	}
	
	@Subscribe
	public void onResetScreen(ResetScreenStateEvent event) {
		changeLeftMenuVisibility();
	}
	
	@Subscribe
	public void onSyncAdapters(SyncAdaptersEvent event) {
		historyAdapter.sync();
	}
	
}
