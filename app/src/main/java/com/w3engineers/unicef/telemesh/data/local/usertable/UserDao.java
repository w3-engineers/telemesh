package com.w3engineers.unicef.telemesh.data.local.usertable;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;
import io.reactivex.Flowable;

@Dao
public abstract class UserDao implements BaseDao<UserEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long writeUser(UserEntity userEntity);

    @Query("DELETE FROM " + TableNames.USERS)
    abstract void deleteAllUsers();

    @Query("DELETE FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract int deleteUser(String meshId);


    @Query("SELECT * FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract Flowable<UserEntity> getUserById(String meshId);


    @Query("SELECT * FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract UserEntity getSingleUserById(String meshId);


    @Query("UPDATE " + TableNames.USERS + " SET " + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.OFFLINE
            + " WHERE " + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.ONLINE)
    abstract int updateUserOffline();

    @Query("SELECT * FROM " + TableNames.USERS + " ORDER BY " + ColumnNames.ID + " DESC LIMIT 1")
    public abstract Flowable<UserEntity> getLastInsertedUser();

    // Relatively faster then direct sub query, still expecting some more performance improvement of this query
    // Should be exactly minimum value
    @Query("SELECT * FROM " + TableNames.USERS + " LEFT JOIN ( SELECT * FROM ( SELECT *, sum(CASE "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " WHEN " + Constants.MessageStatus.STATUS_UNREAD
            + " THEN 1 ELSE 0 END) AS hasUnreadMessage, MAX(" + ColumnNames.ID + ") AS MAXID FROM "
            + TableNames.MESSAGE + " GROUP BY " + ColumnNames.COLUMN_FRIENDS_ID + ") AS M INNER JOIN "
            + TableNames.MESSAGE + " AS MSG ON MSG." + ColumnNames.COLUMN_FRIENDS_ID + " = M."
            + ColumnNames.COLUMN_FRIENDS_ID + " WHERE MSG." + ColumnNames.ID + " = M.MAXID) AS MESS ON "
            + TableNames.USERS + "." + ColumnNames.COLUMN_USER_MESH_ID + " = MESS."
            + ColumnNames.COLUMN_FRIENDS_ID + " ORDER BY IFNULL(" + ColumnNames.COLUMN_MESSAGE_STATUS + ","
            + Constants.MessageStatus.STATUS_READ + ") ASC, " + ColumnNames.COLUMN_USER_IS_ONLINE
            + " DESC, " + TableNames.USERS + "." + ColumnNames.COLUMN_USER_FIRST_NAME + " COLLATE NOCASE ASC, "
            + TableNames.USERS + "." + ColumnNames.COLUMN_USER_LAST_NAME + " COLLATE NOCASE ASC")
    abstract Flowable<List<UserEntity>> getAllUsers();

}