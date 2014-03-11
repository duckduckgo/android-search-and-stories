package com.duckduckgo.mobile.android.download;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
import com.duckduckgo.mobile.android.listener.MimeDownloadListener;
import com.duckduckgo.mobile.android.tasks.MimeDownloadTask;
import com.duckduckgo.mobile.android.util.DDGUtils;

public class ContentDownloader {

	private DownloadManager downloadManager;
	private DuckDuckGo duckDuckGoActivity;

	public ContentDownloader(DuckDuckGo duckDuckGoActivity) {
		this.duckDuckGoActivity = duckDuckGoActivity;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			this.downloadManager = (DownloadManager) duckDuckGoActivity.getSystemService(DuckDuckGo.DOWNLOAD_SERVICE);
		}
	}

	@SuppressLint("NewApi")
	public void downloadContent(final String url, final String mimeType) {
		// use mimeType to figure out an extension for temporary file
		String extension = decideExtension(mimeType);
		String fileName = "down." + extension;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			Uri uri = Uri.parse(url);
			DownloadManager.Request request = new DownloadManager.Request(uri);
			// When downloading music and videos they will be listed in the
			// player
			// (Seems to be available since Honeycomb only)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				request.allowScanningByMediaScanner();
				// Notify user when download is completed
				// (Seems to be available since Honeycomb only)
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			}
			// Start download
			downloadManager.enqueue(request);
		} else {
			// manual download for devices below GINGERBREAD
			// TODO AsyncTask here
			MimeDownloadListener mimeListener = new MimeDownloadListener() {

				@Override
				public void onDownloadFailed() {
					// TODO Fail gracefully here... inform the user about failed
					// download!
					Toast.makeText(duckDuckGoActivity, R.string.ErrorDownloadFailed, Toast.LENGTH_LONG).show();
				}

				@Override
				public void onDownloadComplete(String filePath) {
					// intent to view content
					Intent viewIntent = new Intent(Intent.ACTION_VIEW);
					File file = new File(filePath);
					viewIntent.setDataAndType(Uri.fromFile(file), mimeType);
					viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					DDGUtils.execIntentIfSafe(duckDuckGoActivity, viewIntent);
				}
			};

			MimeDownloadTask mimeTask = new MimeDownloadTask(mimeListener, url, fileName);
			mimeTask.execute();
		}
	}

	private String decideExtension(final String mimeType) {
		int idxSlash = mimeType.indexOf('/') + 1;
		String ext = "tmp";
		if (idxSlash != -1) {
			ext = mimeType.substring(idxSlash);
		}
		return ext;
	}
}
