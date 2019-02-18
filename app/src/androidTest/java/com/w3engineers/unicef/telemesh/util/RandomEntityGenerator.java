package com.w3engineers.unicef.telemesh.util;

import com.github.javafaker.Faker;
import com.google.protobuf.ByteString;
import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.unicef.telemesh.TeleMeshUser.RMDataModel;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-01-31 at 11:20 AM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: telemesh.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-01-31 at 11:20 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-01-31 at 11:20 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

/**
 * Descendant of {@link RandomGenerator}. Particularly generates {@link UserEntity}.
 * <br/>Put names properly
 * with a library called <a href="https://github.com/DiUS/java-faker">Java Faker</a>
 */
public class RandomEntityGenerator extends RandomGenerator {

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

    public UserEntity createUserEntity() throws Exception {
        UserEntity userEntity = createAndFill(UserEntity.class);

        Faker faker = new Faker();

        userEntity.setUserFirstName(faker.name().firstName());
        userEntity.setUserLastName(faker.name().lastName());
        userEntity.setAvatarIndex(faker.random().nextInt(20));

        return userEntity;
    }

    public UserEntity createUserEntityWithId() throws Exception {
        UserEntity userEntity = createAndFill(UserEntity.class);

        Faker faker = new Faker();

        userEntity.setUserFirstName(faker.name().firstName());
        userEntity.setUserLastName(faker.name().lastName());
        userEntity.setAvatarIndex(faker.random().nextInt(20));
        userEntity.setMeshId(faker.idNumber().valid());

        return userEntity;
    }

    public BaseMeshData createBaseMeshData(UserEntity userEntity) throws Exception {
        BaseMeshData baseMeshData = new BaseMeshData();

        Faker faker = new Faker();

        baseMeshData.mData = userEntity == null ? null : userEntity.getProtoUser().toByteArray();
        baseMeshData.mMeshPeer = new MeshPeer(faker.idNumber().valid());

        return baseMeshData;
    }

    public RMDataModel createRMDataModel() throws Exception {

        Faker faker = new Faker();

        return RMDataModel.newBuilder()
                .setRawData(ByteString.copyFrom("Hi".getBytes()))
                .setUserMeshId(faker.idNumber().valid())
                .setDataType(1).build();
    }

    public ChatEntity createChatEntity(String userId) throws Exception {
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
    }

    public ChatEntity createReceiverChatEntity(String userId) throws Exception {
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
    }

    public MeshData createMeshData(String userId, ChatEntity chatEntity) throws Exception {
        MeshData meshData = new MeshData();

        meshData.mType = Constants.DataType.MESSAGE;
        meshData.mMeshPeer = new MeshPeer(userId);
        meshData.mData = ((MessageEntity)chatEntity).toProtoChat().toByteArray();

        return meshData;
    }

    public RMDataModel createChatEntityRmDataModel(String userId, MessageEntity chatEntity) {
        return RMDataModel.newBuilder()
                .setUserMeshId(userId)
                .setRawData(ByteString.copyFrom(chatEntity.toProtoChat().toByteArray()))
                .setDataType(Constants.DataType.MESSAGE)
                .build();
    }

    public MeshAcknowledgement createAckRmDataModel(String userId, int transferId) {
        MeshAcknowledgement meshAcknowledgement = new MeshAcknowledgement(transferId);
        meshAcknowledgement.mMeshPeer = new MeshPeer(userId);
        return meshAcknowledgement;
    }

}
