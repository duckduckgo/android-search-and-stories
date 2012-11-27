package com.duckduckgo.mobile.android.tasks;

import java.io.InputStream;

import android.os.AsyncTask;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.download.FileCache;
import com.duckduckgo.mobile.android.listener.MimeDownloadListener;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;

public class MimeDownloadTask extends AsyncTask<Void, Void, String> {
	
	private MimeDownloadListener listener = null;
	String url, fileName;
	
	public MimeDownloadTask(MimeDownloadListener listener, String url, String fileName) {
		this.listener = listener;
		this.url = url;
		this.fileName = fileName;
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			InputStream is = DDGNetworkConstants.mainClient.doGetStream(url);
			FileCache fileCache = DDGApplication.getFileCache();
			fileCache.saveStreamToInternal(fileName, is);
			return fileCache.getPath(fileName);
		}
		catch(DDGHttpException e){		
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {		
		if (this.listener != null) {
			if (result != null) {
				this.listener.onDownloadComplete(result);
			} else {
				this.listener.onDownloadFailed();
			}
		}
	}

}
