package com.android.vending.updater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.IPackageInstallObserver;
import android.net.Uri;
import android.util.Log;

public class UpdateMarketReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri contentUri = intent.getData();
        context.getPackageManager().installPackage(contentUri, new InstallObserver(context), 2, null);        
    }
    
    static class InstallObserver extends IPackageInstallObserver.Stub {
        private final Context mContext;
        
        public InstallObserver(Context context) {
            mContext = context;
        }
        
        public void packageInstalled(String packageName, int returnCode) {
            if (returnCode != 1) {
                Log.w("UpdateMarketReceiver", new StringBuilder().append(
                            "Failed to install package ").append(packageName).append(
                                " with return code: ").append(returnCode).toString());

                if ("com.android.vending".equals(packageName)) {
                    Intent failureIntent = new Intent("com.android.vending.UPDATE_MARKET_FAILURE");
                    failureIntent.putExtra("failure_return_code", returnCode);
                    mContext.sendBroadcast(failureIntent);
                }
            }
        }
    }
}

