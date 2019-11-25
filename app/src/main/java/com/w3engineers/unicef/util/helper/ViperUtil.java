package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.ApiEvent;
import com.w3engineers.mesh.application.data.AppDataObserver;
import com.w3engineers.mesh.application.data.local.dataplan.DataPlanManager;
import com.w3engineers.mesh.application.data.local.wallet.WalletManager;
import com.w3engineers.mesh.application.data.model.DataAckEvent;
import com.w3engineers.mesh.application.data.model.DataEvent;
import com.w3engineers.mesh.application.data.model.PeerAdd;
import com.w3engineers.mesh.application.data.model.PeerRemoved;
import com.w3engineers.mesh.application.data.model.TransportInit;
import com.w3engineers.mesh.application.data.model.UserInfoEvent;
import com.w3engineers.mesh.application.data.model.WalletLoaded;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.mesh.util.lib.mesh.ViperClient;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.model.ViperData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ViperUtil {

    private ViperClient viperClient;
    private String myUserId;
    private Context context;

    protected ViperUtil(UserModel userModel) {

        try {

            context = MainActivity.getInstance() != null ? MainActivity.getInstance() : TeleMeshApplication.getContext();
            String appName = context.getResources().getString(R.string.app_name);

            SharedPref sharedPref = SharedPref.getSharedPref(context);

            String jsonData = loadJSONFromAsset(context);

            if (!TextUtils.isEmpty(jsonData)) {
                JSONObject jsonObject = new JSONObject(jsonData);

                String AUTH_USER_NAME = jsonObject.optString("AUTH_USER_NAME");
                String AUTH_PASSWORD = jsonObject.optString("AUTH_PASSWORD");
                String APP_DOWNLOAD_LINK = jsonObject.optString("APP_DOWNLOAD_LINK");
                String GIFT_DONATE_LINK = jsonObject.optString("GIFT_DONATE_LINK");

                String address = sharedPref.read(Constants.preferenceKey.MY_WALLET_ADDRESS);
                String publicKey = sharedPref.read(Constants.preferenceKey.MY_PUBLIC_KEY);

                initObservers();

                viperClient = ViperClient.on(context, appName, "com.w3engineers.unicef.telemesh", "captor", userModel.getName(),
                        address, publicKey, userModel.getImage(), userModel.getTime(), true)
                        .setConfig(AUTH_USER_NAME, AUTH_PASSWORD, APP_DOWNLOAD_LINK, GIFT_DONATE_LINK);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initObservers() {

        AppDataObserver.on().startObserver(ApiEvent.TRANSPORT_INIT, event -> {
            TransportInit transportInit = (TransportInit) event;

            Log.v("MIMO_SAHA:", "Transport init");
            if (transportInit.success) {
                myUserId = transportInit.nodeId;

                onMesh(myUserId);
            }
        });

        AppDataObserver.on().startObserver(ApiEvent.WALLET_LOADED, event -> {
            WalletLoaded walletLoaded = (WalletLoaded) event;

            Log.v("MIMO_SAHA:", "Wallet loaded");

            if (walletLoaded.success) {
                myUserId = walletLoaded.walletAddress;
                onMeshPrepared(walletLoaded.walletAddress);
            }
        });

        AppDataObserver.on().startObserver(ApiEvent.PEER_ADD, event -> {
            PeerAdd peerAdd = (PeerAdd) event;
            peerDiscoveryProcess(peerAdd.peerId, true);
        });

        AppDataObserver.on().startObserver(ApiEvent.PEER_REMOVED, event -> {
            PeerRemoved peerRemoved = (PeerRemoved) event;
            peerDiscoveryProcess(peerRemoved.peerId, false);
        });

        AppDataObserver.on().startObserver(ApiEvent.DATA, event -> {

            DataEvent dataEvent = (DataEvent) event;

            dataReceive(dataEvent.peerId, dataEvent.data);
        });

        AppDataObserver.on().startObserver(ApiEvent.DATA_ACKNOWLEDGEMENT, event -> {

            DataAckEvent dataAckEvent = (DataAckEvent) event;

            onAck(dataAckEvent.dataId, dataAckEvent.status);

        });

        AppDataObserver.on().startObserver(ApiEvent.USER_INFO, event -> {

            UserInfoEvent userInfoEvent = (UserInfoEvent) event;

            UserModel userModel = new UserModel().setName(userInfoEvent.getUserName())
                    .setImage(userInfoEvent.getAvatar())
                    .setTime(userInfoEvent.getRegTime());

            peerAdd(userInfoEvent.getAddress(), userModel);
        });

    }

    private void peerDiscoveryProcess(String nodeId, boolean isActive) {
        HandlerUtil.postBackground(() -> {

            int userConnectivityStatus = isActive ? getUserActiveStatus(nodeId) : 0;
            boolean isUserExist = isNodeAvailable(nodeId, userConnectivityStatus);

            if (!isUserExist) {
                if (isActive) {
                    pingedNodeId(nodeId);
                } else {
                    peerRemove(nodeId);
                }
            }
        });
    }

    /*********************Ping*************************/

    private void pingedNodeId(String nodeId) {
        if (!TextUtils.isEmpty(nodeId) && nodeId.equals(myUserId))
            return;

        sendProfilePing(nodeId);
    }

    private void sendProfilePing(String nodeId) {
        ViperData viperData = ViperDataProcessor.getInstance().getPingForProfile();

        if (viperData != null) {
            String sendId = UUID.randomUUID().toString();
            sendDataToMesh(nodeId, viperData, sendId);
        }
    }

    /*********************Ping*************************/

    private void dataReceive(String senderId, byte[] frameData) {
        if (frameData != null) {

            ViperData viperData = ViperDataProcessor.getInstance().setDataFormatFromJson(frameData);

            if (viperData != null) {

                if (ViperDataProcessor.getInstance().isProfilePing(viperData)) {

                    myProfileSend(senderId);

                } else if (ViperDataProcessor.getInstance().isProfileData(viperData)) {

                    peerAdd(senderId, frameData);

                } else {

                    if (viperData.rawData != null) {
                        onData(senderId, viperData);
                    }
                }
            }
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

        ViperData viperData = ViperDataProcessor.getInstance().getMyProfileMeshData();

        if (viperData != null) {
            String sendId = UUID.randomUUID().toString();
            sendDataToMesh(nodeId, viperData, sendId);
        }
    }


    private void sendDataToMesh(String nodeId, ViperData viperData, String sendId) {
        byte[] data = ViperDataProcessor.getInstance().getDataFormatToJson(viperData);

        boolean isNotificationEnable = viperData.isNotificationEnable;

        Log.v("MIMO_SAHA:", "Notification enable " + isNotificationEnable);

        try {
            viperClient.sendMessage(myUserId, nodeId, sendId, data, isNotificationEnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUserActiveStatus(String nodeId) {
        try {
            return viperClient.getLinkTypeById(nodeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String sendMeshData(String peerId, ViperData viperData) {
        if (viperData != null) {
            String sendId = UUID.randomUUID().toString();
            Log.v("MIMO_SAHA:", viperData.isNotificationEnable + " Message is generated id: " + sendId);
            sendDataToMesh(peerId, viperData, sendId);
            return sendId;
        }
        return null;
    }

    public List<String> getAllSellers() {
        List<String> allInternetSellers = null;
        try {
            allInternetSellers = viperClient.getInternetSellers();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allInternetSellers == null || allInternetSellers.size() == 0) {
            return new ArrayList<>();
        } else {
            return allInternetSellers;
        }
    }

    public void stopMeshService() {
        viperClient.stopMesh();
    }

    public void restartMeshService() {
        int myCurrentRole = DataPlanManager.getInstance().getDataPlanRole();
        viperClient.restartMesh(myCurrentRole);
    }

    ////////////////////////////////////////////////////////////////////////////////////

    protected abstract void onMesh(String myMeshId);

    protected abstract void onMeshPrepared(String myWalletAddress);

    protected abstract void offMesh();

    protected abstract void peerAdd(String peerId, byte[] peerData);

    protected abstract void peerAdd(String peerId, UserModel userModel);

    protected abstract void peerRemove(String nodeId);

    protected abstract void onData(String peerId, ViperData viperData);

    protected abstract void onAck(String messageId, int status);

    protected abstract boolean isNodeAvailable(String nodeId, int userActiveStatus);


    private String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("config.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
