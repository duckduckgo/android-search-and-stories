package com.duckduckgo.mobile.android.objects;

public class Section implements SectionedListItem{

	private final String title;
	
	public Section(String title) {
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
	
	@Override
	public boolean isSection() {
		return true;
	}

}
