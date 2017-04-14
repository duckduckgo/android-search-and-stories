package com.duckduckgo.mobile.android.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.network.DDGHttpException;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;

import java.util.List;

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
        context.startActivity(getDDGViewIntent());
    }

    public static void addDDGToHomescreen(Context context) {
        context.sendBroadcast(getAddToHomescreenIntent(context));

        /*

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(new Intent(Intent.ACTION_CREATE_SHORTCUT), 0);
        Log.e("shortcuts", "info -> size: "+info.size());
        for(ResolveInfo ri : info) {
            Log.e("shortcuts", "resolveInfo: "+ri);
        }
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = pm.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            //AppDetail app = new AppDetail();
            String app = ri.loadLabel(pm)+" "+ri.activityInfo.packageName+" "+ri.activityInfo.loadIcon(pm);
            Log.e("shortcuts", "app: "+app);
        }

        */
    }

    public static Intent getAddToHomescreenIntent(Context context) {
        Intent ddgIntent = getDDGViewIntent();
        Intent shortcutIntent = new Intent();
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, ddgIntent);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Search DDG");
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.icon));
        shortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //shortcutIntent.putExtra("duplicate", true);
        shortcutIntent.putExtra("duplicate", false);
        return shortcutIntent;
    }

    public static void performActionDone() {
        performAction(ACTION_DONE);
    }

    private static Intent getDDGViewIntent() {
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
