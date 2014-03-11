package com.duckduckgo.mobile.android.tasks;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.db.DdgDB;
import com.duckduckgo.mobile.android.util.AppShortInfo;
import com.duckduckgo.mobile.android.util.DDGUtils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class ScanAppsTask extends AsyncTask<Void, Void, Void>{
	Context context;
	
	public ScanAppsTask(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(Void... params) {
		
		Log.v("SCAN", "indexing apps...");
		
		DdgDB db = DDGApplication.getDB();
		SQLiteDatabase sqlDB = db.getSQLiteDB();
		
		try{
			sqlDB.beginTransaction();
			db.deleteApps();		
			for(AppShortInfo appInfo : DDGUtils.getInstalledComponents(context)) {
				db.insertApp(appInfo);
			}
			sqlDB.setTransactionSuccessful();
		} catch (SQLException e) {
		} finally {
			sqlDB.endTransaction();
		}
		
		return null;
	}

}
