package com.w3engineers.unicef.telemesh.data.local.feed;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;

@Dao
public abstract class FeedDao extends BaseDao<FeedEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertFeed(FeedEntity feedEntity);


    @Query("SELECT * FROM " + TableNames.FEED + " ORDER BY " + ColumnNames.ID + " DESC")
    public abstract LiveData<List<FeedEntity>> getAllFeed();

    @Query("UPDATE " + TableNames.FEED + " SET " + ColumnNames.COLUMN_FEED_READ_STATUS + " = 1 WHERE "
            + ColumnNames.ID + " = :messageId")
    public abstract void updateFeedMessageReadStatusByMessageId(long messageId);
}
