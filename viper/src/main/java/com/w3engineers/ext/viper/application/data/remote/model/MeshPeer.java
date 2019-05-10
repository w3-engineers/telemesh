package com.w3engineers.ext.viper.application.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

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

    // Remove RM-Lib
    /*public static MeshPeer from(MeshId meshId) {
        if(meshId != null) {
            return new MeshPeer(meshId.toString());
        }
        return null;
    }*/

    public static MeshPeer from(String peerId) {
        if(!TextUtils.isEmpty(peerId)) {
            return new MeshPeer(peerId);
        }
        return null;
    }
}
