/*
package com.w3engineers.unicef.util.helper;
 
*/
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*//*


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.w3engineers.unicef.telemesh.data.broadcast.Util;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BulletinJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(@Nullable JobParameters params) {

        Log.v("MIMO_SAHA::", "Job called");
        BulletinTimeScheduler.getInstance().processesForInternetConnection();
        RmDataHelper.getInstance().requestWsMessage();
        Util.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(@Nullable JobParameters params) {
        Log.v("MIMO_SAHA::", "Job Cancelled");
        return true;
    }

}*/
