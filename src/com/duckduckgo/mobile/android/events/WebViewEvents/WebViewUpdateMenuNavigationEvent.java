package com.duckduckgo.mobile.android.events.WebViewEvents;

public class WebViewUpdateMenuNavigationEvent {

    public int disableId;
    public int enableId;

    public WebViewUpdateMenuNavigationEvent(int disableId, int enableId) {
        this.disableId = disableId;
        this.enableId = enableId;
    }
}
