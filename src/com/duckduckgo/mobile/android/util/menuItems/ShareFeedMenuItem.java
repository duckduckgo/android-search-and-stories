package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.events.shareEvents.ShareFeedEvent;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;
import com.duckduckgo.mobile.android.util.Sharer;

public class ShareFeedMenuItem extends Item {
	
	public ShareFeedMenuItem(DuckDuckGo context, String title, String url){
		super(context.getResources().getString(R.string.Share), android.R.drawable.ic_menu_share, ItemType.SHARE);
		this.EventToFire = new ShareFeedEvent(context, title, url);
	}
	
}
