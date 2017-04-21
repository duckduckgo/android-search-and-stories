package com.duckduckgo.mobile.android.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.duckduckgo.mobile.android.R;

import java.util.List;

/**
 * Created by fgei on 4/20/17.
 */

public class OnboardingHelper {

    private static final String PACKAGE_CHROME = "com.android.chrome";
    private static final String PACKAGE_FIREFOX = "org.mozilla.firefox";


    private Context context;
    private PackageManager packageManager;

    public OnboardingHelper(Context context) {
        this.context = context.getApplicationContext();
        packageManager = context.getPackageManager();
    }

    public void addToHomeScreen() {
        context.sendBroadcast(getAddToHomescreenIntent(context));
        PreferencesManager.setDDGAddedToHomeScreen();
        Toast.makeText(context, R.string.ddg_added_to_home_page, Toast.LENGTH_SHORT).show();
    }

    public boolean isDefaultBrowserSelected() {
        ResolveInfo resolveInfo = getDefaultActivityForIntent(packageManager, getDDGIntent());
        if(resolveInfo == null || resolveInfo.activityInfo == null || resolveInfo.activityInfo.packageName == null) return false;
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        return activityInfo.packageName != null;
    }

    public boolean isDefaultBrowserFirefox() {
        return isDefaultBrowserForPackage(PACKAGE_FIREFOX);
    }

    public boolean isDefaultBrowserChrome() {
        return isDefaultBrowserForPackage(PACKAGE_CHROME);
    }

    private boolean isDefaultBrowserForPackage(String packageName) {
        if(!isDefaultBrowserSelected()) return false;
        ActivityInfo activityInfo = getDefaultActivityForIntent(packageManager, getDDGIntent()).activityInfo;
        return activityInfo.packageName.contains(packageName);
    }

    private ResolveInfo getDefaultActivityForIntent(PackageManager packageManager, Intent intent) {
        return packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    public static Intent getAddToHomescreenIntent(Context context) {
        Intent ddgIntent = getDDGIntent();
        Intent shortcutIntent = new Intent();
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, ddgIntent);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Search DDG");
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.icon));
        shortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //shortcutIntent.putExtra("duplicate", true);
        shortcutIntent.putExtra("duplicate", false);
        return shortcutIntent;
    }

    private static Intent getDDGIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(DDGConstants.BASE_URL));
    }
}
