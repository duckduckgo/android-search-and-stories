package com.duckduckgo.mobile.android.download;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.graphics.Bitmap;
import android.util.Log;

import com.duckduckgo.mobile.android.tasks.DownloadBitmapTask;
import com.duckduckgo.mobile.android.util.DDGControlVar;

//TODO: Any way we can limit the number of simultaneous downloads? Do we need to?
//TODO: Any way we can find out that multiple objects are attempting to get the same item?
//TODO: Think we need a download service for those that these tasks can register and wait on, or some such...

public class ImageDownloader {
	private static final String TAG = "ImageDownloader";
	
	private final ImageCache cache;
	
    private Map<DownloadableImage, String> imageViews=Collections.synchronizedMap(new WeakHashMap<DownloadableImage, String>());
    
    private Executor executor;
    
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final int KEEP_ALIVE = 1;
    
    private ArrayList<DownloadBitmapTask> queuedTasks;
    
    // track running tasks in queue
    private Runnable taskWatcher;
    
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(10);
    
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };
	
	public ImageDownloader(ImageCache cache) {
		this.cache = cache;
		this.executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
			                    TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
		this.queuedTasks = new ArrayList<DownloadBitmapTask>(6);
		this.taskWatcher = new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					synchronized(DDGControlVar.taskCompleteSignal) {
						try {
							DDGControlVar.taskCompleteSignal.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if(DDGControlVar.taskCompleteSignal != null) {
							DownloadBitmapTask task = DDGControlVar.taskCompleteSignal.task;
							queuedTasks.remove(task);
						}
					}
				}
			}
		};
	}
	
    boolean imageViewReused(DownloadableImage image, String url){
        String tag=imageViews.get(image);
        if(tag==null || !tag.equals(url))
            return true;
        return false;
    }
	
	//TODO: Should take a Downloadable object
	public void download(String url, DownloadableImage image, boolean onlyUseMemCache) {
		if (url == null || url.length() == 0) {
			//Cancel anything downloading, set the image to default, and return
			cancelPreviousDownload(url, image);
			image.setDefault();			
			return;

		}
		imageViews.put(image, url);

		Bitmap bitmap = cache.getBitmapFromCache(url, onlyUseMemCache);
		
		if(cache.checkFail(url)){
			url = null;
		}
		

		if (bitmap == null) {
			Log.d(TAG, "Attempting download of URL: " + url);
			
			if(onlyUseMemCache){
				image.setBitmap(null);
				image.setMemCacheDrawn(false);
				return;
			}
			
			attemptDownload(url, image);
		} else {
			Log.d(TAG, "Using Cached Image for URL: " + url);
			cancelPreviousDownload(url, image);
			image.setBitmap(bitmap);
			
			if(onlyUseMemCache){
				image.setMemCacheDrawn(true);
			}
		}
	}
	
	private void attemptDownload(String url, DownloadableImage image) {
		if (url == null) {
			//No image to download!
			image.setDownloadBitmapTask(null);
			image.setBitmap(null);
			return;
		}
		
		if (!cancelPreviousDownload(url, image)) {
			Log.d(TAG, "Already downloading URL: " + url);
			//We are already downloading that exact image
			return;
		}
		
		if(imageViewReused(image,url))
            return;
		
		DownloadBitmapTask task = new DownloadBitmapTask(image, cache);
		image.setDownloadBitmapTask(task);
		image.setDefault();
		task.execute(url);
	}
	
	private boolean cancelPreviousDownload(String url, DownloadableImage image) {
		DownloadBitmapTask task = image.getDownloadBitmapTask();
		
		if (task != null) {
			String bitmapUrl = task.url;
			if ((bitmapUrl == null) || !bitmapUrl.equals(url)) {
				Log.v(TAG,"Canceling Prev: " + url);
				task.cancel(true);
			} else {
				this.queuedTasks.remove(task);
				return false;
			}
			
			this.queuedTasks.remove(task);
		}
				
		return true;
	}
	
	public void clearVisibleDownloads() {
		HashSet<DownloadableImage> removeList = new HashSet<DownloadableImage>();
		for(DownloadableImage image : imageViews.keySet()){
			DownloadBitmapTask task = image.getDownloadBitmapTask();
			
				if(task != null && !task.isCompleted){
					Log.v(TAG,"Canceling VIS: " + task.url);
					task.cancel(true);
					removeList.add(image);
				}

		}
		
		for(DownloadableImage image : removeList){
			imageViews.remove(image);
		}
	}
	
	// queue all downloads as background downloads
	public void queueUrls(final ArrayList<String> imageUrls) {	
				
		for(String url : imageUrls) {
			if(url == null)
				continue;
			DownloadBitmapTask task = new DownloadBitmapTask(null, cache);
			this.queuedTasks.add(task);
			task.executeOnExecutor(this.executor, url);
		}		
	}
	
	public void clearQueueDownloads() {
		ArrayList<DownloadBitmapTask> removeList = new ArrayList<DownloadBitmapTask>();
		for(DownloadBitmapTask task : queuedTasks) {
			Log.v(TAG,"Canceling QUEUE: " + task.url);
			task.cancel(true);
			removeList.add(task);
		}
		
		for(DownloadBitmapTask task : removeList) {
			this.queuedTasks.remove(task);
		}
	}
}
