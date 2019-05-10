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
import android.text.TextUtils;

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

        ByteBuffer buffer;
        String peerId = meshData.mPeerId;
        byte hasPeer;

        if (TextUtils.isEmpty(peerId)) {
            hasPeer = 0;
            buffer = ByteBuffer.allocate(1 + 1 + meshData.mData.length);
            buffer.put(meshData.mType);
            buffer.put(hasPeer);
            buffer.put(meshData.mData);
        } else {

            hasPeer = 1;
            byte[] peerByte = peerId.getBytes();

            byte length = (byte) peerByte.length;
            buffer = ByteBuffer.allocate(1 + 1 + 1 + peerByte.length + meshData.mData.length);
            buffer.put(meshData.mType);
            buffer.put(hasPeer);
            buffer.put(length);
            buffer.put(peerByte);
            buffer.put(meshData.mData);
        }

        return buffer.array();
    }

    public static MeshData setMeshData(byte[] meshDataBytes) {

        if(meshDataBytes == null || meshDataBytes.length < 2) {
            throw new IllegalStateException("Corrupted data");
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(meshDataBytes);

        MeshData meshData = new MeshData();
        meshData.mType = byteBuffer.get();
        byte hasPeer = byteBuffer.get();

        if (hasPeer == 0) {
            meshData.mData = new byte[byteBuffer.remaining()];

        } else if (hasPeer == 1) {
            byte peerLength = byteBuffer.get();

            byte[] peerByte = new byte[peerLength];

            byteBuffer.get(peerByte);

            meshData.mPeerId = new String(peerByte);

            byte[] peerData = new byte[byteBuffer.remaining()];
            byteBuffer.get(peerData);

            meshData.mData = Arrays.copyOfRange(peerData, 0, peerData.length);
        }


        return meshData;
    }

    public MeshData copy() {
        MeshData meshData = new MeshData();
        meshData.mType = mType;
        meshData.mData = mData;

        return meshData;
    }
}
