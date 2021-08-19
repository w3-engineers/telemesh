package com.w3engineers.unicef.telemesh.data.local.messagetable;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;

import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

/**
 * Created by Azizul Islam on 8/16/21.
 */

@Dao
public abstract class GroupContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertOrUpdate(GroupContentEntity entity);

    @NonNull
    @Query("SELECT * FROM " + TableNames.GROUP_CONTENT + " WHERE " + ColumnNames.COLUMN_CONTENT_ID + " = :contentId LIMIT 1")
    public abstract GroupContentEntity getContentById(@NonNull String contentId);
}
