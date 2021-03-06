package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import com.w3engineers.unicef.util.helper.model.ViperData;

import org.json.JSONException;
import org.json.JSONObject;

public class ViperDataProcessor {

    private static ViperDataProcessor viperDataProcessor;

    private final String TYPE = "t";
    private final String DATA = "d";

//    public static final byte TYPE_PING = 1, TYPE_PROFILE = 3;

//    private byte[] myProfileInfo = null;

    static {
        viperDataProcessor = new ViperDataProcessor();
    }

    public static ViperDataProcessor getInstance() {
        return viperDataProcessor;
    }

    public byte[] getDataFormatToJson(ViperData viperData) {
        byte type = viperData.dataType;
        byte[] rawData = viperData.rawData;

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

    public ViperData setDataFormatFromJson(byte[] meshDataBytes) {
        if(meshDataBytes == null) {
            throw new IllegalStateException("Corrupted data");
        }

        ViperData viperData = new ViperData();
        String data = new String(meshDataBytes);

        try {
            JSONObject jsonObject = new JSONObject(data);

            byte type = (byte) jsonObject.optInt(TYPE);
            String raw = jsonObject.optString(DATA);

            viperData.dataType = type;
            viperData.rawData = raw.getBytes();

            return viperData;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return viperData;
    }

    /*public ViperData getPingForProfile() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ping", "ping");

            ViperData meshData = new ViperData();
            meshData.rawData = jsonObject.toString().getBytes();
            meshData.dataType = TYPE_PING;

            return meshData;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }*/

    /*public ViperData getMyProfileMeshData() {

        if (myProfileInfo == null)
            return null;

        ViperData meshData = new ViperData();
        meshData.rawData = myProfileInfo;
        meshData.dataType = TYPE_PROFILE;

        return meshData;
    }

    public boolean isProfileData(ViperData viperData) {
        if (viperData != null && viperData.dataType == TYPE_PROFILE)
            return true;
        else
            return false;
    }

    public boolean isProfilePing(ViperData viperData) {
        if (viperData != null && viperData.dataType == TYPE_PING)
            return true;
        else
            return false;
    }*/

}
