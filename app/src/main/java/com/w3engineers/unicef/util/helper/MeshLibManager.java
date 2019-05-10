package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;

public class MeshLibManager {

    private static MeshLibManager meshLibManager = new MeshLibManager();
    private byte[] myProfileInfo = null;
    private String myPeerId;
    public static final byte TYPE_PROFILE = 3, TYPE_DATA = 4;

    public static MeshLibManager getInstance() {
        return meshLibManager;
    }

    public MeshLibManager setMyProfileInfo(byte[] myProfileInfo) {
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
        meshData.mPeerId = myPeerId;
        meshData.mType = TYPE_PROFILE;

        return meshData;
    }

    public boolean isProfileData(MeshData meshData) {
        if (meshData != null && meshData.mType == TYPE_PROFILE)
            return true;
        else
            return false;
    }
}
