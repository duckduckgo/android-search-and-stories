package com.duckduckgo.mobile.android.util;

import android.app.Activity;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import info.guardianproject.onionkit.ui.OrbotHelper;
import info.guardianproject.onionkit.web.WebkitProxy;

/**
 * Created with IntelliJ IDEA.
 * User: Koen
 * Date: 16/06/13
 * Time: 21:18
 * To change this template use File | Settings | File Templates.
 */
public class TorIntegration {

    private final Activity activity;
    private final OrbotHelper orbotHelper;

    public TorIntegration(Activity activity){
        this.activity = activity;
        orbotHelper = new OrbotHelper(this.activity);
    }

    public void prepareTorSettings(){
        prepareTorSettings(isTorSettingEnabled());
    }

    public void prepareTorSettings(boolean enableTor){
        if(enableTor){
            requestOrbotStart();
            enableOrbotProxy();
        }
        else{
            resetProxy();
        }
        DDGNetworkConstants.initializeMainClient(activity.getApplication(), enableTor);
    }

    private void resetProxy() {
        try {
            WebkitProxy.resetProxy(activity.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableOrbotProxy() {
        try {
            WebkitProxy.setProxy(activity.getApplication());
        } catch (Exception e) {
            // what should we do here? Discuss!
            e.printStackTrace();
        }
    }

    private void requestOrbotStart() {
        if (!orbotHelper.isOrbotInstalled()){
            orbotHelper.promptToInstall(activity);
        }
        else if (!orbotHelper.isOrbotRunning()){
            orbotHelper.requestOrbotStart(activity);
        }
    }

    public boolean isTorSettingEnabled() {
        return PreferencesManager.getEnableTor();
    }

    public boolean isOrbotRunningAccordingToSettings() {
        return !isTorSettingEnabled() || isTorEnabledAndOrbotRunning();
    }

    private boolean isTorEnabledAndOrbotRunning(){
        return isTorSettingEnabled() && orbotHelper.isOrbotRunning();
    }
}
