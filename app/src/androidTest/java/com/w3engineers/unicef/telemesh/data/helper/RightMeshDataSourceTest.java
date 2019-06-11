package com.w3engineers.unicef.telemesh.data.helper;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

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

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
@RunWith(AndroidJUnit4.class)
public class RightMeshDataSourceTest {

    private UserDataSource userDataSource;
    private MessageSourceData messageSourceData;

    private RmDataHelper rmDataHelper;

    private MeshDataSource SUT;
    private RandomEntityGenerator randomEntityGenerator;
    private long transferKey = 2381;

    @Before
    public void setUp() {

        randomEntityGenerator = new RandomEntityGenerator();

        AppDatabase appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = UserDataSource.getInstance(appDatabase.userDao());
        messageSourceData = MessageSourceData.getInstance(appDatabase.messageDao());

        Source source = new Source(appDatabase);

        rmDataHelper = RmDataHelper.getInstance();
        rmDataHelper.initSource(source);

        UserEntity userEntity = randomEntityGenerator.createUserEntity();
        SUT = new MeshDataSource(userEntity.getProtoUser().toByteArray());

        SUT.onRmOn();
    }

    @Test
    public void testOnPeerAdd_checkUserFullName_setValidUser() {

        UserEntity userEntity = randomEntityGenerator.createUserEntity();

        BaseMeshData baseMeshData = randomEntityGenerator.createBaseMeshData(userEntity);

        SUT.onPeer(baseMeshData);

        addDelay();

        UserEntity retrieveUser = userDataSource.getSingleUserById(baseMeshData.mMeshPeer.getPeerId());

        String retrieveFullName = retrieveUser == null ? null : retrieveUser.getFullName();

        assertEquals(userEntity.getFullName(), retrieveFullName);
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
    public void testOnPeerGone_getOnlineStatus_setExistingUser() {
        UserEntity userEntity = randomEntityGenerator.createUserEntity();

        BaseMeshData baseMeshData = randomEntityGenerator.createBaseMeshData(userEntity);
        SUT.onPeer(baseMeshData);

        addDelay();

        MeshPeer meshPeer = baseMeshData.mMeshPeer;
        SUT.onPeerGone(meshPeer);

        UserEntity retrieveUser = userDataSource.getSingleUserById(baseMeshData.mMeshPeer.getPeerId());
        assertFalse(retrieveUser !=null && retrieveUser.isOnline());
    }

    @Test
    public void testDataSend() {

        addDelay();

        TeleMeshUser.RMDataModel rmDataModel = randomEntityGenerator.createRMDataModel();
        SUT.DataSend(rmDataModel);
    }

    private void addDelay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    public void testOnData_checkMessageProperties_forValidMessage() {

        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        addDelay();

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());

        SUT.onData(randomEntityGenerator.createMeshData(userEntity.getMeshId(), chatEntity));

        addDelay();

        ChatEntity retrieveChatEntity = messageSourceData.getMessageEntityById(chatEntity.getMessageId());

        assertThat(chatEntity.getMessageId(), is(retrieveChatEntity.getMessageId()));
    }

    @Test
    public void testOnAcknowledgement() {
        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        addDelay();

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());
        messageSourceData.insertOrUpdateData(chatEntity);

        long transferKey = this.transferKey++;
        TeleMeshUser.RMDataModel rmDataModel = randomEntityGenerator
                .createChatEntityRmDataModel(userEntity.getMeshId(), (MessageEntity) chatEntity);

        rmDataHelper.rmDataMap.put(transferKey, rmDataModel);

        MeshAcknowledgement meshAcknowledgement = randomEntityGenerator.createAckRmDataModel(userEntity.getMeshId(), transferKey);

        SUT.onAcknowledgement(meshAcknowledgement);

        addDelay();

        ChatEntity retrieveChatEntity = messageSourceData.getMessageEntityById(chatEntity.getMessageId());

        assertThat(retrieveChatEntity.getStatus(), greaterThanOrEqualTo(Constants.MessageStatus.STATUS_SENDING));
    }

    @After
    public void tearDown() {
//        SUT.onRmOff();
    }
}