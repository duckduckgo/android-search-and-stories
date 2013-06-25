package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.events.saveEvents.SaveSearchEvent;
import com.duckduckgo.mobile.android.util.Item;

public class SaveSearchMenuItem extends Item {

	public SaveSearchMenuItem(DuckDuckGo context, String pageData){
		super(context.getResources().getString(R.string.Save), android.R.drawable.ic_menu_save, ItemType.SAVE);		
		EventToFire = new SaveSearchEvent(pageData);
	}
	
}
