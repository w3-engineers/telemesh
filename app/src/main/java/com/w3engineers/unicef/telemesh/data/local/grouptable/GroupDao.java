package com.w3engineers.unicef.telemesh.data.local.grouptable;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;


@Dao
public abstract class GroupDao extends BaseDao<GroupEntity> {

    @Query("SELECT * FROM " + TableNames.GROUP)
    abstract List<GroupEntity> getAllGroups();
}
