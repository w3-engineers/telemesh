package com.w3engineers.unicef.telemesh._UiTest;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.data.helper.BroadcastWebSocket;
import com.w3engineers.unicef.telemesh.data.helper.DataModel;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.ShareCountModel;
import com.w3engineers.unicef.telemesh.data.local.feed.AckCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinFeed;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageCount;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
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
    private String msgBody;
    private String createdAt;
    private String date;
    private String meshId;

    @Before
    public void setUp() {
        msgBody = "This is test message";
        createdAt = "2019-07-19T06:02:30.628Z";
        date = "16-07-2019";
        meshId = "0xuodnaiabd1983nd";
    }

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

        ShareCountModel appShareCount = new ShareCountModel()
                .setCount(1)
                .setTime(date)
                .setId(UUID.randomUUID().toString());

        String shareCountString = new Gson().toJson(appShareCount);

        DataModel rmDataModel = new DataModel()
                .setUserId(meshId)
                .setRawData(shareCountString.getBytes())
                .setDataType(Constants.DataType.APP_SHARE_COUNT)
                .setAckSuccess(false);

        RmDataHelper.getInstance().dataReceive(rmDataModel, true);

        addDelay(1000);

        DataModel rmDataModel2 = new DataModel()
                .setUserId(meshId)
                .setRawData(shareCountString.getBytes())
                .setDataType(Constants.DataType.APP_SHARE_COUNT)
                .setAckSuccess(true);

        RmDataHelper.getInstance().dataReceive(rmDataModel2, true);

        addDelay(500);
        AppShareCountEntity entity = new AppShareCountEntity();
        entity.setCount(1);
        entity.setDate(date);
        entity.setUserId(meshId);
        List<AppShareCountEntity> entities = new ArrayList<>();
        entities.add(entity);
        RmDataHelper.getInstance().sendAppShareCountToSellers(entities);
        addDelay(500);
    }

    @Test
    public void localMessageCountTest() {
        addDelay(500);
        MessageCount messageCount = new MessageCount()
                .setCount(1)
                .setId(meshId)
                .setTime(System.currentTimeMillis());
        String messageCountString = new Gson().toJson(messageCount);

        DataModel rmDataModel = new DataModel()
                .setUserId(meshId)
                .setRawData(messageCountString.getBytes())
                .setDataType(Constants.DataType.MESSAGE_COUNT)
                .setAckSuccess(false);

        RmDataHelper.getInstance().dataReceive(rmDataModel, true);

        addDelay(700);

        assertTrue(true);

    }

    @After
    public void tearDown() {
//        SUT.onRmOff();
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

