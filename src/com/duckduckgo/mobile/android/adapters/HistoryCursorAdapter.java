package com.duckduckgo.mobile.android.adapters;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.util.DDGConstants;

public class HistoryCursorAdapter extends CursorAdapter {

    public HistoryCursorAdapter(Context context, Cursor c) {
        super(context, c);
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

        TextView textViewHistory = (TextView) view.findViewById(R.id.recentSearchText);
        textViewHistory.setText(cursor.getString(cursor.getColumnIndex("data")));
        
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
    }
}