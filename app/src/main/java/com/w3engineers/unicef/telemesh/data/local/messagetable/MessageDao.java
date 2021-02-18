package com.w3engineers.unicef.telemesh.data.local.messagetable;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;
import io.reactivex.Flowable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

/**
 * Access message table. Read, Write, Update operations
 * are performed here
 */

@Dao
public abstract class MessageDao extends BaseDao<MessageEntity> {

    /**
     * <h1>Retrieve all messages by user id</h1>
     * <p>The messages send or received with a very specific user
     * will be retrieve. User id must not empty</p>
     *
     * @param threadId : String (required) must not null or empty
     * @return : Flowable list of messaged
     */

    @NonNull
    @Query("SELECT * FROM " + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_FRIENDS_ID + " = :threadId AND "
            + ColumnNames.COLUMN_MESSAGE_PLACE + " = :place" + " ORDER BY " +ColumnNames.COLUMN_MESSAGE_TIME + " ASC")
    public abstract Flowable<List<MessageEntity>> getAllMessages(@NonNull String threadId, boolean place);

    @NonNull
    @Query("SELECT * FROM " + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_GROUP_ID + " = :threadId AND "
            + ColumnNames.COLUMN_MESSAGE_PLACE + " = :place" + " ORDER BY " +ColumnNames.COLUMN_MESSAGE_TIME + " ASC")
    public abstract Flowable<List<MessageEntity>> getGroupAllMessages(@NonNull String threadId, boolean place);

    @NonNull
    @Query("SELECT * FROM " + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_GROUP_ID + " = :threadId AND "
            + ColumnNames.COLUMN_MESSAGE_PLACE + " = :place AND " + ColumnNames.COLUMN_MESSAGE_TYPE + " = "
            + Constants.MessageType.GROUP_CREATE + " ORDER BY " +ColumnNames.COLUMN_MESSAGE_TIME + " ASC")
    public abstract MessageEntity getCreateGroupInfo(@NonNull String threadId, boolean place);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long writeMessage(@NonNull MessageEntity messageEntity);

    /*
     * <h1>Provide last row id</h1>
     * <p>Sometime we need last row id for different purpose</p>
     *
     * @return : Long
     */
    // This api is not used in app layer
    /*@Query("SELECT " + ColumnNames.ID + " FROM " + TableNames.MESSAGE + " ORDER BY " + ColumnNames.ID + " DESC LIMIT 1")
    long getLastRowId();*/

    /**
     * <h1>Update message status</h1>
     * <p>Message status seen , delivery, read, unread  will update </p>
     *
     * @param messageId:String  (required) must not null or empty
     * @param messageStatus:int (required) must not null or empty
     * @return : Long
     */
    @Query("UPDATE " + TableNames.MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS + " = :messageStatus WHERE "
            + ColumnNames.COLUMN_MESSAGE_ID + " LIKE :messageId")
    public abstract long updateMessageStatus(@NonNull String messageId, int messageStatus);

    /**
     * <h>Get specific message by id</h1>
     *
     * @param messageId : String (Required)
     * @return : MessageEntity
     */
    @NonNull
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM " + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_MESSAGE_ID + " LIKE :messageId LIMIT 1")
    public abstract ChatEntity getMessageById(@NonNull String messageId);

    @NonNull
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM " + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_MESSAGE_ID + " LIKE :messageId LIMIT 1")
    public abstract MessageEntity getMessageFromId(@NonNull String messageId);

    @Nullable
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM " + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_CONTENT_ID + " LIKE :contentId LIMIT 1")
    public abstract MessageEntity getMessageFromContentId(String contentId);
    /**
     * Mark all message as read
     *
     * @param friendsId    : friends id
     * @return : long
     */
    @Query("UPDATE " + TableNames.MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS +
            " = " + Constants.MessageStatus.STATUS_READ + " WHERE " + ColumnNames.COLUMN_FRIENDS_ID +
            " LIKE :friendsId AND " + ColumnNames.COLUMN_MESSAGE_STATUS + " = " + Constants.MessageStatus.STATUS_UNREAD)
    public abstract long updateMessageAsRead(@NonNull String friendsId);

    @Query("UPDATE " + TableNames.MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS +
            " = " + Constants.MessageStatus.STATUS_READ + " WHERE " + ColumnNames.COLUMN_GROUP_ID +
            " LIKE :friendsId AND " + ColumnNames.COLUMN_MESSAGE_STATUS + " = " + Constants.MessageStatus.STATUS_UNREAD)
    public abstract long updateMessageAsReadForGroup(@NonNull String friendsId);

