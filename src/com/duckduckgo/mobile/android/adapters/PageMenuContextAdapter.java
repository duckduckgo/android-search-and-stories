package com.duckduckgo.mobile.android.adapters;

import static com.duckduckgo.mobile.android.adapters.PageMenuContextAdapter.dialogItems;

import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.Item;
import com.duckduckgo.mobile.android.util.Item.ItemType;

public class PageMenuContextAdapter extends ArrayAdapter<Item> {
	
	Context context;
	static HashMap<ItemType, Item> dialogItems;
	
	private void initItemMap() {
        dialogItems.put(ItemType.SHARE, new Item(context.getResources().getString(R.string.Share), android.R.drawable.ic_menu_share, ItemType.SHARE));
        dialogItems.put(ItemType.SAVE, new Item(context.getResources().getString(R.string.Save), android.R.drawable.ic_menu_save, ItemType.SAVE));
        dialogItems.put(ItemType.UNSAVE, new Item(context.getResources().getString(R.string.Unsave), android.R.drawable.ic_menu_delete, ItemType.UNSAVE));
        dialogItems.put(ItemType.DELETE, new Item(context.getResources().getString(R.string.Delete), android.R.drawable.ic_menu_close_clear_cancel, ItemType.DELETE));
        dialogItems.put(ItemType.EXTERNAL, new Item(context.getResources().getString(R.string.OpenInExternalBrowser), android.R.drawable.ic_menu_rotate, ItemType.EXTERNAL));
        dialogItems.put(ItemType.REFRESH, new Item(context.getResources().getString(R.string.Refresh), R.drawable.icon_reload, ItemType.REFRESH));
        dialogItems.put(ItemType.READABLE, new Item(context.getResources().getString(R.string.Readable), android.R.drawable.ic_menu_add, ItemType.READABLE));
	}
	
	protected Item getItem(ItemType itemType){
		return dialogItems.get(itemType);
	}

	public PageMenuContextAdapter(Context context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
		
		// avoid creating static item map again
		if(dialogItems == null) {
			dialogItems = new HashMap<ItemType, Item>();
			initItemMap();
		}
	}
	
	public PageMenuContextAdapter(Context context, int resource, 
			int textViewResourceId, String pageType, boolean isItemSaved) {
		this(context, resource, textViewResourceId);
		setType(pageType, isItemSaved);
	}
	
	public void setType(String pageType, boolean isItemSaved) {
		if(pageType.equals("mainfeed")) {
			add(dialogItems.get(ItemType.SHARE));
			add(dialogItems.get(ItemType.EXTERNAL));			
			if(isItemSaved)
				add(dialogItems.get(ItemType.UNSAVE));
			else
				add(dialogItems.get(ItemType.SAVE));
		}
		else if(pageType.equals("savedfeed")) {
			add(dialogItems.get(ItemType.SHARE));
			add(dialogItems.get(ItemType.EXTERNAL));			
			add(dialogItems.get(ItemType.UNSAVE));
		}
		else if(pageType.equals("savedsearch")) {
			add(dialogItems.get(ItemType.SHARE));
			add(dialogItems.get(ItemType.EXTERNAL));
			add(dialogItems.get(ItemType.UNSAVE));
		}
		else if(pageType.equals("history")) {
			add(dialogItems.get(ItemType.SHARE));
			add(dialogItems.get(ItemType.EXTERNAL));
			add(dialogItems.get(ItemType.DELETE));
			
			if(isItemSaved)
				add(dialogItems.get(ItemType.UNSAVE));
			else
				add(dialogItems.get(ItemType.SAVE));
		}
		else if(pageType.contains("webview-F") || pageType.equals("webview-R")) {
			add(dialogItems.get(ItemType.SHARE));
			add(dialogItems.get(ItemType.EXTERNAL));
			add(dialogItems.get(ItemType.REFRESH));
			
			if(isItemSaved)
				add(dialogItems.get(ItemType.UNSAVE));
			else
				add(dialogItems.get(ItemType.SAVE));
			
			// enable readability button
			if(pageType.equals("webview-FR")) {
				add(dialogItems.get(ItemType.READABLE));
			}
		}
		else if(pageType.equals("webview-W")) {
			add(dialogItems.get(ItemType.SHARE));
			add(dialogItems.get(ItemType.EXTERNAL));
			add(dialogItems.get(ItemType.REFRESH));
		}
	}
	
	public View getView(int position, View convertView, android.view.ViewGroup parent) {		
		View v = super.getView(position, convertView, parent);
		TextView tv = (TextView)v.findViewById(android.R.id.text1);
		tv.setCompoundDrawablesWithIntrinsicBounds(getItem(position).icon, 0, 0, 0);

		//Add 10dp margin between image and text (support various screen densities)
		int dp10 = (int) (10 * context.getResources().getDisplayMetrics().density + 0.5f);
		tv.setCompoundDrawablePadding(dp10);

		return v;
	}

}
