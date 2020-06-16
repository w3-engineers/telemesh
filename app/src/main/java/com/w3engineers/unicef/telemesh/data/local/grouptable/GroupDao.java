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
