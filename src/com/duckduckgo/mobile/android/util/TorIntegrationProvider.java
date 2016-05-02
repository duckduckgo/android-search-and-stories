package com.duckduckgo.mobile.android.util;

import android.app.Activity;

/**
 * This class encapsulates creation and retrieval of a TorIntegration instance.
 * It makes sure that only one TorIntegration object is created through it.
 *
 * This looks like a singleton except it isn't, a TorIntegration instance can be
 * created outside this provider (TorIntegration constructor is public) and therefore
 * multiple instances can be created. (is this intended or a wip?)
 */
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
