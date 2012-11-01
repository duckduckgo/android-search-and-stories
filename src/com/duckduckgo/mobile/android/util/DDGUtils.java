package com.duckduckgo.mobile.android.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import android.widget.Toast;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;

public final class DDGUtils {
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
	
	public static Bitmap downloadBitmap(AsyncTask<?, ?, ?> task, String url) {
		final String TAG = "downloadBitmap";
		
		try {

			if (task.isCancelled()) return null;
				
			InputStream inputStream = null;
			try {
				inputStream = DDGNetworkConstants.mainClient.doGetStream(url);
				if (inputStream != null) {
					// FIXME large bitmaps cause OutOfMemoryErrors
					// see: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
					return BitmapFactory.decodeStream(inputStream);
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
		    try {
		    	dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
		    }
		    catch(Exception e){
		    	Log.e("UTIL", e.getMessage());
		    	Log.e("UTIL", newWidth + " " + newHeight);
		    	Log.e("UTIL", "Source: " + source);
		    	Log.e("UTIL", "Source: " + source.getConfig());
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
		  
		  ArrayList<AppShortInfo> labels = new ArrayList<AppShortInfo>();
		  for(ResolveInfo rInfo : pkgAppsList) {
				  String label = "";
				  label += rInfo.loadLabel(context.getPackageManager());
				  labels.add(new AppShortInfo(label, rInfo.activityInfo.packageName));
		  }
		  
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
