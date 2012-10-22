package com.duckduckgo.mobile.android.db;

import java.util.ArrayList;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class DdgDB {

	private static final String DATABASE_NAME = "ddg.db";
	private static final int DATABASE_VERSION = 1;
	private static final String FEED_TABLE = "feed";
	
	
	private Context context;
	private SQLiteDatabase db;

	private SQLiteStatement insertStmt;
	// private static final String INSERT = "insert or ignore into " + FEED_TABLE + " (id,title,description,feed,url,imageurl,favicon,timestamp,category,type) values (?,?,?,?,?,?,?,?,?,?)";
	private static final String INSERT = "insert or replace into " + FEED_TABLE + " (id,title,description,feed,url,imageurl,favicon,timestamp,category,type) values (?,?,?,?,?,?,?,?,?,?)";
	
	public DdgDB(Context context) {
	      this.context = context;
	      OpenHelper openHelper = new OpenHelper(this.context);
	      this.db = openHelper.getWritableDatabase();
	      this.insertStmt = this.db.compileStatement(INSERT);
	}
	
	public long insert(FeedObject e) {
	      this.insertStmt.bindString(1, e.getId());
	      this.insertStmt.bindString(2, e.getTitle());
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
	
//	public long update(FeedObject e) {
//	      ContentValues args = new ContentValues();
//	      args.put("text", e.text);
//	      return this.db.update(FEED_TABLE, args, "id=" + String.valueOf(e.id), null);
//	}

	public void deleteAll() {
	      this.db.delete(FEED_TABLE, null, null);
	}
	
	public void deleteById(int id) {
		String sid = String.valueOf(id);
	      this.db.delete(FEED_TABLE, "id=?", new String[]{sid});
	}
	
	public void deleteByUrl(String url) {
	      this.db.delete(FEED_TABLE, "url=?", new String[]{url});
	}
	
	private FeedObject getFeedObject(Cursor c) {
		return new FeedObject(c.getString(0), c.getString(1), c.getString(2), c.getString(3),
				c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9));
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
		ArrayList<FeedObject> feeds = new ArrayList<FeedObject>(50);
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
		ArrayList<FeedObject> feeds = new ArrayList<FeedObject>(50);
		
		String query = "";
		if(types.size() > 1) {
			for(int i=0;i<types.size()-1;i++) {
				query += "type = ? OR ";
			}
		}
		query += "type = ?";
		
		String[] typeArray = (String[]) types.toArray();
		
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

		  	}
	
		  	@Override
		  	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		  		db.execSQL("DROP TABLE IF EXISTS feed");
		  		onCreate(db);
		  	}
	}
	
	public void close(){
		db.close();
	}
}
