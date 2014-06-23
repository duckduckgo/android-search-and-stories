package com.duckduckgo.mobile.android.util;

import android.app.Activity;
import android.os.Build;
import android.webkit.WebView;
import com.duckduckgo.mobile.android.network.DDGNetworkConstants;
import info.guardianproject.onionkit.ui.OrbotHelper;
import info.guardianproject.onionkit.web.WebkitProxy;

public class TorIntegration {

    public static final int JELLY_BEAN_MR2 = 18;
    private final Activity context;
    private final OrbotHelper orbotHelper;

    public TorIntegration(Activity context){
        this.context = context;
        orbotHelper = new OrbotHelper(this.context);
    }

    public boolean prepareTorSettings(){
        return prepareTorSettings(isTorSettingEnabled());
    }

    public boolean prepareTorSettings(boolean enableTor){
        if(!isTorSupported()){
            return false;
        }
        DDGNetworkConstants.initializeMainClient(context.getApplication(), enableTor);
        if(enableTor){
            enableOrbotProxy();
            requestOrbotInstallAndStart();
        }
        else{
            resetProxy();
        }
        return true;
    }

    private void resetProxy() {
        try {
            WebkitProxy.resetProxy("com.duckduckgo.mobile.android.DDGApplication", DDGNetworkConstants.getWebView().getContext().getApplicationContext());
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void resetProxy(WebView webView) {
        try {
            WebkitProxy.resetProxy("com.duckduckgo.mobile.android.DDGApplication", webView.getContext().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableOrbotProxy() {
        try {
            WebkitProxy.setProxy("com.duckduckgo.mobile.android.DDGApplication", DDGNetworkConstants.getWebView().getContext().getApplicationContext(), DDGNetworkConstants.PROXY_HOST, DDGNetworkConstants.PROXY_HTTP_PORT);
        } catch (Exception e) {
            // what should we do here? Discuss!
            e.printStackTrace();
        }
    }

    public void enableOrbotProxy(WebView webView) {
        try {
            WebkitProxy.setProxy("com.duckduckgo.mobile.android.DDGApplication", webView.getContext().getApplicationContext(), DDGNetworkConstants.PROXY_HOST, DDGNetworkConstants.PROXY_HTTP_PORT);
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

    public boolean isTorSupported() {
        return true; //Build.VERSION.SDK_INT <= JELLY_BEAN_MR2;
    }
}
