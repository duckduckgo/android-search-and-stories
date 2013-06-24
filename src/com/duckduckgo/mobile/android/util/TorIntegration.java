package com.duckduckgo.mobile.android.util;

import android.app.Activity;
import com.duckduckgo.mobile.android.activity.DuckDuckGo;
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

    private final DuckDuckGo context;
    private final OrbotHelper orbotHelper;

    public TorIntegration(DuckDuckGo context){
        this.context = context;
        orbotHelper = new OrbotHelper(this.context);
    }

    public void prepareTorSettings(){
        prepareTorSettings(isTorSettingEnabled());
    }

    public void prepareTorSettings(boolean enableTor){
        DDGNetworkConstants.initializeMainClient(context.getApplication(), enableTor);
        if(enableTor){
            enableOrbotProxy();
            requestOrbotInstallAndStart();
        }
        else{
            resetProxy();
        }
    }

    private void resetProxy() {
        try {
            WebkitProxy.resetProxy(context.getApplication());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableOrbotProxy() {
        try {
            WebkitProxy.setProxy(context.getApplication());
        } catch (Exception e) {
            // what should we do here? Discuss!
            e.printStackTrace();
        }
    }

    private void requestOrbotInstallAndStart() {
        if (!orbotHelper.isOrbotInstalled()){
            orbotHelper.promptToInstall(context);
        }
        else if (!orbotHelper.isOrbotRunning()){
            orbotHelper.requestOrbotStart(context);
        }
    }

    public boolean isTorSettingEnabled() {
        return PreferencesManager.getEnableTor();
    }

    public boolean isOrbotRunningAccordingToSettings() {
        return !isTorSettingEnabled() || isTorEnabledAndOrbotRunning();
    }

    private boolean isTorEnabledAndOrbotRunning(){
        return isTorSettingEnabled() &&
                orbotHelper.isOrbotInstalled() &&
                orbotHelper.isOrbotRunning();
    }
}
