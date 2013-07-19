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
    	
    	
    	TypedValue tmpTypedValue = new TypedValue(); 
    	theme.resolveAttribute(R.attr.leftButtonTextSize, tmpTypedValue, true);
    	// XXX getDimension returns in PIXELS !
    	float defLeftTitleTextSize = tmpTypedValue.getDimension(getResources().getDisplayMetrics());    	
    	DDGControlVar.leftTitleTextSize = PreferencesManager.getLeftTitleTextSize(defLeftTitleTextSize);
    	
    	leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize); 
    	    	
    	leftHomeButtonLayout = (LinearLayout) contentView.findViewById(R.id.LeftHomeButtonLayout);
    	leftStoriesButtonLayout = (LinearLayout) contentView.findViewById(R.id.LeftStoriesButtonLayout);
    	leftSavedButtonLayout = (LinearLayout) contentView.findViewById(R.id.LeftSavedButtonLayout);
    	leftSettingsButtonLayout = (LinearLayout) contentView.findViewById(R.id.LeftSettingsButtonLayout);
    	
    	
    	int pixelValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) 20.0, getResources().getDisplayMetrics());
    	
    	TypedValue typedValue = new TypedValue(); 
    	theme.resolveAttribute(R.attr.leftDrawableHome, typedValue, true);
    	
    	Drawable xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftHomeTextView.setCompoundDrawables(xt, null, null, null);
        
        theme.resolveAttribute(R.attr.leftDrawableStories, typedValue, true);
    	xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftStoriesTextView.setCompoundDrawables(xt, null, null, null);        
        
        theme.resolveAttribute(R.attr.leftDrawableSaved, typedValue, true);
    	xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftSavedTextView.setCompoundDrawables(xt, null, null, null);
        
        theme.resolveAttribute(R.attr.leftDrawableSettings, typedValue, true);
    	xt = getResources().getDrawable(typedValue.resourceId);
        xt.setBounds(0, 0, pixelValue, pixelValue);
        leftSettingsTextView.setCompoundDrawables(xt, null, null, null);
    	
        
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
		DDGControlVar.leftTitleTextSize = DDGControlVar.prevLeftTitleTextSize + event.diffPixel;
		
		leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	contentView.invalidate();
	}
		
	@Subscribe
	public void onFontSizeCancel(FontSizeCancelEvent event) {
		DDGControlVar.leftTitleTextSize = DDGControlVar.prevLeftTitleTextSize;
		historyAdapter.notifyDataSetInvalidated();		
		DDGControlVar.prevLeftTitleTextSize = 0;
		
		leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
    	leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
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
