package com.duckduckgo.mobile.android.dialogs;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.activity.SourcePreferences;

public final class OpenInExternalDialogBuilder extends AlertDialog.Builder{

	public OpenInExternalDialogBuilder(final DuckDuckGo context, final String touchedUrl) {
		super(context);
        setTitle(context.getResources().getString(R.string.OpenInExternalBrowser));
        setMessage(context.getString(R.string.ConfirmExternalBrowser));
        setCancelable(false);
        setPositiveButton(context.getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            	try {
            		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(touchedUrl));
                	context.startActivity(browserIntent);
            	}
            	catch(ActivityNotFoundException e) {
            		Toast.makeText(context, R.string.ErrorActivityNotFound, Toast.LENGTH_SHORT).show();
            	}
            }
        });
        setNegativeButton(context.getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                dialog.dismiss();
            }
        });
        setIcon(android.R.drawable.ic_dialog_info);
	}
}
