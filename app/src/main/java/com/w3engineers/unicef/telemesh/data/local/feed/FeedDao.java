package com.w3engineers.unicef.telemesh.data.local.feed;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.annotation.NonNull;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;

@Dao
public abstract class FeedDao extends BaseDao<FeedEntity> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insertFeed(@NonNull FeedEntity feedEntity);


    @Query("SELECT * FROM " + TableNames.FEED + " ORDER BY " + ColumnNames.ID + " DESC")
    @NonNull
    public abstract LiveData<List<FeedEntity>> getAllFeed();

    @Query("UPDATE " + TableNames.FEED + " SET " + ColumnNames.COLUMN_FEED_READ_STATUS + " = 1 WHERE "
            + ColumnNames.COLUMN_FEED_ID + " = :feedId")
    public abstract long updateFeedMessageReadStatusByMessageId(@NonNull String feedId);

    @Query("SELECT * FROM " + TableNames.FEED + " WHERE " + ColumnNames.COLUMN_FEED_READ_STATUS + " = 0 ")
    public abstract LiveData<List<FeedEntity>> getAllUnreadFeed();
}
