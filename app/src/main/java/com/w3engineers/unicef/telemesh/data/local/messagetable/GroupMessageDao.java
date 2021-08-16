package com.w3engineers.unicef.telemesh.data.local.messagetable;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
import com.w3engineers.unicef.util.base.database.BaseDao;

import java.util.List;

import io.reactivex.Flowable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@Dao
public abstract class GroupMessageDao extends BaseDao<GroupMessageEntity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long writeMessage(@NonNull GroupMessageEntity messageEntity);

    @NonNull
    @Query("SELECT * FROM " + TableNames.GROUP_MESSAGE + " WHERE " + ColumnNames.COLUMN_GROUP_ID + " = :groupId "
            + " ORDER BY " + ColumnNames.COLUMN_MESSAGE_TIME + " ASC")
    public abstract Flowable<List<GroupMessageEntity>> getGroupAllMessages(@NonNull String groupId);

    @NonNull
    @Query("SELECT * FROM " + TableNames.GROUP_MESSAGE + " where " + ColumnNames.COLUMN_MESSAGE_STATUS + " = " + Constants.MessageStatus.STATUS_SENDING +
            " ORDER BY " + ColumnNames.ID + " DESC LIMIT 1")
    public abstract Flowable<GroupMessageEntity> getLastInsertedMessage();

    /**
     * <h1>Update message status</h1>
     * <p>Message status seen , delivery, read, unread  will update </p>
     *
     * @param messageId:String  (required) must not null or empty
     * @param messageStatus:int (required) must not null or empty
     * @return : Long
     */
    @Query("UPDATE " + TableNames.GROUP_MESSAGE + " SET " + ColumnNames.COLUMN_MESSAGE_STATUS + " = :messageStatus WHERE "
            + ColumnNames.COLUMN_MESSAGE_ID + " LIKE :messageId")
    public abstract long updateMessageStatus(@NonNull String messageId, int messageStatus);

}
