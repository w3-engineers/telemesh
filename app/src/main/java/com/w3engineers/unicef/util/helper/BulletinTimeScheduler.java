package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.w3engineers.ext.strom.App;
import com.w3engineers.unicef.telemesh.data.broadcast.Util;

public class BulletinTimeScheduler {

    @SuppressLint("StaticFieldLeak")
    private static BulletinTimeScheduler bulletinTimeScheduler = new BulletinTimeScheduler();
    private Context context;
    protected int DEFAULT = 0, WIFI = 1, DATA = 2, AP = 3;

    private BulletinTimeScheduler() {
        context = App.getContext();
    }

    public static BulletinTimeScheduler getInstance() {
        return bulletinTimeScheduler;
    }

    public void connectivityRegister() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(new NetworkCheckReceiver(), intentFilter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean isMobileDataEnable() {
        int state = getNetworkState();
        if (state == DATA) {
            return true;
        } else {
            Util.cancelJob(context);
            return false;
        }
    }

    protected int getNetworkState() {
        ConnectivityManager connectivitymanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivitymanager.getAllNetworkInfo();

        for (NetworkInfo netInfo : networkInfo) {

            /*if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    return WIFI;*/

            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected())
                    return DATA;
        }
        return 0;
    }

    public class NetworkCheckReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                boolean noConnectivity = intent.getBooleanExtra(
                        ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

                if (!noConnectivity) {
                    int state = getNetworkState();
                    if (state == DATA) {
                        resetScheduler(context);
                    }
                } else {
                    // No action needed
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void resetScheduler(Context context) {
        Util.cancelJob(context);
        Util.scheduleJob(context);
    }

}
