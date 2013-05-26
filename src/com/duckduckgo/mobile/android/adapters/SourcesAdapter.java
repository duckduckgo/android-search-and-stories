package com.duckduckgo.mobile.android.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.SourceHolder;
import com.duckduckgo.mobile.android.objects.Section;
import com.duckduckgo.mobile.android.objects.SectionedListItem;
import com.duckduckgo.mobile.android.objects.SourcesObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.PreferencesManager;
import com.squareup.picasso.Picasso;


public class SourcesAdapter extends ArrayAdapter<SectionedListItem> {	
	private final class ToggleCheckBoxOnClickListener implements
			OnClickListener {
		
		private CheckBox checkBox;
		public ToggleCheckBoxOnClickListener(CheckBox checkBox){
			this.checkBox = checkBox;  
		}
		@Override
		public void onClick(View view) {
			checkBox.setChecked(!checkBox.isChecked());
		}
	}


	private static final String TAG = "SourcesAdapter";
	
	private final LayoutInflater inflater;
	private Context context;
		
	//TODO: Should share this image downloader with the autocompleteresults adapter instead of creating a second one...
					
	public SourcesAdapter(Context context) {
		super(context, 0);
		this.context = context;
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	 public boolean areAllItemsEnabled() 
     { 
             return false; 
     } 

     public boolean isEnabled(int position) 
     { 
             return false; 
     } 
	
	@Override
	public View getView(int position, View cv, ViewGroup parent) {
		SectionedListItem item = getItem(position);		
		
		if(!item.isSection()) {
			
			if (cv == null || cv.getTag() == null) {
				cv = inflater.inflate(R.layout.sourcepref_layout, null);
				cv.setTag(new SourceHolder((TextView)cv.findViewById(R.id.sourceTitleTextView),
									 (TextView)cv.findViewById(R.id.sourceTitleSubTextView),
						             (AsyncImageView)cv.findViewById(R.id.sourceItemBackground),
						             (CheckBox)cv.findViewById(R.id.sourceCheckbox)));
			}
		
			SourcesObject feed = (SourcesObject) item;
			
			final SourceHolder holder = (SourceHolder) cv.getTag();
	
			if (feed != null) {			
				
				String feedId = feed.getId();
				
				if(feedId != null && !feedId.equals("null")){
										
						Bitmap bitmap = DDGApplication.getImageCache().getBitmapFromCache("DUCKDUCKICO--" + feed.getId(), false);
						if(bitmap != null){
							holder.imageViewBackground.setBitmap(bitmap);
						}
						else {
							// fallback - happens after an update to the source list
							
							//Download the background image
							if (feed.getImageUrl() != null && !feed.getImageUrl().equals("null")) {
								Picasso.with(context)
								.load(feed.getImageUrl())
								.placeholder(android.R.color.transparent)
								.into(holder.imageViewBackground);
							}
						}
						
				}
	
				//Set the Title
				holder.textViewTitle.setText(feed.getTitle());
				holder.textViewDescription.setText(feed.getDescription());
				
				holder.id = feedId;
				
				
				if(DDGControlVar.userAllowedSources.contains(holder.id) 
						|| (!DDGControlVar.userDisallowedSources.contains(holder.id) && DDGControlVar.defaultSources.contains(holder.id)) ){
					holder.checkbox.setChecked(true);
				}
				else {
					holder.checkbox.setChecked(false);
				}
				
				OnClickListener toggleCheckBoxOnClickListener = new ToggleCheckBoxOnClickListener(holder.checkbox);
				holder.textViewTitle.setOnClickListener(toggleCheckBoxOnClickListener);
				holder.textViewDescription.setOnClickListener(toggleCheckBoxOnClickListener);
				holder.imageViewBackground.setOnClickListener(toggleCheckBoxOnClickListener);
				holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
																						
						if(isChecked){
							DDGControlVar.userAllowedSources.add(holder.id);
							DDGControlVar.userDisallowedSources.remove(holder.id);
							PreferencesManager.saveUserAllowedSources(DDGControlVar.userAllowedSources);
						}
						else {
							DDGControlVar.userDisallowedSources.add(holder.id);
							DDGControlVar.userAllowedSources.remove(holder.id);
							PreferencesManager.saveUserDisallowedSources(DDGControlVar.userDisallowedSources);
						}
						
						DDGControlVar.hasUpdatedFeed = false;
						
						// reset temporary filter before going back
						DDGControlVar.targetSource = null;
					}
				});
			}
		
		}
		
		else {
			// section item
			
			Section si = (Section)item;
			cv = inflater.inflate(R.layout.list_item_section, null);
			
			final TextView sectionView = (TextView) cv.findViewById(R.id.list_item_section_text);
			sectionView.setText(si.getTitle());
		}

		return cv;
	}
	

	public void setList(List<SourcesObject> feed) {
		this.clear();
		
		Map<String, ArrayList<SourcesObject>> categoryMap = new TreeMap<String, ArrayList<SourcesObject>>();
		for(SourcesObject feedItem : feed) {
			String category = feedItem.getCategory();
			
			if(!categoryMap.containsKey(category)) {
				ArrayList<SourcesObject> objList = new ArrayList<SourcesObject>();
				objList.add(feedItem);
				categoryMap.put(category, objList);
			}
			else {
				categoryMap.get(category).add(feedItem);
			}
		}
		
		for(ArrayList<SourcesObject> sourceList : categoryMap.values()) {
			// alphabetical sort
			Collections.sort(sourceList, new Comparator<SourcesObject>() {
		        @Override public int compare(SourcesObject p1, SourcesObject p2) {
		            return p1.getTitle().compareToIgnoreCase(p2.getTitle());
		        }

		    });
		}
		
		for(Entry<String, ArrayList<SourcesObject>> entry : categoryMap.entrySet()) {
			String category = entry.getKey();
			ArrayList<SourcesObject> sourceList = entry.getValue();
			
			Section catItem = new Section(category);
			this.add(catItem);
			
			for(SourcesObject next : sourceList) {
				this.add(next);
			}
		}
	}

}
