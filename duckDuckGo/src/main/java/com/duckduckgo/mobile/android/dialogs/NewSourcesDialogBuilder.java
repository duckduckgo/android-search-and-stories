package com.duckduckgo.mobile.android.dialogs;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.activity.SourcePreferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.duckduckgo.mobile.android.util.PreferencesManager;

public final class NewSourcesDialogBuilder extends AlertDialog.Builder{

	private final DuckDuckGo ddgContext;

	public NewSourcesDialogBuilder(DuckDuckGo context) {
		super(context);
		this.ddgContext = context;
		setTitle(R.string.NewSources);
		setPositiveButton(R.string.NewSourcesOk, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   Intent intent = new Intent(ddgContext, SourcePreferences.class);
	        	   ddgContext.startActivity(intent);
	           }
	       });
		setNegativeButton(R.string.NewSourcesNoThx, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   // user said no.
	           }
	       });
	}
}
