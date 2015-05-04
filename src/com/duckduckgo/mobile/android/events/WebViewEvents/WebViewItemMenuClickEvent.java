package com.duckduckgo.mobile.android.events.WebViewEvents;

import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.duckduckgo.mobile.android.objects.FeedObject;

public class WebViewItemMenuClickEvent {

    public MenuItem item;
    public View itemView = null;
    public FeedObject feed = null;

    public WebViewItemMenuClickEvent(MenuItem item) {
        this.item = item;
    }

    public WebViewItemMenuClickEvent(MenuItem item, View itemView) {
        this.item = item;
        this.itemView = itemView;
    }

    public WebViewItemMenuClickEvent(MenuItem item, FeedObject feed) {
        this.item = item;
        this.feed = feed;
    }
}
