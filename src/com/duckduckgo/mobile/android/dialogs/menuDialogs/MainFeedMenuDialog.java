package com.duckduckgo.mobile.android.dialogs.menuDialogs;

import android.app.AlertDialog;
import android.content.Context;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.MainFeedMenuAdapter;
import com.duckduckgo.mobile.android.listener.ExecuteActionOnClickListener;
import com.duckduckgo.mobile.android.objects.FeedObject;


/*
Shows a dialog to alert the user the feedrequest failed, asking him to try again.
 */
public final class MainFeedMenuDialog extends AlertDialog.Builder{
	public MainFeedMenuDialog(final Context context, FeedObject feedObject) {
		super(context);

        final PageMenuContextAdapter contextAdapter = new MainFeedMenuAdapter(context, R.layout.temp_dialog_item,//android.R.layout.select_dialog_item,
                android.R.id.text1, feedObject);
        //setTitle(R.string.StoryOptionsTitle);
        setAdapter(contextAdapter, new ExecuteActionOnClickListener(contextAdapter));
	}
}
