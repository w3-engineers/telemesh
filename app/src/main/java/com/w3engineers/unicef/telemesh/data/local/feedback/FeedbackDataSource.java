package com.w3engineers.unicef.telemesh.data.local.feedback;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class FeedbackDataSource {
    private FeedbackDao mFeedbackDao;
    private ExecutorService mIoExecutor;
    private static FeedbackDataSource feedbackDataSource = new FeedbackDataSource();

    public static FeedbackDataSource getInstance() {
        return feedbackDataSource;
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

    public int deleteFeedback(FeedbackEntity entity) {
        return mFeedbackDao.delete(entity);
    }
}
