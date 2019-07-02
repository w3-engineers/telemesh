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
import com.w3engineers.mesh.util.HandlerUtil;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.TeleMeshUser.RMDataModel;
import com.w3engineers.unicef.telemesh.TeleMeshUser.RMUserModel;
import com.w3engineers.unicef.telemesh.data.broadcast.BroadcastManager;
import com.w3engineers.unicef.telemesh.data.broadcast.SendDataTask;
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

    private List<String> userIds;
    private BroadcastManager broadcastManager;

    MeshDataSource(@NonNull byte[] profileInfo) {
        super(App.getContext(), profileInfo);

        userIds = new ArrayList<>();
        broadcastManager = BroadcastManager.getInstance();
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

        SharedPref.getSharedPref(TeleMeshApplication.getContext()).write(Constants.preferenceKey.MY_USER_ID, getMyMeshId());
    }

    /**
     * During send data to peer
     *
     * @param rmDataModelBuilder -> A generic data model which contains userData, type and peerId
     * @return return the send message id
     */
    public void DataSend(@NonNull RMDataModel.Builder rmDataModelBuilder, @NonNull String receiverId) {

        RMDataModel rmDataModel = rmDataModelBuilder.setUserMeshId(receiverId).build();

        MeshData meshData = new MeshData();
        meshData.mType = (byte) rmDataModel.getDataType();
        meshData.mData = rmDataModel.getRawData().toByteArray();
        meshData.mMeshPeer = new MeshPeer(rmDataModel.getUserMeshId());

        broadcastManager.addBroadCastMessage(getMeshDataTask(meshData));
    }

    public void DataSend(@NonNull RMDataModel.Builder rmDataModelBuilder, @NonNull List<String> receiverIds) {
        for (String receiverId : receiverIds) {
            DataSend(rmDataModelBuilder, receiverId);
        }
    }

    private SendDataTask getMeshDataTask(MeshData meshData) {
        return new SendDataTask().setMeshData(meshData).setBaseRmDataSource(this);
    }

    /**
     * During receive a peer this time onPeer api is execute
     *
     * @param profileInfo -> Got a peer data (profile information and meshId)
     */
    protected void onPeer(@NonNull BaseMeshData profileInfo) {

        try {
            String userId = profileInfo.mMeshPeer.getPeerId();

            if (!userIds.contains(userId)) {

                userIds.add(userId);

                RMUserModel.Builder rmUserModel = RMUserModel.newBuilder()
                        .mergeFrom(profileInfo.mData);

                if (rmUserModel != null) {

                    rmUserModel.setUserId(userId);
                    HandlerUtil.postBackground(()-> RmDataHelper.getInstance().userAdd(rmUserModel.build()));

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
            HandlerUtil.postBackground(()-> RmDataHelper.getInstance().userLeave(meshPeer));
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

        HandlerUtil.postBackground(()-> RmDataHelper.getInstance().dataReceive(rmDataModel, true));
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

        HandlerUtil.postBackground(()-> RmDataHelper.getInstance().ackReceive(rmDataModel));
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
}
