package com.duckduckgo.mobile.android.events.WebViewEvents;

import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

public class WebViewItemMenuClickEvent {

    public MenuItem item;
    public View itemView = null;

    public WebViewItemMenuClickEvent(MenuItem item) {
        this.item = item;
    }

    public WebViewItemMenuClickEvent(MenuItem item, View itemView) {
        this.item = item;
        this.itemView = itemView;
    }
}
