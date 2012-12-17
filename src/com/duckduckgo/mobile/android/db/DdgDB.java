package com.duckduckgo.mobile.android.db;

import java.util.ArrayList;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.AppShortInfo;

public class DdgDB {

	private static final String DATABASE_NAME = "ddg.db";
	private static final int DATABASE_VERSION = 4;
	private static final String FEED_TABLE = "feed";
	private static final String APP_TABLE = "apps";
	
	
	private SQLiteDatabase db;

	private SQLiteStatement insertStmt, insertStmtApp;
	// private static final String INSERT = "insert or ignore into " + FEED_TABLE + " (id,title,description,feed,url,imageurl,favicon,timestamp,category,type) values (?,?,?,?,?,?,?,?,?,?)";
	private static final String INSERT = "insert or replace into " + FEED_TABLE + " (id,title,description,feed,url,imageurl,favicon,timestamp,category,type) values (?,?,?,?,?,?,?,?,?,?)";
	
	private static final String APP_INSERT = "insert or replace into " + APP_TABLE + " (title,package) values (?,?)";

	
	public DdgDB(Context context) {
	      OpenHelper openHelper = new OpenHelper(context);
	      this.db = openHelper.getWritableDatabase();
	      this.insertStmt = this.db.compileStatement(INSERT);
	      this.insertStmtApp = this.db.compileStatement(APP_INSERT);
	}
	
	/**
	 * insert a FeedObject to SQLite database
	 * for feed items, the existing FeedObject is saved.
	 * for ordinary webpages (including SERP), (url, title) pair is received here.
	 * 
	 * if title == null (happens often e.g. pages only containing an image), URL is used for title field.
	 *   
	 * 
	 * @param e
	 * @return if FeedObject(url,title) both null, return -1. Return Insert execution result otherwise
	 */
	public long insert(FeedObject e) {
		String title = e.getTitle();		
		if(e.getUrl() == null)
			return -1l;
		
		if(title == null) {
			title = e.getUrl();
		}
	      this.insertStmt.bindString(1, e.getId());
	      this.insertStmt.bindString(2, title);
	      this.insertStmt.bindString(3, e.getDescription());
	      this.insertStmt.bindString(4, e.getFeed());
	      this.insertStmt.bindString(5, e.getUrl());
	      this.insertStmt.bindString(6, e.getImageUrl());
	      this.insertStmt.bindString(7, e.getFavicon());
	      this.insertStmt.bindString(8, e.getTimestamp());
	      this.insertStmt.bindString(9, e.getCategory());
	      this.insertStmt.bindString(10, e.getType());
	      long result = this.insertStmt.executeInsert();
	      return result;
	}
	
	public long insertApp(AppShortInfo appInfo) {
	      this.insertStmtApp.bindString(1, appInfo.name);
	      this.insertStmtApp.bindString(2, appInfo.packageName);
	      long result = this.insertStmtApp.executeInsert();
	      return result;
	}
	
	public void deleteApps() {
	      this.db.delete(APP_TABLE, null, null);
	}
	
//	public long update(FeedObject e) {
//	      ContentValues args = new ContentValues();
//	      args.put("text", e.text);
//	      return this.db.update(FEED_TABLE, args, "id=" + String.valueOf(e.id), null);
//	}

	public void deleteAll() {
	      this.db.delete(FEED_TABLE, null, null);
	}
	
	public int deleteById(String id) {
	      return this.db.delete(FEED_TABLE, "id=?", new String[]{id});
	}
	
	public void deleteByUrl(String url) {
	      this.db.delete(FEED_TABLE, "url=?", new String[]{url});
	}
	
