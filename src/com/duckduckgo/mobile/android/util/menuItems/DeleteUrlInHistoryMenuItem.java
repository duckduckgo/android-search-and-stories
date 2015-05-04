package com.duckduckgo.mobile.android.util.menuItems;

import android.content.Context;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.events.deleteEvents.DeleteUrlInHistoryEvent;
import com.duckduckgo.mobile.android.util.Item;

public class DeleteUrlInHistoryMenuItem extends Item {
	
	public DeleteUrlInHistoryMenuItem(Context context, String pageData, String pageUrl){
		//super(context.getResources().getString(R.string.Delete), android.R.drawable.ic_menu_close_clear_cancel, ItemType.DELETE);
        super(context.getResources().getString(R.string.Delete), android.R.drawable.ic_menu_close_clear_cancel, ItemType.SAVE);
		this.EventToFire = new DeleteUrlInHistoryEvent(pageData, pageUrl);
	}
	
}
