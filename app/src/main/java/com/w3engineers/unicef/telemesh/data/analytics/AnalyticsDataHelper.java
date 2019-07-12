package com.w3engineers.unicef.telemesh.data.analytics;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.annotation.TargetApi;
import android.os.Build;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.analytics.callback.AnalyticsCallback;
import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.NewNodeModel;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.util.helper.BulletinTimeScheduler;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AnalyticsDataHelper {

    private static AnalyticsDataHelper analyticsDataHelper;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private int trackMessageCount = 0;
    private static AnalyticsCallback analyticsCallback;

    static {
        analyticsDataHelper = new AnalyticsDataHelper();
        analyticsCallback = new AnalyticsCallback() {
            @Override
            public void onGetMessageCountCallback(boolean isSusscess) {

            }
        };
    }

    public static AnalyticsDataHelper getInstance() {
        return analyticsDataHelper;
    }

    public void analyticsDataObserver() {
        MessageSourceData messageSourceData = MessageSourceData.getInstance();

        compositeDisposable.add(messageSourceData.getBlockMessageInfoForSync()
                .subscribeOn(Schedulers.newThread())
                .subscribe(messageAnalyticsEntity -> {

                    if (messageAnalyticsEntity != null && messageAnalyticsEntity.syncMessageCountToken > 0) {

                        if (trackMessageCount < messageAnalyticsEntity.syncMessageCountToken) {

                            trackMessageCount = messageAnalyticsEntity.syncMessageCountToken;

                            String userId = SharedPref.getSharedPref(TeleMeshApplication.getContext()).read(Constants.preferenceKey.MY_USER_ID);
                            messageAnalyticsEntity.setUserId(userId);

                            processMessageForAnalytics(true, messageAnalyticsEntity);
                        }
                    }

                }, Throwable::printStackTrace));
    }

    public void processMessageForAnalytics(boolean isMine, MessageEntity.MessageAnalyticsEntity messageAnalyticsEntity) {

        if (isMobileDataEnable() || !isMine) {
            MessageCountModel messageCountModel = messageAnalyticsEntity.toMessageCountModel();
            sendMessageCount(messageCountModel,analyticsCallback);
        } else {
            RmDataHelper.getInstance().analyticsDataSendToSellers(messageAnalyticsEntity);
        }
    }

    public void sendMessageCount(MessageCountModel messageCountModel,AnalyticsCallback callback) {
        AnalyticsApi.on().saveMessageCount(messageCountModel,callback);
    }

    public void sendNewUserAnalytics(List<NewNodeModel> nodeList) {
        AnalyticsApi.on().sendNewUserAnalytics(nodeList);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isMobileDataEnable() {
        return BulletinTimeScheduler.getInstance().isMobileDataEnable();
    }

}
