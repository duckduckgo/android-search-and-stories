package com.duckduckgo.mobile.android.dialogs;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.DisplayScreenEvent;
import com.duckduckgo.mobile.android.util.SCREEN;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public final class NewSourcesDialogBuilder extends AlertDialog.Builder{

	private final Context context;

	public NewSourcesDialogBuilder(final Context context) {
		super(context);
		this.context = context;
		setTitle(R.string.NewSources);
		setPositiveButton(R.string.NewSourcesOk, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
                   BusProvider.getInstance().post(new DisplayScreenEvent(SCREEN.SCR_SOURCES, false));
	           }
	       });
		setNegativeButton(R.string.NewSourcesNoThx, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   // user said no.
	           }
	       });
	}
}
