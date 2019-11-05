package com.w3engineers.appshare.util.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;

import com.w3engineers.appshare.application.ui.InAppShareControl;

import java.lang.reflect.Method;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

public class NetworkConfigureUtil {

    @NonNull
    public String SSID_Key = "";
    @NonNull
    public String SSID_Name = "";
    private WifiManager wifiManager;
    private boolean isRmOff = false;

    private Context context;
    private NetworkCallback networkCallback;

    @SuppressLint("StaticFieldLeak")
    private static NetworkConfigureUtil networkConfigureUtil = new NetworkConfigureUtil();

    private NetworkConfigureUtil() {
        context = InAppShareControl.getInstance().getAppShareContext();
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @NonNull
    public static NetworkConfigureUtil getInstance() {
        return networkConfigureUtil;
    }

    public interface NetworkCallback {
        void networkName();

        void networkFailed(String error);
    }

    /**
     * Using a callback, when network is prepared to share app
     *
     * @param networkCallback - get instance from implemented class
     * @return - this class for using cyclic api
     */
    @NonNull
    public NetworkConfigureUtil setNetworkCallback(@NonNull NetworkCallback networkCallback) {
        this.networkCallback = networkCallback;
        return this;
    }

    /**
     * Concern of this method is establish a network using hotspot or wifi.
     * If device have a established mesh network then use this network system
     * otherwise prepare a hotspot manually
     *
     * @return - Using a boolean for accessing in disposable
     */
    public boolean startRouterConfigureProcess() {

        try {

            InAppShareControl.AppShareCallback appShareCallback = InAppShareControl.getInstance().getAppShareCallback();
            if (appShareCallback != null) {
                appShareCallback.closeRmService();
            }

            isRmOff = true;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            wifiDirectConfigure();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

    private void wifiDirectConfigure() {

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);

            int loopMax = 10;
            while (loopMax > 0 && wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
                try {
                    Thread.sleep(500);
                    loopMax--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        manager = (WifiP2pManager) context.getSystemService(context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, context.getMainLooper(), null);

        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                triggerNetworkFailed("Network configuration failed");
            }
        });

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        retrieveP2pInfo();
    }

    private void retrieveP2pInfo() {

        manager.requestGroupInfo(channel, group -> {

            if (group == null) {
                triggerNetworkFailed("Network error. Please try again.");
                return;
            }

            SSID_Name = group.getNetworkName();
            SSID_Key = group.getPassphrase();

            triggerNetworkCall();
        });
    }

    public void stopRouterConfigureProcess() {

        if (manager != null && channel != null) {
            manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int reason) {

                }
            });

            manager = null;
            channel = null;
        }
    }

    /**
     * Preparing a manual hotspot
     * when device node is not in a mesh network
     */
    private void hotspotConfigure() {
        wifiManager.setWifiEnabled(false);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        String networkNamePrefix = "Mesh-";
        SSID_Name = networkNamePrefix + "AppShare";

        wifiConfiguration.SSID = SSID_Name;

        wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

//        wifiConfiguration.preSharedKey = SSID_Key;

        try {
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            setWifiApMethod.invoke(wifiManager, wifiConfiguration, true);

            Method stateMethod = wifiManager.getClass().getMethod("getWifiApState");
            stateMethod.setAccessible(true);

            boolean startPoint = true;
            int AP_STATE_ENABLED = 13;

            while (startPoint) {
                if ((Integer) stateMethod.invoke(wifiManager, (Object[]) null) == AP_STATE_ENABLED) {
                    startPoint = false;
                    triggerNetworkCall();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check device hotspot is enable or not
     *
     * @return - wifi ap state
     */
    private boolean isApOn() {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * The purpose of this method is triggering network callback
     */
    private void triggerNetworkCall() {
        if (networkCallback != null) {
            networkCallback.networkName();
        }
    }

    private void triggerNetworkFailed(String error) {
        if (networkCallback != null) {
            networkCallback.networkFailed(error);
        }
    }

    /**
     * Using a boolean toggle for checking RM
     * is enable or disable
     *
     * @return - the RM enable state
     */
    public boolean isRmOff() {
        return isRmOff;
    }

    /**
     * Set RM off state false
     * When you restart the RM after completing the app sharing process
     *
     * @param rmOff - set RM current state
     */
    public void setRmOff(boolean rmOff) {
        isRmOff = rmOff;
    }

    @NonNull
    public String getNetworkPass() {
        return SSID_Key;
    }

    @NonNull
    public String getNetworkName() {
        return SSID_Name;
    }


    /**
     * Reset all properties when in app share process is completed
     */
    public void resetNetworkConfigureProperties() {
        SSID_Name = "";
        SSID_Key = "";
    }
}
