package com.duckduckgo.mobile.android.dialogs.menuDialogs;

import android.app.AlertDialog;
import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.SavedSearchMenuAdapter;
import com.duckduckgo.mobile.android.adapters.menuAdapters.WebViewQueryMenuAdapter;
import com.duckduckgo.mobile.android.listener.ExecuteActionOnClickListener;
import com.duckduckgo.mobile.android.util.DDGUtils;


/*
Shows a dialog to alert the user the feedrequest failed, asking him to try again.
 */
public final class WebViewQueryMenuDialog extends AlertDialog.Builder{
	public WebViewQueryMenuDialog(final DuckDuckGo context, String webViewUrl) {
		super(context);

        final String query = DDGUtils.getQueryIfSerp(webViewUrl);
        final boolean isPageSaved = DDGApplication.getDB().isSavedSearch(query);
        final PageMenuContextAdapter contextAdapter  = new WebViewQueryMenuAdapter(context, android.R.layout.select_dialog_item, android.R.id.text1,
                query, isPageSaved);

        setTitle(R.string.SearchOptionsTitle);
        setAdapter(contextAdapter, new ExecuteActionOnClickListener(contextAdapter));
	}
}
