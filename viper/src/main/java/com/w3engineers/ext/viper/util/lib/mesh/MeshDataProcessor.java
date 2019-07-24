package com.w3engineers.ext.viper.util.lib.mesh;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.util.Log;

import com.w3engineers.ext.viper.application.data.remote.model.MeshData;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MeshDataProcessor {

    private static MeshDataProcessor meshDataProcessor = null;

    static {
        meshDataProcessor = new MeshDataProcessor();
    }

    public static MeshDataProcessor getInstance() {
        return meshDataProcessor;
    }

    public byte[] getDataFormat(MeshData meshData) {
        byte type = meshData.mType;
        int length = meshData.mData.length;
        byte[] rawData = meshData.mData;

        Log.v("MIMO_SAHA:", " SL: " + length + " T " + type);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1 + 4 + length);
        byteBuffer.put(type);
        byteBuffer.putInt(length);
        byteBuffer.put(rawData);

        return byteBuffer.array();
    }

    public byte[] getPingFormat(MeshData meshData) {
        byte type = meshData.mType;

        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        byteBuffer.put(type);

        return byteBuffer.array();
    }

    public MeshData setDataFormat(byte[] meshDataBytes) {
        if(meshDataBytes == null) {
            throw new IllegalStateException("Corrupted data");
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(meshDataBytes);

        MeshData meshData = new MeshData();
        meshData.mType = byteBuffer.get();

        if (meshData.mType == MeshDataManager.TYPE_PING) {
            return meshData;
        }

        if (byteBuffer.hasRemaining()) {
            int dataLength = byteBuffer.getInt();

            Log.v("MIMO_SAHA:", " RL: " + dataLength + " T " + meshData.mType);

            if (dataLength != 0 && dataLength <= byteBuffer.remaining()) {

                byte[] peerData = new byte[dataLength];
                byteBuffer.get(peerData);
                meshData.mData = Arrays.copyOfRange(peerData, 0, peerData.length);
            }
        }

        return meshData;
    }
}
