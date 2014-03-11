package com.duckduckgo.mobile.android.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.objects.history.HistoryObject;
import com.duckduckgo.mobile.android.util.AppShortInfo;
import com.duckduckgo.mobile.android.util.DDGUtils;
import com.duckduckgo.mobile.android.util.PreferencesManager;

public class DdgDB {

	private static final String DATABASE_NAME = "ddg.db";
	private static final int DATABASE_VERSION = 14;
	private static final String FEED_TABLE = "feed";
	private static final String APP_TABLE = "apps";
	private static final String HISTORY_TABLE = "history";
	private static final String SAVED_SEARCH_TABLE = "saved_search";
	
	
	private SQLiteDatabase db;

	private SQLiteStatement insertStmtApp;
	
	private static final String APP_INSERT = "insert or replace into " + APP_TABLE + " (title,package) values (?,?)";
	
	// if type = recent search, data = query.  if type = web page / feed item, data = title, url is target
	// extraType is for feed source
//	private static final String HISTORY_INSERT = "insert or replace into " + HISTORY_TABLE + " (type, data, url, extraType) values (?,?,?,?)";

	
	public DdgDB(Context context) {
	      OpenHelper openHelper = new OpenHelper(context);
	      this.db = openHelper.getWritableDatabase();
	      this.insertStmtApp = this.db.compileStatement(APP_INSERT);
	}
	
	public long insertSavedSearch(String query) {
		if(query == null)
			return -1L;
		
		ContentValues contentValues = new ContentValues();				 
		contentValues.put("query", query);
		// delete old record if exists
		this.db.delete(SAVED_SEARCH_TABLE, "query=?", new String[]{query});
		return this.db.insert(SAVED_SEARCH_TABLE, null, contentValues);
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
	public long insert(FeedObject e, String hidden) {
		String title = e.getTitle();		
		String url = e.getUrl();
		if(url == null || url.length() == 0)
			return -1l;
		
		if(title == null) {
			title = e.getUrl();
		}
		
		ContentValues contentValues = new ContentValues();
		contentValues.put("_id", e.getId());
		Log.v("insert", e.getId());
		contentValues.put("title", title);
		Log.v("insert", title);
		contentValues.put("description", e.getDescription());
		Log.v("insert", e.getDescription());
		contentValues.put("feed", e.getFeed());
		Log.v("insert", e.getFeed());
		contentValues.put("url", e.getUrl());
		Log.v("insert", e.getUrl());
		contentValues.put("imageurl", e.getImageUrl());
		Log.v("insert", e.getImageUrl());
		contentValues.put("favicon", e.getFavicon());
		Log.v("insert", e.getFavicon());
		contentValues.put("timestamp", e.getTimestamp());
		Log.v("insert", e.getTimestamp());
		contentValues.put("category", e.getCategory());
		Log.v("insert", e.getCategory());
		contentValues.put("type", e.getType());
		Log.v("insert", e.getType());
		contentValues.put("articleurl", e.getArticleUrl());
		Log.v("insert", e.getArticleUrl());
		contentValues.put("hidden", hidden);
		Log.v("insert", hidden);
		long result = this.db.insert(FEED_TABLE, null, contentValues);
		return result;
	}
	
	/**
	 * Insert feed item, using visibility setting from the object itself
	 * @param e feed item
	 * @return
	 */
	public long insert(FeedObject e) {
		// hidden = False, F
		return this.insert(e, e.getHidden());
	}
	
	/**
	 * Ordinary item Save operation - keep Saved item VISIBLE  
	 * @param e
	 * @return
	 */
	public long insertVisible(FeedObject e) {
		// hidden = False, F
		return this.insert(e, "F");
	}
	
	/**
	 * default item Save for browsed feed items - HIDDEN
	 * when Save is used this will become VISIBLE 
	 * @param e
	 * @return
	 */
	public long insertHidden(FeedObject e) {
		// hidden = True, T
		return this.insert(e, "T");
	}
	
	public long insertApp(AppShortInfo appInfo) {
	      this.insertStmtApp.bindString(1, appInfo.name);
	      this.insertStmtApp.bindString(2, appInfo.packageName);
	      long result = this.insertStmtApp.executeInsert();
	      return result;
	}
	
	public long insertRecentSearch(String query) {
		if(PreferencesManager.getRecordHistory()) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("type", "R");
			contentValues.put("data", query);
			contentValues.put("url", "");
			contentValues.put("extraType", "");
			contentValues.put("feedId", "");
			// delete old record if exists
			this.db.delete(HISTORY_TABLE, "type='R' AND data=?", new String[]{query});
			return this.db.insert(HISTORY_TABLE, null, contentValues);
		}
		return -1l;
	}
	
