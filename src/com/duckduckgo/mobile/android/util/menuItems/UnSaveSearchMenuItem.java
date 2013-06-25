package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.events.saveEvents.UnSaveSearchEvent;
import com.duckduckgo.mobile.android.util.Item;

public class UnSaveSearchMenuItem extends Item {

	public UnSaveSearchMenuItem(DuckDuckGo context, String query){
		super(context.getResources().getString(R.string.Unsave), android.R.drawable.ic_menu_delete, ItemType.UNSAVE);
		this.EventToFire = new UnSaveSearchEvent(query);
	}
	
}
