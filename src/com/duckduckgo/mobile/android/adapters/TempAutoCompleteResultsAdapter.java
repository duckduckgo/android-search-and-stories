package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class TempAutoCompleteResultsAdapter extends ArrayAdapter<SuggestObject> implements Filterable {
    private final LayoutInflater inflater;

    protected final String TAG = "AutoCompleteResultsAdapter";
    public List<SuggestObject> mResultList = Collections.synchronizedList(new ArrayList<SuggestObject>());

    RoundCornersTransformation roundTransform;
    ScaleWidthTransformation scaleTransform;

    private CharSequence userInput = "";

    public TempAutoCompleteResultsAdapter(Context context) {
        super(context, 0);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Picasso transformations
        roundTransform = new RoundCornersTransformation();
        scaleTransform = new ScaleWidthTransformation();
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public SuggestObject getItem(int index) {
        SuggestObject suggestObject = getSuggestionObject(index);
        if (suggestObject != null) {
            return suggestObject;
        } else {
            return null;
        }
    }

    public SuggestObject getSuggestionObject(int index) {
        return mResultList.get(index);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            //view = inflater.inflate(R.layout.autocomplete_list_layout, null);
            view = inflater.inflate(R.layout.temp_autocomplete_list_layout, null);
            //view = inflater.inflate(R.layout.temp_search_layout, null);
            view.setTag(new Holder(
                    (TextView)view.findViewById(R.id.item_text),
                    (AsyncImageView)view.findViewById(R.id.item_icon),
                    (ImageButton)view.findViewById(R.id.item_paste)));
        }

        final SuggestObject suggestion = getSuggestionObject(position);

        final Holder holder = (Holder) view.getTag();

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
            if(imageUrl != null && imageUrl.length() != 0) {
                roundTransform.setRadius(holder.icon.getCornerRadius());
                scaleTransform.setTarget(holder.icon);

                Picasso.with(getContext())
                        .load(suggestion.getImageUrl())
                        .placeholder(R.drawable.time)
                        .error(R.drawable.time)
                        .transform(scaleTransform)
                        .transform(roundTransform)
                        .into(holder.icon);
            }
            else {
                holder.icon.setImageResource(R.drawable.time);
            }

            /*
            holder.autoCompleteResult.setText(suggestion.getPhrase());
            final int pixelValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    (float) 2.0, getContext().getResources().getDisplayMetrics());
            holder.autoCompleteResult.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.mainTextSize+pixelValue);
            holder.autoCompleteDetail.setText(suggestion.getSnippet());
            holder.autoCompleteDetail.setTextSize(TypedValue.COMPLEX_UNIT_PX, DDGControlVar.mainTextSize);
            holder.plusImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phrase = suggestion.getPhrase();
                    if(phrase != null) {
                        BusProvider.getInstance().post(new SuggestionPasteEvent(suggestion.getPhrase()));
                    }
                }
            });
            Drawable acDrawable = suggestion.getDrawable();
            String imageUrl = suggestion.getImageUrl();
            if(imageUrl != null && imageUrl.length() != 0) {
                roundTransform.setRadius(holder.autoCompleteImage.getCornerRadius());
                scaleTransform.setTarget(holder.autoCompleteImage);

                Picasso.with(getContext())
                        .load(suggestion.getImageUrl())
                        .placeholder(R.drawable.ic_default_search_result)
                        .error(R.drawable.ic_default_search_result)
                        .transform(scaleTransform)
                        .transform(roundTransform)
                        .into(holder.autoCompleteImage);
            }
            else {
                holder.autoCompleteImage.setImageResource(R.drawable.ic_default_search_result);
            }*/
        }
        return view;
    }

    class Holder {
        final TextView result;
        final AsyncImageView icon;
        final ImageButton plus;
        public Holder(final TextView result, final AsyncImageView icon, final ImageButton plus) {
            this.result = result;
            this.icon = icon;
            this.plus = plus;
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
                        Context context = getContext();
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
