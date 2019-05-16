package com.w3engineers.unicef.telemesh.data.broadcast;

import android.annotation.SuppressLint;
import android.os.RemoteException;
import android.util.Log;

import com.w3engineers.ext.viper.application.data.local.BaseMeshDataSource;
import com.w3engineers.ext.viper.application.data.remote.BaseRmDataSource;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

/**
 * Created by Anjan Debnath on 6/28/2018.
 * Copyright (c) 2018, W3 Engineers Ltd. All rights reserved..
 * <p>
 * MessageBroadcastTask is used for sending tasks to the thread pool. When a callable is submitted,
 * a Future object is returned, allowing the thread pool manager to stop the task.
 */
public class MessageBroadcastTask implements Callable {


    // Keep a weak reference to the CustomThreadPoolManager singleton object, so we can send a
    // message. Use of weak reference is not a must here because CustomThreadPoolManager lives
    // across the whole application lifecycle
    private WeakReference<BroadcastManager> mCustomThreadPoolManagerWeakReference;
    private MeshData mMeshData;
    private BaseMeshDataSource baseRmDataSource;

    public BaseMeshDataSource getBaseRmDataSource() {
        return baseRmDataSource;
    }

    public void setBaseRmDataSource(BaseMeshDataSource baseRmDataSource) {
        this.baseRmDataSource = baseRmDataSource;
    }


    public MeshData getMeshData() {
        return mMeshData;
    }

    public void setMeshData(MeshData mMeshData) {
        this.mMeshData = mMeshData;
    }


    @SuppressLint("TimberArgCount")
    @Override
    public Object call() {
        try {

            long sentStatus = -1;
            // check if thread is interrupted before lengthy operation
            if (Thread.interrupted()) throw new InterruptedException();


                Log.e("Live Peers", "message: sent");
                sentStatus = getBaseRmDataSource().sendMeshData(getMeshData());


            // After work is finished, send a message to CustomThreadPoolManager
            /*Message message = Util.createMessage(Util.MESSAGE_ID, "Thread " +
                    String.valueOf(Thread.currentThread().getId()) + " " +
                    String.valueOf(Thread.currentThread().getName()) + " completed");

            if(mCustomThreadPoolManagerWeakReference != null
                    && mCustomThreadPoolManagerWeakReference.get() != null) {

                mCustomThreadPoolManagerWeakReference.get().sendMessageToUiThread(message);
            }*/

            return sentStatus;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCustomThreadPoolManager(BroadcastManager customThreadPoolManager) {
        this.mCustomThreadPoolManagerWeakReference = new WeakReference<BroadcastManager>(customThreadPoolManager);
    }
}
