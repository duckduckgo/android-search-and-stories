package com.duckduckgo.mobile.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.FileCache;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;

public final class DDGUtils {
	
	public static int feedItemWidth = 0, feedItemHeight = 0;
	public static int maxItemWidthHeight = 0;
	
    private static final Pattern PUNC_PATTERN = Pattern.compile("[:.,/]");
	
	public static boolean saveArray(SharedPreferences prefs, String[] array, String arrayName) {   
	    SharedPreferences.Editor editor = prefs.edit();  
	    editor.putInt(arrayName +"_size", array.length);  
	    for(int i=0;i<array.length;i++)  
	        editor.putString(arrayName + "_" + i, array[i]);  
	    return editor.commit();  
	} 
	
	public static boolean saveSet(SharedPreferences prefs, Set<String> set, String setName) {   		
	    SharedPreferences.Editor editor = prefs.edit();  
	    final int setSize = set.size();
	    editor.putInt(setName +"_size", setSize);  
	    int i=0;
	    for(String s : set)  {
	        editor.putString(setName + "_" + i, s);
	        ++i;
	    }
	    return editor.commit();  
	} 
	
	public static String[] loadArray(SharedPreferences prefs, String arrayName) {  
	    int size = prefs.getInt(arrayName + "_size", 0);  
	    String array[] = new String[size];  
	    for(int i=0;i<size;i++)  
	        array[i] = prefs.getString(arrayName + "_" + i, null);  
	    return array;  
	}  
	
	public static Set<String> loadSet(SharedPreferences prefs, String setName) {  
	    final int size = prefs.getInt(setName + "_size", 0);  
	    Set<String> set = new HashSet<String>(size);
	    for(int i=0;i<size;i++)  
	        set.add(prefs.getString(setName + "_" + i, null));  
	    return set;  
	}  
	
	public static boolean existsSet(SharedPreferences prefs, String setName) {
		return prefs.contains(setName + "_size");
	}
	
	public static void deleteSet(SharedPreferences prefs, String setName) {  
	    final int size = prefs.getInt(setName + "_size", 0);  
	    Editor editor = prefs.edit();
	    for(int i=0;i<size;i++)  
	    	editor.remove(setName + "_" + i);  
	    editor.remove(setName + "_size");
	    editor.commit();  
	} 
	
