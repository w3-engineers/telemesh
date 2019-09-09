package com.w3engineers.unicef.telemesh.data.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.ext.viper.application.data.local.BaseMeshDataSource;
import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.mesh.util.HandlerUtil;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.broadcast.BroadcastManager;
import com.w3engineers.unicef.telemesh.data.broadcast.SendDataTask;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;

import java.util.List;

//import com.w3engineers.unicef.telemesh.TeleMeshUser.RMDataModel;

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

    MeshDataSource(@NonNull byte[] profileInfo) {
        super(App.getContext(), profileInfo);
        broadcastManager = BroadcastManager.getInstance();
    }

    @NonNull
    static MeshDataSource getRmDataSource() {
        if (rightMeshDataSource == null) {
            Context context = TeleMeshApplication.getContext();

            SharedPref sharedPref = SharedPref.getSharedPref(context);

            UserModel userModel = new UserModel()
                    .setName(sharedPref.read(Constants.preferenceKey.USER_NAME))
                    .setImage(sharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX))
                    .setTime(sharedPref.readLong(Constants.preferenceKey.MY_REGISTRATION_TIME));

            String userString = new Gson().toJson(userModel);

            byte[] bytes = userString.getBytes();
            rightMeshDataSource = new MeshDataSource(bytes);
        }
        return rightMeshDataSource;
    }

    @Override
    protected void onRmOn() {
        //when RM will be on then prepare this observer to listen the outgoing messages
        RmDataHelper.getInstance().prepareDataObserver();

        Constants.IsMeshInit = true;
        SharedPref.getSharedPref(TeleMeshApplication.getContext()).write(Constants.preferenceKey.MY_USER_ID, getMyMeshId());
    }

    /**
     * During send data to peer
     *
     * @param dataModel -> A generic data model which contains userData, type and peerId
     * @return return the send message id
     */
    public void DataSend(@NonNull DataModel dataModel, @NonNull String receiverId) {

        DataModel rmDataModel = dataModel.setUserId(receiverId);

        MeshData meshData = new MeshData();
        meshData.mType = rmDataModel.getDataType();
        meshData.mData = rmDataModel.getRawData();
        meshData.mMeshPeer = new MeshPeer(rmDataModel.getUserId());

        broadcastManager.addBroadCastMessage(getMeshDataTask(meshData));
    }

    public void DataSend(@NonNull DataModel rmDataModelBuilder, @NonNull List<String> receiverIds) {
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

            String userString = new String(profileInfo.mData);
            UserModel userModel = new Gson().fromJson(userString, UserModel.class);

            if (userModel != null) {
                userModel.setUserId(userId);
                HandlerUtil.postBackground(() -> RmDataHelper.getInstance().userAdd(userModel));
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

        HandlerUtil.postBackground(() -> RmDataHelper.getInstance().userLeave(meshPeer));
    }

    /**
     * This api execute during we receive data from network
     *
     * @param meshData -> Contains data and peer info also
     */
    @Override
    protected void onData(@NonNull MeshData meshData) {
        DataModel rmDataModel = new DataModel()
                .setUserId(meshData.mMeshPeer.getPeerId())
                .setRawData(meshData.mData)
                .setDataType(meshData.mType);

        HandlerUtil.postBackground(()-> RmDataHelper.getInstance().dataReceive(rmDataModel, true));
    }

    /**
     * The sending data status is success this time we got a success ack using this api
     *
     * @param meshAcknowledgement -> Contains the success data id and user id
     */
    @Override
    protected void onAcknowledgement(@NonNull MeshAcknowledgement meshAcknowledgement) {

        DataModel rmDataModel = new DataModel()
                .setDataTransferId(meshAcknowledgement.id)
                .setAckSuccess(meshAcknowledgement.isSuccess);

        HandlerUtil.postBackground(()-> RmDataHelper.getInstance().ackReceive(rmDataModel));
    }

    @Override
    @NonNull
    protected String getOwnUserId() {
        return SharedPref.getSharedPref(TeleMeshApplication.getContext()).read(Constants.preferenceKey.MY_USER_ID);
    }

    @Override
    protected boolean isNodeAvailable(String nodeId, int userActiveStatus) {
        return RmDataHelper.getInstance().userExistedOperation(nodeId, userActiveStatus);
    }

    @Override
    protected void showLog(String log) {
        RmDataHelper.getInstance().showMeshLog(log);
    }

    @Override
    protected void nodeIdDiscovered(String nodeId) {
        RmDataHelper.getInstance().onlyNodeAdd(nodeId);
    }

    @Override
    protected void onRmOff() {
        RmDataHelper.getInstance().stopMeshService();
    }

    /**
     * For ReInitiating RM service need to reset rightmesh data source instance
     */
    protected void resetMeshService() {
//        restartMesh();
        rightMeshDataSource = null;
    }
}
