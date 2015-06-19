package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.events.pasteEvents.SuggestionPasteEvent;
import com.duckduckgo.mobile.android.image.transformations.RoundCornersTransformation;
import com.duckduckgo.mobile.android.image.transformations.ScaleWidthTransformation;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import com.duckduckgo.mobile.android.objects.SuggestObject;
import com.duckduckgo.mobile.android.util.AppShortInfo;
import com.duckduckgo.mobile.android.util.DDGConstants;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fgei on 6/19/15.
 */
public class RecyclerAutoCompleteResultsAdapter extends RecyclerView.Adapter<RecyclerAutoCompleteResultsAdapter.ViewHolder> implements Filterable {

    public static final String TAG = "recycler_autocomplete_results_adapter";

    public Context context;
    private LayoutInflater inflater;
    private List<SuggestObject> data = Collections.synchronizedList(new ArrayList<SuggestObject>());

    RoundCornersTransformation roundTransform;
    ScaleWidthTransformation scaleTransform;

    private CharSequence userInput = "";

    public RecyclerAutoCompleteResultsAdapter(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Picasso transformations
        roundTransform = new RoundCornersTransformation();
        scaleTransform = new ScaleWidthTransformation();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_autocomplete, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SuggestObject suggestion = getSuggestionObject(position);
        if (suggestion != null) {


            StringBuilder stringInput, stringSuggestion;
            stringInput = new StringBuilder();
            stringSuggestion = new StringBuilder();

            boolean keepCopying = true;
            for(int i=0; i<suggestion.getPhrase().length(); i++) {
                if(keepCopying && i<userInput.length() && userInput.charAt(i)==suggestion.getPhrase().charAt(i)) {
                    stringInput.append(userInput.charAt(i));
                } else {
                    keepCopying = false;
                    stringSuggestion.append(suggestion.getPhrase().charAt(i));
                }
            }

            Spannable word = new SpannableString(stringInput);
            word.setSpan(new ForegroundColorSpan(Color.parseColor("#212121")), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.result.setText(word);

            word = new SpannableString(stringSuggestion);
            word.setSpan(new ForegroundColorSpan(Color.parseColor("#A4A4A4")), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.result.append(word);

            String snippet = suggestion.getSnippet();
            if(snippet!=null && snippet.length()>0) {
                holder.detail.setText(suggestion.getSnippet());
                holder.detail.setVisibility(View.VISIBLE);
            } else {
                holder.detail.setVisibility(View.GONE);
            }

            //holder.icon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.time));
            holder.plus.setVisibility(View.VISIBLE);
            holder.plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phrase = suggestion.getPhrase();
                    if(phrase!=null) {
                        BusProvider.getInstance().post(new SuggestionPasteEvent(suggestion.getPhrase()));
                    }
                }
            });

            //Drawable acDrawable = suggestion.getDrawable();
            String imageUrl = suggestion.getImageUrl();
            if(imageUrl != null && imageUrl.length() != 0){// && !imageUrl.contains("search-suggestions_default.png")) {
                Log.e("aaa", "image url: " + imageUrl);
                roundTransform.setRadius(holder.icon.getCornerRadius());
                //scaleTransform.setTarget(holder.icon, 0.6);
                scaleTransform.setTarget((int) context.getResources().getDimension(R.dimen.bang_icon_dimen));

                Picasso.with(context)
                        .load(suggestion.getImageUrl())
                        .placeholder(null)
                        .transform(scaleTransform)
                        .transform(roundTransform)
                        .into(holder.icon);
            }
            else {
                holder.icon.setImageDrawable(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public SuggestObject getItem(int index) {
        SuggestObject suggestObject = getSuggestionObject(index);
        if (suggestObject != null) {
            return suggestObject;
        } else {
            return null;
        }
    }

    public SuggestObject getSuggestionObject(int index) {
        return data.get(index);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView result;
        final TextView detail;
        final AsyncImageView icon;
        final ImageButton plus;
        public ViewHolder(View v) {
            super(v);
            this.result = (TextView) v.findViewById(R.id.item_text);
            this.detail = (TextView) v.findViewById(R.id.item_text_detail);
            this.icon = (AsyncImageView) v.findViewById(R.id.item_icon);
            this.plus = (ImageButton) v.findViewById(R.id.item_paste);
        }
    }

    @Override
    public Filter getFilter() {
        Filter webFilter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<SuggestObject> newResults = new ArrayList<SuggestObject>();
                userInput = constraint;

                if (constraint != null) {
                    //TODO: Check if this constraint is already in the cache
                    JSONArray json = getJSONResultForConstraint(constraint);
                    // also search in apps
                    if(DDGControlVar.includeAppsInSearch) {
                        //Context context = constraint;
                        ArrayList<AppShortInfo> appResults = DDGApplication.getDB().selectApps(constraint.toString());
                        if(appResults != null) {
                            for(AppShortInfo appInfo : appResults) {
                                SuggestObject item = new SuggestObject(appInfo.name, appInfo.packageName, context);
                                if (item != null) {
                                    newResults.add(item);
                                }
                            }
                        }
                    }

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
                data.clear();
                if (results != null && results.count > 0) {
                    @SuppressWarnings("unchecked")
                    ArrayList<SuggestObject> newResults = (ArrayList<SuggestObject>)results.values;
                    data.addAll(newResults);
                    notifyDataSetChanged();
                } else {
                    data.clear();
                    //notifyDataSetInvalidated();
                    notifyDataSetChanged();
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
