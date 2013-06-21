package com.duckduckgo.mobile.android.subscribers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.events.Event;
import com.duckduckgo.mobile.android.events.KillService;
import com.squareup.otto.Subscribe;

public class MainSubscriber extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		BusProvider.getInstance().register(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Subscribe
	public void handleEvent(Event event) {
		event.process();
	}

	// Use bus to kill the service
	@Subscribe
	public void killService(KillService event) {
		onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
}
