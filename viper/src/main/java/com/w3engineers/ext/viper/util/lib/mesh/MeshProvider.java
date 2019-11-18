package com.w3engineers.ext.viper.util.lib.mesh;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.viper.BuildConfig;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.mesh.TransportManager;
import com.w3engineers.mesh.TransportState;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.mesh.util.HandlerUtil;
import com.w3engineers.mesh.wifi.dispatch.LinkStateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MeshProvider implements LinkStateListener {

    private static MeshProvider meshProvider;

    private Context context;

    private ProviderCallback providerCallback;
    private TransportManager transportManager;

    private MeshConfig config;
    private byte[] myProfileInfo;
    private String myUserId;
    //BT NAME
    /*private String WIFI_PREFIX = "bna.1";
    private String BLE_PREFIX = "que";*/

    private String NETWORK_PREFIX = "meso";

    private MeshProvider() {
        this.context = App.getContext();
    }

    public static MeshProvider getInstance() {
        if (meshProvider == null) {
            meshProvider = new MeshProvider();
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

    public int getMyCurrentMode() {
        if (transportManager != null && providerCallback != null) {
            return transportManager.getUserMode();
        }

        return 0;
    }

    /**
     * Start the mesh communication process
     */
    public void startMesh() {
        if (config == null || myProfileInfo == null)
            return;

//        setLogBroadcastRegister();
        transportManager = TransportManager.on(App.getContext(), App.getContext(), NETWORK_PREFIX, BuildConfig.MULTIVERSE_URL, this);
    }


    public void stopMesh(boolean isStopProcess) {
        if (transportManager != null) {
            transportManager.stopMesh();

            if (providerCallback != null && isStopProcess) {
//                setLogBroadcastUnregister();
                providerCallback.meshStop();
            }
        }
    }

    public void restartMesh() {
        if (transportManager != null) {
            transportManager.restart();
        }

    }

    public interface ProviderCallback {

        void meshStart();

        void connectionAdd(MeshData meshData);

        void connectionRemove(MeshPeer meshPeer);

        void receiveData(MeshData meshData);

        void receiveAck(MeshAcknowledgement meshAcknowledgement);

        void meshStop();

        boolean isNodeExist(String nodeId, int activeStatus);

        void showMeshLog(String log);

        void onlyNodeDiscover(String nodeId);

        void onGetUserMode(int userMode);
    }

    @Override
    public void onTransportInit(String nodeId, String publicKey, TransportState transportState, String msg) {
        boolean isSuccess = transportState == TransportState.SUCCESS;
        if (isSuccess) {
            //SharedPref.write(Constant.KEY_USER_ID, nodeId);
            myUserId = nodeId;

            MeshDataManager.getInstance().setMyProfileInfo(myProfileInfo).setMyPeerId(myUserId);

            Log.v("MIMO_SAHA::", "Length: " + myProfileInfo.length + " Id: " + nodeId);

            MeshData meshData = MeshDataManager.getInstance().getMyProfileMeshData();
            if (meshData == null)
                return;

            String profileData = MeshDataProcessor.getInstance().getStringDataFormatToJson(meshData);

            transportManager.configTransport(nodeId, publicKey, config.mPort, profileData);

            if (transportManager != null) {
                transportManager.startMesh();
            }
//            Paylib.getInstance(App.getContext());
        }
        if (providerCallback != null)
            providerCallback.meshStart();
    }

    @Override
    public void onLocalUserConnected(String nodeId, byte[] frameData) {
        Log.d("MeshSdkIntegration", "onLocalUserConnected call");
        if (frameData != null) {
            MeshData meshData = MeshDataProcessor.getInstance().setDataFormatFromJson(frameData);

            if (meshData != null) {
                Log.d("MeshSdkIntegration", "mesh data not null");
                meshData.mMeshPeer = new MeshPeer(nodeId);

                if (MeshDataManager.getInstance().isProfileData(meshData)) {
                    Log.d("MeshSdkIntegration", "profile data call");
                    if (providerCallback != null) {
                        providerCallback.connectionAdd(meshData);
                    }
                }
            }
        }
    }

    @Override
    public void onRemoteUserConnected(String nodeId) {
        Log.d("MeshSdkIntegration", "onRemoteUserConnected call");
        peerDiscoveryProcess(nodeId, true);
    }

    @Override
    public void onUserDisconnected(String nodeId) {
        peerDiscoveryProcess(nodeId, false);
    }

    @Override
    public void onMessageReceived(String senderId, byte[] frameData) {
        Log.d("MeshSdkIntegration", "onMessageReceived call");
        if (frameData != null) {

            MeshData meshData = MeshDataProcessor.getInstance().setDataFormatFromJson(frameData);

            if (meshData != null) {

                meshData.mMeshPeer = new MeshPeer(senderId);

                if (MeshDataManager.getInstance().isProfilePing(meshData)) {
                    myProfileSend(senderId);

                } else if (MeshDataManager.getInstance().isProfileData(meshData)) {
                    if (providerCallback != null) {
                        providerCallback.connectionAdd(meshData);
                    }
                } else {
                    if (meshData.mData != null && providerCallback != null) {
                        providerCallback.receiveData(meshData);
                    }
                }
            }
        }
    }

    @Override
    public void onModeChange(int userMode) {
        // Here Service will be restart
        if (providerCallback != null) {
            providerCallback.onGetUserMode(userMode);
        }


    }

    private void peerDiscoveryProcess(String nodeId, boolean isActive) {

        HandlerUtil.postBackground(() -> {
            // If you send data directly without ping then enable this api
            // and after the following lines should be comment out

            /*if (providerCallback != null && isActive) {
                providerCallback.onlyNodeDiscover(nodeId);
            }*/

//            directSend(nodeId, isActive);

            boolean isUserExist = false;

            if (providerCallback != null) {
                int userConnectivityStatus = isActive ? getUserActiveStatus(nodeId) : 0;
                isUserExist = providerCallback.isNodeExist(nodeId, userConnectivityStatus);
            }

            if (!isUserExist) {
                if (isActive) {
                    pingedNodeId(nodeId);
                } else {
                    peerRemoved(nodeId);
                }
            }
        });
    }

    private void directSend(String nodeId, boolean isActive) {
        if (isActive) {
            sendMyInfo(nodeId);
        } else {
            peerRemoved(nodeId);
        }
    }

    private void pingedNodeId(String nodeId) {
        if (!TextUtils.isEmpty(nodeId) && nodeId.equals(myUserId))
            return;

        sendProfilePing(nodeId);
    }

    private void sendProfilePing(String nodeId) {
        MeshData meshData = MeshDataManager.getInstance().getPingForProfile();

        if (meshData != null) {
            String sendId = UUID.randomUUID().toString();
            sendDataToMesh(nodeId, meshData, sendId);
        }
    }

    private void myProfileSend(String nodeId) {

        if (!TextUtils.isEmpty(nodeId) && nodeId.equals(myUserId))
            return;

        HandlerUtil.postBackground(() -> {
            sendMyInfo(nodeId);
        });
    }


    /**
     * Send my info after discovering him
     *
     * @param nodeId - The discovered node id
     */
    private void sendMyInfo(String nodeId) {

        MeshData meshData = MeshDataManager.getInstance().getMyProfileMeshData();

        if (meshData != null) {
            String sendId = UUID.randomUUID().toString();
            sendProfileDataToMesh(nodeId, meshData, sendId);
        }
    }

    private void sendProfileDataToMesh(String nodeId, MeshData meshData, String sendId) {
        byte[] data = MeshDataProcessor.getInstance().getDataFormatToJson(meshData);
        transportManager.sendMessage(myUserId, nodeId, sendId, data);
    }

    private void sendDataToMesh(String nodeId, MeshData meshData, String sendId) {
        byte[] data = MeshDataProcessor.getInstance().getDataFormatToJson(meshData);
        transportManager.sendMessage(myUserId, nodeId, sendId, data);
    }


    private void peerRemoved(String peerId) {
        if (providerCallback != null)
            providerCallback.connectionRemove(new MeshPeer(peerId));
    }

    /**
     * By default data sent from Remote Service Binder thread
     *
     * @param meshData - message send
     * @return - get the message send id
     */
    public String sendMeshData(MeshData meshData) {
        if (meshData != null) {
            String peerId = meshData.mMeshPeer.getPeerId();
            if (!TextUtils.isEmpty(peerId)) {

                if (transportManager != null) {
                    String sendId = UUID.randomUUID().toString();
                    sendDataToMesh(peerId, meshData, sendId);
                    return sendId;
                }
            }
        }
        return null;
    }


    /**
     * After successful deliver a frame we get message delivery id
     *
     * @param messageId : Long message sent id
     * @param status    : boolean status true of success false otherwise
     */
    @Override
    public void onMessageDelivered(String messageId, int status) {
        if (providerCallback != null) {

            if (status == Constant.MessageStatus.SEND
                    || status == Constant.MessageStatus.DELIVERED
                    || status == Constant.MessageStatus.RECEIVED) {
                MeshAcknowledgement meshAcknowledgement = new MeshAcknowledgement(messageId)
                        .setSuccess(true).setStatus(status);
                providerCallback.receiveAck(meshAcknowledgement);
            }
        }
    }

    public List<String> getAllSellers() {
        if (transportManager != null) {
            return transportManager.getInternetSellers();
        }
        return new ArrayList<>();
    }

    public String getMyUserId() {
        return myUserId;
    }

    private long getDataTransferId() {
        return System.currentTimeMillis();
    }

    public int getUserActiveStatus(String userId) {
        if (transportManager != null) {
            return transportManager.getLinkTypeById(userId);
        }
        return 0;
    }

    private BroadcastReceiver logBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals("com.w3engineers.meshrnd.DEBUG_MESSAGE")) {
                    String text = intent.getStringExtra("value");
                    showMeshLog(text);
                }
            }
        }
    };

    private void setLogBroadcastRegister() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.w3engineers.meshrnd.DEBUG_MESSAGE");
        context.registerReceiver(logBroadcastReceiver, intentFilter);
    }

    private void setLogBroadcastUnregister() {
        try {
            context.unregisterReceiver(logBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMeshLog(String meshLog) {
        if (providerCallback != null) {
            providerCallback.showMeshLog(meshLog);
        }
    }
}