package com.w3engineers.unicef.telemesh.data.local.messagetable;

import androidx.annotation.NonNull;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class MessageSourceData {

    private static MessageSourceData messageSourceData/* = new MessageSourceData()*/;
    private MessageDao messageDao;

//    public MessageSourceData() {
//        messageDao = AppDatabase.getInstance().messageDao();
//    }

    /**
     * This constructor is restricted and only used in unit test class
     *
     * @param messageDao -> provide dao from unit test class
     */
    public MessageSourceData(@NonNull MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @NonNull
    public static MessageSourceData getInstance() {
        if (messageSourceData == null) {
            messageSourceData = getInstance(AppDatabase.getInstance().messageDao());
        }
        return messageSourceData;
    }

    /**
     * This constructor is restricted and only used in unit test class
     *
     * @param messageDao -> provide dao from unit test class
     */
    @NonNull
    public static MessageSourceData getInstance(@NonNull MessageDao messageDao) {
        if (messageSourceData == null) {
            messageSourceData = new MessageSourceData(messageDao);
        }
        return messageSourceData;
    }

    @NonNull
    public Flowable<ChatEntity> getLastData() {
        return messageDao.getLastInsertedMessage().flatMap(messageEntity ->
                Flowable.just((ChatEntity) messageEntity));
    }

    public long insertOrUpdateData(@NonNull ChatEntity baseEntity) {
        return messageDao.writeMessage((MessageEntity) baseEntity);
    }

    // This api is not used in app layer
    /*public void deleteAllData() {
        messageDao.deleteAllUsers();
    }*/

    @NonNull
    public Flowable<List<ChatEntity>> getAllMessages(@NonNull String friendsId) {

        return messageDao.getAllMessages(friendsId).flatMap(messageEntities ->
                Flowable.just(new ArrayList<>(messageEntities)));
    }

    @NonNull
    public ChatEntity getMessageEntityById(@NonNull String messageId) {
        return messageDao.getMessageById(messageId);
    }

    // This api is not used in app layer
    /*public long updateMessageEntityStatus(String messageId, int messageStatus) {
        return messageDao.updateMessageStatus(messageId, messageStatus);
    }*/
    public long updateUnreadToRead(@NonNull String friendsId) {
        return messageDao.updateMessageAsRead(friendsId);
    }

    public long changeMessageStatusFrom(int fromStatus, int toStatus) {
        return messageDao.changeMessageStatusFrom(fromStatus, toStatus);
    }

    public Flowable<Integer> getBlockMessageInfoForSync() {
        return messageDao.getBlockMessageInfoForSync();
    }

    // This api is not used in app layer
    /*public Boolean hasChatEntityExist(String friendsId, String messageId) {
        return messageDao.hasChatEntityExist(friendsId, messageId);
    }*/
}
