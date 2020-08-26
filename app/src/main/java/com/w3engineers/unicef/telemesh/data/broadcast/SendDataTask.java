package com.w3engineers.unicef.telemesh.data.broadcast;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.unicef.util.helper.ViperUtil;
import com.w3engineers.unicef.util.helper.model.ViperBroadcastData;
import com.w3engineers.unicef.util.helper.model.ViperContentData;
import com.w3engineers.unicef.util.helper.model.ViperData;

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
    private ViperData viperData;
    private ViperContentData viperContentData;
    private ViperBroadcastData viperBroadcastData;
    private ViperUtil viperUtil;
    private String peerId;

    @Nullable
    public ViperUtil getViperUtil() {
        return viperUtil;
    }

    @NonNull
    public SendDataTask setBaseRmDataSource(@NonNull ViperUtil baseRmDataSource) {
        this.viperUtil = baseRmDataSource;
        return this;
    }

    @Nullable
    public ViperData getViperData() {
        return viperData;
    }

    @NonNull
    public SendDataTask setMeshData(@Nullable ViperData mMeshData) {
        this.viperData = mMeshData;
        return this;
    }

    public ViperBroadcastData getViperBroadcastData() {
        return viperBroadcastData;
    }

    public SendDataTask setViperBroadcastData(ViperBroadcastData viperBroadcastData) {
        this.viperBroadcastData = viperBroadcastData;
        return this;
    }

    public ViperContentData getViperContentData() {
        return viperContentData;
    }

    public SendDataTask setViperContentData(ViperContentData viperContentData) {
        this.viperContentData = viperContentData;
        return this;
    }

    public String getPeerId() {
        return peerId;
    }

    public SendDataTask setPeerId(String peerId) {
        this.peerId = peerId;
        return this;
    }

    @SuppressLint("TimberArgCount")
    @Override
    @Nullable
    public Object call() {
        try {
            // check if thread is interrupted before lengthy operation
            if (Thread.interrupted()) throw new InterruptedException();

            ViperData viperData = getViperData();
            ViperContentData viperContentData = getViperContentData();
            ViperBroadcastData viperBroadcastData = getViperBroadcastData();

            if (getViperUtil() != null) {
                if (viperData != null) {
                    return getViperUtil().sendMeshData(getPeerId(), viperData);
                }

                if (viperContentData != null) {
                    return getViperUtil().sendContentMessage(getPeerId(), viperContentData);
                }

                if (viperBroadcastData != null) {
                    return getViperUtil().sendLocalBroadcast(viperBroadcastData);
                }
            }

        } catch (InterruptedException e) { e.printStackTrace(); }
        return null;
    }
}
