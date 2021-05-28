package com.w3engineers.unicef.telemesh.data.local.bulletintrack;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.util.base.database.BaseDao;

import java.util.List;

import io.reactivex.Single;

@Dao
public abstract class BulletinTrackDao extends BaseDao<BulletinTrackEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertBulletin(@NonNull BulletinTrackEntity feedEntity);

    @Nullable
    @Query("SELECT * FROM " + TableNames.FEED + " WHERE " + ColumnNames.COLUMN_FEED_ID + " IN (SELECT "
            + ColumnNames.COLUMN_BULLETIN_MESSAGE_ID + " FROM " + TableNames.BULLETIN_TRACK + " WHERE "
            + ColumnNames.COLUMN_BULLETIN_OWNER_STATUS + " = " + Constants.Bulletin.MINE + " AND "
            + ColumnNames.COLUMN_BULLETIN_MESSAGE_ID + " NOT IN (SELECT " + ColumnNames.COLUMN_BULLETIN_MESSAGE_ID
            + " FROM " + TableNames.BULLETIN_TRACK + " WHERE " + ColumnNames.COLUMN_BULLETIN_TRACK_USER_ID
            + " = :userId AND (" + ColumnNames.COLUMN_BULLETIN_ACK_STATUS + " = " + Constants.Bulletin.BULLETIN_RECEIVED
            + " OR " + ColumnNames.COLUMN_BULLETIN_ACK_STATUS + " = " + Constants.Bulletin.BULLETIN_SEND_TO_SERVER + ")))")
    public abstract Single<List<FeedEntity>> getUnsentMessage(@NonNull String userId);

    @Nullable
    @Query("SELECT * FROM " + TableNames.BULLETIN_TRACK + " WHERE "
            + ColumnNames.COLUMN_BULLETIN_ACK_STATUS + " = " + Constants.Bulletin.BULLETIN_RECEIVED)
    public abstract Single<List<BulletinTrackEntity>> getAllSuccessBulletin();

    @NonNull
    @Query("UPDATE " + TableNames.BULLETIN_TRACK + " SET " + ColumnNames.COLUMN_BULLETIN_ACK_STATUS + " = "
            + Constants.Bulletin.BULLETIN_SEND_TO_SERVER + " WHERE " + ColumnNames.COLUMN_BULLETIN_MESSAGE_ID
            + " = :messageId AND " + ColumnNames.COLUMN_BULLETIN_TRACK_USER_ID + " = :userId")
    public abstract int setFullSuccess(@NonNull String messageId, @NonNull String userId);
}
