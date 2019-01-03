package com.w3engineers.ext.viper.util.lib.mesh;


import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;

/**
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2018-08-09 at 12:16 PM].
 * <br>Email: azim@w3engineers.com
 * <br>----------------------------------------------------------------------------
 * <br>Project: android-framework.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2018-08-09 at 12:16 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2018-08-09 at 12:16 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
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
    public int sendMyProfileInfo(MeshPeer meshPeer) {

        MeshData myProfileMeshData = new MeshData();
        myProfileMeshData.mData = mProfileInfo;
        myProfileMeshData.mMeshPeer = meshPeer;
        myProfileMeshData.mType = MY_PROFILE_INFO_TYPE;

        return MeshProvider.getInstance().sendProfileInfo(myProfileMeshData);

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
