package com.w3engineers.unicef.telemesh.data.local.messagetable;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
import com.w3engineers.unicef.util.base.database.BaseDao;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Azizul Islam on 8/11/21.
 */

@Dao
public abstract class GroupMessageDao extends BaseDao<GroupMessageEntity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long writeMessage(@NonNull GroupMessageEntity messageEntity);

    @NonNull
    @Query("SELECT * FROM " + TableNames.GROUP_MESSAGE + " WHERE " + ColumnNames.COLUMN_GROUP_ID + " = :groupId "
            + " ORDER BY " +ColumnNames.COLUMN_MESSAGE_TIME + " ASC")
    public abstract Flowable<List<GroupMessageEntity>> getGroupAllMessages(@NonNull String groupId);
}
