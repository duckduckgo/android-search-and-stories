package com.duckduckgo.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Assist extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent duckDuckGoIntent = new Intent(this, DuckDuckGo.class);

        Intent assistIntent = getIntent();
        if(assistIntent!=null) {
            if(Intent.ACTION_ASSIST.equals(assistIntent.getAction())) {
                //duckDuckGoIntent.putExtra("assist", true);
            }
            //startActivity(assistIntent);
            duckDuckGoIntent.setAction(assistIntent.getAction());
        }
        startActivity(duckDuckGoIntent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
