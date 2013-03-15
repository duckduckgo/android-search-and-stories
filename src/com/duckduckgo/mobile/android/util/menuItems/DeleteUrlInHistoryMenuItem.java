package com.duckduckgo.mobile.android.util.menuItems;

import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;
import com.duckduckgo.mobile.android.util.Item.ItemType;

public class DeleteUrlInHistoryMenuItem extends Item {
	private DuckDuckGo context;
	private String pageUrl;
	private String pageData;

	public DeleteUrlInHistoryMenuItem(DuckDuckGo context, String pageData, String pageUrl){
		super(context.getResources().getString(R.string.Delete), android.R.drawable.ic_menu_close_clear_cancel, ItemType.DELETE);
		this.context = context;
		this.pageData = pageData;
		this.pageUrl = pageUrl;
		
		ActionToExecute = getActionToExecute();
	}
	
	public Action getActionToExecute() {
		return new Action() {
			@Override
			public void Execute() {
				final long delHistory = DDGApplication.getDB().deleteHistoryByDataUrl(pageData, pageUrl);				
				if(delHistory != 0) {							
					context.syncAdapters();
				}	
				Toast.makeText(context, R.string.ToastDeleteUrlInHistory, Toast.LENGTH_SHORT).show();
			};
		};
	}
}
