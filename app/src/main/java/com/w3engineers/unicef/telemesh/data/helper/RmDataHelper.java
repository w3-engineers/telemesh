package com.w3engineers.unicef.telemesh.data.helper;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.ext.viper.application.data.local.service.MeshService;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.mesh.wifi.protocol.Link;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsDataHelper;
import com.w3engineers.unicef.telemesh.data.broadcast.BroadcastManager;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountDataService;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.ShareCountModel;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinDataSource;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinTrackEntity;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.AckCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinFeed;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.feed.GeoLocation;
import com.w3engineers.unicef.telemesh.data.local.feed.Payload;
import com.w3engineers.unicef.telemesh.data.local.meshlog.MeshLogDataSource;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageCount;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.util.helper.LocationUtil;
import com.w3engineers.unicef.util.helper.LogProcessUtil;
import com.w3engineers.unicef.util.helper.NotifyUtil;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public native String getBroadCastUrl();

    public native String getBroadcastToken();


    private static RmDataHelper rmDataHelper = new RmDataHelper();
    private MeshDataSource rightMeshDataSource;

    private DataSource dataSource;

    @SuppressLint("UseSparseArrays")
    @NonNull
    public HashMap<String, DataModel> rmDataMap = new HashMap<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private RmDataHelper() {
        BroadcastManager.getInstance().setBroadcastSendCallback(this);
    }

    @NonNull
    public static RmDataHelper getInstance() {
        return rmDataHelper;
    }

    @NonNull
    public MeshDataSource initRM(@NonNull DataSource dataSource) {

        this.dataSource = dataSource;
        rightMeshDataSource = MeshDataSource.getRmDataSource();

        return rightMeshDataSource;
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

        UserEntity userEntity = new UserEntity()
                .toUserEntity(userModel)
                .setOnlineStatus(userConnectivityStatus);
        UserDataSource.getInstance().insertOrUpdateData(userEntity);

        syncUserWithBroadcastMessage(userId);
    }

    public void onlyNodeAdd(String nodeId) {
        int userActiveStatus = rightMeshDataSource.getUserActiveStatus(nodeId);

        int userConnectivityStatus = getActiveStatus(userActiveStatus);

        UserEntity userEntity = new UserEntity().setUserName("").setAvatarIndex(-1).setMeshId(nodeId).setOnlineStatus(userConnectivityStatus);

        UserDataSource.getInstance().insertOrUpdateData(userEntity);

    }

    public boolean userExistedOperation(String userId, int userActiveStatus) {

        int userConnectivityStatus = getActiveStatus(userActiveStatus);

        Log.v("MIMO_SAHA:", "Status: " + userConnectivityStatus);

        int updateId = UserDataSource.getInstance()
                .updateUserStatus(userId, userConnectivityStatus);

        if (updateId > 0 && (userConnectivityStatus == Constants.UserStatus.WIFI_ONLINE
                || userConnectivityStatus == Constants.UserStatus.BLE_ONLINE)) {
            syncUserWithBroadcastMessage(userId);
        }

        return updateId > 0;
    }

    public int getActiveStatus(int userActiveStatus) {

        if (userActiveStatus == Link.Type.WIFI.getValue()) {
            return Constants.UserStatus.WIFI_ONLINE;
        } else if (userActiveStatus == Link.Type.WIFI_MESH.getValue()) {
            return Constants.UserStatus.WIFI_MESH_ONLINE;
        } else if (userActiveStatus == Link.Type.BT.getValue()) {
            return Constants.UserStatus.BLE_ONLINE;
        } else if (userActiveStatus == Link.Type.BT_MESH.getValue()) {
            return Constants.UserStatus.BLE_MESH_ONLINE;
        } else if (userActiveStatus == Link.Type.INTERNET.getValue()) {
            return Constants.UserStatus.INTERNET_ONLINE;
        } else {
            return Constants.UserStatus.OFFLINE;
        }
    }

    /**
     * This api is responsible for update user info in database
     * when users is gone in mesh network
     *
     * @param meshPeer -> contains peer id
     */

    public void userLeave(@NonNull MeshPeer meshPeer) {

        String userId = meshPeer.getPeerId();
        UserDataSource.getInstance().updateUserStatus(userId, Constants.UserStatus.OFFLINE);
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

        compositeDisposable.add(dataSource.getLastChatData()
                .subscribeOn(Schedulers.io())
                .subscribe(chatEntity -> {

                    if (!chatEntity.isIncoming()
                            && chatEntity.getStatus() == Constants.MessageStatus.STATUS_SENDING) {

                        MessageEntity messageEntity = (MessageEntity) chatEntity;
                        String messageModelString = new Gson().toJson(messageEntity.toMessageModel());

                        dataSend(messageModelString.getBytes(),
                                Constants.DataType.MESSAGE, chatEntity.getFriendsId());
                    }
                }, Throwable::printStackTrace));

        compositeDisposable.add(Objects.requireNonNull(dataSource.getReSendMessage())
                .subscribeOn(Schedulers.newThread())
                .subscribe(chatEntity -> {

                    if (!chatEntity.isIncoming()
                            && chatEntity.getStatus() == Constants.MessageStatus.STATUS_SENDING) {

                        MessageEntity messageEntity = (MessageEntity) chatEntity;
                        String messageModelString = new Gson().toJson(messageEntity.toMessageModel());

                        dataSend(messageModelString.getBytes(),
                                Constants.DataType.MESSAGE, chatEntity.getFriendsId());
                    }
                }, Throwable::printStackTrace));
    }

    /**
     * This api is responsible for creating a data model and send data to RM
     *
     * @param data -> raw data
     * @param type -> data type
     */
    private void dataSend(@NonNull byte[] data, byte type, String userId) {

        DataModel rmDataModel = new DataModel()
                .setRawData(data)
                .setDataType(type);

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> rightMeshDataSource.DataSend(rmDataModel, userId));
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

        switch (dataType) {
            case Constants.DataType.MESSAGE:
                setChatMessage(rawData, userId, isNewMessage, isAckSuccess);
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
        }
    }

    private void setChatMessage(byte[] rawChatData, String userId, boolean isNewMessage, boolean isAckSuccess) {
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
                chatEntity.setStatus(isAckSuccess ? Constants.MessageStatus.STATUS_DELIVERED
                        : Constants.MessageStatus.STATUS_FAILED).setIncoming(false);
                Timber.e("Delivered :: %s", chatEntity.getMessageId());
                dataSource.updateMessageStatus(chatEntity.getMessageId(), chatEntity.getStatus());
            }


        } catch (Exception e) {
            Log.v("MIMO_SAHA:", "Error message: " + e.getMessage());
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
                                Log.d("AppShareCount", "Data saved");
                            }
                        }, Throwable::printStackTrace));


            } else {
                compositeDisposable.add(Single.fromCallable(() -> AppShareCountDataService.getInstance()
                        .updateSentShareCount(entity.getUserId(), entity.getDate()))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(longResult -> {
                            if (longResult > 1) {
                                Log.d("AppShareCount", "Data Deleted");
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
    public void ackReceive(@NonNull DataModel dataModel) {

        String dataSendId = dataModel.getDataTransferId();

        if (dataSendId != null && rmDataMap.get(dataSendId) != null) {

            DataModel prevRMDataModel = rmDataMap.get(dataSendId);
            if (prevRMDataModel != null) {

                prevRMDataModel.setAckSuccess(dataModel.isAckSuccess());

                dataReceive(prevRMDataModel, false);
                rmDataMap.remove(dataSendId);
            }
        }
    }

    public void stopMeshService() {
        updateUserStatus(true);
    }

    private void updateUserStatus(boolean isServiceStop) {
        compositeDisposable.add(updateUserToOffline()
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    if (isServiceStop) {
                        stopMeshProcess();
                    }
                }, Throwable::printStackTrace));
    }

    private Single<Integer> updateUserToOffline() {
        return Single.fromCallable(() ->
                UserDataSource.getInstance().updateUserToOffline());
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
        rightMeshDataSource.stopMeshProcess();
    }

    public void resetUserToOfflineBasedOnService() {
        boolean isServiceEnable = isMeshServiceRunning();
        Log.v("MIMO_SAHA:", "PP: " + isServiceEnable);
        if (!isServiceEnable) {
            updateUserStatus(false);
        }
    }

    public boolean isMeshServiceRunning() {
        Context context = TeleMeshApplication.getContext();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MeshService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * For ReInitiating RM service need to reset rightmesh data source instance
     */
    public void restartMesh() {
        rightMeshDataSource.resetMeshService();
    }

    public void requestWsMessage() {
        LocationUtil.getInstance().init(TeleMeshApplication.getContext()).getLocation().addLocationListener(new LocationUtil.LocationRequestCallback() {
            @Override
            public void onGetLocation(String lat, String lang) {

                LocationUtil.getInstance().removeListener();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(BuildConfig.BROADCAST_URL).build();
                BroadcastWebSocket listener = new BroadcastWebSocket();
                listener.setBroadcastCommand(getBroadcastCommand(lat, lang));
                client.newWebSocket(request, listener);
                client.dispatcher().executorService().shutdown();
            }
        });

    }

    private String getMyMeshId() {
        return SharedPref.getSharedPref(TeleMeshApplication.getContext()).read(Constants.preferenceKey.MY_USER_ID);
    }

    private void requestAckMessage(String messageId) {
        requestAckMessage(messageId, getMyMeshId());
    }

    private void requestAckMessage(String messageId, String userId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(BuildConfig.BROADCAST_URL).build();
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
        if (rightMeshDataSource == null) {
            rightMeshDataSource = MeshDataSource.getRmDataSource();
        }
        service.execute(() -> rightMeshDataSource.DataSend(rmDataModel, meshDataList));
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
                dataSend(bulletinString.getBytes(), Constants.DataType.MESSAGE_FEED, userId);
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

    private BroadcastCommand getBroadcastCommand(String lat, String lang) {
        Payload payload = new Payload();

        GeoLocation geoLocation = new GeoLocation()
                .setLatitude(lat).setLongitude(lang);

        payload.setGeoLocation(geoLocation);
        payload.setConnectedClients("2");

        return new BroadcastCommand().setEvent("connect")
                .setToken(BuildConfig.BROADCAST_TOKEN)
                .setBaseStationId(getMyMeshId())
                .setClientId(getMyMeshId())
                .setPayload(payload);
    }

    private BroadcastCommand getAckCommand(String messageId, String userId) {
        Payload payload = new Payload().setMessageId(messageId);
        return new BroadcastCommand().setEvent("ack_msg_received")
                .setToken(BuildConfig.BROADCAST_TOKEN)
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

        for (String sellersId : rightMeshDataSource.getAllSellers()) {
            dataSend(messageCountString.getBytes(), Constants.DataType.MESSAGE_COUNT, sellersId);
        }

    }

    public void sendAppShareCount() {
        if (AnalyticsDataHelper.getInstance().isMobileDataEnable()) {
            sendAppShareCountAnalytics();
        } else {
            compositeDisposable.add(AppShareCountDataService.getInstance()
                    .getTodayAppShareCount(TimeUtil.getDateString(System.currentTimeMillis()))
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(this::sendAppShareCountToSellers, Throwable::printStackTrace));
        }
    }

    /**
     * Send data to local user (Seller)
     */
    public void sendAppShareCountToSellers(List<AppShareCountEntity> entityList) {
        for (AppShareCountEntity entity : entityList) {
            ShareCountModel appShareCount = entity.toAnalyticAppShareCount();
            String shareCountString = new Gson().toJson(appShareCount);
            if (rightMeshDataSource == null) {
                rightMeshDataSource = MeshDataSource.getRmDataSource();
            }
            for (String sellersId : rightMeshDataSource.getAllSellers()) {
                dataSend(shareCountString.getBytes(), Constants.DataType.APP_SHARE_COUNT, sellersId);
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

    public void uploadLogFile() {
        compositeDisposable.add(Single.fromCallable(() ->
                MeshLogDataSource.getInstance().getAllUploadedLogList())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::uploadLogFile, Throwable::printStackTrace));
    }

    private void uploadLogFile(List<String> previousList) {
        if (previousList == null) {
            previousList = new ArrayList<>();
        }

        Log.d("ParseFileUpload", "Upload file call");
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() +
                "/MeshRnD");
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!previousList.contains(file.getName())) {
                    AnalyticsDataHelper.getInstance().sendLogFileInServer(file, TextUtils.isEmpty(getMyMeshId()) ? "Test User" : getMyMeshId(), Constants.getDeviceName());
                }
            }
        }
    }

    @Override
    public void dataSent(@NonNull DataModel rmDataModel, String dataSendId) {
        rmDataMap.put(dataSendId, rmDataModel);
    }

    public void showMeshLog(String log) {
        // LogProcessUtil.getInstance().writeLog(log);
    }
}
