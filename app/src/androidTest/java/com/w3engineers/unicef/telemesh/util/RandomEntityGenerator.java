package com.w3engineers.unicef.telemesh.util;
import com.google.gson.Gson;
import com.w3engineers.mesh.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.mesh.application.data.remote.model.MeshPeer;
import com.w3engineers.unicef.telemesh.data.helper.DataModel;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.feed.BulletinModel;
import com.w3engineers.unicef.telemesh.data.local.feed.GeoLocation;
import com.w3engineers.unicef.telemesh.data.local.feed.Payload;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.util.helper.model.ViperData;

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

    /*@Override
    public <T> T createAndFill(Class<T> clazz) throws Exception {
        UserEntity userEntity = (UserEntity) super.createAndFill(clazz);

        Faker faker = new Faker();

        userEntity.setUserFirstName(faker.name().firstName());
        userEntity.setUserLastName(faker.name().lastName());

        @SuppressWarnings("unchecked")
        T t = (T) userEntity;
        return t;
    }*/

    /*public UserEntity createUserEntity() throws Exception {
        UserEntity userEntity = createAndFill(UserEntity.class);

        Faker faker = new Faker();

        userEntity.setUserFirstName(faker.name().firstName());
        userEntity.setUserLastName(faker.name().lastName());
        userEntity.setAvatarIndex(faker.random().nextInt(20));

        return userEntity;
    }*/

    public UserEntity createUserEntity() {

        String firstName = "Daniel";
        String lastName = "Alvez";

        return new UserEntity()
                .setUserName(firstName)
                .setAvatarIndex(3);
    }

    /*public UserEntity createUserEntityWithId() throws Exception {
        UserEntity userEntity = createAndFill(UserEntity.class);

        Faker faker = new Faker();

        userEntity.setUserFirstName(faker.name().firstName());
        userEntity.setUserLastName(faker.name().lastName());
        userEntity.setAvatarIndex(faker.random().nextInt(20));
        userEntity.setMeshId(faker.idNumber().valid());

        return userEntity;
    }*/

    public UserEntity createUserEntityWithId() {

        String firstName = "Daniel";
        String lastName = "Alvez";

        return new UserEntity()
                .setUserName(firstName)
                .setAvatarIndex(3)
                .setMeshId(UUID.randomUUID().toString());
    }

    /*public BaseMeshData createBaseMeshData(UserEntity userEntity) throws Exception {
        BaseMeshData baseMeshData = new BaseMeshData();

        Faker faker = new Faker();

        baseMeshData.mData = userEntity == null ? null : userEntity.getProtoUser().toByteArray();
        baseMeshData.mMeshPeer = new MeshPeer(faker.idNumber().valid());

        return baseMeshData;
    }*/

   /* public BaseMeshData createBaseMeshData(UserEntity userEntity) {
        BaseMeshData baseMeshData = new BaseMeshData();
        baseMeshData.mData = userEntity == null ? null : new Gson().toJson(userEntity.getProtoUser()).getBytes();
        baseMeshData.mMeshPeer = new MeshPeer(UUID.randomUUID().toString());

        return baseMeshData;
    }*/

    /*public RMDataModel createRMDataModel() throws Exception {

        Faker faker = new Faker();

        return RMDataModel.newBuilder()
                .setRawData(ByteString.copyFrom("Hi".getBytes()))
                .setUserMeshId(faker.idNumber().valid())
                .setDataType(1).build();
    }*/

    public DataModel createRMDataModel() {


        return new DataModel()
                .setRawData("Hi".getBytes())
                .setDataType(Constants.DataType.MESSAGE_FEED);

        /*return RMDataModel.newBuilder()
                .setRawData(ByteString.copyFrom("Hi".getBytes()))
                .setUserMeshId(UUID.randomUUID().toString())
                .setDataType(1).build();*/
    }

    /*public ChatEntity createChatEntity(String userId) throws Exception {
        MessageEntity messageEntity = createAndFill(MessageEntity.class);

        Faker faker = new Faker();

        messageEntity.setMessage("Hi")
                .setFriendsId(userId)
                .setMessageId(faker.idNumber().valid())
                .setIncoming(true)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_SENDING);

        return messageEntity;
    }*/

    public ChatEntity createChatEntity(String userId) {

        return new MessageEntity().setMessage("Hi")
                .setFriendsId(userId)
                .setMessageId(UUID.randomUUID().toString())
                .setIncoming(true)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_SENDING);
    }

    /*public ChatEntity createReceiverChatEntity(String userId) throws Exception {
        MessageEntity messageEntity = createAndFill(MessageEntity.class);

        Faker faker = new Faker();

        messageEntity.setMessage("Hi")
                .setFriendsId(userId)
                .setMessageId(faker.idNumber().valid())
                .setIncoming(true)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_UNREAD);

        return messageEntity;
    }*/

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

    public MeshAcknowledgement createAckRmDataModel(String userId, long transferId) {
        MeshAcknowledgement meshAcknowledgement = new MeshAcknowledgement(String.valueOf(transferId));
        meshAcknowledgement.mMeshPeer = new MeshPeer(userId);
        return meshAcknowledgement;
    }

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

    public UserModel createUserModel(UserEntity entity){

        return entity.getProtoUser();
    }

    public BulletinModel getBulletinModel(){
        BulletinModel model = new BulletinModel();
        model.setId(UUID.randomUUID().toString());
        model.setMessage("Test message");
        model.setTime("2019-08-02T06:05:30.000Z");
        return model;
    }
}
