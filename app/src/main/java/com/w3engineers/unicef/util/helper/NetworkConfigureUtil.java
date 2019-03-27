package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;

import java.lang.reflect.Method;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [19-Mar-2019 at 3:20 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [19-Mar-2019 at 3:20 PM].
 * --> <Second Editor> on [19-Mar-2019 at 3:20 PM].
 * Reviewed by :
 * --> <First Reviewer> on [19-Mar-2019 at 3:20 PM].
 * --> <Second Reviewer> on [19-Mar-2019 at 3:20 PM].
 * ============================================================================
 **/
public class NetworkConfigureUtil {

    private static int AP_STATE_ENABLED = 13;

    private String networkNamePrefix = "RM-";
    public String SSID_Key = "m3sht3st";
    private WifiManager wifiManager;
    private boolean isRmOff = false;

    private Context context;
    private NetworkCallback networkCallback;

    private static NetworkConfigureUtil networkConfigureUtil = new NetworkConfigureUtil();

    private NetworkConfigureUtil() {
        context = TeleMeshApplication.getContext();
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static NetworkConfigureUtil getInstance() {
        return networkConfigureUtil;
    }

    public interface NetworkCallback {
        void networkName(String SSID);
    }

    /**
     * Using a callback, when network is prepared to share app
     * @param networkCallback - get instance from implemented class
     * @return - this class for using cyclic api
     */
    public NetworkConfigureUtil setNetworkCallback(NetworkCallback networkCallback) {
        this.networkCallback = networkCallback;
        return this;
    }

    /**
     * Concern of this method is establish a network using hotspot or wifi.
     * If device have a established mesh network then use this network system
     * otherwise prepare a hotspot manually
     * @return - Using a boolean for accessing in disposable
     */
    public boolean startRouterConfigureProcess() {
        String SSID_Name = getWifiConfigurableName();

        try {
            if (TextUtils.isEmpty(SSID_Name)) {

                if (isApOn()) {
                    Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                    WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

                    SSID_Name = wifiConfig.SSID;
                    triggerNetworkCall(SSID_Name);

                } else {
                    RmDataHelper.getInstance().stopRmService();

                    isRmOff = true;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    hotspotConfigure();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Preparing a manual hotspot
     * when device node is not in a mesh network
     */
    private void hotspotConfigure() {
        wifiManager.setWifiEnabled(false);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        String networkName = networkNamePrefix + context.getString(R.string.rm_ssid);

        wifiConfiguration.SSID = networkName;

        wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

        wifiConfiguration.preSharedKey = SSID_Key;

        try {
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            setWifiApMethod.invoke(wifiManager, wifiConfiguration, true);

            Method stateMethod = wifiManager.getClass().getMethod("getWifiApState");
            stateMethod.setAccessible(true);

            boolean startPoint = true;

            while (startPoint) {
                if ((Integer) stateMethod.invoke(wifiManager, (Object[]) null) == AP_STATE_ENABLED) {
                    startPoint = false;
                    triggerNetworkCall(networkName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check device hotspot is enable or not
     * @return - wifi ap state
     */
    private boolean isApOn() {
        try {
            Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (Throwable ignored) {
        }
        return false;
    }

    /**
     * Concern of this api check the wifi is a RightMesh prepared or not
     * @return - Only provide the RM configurable wifi name
     */
    @Nullable
    private String getWifiConfigurableName() {
        try {

            String SSID_Name;

            if (wifiManager.isWifiEnabled()) {

                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (networkInfo.isConnected()) {

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                    SSID_Name = wifiInfo.getSSID();

                    SSID_Name = SSID_Name.replace("\"", "");

                    if (!TextUtils.isEmpty(SSID_Name) && SSID_Name.startsWith(networkNamePrefix)) {
                        triggerNetworkCall(SSID_Name);
                        return SSID_Name;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The purpose of this method is triggering network callback
     * @param SSID - for triggering this callback need a ssid name for exposing to others
     */
    private void triggerNetworkCall(@NonNull String SSID) {
        if (networkCallback != null) {
            networkCallback.networkName(SSID);
        }
    }

    /**
     * Using a boolean toggle for checking RM
     * is enable or disable
     * @return - the RM enable state
     */
    public boolean isRmOff() {
        return isRmOff;
    }

    /**
     * Set RM off state false
     * When you restart the RM after completing the app sharing process
     * @param rmOff - set RM current state
     */
    public void setRmOff(boolean rmOff) {
        isRmOff = rmOff;
    }
}