	public long insertHistoryObject(HistoryObject object) {
		if(object.getType().equals("F")) {
			return insertFeedItemToHistory(object.getData(), object.getUrl(), object.getExtraType(), object.getFeedId());
		}
		else if(object.getType().equals("W")) {
			return insertWebPage(object.getData(), object.getUrl());
		}
		else if(object.getType().equals("R")) {
			return insertRecentSearch(object.getData());
		}
		return -1L;
	}
	
	public long insertWebPage(String title, String url) {
		if(PreferencesManager.getRecordHistory()) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("type", "W");
			contentValues.put("data", title);
			contentValues.put("url", url);
			contentValues.put("extraType", "");
			contentValues.put("feedId", "");
			// delete old record if exists
			this.db.delete(HISTORY_TABLE, "type='W' AND data=? AND url=?", new String[]{title, url});
			return this.db.insert(HISTORY_TABLE, null, contentValues);
		}
		return -1l;
	}
	
	public long insertFeedItemToHistory(String title, String url, String extraType, String feedId) {		
		if(PreferencesManager.getRecordHistory()) {
	        ContentValues contentValues = new ContentValues();
	        contentValues.put("type", "F");
	        contentValues.put("data", title);
	        contentValues.put("url", url);
	        contentValues.put("extraType", extraType);
	        contentValues.put("feedId", feedId);
	        // delete old record if exists
	     	this.db.delete(HISTORY_TABLE, "type='F' AND feedId=?", new String[]{feedId});
	        long res = this.db.insertOrThrow(HISTORY_TABLE, null, contentValues);        
	        return res;
		}
		return -1l;
	}
	
	public long insertFeedItem(FeedObject feedObject) {
		this.deleteFeedObject(feedObject);
		long res = this.insert(feedObject);
		long resHistory = insertFeedItemToHistory(feedObject.getTitle(), feedObject.getUrl(), feedObject.getType(), feedObject.getId());
		if(res == -1l)
			res = resHistory;
        return res;
	}
	
	/**
	 * make a hidden feed item VISIBLE
	 * @param feedObject
	 * @return
	 */
	public long makeItemVisible(String id) {
	      ContentValues args = new ContentValues();
	      args.put("hidden", "F");
		return this.db.update(FEED_TABLE, args, "_id=?", new String[]{id});
	}
	
	/**
	 * make a hidden feed item VISIBLE
	 * @param feedObject
	 * @return
	 */
	public long makeItemHidden(String id) {
	      ContentValues args = new ContentValues();
	      args.put("hidden", "T");
		return this.db.update(FEED_TABLE, args, "_id=?", new String[]{id});
	}
	
	public void deleteApps() {
	      this.db.delete(APP_TABLE, null, null);
	}
	
	public void deleteHistory() {
	      this.db.delete(HISTORY_TABLE, null, null);
	}
	
