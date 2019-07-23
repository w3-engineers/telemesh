package com.w3engineers.ext.viper.util.lib.mesh;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;

public class MeshDataManager {

    private static MeshDataManager meshLibManager = new MeshDataManager();
    private byte[] myProfileInfo = null;
    private String myPeerId;
    public static final byte TYPE_PING = 1, TYPE_PROFILE = 3;

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

        MeshData meshData = new MeshData();
        meshData.mMeshPeer = new MeshPeer(myPeerId);
        meshData.mType = TYPE_PING;

        return meshData;
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
