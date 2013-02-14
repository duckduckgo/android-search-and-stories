package com.duckduckgo.mobile.android.adapters;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;

public class HistoryCursorAdapter extends CursorAdapter {
	Activity parentActivity;
	
    public HistoryCursorAdapter(Activity parentActivity, Context context, Cursor c) {
        super(context, c);
        this.parentActivity = parentActivity; 
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.recentsearch_list_layout, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

    	final String data = cursor.getString(cursor.getColumnIndex("data"));
    	final String type = cursor.getString(cursor.getColumnIndex("type"));
    	
        TextView textViewHistory = (TextView) view.findViewById(R.id.recentSearchText);
        textViewHistory.setText(data);
        textViewHistory.setTextSize(DDGControlVar.recentTextSize);
        
        String strUrl = cursor.getString(cursor.getColumnIndex("url"));
        String extraType = cursor.getString(cursor.getColumnIndex("extraType"));
        AsyncImageView imageViewHistory = (AsyncImageView) view.findViewById(R.id.recentSearchImage);
        if(extraType.length() != 0) {
          imageViewHistory.setType(extraType);
          
	          if (strUrl != null) {
	        	  URL url = null;
	        	  try {
	        	  	url = new URL(strUrl);
	        	  } catch (MalformedURLException e) {
	  				e.printStackTrace();
	    		  }     
	        	  	
	        	  	if (url != null) {
						String host = url.getHost();
						if (host.indexOf(".") != host.lastIndexOf(".")) {
							//Cut off the beginning, because we don't want/need it
							host = host.substring(host.indexOf(".")+1);
						}
						
						Bitmap bitmap = DDGApplication.getImageCache().getBitmapFromCache("DUCKDUCKICO--" + extraType, false);
						if(bitmap != null){
							imageViewHistory.setBitmap(bitmap);
						}
						else {
							DDGApplication.getImageDownloader().download(DDGConstants.ICON_LOOKUP_URL + host + ".ico", imageViewHistory, false);
						}
	        	  	}
	          }
	          else {
	        	  DDGApplication.getImageDownloader().download(null, imageViewHistory, false);
				}     
        }
        else {
        	imageViewHistory.setImageResource(R.drawable.ddg_source_icon);
        }
        
        // query use button
        Button buttonHistory = (Button) view.findViewById(R.id.recentSearchPaste);
        
        // only for a recent search
        if(type.equals("R")) {
        	buttonHistory.setVisibility(View.VISIBLE);
	        buttonHistory.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(parentActivity instanceof DuckDuckGo) {
						// prepare searchbar with given data and wait for user action
						((DuckDuckGo) parentActivity).preSearch(data);
					}
				}
			});
        }
        else {
        	buttonHistory.setVisibility(View.GONE);
        	buttonHistory.setOnClickListener(null);
        }

    }
}