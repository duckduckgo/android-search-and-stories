package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;
import com.duckduckgo.mobile.android.util.Sharer;

public class ShareFeedMenuItem extends Item {
	private final String title;
	private final String url;
	private final DuckDuckGo context;
	
	public ShareFeedMenuItem(DuckDuckGo context, String title, String url){
		super(context.getResources().getString(R.string.Share), android.R.drawable.ic_menu_share, ItemType.SHARE);
		this.context = context;
		this.title = title;
		this.url = url;
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
				Sharer.shareStory(context, title, url);
			};
		};
	}
}
