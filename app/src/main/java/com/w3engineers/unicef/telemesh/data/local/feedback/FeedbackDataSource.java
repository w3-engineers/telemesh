package com.w3engineers.unicef.telemesh.data.local.feedback;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class FeedbackDataSource {
    private FeedbackDao mFeedbackDao;
    private ExecutorService mIoExecutor;
    private static FeedbackDataSource feedbackDataSource = new FeedbackDataSource();

    public static FeedbackDataSource getInstance() {
        return feedbackDataSource;
    }

    private FeedbackDataSource() {
        mFeedbackDao = AppDatabase.getInstance().feedbackDao();
        mIoExecutor = Executors.newSingleThreadExecutor();
    }

    public long insertOrUpdate(FeedbackEntity entity) {
        Callable<Long> insertCallable = () -> mFeedbackDao.insertOrUpdate(entity);

        try {
            return mIoExecutor.submit(insertCallable).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public FeedbackEntity getFirstFeedback() {
        return mFeedbackDao.getFirstFeedback();
    }

    public FeedbackEntity getFeedbackById(String feedbackId) {
        return mFeedbackDao.getFeedbackById(feedbackId);
    }

    public int deleteFeedbackById(String feedbackId) {
        return mFeedbackDao.deleteFeedbackById(feedbackId);
    }
}
