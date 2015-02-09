package com.duckduckgo.mobile.android.util;

import android.app.Activity;

public class TorIntegrationProvider {

	public static TorIntegration torIntegration = null;

	public static TorIntegration getInstance(Activity activity) {
		if(torIntegration==null) {
			torIntegration = new TorIntegration(activity);
		}
		return torIntegration;
	}

	private TorIntegrationProvider() {

	}
}
