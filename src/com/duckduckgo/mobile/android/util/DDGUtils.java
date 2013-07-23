package com.duckduckgo.mobile.android.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;
import ch.boye.httpclientandroidlib.HttpEntity;

import com.actionbarsherlock.view.MenuItem;
import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.FileCache;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.objects.FeedObject;

public final class DDGUtils {
	
	public static DisplayStats displayStats;
	
	public static boolean saveArray(SharedPreferences prefs, String[] array, String arrayName) {   
	    SharedPreferences.Editor editor = prefs.edit();  
	    editor.putInt(arrayName +"_size", array.length);  
	    for(int i=0;i<array.length;i++)  
	        editor.putString(arrayName + "_" + i, array[i]);  
	    return editor.commit();  
	} 
	
	public static boolean saveList(SharedPreferences prefs, List<String> list, String listName) {   
	    SharedPreferences.Editor editor = prefs.edit();  
	    editor.putInt(listName +"_size", list.size());  
	    int i=0;
	    for(String s : list) {
	    	editor.putString(listName + "_" + i, s);
	    	++i;
	    }
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
	
	public static LinkedList<String> loadList(SharedPreferences prefs, String listName) {  
	    int size = prefs.getInt(listName + "_size", 0);  
	    LinkedList<String> list = new LinkedList<String>();  
	    for(int i=0;i<size;i++)  {
	    	list.add(prefs.getString(listName + "_" + i, null));
	    }
	    return list;  
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
	
	static int calculateInSampleSize(BitmapFactory.Options bitmapOptions, int reqWidth, int reqHeight) {
		final int height = bitmapOptions.outHeight;
		final int width = bitmapOptions.outWidth;
		int sampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			sampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return sampleSize;
	}
	
	public static Bitmap decodeImage(String filePath) {
		//Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		int scale=calculateInSampleSize(o, displayStats.maxItemWidthHeight, displayStats.maxItemWidthHeight);

		BitmapFactory.Options options=new BitmapFactory.Options();
		//Decode with inSampleSize
		options.inSampleSize=scale;
		options.inPurgeable = true;
		options.inInputShareable = true;

		synchronized (DDGControlVar.DECODE_LOCK) {
			Bitmap result = BitmapFactory.decodeFile(filePath, options);
			return result;
		}
	}
	
	public static boolean downloadAndSaveBitmapToCache(AsyncTask<?, ?, ?> task, String url, String targetName) {
		final String TAG = "downloadAndSaveBitmapToCache";
		
		FileCache fileCache = DDGApplication.getFileCache();
		
		try {

			if (task.isCancelled()) return false;
				
			HttpEntity entity = null;
			try {
				entity = DDGNetworkConstants.mainClient.doGet(url);
				if (entity != null) {																			
					Log.v("SAVE", "Saving stream to internal file: " + url);
					fileCache.saveHttpEntityToCache(targetName, entity);
			    	return true;
				}
			} 
			catch(DDGHttpException conex) {
				Log.e(TAG, "Http Call Returned Bad Status. " + conex.getHttpStatus());
				throw conex;
			}
		} catch (DDGHttpException conException) {
			Log.e(TAG, conException.getMessage(), conException);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		return false;
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
	  
	  public static Bitmap roundCorners(Bitmap bitmap, float radius) {
          Paint paint = new Paint();    
		  paint.setAntiAlias(true);
		  
		  Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				  bitmap.getHeight(), bitmap.getConfig());
		  Canvas canvas = new Canvas(output);

		  final int color = 0xff424242;
		  final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		  final RectF rectF = new RectF(rect);

		  canvas.drawARGB(0, 0, 0, 0);
		  paint.setColor(color);
		  canvas.drawRoundRect(rectF, radius, radius, paint);

		  paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		  canvas.drawBitmap(bitmap, rect, rect, paint);

		  return output;
	  }
	  
	  public static List<AppShortInfo> getInstalledComponents(Context context) {
		  final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		  mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
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
	  	  
		/**
		 * Checks to see if URL is DuckDuckGo SERP
		 * Returns the query if it's a SERP, otherwise null
		 * 
		 * @param url
		 * @return
		 */
		static public String getQueryIfSerp(String url) {
			if(!isSerpUrl(url)) {
                return null;
            }
			
			Uri uri = Uri.parse(url);
			String query = uri.getQueryParameter("q");
			if(query != null)
				return query;
			
			String lastPath = uri.getLastPathSegment();
			if(lastPath == null)
				return null;
			
			if(!lastPath.contains(".html")) {
				return lastPath.replace("_", " ");
			}
			
			return null;
		}

    public static boolean isSerpUrl(String url) {
        return url.contains("duckduckgo.com");
    }

    /**
		 * Read cached sources from file cache
		 * 
		 * @return Cached source set, if not readable from cache returns null
		 */
		static public Set<String> getCachedSources() {
			String body = DDGApplication.getFileCache().getStringFromInternal(DDGConstants.SOURCE_JSON_PATH);
			if(body == null)
				return null;
			
			try {
				JSONArray json = new JSONArray(body);
				Set<String> cachedSources = new HashSet<String>();

				for (int i = 0; i < json.length(); i++) {
					JSONObject nextObj = json.getJSONObject(i);
					if (nextObj != null) {
						String id = nextObj.getString("id");

						if(id != null && !id.equals("null")){
							cachedSources.add(id);
						}
					}
				}
				
				return cachedSources;
				
			} catch (JSONException e) {
				return null;
			}
		}
		
	private static boolean isIntentSafe(Context context, Intent intent) {
		// Verify it resolves
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
		return activities.size() > 0;
	}
	
	public static void execIntentIfSafe(Context context, Intent intent) {
		if(DDGUtils.isIntentSafe(context, intent)) {
        	context.startActivity(intent);
        }
        else {
        	Toast.makeText(context, R.string.ErrorActivityNotFound, Toast.LENGTH_SHORT).show();
        }
	}
	
	
	
    /**
     * save feed by object or by the feed id
     * 
     * @param feedObject
     * @param pageFeedId
     */
    public static void itemSaveFeed(FeedObject feedObject, String pageFeedId) {
    	if(feedObject != null) {
    		if(DDGApplication.getDB().existsAllFeedById(feedObject.getId())) {
    			DDGApplication.getDB().makeItemVisible(feedObject.getId());
    		}
    		else {
    			DDGApplication.getDB().insertVisible(feedObject);
    		}
    	}
    	else if(pageFeedId != null && pageFeedId.length() != 0){
    		DDGApplication.getDB().makeItemVisible(pageFeedId);
    	}
    }
    
    public static void itemSaveSearch(String query) {
    	DDGApplication.getDB().insertSavedSearch(query);
    }
	
    public static int dpToPixel(DisplayMetrics displayMetrics, int dp) {
    	return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) dp, displayMetrics);
    }
    
    
    
    public static android.view.MenuItem getMenuItem(final MenuItem item) {
	      return new android.view.MenuItem() {
	         @Override
	         public int getItemId() {
	            return item.getItemId();
	         }

	         public boolean isEnabled() {
	            return true;
	         }

	         @Override
	         public boolean collapseActionView() {
	            // TODO Auto-generated method stub
	            return false;
	         }

	         @Override
	         public boolean expandActionView() {
	            // TODO Auto-generated method stub
	            return false;
	         }

	         @Override
	         public ActionProvider getActionProvider() {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public View getActionView() {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public char getAlphabeticShortcut() {
	            // TODO Auto-generated method stub
	            return 0;
	         }

	         @Override
	         public int getGroupId() {
	            // TODO Auto-generated method stub
	            return 0;
	         }

	         @Override
	         public Drawable getIcon() {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public Intent getIntent() {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public ContextMenuInfo getMenuInfo() {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public char getNumericShortcut() {
	            // TODO Auto-generated method stub
	            return 0;
	         }

	         @Override
	         public int getOrder() {
	            // TODO Auto-generated method stub
	            return 0;
	         }

	         @Override
	         public SubMenu getSubMenu() {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public CharSequence getTitle() {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public CharSequence getTitleCondensed() {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public boolean hasSubMenu() {
	            // TODO Auto-generated method stub
	            return false;
	         }

	         @Override
	         public boolean isActionViewExpanded() {
	            // TODO Auto-generated method stub
	            return false;
	         }

	         @Override
	         public boolean isCheckable() {
	            // TODO Auto-generated method stub
	            return false;
	         }

	         @Override
	         public boolean isChecked() {
	            // TODO Auto-generated method stub
	            return false;
	         }

	         @Override
	         public boolean isVisible() {
	            // TODO Auto-generated method stub
	            return false;
	         }

	         @Override
	         public android.view.MenuItem setActionProvider(ActionProvider actionProvider) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setActionView(View view) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setActionView(int resId) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setAlphabeticShortcut(char alphaChar) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setCheckable(boolean checkable) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setChecked(boolean checked) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setEnabled(boolean enabled) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setIcon(Drawable icon) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setIcon(int iconRes) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setIntent(Intent intent) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setNumericShortcut(char numericChar) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setShortcut(char numericChar, char alphaChar) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public void setShowAsAction(int actionEnum) {
	            // TODO Auto-generated method stub

	         }

	         @Override
	         public android.view.MenuItem setShowAsActionFlags(int actionEnum) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setTitle(CharSequence title) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setTitle(int title) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setTitleCondensed(CharSequence title) {
	            // TODO Auto-generated method stub
	            return null;
	         }

	         @Override
	         public android.view.MenuItem setVisible(boolean visible) {
	            // TODO Auto-generated method stub
	            return null;
	         }
	      };
	   }
}
