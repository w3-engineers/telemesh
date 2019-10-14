package com.w3engineers.ext.viper.util.lib.mesh;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;

import org.json.JSONException;
import org.json.JSONObject;

public class MeshDataManager {

    private static MeshDataManager meshLibManager = new MeshDataManager();
    private byte[] myProfileInfo = null;
    private String myPeerId;
    public static final byte TYPE_PING = 1, TYPE_PROFILE = 3;
    private String VERSION_KEY = "version";
    private String VERSION_CODER_KEY = "version_code";
    private String SERVER_LINK_KEY = "server_link";

    public static MeshDataManager getInstance() {
        return meshLibManager;
    }

    public MeshDataManager setMyProfileInfo(byte[] myProfileInfo) {
        this.myProfileInfo = myProfileInfo;
        return this;
    }

    public void setMyPeerId(String myPeerId) {
        this.myPeerId = myPeerId;
    }

    public MeshData getMyProfileMeshData() {

        if (myProfileInfo == null)
            return null;

        MeshData meshData = new MeshData();
        meshData.mData = myProfileInfo;
        meshData.mMeshPeer = new MeshPeer(myPeerId);
        meshData.mType = TYPE_PROFILE;

        return meshData;
    }

    public MeshData getPingForProfile() {

        try {
            JSONObject jaJsonObject = new JSONObject();
            jaJsonObject.put("ping", "ping");

            MeshData meshData = new MeshData();
            meshData.mData = jaJsonObject.toString().getBytes();
            meshData.mMeshPeer = new MeshPeer(myPeerId);
            meshData.mType = TYPE_PING;

            return meshData;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isProfileData(MeshData meshData) {
        if (meshData != null && meshData.mType == TYPE_PROFILE)
            return true;
        else
            return false;
    }

    public boolean isProfilePing(MeshData meshData) {
        if (meshData != null && meshData.mType == TYPE_PING)
            return true;
        else
            return false;
    }

}
