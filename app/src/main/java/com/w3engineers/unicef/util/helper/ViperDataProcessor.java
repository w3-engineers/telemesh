package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.mesh.util.MeshApp;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.data.helper.AppCredentials;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.updateapp.UpdateConfigModel;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.helper.model.ViperData;

import org.json.JSONException;
import org.json.JSONObject;

public class ViperDataProcessor {

    private static ViperDataProcessor viperDataProcessor;

    private final String TYPE = "t";
    private final String DATA = "d";

//    public static final byte TYPE_PING = 1, TYPE_PROFILE = 3;

//    private byte[] myProfileInfo = null;

    static {
        viperDataProcessor = new ViperDataProcessor();
    }

    public static ViperDataProcessor getInstance() {
        return viperDataProcessor;
    }

    public byte[] getDataFormatToJson(ViperData viperData) {

        try {
            byte type = viperData.dataType;
            byte[] rawData = viperData.rawData;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(TYPE, type);
            jsonObject.put(DATA, new String(rawData));

            return jsonObject.toString().getBytes();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        return null;
    }

    public ViperData setDataFormatFromJson(byte[] meshDataBytes) {
        if (meshDataBytes == null) {
            throw new IllegalStateException("Corrupted data");
        }

        ViperData viperData = new ViperData();
        String data = new String(meshDataBytes);

        try {
            JSONObject jsonObject = new JSONObject(data);

            byte type = (byte) jsonObject.optInt(TYPE);
            String raw = jsonObject.optString(DATA);

            viperData.dataType = type;
            viperData.rawData = raw.getBytes();

            return viperData;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return viperData;
    }

    /*public ViperData getPingForProfile() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ping", "ping");

            ViperData meshData = new ViperData();
            meshData.rawData = jsonObject.toString().getBytes();
            meshData.dataType = TYPE_PING;

            return meshData;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }*/

    /*public ViperData getMyProfileMeshData() {

        if (myProfileInfo == null)
            return null;

        ViperData meshData = new ViperData();
        meshData.rawData = myProfileInfo;
        meshData.dataType = TYPE_PROFILE;

        return meshData;
    }

    public boolean isProfileData(ViperData viperData) {
        if (viperData != null && viperData.dataType == TYPE_PROFILE)
            return true;
        else
            return false;
    }

    public boolean isProfilePing(ViperData viperData) {
        if (viperData != null && viperData.dataType == TYPE_PING)
            return true;
        else
            return false;
    }*/

    public void processUpdateAppConfigJson(String configData) {

        UpdateConfigModel updateConfigModel;
        SharedPref.write(Constants.preferenceKey.APP_UPDATE_CHECK_TIME, System.currentTimeMillis());

        updateConfigModel = new Gson().fromJson(configData, UpdateConfigModel.class);

        //   int currentUpdateType = sharedPref.readInt(Constants.preferenceKey.APP_UPDATE_TYPE);

        if (updateConfigModel != null) {
            Log.d("FileDownload", "Config file info: " + updateConfigModel.getVersionName());

            String versionName = updateConfigModel.getVersionName();
            int versionCode = updateConfigModel.getVersionCode();
            int updateType = updateConfigModel.getUpdateType();
            String releaseNote = updateConfigModel.getReleaseNote();

            if (BuildConfig.VERSION_CODE < versionCode) {

                SharedPref.write(Constants.preferenceKey.APP_UPDATE_TYPE, updateType);
                SharedPref.write(Constants.preferenceKey.APP_UPDATE_VERSION_CODE, versionCode);
                SharedPref.write(Constants.preferenceKey.APP_UPDATE_VERSION_NAME, versionName);

                if (Constants.AppUpdateType.NORMAL_UPDATE == updateType) {
                    String normalUpdateJson = buildAppUpdateJson(versionName, versionCode, releaseNote);
                    if (MainActivity.getInstance() != null) {
                        MainActivity.getInstance().checkPlayStoreAppUpdate(updateType, normalUpdateJson);
                    }
                } else if (Constants.AppUpdateType.BLOCKER == updateType) {
                    if (MainActivity.getInstance() != null) {
                        MainActivity.getInstance().openAppBlocker(versionName);
                    }
                }
            }

            //  sharedPref.write(Constants.preferenceKey.APP_UPDATE_TYPE, updateType);
        }
    }

    private String buildAppUpdateJson(String versionName, int versionCode, String releaseNote) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.InAppUpdate.LATEST_VERSION_CODE_KEY, versionCode);
            jsonObject.put(Constants.InAppUpdate.LATEST_VERSION_KEY, versionName);
            jsonObject.put(Constants.InAppUpdate.URL_KEY, AppCredentials.getInstance().getFileRepoLink());
            jsonObject.put(Constants.InAppUpdate.RELEASE_NOTE_KEY, releaseNote);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
