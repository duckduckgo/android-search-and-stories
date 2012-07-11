package com.duckduckgo.mobile.android.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class DDGDownloaderServiceConnection implements ServiceConnection 
{ 
        public DDGDownloaderService newService = null;
        JobInterface jobInterface = null;
        
        public void setExtraJob(JobInterface j){
        	this.jobInterface = j;
        }
        
        public void onServiceConnected(ComponentName name, IBinder service) 
        { 
                newService = ((DDGDownloaderService.LocalBinder)service).getService(); 
                if(jobInterface != null) {
                	jobInterface.job();
                }
        } 
        public void onServiceDisconnected(ComponentName name) 
        { 
                newService = null; 
        } 
}
