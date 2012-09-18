package com.duckduckgo.mobile.android.activity;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.fragment.ConfirmClearHistoryDialog;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SCREEN;

@TargetApi(11)
public class DDGPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

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
		
		Preference resetSourcesPref = (Preference) findPreference("resetSourcesPref");
		resetSourcesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				DDGUtils.deleteSet(DDGApplication.getSharedPreferences(), "sourceset");
				DDGControlVar.hasUpdatedFeed = false;
				
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
        	if(ddg.mDuckDuckGoContainer.prefShowing){
        		ddg.showPrefFragment();
        	}
        }
        
    }
	
}