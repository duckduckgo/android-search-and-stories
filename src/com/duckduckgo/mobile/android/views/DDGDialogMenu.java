package com.duckduckgo.mobile.android.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.WebViewEvents.WebViewItemMenuClickEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;

import java.util.ArrayList;
import java.util.List;

public class DDGDialogMenu extends DialogFragment {

    public static final String TAG = "dialog_menu";

    private Context context;
    private LayoutInflater inflater;

    private ListView menuListView = null;

    private DDGMenuAdapter menuAdapter;
    private List<MenuItem> menuItems;
    private CharSequence[] items;// = {"ciao", "champagne", "bello"};

    private View fragmentView = null;

    private FeedObject feed = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fragmentView = inflater.inflate(R.layout.temp_popupwindows, null);
        menuListView = (ListView) fragmentView.findViewById(R.id.menu_listview);
        menuListView.setVisibility(View.VISIBLE);
        menuAdapter = new DDGMenuAdapter(getActivity(), R.layout.temp_menuitem, menuItems);
        Log.e("aaa", "menu adapter count: "+menuAdapter.getCount());
        menuListView.setAdapter(menuAdapter);

        builder.setView(fragmentView);



        //builder.setTitle("test");
        //builder.setMessage("ciao test test");
        /*
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("aaa", "clicked: "+items[which]);
            }
        });*/

        boolean test = items == null;
        Log.e("aaa", "items==null: "+test);
        if(!test) {
            Log.e("aaa", "items length: "+items.length);
            for(int i=0; i<items.length; i++) {
                Log.e("aaa", "items["+i+"]: "+items[i]);
            }
        }

        return builder.create();
    }

    //@Override
    //public void
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.temp_popupwindows, container, false);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        menuListView = (ListView) fragmentView.findViewById(R.id.menu_listview);
        menuAdapter = new DDGMenuAdapter(getActivity(), R.layout.temp_menuitem, menuItems);
        menuListView.setAdapter(menuAdapter);
        menuListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("aaa", "on item clicked: " + menuItems.get(position));
        if(feed==null) {
            BusProvider.getInstance().post(new WebViewItemMenuClickEvent(menuItems.get(position)));
        } else {
            BusProvider.getInstance().post(new WebViewItemMenuClickEvent(menuItems.get(position), feed));
        }
        dismiss();
    }
*/
    public void setFeed(FeedObject feed) {
        this.feed = feed;
    }

    public void setMenu(Menu menu) {
        //menuListView.setVisibility(View.VISIBLE);
        menuItems = new ArrayList<MenuItem>();
        for(int i=0; i<menu.size(); i++) {
            if(menu.getItem(i).isVisible()) {
                menuItems.add(menu.getItem(i));
                //items[i] = menu.getItem(i).getTitle();
            }
        }
        items = new String[menuItems.size()];
        for(int i=0; i<menuItems.size(); i++) {
            items[i] = menuItems.get(i).toString();
        }
        //adapter = new ArrayAdapter(context, R.layout.temp_menuitem, R.id.text1, menuItems);

        //menuListView.setAdapter(adapter);
        //menuListView.setAdapter(menuAdapter);
        /*
        for(int i=0; i<adapter.getCount(); i++) {
            MenuItem item = (MenuItem) adapter.getItem(i);
            Log.e("aaa", "item: "+item.getTitle()+" - enabled: "+item.isEnabled());
            //menuListView.getChildAt(i).setEnabled(item.isEnabled());
        }*/
        //menuListView.setOnItemClickListener(this);
    }

    public class DDGMenuAdapter extends ArrayAdapter<MenuItem> {

        private Context context;
        private int layoutResId;
        private List<MenuItem> menuItems;

        public DDGMenuAdapter(Context context, int layoutResId, List<MenuItem> menuItems) {
            super(context, layoutResId, menuItems);
            this.context = context;
            this.layoutResId = layoutResId;
            this.menuItems = menuItems;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View root = convertView;
            Holder holder = null;

            if(root==null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                root = inflater.inflate(layoutResId, parent, false);

                holder = new Holder();
                holder.text = (TextView) root.findViewById(R.id.text1);
                root.setTag(holder);
            } else {
                holder = (Holder) root.getTag();
            }

            MenuItem item = menuItems.get(position);
            holder.text.setText(item.getTitle());
            holder.text.setEnabled(item.isEnabled());
            //Log.e("aaa", "get view position: "+position+" - text: "+item.getTitle()+" - enabled: "+item.isEnabled());

            return root;
        }

        class Holder {
            TextView text;
        }


    }
}
