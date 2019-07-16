package com.w3engineers.unicef.telemesh.ui.main;

import android.arch.lifecycle.LiveData;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.analytics.workmanager.NewUserCountWorker;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MainActivityViewModel extends BaseRxViewModel {

    //initialize Work Manager
    private WorkManager mWorkManager;

    // New instance variable for the WorkInfo
    private LiveData<List<WorkInfo>> mNewUserCountWorkInfo;
    public static final String NEW_USER_COUNT = "new_user_count";
    private PeriodicWorkRequest userCountWorkRequest;

    private MessageSourceData messageSourceData;

    public MainActivityViewModel() {
        messageSourceData = MessageSourceData.getInstance();
        mWorkManager = WorkManager.getInstance();
        mNewUserCountWorkInfo = mWorkManager.getWorkInfosByTagLiveData(NEW_USER_COUNT);
    }

    public void userOfflineProcess() {
        getCompositeDisposable().add(updateUserToOffline()
                .subscribeOn(Schedulers.io()).subscribe(integer -> {}, Throwable::printStackTrace));
    }

    private Single<Integer> updateUserToOffline() {
        return Single.fromCallable(() ->
                UserDataSource.getInstance().updateUserToOffline());
    }

    // Add a getter method for mNewUserCountWorkInfo
    public LiveData<List<WorkInfo>> getNewUserWorkInfo() {
        return mNewUserCountWorkInfo;
    }

    public void setUserCountWorkRequest(){

        // Create charging constraint
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        userCountWorkRequest = new PeriodicWorkRequest.Builder(NewUserCountWorker.class, 20, TimeUnit.MINUTES)
                .addTag(NEW_USER_COUNT)
                .setConstraints(constraints)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();

        mWorkManager.enqueue(userCountWorkRequest);
    }



}
