package com.w3engineers.unicef.telemesh.data.helper;

import android.support.test.runner.AndroidJUnit4;

import com.google.protobuf.ByteString;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.TeleMeshAnalyticsOuterClass;
import com.w3engineers.unicef.telemesh.TeleMeshUser;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.feed.AckCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinFeed;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import static org.junit.Assert.*;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class RmDataHelperTest {
    private final String msgBody = "This is test message";
    private final String createdAt = "2019-07-19T06:02:30.628Z";
    private final String date = "16-07-2019";
    private final String meshId = "0xuodnaiabd1983nd";

    @Test
    public void ackCommandTest() {
        BroadcastWebSocket listener = new BroadcastWebSocket();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(BuildConfig.BROADCAST_URL).build();
        WebSocket socket = client.newWebSocket(request, listener);
        //fake response create and test
        listener.onMessage(socket, getAckResponse());

        // parsing test of ACK command
        AckCommand command = new AckCommand();
        command.setAckMsgId(UUID.randomUUID().toString());
        command.setClientId(UUID.randomUUID().toString());
        command.setStatus(1);
        assertTrue(!command.getAckMsgId().equals(command.getClientId()));
    }

    @Test
    public void bulletinFeedTest() {
        BroadcastWebSocket listener = new BroadcastWebSocket();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(BuildConfig.BROADCAST_URL).build();
        WebSocket socket = client.newWebSocket(request, listener);
        //fake response create and test
        listener.onMessage(socket, getBulletinResponse());

        BulletinFeed bulletinFeed = new BulletinFeed()
                .setCreatedAt(createdAt)
                .setMessageBody(msgBody)
                .setMessageId(UUID.randomUUID().toString());

        assertEquals(bulletinFeed.getMessageBody(), msgBody);
    }

    @Test
    public void localAppShareCountTest() {
        addDelay(500);

        TeleMeshAnalyticsOuterClass.AppShareCount appShareCount = TeleMeshAnalyticsOuterClass.AppShareCount
                .newBuilder()
                .setShareCount(1)
                .setDate(date)
                .setUserId(UUID.randomUUID().toString())
                .build();

        TeleMeshUser.RMDataModel rmDataModel = TeleMeshUser.RMDataModel.newBuilder()
                .setUserMeshId(meshId)
                .setRawData(ByteString.copyFrom(appShareCount.toByteArray()))
                .setDataType(Constants.DataType.APP_SHARE_COUNT)
                .setIsAckSuccess(false)
                .build();

        RmDataHelper.getInstance().dataReceive(rmDataModel, true);

        addDelay(1000);

        TeleMeshUser.RMDataModel rmDataModel2 = TeleMeshUser.RMDataModel.newBuilder()
                .setUserMeshId(meshId)
                .setRawData(ByteString.copyFrom(appShareCount.toByteArray()))
                .setDataType(Constants.DataType.APP_SHARE_COUNT)
                .setIsAckSuccess(true)
                .build();

        RmDataHelper.getInstance().dataReceive(rmDataModel2, true);


    }

    private String getBulletinResponse() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("messageId", UUID.randomUUID().toString());
            jsonObject.put("messageBody", msgBody);
            jsonObject.put("createdAt", createdAt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String getAckResponse() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", 1);
            jsonObject.put("ack_msg_id", UUID.randomUUID().toString());
            jsonObject.put("clientId", UUID.randomUUID().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

