package com.w3engineers.ext.viper.util.lib.mesh;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.mesh.settings.SettingsActivity;
import com.w3engineers.mesh.wifi.TransportManager;
import com.w3engineers.mesh.wifi.dispatch.LinkStateListener;
import com.w3engineers.mesh.wifi.protocol.Link;
import com.w3engineers.mesh.wifi.protocol.MeshTransport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MeshLibProvider implements LinkStateListener {

    private final long DEFAULT_ACK_ID = -1;
    private static MeshLibProvider meshLibProvider = new MeshLibProvider();

    private MeshConfig mMeshConfig;
    private MeshTransport meshTransPort;
    private String userId;

    private final byte[] PROFILE_DATA_DEFAULT_BYTES;
    private final ConcurrentHashMap<String, byte[]> mMeshIdPeerMap;
    private final Set<Long> mDeliveryIdSetMessage;
    private final Set<Long> mDeliveryIdSetPi;
    private Map<String, Link> connectedLinkMap;

    private IMeshCallBack mIMeshCallBack;

    public static MeshLibProvider getInstance() {
        return meshLibProvider;
    }

    private MeshLibProvider() {
        PROFILE_DATA_DEFAULT_BYTES = new byte[]{1, 2};
        mMeshIdPeerMap = new ConcurrentHashMap<>(3);
        mDeliveryIdSetMessage = Collections.synchronizedSet(new HashSet<>());
        mDeliveryIdSetPi = Collections.synchronizedSet(new HashSet<>());
        connectedLinkMap = new HashMap<>();
    }

    public void setProfileInfo(byte[] profileInfo) {
        ProfileManager.getInstance().setProfileInfo(profileInfo);
    }

    public void setMeshConfig(MeshConfig meshConfig) {
        this.mMeshConfig = meshConfig;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setIMeshCallBack(IMeshCallBack iMeshCallBack) {
        this.mIMeshCallBack = iMeshCallBack;
    }

    public void start() {
        meshTransPort = TransportManager.on(App.getContext())
                .configTransport(mMeshConfig.mPort, userId, this);

        if (meshTransPort != null) {
            meshTransPort.start();

            if (mIMeshCallBack != null) {
                mIMeshCallBack.onInitSuccess(new MeshPeer(userId));
            }
        }
    }

    public void stop() {
        if (meshTransPort != null) {
            meshTransPort.stop();
        }
    }

    public List<BaseMeshData> getLivePeers() {
        List<BaseMeshData> meshPeerList = new ArrayList<>();


        BaseMeshData baseMeshData;
        byte[] data;
        for (String meshId : mMeshIdPeerMap.keySet()) {//By default ConcurrentHashMap does not allow null
            data = mMeshIdPeerMap.get(meshId);
            if (PROFILE_DATA_DEFAULT_BYTES.equals(data)) {//As we always use  the same object
                continue;//As no profile data yet available
            }

            baseMeshData = new MeshData();

            //init data
            baseMeshData.mMeshPeer = new MeshPeer(meshId);
            baseMeshData.mData = mMeshIdPeerMap.get(meshId);

            meshPeerList.add(baseMeshData);
        }

        return meshPeerList;
    }

    @Override
    public void linkConnected(String nodeId, Link link) {
        peerAdd(nodeId, link);
    }

    @Override
    public void onMeshLinkFound(String nodeId, Link link) {
        peerAdd(nodeId, link);
    }

    private void peerAdd(String peerId, Link link) {
        connectedLinkMap.put(peerId, link);
        if (!mMeshIdPeerMap.containsKey(peerId)) {//If it was really able to add
            mMeshIdPeerMap.put(peerId, PROFILE_DATA_DEFAULT_BYTES);
            ProfileManager.getInstance().sendMyProfileInfo(new MeshPeer(peerId));
        }
    }

    @Override
    public void linkDisconnected(Link link) {
        peerRemove(link.getNodeId());
    }

    @Override
    public void onMeshLinkDisconnect(String nodeId) {
        peerRemove(nodeId);
    }

    private void peerRemove(String peerId) {
        connectedLinkMap.remove(peerId);
        mMeshIdPeerMap.remove(peerId);
        mIMeshCallBack.onPeerRemoved(new MeshPeer(peerId));
    }

    @Override
    public void linkDidReceiveFrame(String msgOwner, byte[] frameData) {
        handleDataReceived(frameData);
    }

    private void handleDataReceived(byte[] frameData) {


        MeshData meshData = MeshData.setMeshData(frameData);

        if(meshData != null) {

            //first check whether this is profile data or not
            //We would init switch case if more facility provides by framework (like file sending)
            byte[] profileData = ProfileManager.getInstance().processProfileData(meshData);

            if (profileData == null) {//not profile data

                mIMeshCallBack.onMesh(meshData);

            } else {//profile data

                //last time check that the peer is not removed

                String peerId = meshData.mMeshPeer.getPeerId();

                if(isPeerAvailable(peerId)) {

                    meshData.mMeshPeer = new MeshPeer(peerId);
                    //Keeping peer records with info
                    mMeshIdPeerMap.put(peerId, meshData.mData);

                    mIMeshCallBack.onProfileInfo(meshData);
                }
            }
        }

    }

    @Override
    public void onMessageDelivered(long messageId, boolean isSuccess) {
        handleDataDelivery(messageId);
    }

    private void handleDataDelivery(long messageId) {

        if(!mDeliveryIdSetPi.remove(messageId)) {

            if(mDeliveryIdSetMessage.remove(messageId)) {
                MeshAcknowledgement meshAcknowledgement = new MeshAcknowledgement(messageId);
                mIMeshCallBack.onMesh(meshAcknowledgement);
            }
        }
    }

    public long sendData(MeshData meshData) {

        long ackId = DEFAULT_ACK_ID;
        ackId = send(meshData);
        mDeliveryIdSetMessage.add(ackId);
        return ackId;
    }

    public long sendProfileInfo(MeshData meshData) {

        long ackId = DEFAULT_ACK_ID;
        ackId = send(meshData);
        mDeliveryIdSetPi.add(ackId);
        return ackId;
    }

    private long send(MeshData meshData) {

        if(meshData == null || meshData.mMeshPeer == null || meshData.mMeshPeer.getPeerId() == null
                || meshData.mData == null) {
            throw new NullPointerException("Mesh Data not initialized properly");
        }

        String receiverId = meshData.mMeshPeer.getPeerId();
        Link sendLink = connectedLinkMap.get(receiverId);

        if (sendLink == null || !sendLink.isConnected()) return DEFAULT_ACK_ID;

        byte[] bytes = MeshData.getMeshData(meshData);

        return sendLink.sendFrame(receiverId, userId, bytes);
    }

    private boolean isPeerAvailable(String targetPeer) {
        if(!TextUtils.isEmpty(targetPeer)) {
            return mMeshIdPeerMap.containsKey(targetPeer);
        }
        return false;
    }

    public void openRmSettings() {
        Context context = App.getContext();
        context.startActivity(new Intent(context, SettingsActivity.class));
    }
}
