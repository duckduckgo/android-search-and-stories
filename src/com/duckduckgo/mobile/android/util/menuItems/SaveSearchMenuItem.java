package com.duckduckgo.mobile.android.util.menuItems;

import android.widget.Toast;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;

public class SaveSearchMenuItem extends Item {
	
	private DuckDuckGo context;
	private String pageData;

	public SaveSearchMenuItem(DuckDuckGo context, String pageData){
		super(context.getResources().getString(R.string.Save), android.R.drawable.ic_menu_save, ItemType.SAVE);
		this.context = context;
		this.pageData = pageData;
		
		ActionToExecute = getActionToExecute();
	}
	
	public Action getActionToExecute() {
		return new Action() {
			@Override
			public void Execute() {
				context.itemSaveSearch(pageData);
				context.syncAdapters();
				Toast.makeText(context, R.string.ToastSaveSearch, Toast.LENGTH_SHORT).show();
			};
		};
	}
}
