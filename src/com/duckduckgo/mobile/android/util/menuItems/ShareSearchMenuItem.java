package com.duckduckgo.mobile.android.util.menuItems;

import android.content.Intent;
import android.widget.ShareActionProvider;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;
import com.duckduckgo.mobile.android.util.Sharer;

public class ShareSearchMenuItem extends Item {
	private final String query;
	private final DuckDuckGo context;
	
	public ShareSearchMenuItem(DuckDuckGo context, String query){
		super(context.getResources().getString(R.string.Share), android.R.drawable.ic_menu_share, ItemType.SHARE);
		this.context = context;
		this.query = query;
		
		this.ActionToExecute = getActionToExecute();		
	}

	/** 
	 * we're actually going to call this directly instead of the public field ActionToExecute, after more refactoring
	 * @return
	 */
	public Action getActionToExecute() {
		return new Action() {
			@Override
			public void Execute() {
				Sharer.shareSearch(context, query);
			};
		};
	}
}
