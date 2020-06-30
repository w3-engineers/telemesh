package com.w3engineers.unicef.telemesh.data.local.grouptable;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;

import java.util.List;

import io.reactivex.Flowable;


@Dao
public abstract class GroupDao extends BaseDao<GroupEntity> {

    @Query("SELECT * FROM " + TableNames.GROUP + " WHERE " + ColumnNames.COLUMN_GROUP_NAME + " IS NOT NULL ORDER BY " + ColumnNames.COLUMN_GROUP_CREATION_TIME + " DESC")
    abstract Flowable<List<GroupEntity>> getAllGroups();

    @Query("SELECT *, group_table.group_id FROM " + TableNames.GROUP + " LEFT JOIN ( SELECT * FROM ( SELECT *, sum(CASE "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " WHEN " + Constants.MessageStatus.STATUS_UNREAD
            + " THEN 1 " + " WHEN " + Constants.MessageStatus.STATUS_UNREAD_FAILED + " THEN 1 "
            + " ELSE 0 END) AS hasUnreadMessage, MAX(" + ColumnNames.ID + ") AS MAXID, " + ColumnNames.COLUMN_MESSAGE
            + " AS lastMessage, (SELECT " + ColumnNames.COLUMN_USER_NAME + " from " + TableNames.USERS + " WHERE "
            + ColumnNames.COLUMN_USER_MESH_ID + " = " + ColumnNames.COLUMN_FRIENDS_ID + ") AS lastPersonName, "
            + ColumnNames.COLUMN_MESSAGE_TYPE + " AS lastMessageType, " + ColumnNames.COLUMN_FRIENDS_ID
            + " AS lastPersonId FROM " + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_MESSAGE_PLACE
            + " == " + Constants.MessagePlace.VALUE_MESSAGE_PLACE_GROUP + " GROUP BY " + ColumnNames.COLUMN_GROUP_ID
            + ") AS M INNER JOIN " + TableNames.MESSAGE + " AS MSG ON MSG." + ColumnNames.COLUMN_GROUP_ID
            + " = M." + "group_id" + " WHERE MSG." + ColumnNames.ID + " = M.MAXID) AS MESS ON "
            + TableNames.GROUP + "." + "group_id" + " = MESS." + ColumnNames.COLUMN_GROUP_ID
            + " WHERE " + ColumnNames.COLUMN_GROUP_NAME + " IS NOT NULL ORDER BY " + ColumnNames.COLUMN_GROUP_CREATION_TIME + " DESC")
    abstract Flowable<List<GroupEntity>> getAllGroupsWithCount();

    @NonNull
    @Query("SELECT * FROM " + TableNames.GROUP + " WHERE " + ColumnNames.COLUMN_GROUP_OWN_STATUS + " = "
            + Constants.GroupUserOwnState.GROUP_CREATE + " ORDER BY " + ColumnNames.ID + " DESC LIMIT 1")
    abstract Flowable<GroupEntity> getLastCreatedGroup();

    @Query("SELECT * FROM " + TableNames.GROUP + " WHERE " + ColumnNames.COLUMN_GROUP_ID + " = :groupId")
    abstract GroupEntity getGroupById(String groupId);

    @Query("SELECT * FROM " + TableNames.GROUP + " WHERE " + ColumnNames.COLUMN_GROUP_ID + " = :groupId")
    abstract LiveData<GroupEntity> getLiveGroupById(String groupId);

    @Query("SELECT * FROM " + TableNames.GROUP + " WHERE " + ColumnNames.COLUMN_GROUP_NAME + " LIKE :userId")
    abstract List<GroupEntity> getGroupByUserId(String userId);

    @Query("DELETE FROM " + TableNames.GROUP + " WHERE " + ColumnNames.COLUMN_GROUP_ID + " = :groupId")
    abstract int deleteGroupById(String groupId);
}
