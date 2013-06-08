package com.duckduckgo.mobile.android.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.FileProcessor;

public class FileCache {

	private final File cacheDirectory;
	private final File externalImageDirectory;
	private final Context context;
	
	public FileCache(Context context) {
		this.context = context;
		cacheDirectory = this.context.getCacheDir();
		
		// deprecated from now on 2013-06-04 (Version Code: 45)
		externalImageDirectory = this.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES); 
	}
	
	public boolean saveBitmapAsFile(String name, Bitmap bitmap) {		
		File saveFile = new File(cacheDirectory, name);

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
		File file = new File(cacheDirectory, name);
		if (file.exists() && file.isFile()) {
			Log.d("FileCache", "Getting File from path " + file.getPath());
			synchronized (DDGControlVar.DECODE_LOCK) {
				return BitmapFactory.decodeFile(file.getPath());
			}
		}
		
		return null;
	}
	
	public boolean saveStringToInternal(String name, String file){		
		try {
			FileOutputStream fos = this.context.openFileOutput(name, Context.MODE_PRIVATE);
			fos.write(file.getBytes());
			fos.close();
			
			return true;
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean saveStreamToInternal(String name, InputStream is){		
		byte[] bucket = new byte[1024];
	    try {
			FileOutputStream fos = this.context.openFileOutput(name, Context.MODE_PRIVATE);
		      int readSize;
		      while((readSize = is.read(bucket)) != -1) {
		    	  fos.write(bucket,0,readSize);
		      }
		      fos.close();
		      return true;
		    } catch (IOException e) {
		    	e.printStackTrace();
		      return false;
		    }	
	}
	
	public String getPath(String name) {
		File f =  this.context.getFileStreamPath(name);
		if(f != null) {
			return f.getAbsolutePath();
		}
		return null;
	}
	
	public FileDescriptor getFd(String name) {
		try {
			return this.context.openFileInput(name).getFD();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public FileInputStream getFileInputStream(String name) {
		try {
			return this.context.openFileInput(name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void removeFile(String name) {
		this.context.deleteFile(name);
	}
	
	public String getStringFromInternal(String name){
		String result = null;
		
		try {
			FileInputStream fis = this.context.openFileInput(name);
			result = DDGUtils.readStream(fis);
			fis.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void processFromInternal(String name, FileProcessor processor) {
		
		try {
			FileInputStream fis = this.context.openFileInput(name);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String strLine;
			
			while ((strLine = br.readLine()) != null) {
				processor.processLine(strLine);
			}
			fis.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Remove files that have become unnecessary upon migration 
	 */
	public void removeThrashOnMigration() {
		if(this.externalImageDirectory != null) {
			File[] files = this.externalImageDirectory.listFiles();
			for(File file : files) {
				file.delete();
			}
		}
	}
}
