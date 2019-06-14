package com.w3engineers.unicef.telemesh.data.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.protobuf.ByteString;
import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.ext.viper.application.data.local.BaseMeshDataSource;
import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.TeleMeshUser.RMDataModel;
import com.w3engineers.unicef.telemesh.TeleMeshUser.RMUserModel;
import com.w3engineers.unicef.telemesh.data.broadcast.BroadcastManager;
import com.w3engineers.unicef.telemesh.data.broadcast.MessageBroadcastTask;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class MeshDataSource extends BaseMeshDataSource {

    @SuppressLint("StaticFieldLeak")
    private static MeshDataSource rightMeshDataSource;
    private BroadcastManager broadcastManager;

    private List<String> userIds;

    MeshDataSource(@NonNull byte[] profileInfo) {
        super(App.getContext(), profileInfo);

        userIds = new ArrayList<>();
    }

    @NonNull
    static MeshDataSource getRmDataSource() {
        if (rightMeshDataSource == null) {
            Context context = TeleMeshApplication.getContext();

            RMUserModel rmUserMe = RMUserModel.newBuilder()
                    .setUserName(SharedPref.getSharedPref(context).read(Constants.preferenceKey.USER_NAME))
                    .setImageIndex(SharedPref.getSharedPref(context).readInt(Constants.preferenceKey.IMAGE_INDEX))
                    .build();

            byte[] bytes = rmUserMe.toByteArray();
            rightMeshDataSource = new MeshDataSource(bytes);
        }
        return rightMeshDataSource;
    }

    @Override
    protected void onRmOn() {
        //when RM will be on then prepare this observer to listen the outgoing messages
        RmDataHelper.getInstance().prepareDataObserver();
    }

    /**
     * During send data to peer
     *
     * @param rmDataModel -> A generic data model which contains userData, type and peerId
     * @return return the send message id
     */
    public long DataSend(@NonNull RMDataModel rmDataModel) {

        try {

            MeshData meshData = new MeshData();
            meshData.mType = (byte) rmDataModel.getDataType();
            meshData.mData = rmDataModel.getRawData().toByteArray();
            meshData.mMeshPeer = new MeshPeer(rmDataModel.getUserMeshId());

            return sendMeshData(meshData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * During receive a peer this time onPeer api is execute
     *
     * @param profileInfo -> Got a peer data (profile information and meshId)
     */
    @SuppressLint("TimberArgCount")
    @Override
    protected void onPeer(@NonNull BaseMeshData profileInfo) {

        try {
            String userId = profileInfo.mMeshPeer.getPeerId();

            if (!userIds.contains(userId)) {

                userIds.add(userId);

                RMUserModel.Builder rmUserModel = RMUserModel.newBuilder()
                        .mergeFrom(profileInfo.mData);

                if (rmUserModel != null) {

                    rmUserModel.setUserId(userId);
                    RmDataHelper.getInstance().userAdd(rmUserModel.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * When a peer is gone or switched the another network
     * this time onPeerGone api is executed
     *
     * @param meshPeer - > It contains the peer id which is currently inactive in mesh
     */
    @Override
    protected void onPeerGone(@NonNull MeshPeer meshPeer) {

        String userId = meshPeer.getPeerId();

        if (userIds.contains(userId)) {
            RmDataHelper.getInstance().userLeave(meshPeer);
            userIds.remove(userId);
        }
    }

    /**
     * This api execute during we receive data from network
     *
     * @param meshData -> Contains data and peer info also
     */
    @Override
    protected void onData(@NonNull MeshData meshData) {
        RMDataModel rmDataModel = RMDataModel.newBuilder()
                .setUserMeshId(meshData.mMeshPeer.getPeerId())
                .setRawData(ByteString.copyFrom(meshData.mData))
                .setDataType(meshData.mType)
                .build();

        RmDataHelper.getInstance().dataReceive(rmDataModel, true);
    }

    /**
     * The sending data status is success this time we got a success ack using this api
     *
     * @param meshAcknowledgement -> Contains the success data id and user id
     */
    @Override
    protected void onAcknowledgement(@NonNull MeshAcknowledgement meshAcknowledgement) {

        RMDataModel rmDataModel = RMDataModel.newBuilder()
                .setRecDataId(meshAcknowledgement.id)
                .setIsAckSuccess(meshAcknowledgement.isSuccess)
                .build();

        RmDataHelper.getInstance().ackReceive(rmDataModel);
    }

    @Override
    @NonNull
    protected String getOwnUserId() {
        return SharedPref.getSharedPref(TeleMeshApplication.getContext()).read(Constants.preferenceKey.MY_USER_ID);
    }

    @Override
    protected void onRmOff() {
        RmDataHelper.getInstance().stopMeshService();
    }

    /**
     * For ReInitiating RM service need to reset rightmesh data source instance
     */
    protected void resetInstance() {
        rightMeshDataSource = null;
    }


    public int broadcastMessage(@NonNull byte[] rawData, @NonNull List<BaseMeshData> livePeers){

        broadcastManager = BroadcastManager.getInstance();

        int size = livePeers.size();


        for(int i=0; i< size; i++){

            Timber.tag("Live Peers").e("size:" + size + " PeerId: " + livePeers.get(i).mMeshPeer.getPeerId());

            if(livePeers.get(i).mMeshPeer.getPeerId()!= null){

                MeshData meshData = new MeshData();
                // Since message feed will be broadcasted so here the type will be feed
                meshData.mType = Constants.DataType.MESSAGE_FEED;
                meshData.mData = rawData;
                meshData.mMeshPeer = livePeers.get(i).mMeshPeer;


                MessageBroadcastTask messageBroadcastTask = new MessageBroadcastTask();
                messageBroadcastTask.setMeshData(meshData);
                messageBroadcastTask.setBaseRmDataSource(this);
                messageBroadcastTask.setCustomThreadPoolManager(broadcastManager);

                broadcastManager.addBroadCastMessage(messageBroadcastTask);
            }

        }

        return -1;
    }
}
