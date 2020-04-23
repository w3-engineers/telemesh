package com.w3engineers.unicef.telemesh.data.local.feed;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Single;

public class FeedDataSource {

    private FeedDao feedDao;
    private static FeedDataSource feedDataSource = new FeedDataSource();

    //ExecutorService is a construct that allows you to pass a task to be executed by a thread asynchronously.
    private ExecutorService mIoExecutor;

    private FeedDataSource() {
        feedDao = AppDatabase.getInstance().feedDao();
        mIoExecutor = Executors.newSingleThreadExecutor();
    }

    @NonNull
    public static FeedDataSource getInstance() {
        return feedDataSource;
    }

    public long insertOrUpdateData(@NonNull FeedEntity feedEntity) {

        Callable<Long> insertCallable = () -> feedDao.insertFeed(feedEntity);

        try {
            return mIoExecutor.submit(insertCallable).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @NonNull
    public LiveData<List<FeedEntity>> loadFeeds() {
        return feedDao.getAllFeed();
    }

    @NonNull
    public LiveData<List<FeedEntity>> getAllUnreadFeeds() {
        return feedDao.getAllUnreadFeed();
    }

    public long updateFeedMessageReadStatus(@NonNull String feedId) {

        Callable<Long> updateFeed = () -> feedDao.updateFeedMessageReadStatusByMessageId(feedId);

        try {
            return mIoExecutor.submit(updateFeed).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Single<Integer> deleteAll() {
        return Single.fromCallable(() -> feedDao.deleteAll());
    }
}
