package com.w3engineers.unicef.telemesh.data.local.usertable;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public abstract class UserDao extends BaseDao<UserEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long writeUser(@NonNull UserEntity userEntity);

    @Query("DELETE FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract int deleteUser(@NonNull String meshId);

    @NonNull
    @Query("SELECT * FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract Flowable<UserEntity> getUserById(@NonNull String meshId);

    @Nullable
    @Query("SELECT * FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract UserEntity getSingleUserById(@NonNull String meshId);


    @Query("UPDATE " + TableNames.USERS + " SET " + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.OFFLINE
            + " WHERE " + ColumnNames.COLUMN_USER_IS_ONLINE + " != " + Constants.UserStatus.OFFLINE)
    abstract int updateUserOffline();

    @NonNull
    @Query("SELECT * FROM " + TableNames.USERS + " ORDER BY " + ColumnNames.ID + " DESC LIMIT 1")
    public abstract Flowable<UserEntity> getLastInsertedUser();

    // Relatively faster then direct sub query, still expecting some more performance improvement of this query
    // Should be exactly minimum value

    @NonNull
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM " + TableNames.USERS + " LEFT JOIN ( SELECT * FROM ( SELECT *, sum(CASE "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " WHEN " + Constants.MessageStatus.STATUS_UNREAD
            + " THEN 1 " + " WHEN " + Constants.MessageStatus.STATUS_UNREAD_FAILED + " THEN 1 "
            + " ELSE 0 END) AS hasUnreadMessage, MAX(" + ColumnNames.ID + ") AS MAXID FROM "
            + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_MESSAGE_PLACE + " == "
            + Constants.MessagePlace.VALUE_MESSAGE_PLACE_P2P + " GROUP BY " + ColumnNames.COLUMN_FRIENDS_ID
            + ") AS M INNER JOIN " + TableNames.MESSAGE + " AS MSG ON MSG." + ColumnNames.COLUMN_FRIENDS_ID + " = M."
            + ColumnNames.COLUMN_FRIENDS_ID + " WHERE MSG." + ColumnNames.ID + " = M.MAXID) AS MESS ON "
            + TableNames.USERS + "." + ColumnNames.COLUMN_USER_MESH_ID + " = MESS."
            + ColumnNames.COLUMN_FRIENDS_ID + " WHERE ( " + ColumnNames.COLUMN_USER_IS_ONLINE + " != "
            + Constants.UserStatus.OFFLINE + " OR ( " + ColumnNames.COLUMN_USER_IS_ONLINE + " = "
            + Constants.UserStatus.OFFLINE + " AND (" + ColumnNames.COLUMN_MESSAGE_STATUS + " IS "
            + Constants.MessageStatus.STATUS_UNREAD + " OR " + ColumnNames.COLUMN_MESSAGE_STATUS
            + " IS " + Constants.MessageStatus.STATUS_UNREAD_FAILED + " ))) ORDER BY CASE " + ColumnNames.COLUMN_MESSAGE_STATUS
            + " WHEN NULL THEN " + Constants.MessageStatus.STATUS_READ + " ELSE (CASE "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " WHEN " + Constants.MessageStatus.STATUS_UNREAD
            + " THEN " + Constants.MessageStatus.STATUS_UNREAD + " WHEN " + Constants.MessageStatus.STATUS_UNREAD_FAILED
            + " THEN " + Constants.MessageStatus.STATUS_UNREAD + " ELSE " + Constants.MessageStatus.STATUS_READ
            + " END) END ASC, CASE " + ColumnNames.COLUMN_USER_IS_ONLINE + " WHEN " + Constants.UserStatus.OFFLINE
            + " THEN " + Constants.UserStatus.OFFLINE + " ELSE " + Constants.UserStatus.WIFI_ONLINE + " END DESC, "
            + TableNames.USERS + "." + ColumnNames.COLUMN_USER_NAME + " COLLATE NOCASE ASC")
    abstract Flowable<List<UserEntity>> getAllOnlineUsers();

    @Query("SELECT * FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " != :myMeshId"
            + " ORDER BY CASE " + ColumnNames.COLUMN_USER_IS_ONLINE + " WHEN " + Constants.UserStatus.OFFLINE
            + " THEN " + Constants.UserStatus.OFFLINE + " ELSE " + Constants.UserStatus.WIFI_ONLINE + " END DESC, "
            + TableNames.USERS + "." + ColumnNames.COLUMN_USER_NAME + " COLLATE NOCASE ASC")
    abstract Flowable<List<UserEntity>> getAllUsersForGroup(String myMeshId);

    @NonNull
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM " + TableNames.USERS + " LEFT JOIN ( SELECT * FROM ( SELECT *, sum(CASE "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " WHEN " + Constants.MessageStatus.STATUS_UNREAD
            + " THEN 1 " + " WHEN " + Constants.MessageStatus.STATUS_UNREAD_FAILED + " THEN 1 "
            + " ELSE 0 END) AS hasUnreadMessage, MAX(" + ColumnNames.ID + ") AS MAXID FROM "
            + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_MESSAGE_PLACE + " == "
            + Constants.MessagePlace.VALUE_MESSAGE_PLACE_P2P + " GROUP BY " + ColumnNames.COLUMN_FRIENDS_ID
            + ") AS M INNER JOIN " + TableNames.MESSAGE + " AS MSG ON MSG." + ColumnNames.COLUMN_FRIENDS_ID + " = M."
            + ColumnNames.COLUMN_FRIENDS_ID + " WHERE MSG." + ColumnNames.ID + " = M.MAXID) AS MESS ON "
            + TableNames.USERS + "." + ColumnNames.COLUMN_USER_MESH_ID + " = MESS."
            + ColumnNames.COLUMN_FRIENDS_ID + " WHERE " /*+ ColumnNames.COLUMN_MESSAGE_PLACE + " == "
            + Constants.MessagePlace.VALUE_MESSAGE_PLACE_P2P + " AND "*/ + ColumnNames.COLUMN_USER_IS_FAVOURITE
            + " == " + Constants.FavouriteStatus.FAVOURITE + " ORDER BY CASE " + ColumnNames.COLUMN_MESSAGE_STATUS
            + " WHEN NULL THEN " + Constants.MessageStatus.STATUS_READ + " ELSE (CASE "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " WHEN " + Constants.MessageStatus.STATUS_UNREAD
            + " THEN " + Constants.MessageStatus.STATUS_UNREAD + " WHEN " + Constants.MessageStatus.STATUS_UNREAD_FAILED
            + " THEN " + Constants.MessageStatus.STATUS_UNREAD + " ELSE " + Constants.MessageStatus.STATUS_READ
            + " END) END ASC, CASE " + ColumnNames.COLUMN_USER_IS_ONLINE + " WHEN " + Constants.UserStatus.OFFLINE
            + " THEN " + Constants.UserStatus.OFFLINE + " ELSE " + Constants.UserStatus.WIFI_ONLINE + " END DESC, "
            + TableNames.USERS + "." + ColumnNames.COLUMN_USER_NAME + " COLLATE NOCASE ASC")
    abstract Flowable<List<UserEntity>> getAllFavouriteContactUsers();

    @NonNull
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM " + TableNames.USERS + " LEFT JOIN ( SELECT * FROM ( SELECT *, sum(CASE "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " WHEN " + Constants.MessageStatus.STATUS_UNREAD
            + " THEN 1 " + " WHEN " + Constants.MessageStatus.STATUS_UNREAD_FAILED + " THEN 1 "
            + " ELSE 0 END) AS hasUnreadMessage, MAX(" + ColumnNames.ID + ") AS MAXID FROM "
            + TableNames.MESSAGE + " WHERE " + ColumnNames.COLUMN_MESSAGE_PLACE + " == "
            + Constants.MessagePlace.VALUE_MESSAGE_PLACE_P2P + " GROUP BY " + ColumnNames.COLUMN_FRIENDS_ID + ") AS M INNER JOIN "
            + TableNames.MESSAGE + " AS MSG ON MSG." + ColumnNames.COLUMN_FRIENDS_ID + " = M."
            + ColumnNames.COLUMN_FRIENDS_ID + " WHERE MSG." + ColumnNames.ID + " = M.MAXID) AS MESS ON "
            + TableNames.USERS + "." + ColumnNames.COLUMN_USER_MESH_ID + " = MESS."
            + ColumnNames.COLUMN_FRIENDS_ID + " WHERE ((" + ColumnNames.COLUMN_MESSAGE_STATUS
            + " IS NOT NULL AND " + ColumnNames.COLUMN_MESSAGE_PLACE + " == " + Constants.MessagePlace.VALUE_MESSAGE_PLACE_P2P
            + ") OR " + ColumnNames.COLUMN_USER_IS_FAVOURITE + " == " + Constants.FavouriteStatus.FAVOURITE + ") ORDER BY CASE "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " WHEN NULL THEN " + Constants.MessageStatus.STATUS_READ + " ELSE (CASE "
            + ColumnNames.COLUMN_MESSAGE_STATUS + " WHEN " + Constants.MessageStatus.STATUS_UNREAD
            + " THEN " + Constants.MessageStatus.STATUS_UNREAD + " WHEN " + Constants.MessageStatus.STATUS_UNREAD_FAILED
            + " THEN " + Constants.MessageStatus.STATUS_UNREAD + " ELSE " + Constants.MessageStatus.STATUS_READ
            + " END) END ASC, CASE " + ColumnNames.COLUMN_USER_IS_ONLINE + " WHEN " + Constants.UserStatus.OFFLINE
            + " THEN " + Constants.UserStatus.OFFLINE + " ELSE " + Constants.UserStatus.WIFI_ONLINE + " END DESC, "
            + TableNames.USERS + "." + ColumnNames.COLUMN_USER_NAME + " COLLATE NOCASE ASC")
    abstract Flowable<List<UserEntity>> getAllMessagedWithFavouriteUsers();


    @Query("SELECT * FROM " + TableNames.USERS + " WHERE "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.HB_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.HB_MESH_ONLINE)
    @NonNull
    public abstract List<UserEntity> getLivePeers();

    @Query("SELECT " + ColumnNames.COLUMN_USER_MESH_ID + ", " + ColumnNames.COLUMN_USER_REGISTRATION_TIME
            + " FROM " + TableNames.USERS + " WHERE " + ColumnNames.COLUMN_USER_IS_SYNCED + " = " + 0
            + " AND " + ColumnNames.COLUMN_USER_MESH_ID + " != :myMeshId")
    public abstract List<UserEntity.NewMeshUserCount> getUnSyncedUsers(String myMeshId);

    @Query("UPDATE " + TableNames.USERS + " SET " + ColumnNames.COLUMN_USER_IS_SYNCED + " = " + 1
            + " WHERE " + ColumnNames.COLUMN_USER_IS_SYNCED + " = " + 0 + " AND "
            + ColumnNames.COLUMN_USER_MESH_ID + " != :myMeshId")
    abstract int updateUserToSynced(String myMeshId);

    @Query("UPDATE " + TableNames.USERS + " SET " + ColumnNames.COLUMN_USER_IS_ONLINE + " = :activityStatus"
            + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract int updateUserStatus(String meshId, int activityStatus);

    @Query("UPDATE " + TableNames.USERS + " SET " + ColumnNames.COLUMN_USER_IS_FAVOURITE + " = :favouriteStatus"
            + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :meshId")
    abstract int updateFavouriteStatus(String meshId, int favouriteStatus);

    @NonNull
    @Query("SELECT * FROM " + TableNames.USERS + " WHERE mesh_id IN (:whereCl)")
    abstract LiveData<List<UserEntity>> getGroupMembers(List<String> whereCl);

    @NonNull
    @Query("SELECT * FROM " + TableNames.USERS + " WHERE mesh_id IN (:whereCl)")
    abstract List<UserEntity> getGroupLiveMembers(List<String> whereCl);

    @NonNull
    @Query("SELECT * FROM " + TableNames.USERS + " WHERE "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.INTERNET_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.HB_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.HB_MESH_ONLINE)
    abstract LiveData<List<UserEntity>> getActiveUser();

    @Query("SELECT " + ColumnNames.COLUMN_USER_MESH_ID + " FROM " + TableNames.USERS + " WHERE "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.HB_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.HB_MESH_ONLINE)
    abstract List<String> getLocalActiveUsers();

    @Query("SELECT " + ColumnNames.COLUMN_USER_MESH_ID + " FROM " + TableNames.USERS + " WHERE ("
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.HB_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.HB_MESH_ONLINE + ") AND "
            + ColumnNames.COLUMN_USER_CONFIG_VERSION + " < :updateVersionCode")
    abstract List<String> getLocalWithBackConfigUsers(int updateVersionCode);

    @Query("UPDATE " + TableNames.USERS + " SET " + ColumnNames.COLUMN_USER_CONFIG_VERSION + " = :updateVersionCode"
            + " WHERE (" + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.WIFI_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_MESH_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.BLE_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.HB_ONLINE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " = " + Constants.UserStatus.HB_MESH_ONLINE + ") AND "
            + ColumnNames.COLUMN_USER_CONFIG_VERSION + " < :updateVersionCode")
    abstract int updateBackConfigUsers(int updateVersionCode);


    @Query("UPDATE " + TableNames.USERS + " SET " + ColumnNames.COLUMN_USER_CONFIG_VERSION + " = :updateVersionCode"
            + " WHERE " + ColumnNames.COLUMN_USER_MESH_ID + " = :userId")
    abstract int updateBroadcastUserConfigVersion(int updateVersionCode, String userId);

    @NonNull
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT " + ColumnNames.COLUMN_USER_MESH_ID +" FROM " + TableNames.USERS
            + " LEFT JOIN ( SELECT * FROM ( SELECT *, sum(CASE " + ColumnNames.COLUMN_MESSAGE_STATUS
            + " WHEN " + Constants.MessageStatus.STATUS_UNREAD + " THEN 1 ELSE 0 END) AS hasUnreadMessage, MAX("
            + ColumnNames.ID + ") AS MAXID FROM " + TableNames.MESSAGE + " GROUP BY " + ColumnNames.COLUMN_FRIENDS_ID
            + ") AS M INNER JOIN " + TableNames.MESSAGE + " AS MSG ON MSG." + ColumnNames.COLUMN_FRIENDS_ID + " = M."
            + ColumnNames.COLUMN_FRIENDS_ID + " WHERE MSG." + ColumnNames.ID + " = M.MAXID) AS MESS ON "
            + TableNames.USERS + "." + ColumnNames.COLUMN_USER_MESH_ID + " = MESS."
            + ColumnNames.COLUMN_FRIENDS_ID + " WHERE (" + ColumnNames.COLUMN_MESSAGE_STATUS + " IS NOT NULL OR "
            + ColumnNames.COLUMN_USER_IS_FAVOURITE + " == " + Constants.FavouriteStatus.FAVOURITE + " OR "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " != " + Constants.UserStatus.OFFLINE + ")")
    abstract Single<List<String>> getAllFabMessagedActiveUserIds();

    /*@Query("SELECT " + ColumnNames.COLUMN_USER_MESH_ID + " FROM " + TableNames.USERS + " WHERE "
            + ColumnNames.COLUMN_USER_IS_ONLINE + " != " + Constants.UserStatus.OFFLINE)
    abstract Single<List<String>> getAllActiveUsers();*/
}