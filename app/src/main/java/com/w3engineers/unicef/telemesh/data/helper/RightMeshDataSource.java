package com.w3engineers.unicef.telemesh.data.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.RemoteException;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.ext.viper.application.data.remote.BaseRmDataSource;
import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.TeleMeshUser.*;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [22-Oct-2018 at 7:12 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [22-Oct-2018 at 7:12 PM].
 * * --> <Second Editor> on [22-Oct-2018 at 7:12 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [22-Oct-2018 at 7:12 PM].
 * * --> <Second Reviewer> on [22-Oct-2018 at 7:12 PM].
 * * ============================================================================
 **/
public class RightMeshDataSource extends BaseRmDataSource {

    @SuppressLint("StaticFieldLeak")
    private static RightMeshDataSource rightMeshDataSource;

    private List<String> userIds;

    protected RightMeshDataSource(byte[] profileInfo) {
        super(App.getContext(), profileInfo);

        userIds = new ArrayList<>();
    }

    public static RightMeshDataSource getRmDataSource() {
        if (rightMeshDataSource == null) {
            Context context = TeleMeshApplication.getContext();

            RMUserModel rmUserMe = RMUserModel.newBuilder()
                    .setUserFirstName(SharedPref.getSharedPref(context).read(Constants.preferenceKey.FIRST_NAME))
                    .setUserLastName(SharedPref.getSharedPref(context).read(Constants.preferenceKey.LAST_NAME))
                    .setImageIndex(SharedPref.getSharedPref(context).readInt(Constants.preferenceKey.IMAGE_INDEX))
                    .build();


            byte[] bytes = rmUserMe.toByteArray();
            rightMeshDataSource = new RightMeshDataSource(bytes);
        }
        return rightMeshDataSource;
    }

    @Override
    protected void onRmOn() {
        super.onRmOn();

        //when RM will be on then prepare this observer to listen the outgoing messages
        RmDataHelper.getInstance().prepareDataObserver();
    }

    /**
     * During send data to peer
     *
     * @param rmDataModel -> A generic data model which contains userData, type and peerId
     * @return return the send message id
     */
    public int DataSend(RMDataModel rmDataModel) {

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
    protected void onPeer(BaseMeshData profileInfo) {

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
    protected void onPeerGone(MeshPeer meshPeer) {

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
    protected void onData(MeshData meshData) {
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
    protected void onAcknowledgement(MeshAcknowledgement meshAcknowledgement) {

        RMDataModel rmDataModel = RMDataModel.newBuilder()
                .setUserMeshId(meshAcknowledgement.mMeshPeer.getPeerId())
                .setRecDataId(meshAcknowledgement.id)
                .build();

        RmDataHelper.getInstance().ackReceive(rmDataModel);
    }

    @Override
    public void onRmOff() {
        super.onRmOff();
    }
}
