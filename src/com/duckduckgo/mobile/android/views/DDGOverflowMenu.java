package com.duckduckgo.mobile.android.views;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.ListPopupWindow;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewItemMenuClickEvent;

import java.util.ArrayList;
import java.util.List;

public class DDGOverflowMenu extends ListPopupWindow implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private Context context;
    private List<MenuItem> items;
    private List<String> titles;
    private int maxLenth = 0;
    private ArrayAdapter adapter = null;
    private View header = null;
    private List<MenuItem> headerMenu;

    private View anchor;

    public DDGOverflowMenu(Context context) {
        super(context);
        this.context = context;
        this.items = new ArrayList<MenuItem>();
        setOnItemSelectedListener(this);
    }

    public DDGOverflowMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.items = new ArrayList<MenuItem>();
        setOnItemSelectedListener(this);
    }

    public void setMenu(Menu menu) {
        titles = new ArrayList<String>();
        for(int i=0; i<menu.size(); i++) {
            //Log.e("aaa", "item name: " + menu.getItem(i).getTitle() + "visibility: " + menu.getItem(i).isVisible());
            if(menu.getItem(i).isVisible()) {
                //titles.add(""+menu.getItem(i).getTitle());
                items.add(menu.getItem(i));
                if(menu.getItem(i).getTitle().length()>maxLenth) {
                    maxLenth = menu.getItem(i).getTitle().length();
                }
            }
        }
        /*
        this.menu = menu;
        titles = new ArrayList<String>();

        for(int i=0; i<menu.size(); i++) {
            if(menu.getItem(i).isVisible()) {
                items.add(menu.getItem(i));
                titles.add(""+menu.getItem(i).getTitle());
            } else {
                this.menu.removeItem(menu.getItem(i).getItemId());
            }
        }
*/
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, titles);
        ArrayAdapter adapter1 = new ArrayAdapter(context, android.R.layout.simple_list_item_1, android.R.id.text1, items);
        ArrayAdapter adapter2 = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, items);
        ArrayAdapter adapter3 = new ArrayAdapter(context, R.layout.temp_menuitem, R.id.text1, items);
        adapter = adapter3;
        //setAdapter(adapter);
        //setAdapter(adapter3);
        setOnItemClickListener(this);
        setOnItemSelectedListener(this);
    }

    //public void setHeader(Menu menu) {
    public void setHeaderView(Menu menu) {/*
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout headerView = (LinearLayout) inflater.inflate(R.layout.temp_header, null);
        ImageButton backButton = (ImageButton) headerView.findViewById(R.id.button_back);
        ImageButton forwardButton = (ImageButton) headerView.findViewById(R.id.button_forward);
        ImageButton closeButton = (ImageButton) headerView.findViewById(R.id.button_close);
        TextView textView = new TextView(context);
        textView.setText("TITOLO");

        header = headerView;*/

/*        LinearLayout headerView_ = new LinearLayout(context);
        headerView_.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, WRAP_CONTENT));
        headerView_.setOrientation(LinearLayout.HORIZONTAL);
*/

        LayoutInflater inflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout headerView_ = (LinearLayout) inflater_.inflate(R.layout.temp_header, null);
        //headerView_.setWeightSum(menu.size());

        for(int i=0; i<menu.size(); i++) {//todo save the buttons locally so back and forward can be desabled
            ImageButton imageButton = (ImageButton) inflater_.inflate(R.layout.temp_header_item, headerView_, false);
            imageButton.setImageDrawable(menu.getItem(i).getIcon());
            //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageButton.getLayoutParams();
            //params.weight = 1;
            final int actionId = menu.getItem(i).getItemId();
            final String title = ""+menu.getItem(i).getTitle();
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("aaa", "title: "+title+" - action id: "+actionId);
                }
            });
            if(i==0) {
                //imageButton.setEnabled(false);
            }
            headerView_.addView(imageButton);
        }

        TextView text = new TextView(context);
        text.setText("ciao");
        //headerView_.addView(text);
        header = headerView_;


        //textView.setTextColor(0xff0000);
        //textView.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        //getListView().addHeaderView(headerView);
        //getListView().addHeaderView(textView);
        //ListView listView = new ListView(context);
        //listView.addHeaderView(textView);
        //listView.addHeaderView(headerView);
        //listView.setAdapter(adapter);

        //listView.getLayoutParams().width = getWidestView(context, listView.getAdapter());
        //setAdapter(listView.getAdapter());

    }

    @Override
    public void setAnchorView(View anchor) {
        this.anchor = anchor;
        super.setAnchorView(anchor);
    }

    @Override
    public void show() {
        int dp = 9 * (int) context.getResources().getDisplayMetrics().density;
        int width = (int) context.getResources().getDimension(R.dimen.menu_letterspace) * maxLenth;
        //Log.e("aaa", "letterspace: "+context.getResources().getDimension(R.dimen.menu_letterspace)+" - max length: "+maxLenth+" - width: "+width);
        int menuPadding = (int) context.getResources().getDimension(R.dimen.menu_padding) * 2;
        //setWidth(width + menuPadding);


        //setWidth(getWidestView(context, adapter));

        //header.setLayoutParams(new ViewGroup.LayoutParams(new ViewGroup.LayoutParams(width+menuPadding, WRAP_CONTENT)));
        ListView listView = new ListView(context);
        if(header!=null) {
            listView.addHeaderView(header);
        }
        listView.setAdapter(adapter);
        setAdapter(listView.getAdapter());

        setWidth(getMaxWidth(context, adapter));

        //Log.e("aaa", "background: "+getBackground());

        int w = anchor.getMeasuredWidth() - getWidth();// - 10;

        //Log.e("aaa", "w: "+w);
        int h = anchor.getMeasuredHeight();// - 10;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            w = w - (int) context.getResources().getDimension(R.dimen.menu_outer_margin);
            h = h - (int) context.getResources().getDimension(R.dimen.menu_outer_margin);
        }
        setHorizontalOffset(w);
        setVerticalOffset(h*-1);
        //Log.e("aaa", "h: "+h);

        //Log.e("aaa", "")
        super.show();
        //getListView().addHeaderView(header);
        //getListView().getAdapter().notify();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //BusProvider.getInstance().post(new WebViewItemMenuClickEvent(items.get(position)));//aaa todo change
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
}
