/*
**
** Copyright 2011, lytsing.org
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/
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
        context.getPackageManager().installPackage(contentUri, new InstallObserver(context),
                PackageManager.INSTALL_REPLACE_EXISTING, null);
    }
    
    static class InstallObserver extends IPackageInstallObserver.Stub {
        private final Context mContext;
        
        public InstallObserver(Context context) {
            mContext = context;
        }
        
        public void packageInstalled(String packageName, int returnCode) {
            if (returnCode != PackageManager.INSTALL_SUCCEEDED) {
                Log.w("UpdateMarketReceiver", new StringBuilder()
                        .append("Failed to install package ")
                        .append(packageName)
                        .append(" with return code: ")
                        .append(returnCode)
                        .toString());

                if ("com.android.vending".equals(packageName)) {
                    Intent failureIntent = new Intent("com.android.vending.UPDATE_MARKET_FAILURE");
                    failureIntent.putExtra("failure_return_code", returnCode);
                    mContext.sendBroadcast(failureIntent);
                }
            }
        }
    }
}

