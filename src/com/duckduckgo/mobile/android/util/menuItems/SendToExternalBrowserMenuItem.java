package com.duckduckgo.mobile.android.util.menuItems;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;

public class SendToExternalBrowserMenuItem extends Item {
	private DuckDuckGo context;
	private String url;

	public SendToExternalBrowserMenuItem(DuckDuckGo context, String url){
		super(context.getResources().getString(R.string.OpenInExternalBrowser), android.R.drawable.ic_menu_rotate, ItemType.EXTERNAL);
		this.context = context;
		this.url = url;
		
		this.ActionToExecute = getActionToExecute();
	}
	
	/** 
	 * we're actually going to call this directly instead of the public field ActionToExecute, after more refactoring
	 * @return
	 */
	public Action getActionToExecute() {
		return new Action() {
			@Override
			public void Execute() {
				try {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					context.startActivity(browserIntent);
				}
				catch(ActivityNotFoundException e) {
            		Toast.makeText(context, R.string.ErrorActivityNotFound, Toast.LENGTH_SHORT).show();
            	}
			};
		};
	}
}
