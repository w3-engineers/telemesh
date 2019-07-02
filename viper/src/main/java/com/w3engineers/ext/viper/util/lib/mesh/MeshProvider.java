package com.w3engineers.ext.viper.util.lib.mesh;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.content.Context;
import android.text.TextUtils;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.mesh.TransportManager;
import com.w3engineers.mesh.TransportState;
import com.w3engineers.mesh.db.SharedPref;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.mesh.util.HandlerUtil;
import com.w3engineers.mesh.wifi.dispatch.LinkStateListener;
import com.w3engineers.mesh.wifi.protocol.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MeshProvider implements LinkStateListener {

    private static MeshProvider meshProvider;

    private Context context;
    private List<String> userIds;
    private ProviderCallback providerCallback;
    private TransportManager transportManager;

    private MeshConfig config;
    private byte[] myProfileInfo;
    private String myUserId;
    private String NETWORK_PREFIX = "telemeshApp-";

    private MeshProvider(Context context) {
        this.context = context;
        userIds = new ArrayList<>();
    }

    public static MeshProvider getInstance(Context context) {
        if (meshProvider == null) {
            meshProvider = new MeshProvider(context);
        }
        return meshProvider;
    }

    public void setConfig(MeshConfig config) {
        this.config = config;
    }

    public void setMyProfileInfo(byte[] myProfileInfo) {
        this.myProfileInfo = myProfileInfo;
    }

    public void setProviderCallback(ProviderCallback providerCallback) {
        this.providerCallback = providerCallback;
    }

    /**
     * Start the mesh communication process
     */
    public void startMesh() {
        if (config == null || myProfileInfo == null)
            return;

        transportManager = TransportManager.on(App.getContext(), NETWORK_PREFIX, this);
    }



    public void stopMesh(boolean isStopProcess) {
        if (transportManager != null) {
            transportManager.stopMesh();

            if (providerCallback != null && isStopProcess)
                providerCallback.meshStop();
        }
    }

    public interface ProviderCallback {

        void meshStart();

        void connectionAdd(MeshData meshData);

        void connectionRemove(MeshPeer meshPeer);

        void receiveData(MeshData meshData);

        void receiveAck(MeshAcknowledgement meshAcknowledgement);

        void meshStop();
    }

    @Override
    public void onTransportInit(String nodeId, String publicKey, TransportState transportState, String msg) {
        boolean isSuccess = transportState == TransportState.SUCCESS;
        if (isSuccess) {
            SharedPref.write(Constant.KEY_USER_ID, nodeId);
            myUserId = nodeId;

            MeshDataManager.getInstance().setMyProfileInfo(myProfileInfo).setMyPeerId(myUserId);

            transportManager.configTransport(nodeId, publicKey, config.mPort, myProfileInfo);

            if (transportManager != null) {
                transportManager.startMesh();
            }
//            Paylib.getInstance(App.getContext());
        }
        if (providerCallback != null)
            providerCallback.meshStart();
    }

    @Override
    public void linkConnected(String nodeId) {
        peerDiscovered(nodeId);
    }

    @Override
    public void onMeshLinkFound(String nodeId) {
        peerDiscovered(nodeId);
    }

    private void peerDiscovered(String nodeId) {

        if (!TextUtils.isEmpty(nodeId) && nodeId.equals(myUserId))
            return;

        if (!userIds.contains(nodeId)) {
            sendMyInfo(nodeId);
            userIds.add(nodeId);
        }
    }

    /**
     * Send my info after discovering him
     * @param nodeId - The discovered node id
     */
    private void sendMyInfo(String nodeId) {
        HandlerUtil.postBackground(()-> {
            MeshData meshData = MeshDataManager.getInstance().getMyProfileMeshData();

            if (meshData != null) {
                meshData.mPeerId = myUserId;
                transportManager.sendMessage(nodeId, myUserId, MeshData.getMeshData(meshData));
//                link.sendFrame(nodeId, myUserId, MeshData.getMeshData(meshData));
            }
        });
    }

    /**
     * When a node id or peer link is removed we get those callback
     */
    @Override
    public void linkDisconnected(String nodeId) {
        peerRemoved(nodeId);
    }

    @Override
    public void onMeshLinkDisconnect(String nodeId) {
        peerRemoved(nodeId);
    }

    private void peerRemoved(String peerId) {
        userIds.remove(peerId);
        if (providerCallback != null)
            providerCallback.connectionRemove(new MeshPeer(peerId));
    }

    /**
     * By default data sent from Remote Service Binder thread
     * @param meshData - message send
     * @return - get the message send id
     */
    public long sendMeshData(MeshData meshData) {
        if (meshData != null) {
            String peerId = meshData.mMeshPeer.getPeerId();
            if (!TextUtils.isEmpty(peerId)) {
                meshData.mPeerId = myUserId;

                if (transportManager != null) {
                    return transportManager.sendMessage(peerId, myUserId, MeshData.getMeshData(meshData));
                }
            }
        }
        return -1L;
    }

    /**
     * When any kind of message data we received
     * @param msgOwner - Get my id
     * @param frameData frame data received from remote device
     */
    @Override
    public void linkDidReceiveFrame(String msgOwner, byte[] frameData) {
        if (frameData != null) {

            MeshData meshData = MeshData.setMeshData(frameData);

            if (meshData != null && meshData.mData != null) {

//                String peerId = msgOwner;
                meshData.mMeshPeer = new MeshPeer(msgOwner);

                if (MeshDataManager.getInstance().isProfileData(meshData)) {
                    if (providerCallback != null) {
                        providerCallback.connectionAdd(meshData);
                    }
                } else {
                    if (providerCallback != null) {
                        providerCallback.receiveData(meshData);
                    }
                }
            }
        }
    }

    /**
     * After successful deliver a frame we get message delivery id
     * @param messageId : Long message sent id
     * @param isSuccess : boolean status true of success false otherwise
     */
    @Override
    public void onMessageDelivered(long messageId, boolean isSuccess) {
        if (providerCallback != null) {
            MeshAcknowledgement meshAcknowledgement = new MeshAcknowledgement(messageId).setSuccess(isSuccess);
            providerCallback.receiveAck(meshAcknowledgement);
        }
    }

    public String getMyUserId() {
        return myUserId;
    }

}