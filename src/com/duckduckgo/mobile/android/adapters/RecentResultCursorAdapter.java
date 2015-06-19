package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.pasteEvents.RecentSearchPasteEvent;

public class RecentResultCursorAdapter extends CursorAdapter {

    private CharSequence userInput = "";

    private boolean hidePasteButton = false;

    public RecentResultCursorAdapter(Context context, Cursor c) {
        super(context, c);
        //super context, c, flags api 11
    }

    public RecentResultCursorAdapter(Context context, Cursor c, boolean hidePasteButton) {
        super(context, c);
        //this.hideIcon = hideIcon;
        this.hidePasteButton = hidePasteButton;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //View retView = inflater.inflate(R.layout.recentsearch_list_layout, parent, false);
        View retView = inflater.inflate(R.layout.item_search, parent, false);
        //View retView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        final String data = cursor.getString(cursor.getColumnIndex("data"));
        TextView sub = (TextView) view.findViewById(R.id.item_text2);
        if(sub!=null) {
            //sub.setText(userInput);
        }
        TextView sub2 = (TextView) view.findViewById(R.id.item_text3);
        if(sub2!=null && userInput.length()>0) {
            //sub2.setText(data.substring(userInput.length()));
        }
        Spannable word;

        //final String type = cursor.getString(cursor.getColumnIndex("type"));

        //TextView textViewTitle = (TextView) view.findViewById(android.R.id.text1);
        TextView title = (TextView) view.findViewById(R.id.item_text);
        if(title!=null) {

            if (userInput.length() != 0 && data.startsWith(userInput.toString())) {
                word = new SpannableString(userInput);
                word.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.search_dark)), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setText(word);

                word = new SpannableString(data.substring(userInput.length()));//data.replace(userInput.toString(), ""));
                word.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.search_light)), 0, word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.append(word);
            } else {
                title.setTextColor(context.getResources().getColor(R.color.search_dark));
                title.setText(data);
            }
        }

        ImageView icon = (ImageView) view.findViewById(R.id.item_icon);
        if (icon != null) {
            icon.setImageDrawable(context.getResources().getDrawable(R.drawable.time));
        }

        ImageButton pasteButton = (ImageButton) view.findViewById(R.id.item_paste);
        if(pasteButton!=null) {
            if(hidePasteButton) {
                pasteButton.setVisibility(View.GONE);
            } else {
                pasteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BusProvider.getInstance().post(new RecentSearchPasteEvent(data));
                    }
                });
                pasteButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        userInput = constraint;
        if(!constraint.toString().equals("")) {
            return DDGApplication.getDB().getCursorSearchHistory(constraint.toString());
        }
        return DDGApplication.getDB().getCursorSearchHistory();
    }
}
