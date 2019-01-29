package com.w3engineers.unicef.telemesh.data.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.TeleMeshChatOuterClass.*;
import com.w3engineers.unicef.telemesh.TeleMeshUser.*;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.NotifyUtil;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.util.HashMap;
import io.reactivex.schedulers.Schedulers;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [22-Oct-2018 at 6:33 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [22-Oct-2018 at 6:33 PM].
 * * --> <Second Editor> on [22-Oct-2018 at 6:33 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [22-Oct-2018 at 6:33 PM].
 * * --> <Second Reviewer> on [22-Oct-2018 at 6:33 PM].
 * * ============================================================================
 **/
public class RmDataHelper {

    private static RmDataHelper rmDataHelper = new RmDataHelper();
    private RightMeshDataSource rightMeshDataSource;

    private DataSource dataSource;

    private HashMap<String, RMUserModel> rmUserMap;
    private HashMap<Integer, RMDataModel> rmDataMap;

    private RmDataHelper() {
        rmDataMap = new HashMap<>();
        rmUserMap = new HashMap<>();
    }

    public static RmDataHelper getInstance() {
        return rmDataHelper;
    }

    public RightMeshDataSource initRM(DataSource dataSource) {
        Context context = TeleMeshApplication.getContext();

        this.dataSource = dataSource;

       /* RMUserModel rmUserModel = RMUserModel.newBuilder()
                .setUserFirstName(SharedPref.getSharedPref(context).read(Constants.preferenceKey.FIRST_NAME))
                .setUserLastName(SharedPref.getSharedPref(context).read(Constants.preferenceKey.LAST_NAME))
                .setImageIndex(SharedPref.getSharedPref(context).readInt(Constants.preferenceKey.IMAGE_INDEX))
                .build();*/

        rightMeshDataSource = RightMeshDataSource.getRmDataSource();

        return rightMeshDataSource;
    }

    /**
     * This api is responsible for insert users in database when users is added
     *
     * @param rmUserModel -> contains all of info about users
     */

    public long userAdd(RMUserModel rmUserModel) {
        String userId = rmUserModel.getUserId();
        if (rmUserMap.containsKey(userId))
            return -1L;

        rmUserMap.put(userId, rmUserModel);

        UserEntity userEntity = new UserEntity()
                .toUserEntity(rmUserModel)
                .setOnline(true);
        return UserDataSource.getInstance().insertOrUpdateData(userEntity);
    }

    /**
     * This api is responsible for update user info in database
     * when users is gone in mesh network
     *
     * @param meshPeer -> contains peer id
     */

    public long userLeave(MeshPeer meshPeer) {
        String userId = meshPeer.getPeerId();

        if (rmUserMap.containsKey(userId)) {

            RMUserModel rmUserModel = rmUserMap.get(userId);
            rmUserMap.remove(userId);

            UserEntity userEntity = new UserEntity()
                    .toUserEntity(rmUserModel)
                    .setLastOnlineTime(System.currentTimeMillis())
                    .setOnline(false);

            return UserDataSource.getInstance().insertOrUpdateData(userEntity);
        }
        return -1L;
    }

    @SuppressLint("CheckResult")
    /**
     * after inserting the message to the db
     * here we will fetch the last inserted message that will be
     * sent via RM.
     *
     * Only for outgoing message this method will be responsible
     */
    public void prepareDataObserver() {
        dataSource.getLastChatData()
                .subscribeOn(Schedulers.io())
                .subscribe(chatEntity -> {

                    if (!chatEntity.isIncoming()
                            && chatEntity.getStatus() == Constants.MessageStatus.STATUS_SENDING) {

                        MessageEntity messageEntity = (MessageEntity) chatEntity;
                        dataSend(messageEntity.toProtoChat().toByteArray(),
                                Constants.DataType.MESSAGE, chatEntity.getFriendsId());
                    }

                });
    }

    /**
     * This api is responsible for creating a data model and send data to RM
     *
     * @param data -> raw data
     * @param type -> data type
     */
    private void dataSend(byte[] data, byte type, String userId) {

        RMDataModel rmDataModel = RMDataModel.newBuilder()
                .setRawData(ByteString.copyFrom(data))
                .setUserMeshId(userId)
                .setDataType(type).build();

        int dataSendId = rightMeshDataSource.DataSend(rmDataModel);

        rmDataMap.put(dataSendId, rmDataModel);
    }

    /**
     * During receive any data to from RM this API is manipulating data based on application
     *
     * @param rmDataModel -> contains all of info about receive data
     */

    public long dataReceive(RMDataModel rmDataModel, boolean isNewMessage) {

        int dataType = rmDataModel.getDataType();
        byte[] rawData = rmDataModel.getRawData().toByteArray();
        String userId = rmDataModel.getUserMeshId();

        switch (dataType) {
            case Constants.DataType.USER:
                break;

            case Constants.DataType.MESSAGE:
                return setChatMessage(rawData, userId, isNewMessage);

            case Constants.DataType.SURVEY:
                // TODO include survey data operation module. i.e. DB operation and process and return a single insertion observer
                break;

            case Constants.DataType.MESSAGE_FEED:
                // TODO include feed data operation module. i.e. DB operation and return a single insertion observer
                break;
        }
        return -1L;
    }

    private long setChatMessage(byte[] rawChatData, String userId, boolean isNewMessage) {
        try {
            TeleMeshChat teleMeshChat = TeleMeshChat.newBuilder()
                    .mergeFrom(rawChatData).build();

            ChatEntity chatEntity = new MessageEntity()
                    .toChatEntity(teleMeshChat)
                    .setFriendsId(userId)
                    .setIncoming(true);

            if (isNewMessage) {
                chatEntity.setStatus(Constants.MessageStatus.STATUS_READ).setIncoming(true);
                //prepareDateSeparator(chatEntity);

                if (TextUtils.isEmpty(dataSource.getCurrentUser())
                        || !userId.equals(dataSource.getCurrentUser())) {
                    NotifyUtil.showNotification(chatEntity);
                    chatEntity.setStatus(Constants.MessageStatus.STATUS_UNREAD);
                }

            } else {
                chatEntity.setStatus(Constants.MessageStatus.STATUS_DELIVERED).setIncoming(false);
            }

            return dataSource.insertOrUpdateData(chatEntity);

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return -1L;
    }

    private void prepareDateSeparator(ChatEntity chatEntity) {

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
    }


    /**
     * When we got any ack message from RM this API is responsible
     * for updating med message status which already sent
     *
     * @param rmDataModel -> Contains received message id
     */
    public long ackReceive(RMDataModel rmDataModel) {

        int dataSendId = rmDataModel.getRecDataId();

        if (rmDataMap.containsKey(dataSendId)) {

            RMDataModel prevRMDataModel = rmDataMap.get(dataSendId);

            long ackDataUpdate = dataReceive(prevRMDataModel, false);

            rmDataMap.remove(dataSendId);
            return ackDataUpdate;
        }
        return -1L;
    }
}
