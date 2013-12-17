/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.duckduckgo.mobile.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.container.DuckDuckGoContainer;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.TorIntegration;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final int CONFIRM_CLEAR_HISTORY = 100;
    private final TorIntegration torIntegration;
    private boolean result_hasClearedHistory = false;
    private boolean result_startOrbotCheck = false;
    private boolean result_switchTheme = false;

    public Preferences() {
        this.torIntegration = new TorIntegration(this);
    }

    private boolean isDarkTheme(String themeName){
        return themeName.equals("DDGDark");
    }

    private void setHoloTheme(String ddgThemeName){
        if(isDarkTheme(ddgThemeName)){
            setTheme(android.R.style.Theme_Holo);
        }else{
            setTheme(android.R.style.Theme_Holo_Light);
        }
    }
	
  @SuppressWarnings("deprecation")
  @Override
  public void onCreate(Bundle savedInstanceState) {
      setHoloTheme(PreferencesManager.getThemeName());
      super.onCreate(savedInstanceState);



      addPreferencesFromResource(R.xml.preferences);
      getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

      Preference clearHistoryPref = findPreference("clearHistoryPref");
      clearHistoryPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
          public boolean onPreferenceClick(Preference preference) {
              showDialog(CONFIRM_CLEAR_HISTORY);
              return true;
          }
      });

      Preference sourcesPref = findPreference("sourcesPref");
      sourcesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

          public boolean onPreferenceClick(Preference preference) {
              Intent intent = new Intent(getBaseContext(), SourcePreferences.class);
              startActivity(intent);

              return true;
          }
      });


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

      Preference ratePref = findPreference("ratePref");
      ratePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

          public boolean onPreferenceClick(Preference preference) {
              Intent intent = new Intent(Intent.ACTION_VIEW);
              intent.setData(Uri.parse(getString(R.string.LinkToApp_Google)));
              startActivity(intent);
              return true;
          }
      });

      Preference mainFontSizePref = findPreference("mainFontSizePref");
      mainFontSizePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

          public boolean onPreferenceClick(Preference preference) {
              PreferencesManager.setFontSliderVisibility(true);
              finish();
              return true;
          }
      });
      whenChangingTorChecksForOrbot();
      whenCheckingOrbotStatusStartsOrbotAndSetsProxy();
      whenSwitchingThemesRestartsDDGActivity();
  }

    private void whenSwitchingThemesRestartsDDGActivity() {
        Preference checkOrbotPreference = findPreference("themePref");
        checkOrbotPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                result_switchTheme = true;
                Intent intent = new Intent(getApplicationContext(), Preferences.class);
                finish();
                startActivity(intent);
                return true;
            }
        });
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
                    result_startOrbotCheck = true;
                    finish();
                }
                return true;
            }
        });
    }

    private void whenChangingTorChecksForOrbot() {
        Preference enableTorPreference = findPreference("enableTor");
        if(!torIntegration.isTorSupported()){
            setTorNotSupportedInfo(enableTorPreference);
        }else{
            enableTorPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    return torIntegration.prepareTorSettings((Boolean) newValue);
                }
            });
        }
    }

    private void setTorNotSupportedInfo(Preference enableTorPreference) {
        enableTorPreference.setEnabled(false);
        enableTorPreference.setSummary("Tor is currently not supported in Android 4.4 due to changes in the WebView implementation.");
    }

    @Override
  public Dialog onCreateDialog(int id) {
	  Dialog d;
	  switch(id) {
		  case CONFIRM_CLEAR_HISTORY:
			  d = new AlertDialog.Builder(this)
			  .setTitle(getResources().getString(R.string.Confirm))
			  .setMessage(getResources().getString(R.string.ConfirmClearHistory))
			  .setIcon(android.R.drawable.ic_dialog_alert)
			  .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	
				  public void onClick(DialogInterface dialog, int whichButton) {
					  DDGApplication.getDB().deleteHistory();
                      result_hasClearedHistory = true;
				  }})
				  .setNegativeButton(android.R.string.no, new OnClickListener() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						removeDialog(Preferences.CONFIRM_CLEAR_HISTORY);
						
					}
				}).create();
	
			  break;
		  default:
			  d = null;
	  }
	  return d;
  }
  
  @Override
  public void finish() {
      Intent res = new Intent();
      res.putExtra("startOrbotCheck", result_startOrbotCheck);
      res.putExtra("hasClearedHistory", result_hasClearedHistory);
      res.putExtra("switchTheme", result_switchTheme);
      setResult(RESULT_OK, res);
      super.finish();
  }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PreferencesManager.onSharedPreferenceChanged(sharedPreferences, key);
    }
	
}
