package com.w3engineers.unicef.telemesh.data.analytics;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.analytics.callback.AnalyticsResponseCallback;
import com.w3engineers.unicef.telemesh.data.analytics.callback.FileUploadResponseCallback;
import com.w3engineers.unicef.telemesh.data.analytics.model.AppShareCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.NewNodeModel;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountDataService;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogDataSource;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.BulletinTimeScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AnalyticsDataHelper implements AnalyticsResponseCallback {

    private static AnalyticsDataHelper analyticsDataHelper;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private int trackMessageCount = 0;
    private List<AppShareCountEntity> countSentList;
    private static FileUploadResponseCallback fileUploadResponseCallback;

    static {
        analyticsDataHelper = new AnalyticsDataHelper();
        
        fileUploadResponseCallback = (isSuccessful, name) -> {
            if (isSuccessful) {
                AsyncTask.execute(() -> {
                    MeshLogEntity entity = new MeshLogEntity();
                    entity.setLogName(name);

                    MeshLogDataSource.getInstance().insertOrUpdateData(entity);
                });
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
                .subscribe(messagePlotCount -> {
                    if (messagePlotCount != null && messagePlotCount > 0) {

                        if (trackMessageCount < messagePlotCount) {

                            trackMessageCount = messagePlotCount;

                            String userId = SharedPref.getSharedPref(TeleMeshApplication.getContext()).read(Constants.preferenceKey.MY_USER_ID);

                            MessageEntity.MessageAnalyticsEntity messageAnalyticsEntity = new MessageEntity.MessageAnalyticsEntity()
                                    .setTime(System.currentTimeMillis()).setSyncMessageCountToken(messagePlotCount).setUserId(userId);

                            processMessageForAnalytics(true, messageAnalyticsEntity);
                        }
                    }

                }, this::exception));
    }

    private void exception(Throwable tr) {
        Log.e("MessageCountTest", "Error: " + tr.getMessage());
    }

    public void processMessageForAnalytics(boolean isMine, MessageEntity.MessageAnalyticsEntity messageAnalyticsEntity) {

        if (isMobileDataEnable() || !isMine) {
            MessageCountModel messageCountModel = messageAnalyticsEntity.toMessageCountModel();
            sendMessageCount(messageCountModel);
        } else {
            RmDataHelper.getInstance().analyticsDataSendToSellers(messageAnalyticsEntity);
        }
    }

    public void sendMessageCount(MessageCountModel messageCountModel) {
        AnalyticsApi.on().setAnalyticsType(Constants.AnalyticsResponseType.MESSAGE_COUNT)
                .setAnalyticsResponseCallback(this).saveMessageCount(messageCountModel);
    }

    /////////////////////////////////  New User Count //////////////////////////////////////////

    public void processNewNodesForAnalytics(List<UserEntity.NewMeshUserCount> newMeshUserCounts) {
        List<NewNodeModel> newNodeModels = new ArrayList<>();

        for (UserEntity.NewMeshUserCount newMeshUserCount : newMeshUserCounts) {
            newNodeModels.add(newMeshUserCount.toNewNodeModel());
        }

        if (newNodeModels.size() > 0) {
            SharedPref sharedPref = SharedPref.getSharedPref(TeleMeshApplication.getContext());

            if (!sharedPref.readBoolean(Constants.preferenceKey.MY_SYNC_IS_DONE)) {
                NewNodeModel newNodeModel = new NewNodeModel()
                        .setUserAddingTime(sharedPref.readLong(Constants.preferenceKey.MY_REGISTRATION_TIME))
                        .setUserId(sharedPref.read(Constants.preferenceKey.MY_USER_ID));

                newNodeModels.add(newNodeModel);
            }

            sendNewUserAnalytics(newNodeModels);
        }
    }

    public void sendNewUserAnalytics(List<NewNodeModel> nodeList) {
        AnalyticsApi.on().setAnalyticsType(Constants.AnalyticsResponseType.NEW_USER_COUNT)
                .setAnalyticsResponseCallback(this).sendNewUserAnalytics(nodeList);
    }

    ///////////////////////////// AppShare count /////////////////////

    public void sendAppShareCountAnalytics(List<AppShareCountEntity> entityList) {
        if (entityList.isEmpty()) return;
        List<AppShareCountModel> modelList = new ArrayList<>();
        for (AppShareCountEntity entity : entityList) {
            AppShareCountModel model = new AppShareCountModel();
            model.setCount(entity.getCount());
            model.setDate(entity.getDate());
            model.setUserId(entity.getUserId());

            modelList.add(model);
        }

        countSentList = entityList;

        sendAppShareCount(modelList);
    }

    private void sendAppShareCount(List<AppShareCountModel> model) {
        AnalyticsApi.on().setAnalyticsType(Constants.AnalyticsResponseType.APP_SHARE_COUNT)
                .setAnalyticsResponseCallback(this).sendAppShareCount(model);
    }

    public void sendLogFileInServer(File file, String userId, String deviceName) {
        AnalyticsApi.on().sendLogFileInServer(file, userId, deviceName, fileUploadResponseCallback);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean isMobileDataEnable() {
        return BulletinTimeScheduler.getInstance().isMobileDataEnable();
    }

    @Override
    public void response(boolean isSuccess, byte analyticsType) {
        if (analyticsType == Constants.AnalyticsResponseType.MESSAGE_COUNT) {

        } else if (analyticsType == Constants.AnalyticsResponseType.NEW_USER_COUNT) {
            if (isSuccess) {
                SharedPref.getSharedPref(TeleMeshApplication.getContext()).write(Constants.preferenceKey.MY_SYNC_IS_DONE, true);
                RmDataHelper.getInstance().updateSyncedUser();
            }
        } else if (analyticsType == Constants.AnalyticsResponseType.APP_SHARE_COUNT) {
            HandlerUtil.postBackground(() -> {
                if (countSentList != null) {
                    if (isSuccess) {
                        Log.d("AppShareTest", "countSentList not null");
                        for (AppShareCountEntity entity : countSentList) {
                            int id = AppShareCountDataService.getInstance().deleteCount(entity.getUserId(), entity.getDate());
                            Log.d("AppShareTest", "Delete id " + id);
                        }
                    }

                }
            });
        }
    }
}
