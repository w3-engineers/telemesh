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
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinDataSource;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinTrackEntity;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.feed.AckCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinFeed;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.feed.GeoLocation;
import com.w3engineers.unicef.telemesh.data.local.feed.Payload;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackDataSource;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackEntity;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackModel;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogDataSource;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageCount;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.ConnectivityUtil;
import com.w3engineers.unicef.util.helper.ContentUtil;
import com.w3engineers.unicef.util.helper.LocationUtil;
import com.w3engineers.unicef.util.helper.NotifyUtil;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import timber.log.Timber;

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

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

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

    /**
     * This api is responsible for insert users in database when users is added
     *
     * @param userModel -> contains all of info about users
     */

    public void userAdd(@NonNull UserModel userModel) {

        String userId = userModel.getUserId();
        int userActiveStatus = rightMeshDataSource.getUserActiveStatus(userId);

        int userConnectivityStatus = getActiveStatus(userActiveStatus);

        UserEntity previousEntity = UserDataSource.getInstance().getSingleUserById(userId);

        UserEntity userEntity;

        if (previousEntity != null) {

            userEntity = previousEntity
                    .toUserEntity(userModel)
                    .setOnlineStatus(userConnectivityStatus);

        } else {
            userEntity = new UserEntity()
                    .toUserEntity(userModel)
                    .setOnlineStatus(userConnectivityStatus);
        }


        UserDataSource.getInstance().insertOrUpdateData(userEntity);

        syncUserWithBroadcastMessage(userId);
    }

    public boolean userExistedOperation(String userId, int userActiveStatus) {

        int userConnectivityStatus = getActiveStatus(userActiveStatus);

        int updateId = UserDataSource.getInstance()
                .updateUserStatus(userId, userConnectivityStatus);

        if (updateId > 0 && (userConnectivityStatus == Constants.UserStatus.WIFI_ONLINE
                || userConnectivityStatus == Constants.UserStatus.WIFI_MESH_ONLINE
                || userConnectivityStatus == Constants.UserStatus.BLE_ONLINE
                || userConnectivityStatus == Constants.UserStatus.BLE_MESH_ONLINE
                || userConnectivityStatus == Constants.UserStatus.HB_ONLINE
                || userConnectivityStatus == Constants.UserStatus.HB_MESH_ONLINE)) {
            syncUserWithBroadcastMessage(userId);
        }

        return updateId > 0;
    }

    public int getActiveStatus(int userActiveStatus) {

        switch (userActiveStatus) {
            case 1:
                return Constants.UserStatus.WIFI_ONLINE;
            case 2:
                return Constants.UserStatus.BLE_ONLINE;
            case 3:
                return Constants.UserStatus.WIFI_MESH_ONLINE;
            case 4:
                return Constants.UserStatus.BLE_MESH_ONLINE;
            case 5:
                return Constants.UserStatus.INTERNET_ONLINE;
            case 6:
                return Constants.UserStatus.HB_ONLINE;
            case 7:
                return Constants.UserStatus.HB_MESH_ONLINE;
            default:
                return Constants.UserStatus.OFFLINE;
        }
    }

    /**
     * This api is responsible for update user info in database
     * when users is gone in mesh network
     *
     * @param peerId -> network peer id
     */

    public void userLeave(@NonNull String peerId) {

        // FAILED MAINTAINED
        MessageSourceData.getInstance().changeMessageStatusByUserId(
                Constants.ContentStatus.CONTENT_STATUS_RECEIVING,
                Constants.MessageStatus.STATUS_FAILED, peerId);

        MessageSourceData.getInstance().changeUnreadMessageStatusByUserId(
                Constants.ContentStatus.CONTENT_STATUS_RECEIVING,
                Constants.MessageStatus.STATUS_UNREAD_FAILED, peerId);
        // No need already update user status in userExistedOperation api of RmDataHelper class
//        UserDataSource.getInstance().updateUserStatus(peerId, Constants.UserStatus.OFFLINE);
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

                            dataSend(messageModelString.getBytes(), Constants.DataType.MESSAGE,
                                    chatEntity.getFriendsId(), true);
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
                    }
                }, Throwable::printStackTrace));
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
                setChatMessage(rawData, userId, isNewMessage, isAckSuccess, ackStatus);
                break;

            case Constants.DataType.MESSAGE_FEED:
                setBulletinMessage(rawData, userId, isNewMessage, isAckSuccess);
                break;

            case Constants.DataType.MESSAGE_COUNT:
                setAnalyticsMessageCount(rawData, isAckSuccess);
                break;
            case Constants.DataType.APP_SHARE_COUNT:
                saveAppShareCount(rawData, isAckSuccess);
                break;
            case Constants.DataType.VERSION_HANDSHAKING:
                versionCrossMatching(rawData, userId, isAckSuccess);
                break;
            case Constants.DataType.SERVER_LINK:
                startAppUpdate(rawData, isAckSuccess, userId);
                break;
            case Constants.DataType.FEEDBACK_TEXT:
                parseFeedbackText(rawData, isAckSuccess);
                break;
            case Constants.DataType.FEEDBACK_ACK:
                deleteSentFeedback(rawData, isAckSuccess);
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
        }
    }

    private void setChatMessage(byte[] rawChatData, String userId, boolean isNewMessage, boolean isAckSuccess, int ackStatus) {
        try {

            String messageModelText = new String(rawChatData);
            MessageModel messageModel = new Gson().fromJson(messageModelText, MessageModel.class);

            ChatEntity chatEntity = new MessageEntity()
                    .toChatEntity(messageModel)
                    .setFriendsId(userId)
                    .setTime(System.currentTimeMillis())
                    .setIncoming(true);

            if (isNewMessage) {
                chatEntity.setStatus(Constants.MessageStatus.STATUS_READ).setIncoming(true);
                Timber.e("Read :: %s", chatEntity.getMessageId());
                //prepareDateSeparator(chatEntity);

                if (TextUtils.isEmpty(dataSource.getCurrentUser()) || !userId.equals(dataSource.getCurrentUser())) {
                    Timber.e("Un Read :: %s", chatEntity.getMessageId());
                    NotifyUtil.showNotification(chatEntity);
                    chatEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD);
                }

                MessageSourceData.getInstance().insertOrUpdateData(chatEntity);

            } else {

                /*
                 * for delivery status update we don't need to replace the message and insert again.
                 * If we do so then paging library Diff Callback can't properly work
                 */

                chatEntity.setStatus(ackStatus).setIncoming(false);
                Timber.e("Delivered :: %s", chatEntity.getMessageId());
                dataSource.updateMessageStatus(chatEntity.getMessageId(), chatEntity.getStatus());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBulletinMessage(byte[] rawBulletinData, String userId, boolean isNewMessage, boolean isAckSuccess) {
        try {

            String bulletinString = new String(rawBulletinData);
            BulletinModel bulletinModel = new Gson().fromJson(bulletinString, BulletinModel.class);

            FeedEntity feedEntity = new FeedEntity()
                    .toFeedEntity(bulletinModel);

            if (isNewMessage) {
                feedEntity.setFeedReadStatus(false);

                compositeDisposable.add(Single.fromCallable(() -> FeedDataSource.getInstance()
                        .insertOrUpdateData(feedEntity)).subscribeOn(Schedulers.newThread())
                        .subscribe(aLong -> {
                            if (aLong != -1) {
                                if (!TextUtils.isEmpty(feedEntity.getFeedId())) {
                                    BulletinDataSource.getInstance().insertOrUpdate(
                                            getMyTrackEntity(feedEntity.getFeedId())
                                                    .setBulletinOwnerStatus(Constants.Bulletin.OTHERS)
                                                    .setBulletinAckStatus(Constants.Bulletin.BULLETIN_SEND_TO_SERVER));
                                }
                            }
                        }, Throwable::printStackTrace));
            } else {
                if (!TextUtils.isEmpty(feedEntity.getFeedId())) {
                    BulletinDataSource.getInstance().insertOrUpdate(
                            getOthersTrackEntity(feedEntity.getFeedId(), userId)
                                    .setBulletinAckStatus(isAckSuccess ?
                                            Constants.Bulletin.BULLETIN_RECEIVED : Constants.Bulletin.DEFAULT));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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

    private void saveAppShareCount(byte[] rawData, boolean isAckSuccess) {
        try {

            String shareCountString = new String(rawData);

            ShareCountModel shareCountModel = new Gson().fromJson(shareCountString, ShareCountModel.class);
            AppShareCountEntity entity = new AppShareCountEntity().toAppShareCountEntity(shareCountModel);

            if (!isAckSuccess) {
                compositeDisposable.add(Single.fromCallable(() -> AppShareCountDataService.getInstance()
                        .insertAppShareCount(entity))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(longResult -> {
                            if (longResult > 1) {
//                                Timber.tag("AppShareCount").d("Data saved");
                            }
                        }, Throwable::printStackTrace));


            } else {
                compositeDisposable.add(Single.fromCallable(() -> AppShareCountDataService.getInstance()
                        .updateSentShareCount(entity.getUserId(), entity.getDate()))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(longResult -> {
                            if (longResult > 1) {
//                                Timber.tag("AppShareCount").d("Data Deleted");
                            }
                        }, Throwable::printStackTrace));
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

   /* public void stopMeshService() {
        updateUserStatus(true);
    }*/

    public void updateUserStatus(boolean isServiceStop) {
        compositeDisposable.add(updateUserToOffline()
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    /*if (isServiceStop) {
                        stopMeshProcess();
                    }*/
                    updateMessageStatus();
                }, Throwable::printStackTrace));
    }

    private Single<Integer> updateUserToOffline() {
        return Single.fromCallable(() ->
                UserDataSource.getInstance().updateUserToOffline());
    }

    public void destroyMeshService() {
        compositeDisposable.add(updateUserToOffline()
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    updateMessageStatus();
                }, Throwable::printStackTrace));
    }

    private void updateMessageStatus() {
        compositeDisposable.add(updateMessageStatusFailed()
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    updateReceiveMessageStatusFailed();
                }, Throwable::printStackTrace));
    }

    private Single<Long> updateMessageStatusFailed() {
        return Single.fromCallable(() ->
                MessageSourceData.getInstance().changeMessageStatusFrom(
                        Constants.MessageStatus.STATUS_SENDING_START,
                        Constants.MessageStatus.STATUS_FAILED));
    }

    private void updateReceiveMessageStatusFailed() {
        // FAILED MAINTAINED
        compositeDisposable.add(Single.fromCallable(() ->
                MessageSourceData.getInstance().changeMessageStatusByContentStatus(
                        Constants.ContentStatus.CONTENT_STATUS_RECEIVING,
                        Constants.MessageStatus.STATUS_FAILED))
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    updateReceiveUnreadMessageStatusFailed();
                }, Throwable::printStackTrace));
    }

    private void updateReceiveUnreadMessageStatusFailed() {
        // FAILED MAINTAINED
        compositeDisposable.add(Single.fromCallable(() ->
                MessageSourceData.getInstance().changeUnreadMessageStatusByContentStatus(
                        Constants.ContentStatus.CONTENT_STATUS_RECEIVING,
                        Constants.MessageStatus.STATUS_UNREAD_FAILED))
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {

                }, Throwable::printStackTrace));
    }

    /**
     * Concern for this api stopping RM service from app layer
     */
    public void stopRmService() {
        rightMeshDataSource.stopMeshService();
    }

    /**
     * This api called when all of app layer dependencies are removed,
     * i.e. update user status to offline successfully then called this method
     */
    private void stopMeshProcess() {
        prepareRightMeshDataSource();
        // TODO app needed to stop full process when close the service from library
//        rightMeshDataSource.stopMeshProcess();
    }

    public void resetUserToOfflineBasedOnService() {
        boolean isServiceEnable = isMeshServiceRunning();
        if (!isServiceEnable) {
            updateUserStatus(false);
        }
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

    // TODO SSID_Change
    /*public void destroyMeshService() {
        rightMeshDataSource.destroyMeshService();
        rightMeshDataSource.resetViperInstance();

        rightMeshDataSource.resetInstance();

        rightMeshDataSource = MeshDataSource.getRmDataSource();

    }*/

    public void requestWsMessage() {
        if (TextUtils.isEmpty(mLatitude) || TextUtils.isEmpty(mLongitude)) {
            LocationUtil.getInstance().init(TeleMeshApplication.getContext()).getLocation().addLocationListener((lat, lang) -> {

                LocationUtil.getInstance().removeListener();

                mLatitude = lat;
                mLongitude = lang;

                getLocalUserCount();
            });
        } else {
            getLocalUserCount();
        }
    }

    private void requestWsMessageWithUserCount(List<String> localActiveUsers) {
        if (NetworkMonitor.isOnline()) {
            OkHttpClient.Builder client1 = new OkHttpClient.Builder();
            OkHttpClient client = client1.socketFactory(NetworkMonitor.getNetwork().getSocketFactory()).build();

//        OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(AppCredentials.getInstance().getBroadCastUrl()).build();
            BroadcastWebSocket listener = new BroadcastWebSocket();
            listener.setBroadcastCommand(getBroadcastCommand(mLatitude, mLongitude, localActiveUsers));
            client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();
        }
    }

    private String getMyMeshId() {
        return SharedPref.getSharedPref(TeleMeshApplication.getContext()).read(Constants.preferenceKey.MY_USER_ID);
    }

    private void requestAckMessage(String messageId) {
        requestAckMessage(messageId, getMyMeshId());
    }

    private void requestAckMessage(String messageId, String userId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(AppCredentials.getInstance().getBroadCastUrl()).build();
        BroadcastWebSocket listener = new BroadcastWebSocket();
        listener.setBroadcastCommand(getAckCommand(messageId, userId));
        client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    void processBroadcastMessage(@NonNull String broadcastText) {
        try {
            Timber.tag("MIMO_SAHA:").v("Brd: %s", broadcastText);
            BulletinFeed bulletinFeed = new Gson().fromJson(broadcastText, BulletinFeed.class);

            requestAckMessage(bulletinFeed.getMessageId());

            FeedEntity feedEntity = new FeedEntity().toFeedEntity(bulletinFeed).setFeedReadStatus(false);

            compositeDisposable.add(Single.fromCallable(() -> FeedDataSource.getInstance()
                    .insertOrUpdateData(feedEntity)).subscribeOn(Schedulers.newThread())
                    .subscribe(aLong -> {
                        if (aLong != -1) {
                            if (!TextUtils.isEmpty(feedEntity.getFeedId())) {
                                BulletinDataSource.getInstance().insertOrUpdate(getMyTrackEntity(feedEntity.getFeedId()));
                            }
                            broadcastMessage(feedEntity);
                        }
                    }, Throwable::printStackTrace));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(@NonNull FeedEntity feedEntity) {

        List<String> meshDataList = new ArrayList<>();

        for (UserEntity userEntity : UserDataSource.getInstance().getLivePeers()) {

            meshDataList.add(userEntity.meshId);

            if (!TextUtils.isEmpty(feedEntity.getFeedId())) {
                BulletinDataSource.getInstance().insertOrUpdate(
                        getOthersTrackEntity(feedEntity.getFeedId(), userEntity.meshId));
            }
        }

        String bulletinString = new Gson().toJson(feedEntity.toTelemeshBulletin());

        DataModel rmDataModel = new DataModel()
                .setRawData(bulletinString.getBytes())
                .setDataType(Constants.DataType.MESSAGE_FEED);

        ExecutorService service = Executors.newSingleThreadExecutor();
        prepareRightMeshDataSource();
        service.execute(() -> rightMeshDataSource.DataSend(rmDataModel, meshDataList, false));
    }

    private void syncUserWithBroadcastMessage(String userId) {
        compositeDisposable.add(BulletinDataSource.getInstance()
                .getUnsentMessage(userId).subscribeOn(Schedulers.newThread())
                .subscribe(feedEntities -> {
                    sendSyncBroadcastMessage(feedEntities, userId);
                }, Throwable::printStackTrace));
    }

    private void sendSyncBroadcastMessage(List<FeedEntity> feedEntities, String userId) {
        if (feedEntities != null) {
            for (FeedEntity feedEntity : feedEntities) {

                if (!TextUtils.isEmpty(feedEntity.getFeedId())) {
                    BulletinDataSource.getInstance().insertOrUpdate(
                            getOthersTrackEntity(feedEntity.getFeedId(), userId));
                }
                String bulletinString = new Gson().toJson(feedEntity.toTelemeshBulletin());
                dataSend(bulletinString.getBytes(), Constants.DataType.MESSAGE_FEED, userId, false);
            }
        }
    }

    void processBroadcastAck(@NonNull String ackText) {
        AckCommand ackCommand = new Gson().fromJson(ackText, AckCommand.class);
        if (ackCommand.getStatus() == 1) {
            compositeDisposable.add(BulletinDataSource.getInstance()
                    .setFullSuccess(ackCommand.getAckMsgId(), ackCommand.getClientId())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(integer -> {
                    }, Throwable::printStackTrace));
        }
    }

    public void sendPendingAck() {
        compositeDisposable.add(BulletinDataSource.getInstance().getAllSuccessBulletin()
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::sendToServer, Throwable::printStackTrace));
    }

    private void sendToServer(List<BulletinTrackEntity> bulletinTrackEntities) {
        for (BulletinTrackEntity bulletinTrackEntity : bulletinTrackEntities) {
            requestAckMessage(bulletinTrackEntity.getBulletinMessageId(), bulletinTrackEntity.getBulletinTrackUserId());
        }
    }

    private BroadcastCommand getBroadcastCommand(String lat, String lang, List<String> localActiveUsers) {
        Payload payload = new Payload();

        GeoLocation geoLocation = new GeoLocation()
                .setLatitude(lat).setLongitude(lang);

        payload.setGeoLocation(geoLocation);

        if (localActiveUsers != null) {
            payload.setConnectedClients(String.valueOf(localActiveUsers.size()));
            payload.setConnectedClientEthIds(localActiveUsers);
        } else {
            payload.setConnectedClients("0");
            payload.setConnectedClientEthIds(new ArrayList<>());
        }

        return new BroadcastCommand().setEvent("connect")
                .setToken(AppCredentials.getInstance().getBroadCastToken())
                .setBaseStationId(getMyMeshId())
                .setClientId(getMyMeshId())
                .setPayload(payload);
    }

    private BroadcastCommand getAckCommand(String messageId, String userId) {
        Payload payload = new Payload().setMessageId(messageId);
        return new BroadcastCommand().setEvent("ack_msg_received")
                .setToken(AppCredentials.getInstance().getBroadCastToken())
                .setBaseStationId(getMyMeshId())
                .setClientId(userId)
                .setPayload(payload);
    }

    private BulletinTrackEntity getMyTrackEntity(String messageId) {
        return new BulletinTrackEntity()
                .setBulletinMessageId(messageId)
                .setBulletinTrackUserId(getMyMeshId())
                .setBulletinAckStatus(Constants.Bulletin.BULLETIN_RECEIVED)
                .setBulletinOwnerStatus(Constants.Bulletin.MINE);
    }

    private BulletinTrackEntity getOthersTrackEntity(String messageId, String userId) {
        return new BulletinTrackEntity()
                .setBulletinMessageId(messageId)
                .setBulletinTrackUserId(userId)
                .setBulletinAckStatus(Constants.Bulletin.BULLETIN_SEND)
                .setBulletinOwnerStatus(Constants.Bulletin.OTHERS);
    }

    ////////////////////////////////////////////////////////////////
    ////////////////// Analytics data Process //////////////////////
    ////////////////////////////////////////////////////////////////

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
                UserDataSource.getInstance().getUnSyncedUsers())
                .subscribeOn(Schedulers.newThread())
                .subscribe(newMeshUserCounts -> {
                    AnalyticsDataHelper.getInstance().processNewNodesForAnalytics(newMeshUserCounts);
                }, Throwable::printStackTrace));
    }

    public void updateSyncedUser() {
        compositeDisposable.add(Single.fromCallable(() ->
                UserDataSource.getInstance().updateUserSynced())
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

    public void appUpdateFromOtherServer(int type, String normalUpdateJson) {

        // check app update for internet;

        if (type == Constants.AppUpdateType.BLOCKER) {
            if (NetworkMonitor.isOnline()) {
                InAppUpdate.getInstance(MainActivity.getInstance()).setAppUpdateProcess(true);

                AppInstaller.downloadApkFile(AppCredentials.getInstance().getFileRepoLink(), MainActivity.getInstance(), NetworkMonitor.getNetwork());
            }

        } else {
            HandlerUtil.postForeground(() -> {
                if (!InAppUpdate.getInstance(TeleMeshApplication.getContext()).isAppUpdating()) {
                    //InAppUpdate.getInstance(TeleMeshApplication.getContext()).setAppUpdateProcess(true);
                    if (MainActivity.getInstance() == null) return;

                    SharedPref sharedPref = SharedPref.getSharedPref(TeleMeshApplication.getContext());
                    if (sharedPref.readBoolean(Constants.preferenceKey.ASK_ME_LATER)) {
                        long saveTime = sharedPref.readLong(Constants.preferenceKey.ASK_ME_LATER_TIME);
                        long dif = System.currentTimeMillis() - saveTime;
                        long days = dif / (24 * 60 * 60 * 1000);

                        if (days <= 2) return;
                    }

                    // We can show the dialog directly by creating a json file

                    InAppUpdate.getInstance(MainActivity.getInstance()).showAppInstallDialog(normalUpdateJson, MainActivity.getInstance());

                    // InAppUpdate.getInstance(TeleMeshApplication.getContext()).checkForUpdate(MainActivity.getInstance(), InAppUpdate.LIVE_JSON_URL);
                }
            }, TimeUnit.SECONDS.toMillis(5));
        }

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

    private void getLocalUserCount() {
        compositeDisposable.add(Single.fromCallable(() ->
                UserDataSource.getInstance().getLocalUserCount())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::requestWsMessageWithUserCount, Throwable::printStackTrace));
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

            userEntity = userEntity.updateUserEntity(userModel);

            rightMeshDataSource.saveUpdateOtherUserInfo(userEntity.getMeshId(), userEntity.getUserName(), userEntity.getAvatarIndex());

            UserDataSource.getInstance().insertOrUpdateData(userEntity);
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

                    ExecutorService service = Executors.newSingleThreadExecutor();
                    service.execute(() -> rightMeshDataSource.DataSend(dataModel, users, false));

                }, Throwable::printStackTrace));
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


    private void versionCrossMatching(byte[] rawData, String userId, boolean isAckSuccess) {
        if (isAckSuccess) return;

        String appVersionData = new String(rawData);
        Timber.tag("InAppUpdateTest").d("version rcv: " + appVersionData + " userId: " + userId);
        InAppUpdateModel versionModel = new Gson().fromJson(appVersionData, InAppUpdateModel.class);

        InAppUpdateModel myVersionModel = InAppUpdate.getInstance(TeleMeshApplication.getContext()).getAppVersion();

        InAppUpdate instance = InAppUpdate.getInstance(TeleMeshApplication.getContext());

        String myServerLink = instance.getMyLocalServerLink();
        Timber.tag("InAppUpdateTest").d("My version Code: %s", myVersionModel.getVersionCode());
        if (myVersionModel.getVersionCode() > versionModel.getVersionCode()) {

            if (myServerLink != null) {
                // start my server
                if (!instance.isServerRunning()) {
                    instance.prepareLocalServer();
                }

                InAppUpdateModel model = new InAppUpdateModel();
                model.setUpdateLink(myServerLink);
                String data = new Gson().toJson(model);
                dataSend(data.getBytes(), Constants.DataType.SERVER_LINK, userId, false);

                Timber.tag("InAppUpdateTest").d("My version is Big: ");
            }
        } else if (versionModel.getVersionCode() > myVersionModel.getVersionCode()) {
            if (versionModel.getUpdateType() == Constants.AppUpdateType.BLOCKER) {
                if (MainActivity.getInstance() != null) {
                    MainActivity.getInstance().openAppBlocker(versionModel.getVersionName());
                }
            }
        }
    }

    private void startAppUpdate(byte[] rawData, boolean isAckSuccess, String userId) {
        if (isAckSuccess) return;

        int userActiveStatus = rightMeshDataSource.getUserActiveStatus(userId);

        int userConnectivityStatus = getActiveStatus(userActiveStatus);

        if (userConnectivityStatus != Constants.UserStatus.WIFI_ONLINE) {
            return;
        }

        SharedPref sharedPref = SharedPref.getSharedPref(TeleMeshApplication.getContext());
        if (sharedPref.readBoolean(Constants.preferenceKey.ASK_ME_LATER)) {
            long saveTime = sharedPref.readLong(Constants.preferenceKey.ASK_ME_LATER_TIME);
            long days = (System.currentTimeMillis() - saveTime) / (24 * 60 * 60 * 1000);

            if (days <= 2) return;
        }

        String appVersionData = new String(rawData);
        InAppUpdateModel versionModel = new Gson().fromJson(appVersionData, InAppUpdateModel.class);

        //AppInstaller.downloadApkFile(versionModel.getUpdateLink(), MainActivity.getInstance());

        if (!InAppUpdate.getInstance(TeleMeshApplication.getContext()).isAppUpdating()) {
            //InAppUpdate.getInstance(TeleMeshApplication.getContext()).setAppUpdateProcess(true);
            if (MainActivity.getInstance() == null) return;
            InAppUpdate.getInstance(TeleMeshApplication.getContext()).checkForUpdate(MainActivity.getInstance(), versionModel.getUpdateLink());
        }

    }

    private void parseFeedbackText(byte[] rawData, boolean isAckSuccess) {
        if (isAckSuccess) return;
        String feedbackRawData = new String(rawData);
        FeedbackModel feedbackModel = new Gson().fromJson(feedbackRawData, FeedbackModel.class);

        AnalyticsDataHelper.getInstance().sendFeedbackToInternet(FeedbackEntity.toFeedbackEntity(feedbackModel), false);
    }

    private void deleteSentFeedback(byte[] rawData, boolean isAckSuccess) {
        if (isAckSuccess) return;
        String feedbackRawData = new String(rawData);
        FeedbackModel feedbackModel = new Gson().fromJson(feedbackRawData, FeedbackModel.class);
        FeedbackDataSource.getInstance().deleteFeedbackById(feedbackModel.getFeedbackId());
    }

    /////////////////////Broadcast config file/////////////////////////////

    // TODO update configuration process need to switch in service layer - mimo
    /*public void syncConfigFileAndBroadcast(boolean isUpdate, ConfigurationCommand configurationCommand) {
        if (isUpdate) {
            String configText = new Gson().toJson(configurationCommand);
            SharedPref.getSharedPref(TeleMeshApplication.getContext()).write(Constants.preferenceKey.CONFIG_VERSION_CODE, configurationCommand.getConfigVersionCode());
            SharedPref.getSharedPref(TeleMeshApplication.getContext()).write(Constants.preferenceKey.TOKEN_GUIDE_VERSION_CODE, configurationCommand.getTokenGuideVersion());
            SharedPref.getSharedPref(TeleMeshApplication.getContext()).write(Constants.preferenceKey.CONFIG_STATUS, configText);

            rightMeshDataSource.saveUpdateUserInfo();

            Log.v("MIMO_SAHA::", "Config version: " + configurationCommand.getConfigVersionCode());

            DataModel dataModel = new DataModel()
                    .setRawData(configText.getBytes())
                    .setDataType(Constants.DataType.CONFIG_UPDATE_INFO);

            compositeDisposable.add(Single.fromCallable(() -> UserDataSource.getInstance()
                    .getLocalWithBackConfigUsers(configurationCommand.getConfigVersionCode()))
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(users -> {

                        ExecutorService service = Executors.newSingleThreadExecutor();
                        service.execute(() -> rightMeshDataSource.DataSend(dataModel, users, false));

                        updateBackdatedConfigUsersVersion(configurationCommand.getConfigVersionCode());

                    }, Throwable::printStackTrace));
        }
    }

    private void updateBackdatedConfigUsersVersion(int version) {
        compositeDisposable.add(Single.fromCallable(() -> UserDataSource.getInstance()
                .updateBackConfigUsers(version))
                .subscribeOn(Schedulers.newThread())
                .subscribe(updatedUsersCount -> {
                    Timber.e("backdated user update %s", updatedUsersCount);
                }, Throwable::printStackTrace));
    }

    private void updateBroadcasterConfigVersion(int version, String userId) {
        compositeDisposable.add(Single.fromCallable(() -> UserDataSource.getInstance()
                .updateBroadcastUserConfigVersion(version, userId))
                .subscribeOn(Schedulers.newThread())
                .subscribe(updatedUsersCount -> {
                    Timber.e("broadcast user update %s", updatedUsersCount);
                }, Throwable::printStackTrace));
    }

    public void configFileSendToOthers(int versionCode, String userId) {

        int myConfigVersion = SharedPref.getSharedPref(TeleMeshApplication.getContext()).readInt(Constants.preferenceKey.CONFIG_VERSION_CODE);

        if (versionCode < myConfigVersion) {

            String configText = SharedPref.getSharedPref(TeleMeshApplication.getContext()).read(Constants.preferenceKey.CONFIG_STATUS);

            if (!TextUtils.isEmpty(configText)) {

                DataModel dataModel = new DataModel()
                        .setRawData(configText.getBytes())
                        .setDataType(Constants.DataType.CONFIG_UPDATE_INFO);

                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(() -> rightMeshDataSource.DataSend(dataModel, userId, false));
            }
        }
    }

    private void configFileReceiveFromOthers(byte[] rawData, boolean isNewMessage, String userId) {
        if (isNewMessage) {

            String configText = new String(rawData);
            ConfigurationCommand configurationCommand = new Gson().fromJson(configText, ConfigurationCommand.class);

            int myConfigVersion = SharedPref.getSharedPref(TeleMeshApplication.getContext()).readInt(Constants.preferenceKey.CONFIG_VERSION_CODE);

            if (myConfigVersion < configurationCommand.getConfigVersionCode()) {
                SharedPref.getSharedPref(TeleMeshApplication.getContext()).write(Constants.preferenceKey.CONFIG_VERSION_CODE, configurationCommand.getConfigVersionCode());
                SharedPref.getSharedPref(TeleMeshApplication.getContext()).write(Constants.preferenceKey.CONFIG_STATUS, configText);

                prepareRightMeshDataSource();

                rightMeshDataSource.sendConfigToViper(configurationCommand);

                rightMeshDataSource.saveUpdateUserInfo();

                updateBroadcasterConfigVersion(configurationCommand.getConfigVersionCode(), userId);
            }

            // check token guide version and request token_guide
            int myTokenGuideVersion = SharedPref.getSharedPref(TeleMeshApplication.getContext()).readInt(Constants.preferenceKey.TOKEN_GUIDE_VERSION_CODE);
            if (myTokenGuideVersion < configurationCommand.getTokenGuideVersion()) {
                // request toke guide info
                requestTokenGuideInfo(userId);
            }
        }
    }

    private void requestTokenGuideInfo(String userId) {
        TokenGuideRequestModel model = new TokenGuideRequestModel();
        model.setRequest("Request");
        String jsonText = new Gson().toJson(model);

        dataSend(jsonText.getBytes(), Constants.DataType.TOKEN_GUIDE_REQUEST, userId, false);
    }

    private void sendTokenGuideInfo(String userId, boolean isNewMessage) {
        if (!isNewMessage) return;

        PointGuideLine guideLine = rightMeshDataSource.requestTokenGuideline();

        if (guideLine != null) {
            String guidelineInfo = new Gson().toJson(guideLine);

            dataSend(guidelineInfo.getBytes(), Constants.DataType.TOKEN_GUIDE_INFO, userId, false);
        }
    }

    private void tokenGuidelineReceivedFromOther(byte[] rawData, boolean isNewMessage) {
        if (!isNewMessage) return;

        String configText = SharedPref.getSharedPref(TeleMeshApplication.getContext()).read(Constants.preferenceKey.CONFIG_STATUS);
        if (!TextUtils.isEmpty(configText)) {
            ConfigurationCommand configurationCommand = new Gson().fromJson(configText, ConfigurationCommand.class);
            SharedPref.getSharedPref(TeleMeshApplication.getContext()).write(Constants.preferenceKey.TOKEN_GUIDE_VERSION_CODE, configurationCommand.getTokenGuideVersion());
        }

        String guidelineInfo = new String(rawData);
        rightMeshDataSource.sendTokenGuidelineInfoToViper(guidelineInfo);
    }*/


}
