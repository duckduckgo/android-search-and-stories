package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.events.shareEvents.ShareWebPageEvent;
import com.duckduckgo.mobile.android.util.Item;

public class ShareWebPageMenuItem extends Item {
	
	public ShareWebPageMenuItem(DuckDuckGo context, String pageData, String pageUrl){
		super(context.getResources().getString(R.string.Share), android.R.drawable.ic_menu_share, ItemType.SHARE);
		this.EventToFire = new ShareWebPageEvent(pageUrl, pageUrl);
	}

}
