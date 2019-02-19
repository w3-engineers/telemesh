package com.w3engineers.unicef.telemesh.data.helper;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.javafaker.Faker;
import com.w3engineers.ext.viper.application.data.remote.model.BaseMeshData;
import com.w3engineers.ext.viper.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.ext.viper.application.data.remote.model.MeshPeer;
import com.w3engineers.unicef.telemesh.TeleMeshUser;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [08-Feb-2019 at 4:58 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [08-Feb-2019 at 4:58 PM].
 * --> <Second Editor> on [08-Feb-2019 at 4:58 PM].
 * Reviewed by :
 * --> <First Reviewer> on [08-Feb-2019 at 4:58 PM].
 * --> <Second Reviewer> on [08-Feb-2019 at 4:58 PM].
 * ============================================================================
 **/
@RunWith(AndroidJUnit4.class)
public class RightMeshDataSourceTest {

    private AppDatabase appDatabase;
    private UserDataSource userDataSource;
    private MessageSourceData messageSourceData;

    private Source source;
    private RmDataHelper rmDataHelper;

    RightMeshDataSource SUT;
    RandomEntityGenerator randomEntityGenerator;

    @Before
    public void setUp() throws Exception {

        randomEntityGenerator = new RandomEntityGenerator();

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = UserDataSource.getInstance(appDatabase.userDao());
        messageSourceData = MessageSourceData.getInstance(appDatabase.messageDao());

        source = new Source(appDatabase);

        rmDataHelper = RmDataHelper.getInstance();
        rmDataHelper.initSource(source);

        UserEntity userEntity = randomEntityGenerator.createUserEntity();
        SUT = new RightMeshDataSource(userEntity.getProtoUser().toByteArray());

        SUT.onRmOn();
    }

    /*@Test
    public void testOnPeerAdd_getNonNull_setValidUser() throws Exception {

        UserEntity userEntity = randomEntityGenerator.createUserEntity();

        BaseMeshData baseMeshData = randomEntityGenerator.createBaseMeshData(userEntity);

        SUT.onPeer(baseMeshData);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        UserEntity retrieveUser = userDataSource.getSingleUserById(baseMeshData.mMeshPeer.getPeerId());

        assertNotNull(retrieveUser);
    }*/

    @Test
    public void testOnPeerAdd_checkUserFullName_setValidUser() throws Exception {

        UserEntity userEntity = randomEntityGenerator.createUserEntity();

        BaseMeshData baseMeshData = randomEntityGenerator.createBaseMeshData(userEntity);

        SUT.onPeer(baseMeshData);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        UserEntity retrieveUser = userDataSource.getSingleUserById(baseMeshData.mMeshPeer.getPeerId());

        assertEquals(userEntity.getFullName(), retrieveUser.getFullName());
    }

    /*@Test
    public void testOnPeerGone_getNonNull_setExistingUser() throws Exception {
        UserEntity userEntity = randomEntityGenerator.createUserEntity();

        BaseMeshData baseMeshData = randomEntityGenerator.createBaseMeshData(userEntity);
        SUT.onPeer(baseMeshData);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MeshPeer meshPeer = baseMeshData.mMeshPeer;
        SUT.onPeerGone(meshPeer);

        UserEntity retrieveUser = userDataSource.getSingleUserById(baseMeshData.mMeshPeer.getPeerId());
        assertNotNull(retrieveUser);
    }*/

    @Test
    public void testOnPeerGone_getOnlineStatus_setExistingUser() throws Exception {
        UserEntity userEntity = randomEntityGenerator.createUserEntity();

        BaseMeshData baseMeshData = randomEntityGenerator.createBaseMeshData(userEntity);
        SUT.onPeer(baseMeshData);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MeshPeer meshPeer = baseMeshData.mMeshPeer;
        SUT.onPeerGone(meshPeer);

        UserEntity retrieveUser = userDataSource.getSingleUserById(baseMeshData.mMeshPeer.getPeerId());
        assertFalse(retrieveUser.isOnline());
    }

    @Test
    public void testDataSend() throws Exception {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TeleMeshUser.RMDataModel rmDataModel = randomEntityGenerator.createRMDataModel();
        SUT.DataSend(rmDataModel);
    }

    /*@Test
    public void testOnData_checkNull_forValidMessage() throws Exception {
        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());

        SUT.onData(randomEntityGenerator.createMeshData(userEntity.getMeshId(), chatEntity));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ChatEntity retrieveChatEntity = messageSourceData.getMessageEntityById(chatEntity.getMessageId());

        assertNotNull(retrieveChatEntity);
    }*/

    @Test
    public void testOnData_checkMessageProperties_forValidMessage() throws Exception {

        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());

        SUT.onData(randomEntityGenerator.createMeshData(userEntity.getMeshId(), chatEntity));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ChatEntity retrieveChatEntity = messageSourceData.getMessageEntityById(chatEntity.getMessageId());

        assertThat(chatEntity.getMessageId(), is(retrieveChatEntity.getMessageId()));
    }

    @Test
    public void testOnAcknowledgement() throws Exception {
        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());
        messageSourceData.insertOrUpdateData(chatEntity);

        int transferKey = Faker.instance().random().nextInt(100);
        TeleMeshUser.RMDataModel rmDataModel = randomEntityGenerator
                .createChatEntityRmDataModel(userEntity.getMeshId(), (MessageEntity) chatEntity);

        rmDataHelper.rmDataMap.put(transferKey, rmDataModel);

        MeshAcknowledgement meshAcknowledgement = randomEntityGenerator.createAckRmDataModel(userEntity.getMeshId(), transferKey);

        SUT.onAcknowledgement(meshAcknowledgement);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ChatEntity retrieveChatEntity = messageSourceData.getMessageEntityById(chatEntity.getMessageId());

        assertThat(retrieveChatEntity.getStatus(), greaterThanOrEqualTo(Constants.MessageStatus.STATUS_SENDING));
    }

    @After
    public void tearDown() throws Exception {
//        SUT.onRmOff();
    }
}