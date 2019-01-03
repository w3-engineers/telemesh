package com.w3engineers.ext.viper.application.data.remote.model;

/**
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2018-08-07 at 1:13 PM].
 * <br>Email: azim@w3engineers.com
 * <br>----------------------------------------------------------------------------
 * <br>Project: android-framework.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2018-08-07 at 1:13 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2018-08-07 at 1:13 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

import android.os.Parcel;
import android.os.Parcelable;

import com.w3engineers.ext.viper.util.lib.mesh.ProfileManager;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Represent piece of data. Contains: type, sender/receiver and data in bytes
 */
public class MeshData extends BaseMeshData implements Parcelable {

    /**
     * Type of Data. Value {@value ProfileManager#MY_PROFILE_INFO_TYPE}
     * is reserved for Profile Info type. Developers should not use this value.
     */
    public byte mType;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.mType);
    }

    public MeshData() {
    }

    protected MeshData(Parcel in) {
        super(in);
        this.mType = in.readByte();
    }

    public static final Parcelable.Creator<MeshData> CREATOR = new Parcelable.Creator<MeshData>() {
        @Override
        public MeshData createFromParcel(Parcel source) {
            return new MeshData(source);
        }

        @Override
        public MeshData[] newArray(int size) {
            return new MeshData[size];
        }
    };


    public static byte[] getMeshData(MeshData meshData) {

        if(meshData == null || meshData.mData == null) {
            return null;
        }

        ByteBuffer buffer = ByteBuffer.allocate(1 + meshData.mData.length);
        buffer.put(meshData.mType);
        buffer.put(meshData.mData);
        return buffer.array();
    }

    public static MeshData setMeshData(byte[] meshDataBytes) {

        if(meshDataBytes == null || meshDataBytes.length < 2) {
            throw new IllegalStateException("Corrupted data");
        }

        MeshData meshData = new MeshData();
        meshData.mType = meshDataBytes[0];
        meshData.mData = Arrays.copyOfRange(meshDataBytes, 1, meshDataBytes.length);
        return meshData;
    }

    public MeshData copy() {
        MeshData meshData = new MeshData();
        meshData.mType = mType;
        meshData.mData = mData;

        return meshData;
    }
}
