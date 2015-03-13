package com.duckduckgo.mobile.android.events;

public class DimBackgroundEvent extends Event {

    public boolean dim;

    public DimBackgroundEvent(boolean dim) {
        this.dim = dim;
    }
}
