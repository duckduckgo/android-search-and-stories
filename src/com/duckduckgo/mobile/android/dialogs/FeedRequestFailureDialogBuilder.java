package com.duckduckgo.mobile.android.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.DDGControlVar;


/*
Shows a dialog to alert the user the feedrequest failed, asking him to try again.
 */
public final class FeedRequestFailureDialogBuilder extends AlertDialog.Builder{
	public FeedRequestFailureDialogBuilder(final DuckDuckGo context) {
		super(context);
        setTitle(R.string.ErrorFeedTitle);
        setMessage(R.string.ErrorFeedDetail);
        setPositiveButton(R.string.Continue, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        setNegativeButton(R.string.Retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DDGControlVar.hasUpdatedFeed = false;
                context.keepFeedUpdated();
            }
        });
	}
}
