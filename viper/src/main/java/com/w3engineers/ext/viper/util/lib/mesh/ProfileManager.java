package com.w3engineers.ext.viper.util.lib.mesh;


import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class ProfileManager {
    private static final ProfileManager ourInstance = new ProfileManager();

    public static ProfileManager getInstance() {
        return ourInstance;
    }

    private ProfileManager() {}

    private byte[] mProfileInfo;
    public static final byte MY_PROFILE_INFO_TYPE = -128;


    public void setProfileInfo(byte[] profileInfo) {
        mProfileInfo = profileInfo;
    }


    /**
     *
     * @param meshPeer
     * @return
     */
    // TODO: 8/9/2018 Will apply caching at this layer and enhance performance
    // fixme We do not have proper specification on sendDataReliable API so we have returned false
    // based on natural assumption
    // Currently we do not handle any profile data acknowledgement
    public long sendMyProfileInfo(MeshPeer meshPeer) {

        MeshData myProfileMeshData = new MeshData();
        myProfileMeshData.mData = mProfileInfo;
        myProfileMeshData.mMeshPeer = meshPeer;
        myProfileMeshData.mType = MY_PROFILE_INFO_TYPE;

        return MeshProviderOld.getInstance().sendProfileInfo(myProfileMeshData);
    }

    /**
     * Retrieve profile data. If not a profile data then return null
     * @param meshData
     * @return
     */
    public byte[] processProfileData(MeshData meshData) {

        if(meshData != null && meshData.mType == MY_PROFILE_INFO_TYPE) {
            return meshData.mData;
        }

        return null;
    }
}
