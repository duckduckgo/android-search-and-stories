package com.duckduckgo.mobile.android.adapters;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.ImageDownloader;
import com.duckduckgo.mobile.android.download.SourceHolder;
import com.duckduckgo.mobile.android.objects.SourcesObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.DDGUtils;


public class SourcesAdapter extends ArrayAdapter<SourcesObject> {
	private static final String TAG = "SourcesAdapter";
	
	private final LayoutInflater inflater;
		
	//TODO: Should share this image downloader with the autocompleteresults adapter instead of creating a second one...
	protected final ImageDownloader imageDownloader;
	
	private SharedPreferences sharedPreferences;
	public Set<String> sourceSet;
				
	public SourcesAdapter(Context context) {
		super(context, 0);
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageDownloader = DDGApplication.getImageDownloader();
		
		sharedPreferences = DDGApplication.getSharedPreferences();
		sourceSet = DDGUtils.loadSet(sharedPreferences, "sourceset");
	}
	
	@Override
	public View getView(int position, View cv, ViewGroup parent) {
		if (cv == null) {
			cv = inflater.inflate(R.layout.sourcepref_layout, null);
			cv.setTag(new SourceHolder((TextView)cv.findViewById(R.id.sourceTitleTextView),
					             (AsyncImageView)cv.findViewById(R.id.sourceItemBackground),
					             (CheckBox)cv.findViewById(R.id.sourceCheckbox)));
		}
		
		SourcesObject feed = getItem(position);
		
		final SourceHolder holder = (SourceHolder) cv.getTag();

		if (feed != null) {			
			
			String feedId = feed.getId();
			
			if(feedId != null && !feedId.equals("null")){
									
					Bitmap bitmap = DDGApplication.getImageCache().getBitmapFromCache("DUCKDUCKICO--" + feed.getId(), false);
					if(bitmap != null){
						holder.imageViewBackground.setBitmap(bitmap);
					}
					else {
						// fallback - should not happen
						
						//Download the background image
//						if (feed.getImageUrl() != null && !feed.getImageUrl().equals("null")) {
//							imageDownloader.download(feed.getImageUrl(), holder.imageViewBackground, false);
//						} else {
//							imageDownloader.download(null, holder.imageViewBackground, false);
//						}
					}
					
			}

			//Set the Title
			holder.textViewTitle.setText(feed.getTitle());
			
			holder.id = feedId;
			
			
			if(sourceSet.contains(holder.id)){
				holder.checkbox.setChecked(true);
			}
			else {
				holder.checkbox.setChecked(false);
			}
			
			holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					DDGControlVar.useDefaultSources = false;
															
					if(isChecked){
						sourceSet.add(holder.id);
						DDGUtils.saveSet(sharedPreferences, sourceSet, "sourceset");
					}
					else {
						sourceSet.remove(holder.id);
						DDGUtils.saveSet(sharedPreferences, sourceSet, "sourceset");
					}
					
					DDGControlVar.hasUpdatedFeed = false;
					
					// reset temporary filter before going back
					DDGControlVar.targetSource = null;
				}
			});
		}

		return cv;
	}
	

	public void setList(List<SourcesObject> feed) {
		this.clear();
		
		// alphabetical sort
		Collections.sort(feed, new Comparator<SourcesObject>() {
	        @Override public int compare(SourcesObject p1, SourcesObject p2) {
	            return p1.getTitle().compareToIgnoreCase(p2.getTitle());
	        }

	    });
		
		for (SourcesObject next : feed) {
			this.add(next);
		}
	}

}
