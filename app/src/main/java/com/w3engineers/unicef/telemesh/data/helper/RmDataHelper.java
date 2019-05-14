package com.w3engineers.unicef.telemesh.data.helper;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.protobuf.ByteString;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.unicef.telemesh.TeleMeshChatOuterClass.TeleMeshChat;
import com.w3engineers.unicef.telemesh.TeleMeshUser.RMDataModel;
import com.w3engineers.unicef.telemesh.TeleMeshUser.RMUserModel;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.NotifyUtil;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.util.HashMap;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class RmDataHelper {

    private static RmDataHelper rmDataHelper = new RmDataHelper();
    private MeshDataSource rightMeshDataSource;

    private DataSource dataSource;

    private HashMap<String, RMUserModel> rmUserMap;
    @NonNull
    public HashMap<Long, RMDataModel> rmDataMap = new HashMap<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private RmDataHelper() {
        rmUserMap = new HashMap<>();
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
     *
     * Only for outgoing message this method will be responsible
     */
    @SuppressLint("CheckResult")
    public void prepareDataObserver() {
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
    }

    /**
     * This api is responsible for creating a data model and send data to RM
     *
     * @param data -> raw data
     * @param type -> data type
     */
    private void dataSend(@NonNull byte[] data, byte type, String userId) {

        RMDataModel rmDataModel = RMDataModel.newBuilder()
                .setRawData(ByteString.copyFrom(data))
                .setUserMeshId(userId)
                .setDataType(type).build();

        long dataSendId = rightMeshDataSource.DataSend(rmDataModel);

        rmDataMap.put(dataSendId, rmDataModel);
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

        switch (dataType) {
            case Constants.DataType.USER:
                break;

            case Constants.DataType.MESSAGE:
                setChatMessage(rawData, userId, isNewMessage);
                break;

            case Constants.DataType.SURVEY:
                // TODO include survey data operation module. i.e. DB operation and process and return a single insertion observer
                break;

            case Constants.DataType.MESSAGE_FEED:
                // TODO include feed data operation module. i.e. DB operation and return a single insertion observer
                break;
        }
    }

    private void setChatMessage(byte[] rawChatData, String userId, boolean isNewMessage) {
        try {
            TeleMeshChat teleMeshChat = TeleMeshChat.newBuilder()
                    .mergeFrom(rawChatData).build();

            ChatEntity chatEntity = new MessageEntity()
                    .toChatEntity(teleMeshChat)
                    .setFriendsId(userId)
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
                chatEntity.setStatus(Constants.MessageStatus.STATUS_DELIVERED).setIncoming(false);
                Timber.e("Delivered :: %s", chatEntity.getMessageId());
                dataSource.updateMessageStatus(chatEntity.getMessageId(), chatEntity.getStatus());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void prepareDateSeparator(ChatEntity chatEntity) {

        String dateFormat = TimeUtil.getDayMonthYear(chatEntity.getTime());

        boolean dateEntity = dataSource.getMessage(chatEntity.getFriendsId(), dateFormat);

        if (!dateEntity) {

            ChatEntity separatorMessage = new MessageEntity().setMessage(dateFormat)
                    .setTime(chatEntity.getTime())
                    .setMessageType(Constants.MessageType.DATE_MESSAGE)
                    .setFriendsId(chatEntity.getFriendsId())
                    .setMessageId(dateFormat);

            dataSource.insertOrUpdateData(separatorMessage);
        }
    }*/


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
                dataReceive(prevRMDataModel, false);
                rmDataMap.remove(dataSendId);
            }
        }
    }

    /**
     * Concern for this api stopping RM service from app layer
     */
    public void stopRmService() {
//        rightMeshDataSource.stopMesh();
    }

    /**
     * For ReInitiating RM service need to reset rightmesh data source instance
     */
    public void resetRmDataSourceInstance() {
        rightMeshDataSource.resetInstance();
    }
}
