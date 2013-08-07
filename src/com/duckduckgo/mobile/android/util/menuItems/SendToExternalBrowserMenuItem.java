package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.events.externalEvents.SendToExternalBrowserEvent;
import com.duckduckgo.mobile.android.util.Item;

public class SendToExternalBrowserMenuItem extends Item {

	public SendToExternalBrowserMenuItem(DuckDuckGo context, String url){
		super(context.getResources().getString(R.string.OpenInExternalBrowser), android.R.drawable.ic_menu_rotate, ItemType.EXTERNAL);
		this.EventToFire = new SendToExternalBrowserEvent(context, url);
	}
	
}
