package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.events.ReloadEvent;
import com.duckduckgo.mobile.android.util.Item;


public class ReloadMenuItem  extends Item {

	public ReloadMenuItem(DuckDuckGo context){
		super(context.getResources().getString(R.string.Refresh), R.drawable.icon_reload, ItemType.REFRESH);
		this.EventToFire = new ReloadEvent();
	}

}