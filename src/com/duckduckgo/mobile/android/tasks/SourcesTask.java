package com.duckduckgo.mobile.android.tasks;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.objects.SourcesObject;
import com.duckduckgo.mobile.android.util.DDGConstants;

public class SourcesTask extends AsyncTask<Void, Void, List<SourcesObject>> {

	private static String TAG = "SourcesTask";
	
	private SourcesListener listener = null;
	private ProgressDialog progressDialog = null;
	private Context context;
				
	public SourcesTask(Context context, SourcesListener listener) {
		this.context = context;
		this.listener = listener;
	}

    private String fetchSourcesJson(){
        String body = null;
        try {
            if (isCancelled()) {
                return null;
            }
            // get source response (type_info=1)
            body = DDGNetworkConstants.mainClient.doGetString(DDGConstants.SOURCES_URL);
            Log.v(TAG, body);
        } catch (DDGHttpException conException) {
            Log.e(TAG, "Unable to execute Query: " + conException.getMessage(), conException);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return body;
    }

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		progressDialog.setMessage(context.getResources().getString(R.string.Loading));
		progressDialog.show();
	}
	
	@Override
	protected List<SourcesObject> doInBackground(Void... arg0) {
        String sourcesJson = fetchSourcesJson();
        if(!TextUtils.isEmpty(sourcesJson)) {
            if (!isCancelled()) {
                return createSourceObjects(sourcesJson);
            }
        }
        return new ArrayList<SourcesObject>();
	}

    private ArrayList<SourcesObject> createSourceObjects(String sourcesJson){
        ArrayList<SourcesObject> feedObjects = new ArrayList<SourcesObject>();
        try {
            JSONArray jsonArray = new JSONArray(sourcesJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject != null) {
                    SourcesObject feed = new SourcesObject(jsonObject);
                    feedObjects.add(feed);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return feedObjects;
    }
	
	@Override
	protected void onPostExecute(List<SourcesObject> feed) {	
		
		if (this.listener != null) {
			if (feed != null) {
				this.listener.onSourcesRetrieved(feed);
			} else {
				this.listener.onSourcesRetrievalFailed();
			}
		}

		try {
			if (progressDialog!=null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public static interface SourcesListener {
		public void onSourcesRetrieved(List<SourcesObject> feed);
		public void onSourcesRetrievalFailed();
	}

}
