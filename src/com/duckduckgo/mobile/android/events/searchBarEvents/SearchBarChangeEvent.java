package com.duckduckgo.mobile.android.events.searchBarEvents;

import com.duckduckgo.mobile.android.events.Event;
import com.duckduckgo.mobile.android.util.SCREEN;

public class SearchBarChangeEvent extends Event {

    public SCREEN screen;

    public SearchBarChangeEvent(SCREEN screen) {
        this.screen = screen;
    }
}
