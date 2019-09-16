package com.w3engineers.unicef.telemesh.data.local.meshlog;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;

/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

@Dao
public abstract class MeshLogDao extends BaseDao<MeshLogEntity> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insertUploadedFile(@NonNull MeshLogEntity meshLogEntity);

    @Query("SELECT " + ColumnNames.LOG_NAME + " FROM " + TableNames.MESH_LOG)
    abstract List<String> getAllUploadedLogList();
}
