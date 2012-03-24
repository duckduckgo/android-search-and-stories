package com.duckduckgo.mobile.android.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class FileCache {

	private final File externalImageDirectory;
	private final Context context;
	
	public FileCache(Context context) {
		this.context = context;
		externalImageDirectory = this.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES); 
	}
	
	public boolean saveBitmapAsFile(String name, Bitmap bitmap) {
		if (!allowedToSaveToFile())
			return false;
		
		File saveFile = new File(externalImageDirectory, name);

		//This will overwrite any existing files, i think...
		boolean saved = false;
		FileOutputStream os = null;
		try {
			Log.e("FileCache", "Saving File To Cache " + saveFile.getPath());
			os = new FileOutputStream(saveFile);
			bitmap.compress(CompressFormat.PNG, 100, os);
			os.flush();
			os.close();
			saved = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return saved;
	}
	
	public Bitmap getBitmapFromImageFile(String name) {
		if (!allowedToReadFromFile()) return null;
		
		File file = new File(externalImageDirectory, name);
		if (file.exists() && file.isFile()) {
			Log.e("FileCache", "Getting File from path " + file.getPath());
			return BitmapFactory.decodeFile(file.getPath());
		}
		
		return null;
	}
	
	public boolean allowedToSaveToFile() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		
		return false;
	}
	
	public boolean allowedToReadFromFile() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		
		return false;
	}
}
