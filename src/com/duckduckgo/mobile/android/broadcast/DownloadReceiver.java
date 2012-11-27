package com.duckduckgo.mobile.android.broadcast;

import java.io.File;
import java.net.URI;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("NewApi")
public class DownloadReceiver extends BroadcastReceiver {
	
	DownloadManager downloadManager = null;
	
	public DownloadReceiver() {
	}
	
	public DownloadReceiver(DownloadManager downloadManager) {
		this.downloadManager = downloadManager;
	}
	
	public void setDownloadManager(DownloadManager downloadManager) {
		this.downloadManager = downloadManager;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(downloadManager == null) {
			downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
		}
		
		Long dwnId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
		Uri uri = null;
		String mimeType = null;
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
			uri = downloadManager.getUriForDownloadedFile(dwnId);
			mimeType = downloadManager.getMimeTypeForDownloadedFile(dwnId);
		}
		else {
			Cursor c = downloadManager.query(new DownloadManager.Query().setFilterById(dwnId)); 
			int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
			if(status == DownloadManager.STATUS_SUCCESSFUL) {
				String localPath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
				mimeType = c.getString(c.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
				uri = Uri.parse("file:///"+localPath);
			}
		}

		// intent to view content
		Intent viewIntent = new Intent(Intent.ACTION_VIEW);
		File file = new File(URI.create(uri.toString()));
		viewIntent.setDataAndType(Uri.fromFile(file), mimeType); 
		viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(viewIntent);
		}
		catch(ActivityNotFoundException e) {
			Toast.makeText(context, R.string.ErrorActivityNotFound, Toast.LENGTH_LONG).show();
		}
	}
}