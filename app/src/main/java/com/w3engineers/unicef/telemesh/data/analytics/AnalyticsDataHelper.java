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

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.analytics.callback.AnalyticsResponseCallback;
import com.w3engineers.unicef.telemesh.data.analytics.callback.FeedbackSendCallback;
import com.w3engineers.unicef.telemesh.data.analytics.callback.FileUploadResponseCallback;
import com.w3engineers.unicef.telemesh.data.analytics.callback.GroupCountSendCallback;
import com.w3engineers.unicef.telemesh.data.analytics.model.AppShareCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.FeedbackParseModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.GroupCountParseModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.NewNodeModel;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountDataService;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackDataSource;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackEntity;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupCountModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogDataSource;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.ConnectivityUtil;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AnalyticsDataHelper implements AnalyticsResponseCallback {

    private static AnalyticsDataHelper analyticsDataHelper;
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    private int trackMessageCount = 0;
    private List<AppShareCountEntity> countSentList;
    private static FileUploadResponseCallback fileUploadResponseCallback;
    private static FeedbackSendCallback feedbackSendCallback;
    private static GroupCountSendCallback groupCountSendCallback;

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

        feedbackSendCallback = (isSuccess, model) -> {
            if (isSuccess) {
                if (model.isDirectSend()) {
                    compositeDisposable
                            .add(Single.fromCallable(() -> FeedbackDataSource.getInstance().deleteFeedbackById(model.getFeedbackId()))
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe((result) -> {
                                        Timber.tag("FeedbackTest").d("Delete result: %s", result);
                                    }, Throwable::printStackTrace));
                } else {
                    FeedbackModel feedbackModel = new FeedbackModel();
                    feedbackModel.setUserId(model.getUserId());
                    feedbackModel.setUserName(model.getUserName());
                    feedbackModel.setFeedback(model.getFeedback());
                    feedbackModel.setFeedbackId(model.getFeedbackId());
                    RmDataHelper.getInstance().sendFeedbackAck(feedbackModel);
                }
            }
        };

        groupCountSendCallback = (isSuccess, modelList) -> {
            if (isSuccess) {
                boolean isDirectSend = modelList.get(0).isDirectSend();
                if (isDirectSend) {
                    for (GroupCountParseModel groupCountParseModel : modelList) {

                            compositeDisposable
                                    .add(Single.fromCallable(() -> GroupDataSource.getInstance().updateGroupAsSynced(groupCountParseModel.getGroupId()))
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe((result) -> {
                                                Timber.tag("GroupCount").d("update result: %s", result);

                                                RmDataHelper.getInstance().notifyGroupMembersAsSyncedGroup(groupCountParseModel.getGroupId());
                                            }, Throwable::printStackTrace));

                    }
                } else {
                    RmDataHelper.getInstance().sendGroupCountSyncAck(modelList);
                }
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

                            String userId = SharedPref.read(Constants.preferenceKey.MY_USER_ID);

                            MessageEntity.MessageAnalyticsEntity messageAnalyticsEntity = new MessageEntity.MessageAnalyticsEntity()
                                    .setTime(System.currentTimeMillis()).setSyncMessageCountToken(messagePlotCount).setUserId(userId);

                            processMessageForAnalytics(true, messageAnalyticsEntity);
                        }
                    }

                }, this::exception));
    }

    private void exception(Throwable tr) {
//        Timber.tag("MessageCountTest").e("Error: %s", tr.getMessage());
    }

    public void processMessageForAnalytics(boolean isMine, MessageEntity.MessageAnalyticsEntity messageAnalyticsEntity) {

        ConnectivityUtil.isInternetAvailable(TeleMeshApplication.getContext(), (s, isConnected) -> {
            if (isConnected || !isMine) {
                MessageCountModel messageCountModel = messageAnalyticsEntity.toMessageCountModel();
                sendMessageCount(messageCountModel);
            } else {
                RmDataHelper.getInstance().analyticsDataSendToSellers(messageAnalyticsEntity);
            }
        });
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

            if (!SharedPref.readBoolean(Constants.preferenceKey.MY_SYNC_IS_DONE)) {
                NewNodeModel newNodeModel = new NewNodeModel()
                        .setUserAddingTime(SharedPref.readLong(Constants.preferenceKey.MY_REGISTRATION_TIME))
                        .setUserId(SharedPref.read(Constants.preferenceKey.MY_USER_ID));

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

    public void sendFeedback(FeedbackEntity entity) {
        ConnectivityUtil.isInternetAvailable(TeleMeshApplication.getContext(), (s, isConnected) -> {
            if (isConnected) {
                Timber.tag("FeedbackTest").d("Feedback send directly");
                sendFeedbackToInternet(entity, true);
            } else {
                RmDataHelper.getInstance().sendFeedbackToInternetUser(entity);
            }
        });
    }

    public void sendGroupCount(List<GroupEntity> groupEntities) {
        ConnectivityUtil.isInternetAvailable(TeleMeshApplication.getContext(), (s, isConnected) -> {
            if (isConnected) {
                Timber.tag("GroupCountTest").d("GroupCount send directly");
                sendGroupCountToInternet(groupEntities, true);
            } else {
                Timber.tag("GroupCountTest").d("No internet available");
                RmDataHelper.getInstance().sendGroupCountToInternetUser(groupEntities);
            }
        });
    }




    public void sendFeedbackToInternet(FeedbackEntity entity, boolean isDirectSend) {
        FeedbackParseModel model = new FeedbackParseModel();
        model.setUserId(entity.getUserId());
        model.setUserName(entity.getUserName());
        model.setFeedback(entity.getFeedback());
        model.setFeedbackId(entity.getFeedbackId());
        model.setDirectSend(isDirectSend);

        AnalyticsApi.on().sendFeedback(model, feedbackSendCallback);
    }

    public void sendGroupCountToInternet(List<GroupEntity> groupList, boolean isDirectSend) {
        String myId = SharedPref.read(Constants.preferenceKey.MY_USER_ID);

        ArrayList<GroupCountParseModel> groupCountParseModels = new ArrayList<>();
        for (GroupEntity groupEntity: groupList) {
            GroupCountParseModel groupCountParseModel = new GroupCountParseModel();

            groupCountParseModel.setGroupId(groupEntity.getGroupId());
            groupCountParseModel.setGroupOwner(groupEntity.getAdminInfo());
            groupCountParseModel.setMemberCount(groupEntity.getMemberCount());
            groupCountParseModel.setCreationDate(TimeUtil.getDateStringFromMillisecond(groupEntity.groupCreationTime));
            groupCountParseModel.setSubmittedBy(myId);
            groupCountParseModel.setDirectSend(isDirectSend);
            groupCountParseModels.add(groupCountParseModel);
        }
        AnalyticsApi.on().sendGroupCount(groupCountParseModels, groupCountSendCallback);
    }

    public void sendGroupCountToInternet(List<GroupCountModel> groupList) {
        ArrayList<GroupCountParseModel> groupCountParseModels = new ArrayList<>();

        for (GroupCountModel groupCountModel: groupList) {
            GroupCountParseModel groupCountParseModel = new GroupCountParseModel();

            groupCountParseModel.setGroupId(groupCountModel.getGroupId());
            groupCountParseModel.setGroupOwner(groupCountModel.getGroupOwnerId());
            groupCountParseModel.setMemberCount(groupCountModel.getMemberCount());
            groupCountParseModel.setCreationDate(groupCountModel.getCreatedTime());
            groupCountParseModel.setSubmittedBy(groupCountModel.getSubmittedBy());
            groupCountParseModel.setDirectSend(groupCountModel.getDirectSend());
            groupCountParseModels.add(groupCountParseModel);
        }
        AnalyticsApi.on().sendGroupCount(groupCountParseModels, groupCountSendCallback);
    }




    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean isInternetDataEnable() {
        return false;
    }

    @Override
    public void response(boolean isSuccess, byte analyticsType) {
        if (analyticsType == Constants.AnalyticsResponseType.MESSAGE_COUNT) {

        } else if (analyticsType == Constants.AnalyticsResponseType.NEW_USER_COUNT) {
            if (isSuccess) {
                SharedPref.write(Constants.preferenceKey.MY_SYNC_IS_DONE, true);
                RmDataHelper.getInstance().updateSyncedUser();
            }
        } else if (analyticsType == Constants.AnalyticsResponseType.APP_SHARE_COUNT) {
            HandlerUtil.postBackground(() -> {
                if (countSentList != null) {
                    if (isSuccess) {
                        Timber.tag("AppShareTest").d("countSentList not null");
                        for (AppShareCountEntity entity : countSentList) {
                            int id = AppShareCountDataService.getInstance().deleteCount(entity.getUserId(), entity.getDate());
                            Timber.tag("AppShareTest").d("Delete id %s", id);
                        }
                    }
                }
            });
        }
    }
}
