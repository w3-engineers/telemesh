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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MeshDataProcessor {

    private static MeshDataProcessor meshDataProcessor = null;
    private final String TYPE = "t";
    private final String DATA = "d";

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

        ByteBuffer byteBuffer = ByteBuffer.allocate(1 + 4 + length);
        byteBuffer.put(type);
        byteBuffer.putInt(length);
        byteBuffer.put(rawData);

        return byteBuffer.array();
    }

    public byte[] getDataFormatToJson(MeshData meshData) {
        byte type = meshData.mType;
        byte[] rawData = meshData.mData;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(TYPE, type);
            jsonObject.put(DATA, new String(rawData));

            return jsonObject.toString().getBytes();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] getPingFormat(MeshData meshData) {
        byte type = meshData.mType;

        ByteBuffer byteBuffer = ByteBuffer.allocate(1);
        byteBuffer.put(type);

        return byteBuffer.array();
    }

    public MeshData setDataFormatFromJson(byte[] meshDataBytes) {
        if(meshDataBytes == null) {
            throw new IllegalStateException("Corrupted data");
        }

        MeshData meshData = new MeshData();
        String data = new String(meshDataBytes);

        try {
            JSONObject jsonObject = new JSONObject(data);

            byte type = (byte) jsonObject.optInt(TYPE);
            String raw = jsonObject.optString(DATA);

            meshData.mType = type;
            meshData.mData = raw.getBytes();

            return meshData;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return meshData;
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

            if (dataLength != 0 && dataLength <= byteBuffer.remaining()) {

                byte[] peerData = new byte[dataLength];
                byteBuffer.get(peerData);
                meshData.mData = Arrays.copyOfRange(peerData, 0, peerData.length);
            }
        }

        return meshData;
    }
}
