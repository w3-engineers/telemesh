package com.w3engineers.unicef.telemesh.util;

import android.os.Parcel;

import com.google.gson.Gson;
import com.w3engineers.mesh.application.data.local.DataPlanConstants;
import com.w3engineers.mesh.application.data.model.DataAckEvent;
import com.w3engineers.mesh.application.data.model.DataEvent;
import com.w3engineers.mesh.application.data.model.PeerRemoved;
import com.w3engineers.mesh.application.data.model.PermissionInterruptionEvent;
import com.w3engineers.mesh.application.data.model.ServiceUpdate;
import com.w3engineers.mesh.application.data.model.TransportInit;
import com.w3engineers.mesh.application.data.model.UserInfoEvent;
import com.w3engineers.unicef.telemesh.data.broadcast.TokenGuideRequestModel;
import com.w3engineers.unicef.telemesh.data.helper.DataModel;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdateModel;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinModel;
import com.w3engineers.unicef.telemesh.data.local.feed.GeoLocation;
import com.w3engineers.unicef.telemesh.data.local.feed.Payload;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackEntity;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.telemesh.data.updateapp.UpdateConfigModel;
import com.w3engineers.unicef.util.helper.model.ViperData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

/**
 * Descendant of {@link RandomGenerator}. Particularly generates {@link UserEntity}.
 * <br/>Put names properly
 * with a library called <a href="https://github.com/DiUS/java-faker">Java Faker</a>
 */
public class RandomEntityGenerator {

    public UserEntity createUserEntity() {

        String firstName = "Daniel";

        return new UserEntity()
                .setUserName(firstName)
                .setOnlineStatus(Constants.UserStatus.WIFI_ONLINE)
                .setIsFavourite(Constants.FavouriteStatus.UNFAVOURITE)
                .setAvatarIndex(3);
    }

    public UserEntity createUserEntityWithId() {

        String firstName = "Daniel";

        return new UserEntity()
                .setUserName(firstName)
                .setOnlineStatus(Constants.UserStatus.WIFI_ONLINE)
                .setIsFavourite(Constants.FavouriteStatus.FAVOURITE)
                .setAvatarIndex(3)
                .setMeshId(UUID.randomUUID().toString());
    }

    public DataModel createRMDataModel() {


        return new DataModel()
                .setRawData("Hi".getBytes())
                .setDataType(Constants.DataType.MESSAGE_FEED);

        /*return RMDataModel.newBuilder()
                .setRawData(ByteString.copyFrom("Hi".getBytes()))
                .setUserMeshId(UUID.randomUUID().toString())
                .setDataType(1).build();*/
    }

    public String getDummyImageLink(){
        return "file:///android_asset/sample_image.jpg";
    }

