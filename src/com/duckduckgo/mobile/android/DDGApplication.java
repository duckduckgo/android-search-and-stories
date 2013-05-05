package com.duckduckgo.mobile.android;

import java.io.File;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;

import com.duckduckgo.mobile.android.db.DdgDB;
import com.duckduckgo.mobile.android.download.FileCache;
import com.duckduckgo.mobile.android.download.ImageCache;
import com.duckduckgo.mobile.android.download.ImageDownloader;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.duckduckgo.mobile.android.util.SCREEN;

@ReportsCrashes(formKey="",
formUri = "https://collect.duckduckgo.com/collect.js?type=crash",
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
resNotifTickerText = R.string.crash_notif_ticker_text,
resNotifTitle = R.string.crash_notif_title,
resNotifText = R.string.crash_notif_text,
resDialogText = R.string.crash_dialog_text,
resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
)
public class DDGApplication extends Application {

	private static final ImageCache imageCache = new ImageCache(null);
	private static FileCache fileCache = null;
	private static final ImageDownloader imageDownloader = new ImageDownloader(imageCache);
	private static SharedPreferences sharedPreferences = null;
	private static DdgDB db = null;
	
	private static String DB_FOLDER_NAME = "database";
	
	/**
	 * Changes after application upgrade
	 * Also records current app version code to "appVersionCode" sharedPreferences 
	 * 
	 * @param appVersionCode
	 */
	private void onUpgrade(int appVersionCode) {
		// clear old sharedPreferences values, types can conflict (int -> float)
		PreferencesManager.clearValues();
		PreferencesManager.migrateAllowedSources();
		PreferencesManager.saveAppVersionCode(appVersionCode);
	}
	
	@Override
	public void onCreate() {
		ACRA.init(this);
		
		super.onCreate();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		db = new DdgDB(this);
		fileCache = new FileCache(this.getApplicationContext());
		imageCache.setFileCache(fileCache);
		
		try {
			PackageInfo pInfo;
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String appVersion = pInfo.versionName;
			int appVersionCode = pInfo.versionCode;
			int oldVersionCode = PreferencesManager.getAppVersionCode();
			
			Log.v("APP", "oldversion: " + oldVersionCode + " new: " + appVersionCode);
			if(oldVersionCode == 0 || oldVersionCode != appVersionCode) {				
				// upgrade
				onUpgrade(appVersionCode);
			}
			
			DDGConstants.USER_AGENT.replace("%version", appVersion);
		} catch (NameNotFoundException e) {
			// at least specify new Android version
			DDGConstants.USER_AGENT.replace("%version", "2+");
		}
		DDGNetworkConstants.initialize();
		
		// set Helvetica Neue Medium
//		DDGConstants.TTF_HELVETICA_NEUE_MEDIUM = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue_Medium.ttf");
		DDGConstants.TTF_ROBOTO_BOLD = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Bold.ttf");		
		DDGConstants.TTF_ROBOTO_MEDIUM = Typeface.createFromAsset(getAssets(), "fonts/Roboto_Medium.ttf");	
		
		DDGControlVar.START_SCREEN = SCREEN.getByCode(Integer.valueOf(PreferencesManager.getStartScreen()));
		DDGControlVar.regionString = PreferencesManager.getRegion();
		DDGControlVar.alwaysUseExternalBrowser = PreferencesManager.getExternalBrowser();
		DDGControlVar.fontPrevProgress = PreferencesManager.getFontPrevProgress(DDGConstants.FONT_SEEKBAR_MID);
		DDGControlVar.fontProgress = DDGControlVar.fontPrevProgress;
		
		DDGControlVar.userAllowedSources = PreferencesManager.getUserAllowedSources();
		DDGControlVar.userDisallowedSources = PreferencesManager.getUserDisallowedSources();		
		
		String strReadArticles = PreferencesManager.getReadArticles();
		if(strReadArticles != null){
			for(String strId : strReadArticles.split("-")){
				if(strId != null && strId.length() != 0){
					DDGControlVar.readArticles.add(strId);
				}
			}
		}
     
	}
	
	public static ImageDownloader getImageDownloader() {
		return imageDownloader;
	}
	
	public static ImageCache getImageCache() {
		return imageCache;
	}
	
	public static SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}
	
	public static FileCache getFileCache() {
		return fileCache;
	}
	
	public static DdgDB getDB() {
		return db;
	}
	
	// method overridden to put DB in database folder cleanable upon uninstall
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
	CursorFactory factory) {
	    File externalFilesDir = getDir(DB_FOLDER_NAME, MODE_PRIVATE);
	    File dbFile = new File(externalFilesDir, name);
	    return SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.CREATE_IF_NECESSARY);
	}
	
	@Override
	public void onLowMemory() {
		DDGApplication.getImageCache().purge();
		super.onLowMemory();
	}
}