	@TargetApi(10)
	private static Bitmap decodeRegion(FileDescriptor fd) {
		Log.v("REGION","region decoder : ");
		int useWidth, useHeight;
		
		useWidth = feedItemWidth;
		useHeight = feedItemHeight;
		
		//Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, o);
        
        // use original sizes if image is not bigger than feed item view
        if(o.outWidth < feedItemWidth) useWidth = o.outWidth;
        if(o.outHeight < feedItemHeight) useHeight = o.outHeight;
		
		BitmapRegionDecoder decoder;
		try {
			decoder = BitmapRegionDecoder.newInstance(fd, false);
			Log.v("REGION","IMAGE width height: " + useWidth + " " + useHeight);
			Rect innerTile = new Rect(0, 0, useWidth, useHeight);
			Bitmap region = decoder.decodeRegion(innerTile, null);
			return region;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Bitmap decodeImage(FileDescriptor fd, String filePath) {
		final String TAG = "decodeImage";

		//Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, o);
        
        Log.v(TAG,"IMAGE width height: " + o.outWidth + " " + o.outHeight);
        
        //The new size we want to scale to

        //Find the correct scale value. It should be the power of 2.
        int scale=1;
//        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
//        while(o.outWidth/scale>=REQUIRED_SIZE)
                
        while(o.outWidth/scale/2>=maxItemWidthHeight || o.outHeight/scale/2>=maxItemWidthHeight)
            scale*=2;
        
        Log.v(TAG,"Scale: " + scale);
					
			BitmapFactory.Options options=new BitmapFactory.Options();
	        //Decode with inSampleSize
	        options.inSampleSize=scale;
	        
	        try {
				Bitmap result = BitmapFactory.decodeFile(filePath, options);
				return result;
	        }
	        catch(OutOfMemoryError oomError) {
	        	oomError.printStackTrace();
	        	return null;
	        }
	}
	
	public static Bitmap downloadBitmap(AsyncTask<?, ?, ?> task, String url) {
		final String TAG = "downloadBitmap";
		
		FileCache fileCache = DDGApplication.getFileCache();
		FileDescriptor fileDesc;
		Bitmap resultBitmap;
		
		try {

			if (task.isCancelled()) return null;
				
			InputStream inputStream = null;
			try {
				inputStream = DDGNetworkConstants.mainClient.doGetStream(url);
				if (inputStream != null) {
					// FIXME large bitmaps cause OutOfMemoryErrors
					// see: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
										
					Matcher matcher = PUNC_PATTERN.matcher(url);
					String nURL = matcher.replaceAll("_");
					String fname = "tmp" + nURL;
										
					fileCache.saveStreamToInternal(fname, inputStream);
					fileDesc = fileCache.getFd(fname);
					inputStream.close();
					
					String filePath = fileCache.getPath(fname);
					
					if(fileDesc == null)
						return null;
					
					// for API level 10, there is BitmapRegionDecoder
					// http://developer.android.com/reference/android/graphics/BitmapRegionDecoder.html
					
				    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD_MR1) {
				    	resultBitmap = decodeRegion(fileDesc);
				    }
				    else {
				    	resultBitmap = decodeImage(fileDesc, filePath);
				    }
				    fileCache.removeFile(fname);
			    	return resultBitmap;
				}
			} 
			catch(DDGHttpException conex) {
				Log.e(TAG, "Http Call Returned Bad Status. " + conex.getHttpStatus());
				throw conex;
			}
			finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} catch (DDGHttpException conException) {
			Log.e(TAG, conException.getMessage(), conException);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		return null;
	}
	
	  public static String readStream(InputStream is) {
		    try {
		      ByteArrayOutputStream bo = new ByteArrayOutputStream();
		      int i = is.read();
		      while(i != -1) {
		        bo.write(i);
		        i = is.read();
		      }
		      return bo.toString();
		    } catch (IOException e) {
		      return "";
		    }
	}
	  
	  public static Intent newEmailIntent(String toAddress, String subject, String body, String cc) {
	      Intent intent = new Intent(Intent.ACTION_SEND);
	      intent.putExtra(Intent.EXTRA_EMAIL, new String[] { toAddress });
	      intent.putExtra(Intent.EXTRA_TEXT, body);
	      intent.putExtra(Intent.EXTRA_SUBJECT, subject);
	      intent.putExtra(Intent.EXTRA_CC, cc);
	      intent.setType("message/rfc822");
	      return intent;
	  }
	  
	  public static Intent newTelIntent(String telurl) {
	      Intent intent = new Intent(Intent.ACTION_DIAL);
	      // FIXME : need to check XXX is really a short number in tel:XXX 
	      intent.setData(Uri.parse(telurl));
	      return intent;
	  }
	  
	  public static String getBuildInfo(Context context) {		  
		  // get app version info
		  String appVersion = "";
		  PackageInfo pInfo;
		  try {
			  pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			  appVersion = pInfo.versionName + " (" + pInfo.versionCode + ")\n";
		  } catch (NameNotFoundException e) {}
		
		  String board = "Board: " + Build.BOARD + "\n";
		  String bootloader = "Bootloader: " + Build.BOOTLOADER + "\n";
		  String brand = "Brand: " + Build.BRAND + "\n";
		  String device = "Device: " + Build.DEVICE + "\n";
		  String display = "Display: " + Build.DISPLAY + "\n";
		  String product = "Product: " + Build.PRODUCT + "\n";
		  String model = "Model: " + Build.MODEL + "\n";
		  String manufacturer = "Manufacturer: " + Build.MANUFACTURER + "\n";
		  
		  return appVersion + board + bootloader + brand + device + display + product + model + manufacturer;
	  }
	  
	  public static Bitmap scaleCenterCrop(Bitmap source, int newWidth, int newHeight) {
		  Paint paint = new Paint();
		  paint.setAntiAlias(true);
		  paint.setDither(true);
		  paint.setFilterBitmap(true);
		  
		    int sourceWidth = source.getWidth();
		    int sourceHeight = source.getHeight();

		    // Compute the scaling factors to fit the new height and width, respectively.
		    // To cover the final image, the final scaling will be the bigger 
		    // of these two.
		    float xScale = (float) newWidth / sourceWidth;
		    float yScale = (float) newHeight / sourceHeight;
		    float scale = Math.max(xScale, yScale);

		    // Now get the size of the source bitmap when scaled
		    float scaledWidth = scale * sourceWidth;
		    float scaledHeight = scale * sourceHeight;

		    // Let's find out the upper left coordinates if the scaled bitmap
		    // should be centered in the new size give by the parameters
		    float left = (newWidth - scaledWidth) / 2;
		    float top = (newHeight - scaledHeight) / 2;

		    // The target rectangle for the new, scaled version of the source bitmap will now
		    // be
		    RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

		    // Finally, we create a new bitmap of the specified size and draw our new,
		    // scaled bitmap onto it.
		    Bitmap dest = null;
		    Bitmap.Config destConfig = source.getConfig();
		    if(destConfig == null) {
		    	destConfig = Bitmap.Config.ARGB_8888;
		    }
 
		    try {
		    	// just in case illegal arguments (width, height) arrive
		    	dest = Bitmap.createBitmap(newWidth, newHeight, destConfig);
		    }
		    catch(Exception e){
		    	return null;
		    }
		    
		    if(dest == null)
		    	return null;
		    
		    Canvas canvas = new Canvas(dest);
		    canvas.drawBitmap(source, null, targetRect, paint);

		    return dest;
		}
	  
	  public static Bitmap getRoundedCornerImage(Bitmap bitmap, float radius, int targetWidth, int targetHeight) {
		  // crop input 
          
          Paint paint = new Paint();    
		  paint.setAntiAlias(true);
      
          Bitmap targetBitmap = scaleCenterCrop(bitmap, targetWidth, targetHeight);
          if(targetBitmap == null) {
        	// the case when scaleCenterCrop fails
        	  return null;
          }
		  
          // round
		  
		  Bitmap output = Bitmap.createBitmap(targetBitmap.getWidth(),
				  targetBitmap.getHeight(), Config.ARGB_8888);
		  Canvas canvas = new Canvas(output);

		  final int color = 0xff424242;
		  final Rect rect = new Rect(0, 0, targetBitmap.getWidth(), targetBitmap.getHeight());
		  final RectF rectF = new RectF(rect);

		  canvas.drawARGB(0, 0, 0, 0);
		  paint.setColor(color);
		  canvas.drawRoundRect(rectF, radius, radius, paint);

		  paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		  canvas.drawBitmap(targetBitmap, rect, rect, paint);

		  return output;

		  }
	  
	  public static List<AppShortInfo> getInstalledComponents(Context context) {
		  final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		  mainIntent.addCategory(Intent.CATEGORY_DEFAULT);
		  mainIntent.removeCategory(Intent.CATEGORY_TEST);
		  final List<ResolveInfo> pkgAppsList = context.getPackageManager().queryIntentActivities( mainIntent, 0);		 
		  
		  HashSet<String> packageNameSet = new HashSet<String>();
		  
		  ArrayList<AppShortInfo> labels = new ArrayList<AppShortInfo>();
//		  Log.v("APP","al: ...start...");
		  for(ResolveInfo rInfo : pkgAppsList) {
			  String packageName = rInfo.activityInfo.packageName;
				  String label = "";
				  label += rInfo.loadLabel(context.getPackageManager());
				  if(!packageNameSet.contains(label + "-" + packageName)) {
//				  	  Log.v("APP", "al: " + label + " " + packageName);
					  labels.add(new AppShortInfo(label, packageName));
					  packageNameSet.add(label + "-" + packageName);
				  }
		  }
//		  Log.v("APP","al: ...end...");
		  
		  return labels;
	  }
	  
	  public static void launchApp(Context context, String packageName) {
			Intent mIntent = context.getPackageManager().getLaunchIntentForPackage(
					packageName);
			if (mIntent != null) {
				try {
					context.startActivity(mIntent);
				} catch (ActivityNotFoundException err) {
					Toast t = Toast.makeText(context,
							R.string.ErrorAppNotFound, Toast.LENGTH_SHORT);
					t.show();
				}
			}
		}
	  
	  public static void shareWebPage(Context context, String title, String url) {
		  Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "WatrCoolr URL: "+ url);
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
			sendIntent.setType("text/plain");
			context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.send_to)));
	  }
}
