package com.duckduckgo.mobile.android.download;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.duckduckgo.mobile.android.tasks.DownloadBitmapTask;

//TODO: Instead of using DownloadDrawable, we can just subclass ImageView with an AsyncImageView or some such...
public class AsyncImageView extends ImageView implements DownloadableImage {
	private WeakReference<DownloadBitmapTask> downloadTaskReference;
	
	public AsyncImageView(Context context, AttributeSet attr) {
		super (context, attr);
	}
	
	public AsyncImageView(Context context) {
		super(context);
	}
	
	public void setDownloadBitmapTask(DownloadBitmapTask task) {
		downloadTaskReference = new WeakReference<DownloadBitmapTask>(task);
	}
	
	public DownloadBitmapTask getDownloadBitmapTask() {
		if (downloadTaskReference != null) {
			return downloadTaskReference.get();
		}
		return null;
	}
	
	public void setBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			setImageBitmap(bitmap);
		} else {
			setImageBitmap(null);
		}
	}
	
	public void setDefault() {
		setImageBitmap(null);
	}
}
