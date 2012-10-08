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

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.SCREEN;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final int CONFIRM_CLEAR_HISTORY = 100;
	private boolean hasClearedHistory = false; 
	
  @SuppressWarnings("deprecation")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
    	addPreferencesFromResource(R.xml.preferences);
    	getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    	Preference clearHistoryPref = (Preference) findPreference("clearHistoryPref");
    	clearHistoryPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
    		public boolean onPreferenceClick(Preference preference) {
    			showDialog(CONFIRM_CLEAR_HISTORY);		
    			return true;
    		}
    	});
    	
		Preference sourcesPref = (Preference) findPreference("sourcesPref");
		sourcesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(getBaseContext(), SourcePreferences.class);
		        startActivity(intent);
				
				return true;
			}
		});

		Preference resetSourcesPref = (Preference) findPreference("resetSourcesPref");
		resetSourcesPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				DDGUtils.deleteSet(DDGApplication.getSharedPreferences(), "sourceset");
				DDGControlVar.hasUpdatedFeed = false;
				DDGControlVar.useDefaultSources = true;
				DDGControlVar.hasUpdatedSources = false;
				
				return true;
			}
		});
		
    }
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
					  
					  DDGUtils.deleteSet(DDGApplication.getSharedPreferences(), "recentsearch");		    			
		    		  hasClearedHistory = true;
					  
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
	  res.putExtra("hasClearedHistory", hasClearedHistory);
	  setResult(RESULT_OK, res);
	  super.finish();
  }

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals("startScreenPref")){
			DDGControlVar.START_SCREEN = SCREEN.getByCode(Integer.valueOf(sharedPreferences.getString(key, "0")));
		}
		else if(key.equals("regionPref")){
			DDGControlVar.regionString = sharedPreferences.getString(key, "wt-wt");
		}

	}
	
}