//	public long update(FeedObject e) {
//	      ContentValues args = new ContentValues();
//	      args.put("text", e.text);
//	      return this.db.update(FEED_TABLE, args, "id=" + String.valueOf(e.id), null);
//	}

	public void deleteAll() {
	      this.db.delete(FEED_TABLE, null, null);
	}
	
	public int deleteFeedObject(FeedObject object) {
		  return this.db.delete(FEED_TABLE, "_id=?", new String[]{object.getId()});
	}
	
	public int deleteSavedSearch(String query) {
		  return this.db.delete(SAVED_SEARCH_TABLE, "query=?", new String[]{query});
	}
	
	public int deleteHistoryByDataUrl(String data, String url) {
	      return this.db.delete(HISTORY_TABLE, "data=? AND url=?", new String[]{data, url});
	}
	
	public int deleteHistoryByFeedId(String feedId) {
	      return this.db.delete(HISTORY_TABLE, "feedId=?", new String[]{feedId});
	}
	
	private FeedObject getFeedObject(Cursor c) {
		final String id = c.getString(c.getColumnIndex("_id"));
		final String title = c.getString(c.getColumnIndex("title"));
		final String description = c.getString(c.getColumnIndex("description"));
		final String feed = c.getString(c.getColumnIndex("feed"));
		final String url = c.getString(c.getColumnIndex("url"));
		final String imageurl = c.getString(c.getColumnIndex("imageurl"));
		final String favicon = c.getString(c.getColumnIndex("favicon"));
		final String timestamp = c.getString(c.getColumnIndex("timestamp"));
		final String category = c.getString(c.getColumnIndex("category"));
		final String type = c.getString(c.getColumnIndex("type"));
		final String articleurl = c.getString(c.getColumnIndex("articleurl"));
		final String hidden = c.getString(c.getColumnIndex("hidden"));
		return new FeedObject(id, title, description, feed, url, imageurl, favicon, timestamp, category, type, articleurl, "", hidden);
	}
	
	private AppShortInfo getAppShortInfo(Cursor c) {
		return new AppShortInfo(c.getString(0), c.getString(1));
	}
	
	private HistoryObject getHistoryObject(Cursor c) {
		return new HistoryObject(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4));
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
	
	/**
	 * for checking feed items
	 * @param id
	 * @return
	 */
	public boolean isSaved(String id) {
		Cursor c = this.db.query(FEED_TABLE, null, "_id=? AND hidden='F'", new String[]{id} , null, null, null);
		return c.moveToFirst();
	}
	
	/**
	 * for checking saved results
	 * @param id
	 * @return
	 */
	public boolean isSavedSearch(String query) {
		if(query == null)
			return false;
		
		Cursor c = this.db.query(SAVED_SEARCH_TABLE, null, "query=?", new String[]{query} , null, null, null);
		return c.moveToFirst();
	}
	
	/**
	 * for checking ordinary web pages
	 * @param pageTitle
	 * @param pageUrl
	 * @return
	 */
	public boolean isSaved(String pageTitle, String pageUrl) {
		if(pageUrl == null)
			return false;
		
		if(pageTitle == null)
			pageTitle = "";
			
		Cursor c = this.db.query(FEED_TABLE, null, "title=? AND url=? AND hidden='F'", new String[]{pageTitle, pageUrl} , null, null, null);
		return c.moveToFirst();
	}
	
	/**
	 * for checking ordinary web pages
	 * @param pageTitle
	 * @param pageUrl
	 * @return
	 */
	public boolean isSavedInHistory(String data, String url) {
		if(url == null)
			return false;
		
		if(data == null)
			data = "";
			
		Cursor c = this.db.query(HISTORY_TABLE, null, "data=? AND url=?", new String[]{data, url} , null, null, null);
		return c.moveToFirst();
	}
	
	public boolean isQueryInHistory(String query) {
		if(query == null)
			return false;
			
		Cursor c = this.db.query(HISTORY_TABLE, null, "data=? AND url='' AND type='R'", new String[]{query} , null, null, null);
		return c.moveToFirst();
	}
	
	public FeedObject selectFeedById(String id){
		Cursor c = this.db.query(FEED_TABLE, null, "_id=?", new String[]{id} , null, null, null);
		if(c.moveToFirst()) {
			return getFeedObject(c);
		}
		return null;
	}
	
	public boolean existsVisibleFeedById(String id){
		Cursor c = this.db.query(FEED_TABLE, new String[]{"_id"}, "_id=? AND hidden='F'", new String[]{id} , null, null, null);
		if(c.moveToFirst()) {
			return true;
		}
		return false;
	}
	
	public boolean existsAllFeedById(String id){
		Cursor c = this.db.query(FEED_TABLE, new String[]{"_id"}, "_id=?", new String[]{id} , null, null, null);
		if(c.moveToFirst()) {
			return true;
		}
		return false;
	}
	
	public FeedObject selectById(String id){
		Cursor c = this.db.query(FEED_TABLE, null, "_id=? AND hidden='F'", new String[]{id} , null, null, null);
		if(c.moveToFirst()) {
			return getFeedObject(c);
		}
		return null;
	}
	
	public FeedObject selectHiddenById(String id){
		Cursor c = this.db.query(FEED_TABLE, null, "_id=? AND hidden='T'", new String[]{id} , null, null, null);
		if(c.moveToFirst()) {
			return getFeedObject(c);
		}
		return null;
	}
	
	public FeedObject selectByIdType(String id, String type){
		Cursor c = this.db.query(FEED_TABLE, null, "_id=? AND type = ? AND hidden='F'", new String[]{id,type} , null, null, null);
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
		Cursor c = this.db.query(FEED_TABLE, null, "type = ? AND hidden='F'", new String[]{type}, null, null, null, null);
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
	
	public ArrayList<HistoryObject> selectHistory(){
		Cursor c = this.db.query(HISTORY_TABLE, null, null, null , null, null, null);
		if(c.moveToFirst()) {
			ArrayList<HistoryObject> historyItems = new ArrayList<HistoryObject>(30);
			do {
				historyItems.add(getHistoryObject(c));
			} while(c.moveToNext());

			return historyItems;
		}
		
		return null;
	}
	
	public Cursor getCursorSearchHistory() {
		return this.db.query(HISTORY_TABLE, null, "type='R'", null , null, null, "_id DESC");
	}
	
	public Cursor getCursorStoryHistory() {
		return this.db.query(HISTORY_TABLE, null, "type='F'", null , null, null, "_id DESC");
	}
	
	public Cursor getCursorHistory() {
		return this.db.query(HISTORY_TABLE, null, null, null , null, null, "_id DESC");
	}
	
	public Cursor getCursorSavedSearch() {
		return this.db.query(SAVED_SEARCH_TABLE, null, null, null , null, null, "_id DESC");
	}
	
	public Cursor getCursorStoryFeed() {
		return this.db.query(FEED_TABLE, null, "NOT feed='' AND hidden='F'", null , null, null, null);
	}

	
	
	private static class OpenHelper extends SQLiteOpenHelper {

	      OpenHelper(Context context) {
	         super(context, DATABASE_NAME, null, DATABASE_VERSION);
	      }
	      
	      	private void dropTables(SQLiteDatabase db) {
	      		db.execSQL("DROP TABLE IF EXISTS " + FEED_TABLE);
		  		db.execSQL("DROP TABLE IF EXISTS " + APP_TABLE);
		  		db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);
		  		db.execSQL("DROP TABLE IF EXISTS " + SAVED_SEARCH_TABLE);
	      	}
	      	
	      	private void createFeedTable(SQLiteDatabase db) {
	      		db.execSQL("CREATE TABLE " + FEED_TABLE + "(" 
		  			    +"_id VARCHAR(300) UNIQUE, "
		  			    +"title VARCHAR(300), "
		  			    +"description VARCHAR(300), "
		  			    +"feed VARCHAR(300), "
		  			    +"url VARCHAR(300), "
		  			    +"imageurl VARCHAR(300), "
		  			    +"favicon VARCHAR(300), "
		  			    +"timestamp VARCHAR(300), "
		  			    +"category VARCHAR(300), "
		  			    +"type VARCHAR(300), "
		  			    +"articleurl VARCHAR(300), "
		  			    +"hidden CHAR(1)"
		  			    +")"
		  			    );
		  			  
		  			  db.execSQL("CREATE INDEX idx_id ON " + FEED_TABLE + " (_id) ");
		  			  db.execSQL("CREATE INDEX idx_idtype ON " + FEED_TABLE + " (_id, type) ");
	      	}
	      	
	      	private void createAppTable(SQLiteDatabase db) {
	      		db.execSQL("CREATE VIRTUAL TABLE " + APP_TABLE + " USING FTS3 (" 
		  			    +"title VARCHAR(300), "
		  			    +"package VARCHAR(300) "
		  			    +")"
		  			    );
	      	}
	      	
	      	private void createHistoryTable(SQLiteDatabase db) {
	      		db.execSQL("CREATE TABLE " + HISTORY_TABLE + "("
	  					+"_id INTEGER PRIMARY KEY, "
		  			    +"type VARCHAR(300), "
		  			    +"data VARCHAR(300), "
		  			    +"url VARCHAR(300), "
		  			    +"extraType VARCHAR(300), "
		  			    +"feedId VARCHAR(300)"
		  			    +")"
		  			    );
	      	}
	      	
	      	private void createSavedSearchTable(SQLiteDatabase db) {
	  			  db.execSQL("CREATE TABLE " + SAVED_SEARCH_TABLE + "(_id INTEGER PRIMARY KEY, query VARCHAR(300) UNIQUE)");  
	      	}

		    @Override
		  	public void onCreate(SQLiteDatabase db) {		  			  
		  			createFeedTable(db);	
		  			createAppTable(db);
		  			createHistoryTable(db);		  			
		  			createSavedSearchTable(db); 
		  	}
	
		  	@Override
		  	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		  		if(oldVersion == 4 && newVersion >= 12) {		  			
	  				ContentValues contentValues = new ContentValues();	  	
		  			
		  			// shape old FEED_TABLE like the new, and rename it as FEED_TABLE_old
		  			db.execSQL("DROP INDEX IF EXISTS idx_id");
		      		db.execSQL("DROP INDEX IF EXISTS idx_idtype");
		  			db.execSQL("ALTER TABLE " + FEED_TABLE + " RENAME TO " + FEED_TABLE + "_old");
		  			
		  			dropTables(db);
		  			onCreate(db);
		  			
		  			SharedPreferences sharedPreferences = DDGApplication.getSharedPreferences();
		  					  			
		  			// ***** recent queries *******
		  			List<String> recentQueries = DDGUtils.loadList(sharedPreferences, "recentsearch");
		  			Collections.reverse(recentQueries);
		  			for(String query : recentQueries) {
		  				// insertRecentSearch
		  				contentValues.clear();
		  				contentValues.put("type", "R");
		  				contentValues.put("data", query);
		  				contentValues.put("url", "");
		  				contentValues.put("extraType", "");
		  				contentValues.put("feedId", "");
		  				db.insert(HISTORY_TABLE, null, contentValues);		  				
		  			}
		  			// ****************************
		  			
		  			// ****** saved search ********
		  			Cursor c = db.query(FEED_TABLE + "_old", new String[]{"url"}, "feed=''", null, null, null, null);
		  			while(c.moveToNext()) {
		  				final String url = c.getString(0);
		  				final String query = DDGUtils.getQueryIfSerp(url);
		  				if(query == null)
		  					continue;
		  				contentValues.clear();
		  				contentValues.put("query", query);
		  				db.insert(SAVED_SEARCH_TABLE, null, contentValues);		  				
		  			}
		  			// *****************************
		  					  					  					  			
		  			// ***** saved feed items *****
		  			db.execSQL("DELETE FROM " + FEED_TABLE + "_old WHERE feed='' ");
		  			db.execSQL("INSERT INTO " + FEED_TABLE + " SELECT *,'','F' FROM " + FEED_TABLE + "_old");
		  			db.execSQL("DROP TABLE IF EXISTS " + FEED_TABLE + "_old");
		  			// ****************************
		  					  					  		
		  		}
		  		else if(oldVersion == 12 && newVersion >= 14) {		  			
		  			// shape old FEED_TABLE like the new, and rename it as FEED_TABLE_old
		  			db.execSQL("DROP INDEX IF EXISTS idx_id");
		      		db.execSQL("DROP INDEX IF EXISTS idx_idtype");
		  			db.execSQL("ALTER TABLE " + FEED_TABLE + " RENAME TO " + FEED_TABLE + "_old");
		  			
		  			db.execSQL("DROP TABLE IF EXISTS " + FEED_TABLE);
		  			createFeedTable(db);
		  			
		  			// ***** saved feed items *****
		  			db.execSQL("DELETE FROM " + FEED_TABLE + "_old WHERE feed='' ");
		  			db.execSQL("INSERT INTO " + FEED_TABLE + " SELECT _id, title, description, feed, url, imageurl," +
		  					"favicon, timestamp, category, type, '' AS articleurl, hidden FROM " + FEED_TABLE + "_old");
		  			db.execSQL("DROP TABLE IF EXISTS " + FEED_TABLE + "_old");
		  			// ****************************
		  		}
		  		else {
		  			dropTables(db);
			  		onCreate(db);
		  		}
		  	}
	}
	
	public void close(){
		db.close();
	}
	
	public SQLiteDatabase getSQLiteDB() {
		return db;
	}

}
