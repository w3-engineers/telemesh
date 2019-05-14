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

import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.mesh.wifi.TransportManager;
import com.w3engineers.mesh.wifi.dispatch.LinkStateListener;
import com.w3engineers.mesh.wifi.protocol.Link;
import com.w3engineers.mesh.wifi.protocol.MeshTransport;

import java.util.HashMap;

public class MeshProvider implements LinkStateListener {

    private static MeshProvider meshProvider;

    private Context context;
    private HashMap<String, Link> peerLinkMap;
    private ProviderCallback providerCallback;
    private MeshTransport meshTransPort;

    private MeshConfig config;
    private byte[] myProfileInfo;
    private String myUserId;

    private MeshProvider(Context context) {
        this.context = context;
        peerLinkMap = new HashMap<>();
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

    public void setMyUserId(String myUserId) {
        this.myUserId = myUserId;
    }

    public void setProviderCallback(ProviderCallback providerCallback) {
        this.providerCallback = providerCallback;
    }

    /**
     * Start the mesh communication process
     */
    public void startMesh() {
        if (config == null || TextUtils.isEmpty(myUserId) || myProfileInfo == null)
            return;

        MeshDataManager.getInstance().setMyProfileInfo(myProfileInfo).setMyPeerId(myUserId);

        meshTransPort = TransportManager.on(context)
                .configTransport(config.mPort, myUserId, this);

        if (meshTransPort != null) {
            meshTransPort.start();

            if (providerCallback != null)
                providerCallback.meshStart();
        }
    }

    public void stopMesh() {
        if (meshTransPort != null) {
            meshTransPort.stop();

            if (providerCallback != null)
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
    public void linkConnected(String nodeId, Link link) {
        peerDiscovered(nodeId, link);
    }

    @Override
    public void onMeshLinkFound(String nodeId, Link link) {
        peerDiscovered(nodeId, link);
    }

    private void peerDiscovered(String nodeId, Link link) {

        if (!TextUtils.isEmpty(nodeId) && nodeId.equals(myUserId))
            return;

        if (!peerLinkMap.containsKey(nodeId)) {
            peerLinkMap.put(nodeId, link);
            sendMyInfo(nodeId, link);
        }
    }

    /**
     * Send my info after discovering him
     * @param nodeId - The discovered node id
     * @param link - Connection info between us
     */
    private void sendMyInfo(String nodeId, Link link) {

        MeshData meshData = MeshDataManager.getInstance().getMyProfileMeshData();

        if (meshData != null) {
            meshData.mPeerId = myUserId;
            link.sendFrame(nodeId, myUserId, MeshData.getMeshData(meshData));
        }
    }

    /**
     * When a node id or peer link is removed we get those callback
     * @param link connection object to disconnected device
     */
    @Override
    public void linkDisconnected(Link link) {
        peerRemoved(link.getNodeId());
    }

    @Override
    public void onMeshLinkDisconnect(String nodeId) {
        peerRemoved(nodeId);
    }

    private void peerRemoved(String peerId) {
        peerLinkMap.remove(peerId);
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
                Link link = peerLinkMap.get(peerId);
                if (link != null) {
                    meshData.mPeerId = myUserId;
                    return link.sendFrame(peerId, myUserId, MeshData.getMeshData(meshData));
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

            if (meshData != null) {

                String peerId = meshData.mPeerId;
                meshData.mMeshPeer = new MeshPeer(peerId);

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
        if (providerCallback != null)
            providerCallback.receiveAck(new MeshAcknowledgement(messageId));
    }

}