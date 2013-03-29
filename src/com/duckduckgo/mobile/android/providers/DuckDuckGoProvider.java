package com.duckduckgo.mobile.android.providers;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class DuckDuckGoProvider extends ContentProvider {

    private static final String TAG = "DuckDuckGoProvider";
    //private static final int SUGGEST_URI_MATCH = 1;
    //private UriMatcher suggestionUriMatcher = null;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate() {
        /*
        suggestionUriMatcher = new UriMatcher(0);
        suggestionUriMatcher.addURI("duckduckgo", SearchManager.SUGGEST_URI_PATH_QUERY, SUGGEST_URI_MATCH);
        */
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // the uri looks like "content://duckduckgo/search_suggest_query/f?limit=50"
        // the last path segment is whatever the user has typed
        String query = uri.getLastPathSegment();

        if ("search_suggest_query".equals(query)) {
            return null;
        }
/*
		final String url = "https://duckduckgo.com/ac/?q="+query;
		HttpClient client = new HttpClient();
		HttpMethod get = new GetMethod(url);
		int result;
		String body = null;

		try {
			result = client.executeMethod(get);
			if (result != HttpStatus.SC_OK) {
				Log.i(TAG, "HttpStatus: "+result);
			}

			body = get.getResponseBodyAsString();
			Log.i(TAG, "autocomplete body: "+body);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONArray json = null;

		try {
			json = new JSONArray(body);
		} catch (JSONException jex) {
			Log.e(TAG, "JSON Exception: " + jex.toString());
			body = "[" + body + "]";
		}
*/

/*		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse resp = null;
		ByteArrayOutputStream baostream = null;
		try {
			resp = client.execute(new HttpGet("http://va-l3.duckduckgo.com:6767/face/suggest/?q="+query));
			org.apache.http.StatusLine status = resp.getStatusLine();
			if (status.getStatusCode() != 200) {
			    Log.d(TAG, "HTTP error, invalid server status code: " + resp.getStatusLine());
			}
		} catch (ClientProtocolException e) {
			resp = null;
			e.printStackTrace();
		} catch (IOException e) {
			resp = null;
			e.printStackTrace();
		}

		HttpEntity data = resp.getEntity();
		InputStream content = data.getContent();
		int len = 0;
		final byte[] buffer = new byte[1024];
		while ((len=content.read(buffer)) != 1) {
		  baostream.write(buffer,0,len);
		}
*/

        // we'll simulate through a MatrixCursor.
        MatrixCursor cursor = new MatrixCursor(new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1,
                                                            SearchManager.SUGGEST_COLUMN_TEXT_2,
                                                            SearchManager.SUGGEST_COLUMN_QUERY,
                                                            "_id"});

		cursor.addRow(new Object[]{"Search DuckDuckGo", query, query, 0});
/*
		  for (int i = 0; i < json.length(); i++) {
			try {
				String phrase = json.getJSONObject(i).getString("phrase");
				String snippet = json.getJSONObject(i).getString("snippet");
				String image = json.getJSONObject(i).getString("image");
				Log.i(TAG,"Phrase:"+phrase);
				Log.i(TAG,"Snippet:"+snippet);
				Log.i(TAG,"Image:"+image);
				cursor.addRow(new Object[]{phrase, snippet, phrase, i});
			} catch (JSONException e) {
				Log.e(TAG,"JSONExeption:"+e.getMessage());
				e.printStackTrace();
			}
		  }
*/

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }
}