	private FeedObject getFeedObject(Cursor c) {
		return new FeedObject(c.getString(0), c.getString(1), c.getString(2), c.getString(3),
				c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9));
	}
	
	private AppShortInfo getAppShortInfo(Cursor c) {
		return new AppShortInfo(c.getString(0), c.getString(1));
	}
	
	public ArrayList<AppShortInfo> selectApps(String title){
		Cursor c = this.db.query(APP_TABLE, null, "title MATCH ?", new String[]{title+"*"} , null, null, null);
		if(c.moveToFirst()) {
			ArrayList<AppShortInfo> apps = new ArrayList<AppShortInfo>(20);
			do {
				apps.add(getAppShortInfo(c));
			} while(c.moveToNext());

			return apps;
		}
		
		return null;
	}
	
	public ArrayList<FeedObject> selectAll(){
		Cursor c = this.db.query(FEED_TABLE, null, null, null , null, null, null);
		if(c.moveToFirst()) {
			ArrayList<FeedObject> feeds = new ArrayList<FeedObject>(30);
			do {
				feeds.add(getFeedObject(c));
			} while(c.moveToNext());

			return feeds;
		}
		
		return null;
	}
	
	public FeedObject selectById(String id){
		Cursor c = this.db.query(FEED_TABLE, null, "id=?", new String[]{id} , null, null, null);
		if(c.moveToFirst()) {
			return getFeedObject(c);
		}
		return null;
	}
	
	public FeedObject selectByIdType(String id, String type){
		Cursor c = this.db.query(FEED_TABLE, null, "id=? AND type = ?", new String[]{id,type} , null, null, null);
		if(c.moveToFirst()) {
			return getFeedObject(c);
		}
		return null;
	}
	
	public ArrayList<FeedObject> selectByType(String type){
		if(type == null) {
			return null;
		}
		
		ArrayList<FeedObject> feeds = new ArrayList<FeedObject>(20);
		Cursor c = this.db.query(FEED_TABLE, null, "type = ?", new String[]{type}, null, null, null, null);
		if (c.moveToFirst()) {
			do {
				FeedObject e = getFeedObject(c);
				feeds.add(e);
			} while (c.moveToNext());
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}
		
		if(feeds.isEmpty()){
			return null;
		}

		return feeds;
	}
	
	public ArrayList<FeedObject> selectByType(Set<String> types){
		if(types == null || types.isEmpty()) {
			return null;
		}
		
		ArrayList<FeedObject> feeds = new ArrayList<FeedObject>(20);
		
		String query = "";
		if(types.size() > 1) {
			for(int i=0;i<types.size()-1;i++) {
				query += "type = ? OR ";
			}
		}
		query += "type = ?";
		
		String[] typeArray = (String[]) types.toArray(new String[types.size()]);
		
		Cursor c = this.db.query(FEED_TABLE, null, query, typeArray, null, null, null, null);
		if (c.moveToFirst()) {
			do {
				FeedObject e = getFeedObject(c);
				feeds.add(e);
			} while (c.moveToNext());
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}
		
		if(feeds.isEmpty()){
			return null;
		}

		return feeds;
	}
	
	
	private static class OpenHelper extends SQLiteOpenHelper {

	      OpenHelper(Context context) {
	         super(context, DATABASE_NAME, null, DATABASE_VERSION);
	      }

		    @Override
		  	public void onCreate(SQLiteDatabase db) {
		  			  
		  			  db.execSQL("CREATE TABLE " + FEED_TABLE + "(" 
		  			    +"id VARCHAR(300) UNIQUE, "
		  			    +"title VARCHAR(300), "
		  			    +"description VARCHAR(300), "
		  			    +"feed VARCHAR(300), "
		  			    +"url VARCHAR(300), "
		  			    +"imageurl VARCHAR(300), "
		  			    +"favicon VARCHAR(300), "
		  			    +"timestamp VARCHAR(300), "
		  			    +"category VARCHAR(300), "
		  			    +"type VARCHAR(300)"
		  			    +")"
		  			    );
		  			  
		  			  db.execSQL("CREATE INDEX idx_id ON " + FEED_TABLE + " (id) ");
		  			  db.execSQL("CREATE INDEX idx_idtype ON " + FEED_TABLE + " (id, type) ");
		  			  
		  			db.execSQL("CREATE VIRTUAL TABLE " + APP_TABLE + " USING FTS3 (" 
			  			    +"title VARCHAR(300), "
			  			    +"package VARCHAR(300) "
			  			    +")"
			  			    );

		  	}
	
		  	@Override
		  	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		  		db.execSQL("DROP TABLE IF EXISTS " + FEED_TABLE);
		  		db.execSQL("DROP TABLE IF EXISTS " + APP_TABLE);
		  		onCreate(db);
		  	}
	}
	
	public void close(){
		db.close();
	}
	
	public SQLiteDatabase getSQLiteDB() {
		return db;
	}

}
