package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.ListPopupWindow;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewUpdateMenuNavigationEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewItemMenuClickEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DDGOverflowMenu extends PopupWindow implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private Context context;
    private View container;

    private ListView menuListView = null;
    private ArrayAdapter adapter;
    private List<MenuItem> menuItems;

    private LinearLayout header = null;
    private List<ImageButton> headerButtons;
    private HashMap<Integer, MenuItem> headerItems;

    public DDGOverflowMenu(Context context) {
        //super(context, null, android.R.attr.listPopupWindowStyle);

        //super(context, null, android.R.attr.popupMenuStyle);
        //super(context, null, android.R.attr.actionOverflowMenuStyle);
        //super(context);
        super(context, null, android.R.attr.spinnerStyle);
        this.context = context;
        init();
    }

    public DDGOverflowMenu(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.listPopupWindowStyle);
        this.context = context;
        init();
    }

    public void init() {
        BusProvider.getInstance().register(this);
        setFocusable(true);
        setOutsideTouchable(true);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        container = inflater.inflate(R.layout.temp_popupwindows, null);
        setContentView(container);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        ListPopupWindow temp = new ListPopupWindow(context);
        //setBackgroundDrawable(temp.getBackground());

        menuListView = (ListView) container.findViewById(R.id.menu_listview);
        header = (LinearLayout) container.findViewById(R.id.header_container);

        //menuListView.setOnTouchListener(this);
    }

    public void setMenu(Menu menu) {
        menuListView.setVisibility(View.VISIBLE);
        menuItems = new ArrayList<MenuItem>();
        for(int i=0; i<menu.size(); i++) {
            if(menu.getItem(i).isVisible()) {
                menuItems.add(menu.getItem(i));
            }
        }
        adapter = new ArrayAdapter(context, R.layout.temp_menuitem, R.id.text1, menuItems);
        menuListView.setAdapter(adapter);
        menuListView.setOnItemClickListener(this);
        menuListView.setOnItemSelectedListener(this);
    }

    public void setHeaderMenu(Menu menu) {
        header.setVisibility(View.VISIBLE);
        //headerButtons = new ArrayList<ImageButton>();
        headerItems = new HashMap<Integer, MenuItem>();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i=0; i<menu.size(); i++) {
            ImageButton imageButton = (ImageButton) inflater.inflate(R.layout.temp_header_item, header, false);
            final String title = ""+menu.getItem(i).getTitle();
            final int actionId = menu.getItem(i).getItemId();
            imageButton.setId(actionId);
            imageButton.setEnabled(menu.getItem(i).isEnabled());
            imageButton.setImageDrawable(menu.getItem(i).getIcon());
            imageButton.setOnClickListener(this);
            //headerButtons.add(imageButton);
            headerItems.put(imageButton.getId(), menu.getItem(i));
            header.addView(imageButton);
        }
    }

    public void show(View anchor) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight, value, true);
        String s = TypedValue.coerceToString(value.type, value.data);
        DisplayMetrics display = new DisplayMetrics();
        ((DuckDuckGo)context).getWindowManager().getDefaultDisplay().getMetrics(display);
        float item = value.getDimension(display);
        Log.e("aaa", "single item is: "+item);

        setWidth(getMaxWidth(context, adapter));
        //setWidth(54*3*3);
        int height = ((int) context.getResources().getDimension(R.dimen.listview_item_height)) * (menuItems.size() + 1);
        //setHeight(height);

        int xOffset = anchor.getMeasuredWidth() - getWidth();
        int yOffset = anchor.getMeasuredHeight() - getHeight();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            int screenMargin = (int) context.getResources().getDimension(R.dimen.menu_outer_margin);
            xOffset -= screenMargin;
            yOffset -= screenMargin;
        }

        //setHeight(height);

        showAsDropDown(anchor, xOffset, yOffset*-1);
    }

    @Override
    public void dismiss() {
        BusProvider.getInstance().unregister(this);
        super.dismiss();
    }

    @Override
    public void onClick(View view) {
        BusProvider.getInstance().post(new WebViewItemMenuClickEvent(headerItems.get(view.getId())));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BusProvider.getInstance().post(new WebViewItemMenuClickEvent(menuItems.get(position)));
        dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        dismiss();
    }

    public static int getMaxWidth(Context context, Adapter adapter) {
        Log.e("aaa", "-------------get max width");
        int maxLength = 0;
        for(int i=0; i<adapter.getCount(); i++) {
            int newLength = ((MenuItem)adapter.getItem(i)).getTitle().length();
            Log.e("aaa", "new length: "+newLength);
            maxLength = newLength>maxLength ? newLength : maxLength;
            Log.e("aaa", "max length: "+maxLength);
        }
        int width = (int) context.getResources().getDimension(R.dimen.menu_letterspace) * (maxLength+2);
        int menuPadding = (int) context.getResources().getDimension(R.dimen.menu_padding) * 2;
        Log.e("aaa", "size: "+width+menuPadding);
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

    @Subscribe
    public void onWebViewDisableMenuNavigationButtonEvent(WebViewUpdateMenuNavigationEvent event) {
        ImageButton imageButton = (ImageButton) header.findViewById(event.disableId);
        if(imageButton!=null) {
            imageButton.setEnabled(false);
        }
        imageButton = (ImageButton) header.findViewById(event.enableId);
        if(imageButton!=null) {
            imageButton.setEnabled(true);
        }
    }


}
