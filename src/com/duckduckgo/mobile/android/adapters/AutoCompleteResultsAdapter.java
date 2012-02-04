package com.duckduckgo.mobile.android.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.duckduckgo.mobile.android.DDGConstants;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class AutoCompleteResultsAdapter extends ArrayAdapter<String> implements Filterable {

	protected final String TAG = "AutoCompleteResultsAdapter";
	public List<String> mResultList = Collections.synchronizedList(new ArrayList<String>());
	
	public AutoCompleteResultsAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	@Override
	public int getCount() {
		return mResultList.size();
	}
	
	@Override
	public String getItem(int index) {
		return mResultList.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter webFilter = new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();

				ArrayList<String> newResults = new ArrayList<String>();
				
				if (constraint != null) {

					//TODO: Check if this constraint is already in the cache
					
					JSONArray json = getJSONResultForConstraint(constraint);
					for (int i = 0; i < json.length(); i++) {
						try {
							JSONObject nextObj = json.getJSONObject(i);
							String item = nextObj.getString("phrase");
							if (item != null) {
								newResults.add(item);
							}
						} catch (JSONException e) {
							Log.e(TAG, "No JSON Object at index " + i);
						}
					}
					//TODO: Cache the results for later
				}
				
				
				results.values = newResults;
				results.count = newResults.size();
				
				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				mResultList.clear();
				if (results != null && results.count > 0) {
					@SuppressWarnings("unchecked")
					ArrayList<String> newResults = (ArrayList<String>)results.values;
					mResultList.addAll(newResults);
					notifyDataSetChanged();
				} else {
					mResultList.clear();
					notifyDataSetInvalidated();
				}
			}
			
			private JSONArray getJSONResultForConstraint(CharSequence constraint) {
				//TODO: We should move this into a separate class for retrieving results...
				//TODO: Move over the logic for attaching to TOR from the old project
				JSONArray json = null;
				String body = null;
				try {
					HttpClient client = new HttpClient();
					client.getParams().setParameter(HttpMethodParams.USER_AGENT, DDGConstants.USER_AGENT);
					HttpMethod get = new GetMethod(DDGConstants.AUTO_COMPLETE_URL + constraint);

					int result = client.executeMethod(get);

					if (result != HttpStatus.SC_OK) {
						throw new Exception("Unable to execute query");
					}

					body = get.getResponseBodyAsString();

					json = new JSONArray(body);
				} catch (JSONException jex) {
					Log.e(TAG, jex.getMessage(), jex);
				} catch (HttpException httpException) {
					Log.e(TAG, httpException.getMessage(), httpException);
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}

				return json;
		    }
		};
		
		return webFilter;
	}
}
