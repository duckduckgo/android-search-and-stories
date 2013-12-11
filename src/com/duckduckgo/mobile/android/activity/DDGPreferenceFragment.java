package com.duckduckgo.mobile.android.activity;

import com.duckduckgo.mobile.android.dialogs.OrbotStatusOkDialogBuilder;
import com.duckduckgo.mobile.android.util.TorIntegration;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.fragment.ConfirmClearHistoryDialog;
import com.duckduckgo.mobile.android.listener.PreferenceChangeListener;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SCREEN;

@TargetApi(11)
public class DDGPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    private final TorIntegration torIntegration;
    private final DuckDuckGo context;
    OnPreferenceClickListener customListener = null;
	PreferenceChangeListener customChangeListener = null;
	
	public DDGPreferenceFragment(TorIntegration torIntegration, DuckDuckGo context) {
		this.torIntegration = torIntegration;
        this.context = context;
    }

	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		whenClearingHistoryShowsClearHistoryConfirmDialog();
		whenChangingStorySourcesGoesToSourcePreferences();
		whenSendingFeedBackLaunchesEmailIntent();
		whenRatingGoesToMarket();
		whenTurningOffAutoCompleteSyncsOtherAutoCompletePreferences();
		whenChangingTorChecksForOrbot();
		whenCheckingOrbotStatusStartsOrbotAndSetsProxy();
	}

	private void whenCheckingOrbotStatusStartsOrbotAndSetsProxy() {
		Preference checkOrbotPreference = findPreference("checkOrbotStatus");
		checkOrbotPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
                if(!torIntegration.isOrbotRunningAccordingToSettings()){
                    torIntegration.prepareTorSettings();
                }
                else{
                    context.searchOrGoToUrl(getString(R.string.OrbotCheckSite));
                }
                return true;
            }
		});
	}

	private void whenChangingTorChecksForOrbot() {
		Preference enableTorPreference = findPreference("enableTor");
		enableTorPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return torIntegration.prepareTorSettings((Boolean) newValue);
            }
        });
	}

	private void whenClearingHistoryShowsClearHistoryConfirmDialog() {
		Preference clearHistoryPref = findPreference("clearHistoryPref");
		clearHistoryPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		    public boolean onPreferenceClick(Preference preference) {		    	
		    	showClearHistoryConfirm();		    	
		    	return true;
		    }
		});
	}

	private void whenChangingStorySourcesGoesToSourcePreferences() {
		Preference sourcesPref = findPreference("sourcesPref");
		sourcesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(getActivity(), SourcePreferences.class);
		        startActivity(intent);
				
				return true;
			}
		});
	}

	private void whenSendingFeedBackLaunchesEmailIntent() {
		Preference sendFeedbackPref = findPreference("sendFeedbackPref");
		sendFeedbackPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Context context = getPreferenceScreen().getContext();
				
				Intent intent = DDGUtils.newEmailIntent(context.getResources().getString(R.string.FeedbackTo), 
						context.getResources().getString(R.string.FeedbackSubject), DDGUtils.getBuildInfo(context), "");
		        startActivity(Intent.createChooser(intent, "Select application to send"));
				return true;
			}
		});
	}

	private void whenRatingGoesToMarket() {
		Preference ratePref = findPreference("ratePref");
		ratePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(getString(R.string.LinkToApp_Google)));
				startActivity(intent);
				return true;
			}
		});
	}

	private void whenTurningOffAutoCompleteSyncsOtherAutoCompletePreferences() {
		final Preference appSearchPreference = findPreference("appSearchPref");
		final Preference directQueryPreference = findPreference("directQueryPref");
		Preference turnOffAutoCompletePreference = findPreference("turnOffAutocompletePref");
		turnOffAutoCompletePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Boolean isTurnedOff = (Boolean)newValue;
				appSearchPreference.setEnabled(!isTurnedOff);
				directQueryPreference.setEnabled(!isTurnedOff);
				
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
				DDGApplication.getDB().deleteHistory();
		    	
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
        PreferencesManager.onSharedPreferenceChanged(sharedPreferences, key);
		
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
        		//ddg.showPrefFragment();
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