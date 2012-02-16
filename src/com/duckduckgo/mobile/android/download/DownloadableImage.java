package com.duckduckgo.mobile.android.download;

import android.graphics.Bitmap;

import com.duckduckgo.mobile.android.tasks.DownloadBitmapTask;

public interface DownloadableImage {
	public void setDownloadBitmapTask(DownloadBitmapTask task);
	public DownloadBitmapTask getDownloadBitmapTask();
	public void setBitmap(Bitmap bitmap);
	public void setDefault();
}
