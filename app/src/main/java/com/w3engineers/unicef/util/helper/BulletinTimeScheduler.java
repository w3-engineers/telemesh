package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.util.lib.mesh.DataManager;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;
import com.w3engineers.unicef.util.helper.uiutil.NoInternetCallback;

public class BulletinTimeScheduler {

    @SuppressLint("StaticFieldLeak")
    private static BulletinTimeScheduler bulletinTimeScheduler = new BulletinTimeScheduler();
    private Context context;
    protected int DEFAULT = 0, WIFI = 1, DATA = 2, AP = 3;
    private NoInternetCallback noInternetCallback;

    private BulletinTimeScheduler() {
        context = TeleMeshApplication.getContext();
    }

    @NonNull
    public static BulletinTimeScheduler getInstance() {
        return bulletinTimeScheduler;
    }

    public void processesForInternetConnection() {
        if (!Constants.IS_LOG_UPLOADING_START) {
            Constants.IS_LOG_UPLOADING_START = true;

            RmDataHelper.getInstance().uploadLogFile();
            RmDataHelper.getInstance().sendPendingFeedback();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetScheduler(@NonNull Context context) {

    }

    public void checkAppUpdate() {

        Log.d("FileDownload", "Downloading process start");
        long saveTime = SharedPref.readLong(Constants.preferenceKey.APP_UPDATE_CHECK_TIME);
        long dif = System.currentTimeMillis() - saveTime;
        long days = dif / (24 * 60 * 60 * 1000);
        int hour = (int) ((dif - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));

        if (saveTime == 0 || hour > 23) {
            Log.d("FileDownload", "Downloading process time match");
            if (DataManager.on().isNetworkOnline()) {
                Log.d("FileDownload", "Online ");
                // new UpdateAppConfigDownloadTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadLink);
                InAppUpdate.getInstance(context).downloadAppUpdateConfig(DataManager.on().getNetwork());
            }
        }
    }
}
