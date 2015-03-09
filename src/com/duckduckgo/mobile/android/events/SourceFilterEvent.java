package com.duckduckgo.mobile.android.events;

import android.view.View;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class SourceFilterEvent extends Event {

    public View itemView;
    public String sourceType;
    public FeedObject feedObject;

    public SourceFilterEvent(View itemView, String sourceType, FeedObject feedObject) {
        this.itemView = itemView;
        this.sourceType = sourceType;
        this.feedObject = feedObject;
    }
}
