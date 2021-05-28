package com.w3engineers.unicef.telemesh.data.analytics.workmanager;

import android.content.Context;
import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.w3engineers.unicef.util.helper.BulletinTimeScheduler;

public class RefreshJobWorker extends Worker {

    private static int i = 0;
    private Context context;

    public RefreshJobWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        BulletinTimeScheduler.getInstance().processesForInternetConnection();
//        BroadcastDataHelper.getInstance().requestForBroadcast();
        return Result.success();
    }

}
