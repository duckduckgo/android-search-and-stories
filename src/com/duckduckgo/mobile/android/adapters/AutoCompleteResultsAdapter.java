package com.duckduckgo.mobile.android.adapters;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.params.CoreProtocolPNames;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.DDGConstants;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.ImageDownloader;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.objects.SuggestObject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class AutoCompleteResultsAdapter extends ArrayAdapter<String> implements Filterable {
	private final LayoutInflater inflater;
	
	protected final String TAG = "AutoCompleteResultsAdapter";
	public List<SuggestObject> mResultList = Collections.synchronizedList(new ArrayList<SuggestObject>());
	
	//TODO: We need an image downloader & cache that is held globally for the application
	// Should this be a singleton or part of ApplicationContext? probably AppContext...
	protected final ImageDownloader imageDownloader;
	
	public AutoCompleteResultsAdapter(Context context) {
		super(context, 0);
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageDownloader = DDGApplication.getImageDownloader();
	}
	
	@Override
	public int getCount() {
		return mResultList.size();
	}
	
	@Override
	public String getItem(int index) {
		SuggestObject obj = getSuggestionObject(index);
		if (obj != null) {
			return obj.getPhrase();
		} else {
			return null;
		}
	}
	
	public SuggestObject getSuggestionObject(int index) {
		return mResultList.get(index);
	}
	
	@Override
	public View getView(int position, View cv, ViewGroup parent) {
		if (cv == null) {
			cv = inflater.inflate(R.layout.autocomplete_list_layout, null);
			cv.setTag(new Holder((AsyncImageView)cv.findViewById(R.id.autoCompleteImage), (TextView)cv.findViewById(R.id.autoCompleteResultText), (TextView)cv.findViewById(R.id.autoCompleteDetailText)));
		}
		
		SuggestObject suggestion = getSuggestionObject(position);
		
		final Holder holder = (Holder) cv.getTag();
		
		if (suggestion != null) {
			holder.autoCompleteResult.setText(suggestion.getPhrase());
			holder.autoCompleteDetail.setText(suggestion.getSnippet());
			imageDownloader.download(suggestion.getImageUrl(), holder.autoCompleteImage, false);
		}
		
		return cv;
	}
	
	class Holder {
		final AsyncImageView autoCompleteImage;
		final TextView autoCompleteResult;
		final TextView autoCompleteDetail;
		
		public Holder(final AsyncImageView autoCompleteImage, final TextView autoCompleteResult, final TextView autoCompleteDetail) {
			this.autoCompleteImage = autoCompleteImage;
			this.autoCompleteResult = autoCompleteResult;
			this.autoCompleteDetail = autoCompleteDetail;
		}
	}

	@Override
	public Filter getFilter() {
		Filter webFilter = new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();

				ArrayList<SuggestObject> newResults = new ArrayList<SuggestObject>();
				
				if (constraint != null) {

					//TODO: Check if this constraint is already in the cache
										
					JSONArray json = getJSONResultForConstraint(constraint);
								
					for (int i = 0; i < json.length(); i++) {
						try {
							JSONObject nextObj = json.getJSONObject(i);
							SuggestObject item = new SuggestObject(nextObj);
							if (item != null) {
								newResults.add(item);
							}
						} catch (JSONException e) {
							Log.e(TAG, "No JSON Object at index " + i);
							Log.e(TAG, "Exception: " + e.getMessage());
							e.printStackTrace();
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
					ArrayList<SuggestObject> newResults = (ArrayList<SuggestObject>)results.values;
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
					String query = URLEncoder.encode(constraint.toString());
					body = DDGNetworkConstants.mainClient.doGetString(DDGConstants.AUTO_COMPLETE_URL + query);
					json = new JSONArray(body);
				} catch (JSONException jex) {
					Log.e(TAG, jex.getMessage(), jex);
				} catch (DDGHttpException conException) {
					Log.e(TAG, "Unable to execute query" + conException.getMessage(), conException);
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}

				return json;
		    }
		};
		
		return webFilter;
	}
}
