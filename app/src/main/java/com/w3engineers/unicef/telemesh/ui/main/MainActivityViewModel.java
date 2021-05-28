package com.w3engineers.unicef.telemesh.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.w3engineers.unicef.telemesh.data.analytics.workmanager.AppShareCountLocalWorker;
import com.w3engineers.unicef.telemesh.data.analytics.workmanager.AppShareCountSendServerWorker;
import com.w3engineers.unicef.telemesh.data.analytics.workmanager.NewUserCountWorker;
import com.w3engineers.unicef.telemesh.data.analytics.workmanager.RefreshJobWorker;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.base.ui.BaseRxViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MainActivityViewModel extends BaseRxViewModel {

    //initialize Work Manager
    private WorkManager mWorkManager;

    // New instance variable for the WorkInfo
    private LiveData<List<WorkInfo>> mNewUserCountWorkInfo;
    public static final String NEW_USER_COUNT = "new_user_count";
    public static final String LOCAL_APP_SHARE = "local_app_share";
    public static final String APP_SHARE_SEND_SERVER = "app_share_send_server";
    public static final String TAG_REFRESH_JOB = "tag_refresh_job";
    private PeriodicWorkRequest userCountWorkRequest;
    private PeriodicWorkRequest localUserCountRequest;
    private PeriodicWorkRequest sendCountToServerRequest;

    // FeedDataSource instance
    private FeedDataSource mFeedDataSource;
    private MessageSourceData messageSourceData;
    private UserDataSource userDataSource;
    private DataSource dataSource;
    private MutableLiveData<Integer> myModeLiveData;
    private LiveData<List<FeedEntity>> mFeedEntitiesObservable;

    public MainActivityViewModel() {
        userDataSource = UserDataSource.getInstance();
        messageSourceData = MessageSourceData.getInstance();
        mFeedDataSource = FeedDataSource.getInstance();
        mWorkManager = WorkManager.getInstance();
        mNewUserCountWorkInfo = mWorkManager.getWorkInfosByTagLiveData(NEW_USER_COUNT);
        dataSource = Source.getDbSource();
        myModeLiveData = new MutableLiveData<>();
    }

    public void userOfflineProcess() {
        getCompositeDisposable().add(updateUserToOffline()
                .subscribeOn(Schedulers.io()).subscribe(integer -> {
                }, Throwable::printStackTrace));
    }

    private Single<Integer> updateUserToOffline() {
        return Single.fromCallable(() ->
                UserDataSource.getInstance().updateUserToOffline());
    }

    /*public LiveData<Integer> getMyUserMode() {
        getCompositeDisposable().add(dataSource.getMyMode()
                .subscribeOn(Schedulers.io())
                .subscribe(integer -> {
                    myModeLiveData.postValue(integer);
                }, Throwable::printStackTrace));
        return myModeLiveData;
    }*/

    // Add a getter method for mNewUserCountWorkInfo
    public LiveData<List<WorkInfo>> getNewUserWorkInfo() {
        return mNewUserCountWorkInfo;
    }

    public void getMeshInitiatedCall() {
        getCompositeDisposable().add(dataSource.getMeshInitiated()
                .subscribeOn(Schedulers.newThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        setUserCountWorkRequest();
                        setServerAppShareCountWorkerRequest();
                        setLocalAppShareCountWorkerRequest();
                        setRefreshWorkerRequest();
                    }
                }, Throwable::printStackTrace));
    }

    public void setUserCountWorkRequest() {
        try {

            ListenableFuture<List<WorkInfo>> forUniqueWork = WorkManager.getInstance().getWorkInfosByTag(NEW_USER_COUNT);
            List<WorkInfo> workInfos = forUniqueWork.get();

            if (workInfos == null || workInfos.isEmpty()) {
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

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    void setLocalAppShareCountWorkerRequest() {
        try {

            ListenableFuture<List<WorkInfo>> forUniqueWork = WorkManager.getInstance().getWorkInfosByTag(LOCAL_APP_SHARE);
            List<WorkInfo> workInfos = forUniqueWork.get();

            if (workInfos == null || workInfos.isEmpty()) {

                Constraints constraints = new Constraints.Builder()
                        .build();

                localUserCountRequest = new PeriodicWorkRequest.Builder(AppShareCountLocalWorker.class, 6, TimeUnit.HOURS)
                        .addTag(LOCAL_APP_SHARE)
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .build();

                mWorkManager.enqueue(localUserCountRequest);
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void setServerAppShareCountWorkerRequest() {
        try {

            ListenableFuture<List<WorkInfo>> forUniqueWork = WorkManager.getInstance().getWorkInfosByTag(APP_SHARE_SEND_SERVER);
            List<WorkInfo> workInfos = forUniqueWork.get();

            if (workInfos == null || workInfos.isEmpty()) {
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                sendCountToServerRequest = new PeriodicWorkRequest.Builder(AppShareCountSendServerWorker.class, 7, TimeUnit.HOURS)
                        .addTag(APP_SHARE_SEND_SERVER)
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .build();

                mWorkManager.enqueue(sendCountToServerRequest);
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void setRefreshWorkerRequest() {

        try {
            ListenableFuture<List<WorkInfo>> forUniqueWork = WorkManager.getInstance().getWorkInfosByTag(TAG_REFRESH_JOB);
            List<WorkInfo> workInfos = forUniqueWork.get();

            if (workInfos == null || workInfos.isEmpty()) {

                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                PeriodicWorkRequest refreshCpnWork =
                        new PeriodicWorkRequest.Builder(RefreshJobWorker.class, 12, TimeUnit.HOURS)
                                .addTag(TAG_REFRESH_JOB)
                                .setConstraints(constraints)
                                .setBackoffCriteria(
                                        BackoffPolicy.LINEAR,
                                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                        TimeUnit.MILLISECONDS).build();

                mWorkManager.enqueue(refreshCpnWork);
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @NonNull
    public LiveData<List<UserEntity>> getActiveUser() {
        return userDataSource.getActiveUser();
    }

    public LiveData<List<FeedEntity>> getNewFeedsList() {
        return mFeedDataSource.getAllUnreadFeeds();
    }
}
