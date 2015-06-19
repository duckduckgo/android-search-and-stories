package com.duckduckgo.mobile.android.dialogs.menuDialogs;

import android.app.AlertDialog;
import android.content.Context;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.SavedSearchMenuAdapter;
import com.duckduckgo.mobile.android.listener.ExecuteActionOnClickListener;


/*
Shows a dialog to alert the user the feedrequest failed, asking him to try again.
 */
public final class SavedSearchMenuDialog extends AlertDialog.Builder{
	public SavedSearchMenuDialog(final Context context, String query) {
		super(context);

        final PageMenuContextAdapter contextAdapter = new SavedSearchMenuAdapter(context, R.layout.item_dialog/*android.R.layout.select_dialog_item*/, android.R.id.text1, query);
        //setTitle(R.string.SearchOptionsTitle);
        setAdapter(contextAdapter, new ExecuteActionOnClickListener(contextAdapter));
	}
}
