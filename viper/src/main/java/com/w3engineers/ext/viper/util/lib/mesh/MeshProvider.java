package com.w3engineers.ext.viper.util.lib.mesh;

import android.content.Context;
import android.text.TextUtils;

import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.id.MeshId;
import io.left.rightmesh.mesh.MeshManager;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.RightMeshException;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2018-08-07 at 1:56 PM].
 * <br>Email: azim@w3engineers.com
 * <br>----------------------------------------------------------------------------
 * <br>Project: android-framework.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2018-08-07 at 1:56 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2018-08-07 at 1:56 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

// FIXME: 8/13/2018 Probably it has an issue of receiving selfPeer. need more check. If so counter measure is simple
public class MeshProvider implements MeshStateListener {

    protected final int DEFAULT_ACK_ID = -1;
    private static final MeshProvider ourInstance = new MeshProvider();

    public static MeshProvider getInstance() {
        return ourInstance;
    }

    private AndroidMeshManager mAndroidMeshManager;
    private MeshConfig mMeshConfig;
    private transient boolean mIsRunning;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private IMeshCallBack mIMeshCallBack;


    private final byte[] PROFILE_DATA_DEFAULT_BYTES;
    /**
     * All active peer id with it's byte data.
     */
    private final ConcurrentHashMap<String, byte[]> mMeshIdPeerMap;
    private final Set<Integer> mDeliveryIdSetMessage;
    private final Set<Integer> mDeliveryIdSetPi;

    private MeshProvider() {

        PROFILE_DATA_DEFAULT_BYTES = new byte[]{1, 2};//default byte array
        mMeshIdPeerMap = new ConcurrentHashMap<>(3);
        mDeliveryIdSetMessage = Collections.synchronizedSet(new HashSet<>());
        mDeliveryIdSetPi = Collections.synchronizedSet(new HashSet<>());

    }

    public void setProfileInfo(byte[] profileInfo) {
        ProfileManager.getInstance().setProfileInfo(profileInfo);
    }

    public void setIMeshCallBack(IMeshCallBack iMeshCallBack) {

        this.mIMeshCallBack = iMeshCallBack;

    }

    public void setMeshConfig(MeshConfig meshConfig) {
        this.mMeshConfig = meshConfig;
    }

    public void start(Context context) {

        if(context == null) {
            throw new NullPointerException("set a valid context");
        }

        if(mMeshConfig == null || mMeshConfig.mPort == 0) {
            throw new IllegalStateException("Please set port number through configuration");
        }

        //If either super peer or ssid then dev version otherwise production version
        mAndroidMeshManager = TextUtils.isEmpty(mMeshConfig.mSsid) && TextUtils.isEmpty(mMeshConfig.mSuperPeer) ?
                AndroidMeshManager.getInstance(context, this) :
                AndroidMeshManager.getInstance(context, this, mMeshConfig.mSuperPeer,
                        mMeshConfig.mSsid);

    }

    public void stop() {

        if(mAndroidMeshManager != null) {
            mAndroidMeshManager.unregisterAllPeerListener(mMeshConfig.mPort);
            mIsRunning = false;
            mMeshConfig = null;
            mCompositeDisposable.dispose();

            try {

                mAndroidMeshManager.stop();

            }  catch (RightMeshException.RightMeshServiceDisconnectedException e) {
                e.printStackTrace();
            }
        }

    }

