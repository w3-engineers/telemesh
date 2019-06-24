package com.w3engineers.unicef.telemesh.data.broadcast;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.ext.viper.application.data.local.BaseMeshDataSource;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

/**
 * Created by Anjan Debnath on 6/28/2018.
 * Copyright (c) 2018, W3 Engineers Ltd. All rights reserved..
 * <p>
 * SendDataTask is used for sending tasks to the thread pool. When a callable is submitted,
 * a Future object is returned, allowing the thread pool manager to stop the task.
 */
public class SendDataTask implements Callable {


    // Keep a weak reference to the CustomThreadPoolManager singleton object, so we can send a
    // message. Use of weak reference is not a must here because CustomThreadPoolManager lives
    // across the whole application lifecycle
    private MeshData mMeshData;
    private BaseMeshDataSource baseRmDataSource;

    @Nullable
    public BaseMeshDataSource getBaseRmDataSource() {
        return baseRmDataSource;
    }

    public SendDataTask setBaseRmDataSource(@NonNull BaseMeshDataSource baseRmDataSource) {
        this.baseRmDataSource = baseRmDataSource;
        return this;
    }

    @Nullable
    public MeshData getMeshData() {
        return mMeshData;
    }

    public SendDataTask setMeshData(@Nullable MeshData mMeshData) {
        this.mMeshData = mMeshData;
        return this;
    }


    @SuppressLint("TimberArgCount")
    @Override
    @Nullable
    public Object call() {
        try {
            // check if thread is interrupted before lengthy operation
            if (Thread.interrupted()) throw new InterruptedException();

            if (getBaseRmDataSource() != null) {
                return getBaseRmDataSource().sendMeshData(getMeshData());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
