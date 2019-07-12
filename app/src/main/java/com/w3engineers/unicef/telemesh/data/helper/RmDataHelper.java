package com.w3engineers.unicef.telemesh.data.helper;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.TeleMeshAnalyticsOuterClass.MessageCount;
import com.w3engineers.unicef.telemesh.TeleMeshBulletinOuterClass.TeleMeshBulletin;
import com.w3engineers.unicef.telemesh.TeleMeshChatOuterClass.TeleMeshChat;
import com.w3engineers.unicef.telemesh.TeleMeshUser.RMDataModel;
import com.w3engineers.unicef.telemesh.TeleMeshUser.RMUserModel;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsDataHelper;
import com.w3engineers.unicef.telemesh.data.broadcast.BroadcastManager;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinDataSource;
import com.w3engineers.unicef.telemesh.data.local.bulletintrack.BulletinTrackEntity;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.AckCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinFeed;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.feed.Payload;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.NotifyUtil;
import com.w3engineers.unicef.util.helper.TimeUtil;

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

    private HashMap<String, RMUserModel> rmUserMap;

    @SuppressLint("UseSparseArrays")
    @NonNull
    public HashMap<Long, RMDataModel> rmDataMap = new HashMap<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private RmDataHelper() {
        rmUserMap = new HashMap<>();
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
     * @param rmUserModel -> contains all of info about users
     */

    public void userAdd(@NonNull RMUserModel rmUserModel) {

        String userId = rmUserModel.getUserId();
        if (rmUserMap.containsKey(userId))
            return;

        rmUserMap.put(userId, rmUserModel);

        UserEntity userEntity = new UserEntity()
                .toUserEntity(rmUserModel)
                .setOnline(true);
        UserDataSource.getInstance().insertOrUpdateData(userEntity);

        syncUserWithBroadcastMessage(userId);
    }

    /**
     * This api is responsible for update user info in database
     * when users is gone in mesh network
     *
     * @param meshPeer -> contains peer id
     */

    public void userLeave(@NonNull MeshPeer meshPeer) {

        String userId = meshPeer.getPeerId();

        if (rmUserMap.containsKey(userId)) {

            RMUserModel rmUserModel = rmUserMap.get(userId);
            rmUserMap.remove(userId);

            if (rmUserModel != null) {
                UserEntity userEntity = new UserEntity()
                        .toUserEntity(rmUserModel)
                        .setLastOnlineTime(TimeUtil.toCurrentTime())
                        .setOnline(false);

                UserDataSource.getInstance().insertOrUpdateData(userEntity);
            }
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

        compositeDisposable.add(dataSource.getLastChatData()
                .subscribeOn(Schedulers.io())
                .subscribe(chatEntity -> {

                    if (!chatEntity.isIncoming()
                            && chatEntity.getStatus() == Constants.MessageStatus.STATUS_SENDING) {

                        MessageEntity messageEntity = (MessageEntity) chatEntity;
                        dataSend(messageEntity.toProtoChat().toByteArray(),
                                Constants.DataType.MESSAGE, chatEntity.getFriendsId());
                    }
                }, Throwable::printStackTrace));

        compositeDisposable.add(Objects.requireNonNull(dataSource.getReSendMessage())
                .subscribeOn(Schedulers.newThread())
                .subscribe(chatEntity -> {

                    if (!chatEntity.isIncoming()
                            && chatEntity.getStatus() == Constants.MessageStatus.STATUS_SENDING) {

                        MessageEntity messageEntity = (MessageEntity) chatEntity;
                        dataSend(messageEntity.toProtoChat().toByteArray(),
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

        RMDataModel.Builder rmDataModel = RMDataModel.newBuilder()
                .setRawData(ByteString.copyFrom(data))
                .setDataType(type);

        Log.v("MIMO_SAHA:", "User Id: " + userId);

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> rightMeshDataSource.DataSend(rmDataModel, userId));
    }

    /**
     * During receive any data to from RM this API is manipulating data based on application
     *
     * @param rmDataModel -> contains all of info about receive data
     */

    public void dataReceive(@NonNull RMDataModel rmDataModel, boolean isNewMessage) {

        int dataType = rmDataModel.getDataType();
        byte[] rawData = rmDataModel.getRawData().toByteArray();
        String userId = rmDataModel.getUserMeshId();
        boolean isAckSuccess = rmDataModel.getIsAckSuccess();

        Log.v("MIMO_SAHA::", "Type: " + dataType);

        switch (dataType) {
            case Constants.DataType.USER:
                break;

            case Constants.DataType.MESSAGE:
                setChatMessage(rawData, userId, isNewMessage, isAckSuccess);
                break;

            case Constants.DataType.MESSAGE_FEED:
                setBulletinMessage(rawData, userId, isNewMessage, isAckSuccess);
                break;

            case Constants.DataType.MESSAGE_COUNT:
                setAnalyticsMessageCount(rawData, isAckSuccess);
                break;
        }
    }

    private void setChatMessage(byte[] rawChatData, String userId, boolean isNewMessage, boolean isAckSuccess) {
        try {
            TeleMeshChat teleMeshChat = TeleMeshChat.newBuilder()
                    .mergeFrom(rawChatData).build();

            ChatEntity chatEntity = new MessageEntity()
                    .toChatEntity(teleMeshChat)
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
            e.printStackTrace();
        }
    }

    private void setBulletinMessage(byte[] rawBulletinData, String userId, boolean isNewMessage, boolean isAckSuccess) {
        try {

            TeleMeshBulletin teleMeshBulletin = TeleMeshBulletin.newBuilder()
                    .mergeFrom(rawBulletinData).build();

            FeedEntity feedEntity = new FeedEntity()
                    .toFeedEntity(teleMeshBulletin);

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
                MessageCount messageCount = MessageCount.newBuilder()
                        .mergeFrom(rawMessageCountAnalyticsData).build();

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
     * @param rmDataModel -> Contains received message id
     */
    public void ackReceive(@NonNull RMDataModel rmDataModel) {

        long dataSendId = rmDataModel.getRecDataId();

        if (rmDataMap.get(dataSendId) != null) {

            RMDataModel prevRMDataModel = rmDataMap.get(dataSendId);
            if (prevRMDataModel != null) {

                RMDataModel.Builder newRmDataModel = prevRMDataModel.toBuilder();
                newRmDataModel.setIsAckSuccess(rmDataModel.getIsAckSuccess());

                dataReceive(newRmDataModel.build(), false);
                rmDataMap.remove(dataSendId);
            }
        }
    }

    public void stopMeshService() {
        updateUserStatus();
    }

    private void updateUserStatus() {
        compositeDisposable.add(updateUserToOffline()
                .subscribeOn(Schedulers.newThread()).subscribe(integer -> {
                    makeSendingMessageAsFailed();
                }, Throwable::printStackTrace));
    }

    private Single<Integer> updateUserToOffline() {
        return Single.fromCallable(() ->
                UserDataSource.getInstance().updateUserToOffline());
    }

    public void makeSendingMessageAsFailed() {

        compositeDisposable.add(updateMessageStatus()
                .subscribeOn(Schedulers.io()).subscribe(aLong -> {
                    stopMeshProcess();
                }, Throwable::printStackTrace));
    }

    private Single<Long> updateMessageStatus() {
        return Single.fromCallable(() -> MessageSourceData.getInstance()
                .changeMessageStatusFrom(Constants.MessageStatus.STATUS_SENDING,
                        Constants.MessageStatus.STATUS_FAILED));
    }

    /**
     * Concern for this api stopping RM service from app layer
     */
    public void stopRmService() {
        rightMeshDataSource.stopMeshService();
    }

    private void stopMeshProcess() {
        rightMeshDataSource.stopMeshProcess();
    }

    /**
     * For ReInitiating RM service need to reset rightmesh data source instance
     */
    public void restartMesh() {
        rightMeshDataSource.resetMeshService();
    }

    public void requestWsMessage() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(BuildConfig.BROADCAST_URL).build();
        BroadcastWebSocket listener = new BroadcastWebSocket();
        listener.setBroadcastCommand(getBroadcastCommand());
        client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
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

        RMDataModel.Builder rmDataModel = RMDataModel.newBuilder()
                .setRawData(ByteString.copyFrom(feedEntity.toTelemeshBulletin().toByteArray()))
                .setDataType(Constants.DataType.MESSAGE_FEED);

        ExecutorService service = Executors.newSingleThreadExecutor();
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

                dataSend(feedEntity.toTelemeshBulletin().toByteArray(), Constants.DataType.MESSAGE_FEED, userId);
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

    private BroadcastCommand getBroadcastCommand() {
        Payload payload = new Payload();
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

    public void analyticsDataSendToSellers(MessageEntity.MessageAnalyticsEntity messageAnalyticsEntity) {

        MessageCount messageCount = messageAnalyticsEntity.toAnalyticMessageCount();

        for (String sellersId : rightMeshDataSource.getAllSellers()) {
            dataSend(messageCount.toByteArray(), Constants.DataType.MESSAGE_COUNT, sellersId);
        }

    }

    @Override
    public void dataSent(@NonNull RMDataModel rmDataModel, long dataSendId) {
        rmDataMap.put(dataSendId, rmDataModel);
    }
}
