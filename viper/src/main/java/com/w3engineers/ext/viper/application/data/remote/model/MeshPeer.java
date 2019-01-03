package com.w3engineers.ext.viper.application.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.left.rightmesh.id.MeshId;

/**
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2018-08-07 at 1:14 PM].
 * <br>Email: azim@w3engineers.com
 * <br>----------------------------------------------------------------------------
 * <br>Project: android-framework.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2018-08-07 at 1:14 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2018-08-07 at 1:14 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

/**
 * This class is to provide developers at app level data of a node or peer.
 */
public class MeshPeer implements Parcelable {

    /**
     * Id of peer
     */
    private String mPeerId;

    /**
     * Get current peer's Id
     * @return
     */
    public String getPeerId() {
        return mPeerId;
    }

    public void setPeerId(String peerId) {
        mPeerId = peerId;
    }


    //Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPeerId);
    }

    public MeshPeer(String peerId) {
        this.mPeerId = peerId;
    }

    protected MeshPeer(Parcel in) {
        this.mPeerId = in.readString();
    }

    public static final Parcelable.Creator<MeshPeer> CREATOR = new Parcelable.Creator<MeshPeer>() {
        @Override
        public MeshPeer createFromParcel(Parcel source) {
            return new MeshPeer(source);
        }

        @Override
        public MeshPeer[] newArray(int size) {
            return new MeshPeer[size];
        }
    };

    @Override
    public String toString() {
        return mPeerId;
    }

    public static MeshPeer from(MeshId meshId) {
        if(meshId != null) {
            return new MeshPeer(meshId.toString());
        }
        return null;
    }
}
