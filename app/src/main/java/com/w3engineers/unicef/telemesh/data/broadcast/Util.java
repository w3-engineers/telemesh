package com.w3engineers.unicef.telemesh.data.broadcast;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.w3engineers.unicef.util.helper.BulletinJobService;

import java.util.concurrent.TimeUnit;

/**
 * Created by Frank Tan on 10/04/2016.
 *
 * A helper class with static properties and methods
 */
public class Util {

    public static final String LOG_TAG = "BackgroundThread";
    public static final int MESSAGE_ID = 1;
    public static final String MESSAGE_BODY = "MESSAGE_BODY";
    public static final String EMPTY_MESSAGE = "<EMPTY_MESSAGE>";

    private static int jobId = 32;
    // schedule the start of the service every 10 - 30 seconds
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(@NonNull Context context) {

        ComponentName serviceComponent = new ComponentName(context, BulletinJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceComponent);
//        builder.setMinimumLatency(1000); // wait at least
        builder.setOverrideDeadline(TimeUnit.MINUTES.toMillis(1)); // maximum delay 2 miniute now statoc
//        builder.setOverrideDeadline(TimeUnit.HOURS.toMillis(24)); // maximum delay 24 hour
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresDeviceIdle(true); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void cancelJob(@NonNull Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);
    }
}
