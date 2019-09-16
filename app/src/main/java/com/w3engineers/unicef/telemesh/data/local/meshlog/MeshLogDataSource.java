package com.w3engineers.unicef.telemesh.data.local.meshlog;

import android.support.annotation.NonNull;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

public class MeshLogDataSource {

    private MeshLogDao meshLogDao;
    private ExecutorService mIoExecutor;
    private static MeshLogDataSource meshLogDataSource = new MeshLogDataSource();

    private MeshLogDataSource() {
        meshLogDao = AppDatabase.getInstance().meshLogDao();
        mIoExecutor = Executors.newSingleThreadExecutor();
    }

    @NonNull
    public static MeshLogDataSource getInstance() {
        return meshLogDataSource;
    }

    public long insertOrUpdateData(MeshLogEntity entity) {
        Callable<Long> insertCallable = () -> meshLogDao.insertUploadedFile(entity);

        try {
            return mIoExecutor.submit(insertCallable).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<String> getAllUploadedLogList() {
        return meshLogDao.getAllUploadedLogList();
    }
}
