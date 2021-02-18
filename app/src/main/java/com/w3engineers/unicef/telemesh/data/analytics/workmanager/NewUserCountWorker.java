package com.w3engineers.unicef.telemesh.data.analytics.workmanager;

import android.content.Context;
import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;

public class NewUserCountWorker extends Worker {


    public NewUserCountWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

         RmDataHelper.getInstance().newUserAnalyticsSend();

        return Result.success();
    }
}
