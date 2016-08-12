package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.duckduckgo.mobile.android.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Separated List Adapter
 *
 * Allows having multiple ListAdapters in one ListView with section headers
 *
 * @author Jeff Sharkey
 * @link http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/
 *
 */
public class SearchAdapter extends BaseAdapter {

    public final Map<String,Adapter> sections = new LinkedHashMap<String,Adapter>();
    public final ArrayAdapter<String> headers;
    //public final static int TYPE_SECTION_HEADER = 0;
    public final static int TYPE_SECTION_DIVIDER = 0;
    public int resId;
    public LayoutInflater inflater;

    public SearchAdapter(Context context) {
        headers = new ArrayAdapter<String>(context, R.layout.list_header);
        init(context);
    }

    public SearchAdapter(Context context, int headerLayoutId) {
        headers = new ArrayAdapter<String>(context, headerLayoutId);
        init(context);
    }

    private void init(Context context) {
        resId = R.layout.search_divider;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addSection(String section, Adapter adapter) {
        this.headers.add(section);
        this.sections.put(section, adapter);
    }

    public Object getItem(int position) {
        boolean previousSectionEmpty = true;
        int sectionCounter = 0;
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            if(!adapter.isEmpty()) {
                int size = adapter.getCount();
                if(sectionCounter>0) size++;

                if(sectionCounter==0 && position < size) return adapter.getItem(position);
                else {
                    if(position == 0 && !previousSectionEmpty) return inflater.inflate(resId, null, false);
                    if(position < size) return adapter.getItem(position - (previousSectionEmpty ? 0 : 1));
                }

                // check if position inside this section
                //if(position == 0) return inflater.inflate(resId, null, false);
                //if(position < size) return adapter.getItem(position - 1);

                // otherwise jump into next section
                position -= size;
                previousSectionEmpty = false;
            } else {
                previousSectionEmpty = true;
            }
            sectionCounter++;
        }
        return null;
    }

    public int getCount() {
        // total together all sections, plus one for each section header
        int total = 0;
        for(Adapter adapter : this.sections.values())
            if(!adapter.isEmpty())
                total += adapter.getCount() + 1;
        return total-1;
    }

    public int getViewTypeCount() {
        // assume that headers count as one, then total all sections
        int total = 1;
        for(Adapter adapter : this.sections.values()) {
            total += adapter.getViewTypeCount();
        }
        return total;
    }

    public int getItemViewType(int position) {
        boolean previousSectionEmpty = true;
        int type = 1;
        int sectionCounter = 0;
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            if(!adapter.isEmpty()) {
                int size = adapter.getCount();
                if(sectionCounter>0) size++;

                if(sectionCounter==0 && position < size) return type + adapter.getItemViewType(position);
                else {
                    if(position == 0 && !previousSectionEmpty) return TYPE_SECTION_DIVIDER;
                    if(position < size) return type + adapter.getItemViewType(position - (previousSectionEmpty ? 0 : 1));
                }

                // check if position inside this section
                //if(position == 0) return TYPE_SECTION_DIVIDER;
                //if(position < size) return type + adapter.getItemViewType(position - 1);

                // otherwise jump into next section
                position -= size;
                type += adapter.getViewTypeCount();
                previousSectionEmpty = false;
            } else {
                previousSectionEmpty = true;
            }
            sectionCounter++;
        }
        return type;
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_DIVIDER);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean previousSectionEmpty = true;
        int sectionnum = 0;
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            if(!adapter.isEmpty()) {
                int size = adapter.getCount();
                if(sectionnum>0) size++;

                if(sectionnum==0 && position < size) return adapter.getView(position, convertView, parent);
                else {
                    if(position == 0 && !adapter.isEmpty() && !previousSectionEmpty) return inflater.inflate(resId, parent, false);//)headers.getView(sectionnum, convertView, parent);
                    if(position < size) return adapter.getView(position - (previousSectionEmpty ? 0 : 1), convertView, parent);
                }
                // check if position inside this section
                //if(position == 0 && !adapter.isEmpty()) return inflater.inflate(resId, parent, false);//)headers.getView(sectionnum, convertView, parent);
                //if(position < size) return adapter.getView(position - 1, convertView, parent);

                // otherwise jump into next section
                position -= size;
                previousSectionEmpty = false;
            } else {
                previousSectionEmpty = true;
            }
            sectionnum++;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}