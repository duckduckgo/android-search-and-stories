package com.duckduckgo.mobile.android.util.menuItems;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.Item;

public class ShareMenuItem extends Item {
	private final String title;
	private final String url;
	private final DuckDuckGo context;
	
	public ShareMenuItem(DuckDuckGo context, String title, String url){
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
				DDGUtils.shareStory(context, title, url);
			};
		};
	}
}
