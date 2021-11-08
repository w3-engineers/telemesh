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
import com.w3engineers.unicef.util.helper.ContentUtil;
import com.w3engineers.unicef.util.helper.TimeUtil;
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

    public String getDummyImageLink() {
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

    public MessageEntity prepareImageMessage(String path, String userId) {
        MessageEntity messageEntity = new MessageEntity()
                .setMessage(ContentUtil.getInstance().getContentMessageBody(path))
                .setContentPath(path)
                .setContentThumbPath("");

        messageEntity.setMessageId(UUID.randomUUID().toString())
                .setFriendsId(userId)
                .setIncoming(false)
                .setTime(TimeUtil.toCurrentTime())
                .setStatus(Constants.MessageStatus.STATUS_DELIVERED)
                .setMessageType(Constants.MessageType.IMAGE_MESSAGE);

        return messageEntity;
    }

    public ChatEntity createGroupChatEntity(String userId, String groupId) {

        return new MessageEntity()
                .setMessage("Hi")
                .setMessagePlace(true)
                .setGroupId(groupId)
                .setFriendsId(userId)
                .setMessageId(UUID.randomUUID().toString())
                .setIncoming(true)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_RECEIVED);
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

    public ChatEntity createOutgoingContent(String userId) {

        return new MessageEntity().setContentPath(getDummyImageLink())
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

    public PermissionInterruptionEvent generatePermissionInterruptEvent() {
        PermissionInterruptionEvent event = new PermissionInterruptionEvent();
        event.hardwareState = DataPlanConstants.INTERRUPTION_EVENT.USER_DISABLED_BT;
        return event;
    }

    public String sampleByteImage() {
        return "data:image/gif;base64,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>";
    }
}
