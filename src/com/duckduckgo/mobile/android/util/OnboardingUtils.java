package com.duckduckgo.mobile.android.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;

/**
 * Created by fgei on 4/10/17.
 */

public class OnboardingUtils {

    private static final String ACTION_VIEWED = "viewed";
    private static final String ACTION_DONE = "done";
    private static final String ACTION_SKIPPED = "skipped";
    private static final String ACTION_ALREADYDONE = "alreadydone";

    private static final String PARAM = "t/ddg_onboarding_%1$s_android_os%2$s_%3$s";
    private static final String ONBOARDING_VERSION = "v1";

    private OnboardingUtils() {
    }

    public static void launchDDG(Context context) {
        context.startActivity(getDDGViewIntent(context));
    }

    public static void performActionDone() {
        performAction(ACTION_DONE);
    }

    private static Intent getDDGViewIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(DDGConstants.BASE_URL));
        return intent;
    }

    private static void performAction(String action) {
        try {
            String url = getUrlForAction(action);
            Log.e("onboarding_action", "url: "+url);
            String body = DDGNetworkConstants.mainClient.doGet(url).toString();
        } catch(DDGHttpException e) {
            e.printStackTrace();
        }
    }

    private static String getUrlForAction(String action) {
        return new StringBuilder(DDGConstants.BASE_URL)
                .append(String.format(PARAM, ONBOARDING_VERSION, getOSVersion(), action)).toString();

    }

    private static String getOSVersion() {
        return Build.VERSION.RELEASE.replace(".", "-");
    }
}
