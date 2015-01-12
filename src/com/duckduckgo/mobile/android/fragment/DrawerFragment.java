package com.duckduckgo.mobile.android.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.MultiHistoryAdapter;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.SetMainButtonHomeEvent;
import com.duckduckgo.mobile.android.events.SetMainButtonMenuEvent;
import com.duckduckgo.mobile.android.events.fontSizeEvents.FontSizeCancelScalingEvent;
import com.duckduckgo.mobile.android.events.fontSizeEvents.FontSizeOnProgressChangedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuChangeVisibilityEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuClearSelectEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuHistoryDisabledEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuHomeClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuMarkSelectedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSavedClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetAdapterEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetHomeSelectedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetRecentVisibleEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetSavedSelectedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSetStoriesSelectedEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuSettingsClickEvent;
import com.duckduckgo.mobile.android.events.leftMenuEvents.LeftMenuStoriesClickEvent;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SCREEN;
import com.duckduckgo.mobile.android.views.HistoryListView;
import com.squareup.otto.Subscribe;

public class DrawerFragment extends Fragment implements View.OnClickListener {

	public static final String TAG = "drawer_fragment";

	private HistoryListView leftRecentView = null;

	private TextView leftHomeTextView = null;
	private TextView leftStoriesTextView = null;
	private TextView leftSavedTextView = null;
	private TextView leftSettingsTextView = null;

	private LinearLayout leftHomeButtonLayout = null;
	private LinearLayout leftStoriesButtonLayout = null;
	private LinearLayout leftSavedButtonLayout = null;
	private LinearLayout leftSettingsButtonLayout = null;

