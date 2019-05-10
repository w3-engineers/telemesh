package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.mesh.wifi.TransportManager;
import com.w3engineers.mesh.wifi.dispatch.LinkStateListener;
import com.w3engineers.mesh.wifi.protocol.Link;
import com.w3engineers.mesh.wifi.protocol.MeshTransport;
import com.w3engineers.unicef.telemesh.R;

import java.util.HashMap;

public abstract class MeshLibDataSource implements LinkStateListener  {

    private Context context;
    private byte[] myProfileInfo;

    private String ownerId;
    private MeshTransport meshTransPort;
    private HashMap<String, Link> peerLinkMap;

    protected MeshLibDataSource(Context context, byte[] profileInfo) {
        this.context = context;
        this.myProfileInfo = profileInfo;

        peerLinkMap = new HashMap<>();

        ownerId = getOwnUserId();
        int port = context.getResources().getInteger(R.integer.rm_port_number);

        MeshLibManager.getInstance().setMyProfileInfo(myProfileInfo).setMyPeerId(ownerId);

        meshTransPort = TransportManager.on(App.getContext())
                .configTransport(port, ownerId, this);

        startMesh();
    }

    protected abstract void onRmOn();

    protected abstract String getOwnUserId();

    protected abstract void onPeer(@NonNull BaseMeshData profileInfo);

    protected abstract void onPeerGone(@NonNull MeshPeer meshPeer);

    protected abstract void onData(@NonNull MeshData meshData);

    protected abstract void onAcknowledgement(@NonNull MeshAcknowledgement meshAcknowledgement);

    private void startMesh() {
        if (meshTransPort != null) {
            meshTransPort.start();
            onRmOn();
        }
    }

    public void stopMesh() {
        if (meshTransPort != null) {
            meshTransPort.stop();
        }
    }

    // Peer added +++++++++++++++++++++++++++++++
    @Override
    public void linkConnected(String nodeId, Link link) {
        peerDiscovered(nodeId, link);
    }

    @Override
    public void onMeshLinkFound(String nodeId, Link link) {
        peerDiscovered(nodeId, link);
    }

    private void peerDiscovered(String nodeId, Link link) {

        if (!TextUtils.isEmpty(nodeId) && nodeId.equals(ownerId))
            return;

        if (!peerLinkMap.containsKey(nodeId)) {
            peerLinkMap.put(nodeId, link);
            sendMyInfo(nodeId, link);
        }
    }

    private void sendMyInfo(String nodeId, Link link) {

        MeshData meshData = MeshLibManager.getInstance().getMyProfileMeshData();

        if (meshData != null) {
            meshData.mPeerId = ownerId;
            link.sendFrame(nodeId, ownerId, MeshData.getMeshData(meshData));
        }
    }

    // Peer added -------------------------------

    // Peer removed +++++++++++++++++++++++++++++

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
        onPeerGone(new MeshPeer(peerId));
    }

    // Peer removed -----------------------------

    protected long sendMeshData(MeshData meshData) {
        if (meshData != null) {
            String peerId = meshData.mMeshPeer.getPeerId();
            if (!TextUtils.isEmpty(peerId)) {
                Link link = peerLinkMap.get(peerId);
                if (link != null) {
                    meshData.mPeerId = ownerId;
                    return link.sendFrame(peerId, ownerId, MeshData.getMeshData(meshData));
                }
            }
        }
        return -1L;
    }

    @Override
    public void linkDidReceiveFrame(String msgOwner, byte[] frameData) {

        if (frameData != null) {

            MeshData meshData = MeshData.setMeshData(frameData);

            if (meshData != null) {

                String peerId = meshData.mPeerId;
                meshData.mMeshPeer = new MeshPeer(peerId);

                if (MeshLibManager.getInstance().isProfileData(meshData)) {
                    onPeer(meshData);
                } else {
                    onData(meshData);
                }
            }
        }
    }

    @Override
    public void onMessageDelivered(long messageId, boolean isSuccess) {
        onAcknowledgement(new MeshAcknowledgement(messageId));
    }
}
