package com.duckduckgo.mobile.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ShortcutReceiver extends BroadcastReceiver {
    public ShortcutReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context, "DuckDuckGo was added to your Homescreen", Toast.LENGTH_SHORT).show();
        Log.e("shortcuts", "onboarding on receive create shortcut RECEIVER");
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
