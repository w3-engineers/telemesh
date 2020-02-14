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
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.util.ConfigSyncUtil;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.broadcast.Util;
import com.w3engineers.unicef.telemesh.data.helper.AppCredentials;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.uiutil.NoInternetCallback;

public class BulletinTimeScheduler {

    @SuppressLint("StaticFieldLeak")
    private static BulletinTimeScheduler bulletinTimeScheduler = new BulletinTimeScheduler();
    private Context context;
    protected int DEFAULT = 0, WIFI = 1, DATA = 2, AP = 3;
    private NoInternetCallback noInternetCallback;

    private BulletinTimeScheduler() {
        context = App.getContext();
    }

    @NonNull
    public static BulletinTimeScheduler getInstance() {
        return bulletinTimeScheduler;
    }

    /*public BulletinTimeScheduler connectivityRegister() {
     *//*IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(new NetworkCheckReceiver(), intentFilter);*//*
        return this;
    }*/

    /*public void initNoInternetCallback(NoInternetCallback callback) {
        this.noInternetCallback = callback;
    }*/

    public void processesForInternetConnection() {
        RmDataHelper.getInstance().sendPendingAck();
        ConfigSyncUtil.getInstance().startConfigurationSync(context, false);

        if (!Constants.IS_LOG_UPLOADING_START) {
            Constants.IS_LOG_UPLOADING_START = true;

            checkAppUpdate();

            RmDataHelper.getInstance().uploadLogFile();
            RmDataHelper.getInstance().sendPendingFeedback();
        }
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

    /*public class NetworkCheckReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(@NonNull Context context, @NonNull Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                boolean noConnectivity = intent.getBooleanExtra(
                        ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

                if (!noConnectivity) {
                    int state = getNetworkState();
                    if (state == DATA) {
                        Constants.IS_DATA_ON = true;
                        RmDataHelper.getInstance().sendPendingAck();
                        resetScheduler(context);

                        ConfigSyncUtil.getInstance().startConfigurationSync(context, false);

                        if (!Constants.IS_LOG_UPLOADING_START) {
                            Constants.IS_LOG_UPLOADING_START = true;

                            String downloadLink = AppCredentials.getInstance().getFileRepoLink() + "updatedJSon.json";
                            new UpdateAppConfigDownloadTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadLink);


                            RmDataHelper.getInstance().uploadLogFile();
                            RmDataHelper.getInstance().sendPendingFeedback();
                        }

                    } else {
                        Constants.IS_DATA_ON = false;
                    }
                } else {
                    // No action needed
                    Constants.IS_DATA_ON = false;
                }

                sendNoInternetCallbackToUi(Constants.IS_DATA_ON);
            }
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetScheduler(@NonNull Context context) {
        Util.cancelJob(context);
        Util.scheduleJob(context);
    }

    public void setScheduler(Context context) {
        if (Util.isJobExist(context)) return;
        Util.scheduleJob(context);
    }

    public void checkAppUpdate() {
        SharedPref sharedPref = SharedPref.getSharedPref(TeleMeshApplication.getContext());
        long saveTime = sharedPref.readLong(Constants.preferenceKey.APP_UPDATE_CHECK_TIME);
        long dif = System.currentTimeMillis() - saveTime;
        long days = dif / (24 * 60 * 60 * 1000);
        int hour = (int) ((dif - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));

        if (hour > 23) {
            String downloadLink = AppCredentials.getInstance().getFileRepoLink() + "updatedJSon.json";
            new UpdateAppConfigDownloadTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadLink);
        }
    }

    /*@NonNull
    public NetworkCheckReceiver getReceiver() {
        return new NetworkCheckReceiver();
    }*/


    /*private void sendNoInternetCallbackToUi(boolean haveInternet) {
        if (noInternetCallback != null) {
            noInternetCallback.onGetAvailableInternet(haveInternet);
        }
    }*/

   /* private void uploadLogFile() {
        Log.d("ParseFileUpload", "Upload file call");
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() +
                "/MeshRnD");
        File[] files = directory.listFiles();

        AnalyticsDataHelper.getInstance().sendLogFileInServer(files[4], "testUser", Constants.getDeviceName());
    }*/
}
