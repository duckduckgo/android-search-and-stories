package com.duckduckgo.mobile.android.events;

public class TestEvent extends Event {

    public boolean isAutoCompleteActive = true;

    public TestEvent(boolean isAutoCompleteActive) {
        this.isAutoCompleteActive = isAutoCompleteActive;
    }
}
