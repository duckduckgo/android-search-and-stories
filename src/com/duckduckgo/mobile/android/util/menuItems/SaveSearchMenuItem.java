package com.duckduckgo.mobile.android.util.menuItems;

import android.content.Context;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.events.saveEvents.SaveSearchEvent;
import com.duckduckgo.mobile.android.util.Item;

public class SaveSearchMenuItem extends Item {

	public SaveSearchMenuItem(Context context, String pageData){
		super(context.getResources().getString(R.string.action_add_favorite), android.R.drawable.ic_menu_save, ItemType.SAVE);
		EventToFire = new SaveSearchEvent(pageData);
	}
	
}
