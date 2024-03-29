package com.w3engineers.unicef.telemesh.data.local.messagetable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
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
    private GroupMessageDao groupMessageDao;
    private GroupContentDao groupContentDao;

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
        this.groupMessageDao = AppDatabase.getInstance().getGroupMessageDao();
        this.groupContentDao = AppDatabase.getInstance().getGroupContentDao();
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

    public Flowable<GroupMessageEntity> getLastGroupMessage() {
        return groupMessageDao.getLastInsertedMessage().flatMap(groupMessage -> Flowable.just(groupMessage));
    }

    public long insertOrUpdateData(@NonNull ChatEntity baseEntity) {
        if (baseEntity instanceof MessageEntity) {
            return messageDao.writeMessage((MessageEntity) baseEntity);
        } else {
            return groupMessageDao.writeMessage((GroupMessageEntity) baseEntity);
        }
    }

    public long addOrUpdateContent(GroupContentEntity entity){
        return groupContentDao.insertOrUpdate(entity);
    }

    public GroupContentEntity getContentById(String contentId){
        return groupContentDao.getContentById(contentId);
    }

    /**
     * This method only for test cases
     * @param threadId
     * @return
     */
    public MessageEntity getLastIncomingContent(String threadId) {
        return messageDao.getLastIncomingContent(threadId);
    }

    // This api is not used in app layer
    /*public void deleteAllData() {
        messageDao.deleteAllUsers();
    }*/

    @NonNull
    public Flowable<List<ChatEntity>> getAllMessages(@NonNull String friendsId) {

        return messageDao.getAllMessages(friendsId, Constants.MessagePlace.MESSAGE_PLACE_P2P).flatMap(messageEntities ->
                Flowable.just(new ArrayList<>(messageEntities)));
    }

    @NonNull
    public Flowable<List<ChatEntity>> getAllGroupMessages(@NonNull String friendsId) {

        return groupMessageDao.getGroupAllMessages(friendsId)
                .flatMap(messageEntities ->
                        Flowable.just(new ArrayList<>(messageEntities)));
    }

    @NonNull
    public MessageEntity getCreateGroupInfo(@NonNull String friendsId) {
        return messageDao.getCreateGroupInfo(friendsId, Constants.MessagePlace.MESSAGE_PLACE_GROUP);
    }

    @NonNull
    public ChatEntity getMessageEntityById(@NonNull String messageId) {
        return messageDao.getMessageById(messageId);
    }

    @Nullable
    public MessageEntity getMessageEntityFromId(@NonNull String messageId) {
        return messageDao.getMessageFromId(messageId);
    }

    @Nullable
    public GroupMessageEntity getGroupMessageEntityFromId(@NonNull String messageId) {
        return groupMessageDao.getMessageFromId(messageId);
    }

    @Nullable
    public MessageEntity getMessageEntityFromContentId(@NonNull String contentId) {
        return messageDao.getMessageFromContentId(contentId);
    }

    @Nullable
    public GroupMessageEntity getGroupMessageEntityByContentId(@NonNull String contentId) {
        return groupMessageDao.getMessageByContentId(contentId);
    }

    // This api is not used in app layer
    /*public long updateMessageEntityStatus(String messageId, int messageStatus) {
        return messageDao.updateMessageStatus(messageId, messageStatus);
    }*/
    public long updateUnreadToRead(@NonNull String friendsId) {
        return messageDao.updateMessageAsRead(friendsId);
    }

    public long updateUnreadToReadForGroup(@NonNull String friendsId) {
        return messageDao.updateMessageAsReadForGroup(friendsId);
    }

    public long updateUnreadToReadFailed(@NonNull String friendsId) {
        return messageDao.updateMessageAsReadFailed(friendsId);
    }

    public long changeMessageStatusFrom(int fromStatus, int toStatus) {
        return messageDao.changeMessageStatusFrom(fromStatus, toStatus);
    }

    public long changeSendMessageStatusByUserId(int fromContentStatus, int toStatus, String userId) {
        return messageDao.changeSendMessageStatusByUserId(fromContentStatus, toStatus, userId);
    }


    public long changeMessageStatusByUserId(int fromContentStatus, int toStatus, String userId) {
        return messageDao.changeMessageStatusByUserId(fromContentStatus, toStatus, userId);
    }

    public long changeUnreadMessageStatusByUserId(int fromContentStatus, int toStatus, String userId) {
        return messageDao.changeUnreadMessageStatusByUserId(fromContentStatus, toStatus, userId);
    }

    public long changeMessageStatusByContentStatus(int fromContentStatus, int toStatus) {
        return messageDao.changeMessageStatusByContentStatus(fromContentStatus, toStatus);
    }

    public long changeUnreadMessageStatusByContentStatus(int fromContentStatus, int toStatus) {
        return messageDao.changeUnreadMessageStatusByContentStatus(fromContentStatus, toStatus);
    }

    public Flowable<Integer> getBlockMessageInfoForSync() {
        return messageDao.getBlockMessageInfoForSync();
    }

    public int clearMessage(String threadId, boolean isGroup) {
        if (isGroup) {
            return messageDao.clearGroupMessages(threadId);
        } else {
            return messageDao.clearP2pMessages(threadId, isGroup);
        }
    }

    // This api is not used in app layer
    /*public Boolean hasChatEntityExist(String friendsId, String messageId) {
        return messageDao.hasChatEntityExist(friendsId, messageId);
    }*/
}