	private View fragmentView;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.fragment_drawer, container, false);
		init();
		return fragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void init() {
		leftHomeTextView = (TextView) fragmentView.findViewById(R.id.LeftHomeTextView);
		leftStoriesTextView = (TextView) fragmentView.findViewById(R.id.LeftStoriesTextView);
		leftSavedTextView = (TextView) fragmentView.findViewById(R.id.LeftSavedTextView);
		leftSettingsTextView = (TextView) fragmentView.findViewById(R.id.LeftSettingsTextView);

		leftHomeTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
		leftStoriesTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
		leftSavedTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);
		leftSettingsTextView.setTypeface(DDGConstants.TTF_ROBOTO_MEDIUM);

		DDGControlVar.leftTitleTextSize = PreferencesManager.getLeftTitleTextSize();

		leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);

		leftHomeButtonLayout = (LinearLayout) fragmentView.findViewById(R.id.LeftHomeButtonLayout);
		leftStoriesButtonLayout = (LinearLayout) fragmentView.findViewById(R.id.LeftStoriesButtonLayout);
		leftSavedButtonLayout = (LinearLayout) fragmentView.findViewById(R.id.LeftSavedButtonLayout);
		leftSettingsButtonLayout = (LinearLayout) fragmentView.findViewById(R.id.LeftSettingsButtonLayout);


		int pixelValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				(float) 20.0, getResources().getDisplayMetrics());

		TypedValue typedValue = new TypedValue();
		getActivity().getTheme().resolveAttribute(R.attr.leftDrawableHome, typedValue, true);

		Drawable xt = getResources().getDrawable(typedValue.resourceId);
		xt.setBounds(0, 0, pixelValue, pixelValue);
		leftHomeTextView.setCompoundDrawables(xt, null, null, null);

		getActivity().getTheme().resolveAttribute(R.attr.leftDrawableStories, typedValue, true);
		xt = getResources().getDrawable(typedValue.resourceId);
		xt.setBounds(0, 0, pixelValue, pixelValue);
		leftStoriesTextView.setCompoundDrawables(xt, null, null, null);

		getActivity().getTheme().resolveAttribute(R.attr.leftDrawableSaved, typedValue, true);
		xt = getResources().getDrawable(typedValue.resourceId);
		xt.setBounds(0, 0, pixelValue, pixelValue);
		leftSavedTextView.setCompoundDrawables(xt, null, null, null);

		getActivity().getTheme().resolveAttribute(R.attr.leftDrawableSettings, typedValue, true);
		xt = getResources().getDrawable(typedValue.resourceId);
		xt.setBounds(0, 0, pixelValue, pixelValue);
		leftSettingsTextView.setCompoundDrawables(xt, null, null, null);

		leftHomeTextView.setOnClickListener(this);
		leftStoriesTextView.setOnClickListener(this);
		leftSavedTextView.setOnClickListener(this);
		leftSettingsTextView.setOnClickListener(this);

		leftRecentView = (HistoryListView) fragmentView.findViewById(R.id.LeftRecentView);

		leftRecentView.setDivider(null);

		// "Save Recents" not enabled notification click listener
		leftRecentView.setOnHeaderClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				BusProvider.getInstance().post(new LeftMenuSettingsClickEvent());
			}
		});
	}

	public void onClick(View view) {
		if(view.equals(leftHomeTextView)){
			BusProvider.getInstance().post(new LeftMenuHomeClickEvent());
		}
		else if(view.equals(leftStoriesTextView)){
			BusProvider.getInstance().post(new LeftMenuStoriesClickEvent());
		}
		else if(view.equals(leftSavedTextView)){
			BusProvider.getInstance().post(new LeftMenuSavedClickEvent());
		}
		else if(view.equals(leftSettingsTextView)){
			BusProvider.getInstance().post(new LeftMenuSettingsClickEvent());
		}
	}

	private void clearLeftSelect() {
		leftHomeTextView.setSelected(false);
		leftSavedTextView.setSelected(false);
		leftSettingsTextView.setSelected(false);
		leftStoriesTextView.setSelected(false);
	}

	/**
	 * change button visibility in left-side navigation menu
	 * according to screen
	 */
	private void changeLeftMenuVisibility() {
		if(DDGControlVar.START_SCREEN != SCREEN.SCR_STORIES) {
			leftStoriesButtonLayout.setVisibility(View.VISIBLE);
		}
		else {
	    	leftStoriesButtonLayout.setVisibility(View.GONE);
		}

		// saved button
		//if(DDGControlVar.START_SCREEN != SCREEN.SCR_SAVED_FEED) {//aaa
        if(DDGControlVar.START_SCREEN != SCREEN.SCR_SAVED) {
			leftSavedButtonLayout.setVisibility(View.VISIBLE);
		}
		else {
			leftSavedButtonLayout.setVisibility(View.GONE);
		}

		// recent search button
    	//if(DDGControlVar.START_SCREEN != SCREEN.SCR_RECENT_SEARCH) {//aaa
        if(DDGControlVar.START_SCREEN != SCREEN.SCR_RECENTS) {
        	leftRecentView.setVisibility(View.VISIBLE);
    	}
    	else {
        	leftRecentView.setVisibility(View.GONE);
    	}
	}

	private void markLeftSelect(SCREEN current){
		if(DDGControlVar.START_SCREEN == current) {
			leftHomeTextView.setSelected(true);

			if(DDGControlVar.mDuckDuckGoContainer.webviewShowing){
				BusProvider.getInstance().post(new SetMainButtonHomeEvent());
			}
			else {
				BusProvider.getInstance().post(new SetMainButtonMenuEvent());
			}
		}
		else {
			BusProvider.getInstance().post(new SetMainButtonHomeEvent());
			switch(current) {
				case SCR_STORIES:
					leftStoriesTextView.setSelected(true);
					break;
				//case SCR_SAVED_FEED://aaa
                case SCR_SAVED:
					leftSavedTextView.setSelected(true);
					break;
			}
		}
	}

	@Subscribe
	public void onLeftMenuChangeVisibilityEvent(LeftMenuChangeVisibilityEvent event) {
		changeLeftMenuVisibility();
	}

	@Subscribe
	public void onLeftMenuSetAdapterEvent(LeftMenuSetAdapterEvent event) {
		leftRecentView.setAdapter(DDGControlVar.mDuckDuckGoContainer.historyAdapter);
	}

	@Subscribe
	public void onLeftMenuClearSelectEvent(LeftMenuClearSelectEvent event) {
		clearLeftSelect();
	}

	@Subscribe
	public void onLeftMenuMarkSelectedEvent(LeftMenuMarkSelectedEvent event) {
		markLeftSelect(event.current);
	}

	@Subscribe
	public void onLeftMenuSetRecentVisibleEvent(LeftMenuSetRecentVisibleEvent event) {
		leftRecentView.setVisibility(View.VISIBLE);
	}

	@Subscribe
	public void onLeftMenuHistoryDisabledEvent(LeftMenuHistoryDisabledEvent event) {
		leftRecentView.displayRecordHistoryDisabled();
	}

	@Subscribe
	public void onLeftMenuSetHomeSelectedEvent(LeftMenuSetHomeSelectedEvent event) {
		leftHomeTextView.setSelected(event.selected);
	}

	@Subscribe
	public void onLeftMenuSetStoriesSelectedEvent(LeftMenuSetStoriesSelectedEvent event) {
		leftStoriesTextView.setSelected(event.selected);
	}

	@Subscribe
	public void onLeftMenuSetSavedSelectedEvent(LeftMenuSetSavedSelectedEvent event) {
		leftSavedTextView.setSelected(event.selected);
	}

	@Subscribe
	public void onFontSizeOnProgressChangedEvent(FontSizeOnProgressChangedEvent event) {
		DDGControlVar.mDuckDuckGoContainer.historyAdapter.notifyDataSetInvalidated();
		leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		fragmentView.invalidate();
	}

	@Subscribe
	public void onFontSizeCancelScalingEvent(FontSizeCancelScalingEvent event) {
		DDGControlVar.mDuckDuckGoContainer.historyAdapter.notifyDataSetInvalidated();
		leftHomeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		leftStoriesTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		leftSavedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		leftSettingsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.leftTitleTextSize);
		fragmentView.invalidate();
	}
}
