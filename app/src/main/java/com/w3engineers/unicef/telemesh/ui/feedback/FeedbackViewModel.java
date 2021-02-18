package com.w3engineers.unicef.telemesh.ui.feedback;

import android.app.Application;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackDataSource;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackEntity;

import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class FeedbackViewModel extends BaseRxAndroidViewModel {

    private FeedbackDataSource feedbackDataSource;
    private MutableLiveData<Boolean> feedbackLiveData = new MutableLiveData<>();

    public FeedbackViewModel(@NonNull Application application) {
        super(application);
        feedbackDataSource = FeedbackDataSource.getInstance();
    }

    void sendFeedback(String feedBackText) {
        FeedbackEntity entity = prepareFeedbackModel(feedBackText);
        getCompositeDisposable()
                .add(Single.fromCallable(() -> feedbackDataSource.insertOrUpdate(entity))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(result -> {
                            feedbackLiveData.postValue(result > 0);

                            if (result > 0) {
                                AnalyticsDataHelper.getInstance().sendFeedback(entity);
                            }

                        }, Throwable::printStackTrace));
    }

    MutableLiveData<Boolean> feedbackResponse() {
        return feedbackLiveData;
    }

    private FeedbackEntity prepareFeedbackModel(String feedbackText) {
        SharedPref sharedPref = SharedPref.getSharedPref(getApplication());
        FeedbackEntity entity = new FeedbackEntity();
        entity.setFeedback(feedbackText);
        entity.setFeedbackId(UUID.randomUUID().toString());
        entity.setTimeStamp(System.currentTimeMillis());
        entity.setUserName(sharedPref.read(Constants.preferenceKey.USER_NAME));
        entity.setUserId(sharedPref.read(Constants.preferenceKey.MY_USER_ID));
        return entity;
    }
}
