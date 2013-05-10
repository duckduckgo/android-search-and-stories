package com.duckduckgo.mobile.android.dialogs.menuDialogs;

import android.app.AlertDialog;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.MainFeedMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.SavedFeedMenuAdapter;
import com.duckduckgo.mobile.android.listener.ExecuteActionOnClickListener;
import com.duckduckgo.mobile.android.objects.FeedObject;


/*
Shows a dialog to alert the user the feedrequest failed, asking him to try again.
 */
public final class SavedFeedMenuDialog extends AlertDialog.Builder{
	public SavedFeedMenuDialog(final DuckDuckGo context, FeedObject feedObject) {
		super(context);

        final PageMenuContextAdapter contextAdapter = new SavedFeedMenuAdapter(context, android.R.layout.select_dialog_item, android.R.id.text1, feedObject);
        setTitle(R.string.StoryOptionsTitle);
        setAdapter(contextAdapter, new ExecuteActionOnClickListener(contextAdapter));
	}
}
