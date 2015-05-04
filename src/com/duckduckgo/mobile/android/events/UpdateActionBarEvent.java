package com.duckduckgo.mobile.android.events;

public class UpdateActionBarEvent extends Event {

    public String newTag;

    public UpdateActionBarEvent(String newTag) {
        this.newTag = newTag;
    }
}
