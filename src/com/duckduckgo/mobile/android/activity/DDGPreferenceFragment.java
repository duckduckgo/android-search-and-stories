package com.duckduckgo.mobile.android.activity;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.fragment.ConfirmClearHistoryDialog;
import com.duckduckgo.mobile.android.listener.PreferenceChangeListener;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SCREEN;

@TargetApi(11)
public class DDGPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	OnPreferenceClickListener customListener = null;
	PreferenceChangeListener customChangeListener = null;
	
	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
		Preference clearHistoryPref = (Preference) findPreference("clearHistoryPref");
		clearHistoryPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		    public boolean onPreferenceClick(Preference preference) {		    	
		    	showClearHistoryConfirm();		    	
		    	return true;
		    }
		});
		
		Preference sourcesPref = (Preference) findPreference("sourcesPref");
		sourcesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(getActivity(), SourcePreferences.class);
		        startActivity(intent);
				
				return true;
			}
		});
		
		Preference sendFeedbackPref = (Preference) findPreference("sendFeedbackPref");
		sendFeedbackPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Context context = getPreferenceScreen().getContext();
				
				Intent intent = DDGUtils.newEmailIntent(context.getResources().getString(R.string.FeedbackTo), 
						context.getResources().getString(R.string.FeedbackSubject), DDGUtils.getBuildInfo(context), "");
		        startActivity(Intent.createChooser(intent, "Select application to send"));
				return true;
			}
		});
		
		Preference ratePref = (Preference) findPreference("ratePref");
		ratePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.duckduckgo.mobile.android"));
				startActivity(intent);
				return true;
			}
		});
	}
	
	@TargetApi(11)
	private void showClearHistoryConfirm() {
        FragmentManager fm = getFragmentManager();
        final ConfirmClearHistoryDialog confirmDialog = new ConfirmClearHistoryDialog();
        OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
    			DDGUtils.deleteSet(DDGApplication.getSharedPreferences(), "recentsearch");
		    	
		    	if(getActivity().getClass() == DuckDuckGo.class){
		    		DuckDuckGo ddgParent = (DuckDuckGo) getActivity();
		    		ddgParent.clearRecentSearch();
		    	}	
		    	
		    	confirmDialog.dismiss();
			}
		};
		confirmDialog.setOKListener(listener);
		confirmDialog.show(fm, "fragment_clear_history");
    }
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals("startScreenPref")){
			DDGControlVar.START_SCREEN = SCREEN.getByCode(Integer.valueOf(sharedPreferences.getString(key, "0")));
		}
		else if(key.equals("regionPref")){
			DDGControlVar.regionString = sharedPreferences.getString(key, "wt-wt");
		}
		else if(key.equals("appSearchPref")){
			DDGControlVar.includeAppsInSearch = sharedPreferences.getBoolean(key, false);
		}
		else if(key.equals("externalBrowserPref")){
			DDGControlVar.alwaysUseExternalBrowser = sharedPreferences.getBoolean(key, false);
		}
		else if(key.equals("turnOffAutocompletePref")){
			DDGControlVar.isAutocompleteActive = !sharedPreferences.getBoolean(key, false);
		}
		
		if(customChangeListener != null) {
			customChangeListener.onPreferenceChange(key);
		}

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	    View v = super.onCreateView(inflater, container, savedInstanceState);
	    if(v != null) {
	        ListView lv = (ListView) v.findViewById(android.R.id.list);
	        lv.setPadding(0, 0, 0, 0);
	    }
	    return v;
	}
	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() instanceof DuckDuckGo){
        	DuckDuckGo ddg = (DuckDuckGo) getActivity();
        	if(ddg.mDuckDuckGoContainer.currentScreen == SCREEN.SCR_SETTINGS){
        		ddg.showPrefFragment();
        	}
        }
        
    }
	
	public void setCustomPreferenceClickListener(OnPreferenceClickListener listener) {
		customListener = listener;		
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if(customListener != null) {
			customListener.onPreferenceClick(preference);
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
	public void setCustomPreferenceChangeListener(PreferenceChangeListener listener) {
		customChangeListener = listener;
	}
	
}