package com.duckduckgo.mobile.android.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.ListPopupWindow;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.DimBackgroundEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewUpdateMenuNavigationEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewItemMenuClickEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.Action;
import com.duckduckgo.mobile.android.util.Item;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DDGOverflowMenu extends PopupWindow implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private Context context;
    private View container;

    private ListView menuListView = null;
    private DDGOverflowAdapter overflowAdapter;

    private LinearLayout header = null;
    private HashMap<Integer, MenuItem> headerItems = null;
    private boolean isBusRegistered = false;

    private FeedObject feed = null;

    public DDGOverflowMenu(Context context) {
        //super(context, null, android.R.attr.listPopupWindowStyle);
        super(context, null, R.attr.popUp);

        //super(context, null, android.R.attr.popupMenuStyle);
        //super(context, null, android.R.attr.actionOverflowMenuStyle);
        //super(context);
        //super(context, null, android.R.attr.spinnerStyle);
        this.context = context;
        init();
    }

    public DDGOverflowMenu(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.listPopupWindowStyle);
        this.context = context;
        init();
    }

    public void init() {
        setFocusable(true);
        setOutsideTouchable(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        container = inflater.inflate(R.layout.temp_popupwindows, null);
        setContentView(container);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        ListPopupWindow temp = new ListPopupWindow(context);

        menuListView = (ListView) container.findViewById(R.id.menu_listview);
        menuListView.setOnItemClickListener(this);
        menuListView.setOnItemSelectedListener(this);

        header = (LinearLayout) container.findViewById(R.id.header_container);
    }

    public void registerBus() {
        isBusRegistered = true;
        BusProvider.getInstance().register(this);
    }

    public void unregisterBus() {
        if(isBusRegistered) {
            isBusRegistered = false;
            BusProvider.getInstance().unregister(this);
        }
    }

    public void setMenu(Menu menu) {
        overflowAdapter = new DDGOverflowAdapter(context, R.layout.temp_menuitem);
        menuListView.setAdapter(overflowAdapter);
        setMenu(menu, false);
    }

    public void setMenu(Menu menu, boolean newSection) {
        Log.e("ddgmenu", "inside set menu");
        menuListView.setVisibility(View.VISIBLE);

        List<MenuItem> newMenuItems = new ArrayList<>();

        for(int i=0; i<menu.size(); i++) {
            boolean added = false;
            if(menu.getItem(i).isVisible()) {
                newMenuItems.add(menu.getItem(i));
                added = true;
            }
            Log.e("ddgmenu", "item title: "+menu.getItem(i).getTitle()+" - added: "+added);
        }
        overflowAdapter.addItems(newMenuItems, newSection);
        overflowAdapter.notifyDataSetChanged();

    }

    public void setHeaderMenu(Menu menu) {
        registerBus();

        header.setVisibility(View.VISIBLE);
        headerItems = new HashMap<Integer, MenuItem>();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i=0; i<menu.size(); i++) {
            ImageButton imageButton = (ImageButton) inflater.inflate(R.layout.temp_header_item, header, false);
            final String title = ""+menu.getItem(i).getTitle();
            final int actionId = menu.getItem(i).getItemId();
            imageButton.setId(actionId);
            imageButton.setEnabled(menu.getItem(i).isEnabled());
            imageButton.setImageDrawable(menu.getItem(i).getIcon());
            if(menu.getItem(i).getIcon()==null) {
                imageButton.setEnabled(false);
            } else {
                imageButton.setOnClickListener(this);
            }

            headerItems.put(imageButton.getId(), menu.getItem(i));
            header.addView(imageButton);
        }

    }

    public void setFeed(FeedObject feed) {
        this.feed = feed;
    }

    public void show(View anchor) {
        show(anchor, true, true);
    }

    public void showFeedMenu(View anchor) {
        show(anchor, false, true);
        //showAsDropDown(anchor);
    }

    public void showBelowAnchor(View anchor) {
        show(anchor, false, false);
    }

    private void show(View anchor, boolean withMarginOnAnchor, boolean coverAnchor) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight, value, true);
        String s = TypedValue.coerceToString(value.type, value.data);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((DuckDuckGo)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float item = value.getDimension(displayMetrics);
        //Log.e("aaa", "single item is: "+item);

        //setWidth(getMaxWidth(context, adapter));

        setWidth(getMaxWidth(context, overflowAdapter));//aaa ----- temp
        setCloseButtonPadding();

        //setWidth(54*3*3);
        //int height = ((int) context.getResources().getDimension(R.dimen.listview_item_height)) * (menuItems.size() + 1);
        int headerMenu = headerItems!=null ? 1 : 0;
        int itemCount = overflowAdapter.getItemCount();
        Log.e("ddgshowmenu", "item count: "+itemCount);
        int height = ((int) context.getResources().getDimension(R.dimen.listview_item_height)) * (overflowAdapter.getItemCount() + headerMenu);
        int divider = (int) context.getResources().getDimension(R.dimen.simple_divider_height);
        height += divider;

        //int height = ((int)item) * (menuItems.size() + headerMenu);
        //setHeight(height);

        Rect rect = new Rect();
        anchor.getGlobalVisibleRect(rect);
        Log.e("ddgshowmenu", "rect: "+rect.toString());
        Log.e("ddgshowmenu", "rect h: "+rect.height());
        Log.e("ddgshowmenu", "rect w: "+rect.width());

        Log.e("ddgshowmenu", "rect bottom: "+rect.top);
        Log.e("ddgshowmenu", "display height: "+displayMetrics.heightPixels);
        Log.e("ddgshowmenu", "menu calc height: "+height);
        Log.e("ddgshowmenu", "item listpreferreditemheight: "+item);

        boolean reverseMenu = false;
        if(displayMetrics.heightPixels>height) {
            setHeight(height);
            if((displayMetrics.heightPixels-rect.top)<=height) {
                reverseMenu = true;
            }
        }
        Log.e("aaa", "should reverse menu: "+reverseMenu);

        int xOffset = 0;
        int yOffset = 0;

        if(coverAnchor) {
            xOffset = anchor.getMeasuredWidth() - getWidth();
            yOffset = reverseMenu ? height : anchor.getMeasuredHeight();// - getHeight();

            Log.e("ddgshowmenu", "X - xOffset: " + xOffset + " - anchor width: " + anchor.getMeasuredWidth() + " - get width: " + getWidth());
            Log.e("ddgshowmenu", "Y - yOffset: " + yOffset + " - anchor height: " + anchor.getMeasuredHeight() + " - get height: " + getHeight());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (withMarginOnAnchor) {
                    int screenMargin = (int) context.getResources().getDimension(R.dimen.menu_outer_margin);
                    xOffset -= screenMargin;
                    yOffset -= screenMargin;
                }
            } else {
                //int screenMargin = (int) context.getResources().getDimension(R.dimen.menu_outer_margin_2);
                //xOffset += (screenMargin * 20);
                //yOffset += screenMargin;
            }
        }
        //setHeight(height);

        if(coverAnchor) {
            showAsDropDown(anchor, xOffset, yOffset * -1);//aaa ----- temp
        } else {

            View background = ((Activity)context).findViewById(android.R.id.content);
            boolean backgroundIsNull = background==null;
            Log.e("ddgshowmenu", "backgroundview is null: "+backgroundIsNull);
            BusProvider.getInstance().post(new DimBackgroundEvent(true));
            showAtLocation(anchor, Gravity.CENTER, 0, 0);
        }
        //showAsDropDown(anchor);

        //int dpTest = (int) context.getResources().getDimension(R.dimen.menu_outer_margin_2);
        //yOffset = getStatusBarHeight() - (dpTest * 10);


        //showAtLocation(anchor, Gravity.CENTER|Gravity.RIGHT, (-1*yOffset), 0);

        //showAtLocation(anchor, Gravity.TOP|Gravity.RIGHT, (int)context.getResources().getDimension(R.dimen.menu_outer_margin), (int)context.getResources().getDimension(R.dimen.menu_outer_margin)+getStatusBarHeight());
        //showAtLocation(anchor, Gravity.CENTER, 0, 0);
    }

    public void showCentered(View anchor) {
        setWidth(getMaxWidth(context, overflowAdapter));
        showAtLocation(anchor, Gravity.CENTER, 0, 0);
    }

    @Override
    public void dismiss() {
        unregisterBus();
        super.dismiss();
    }

    @Override
    public void onClick(View view) {
        BusProvider.getInstance().post(new WebViewItemMenuClickEvent(headerItems.get(view.getId())));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(feed==null) {
            //BusProvider.getInstance().post(new WebViewItemMenuClickEvent(menuItems.get(position)));
            BusProvider.getInstance().post(new WebViewItemMenuClickEvent(overflowAdapter.getMenuItem(position)));//menuItems.get(position))
        } else {
            BusProvider.getInstance().post(new WebViewItemMenuClickEvent(overflowAdapter.getMenuItem(position), feed));//menuItems.get(position)
        }
        dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        dismiss();
    }

    private void setCloseButtonPadding() {
        int maxWith = getWidth();
        int widthThird = maxWith / 3;

    }

    public static int getMaxWidth(Context context, DDGOverflowAdapter adapter) {
        //Log.e("aaa", "-------------get max width");
        int maxLength = 0;
        for(int i=0; i<adapter.getCount(); i++) {
            int newLength = adapter.getMenuItemTitle(i).length();//(MenuItem)adapter.getItem(i)).getTitle().length();
            //Log.e("aaa", "new length: "+newLength);
            maxLength = newLength>maxLength ? newLength : maxLength;
            //Log.e("aaa", "max length: "+maxLength);
        }
        int width = (int) context.getResources().getDimension(R.dimen.menu_letterspace) * (maxLength+2);
        int menuPadding = (int) context.getResources().getDimension(R.dimen.menu_padding) * 2;
        //Log.e("aaa", "size: "+width+menuPadding);
        return width + menuPadding;

    }

    public static int getWidestView(Context context, Adapter adapter) {
        int maxWidth = 0;
        View view = null;
        FrameLayout fakeParent = new FrameLayout(context);
        for (int i=0, count=adapter.getCount(); i<count; i++) {
            view = adapter.getView(i, view, fakeParent);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int width = view.getMeasuredWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    private int getStatusBarHeight() {
        Rect rect = new Rect();
        Window window = ((DuckDuckGo)context).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    @Subscribe
    public void onWebViewDisableMenuNavigationButtonEvent(WebViewUpdateMenuNavigationEvent event) {

        for(HashMap.Entry<Integer, Boolean> entry : event.newStates.entrySet()) {
            Log.e("aaa web", "key: "+entry.getKey()+" - key:"+entry.getValue());
            ImageButton imageButton = (ImageButton) header.findViewById(entry.getKey());
            if(imageButton!=null) {
                imageButton.setEnabled(entry.getValue() );
            }
        }
/*
        ImageButton imageButton = (ImageButton) header.findViewById(event.disableId);
        if(imageButton!=null) {
            imageButton.setEnabled(false);
        }
        imageButton = (ImageButton) header.findViewById(event.enableId);
        if(imageButton!=null) {
            imageButton.setEnabled(true);
        }*/

        /*
        RelativeLayout imageButton= (RelativeLayout) header.findViewById(event.disableId);
        if(imageButton!=null) {
            imageButton.setEnabled(false);
        }
        imageButton = (RelativeLayout) header.findViewById(event.enableId);
        if(imageButton!=null) {
            imageButton.setEnabled(true);
        }*/
    }

    public class DDGOverflowAdapter extends ArrayAdapter<MenuItem> {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_DIVIDER = 1;

        private Context context;
        private int layoutResId;
        //private List<MenuItem> menuItems;
        private List<Item> items;

        public DDGOverflowAdapter(Context context, int layoutResId) {
            super(context, layoutResId);
            this.context = context;
            this.layoutResId = layoutResId;
            //this.menuItems = menuItems;
            items = new ArrayList<>();
            //addItems(menuItems);
            //initMenuItems(menuItems);
        }
/*
        public void initMenuItems(List<MenuItem> menuItems) {
            items = new ArrayList<>();

            for(MenuItem menuItem : menuItems) {
                items.add(new Item(menuItem));
            }
        }*/
        /*
        public void addItems(List<MenuItem> menuItems) {
            addItems(menuItems, false);
        }*/

        public void addItems(List<MenuItem> menuItems, boolean newSection) {
            if(newSection) {
                items.add(new Item(true));
            }
            for(MenuItem menuItem : menuItems) {
                items.add(new Item(menuItem));
            }
            Log.e("ddgmenu", "print menu, size is: "+items.size());
            printItems();
        }

        public void printItems() {
            for(Item item : items) {
                String text = item.isDivider ? "divider" : item.item.getTitle().toString();
                Log.e("ddgitems", "item: "+text);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View root = convertView;
            Holder holder = null;
            int itemType = getItemViewType(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(root==null) {
                if(itemType==TYPE_ITEM) {
                    root = inflater.inflate(layoutResId, parent, false);
                    holder = new Holder();
                    holder.text = (TextView) root.findViewById(R.id.text1);
                    root.setTag(holder);
                } else {
                    root = inflater.inflate(R.layout.overflowmenu_divier, parent, false);
                }
            } else {
                if(itemType==TYPE_ITEM) {
                    holder = (Holder) root.getTag();
                }
            }

            if(itemType==TYPE_ITEM) {
                MenuItem item = items.get(position).item;//menuItems.get(position);
                holder.text.setText(item.getTitle());
                holder.text.setEnabled(item.isEnabled());
                //Log.e("aaa", "get view position: "+position+" - text: "+item.getTitle()+" - enabled: "+item.isEnabled());
            }

            return root;
        }

        public int getItemCount() {
            int out = 0;
            for(Item item : items) {
                if(!item.isDivider) {
                    out++;
                }
            }
            return out;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public int getItemViewType(int position) {
            Item item = items.get(position);
            if(item.isDivider) {
                return TYPE_DIVIDER;
            }
            return TYPE_ITEM;
        }

        @Override
        public boolean isEnabled(int position) {
            return !items.get(position).isDivider;
        }

        public MenuItem getMenuItem(int position) {
            Item item = items.get(position);
            if(item.isDivider) {
                return null;
            }
            return item.item;
        }

        public String getMenuItemTitle(int position) {
            Item item = items.get(position);
            if(item.isDivider) {
                return "";
            }
            return item.item.getTitle().toString();
        }

        class Holder {
            TextView text;
        }

        class Item {
            public boolean isDivider = false;
            public MenuItem item;

            public Item(boolean isDivider) {
                this.isDivider = isDivider;
            }

            public Item(MenuItem item) {
                this.item = item;
            }

            /*
            public static Item createNewDivider() {
                return new Item(true);
            }

            public Item createNewItem(MenuItem item) {
                return  new Item(item);
            }*/
        }


    }


}
