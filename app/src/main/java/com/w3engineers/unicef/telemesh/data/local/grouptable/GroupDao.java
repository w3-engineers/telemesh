package com.w3engineers.unicef.telemesh.data.local.grouptable;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;


@Dao
public abstract class GroupDao extends BaseDao<GroupEntity> {

    @Query("SELECT * FROM " + TableNames.GROUP + " ORDER BY " + ColumnNames.COLUMN_GROUP_CREATION_TIME + " DESC")
    abstract List<GroupEntity> getAllGroups();
}