    public ChatEntity createChatEntity(String userId) {

        return new MessageEntity().setMessage("Hi")
                .setFriendsId(userId)
                .setMessageId(UUID.randomUUID().toString())
                .setIncoming(true)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_SENDING);
    }

    public ChatEntity createIncomingContent(String userId, File file) {

        return new MessageEntity().setContentPath(file.getPath())
                .setContentProgress(20)
                .setFriendsId(userId)
                .setMessageId(UUID.randomUUID().toString())
                .setIncoming(true)
                .setMessageType(Constants.MessageType.IMAGE_MESSAGE)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_FAILED);
    }

    public ChatEntity createReceiverChatEntity(String userId) {

        return new MessageEntity().setMessage("Hi")
                .setFriendsId(userId)
                .setMessageId(UUID.randomUUID().toString())
                .setIncoming(true)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_UNREAD);
    }

    public ViperData createMeshData(ChatEntity chatEntity) {
        ViperData meshData = new ViperData();

        meshData.dataType = Constants.DataType.MESSAGE;
        meshData.isNotificationEnable = false;
        meshData.rawData = new Gson().toJson(chatEntity.toMessageModel()).getBytes();

        return meshData;
    }

    public DataModel createChatEntityRmDataModel(String userId, MessageEntity chatEntity) {
        return new DataModel()
                .setUserId(userId)
                .setRawData(new Gson().toJson(chatEntity.toMessageModel()).getBytes())
                .setDataType(Constants.DataType.MESSAGE);
    }

   /* public MeshAcknowledgement createAckRmDataModel(String userId, long transferId) {
        MeshAcknowledgement meshAcknowledgement = new MeshAcknowledgement(String.valueOf(transferId));
        meshAcknowledgement.mMeshPeer = new MeshPeer(userId);
        return meshAcknowledgement;
    }*/

    public GeoLocation createGeoLocation() {
        GeoLocation geoLocation = new GeoLocation();
        geoLocation.setLatitude("22.8456");
        geoLocation.setLongitude("89.5403");
        return geoLocation;
    }

    public Payload createPayload(GeoLocation location) {
        Payload payload = new Payload();
        payload.setMessageId(UUID.randomUUID().toString());
        payload.setGeoLocation(location);
        payload.setConnectedClients(String.valueOf(2));
        return payload;
    }

    public UserModel createUserModel(UserEntity entity) {

        return entity.getProtoUser();
    }

    public BulletinModel getBulletinModel() {
        BulletinModel model = new BulletinModel();
        model.setId(UUID.randomUUID().toString());
        model.setMessage("Test message");
        model.setTime("2019-08-02T06:05:30.000Z");
        return model;
    }

    public FeedbackModel generateFeedbackModel() {
        FeedbackModel model = new FeedbackModel();
        model.setFeedbackId(UUID.randomUUID().toString());
        model.setUserName("John Doe");
        model.setUserId("0x550de922bec427fc1b279944e47451a89a4f7cag");
        model.setFeedback("Good app");
        return model;
    }

    public DataModel generateDataModel(String data, byte type, String userId) {
        DataModel dataModel = new DataModel();
        dataModel.setUserId(userId);
        dataModel.setRawData(data.getBytes());
        dataModel.setDataType(type);

        return dataModel;
    }

  /*  public ConfigurationCommand generateConfigFile() {
        ConfigurationCommand configurationCommand = new ConfigurationCommand(Parcel.obtain());

        configurationCommand.setConfigVersionCode(100);

        configurationCommand.setTokenGuideVersion(2);

        configurationCommand.setConfigVersionName("2.0.0");

        return configurationCommand;
    }*/

    public TokenGuideRequestModel generateTokenModel() {
        TokenGuideRequestModel model = new TokenGuideRequestModel();
        model.setRequest("Request");
        return model;
    }

    public InAppUpdateModel generateAppUpdateModel() {
        InAppUpdateModel model = new InAppUpdateModel();

        model.setUpdateLink("192.168.43.1");
        model.setVersionCode(1);
        model.setVersionName("1.0.0");

        return model;
    }

    public UserInfoEvent generateUserInfoEvent(String meshId) {
        UserInfoEvent userInfoEvent = new UserInfoEvent();
        userInfoEvent.setUserName("John Doe");
        userInfoEvent.setAvatar(2);
        userInfoEvent.setRegTime(System.currentTimeMillis());
       // userInfoEvent.setConfigVersion(1);
        userInfoEvent.setAddress(meshId);
        return userInfoEvent;
    }

    public DataAckEvent generateDataAckEvent(String dataId, int status) {
        DataAckEvent event = new DataAckEvent();

        event.dataId = dataId;
        event.status = status;

        return event;
    }

    public PeerRemoved generatePeerRemoveEvent(String meshId) {
        PeerRemoved event = new PeerRemoved();
        event.peerId = meshId;
        return event;
    }

    public TransportInit generateTransportInit(String meshId) {
        TransportInit event = new TransportInit();
        event.nodeId = meshId;
        return event;
    }

    public ServiceUpdate generateServiceUpdate() {
        ServiceUpdate event = new ServiceUpdate();
        event.isNeeded = true;
        return event;
    }

    public DataEvent generateDataEvent(String meshId) {
        DataEvent event = new DataEvent();
        event.peerId = meshId;

        ChatEntity messageModel = createReceiverChatEntity(meshId);

        String messageData = new Gson().toJson(messageModel.toMessageModel());

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("t", Constants.DataType.MESSAGE);
            jsonObject.put("d", messageData);

            event.data = jsonObject.toString().getBytes();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return event;
    }

    public UpdateConfigModel generateUpdateConfigModel() {
        UpdateConfigModel model = new UpdateConfigModel();
        model.setReleaseNote("Test update");
        model.setUpdateType(1);
        model.setVersionCode(100);
        model.setVersionName("100.0.0");

        return model;
    }

    public PermissionInterruptionEvent generatePermissionInterruptEvent(){
        PermissionInterruptionEvent event = new PermissionInterruptionEvent();
        event.hardwareState = DataPlanConstants.INTERRUPTION_EVENT.USER_DISABLED_BT;
        return event;
    }
}
