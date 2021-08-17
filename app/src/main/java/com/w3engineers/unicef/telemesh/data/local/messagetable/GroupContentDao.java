package com.w3engineers.unicef.telemesh.data.local.messagetable;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

/**
 * Created by Azizul Islam on 8/16/21.
 */

@Dao
public abstract class GroupContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertOrUpdate(GroupContentEntity entity);
}
