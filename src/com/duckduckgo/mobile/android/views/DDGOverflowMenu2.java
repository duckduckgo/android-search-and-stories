package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewUpdateMenuNavigationEvent;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewItemMenuClickEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DDGOverflowMenu2 extends PopupWindow implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private Context context;
    private View container;

    private ListView menuListView = null;
    private ArrayAdapter adapter;
    private List<MenuItem> menuItems;

    private LinearLayout header = null;
    private List<ImageButton> headerButtons;
    private HashMap<Integer, MenuItem> headerItems;

    public DDGOverflowMenu2(Context context) {
        super(context, null, android.R.attr.listPopupWindowStyle);
        this.context = context;
        init();
    }

    public DDGOverflowMenu2(Context context, AttributeSet attrs) {
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

        //ListPopupWindow temp = new ListPopupWindow(context);
        //setBackgroundDrawable(temp.getBackground());

        menuListView = (ListView) container.findViewById(R.id.menu_listview);
        header = (LinearLayout) container.findViewById(R.id.header_container);
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
            imageButton.setImageDrawable(menu.getItem(i).getIcon());/*
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("aaa", "header button: "+title+" - "+actionId);
                }
            });*/
            imageButton.setOnClickListener(this);
            //headerButtons.add(imageButton);
            headerItems.put(imageButton.getId(), menu.getItem(i));
            header.addView(imageButton);
        }
    }

    public void show(View anchor) {
        //setWidth(getWidestView(context, adapter));
        setWidth(getMaxWidth(context, adapter));
/*
        int xOffset = 0;
        int yOffset = 0;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            Rect rect = new Rect();
            Window window = activity.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rect);
            int statusBarHeight = rect.top;

            xOffset = (int) context.getResources().getDimension(R.dimen.menu_outer_margin);
            yOffset = xOffset + statusBarHeight;

        }*/

        int xOffset = anchor.getMeasuredWidth() - getWidth();
        int yOffset = anchor.getMeasuredHeight() - getHeight();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            int screenMargin = (int) context.getResources().getDimension(R.dimen.menu_outer_margin);
            xOffset -= screenMargin;
            yOffset -= screenMargin;
        }

        //showAtLocation(anchor, Gravity.TOP|Gravity.RIGHT, xOffset, yOffset);
        //showAtLocation(anchor, Gravity.TOP|Gravity.RIGHT, xOffset, yOffset);
        showAsDropDown(anchor, xOffset, yOffset*-1);
    }

    @Override
    public void showAsDropDown(View anchor) {
        //setWidth(getWidestView(context, adapter));
        setWidth(getMaxWidth(context, adapter));
        int w = anchor.getMeasuredWidth() - getWidth();// - 10;

        int h = anchor.getMeasuredHeight();// - 10;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            w = w - (int) context.getResources().getDimension(R.dimen.menu_outer_margin);
            h = h - (int) context.getResources().getDimension(R.dimen.menu_outer_margin);
        }
        //setHorizontalOffset(w);
        //setVerticalOffset(h * -1);

        super.showAsDropDown(anchor);
    }

    @Override
    public void dismiss() {
        BusProvider.getInstance().unregister(this);
        super.dismiss();
    }

    @Override
    public void onClick(View view) {
        Log.e("aaa", "on click: "+view.getId());
        BusProvider.getInstance().post(new WebViewItemMenuClickEvent(headerItems.get(view.getId())));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("aaa", "on item click, position: "+position);
        BusProvider.getInstance().post(new WebViewItemMenuClickEvent(menuItems.get(position)));
        dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.e("aaa", "on item selected: "+position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.e("aaa", "on nothing selected");
        dismiss();
    }

    public static int getMaxWidth(Context context, Adapter adapter) {
        int maxLength = 0;
        for(int i=0; i<adapter.getCount(); i++) {
            int newLength = ((MenuItem)adapter.getItem(i)).getTitle().length();
            maxLength = newLength>maxLength ? newLength : maxLength;
        }
        int width = (int) context.getResources().getDimension(R.dimen.menu_letterspace) * maxLength;
        //Log.e("aaa", "letterspace: "+context.getResources().getDimension(R.dimen.menu_letterspace)+" - max length: "+maxLenth+" - width: "+width);
        int menuPadding = (int) context.getResources().getDimension(R.dimen.menu_padding) * 2;
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
