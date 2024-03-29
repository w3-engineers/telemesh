package com.w3engineers.unicef.telemesh.data.local.feed;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.annotation.NonNull;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
import com.w3engineers.unicef.util.base.database.BaseDao;

import java.util.List;

import io.reactivex.Single;

@Dao
public abstract class FeedDao extends BaseDao<FeedEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertFeed(@NonNull FeedEntity feedEntity);


    @Query("SELECT * FROM " + TableNames.FEED + " ORDER BY " + ColumnNames.COLUMN_FEED_TIME_MILLIS + " DESC")
    @NonNull
    public abstract LiveData<List<FeedEntity>> getAllFeed();

    @Query("UPDATE " + TableNames.FEED + " SET " + ColumnNames.COLUMN_FEED_READ_STATUS + " = 1 WHERE "
            + ColumnNames.COLUMN_FEED_ID + " = :feedId")
    public abstract long updateFeedMessageReadStatusByMessageId(@NonNull String feedId);

    @Query("SELECT * FROM " + TableNames.FEED + " WHERE " + ColumnNames.COLUMN_FEED_READ_STATUS + " = 0 ")
    public abstract LiveData<List<FeedEntity>> getAllUnreadFeed();

    @Query("SELECT * FROM " + TableNames.FEED + " WHERE " + ColumnNames.COLUMN_FEED_ID + " = :feedId ")
    public abstract LiveData<FeedEntity> getFeedEntityById(String feedId);

    @Query("SELECT * FROM " + TableNames.FEED + " WHERE " + ColumnNames.COLUMN_FEED_ID + " = :feedId ")
    public abstract FeedEntity getFeedById(String feedId);

    @Query("SELECT COUNT ( " + ColumnNames.COLUMN_FEED_ID + " ) FROM " + TableNames.FEED)
    public abstract Single<Integer> getRowCount();
}
