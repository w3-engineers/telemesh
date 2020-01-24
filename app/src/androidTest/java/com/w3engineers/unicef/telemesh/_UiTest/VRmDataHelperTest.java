package com.w3engineers.unicef.telemesh._UiTest;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.models.ConfigurationCommand;
import com.w3engineers.models.PointGuideLine;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.data.broadcast.TokenGuideRequestModel;
import com.w3engineers.unicef.telemesh.data.helper.BroadcastWebSocket;
import com.w3engineers.unicef.telemesh.data.helper.DataModel;
import com.w3engineers.unicef.telemesh.data.helper.MeshDataSource;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdateModel;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.ShareCountModel;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.feed.AckCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastCommand;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinFeed;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinModel;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.GeoLocation;
import com.w3engineers.unicef.telemesh.data.local.feed.Payload;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackDataSource;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackEntity;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageCount;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;

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

import io.reactivex.subscribers.TestSubscriber;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class VRmDataHelperTest {
    private String msgBody;
    private String createdAt;
    private String date;
    private String meshId;
    private RandomEntityGenerator randomEntityGenerator;
    private FeedDataSource feedDataSource;
    private FeedbackDataSource feedbackDataSource;
    private UserDataSource userDataSource;

    @Before
    public void setUp() {
        msgBody = "This is test message";
        createdAt = "2019-07-19T06:02:30.628Z";
        date = "16-07-2019";
        meshId = "0x550de922bec427fc1b279944e47451a89a4f7car";

        feedDataSource = FeedDataSource.getInstance();

        feedbackDataSource = FeedbackDataSource.getInstance();

        userDataSource = UserDataSource.getInstance();

        randomEntityGenerator = new RandomEntityGenerator();
    }

    @Test
    public void ackCommandTest() {
        BroadcastWebSocket listener = new BroadcastWebSocket();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(Constants.GradleBuildValues.BROADCAST_URL).build();
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
        Request request = new Request.Builder().url(Constants.GradleBuildValues.BROADCAST_URL).build();
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
    public void bulletinFeedReceiveTest() {
        addDelay(500);

        DataModel rmDataModel = randomEntityGenerator.createRMDataModel();
        BulletinModel bulletinModel = randomEntityGenerator.getBulletinModel();
        String broadCatMessage = new Gson().toJson(bulletinModel);
        rmDataModel.setRawData(broadCatMessage.getBytes());
        rmDataModel.setUserId(meshId);

        RmDataHelper.getInstance().dataReceive(rmDataModel, true);

        addDelay(2000);

        RmDataHelper.getInstance().dataReceive(rmDataModel, false);

        addDelay(2000);

        long result = feedDataSource.updateFeedMessageReadStatus(bulletinModel.getId());
        assertTrue(result > 0);
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
    public void feedbackReceiveAndAckTest() {
        addDelay(500);
        FeedbackModel feedBackModel = randomEntityGenerator.generateFeedbackModel();
        String feedbackText = new Gson().toJson(feedBackModel);
        DataModel dataModel = randomEntityGenerator.generateDataModel(feedbackText, Constants.DataType.FEEDBACK_TEXT, feedBackModel.getUserId());

        RmDataHelper.getInstance().dataReceive(dataModel, false);

        addDelay(500);

        DataModel dataAckModel = randomEntityGenerator.generateDataModel(feedbackText, Constants.DataType.FEEDBACK_ACK, feedBackModel.getUserId());

        feedbackDataSource.insertOrUpdate(FeedbackEntity.toFeedbackEntity(feedBackModel));

        addDelay(700);

        RmDataHelper.getInstance().dataReceive(dataAckModel, false);

        addDelay(700);

        FeedbackEntity entity = feedbackDataSource.getFeedbackById(feedBackModel.getFeedbackId());

        assertNull(entity);
    }

    @Test
    public void configInfoTransferTest() {
        addDelay(500);
        ConfigurationCommand configModel = randomEntityGenerator.generateConfigFile();

        String configData = new Gson().toJson(configModel);

        DataModel configDataModel = randomEntityGenerator.generateDataModel(configData, Constants.DataType.CONFIG_UPDATE_INFO, meshId);

        RmDataHelper.getInstance().dataReceive(configDataModel, true);

        addDelay(700);

        TokenGuideRequestModel tokenRequestModel = randomEntityGenerator.generateTokenModel();
        String tokenData = new Gson().toJson(tokenRequestModel);
        DataModel tokenDataModel = randomEntityGenerator.generateDataModel(tokenData, Constants.DataType.TOKEN_GUIDE_REQUEST, meshId);

        RmDataHelper.getInstance().dataReceive(tokenDataModel, true);
        addDelay(700);

        DataModel pointConfigDataModel = randomEntityGenerator.generateDataModel(configData, Constants.DataType.TOKEN_GUIDE_INFO, meshId);

        RmDataHelper.getInstance().dataReceive(pointConfigDataModel, true);

        addDelay(700);

        int currentVersion = SharedPref.getSharedPref(TeleMeshApplication.getContext()).readInt(Constants.preferenceKey.CONFIG_VERSION_CODE);

        assertEquals(currentVersion, (int) configModel.getConfigVersionCode());
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

    @Test
    public void broadcastCommandModelsTest() {
        addDelay(500);

        String event = "connect";
        GeoLocation geoLocation = randomEntityGenerator.createGeoLocation();

        Payload payLoad = randomEntityGenerator.createPayload(geoLocation);

        BroadcastCommand command = new BroadcastCommand().setEvent(event)
                .setToken(Constants.GradleBuildValues.BROADCAST_TOKEN)
                .setBaseStationId(meshId)
                .setClientId(meshId)
                .setPayload(payLoad);

        // now testing getter value of value

        assertThat(command.getEvent(), is(event));
        assertThat(command.getToken(), is(Constants.GradleBuildValues.BROADCAST_TOKEN));
        assertThat(command.getClientId(), is(meshId));
        assertThat(command.getBaseStationId(), is(meshId));
        assertThat(command.getPayload().getMessageId(), is(payLoad.getMessageId()));
        assertThat(command.getPayload().getConnectedClients(), is(payLoad.getConnectedClients()));
        assertThat(command.getPayload().getGeoLocation().getLatitude(), is(geoLocation.getLatitude()));
        assertThat(command.getPayload().getGeoLocation().getLongitude(), is(geoLocation.getLongitude()));

        addDelay(500);
    }

    @Test
    public void userNodeStatusFindTest() {
        addDelay(500);

        int wifiOnlineStatus = RmDataHelper.getInstance().getActiveStatus(1);
        assertEquals(Constants.UserStatus.WIFI_ONLINE, wifiOnlineStatus);
        addDelay(200);

        int bleOnlineStatus = RmDataHelper.getInstance().getActiveStatus(2);
        assertEquals(Constants.UserStatus.BLE_ONLINE, bleOnlineStatus);
        addDelay(200);

        int wifiMeshOnlineStatus = RmDataHelper.getInstance().getActiveStatus(3);
        assertEquals(Constants.UserStatus.WIFI_MESH_ONLINE, wifiMeshOnlineStatus);
        addDelay(200);

        int BtMeshOnline = RmDataHelper.getInstance().getActiveStatus(4);
        assertEquals(Constants.UserStatus.BLE_MESH_ONLINE, BtMeshOnline);
        addDelay(200);

        int internetOnline = RmDataHelper.getInstance().getActiveStatus(5);
        assertEquals(Constants.UserStatus.INTERNET_ONLINE, internetOnline);

        addDelay(200);

        int offline = RmDataHelper.getInstance().getActiveStatus(0);
        assertEquals(Constants.UserStatus.OFFLINE, offline);

        addDelay(500);
    }

    @Test
    public void analyticsDataSendToSellersTest() {
        MessageEntity.MessageAnalyticsEntity entity = new MessageEntity.MessageAnalyticsEntity();
        entity.setSyncMessageCountToken(1);
        entity.setTime(System.currentTimeMillis());
        entity.setUserId(meshId);
        RmDataHelper.getInstance().analyticsDataSendToSellers(entity);
    }

    @Test
    public void versionCrossMatchingTest() {
        addDelay(500);

        InAppUpdateModel appUpdateModel = randomEntityGenerator.generateAppUpdateModel();

        String localAppUpdateData = new Gson().toJson(appUpdateModel);

        DataModel updateDataModel = randomEntityGenerator.generateDataModel(localAppUpdateData, Constants.DataType.VERSION_HANDSHAKING, meshId);

        RmDataHelper.getInstance().dataReceive(updateDataModel, true);

        addDelay(1000);

        DataModel apkDownloadDataModel = randomEntityGenerator.generateDataModel(localAppUpdateData, Constants.DataType.SERVER_LINK, meshId);

        RmDataHelper.getInstance().dataReceive(apkDownloadDataModel, true);

        assertTrue(BuildConfig.VERSION_CODE > appUpdateModel.getVersionCode());
    }

    @Test
    public void userUpdatedDataInfoTest() {
        addDelay(500);

        UserEntity userEntity = randomEntityGenerator.createUserEntity();
        userEntity.setMeshId(meshId);

        userDataSource.insertOrUpdateData(userEntity);

        addDelay(1000);

        String updatedName = "John Doe";
        userEntity.setUserName(updatedName);

        UserModel userModel = randomEntityGenerator.createUserModel(userEntity);
        String userUpdatedData = new Gson().toJson(userModel);

        addDelay(500);

        RmDataHelper.getInstance().broadcastUpdateProfileInfo(updatedName, 4);

        addDelay(2000);

        DataModel userUpdateDataModel = randomEntityGenerator.generateDataModel(userUpdatedData, Constants.DataType.USER_UPDATE_INFO, userEntity.getMeshId());

        RmDataHelper.getInstance().dataReceive(userUpdateDataModel, true);

        addDelay(2500);

        UserEntity updatedUserData = userDataSource.getSingleUserById(userEntity.getMeshId());

        assertTrue(updatedUserData.getUserName().equalsIgnoreCase(updatedName));

    }

    @Test
    public void updateAllUserOffline() {
        addDelay(500);

        UserEntity userEntity = randomEntityGenerator.createUserEntity();
        userEntity.setMeshId(meshId);
        userEntity.setOnlineStatus(Constants.UserStatus.WIFI_ONLINE);

        userDataSource.insertOrUpdateData(userEntity);

        addDelay(1000);

        RmDataHelper.getInstance().updateUserStatus(true);

        addDelay(2000);

        UserEntity updatedUserData = userDataSource.getSingleUserById(userEntity.getMeshId());

        if (updatedUserData != null) {
            assertEquals(updatedUserData.isOnline, Constants.UserStatus.OFFLINE);
        }
    }

    @Test
    public void hanShakingVersionTest() {
        addDelay(500);

        RmDataHelper.getInstance().configFileSendToOthers(-2, meshId);

        addDelay(500);

        RmDataHelper.getInstance().versionMessageHandshaking(meshId);

        addDelay(500);

    }

    @Test
    public void sendFeedbackToInternetUser() {
        addDelay(500);
        FeedbackModel feedBackModel = randomEntityGenerator.generateFeedbackModel();
        RmDataHelper.getInstance().sendFeedbackToInternetUser(FeedbackEntity.toFeedbackEntity(feedBackModel));

        addDelay(500);

        assertTrue(true);
    }

    @After
    public void tearDown() {
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