    public List<BaseMeshData> getLivePeers() {

        List<BaseMeshData> meshPeerList = new ArrayList<>();


        BaseMeshData baseMeshData;
        byte[] data;
        for (String meshId : mMeshIdPeerMap.keySet()) {//By default ConcurrentHashMap does not allow null
            data = mMeshIdPeerMap.get(meshId);
            if(PROFILE_DATA_DEFAULT_BYTES.equals(data)) {//As we always use  the same object
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

    public int sendProfileInfo(MeshData meshData) {

        int ackId = DEFAULT_ACK_ID;
        try {
            ackId = send(meshData);
            mDeliveryIdSetPi.add(ackId);
        } catch (RightMeshException e) {
            e.printStackTrace();
        }

        return ackId;
    }

    public int sendData(MeshData meshData) {

        int ackId = DEFAULT_ACK_ID;
        try {
            ackId = send(meshData);
            mDeliveryIdSetMessage.add(ackId);
        } catch (RightMeshException e) {
            e.printStackTrace();
        }

        return ackId;
    }

    /**
     * By default data sent from Remote Service Binder thread
     * @param meshData
     * @return
     * @throws RightMeshException
     */
    private int send(MeshData meshData) throws RightMeshException {

        if(mAndroidMeshManager == null) {
            throw new IllegalStateException("Mesh library not initialized");
        }

        if(meshData == null || meshData.mMeshPeer == null || meshData.mMeshPeer.getPeerId() == null
                || meshData.mData == null) {
            throw new NullPointerException("Mesh Data not initialized properly");
        }

        MeshId meshId = MeshId.fromString(meshData.mMeshPeer.getPeerId());
        byte[] bytes = MeshData.getMeshData(meshData);
        Timber.d("mAndroidMeshManager.sendDataReliable() calling with %s, %s and %s",
                meshId, mMeshConfig.mPort, Arrays.toString(bytes));
        int ackId = mAndroidMeshManager.sendDataReliable(meshId,
                mMeshConfig.mPort, bytes);
        Timber.d("mAndroidMeshManager.sendDataReliable() Ack %d. Thread::%s", ackId, Thread.currentThread().getName());

        return ackId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void meshStateChanged(MeshId meshId, int state) {

        if (state == MeshStateListener.SUCCESS || state == MeshStateListener.RESUME) {

            try {

                mAndroidMeshManager.bind(mMeshConfig.mPort);

            } catch (RightMeshException e) {
                e.printStackTrace();
                return;//no need to proceed further
            }

            Timber.d("Library activated successfully.");

            mIsRunning = true;

            // Subscribes handlers to receive events from the mesh.

            mCompositeDisposable.add(mAndroidMeshManager.on(MeshManager.PEER_CHANGED,
                    (Consumer) o -> handlePeerChanged((MeshManager.RightMeshEvent) o)));

            mCompositeDisposable.add(mAndroidMeshManager.on(MeshManager.DATA_RECEIVED,
                    (Consumer) o -> handleDataReceived((MeshManager.RightMeshEvent) o)));

            mCompositeDisposable.add(mAndroidMeshManager.on(MeshManager.DATA_DELIVERED,
                    (Consumer) o -> handleDataDelivery((MeshManager.RightMeshEvent) o)));

            if(mIMeshCallBack != null) {

                mIMeshCallBack.onInitSuccess(new MeshPeer(meshId.toString()));

            }

        } else if (state == MeshStateListener.DISABLED || state == MeshStateListener.FAILURE) {

            if(mIMeshCallBack != null) {

                mIMeshCallBack.onInitFailed(state);

            }
            stop();

        }

    }

    private void handlePeerChanged(MeshManager.RightMeshEvent e) {

        if(e != null) {

            MeshManager.PeerChangedEvent event = (MeshManager.PeerChangedEvent) e;

            // Ignore ourselves.
            if (event.peerUuid.equals(mAndroidMeshManager.getUuid())) {
                return;
            }

            //We normally accept only Added and Removed event. Reference: S. K. Paik
            if (event.state == MeshManager.REMOVED ||
                    event.state == MeshManager.ADDED || event.state == MeshManager.UPDATED) {

                String meshPeerId = event.peerUuid.toString();

                //Managing set of MeshId
                if(event.state == MeshManager.ADDED) {

                    Timber.d("%s Added", event.peerUuid);

                    if(!mMeshIdPeerMap.containsKey(meshPeerId)) {//If it was really able to add
                        mMeshIdPeerMap.put(meshPeerId, PROFILE_DATA_DEFAULT_BYTES);
                        ProfileManager.getInstance().sendMyProfileInfo(new MeshPeer(event.peerUuid.toString()));
                    }


                } else if(event.state == MeshManager.REMOVED) {

                    Timber.d("%s Removed", event.peerUuid);

                    mMeshIdPeerMap.remove(meshPeerId);//remove record

                    //Updating upper layer about removal
                    mIMeshCallBack.onPeerRemoved(new MeshPeer(meshPeerId));

                }
            }
        }

    }

    private void handleDataReceived(MeshManager.RightMeshEvent e) {

        MeshManager.DataReceivedEvent dataReceivedEvent = (MeshManager.DataReceivedEvent) e;
        MeshData meshData = MeshData.setMeshData(dataReceivedEvent.data);

        if(meshData != null) {

            //first check whether this is profile data or not
            //We would init switch case if more facility provides by framework (like file sending)
            byte[] profileData = ProfileManager.getInstance().processProfileData(meshData);

            if (profileData == null) {//not profile data

                Timber.d("Data received from::%s. Thread::%s", dataReceivedEvent.peerUuid,
                        Thread.currentThread().getName());
                meshData.mMeshPeer = MeshPeer.from(dataReceivedEvent.peerUuid);
                mIMeshCallBack.onMesh(meshData);

            } else {//profile data

                Timber.d("Profile data received from::%s. Thread::%s", dataReceivedEvent.peerUuid,
                        Thread.currentThread().getName());

                //last time check that the peer is not removed
                if(isPeerAvailable(dataReceivedEvent.peerUuid.toString())) {
                    String peerId = dataReceivedEvent.peerUuid.toString();

                    meshData.mMeshPeer = new MeshPeer(peerId);

                    //Keeping peer records with info
                    mMeshIdPeerMap.put(peerId, meshData.mData);

                    mIMeshCallBack.onProfileInfo(meshData);
                }
            }
        }

    }

    private void handleDataDelivery(MeshManager.RightMeshEvent e) {

        MeshManager.DataDeliveredEvent dataDeliveredEvent = (MeshManager.DataDeliveredEvent) e;

        if(dataDeliveredEvent != null) {
            Timber.d("Ack received for::%d", dataDeliveredEvent.data_id);

            if(!mDeliveryIdSetPi.remove(dataDeliveredEvent.data_id)) {

                if(mDeliveryIdSetMessage.remove(dataDeliveredEvent.data_id)) {
                    //Propagate event to higher level only if event is not profile info related event
                    //and available in message set

                    MeshAcknowledgement meshAcknowledgement = new MeshAcknowledgement(dataDeliveredEvent.data_id);
                    meshAcknowledgement.mMeshPeer = new MeshPeer(dataDeliveredEvent.peerUuid.toString());
                    mIMeshCallBack.onMesh(meshAcknowledgement);
                }

            }
        }

    }


    private boolean isPeerAvailable(String targetPeer) {

        if(!TextUtils.isEmpty(targetPeer)) {


            return mMeshIdPeerMap.containsKey(targetPeer);

        }
        return false;
    }

}
