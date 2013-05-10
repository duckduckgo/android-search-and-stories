package com.duckduckgo.mobile.android.dialogs.menuDialogs;

import android.app.AlertDialog;
import android.text.TextUtils;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.HistoryFeedMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.HistorySearchMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.SavedSearchMenuAdapter;
import com.duckduckgo.mobile.android.listener.ExecuteActionOnClickListener;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.util.builders.OptionsDialogBuilder;


/*
Shows a dialog to alert the user the feedrequest failed, asking him to try again.
 */
public final class HistoryMenuDialog extends AlertDialog.Builder{
	public HistoryMenuDialog(final DuckDuckGo context, HistoryObject historyObject) {
		super(context);

        final OptionsDialogBuilder alertDialogBuilder;
        final int titleId;
        final PageMenuContextAdapter contextAdapter;

        if(historyObject.isFeedObject() && TextUtils.isEmpty(historyObject.getFeedId())) {
            contextAdapter = new HistoryFeedMenuAdapter(context, android.R.layout.select_dialog_item, android.R.id.text1, historyObject);
            titleId = R.string.StoryOptionsTitle;
        }
        else{
            contextAdapter = new HistorySearchMenuAdapter(context, android.R.layout.select_dialog_item, android.R.id.text1, historyObject);
            titleId = R.string.SearchOptionsTitle;
        }
        alertDialogBuilder = new OptionsDialogBuilder(context, contextAdapter, titleId);
        alertDialogBuilder.show();
	}
}
