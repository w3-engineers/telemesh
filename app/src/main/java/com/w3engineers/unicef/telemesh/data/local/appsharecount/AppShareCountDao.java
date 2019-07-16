package com.w3engineers.unicef.telemesh.data.local.appsharecount;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;

import io.reactivex.Single;
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */


@Dao
public abstract class AppShareCountDao extends BaseDao<AppShareCountEntity> {

    @Query("SELECT * FROM " + TableNames.APP_SHARE_COUNT + " WHERE " + ColumnNames.COLUMN_DATE + " LIKE :date")
    abstract Single<List<AppShareCountEntity>> getTodayAppShareCount(String date);

    @Query("UPDATE " + TableNames.APP_SHARE_COUNT + " SET " + ColumnNames.COLUMN_COUNT + " = " +
            ColumnNames.COLUMN_COUNT + "+1 WHERE " + ColumnNames.COLUMN_USER_ID +
            " LIKE :userId AND " + ColumnNames.COLUMN_DATE + " Like :date")
    abstract int updateCount(String userId, String date);

    @Query("SELECT COUNT FROM " + TableNames.APP_SHARE_COUNT + " WHERE " + ColumnNames.COLUMN_USER_ID + " LIKE :userId AND " + ColumnNames.COLUMN_DATE + " LIKE :date")
    abstract int isCountExist(String userId, String date);
}
