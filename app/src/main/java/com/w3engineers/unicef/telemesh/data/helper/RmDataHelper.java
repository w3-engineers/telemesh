package com.w3engineers.unicef.telemesh.data.helper;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.mesh.util.NetworkMonitor;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsDataHelper;
import com.w3engineers.unicef.telemesh.data.broadcast.BroadcastManager;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.AppInstaller;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdateModel;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountDataService;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.ShareCountModel;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackDataSource;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackEntity;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackModel;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogDataSource;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageCount;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.ConnectivityUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class RmDataHelper implements BroadcastManager.BroadcastSendCallback {

    private static RmDataHelper rmDataHelper = new RmDataHelper();
    protected MeshDataSource rightMeshDataSource;

    protected DataSource dataSource;

    public String mLatitude;
    public String mLongitude;

    @SuppressLint("UseSparseArrays")
    @NonNull
    public static HashMap<String, DataModel> rmDataMap = new HashMap<>();

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    public RmDataHelper() {
        dataSource = Source.getDbSource();
        BroadcastManager.getInstance().setBroadcastSendCallback(this);
    }

    @NonNull
    public static RmDataHelper getInstance() {
        return rmDataHelper;
    }

    @NonNull
    public MeshDataSource initRM(@NonNull DataSource dataSource) {

        this.dataSource = dataSource;
        prepareRightMeshDataSource();

        return rightMeshDataSource;
    }

    protected void prepareRightMeshDataSource() {
        if (rightMeshDataSource == null) {
            rightMeshDataSource = MeshDataSource.getRmDataSource();
        }
    }

    /**
     * This constructor is restricted and only used in unit test class
     *
     * @param dataSource -> provide mock dataSource from unit test class
     */
    public void initSource(@NonNull DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void meshInitiated() {
        if (dataSource != null) {
            dataSource.setMeshInitiated(true);
        }
    }

    /**
     * after inserting the message to the db
     * here we will fetch the last inserted message that will be
     * sent via RM.
     * <p>
     * Only for outgoing message this method will be responsible
     */
    @SuppressLint("CheckResult")
    public void prepareDataObserver() {

        AnalyticsDataHelper.getInstance().analyticsDataObserver();

        // This observer only for message send
        compositeDisposable.add(dataSource.getLastChatData()
                .subscribeOn(Schedulers.io())
                .subscribe(chatEntity -> {

                    if (!chatEntity.isIncoming() && (chatEntity.getStatus() == Constants.MessageStatus.STATUS_SENDING)) {

                        MessageEntity messageEntity = (MessageEntity) chatEntity;
                        if (messageEntity.getMessageType() == Constants.MessageType.TEXT_MESSAGE) {
                            String messageModelString = new Gson().toJson(messageEntity.toMessageModel());
                            if (messageEntity.getMessagePlace()) {
                                GroupDataHelper.getInstance().sendTextMessageToGroup(messageEntity.getGroupId(),
                                        messageModelString);
                            } else {
                                dataSend(messageModelString.getBytes(), Constants.DataType.MESSAGE,
                                        messageEntity.getFriendsId(), true);
                            }
                        } else {
                            ContentDataHelper.getInstance().prepareContentObserver(messageEntity, true);
                        }

                    }
                }, Throwable::printStackTrace));

        // This observer only for re send
        compositeDisposable.add(Objects.requireNonNull(dataSource.getReSendMessage())
                .subscribeOn(Schedulers.newThread())
                .subscribe(chatEntity -> {

                    if (!chatEntity.isIncoming() && chatEntity.getStatus() == Constants.MessageStatus.STATUS_SENDING) {
                        MessageEntity messageEntity = (MessageEntity) chatEntity;
                        if (messageEntity.getMessageType() == Constants.MessageType.TEXT_MESSAGE) {
                            String messageModelString = new Gson().toJson(messageEntity.toMessageModel());

                            dataSend(messageModelString.getBytes(),
                                    Constants.DataType.MESSAGE, chatEntity.getFriendsId(), true);
                        }
                    } else {
                        ContentDataHelper.getInstance().prepareContentObserver((MessageEntity) chatEntity, false);
                    }
                }, Throwable::printStackTrace));

        compositeDisposable.add(Objects.requireNonNull(dataSource.getLiveUserId())
                .subscribeOn(Schedulers.newThread())
                .subscribe(liveUserId -> {

                    if (!TextUtils.isEmpty(liveUserId)) {
                        prepareRightMeshDataSource();
                        rightMeshDataSource.checkUserIsConnected(liveUserId);
                        int userActiveType = rightMeshDataSource.checkUserConnectivityStatus(liveUserId);

                        HandlerUtil.postBackground(() -> {
                            MeshUserDataHelper.getInstance().updateUserActiveStatus(liveUserId, userActiveType);
                        });
                    }
                }, Throwable::printStackTrace));

        GroupDataHelper.getInstance().groupDataObserver();
    }

    /**
     * This api is responsible for creating a data model and send data to RM
     *
     * @param data -> raw data
     * @param type -> data type
     */
    protected void dataSend(@NonNull byte[] data, byte type, String userId, boolean isNotificationEnable) {

        DataModel dataModel = new DataModel()
                .setRawData(data)
                .setDataType(type);

        prepareRightMeshDataSource();

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> rightMeshDataSource.DataSend(dataModel, userId, isNotificationEnable));
    }

    protected void broadcastDataSend(String broadcastId, byte type, String metaData,
                                     String contentPath, String contentMeta, String expiryTime, boolean isNotificationEnable) {

        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        BroadcastDataModel broadcastDataModel = new BroadcastDataModel();
        broadcastDataModel.broadcastType = type;
        broadcastDataModel.rawData = metaData;

        String broadcastMetaData = gsonBuilder.getBroadcastDataModelJson(broadcastDataModel);

        prepareRightMeshDataSource();

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> rightMeshDataSource.broadcastDataSend(broadcastId, broadcastMetaData,
                contentPath, contentMeta, expiryTime, isNotificationEnable));
    }

    /**
     * During receive any data to from RM this API is manipulating data based on application
     *
     * @param dataModel -> contains all of info about receive data
     */

    public void dataReceive(@NonNull DataModel dataModel, boolean isNewMessage) {

        int dataType = dataModel.getDataType();
        byte[] rawData = dataModel.getRawData();
        String userId = dataModel.getUserId();
        boolean isAckSuccess = dataModel.isAckSuccess();
        int ackStatus = dataModel.getDataAckStatus();

        switch (dataType) {
            case Constants.DataType.MESSAGE:
                MeshMessageDataHelper.getInstance().setChatMessage(rawData, userId, isNewMessage, isAckSuccess, ackStatus);
                break;

            case Constants.DataType.MESSAGE_FEED:
//                BroadcastDataHelper.getInstance().receiveBroadcastMsgFromLocal(rawData, userId, isNewMessage, isAckSuccess);
                break;

            case Constants.DataType.MESSAGE_COUNT:
                setAnalyticsMessageCount(rawData, isAckSuccess);
                break;
            case Constants.DataType.APP_SHARE_COUNT:
                AppUpdateDataHelper.getInstance().saveAppShareCount(rawData, isAckSuccess);
                break;
            case Constants.DataType.VERSION_HANDSHAKING:
                AppUpdateDataHelper.getInstance().versionCrossMatching(rawData, userId, isAckSuccess);
                break;
            case Constants.DataType.SERVER_LINK:
                AppUpdateDataHelper.getInstance().startAppUpdate(rawData, isAckSuccess, userId);
                break;
            case Constants.DataType.FEEDBACK_TEXT:
                MeshMessageDataHelper.getInstance().parseFeedbackText(rawData, isAckSuccess);
                break;
            case Constants.DataType.FEEDBACK_ACK:
                MeshMessageDataHelper.getInstance().deleteSentFeedback(rawData, isAckSuccess);
                break;
            case Constants.DataType.USER_UPDATE_INFO:
                parseUpdatedInformation(rawData, userId, isNewMessage);
                break;
            case Constants.DataType.REQ_CONTENT_MESSAGE:
                ContentDataHelper.getInstance().requestedContentMessageSend(rawData, userId);
                break;
            case Constants.DataType.SUCCESS_CONTENT_MESSAGE:
                ContentDataHelper.getInstance().contentMessageSuccessResponse(rawData);
                break;

            case Constants.DataType.EVENT_GROUP_CREATION:
            case Constants.DataType.EVENT_GROUP_LEAVE:
            case Constants.DataType.EVENT_GROUP_RENAME:
            case Constants.DataType.EVENT_GROUP_MEMBER_ADD:
            case Constants.DataType.EVENT_GROUP_MEMBER_REMOVE:
            case Constants.DataType.EVENT_GROUP_DATA_RELAY:
            case Constants.DataType.EVENT_GROUP_DATA_FORWARD:
                GroupDataHelper.getInstance().groupDataReceive(dataType, userId, rawData, isNewMessage);
                break;
        }
    }

    private void setAnalyticsMessageCount(byte[] rawMessageCountAnalyticsData, boolean isAck) {
        try {
            if (!isAck) {

                String messageCountString = new String(rawMessageCountAnalyticsData);

                MessageCount messageCount = new Gson().fromJson(messageCountString, MessageCount.class);

                MessageEntity.MessageAnalyticsEntity messageAnalyticsEntity = new MessageEntity
                        .MessageAnalyticsEntity().toMessageAnalyticsEntity(messageCount);

                AnalyticsDataHelper.getInstance().processMessageForAnalytics(false, messageAnalyticsEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * When we got any ack message from RM this API is responsible
     * for updating med message status which already sent
     *
     * @param dataModel -> Contains received message id
     */
    public void ackReceive(@NonNull DataModel dataModel, int status) {

        String dataSendId = dataModel.getDataTransferId();

        if (dataSendId != null && rmDataMap.get(dataSendId) != null) {

            DataModel prevRMDataModel = rmDataMap.get(dataSendId);

            if (prevRMDataModel != null) {

                prevRMDataModel.setAckSuccess(dataModel.isAckSuccess());

                if (status == Constant.MessageStatus.SEND) {
                    prevRMDataModel.setDataAckStatus(Constants.MessageStatus.STATUS_SEND);
                } else if (status == Constant.MessageStatus.DELIVERED) {
                    prevRMDataModel.setDataAckStatus(Constants.MessageStatus.STATUS_DELIVERED);
                } else if (status == Constant.MessageStatus.RECEIVED) {
                    prevRMDataModel.setDataAckStatus(Constants.MessageStatus.STATUS_RECEIVED);
                    rmDataMap.remove(dataSendId);
                }

                dataReceive(prevRMDataModel, false);
                return;
            }
        }
    }

    public void destroyMeshService() {
        compositeDisposable.add(MeshUserDataHelper.getInstance().updateUserToOffline()
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    MeshMessageDataHelper.getInstance().updateMessageStatus();
                }, Throwable::printStackTrace));
    }


    /**
     * Concern for this api stopping RM service from app layer
     */
    public void stopRmService() {
        rightMeshDataSource.stopMeshService();
    }


    public boolean isMeshServiceRunning() {
        Context context = TeleMeshApplication.getContext();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals("com.w3engineers.meshrnd.TeleMeshService")) {
                return true;
            }
        }
        return false;
    }

    /**
     * For ReInitiating RM service need to reset rightmesh data source instance
     */
    public void restartMesh() {
        rightMeshDataSource.restartMeshService();
    }

    protected String getMyMeshId() {
        return SharedPref.getSharedPref(TeleMeshApplication.getContext()).read(Constants.preferenceKey.MY_USER_ID);
    }


    public void analyticsDataSendToSellers(MessageEntity.MessageAnalyticsEntity messageAnalyticsEntity) {

        MessageCount messageCount = messageAnalyticsEntity.toAnalyticMessageCount();
        String messageCountString = new Gson().toJson(messageCount);

        prepareRightMeshDataSource();

        for (String sellersId : rightMeshDataSource.getAllSellers()) {
            dataSend(messageCountString.getBytes(), Constants.DataType.MESSAGE_COUNT, sellersId, false);
        }
    }

    public void sendAppShareCount() {

        ConnectivityUtil.isInternetAvailable(TeleMeshApplication.getContext(), (s, isConnected) -> {
            if (isConnected) {
                sendAppShareCountAnalytics();
            } else {
                compositeDisposable.add(AppShareCountDataService.getInstance().getTodayAppShareCount(TimeUtil.getDateString(System.currentTimeMillis()))
                        .subscribeOn(Schedulers.newThread()).subscribe(this::sendAppShareCountToSellers, Throwable::printStackTrace));
            }
        });

        sendPendingFeedback();
    }

    /**
     * Send data to local user (Seller)
     */
    public void sendAppShareCountToSellers(List<AppShareCountEntity> entityList) {
        for (AppShareCountEntity entity : entityList) {
            ShareCountModel appShareCount = entity.toAnalyticAppShareCount();
            String shareCountString = new Gson().toJson(appShareCount);
            prepareRightMeshDataSource();
            for (String sellersId : rightMeshDataSource.getAllSellers()) {
                dataSend(shareCountString.getBytes(), Constants.DataType.APP_SHARE_COUNT, sellersId, false);
            }
        }
    }

    /**
     * Send data to server
     */
    public void sendAppShareCountAnalytics() {
        compositeDisposable.add(AppShareCountDataService.getInstance()
                .getTodayAppShareCount(TimeUtil.getDateString(System.currentTimeMillis()))
                .subscribeOn(Schedulers.newThread())
                .subscribe(countList -> {
                    AnalyticsDataHelper.getInstance().sendAppShareCountAnalytics(countList);
                }, Throwable::printStackTrace));
    }

    public void newUserAnalyticsSend() {
        compositeDisposable.add(Single.fromCallable(() ->
                UserDataSource.getInstance().getUnSyncedUsers(getMyMeshId()))
                .subscribeOn(Schedulers.newThread())
                .subscribe(newMeshUserCounts -> {
                    AnalyticsDataHelper.getInstance().processNewNodesForAnalytics(newMeshUserCounts);
                }, Throwable::printStackTrace));
    }

    public void updateSyncedUser() {
        compositeDisposable.add(Single.fromCallable(() ->
                UserDataSource.getInstance().updateUserSynced(getMyMeshId()))
                .subscribeOn(Schedulers.newThread())
                .subscribe(syncedUserCounts -> {
                }, Throwable::printStackTrace));
    }

    public void sendFeedbackToInternetUser(FeedbackEntity entity) {
        FeedbackModel model = entity.toFeedbackModel();
        String feedbackJson = new Gson().toJson(model);

        for (String sellersId : rightMeshDataSource.getAllSellers()) {
            dataSend(feedbackJson.getBytes(), Constants.DataType.FEEDBACK_TEXT, sellersId, false);
        }
    }

    public void sendFeedbackAck(FeedbackModel model) {
        String feedbackJson = new Gson().toJson(model);
        dataSend(feedbackJson.getBytes(), Constants.DataType.FEEDBACK_ACK, model.getUserId(), false);
    }

    public void sendPendingFeedback() {
        compositeDisposable
                .add(Single.fromCallable(() -> FeedbackDataSource.getInstance().getFirstFeedback())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(feedback -> {
                            AnalyticsDataHelper.getInstance().sendFeedback(feedback);
                        }, Throwable::printStackTrace));
    }

    public void uploadLogFile() {
        // Below method commented out because now we not upload log file in server

        compositeDisposable.add(Single.fromCallable(() ->
                MeshLogDataSource.getInstance().getAllUploadedLogList())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::uploadLogFile, Throwable::printStackTrace));
    }

    private void uploadLogFile(List<String> previousList) {
        if (previousList == null) {
            previousList = new ArrayList<>();
        }

        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() +
                "/Telemesh/MeshLog");
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!previousList.contains(file.getName())
                        && Constant.CURRENT_LOG_FILE_NAME != null
                        && !Constant.CURRENT_LOG_FILE_NAME.equalsIgnoreCase(file.getName())) {
                    AnalyticsDataHelper.getInstance().sendLogFileInServer(file, TextUtils.isEmpty(getMyMeshId()) ? "Test User" : getMyMeshId(), Constants.getDeviceName());
                }
            }
        }
    }

    @Override
    public void dataSent(@NonNull DataModel rmDataModel, String dataSendId) {
        rmDataMap.put(dataSendId, rmDataModel);
    }

    @Override
    public void contentSent(ContentModel contentModel, String dataSendId) {

        ContentDataHelper.getInstance().contentDataSend(dataSendId, contentModel);
    }

    private void parseUpdatedInformation(byte[] rawData, String userId, boolean isNewMessage) {
        if (!isNewMessage) return;

        String updatedUserData = new String(rawData);
        UserModel userModel = new Gson().fromJson(updatedUserData, UserModel.class);
        userModel.setUserId(userId);

        UserEntity userEntity = UserDataSource.getInstance().getSingleUserById(userId);

        if (userEntity != null) {

            if (TextUtils.isEmpty(userEntity.getUserName())) {
                userEntity = userEntity.updateUserEntity(userModel);
            } else {
                userEntity = userEntity.updateUserEntity(userModel);
                prepareRightMeshDataSource();
                rightMeshDataSource.saveUpdateOtherUserInfo(userEntity.getMeshId(), userEntity.getUserName(), userEntity.getAvatarIndex());

                UserDataSource.getInstance().insertOrUpdateData(userEntity);
            }
            GroupDataHelper.getInstance().updateGroupUserInfo(userEntity);
        }
    }

 /*   public void showMeshLog(String log) {
        // LogProcessUtil.getInstance().writeLog(log);
    }*/

    public void broadcastUpdateProfileInfo(@NonNull String userName, int imageIndex) {

        // Save current my information in SDK layer
        rightMeshDataSource.saveUpdateUserInfo();

        UserModel userModel = new UserModel();
        userModel.setImage(imageIndex);
        userModel.setName(userName);

        String updateInfo = new Gson().toJson(userModel);

        DataModel dataModel = new DataModel()
                .setRawData(updateInfo.getBytes())
                .setDataType(Constants.DataType.USER_UPDATE_INFO);

        prepareRightMeshDataSource();

        compositeDisposable.add(UserDataSource.getInstance().getAllFabMessagedActiveUserIds()
                .subscribeOn(Schedulers.newThread())
                .subscribe(users -> {

                    GroupDataHelper.getInstance().updateMyUserInfo();
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    service.execute(() -> rightMeshDataSource.DataSend(dataModel, users, false));

                }, Throwable::printStackTrace));

        compositeDisposable.add(GroupDataHelper.getInstance()
                .syncUserUpdateInfoForUnDiscovered(updateInfo, Constants.DataType.USER_UPDATE_INFO)
                .subscribeOn(Schedulers.newThread())
                .subscribe());
    }

    // APP update process

    public void versionMessageHandshaking(String userId) {
        InAppUpdateModel model = InAppUpdate.getInstance(TeleMeshApplication.getContext()).getAppVersion();

        SharedPref sharedPref = SharedPref.getSharedPref(TeleMeshApplication.getContext());
        int versionCode = sharedPref.readInt(Constants.preferenceKey.APP_UPDATE_VERSION_CODE);
        if (BuildConfig.VERSION_CODE == versionCode) {
            model.setUpdateType(sharedPref.readInt(Constants.preferenceKey.APP_UPDATE_TYPE));
        }

        String data = new Gson().toJson(model);
        dataSend(data.getBytes(), Constants.DataType.VERSION_HANDSHAKING, userId, false);
    }


    public void destroy() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable.dispose();
        }
    }

}