    @Query("UPDATE " + TableNames.MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS +
            " = " + Constants.MessageStatus.STATUS_FAILED + " WHERE " + ColumnNames.COLUMN_FRIENDS_ID +
            " LIKE :friendsId AND " + ColumnNames.COLUMN_MESSAGE_STATUS + " = " + Constants.MessageStatus.STATUS_UNREAD_FAILED)
    public abstract long updateMessageAsReadFailed(@NonNull String friendsId);

    @Query("UPDATE " + TableNames.MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS
            + "=:toStatus WHERE " + ColumnNames.COLUMN_MESSAGE_STATUS + "=:fromStatus")
    public abstract long changeMessageStatusFrom(int fromStatus, int toStatus);

    @Query("UPDATE " + TableNames.MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS
            + "=:toStatus WHERE " + ColumnNames.COLUMN_FRIENDS_ID + " LIKE :userId AND "
            + ColumnNames.COLUMN_MESSAGE_STATUS + "=:fromStatus")
    public abstract long changeSendMessageStatusByUserId(int fromStatus, int toStatus, String userId);

    @Query("UPDATE " + TableNames.MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS
            + "=:toStatus WHERE " + ColumnNames.COLUMN_FRIENDS_ID + " LIKE :userId AND "
            + ColumnNames.COLUMN_CONTENT_STATUS + "=:fromContentStatus AND "
            + ColumnNames.COLUMN_MESSAGE_STATUS + "=:fromStatus")
    public abstract long changeMessageStatusByUserFrom(int fromContentStatus, int fromStatus,
                                                       int toStatus, String userId);

    @Query("UPDATE " + TableNames.MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS
            + "=:toStatus WHERE " + ColumnNames.COLUMN_FRIENDS_ID + " LIKE :userId AND "
            + ColumnNames.COLUMN_CONTENT_STATUS + "=:fromContentStatus AND "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " != " + Constants.MessageStatus.STATUS_UNREAD)
    public abstract long changeMessageStatusByUserId(int fromContentStatus, int toStatus, String userId);

    @Query("UPDATE " + TableNames.MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS
            + "=:toStatus WHERE " + ColumnNames.COLUMN_FRIENDS_ID + " LIKE :userId AND "
            + ColumnNames.COLUMN_CONTENT_STATUS + "=:fromContentStatus AND "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " = " + Constants.MessageStatus.STATUS_UNREAD)
    public abstract long changeUnreadMessageStatusByUserId(int fromContentStatus, int toStatus, String userId);

    @Query("UPDATE " + TableNames.MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS
            + "=:toStatus WHERE " + ColumnNames.COLUMN_CONTENT_STATUS + "=:fromContentStatus AND "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " != " + Constants.MessageStatus.STATUS_UNREAD)
    public abstract long changeMessageStatusByContentStatus(int fromContentStatus, int toStatus);

    @Query("UPDATE " + TableNames.MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS
            + "=:toStatus WHERE " + ColumnNames.COLUMN_CONTENT_STATUS + "=:fromContentStatus AND "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " = " + Constants.MessageStatus.STATUS_UNREAD)
    public abstract long changeUnreadMessageStatusByContentStatus(int fromContentStatus, int toStatus);

    @NonNull
    @Query("SELECT * FROM " + TableNames.MESSAGE + " ORDER BY " + ColumnNames.ID + " DESC LIMIT 1")
    public abstract Flowable<MessageEntity> getLastInsertedMessage();

    @Query("SELECT (COUNT(*)/" + Constants.AppConstant.MESSAGE_SYNC_PLOT + ") FROM " + TableNames.MESSAGE
            + " WHERE ((((SELECT COUNT(*) FROM " + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_IS_INCOMING
            + " = " + Constants.MessageType.MESSAGE_INCOMING + ")% " + Constants.AppConstant.MESSAGE_SYNC_PLOT + ") = "
            + Constants.AppConstant.DEFAULT + ")) AND (((SELECT COUNT(*) FROM " + TableNames.MESSAGE + " WHERE "
            + ColumnNames.COLUMN_IS_INCOMING + " = " + Constants.MessageType.MESSAGE_INCOMING + ")/"
            + Constants.AppConstant.MESSAGE_SYNC_PLOT + ") != " + Constants.AppConstant.DEFAULT + ") AND "
            + ColumnNames.COLUMN_IS_INCOMING + " = " + Constants.MessageType.MESSAGE_INCOMING)
    public abstract Flowable<Integer> getBlockMessageInfoForSync();

    @Query("DELETE FROM " + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_FRIENDS_ID + " = :threadId AND "
            + ColumnNames.COLUMN_MESSAGE_PLACE + " = :messagePlace")
    public abstract int clearP2pMessages(String threadId, boolean messagePlace);

    @Query("DELETE FROM " + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_GROUP_ID + " = :threadId AND "
            + ColumnNames.COLUMN_MESSAGE_PLACE + " = :messagePlace")
    public abstract int clearGroupMessages(String threadId, boolean messagePlace);
}
