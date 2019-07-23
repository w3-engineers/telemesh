package com.w3engineers.ext.viper.application.data.remote.model;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represent piece of data. Contains: type, sender/receiver and data in bytes
 */
public class BaseMeshData implements Parcelable {

    /**
     * Data from meshPeer
     */
    public byte[] mData;

    /**
     * sender or receiver
     */
    public MeshPeer mMeshPeer;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.mData);
        dest.writeParcelable(this.mMeshPeer, flags);
    }

    public BaseMeshData() {
    }

    protected BaseMeshData(Parcel in) {
        this.mData = in.createByteArray();
        this.mMeshPeer = in.readParcelable(MeshPeer.class.getClassLoader());
    }

    public static final Creator<BaseMeshData> CREATOR = new Creator<BaseMeshData>() {
        @Override
        public BaseMeshData createFromParcel(Parcel source) {
            return new BaseMeshData(source);
        }

        @Override
        public BaseMeshData[] newArray(int size) {
            return new BaseMeshData[size];
        }
    };
}
