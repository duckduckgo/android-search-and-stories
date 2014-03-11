package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteUrlInHistoryEvent;
import com.duckduckgo.mobile.android.util.Item;

public class DeleteUrlInHistoryMenuItem extends Item {
	
	public DeleteUrlInHistoryMenuItem(DuckDuckGo context, String pageData, String pageUrl){
		super(context.getResources().getString(R.string.Delete), android.R.drawable.ic_menu_close_clear_cancel, ItemType.DELETE);
		this.EventToFire = new DeleteUrlInHistoryEvent(pageData, pageUrl);
	}
	
}
