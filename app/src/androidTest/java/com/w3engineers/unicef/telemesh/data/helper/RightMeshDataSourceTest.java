package com.w3engineers.unicef.telemesh.data.helper;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.remote.model.BaseMeshData;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

    private AppDatabase appDatabase;
    private Context mContext;
    private Source source;

    public String myAddress = "0x550de922bec427fc1b279944e47451a89a4f7cag";

    @Before
    public void setUp() {

        randomEntityGenerator = new RandomEntityGenerator();
        mContext = InstrumentationRegistry.getContext();
        appDatabase = Room.inMemoryDatabaseBuilder(mContext,
                AppDatabase.class).allowMainThreadQueries().build();

        // userDataSource = UserDataSource.getInstance(appDatabase.userDao());
        // messageSourceData = MessageSourceData.getInstance(appDatabase.messageDao());

        userDataSource = UserDataSource.getInstance();
        messageSourceData = MessageSourceData.getInstance();

        source = new Source(appDatabase);

        rmDataHelper = RmDataHelper.getInstance();
        rmDataHelper.initSource(source);

        UserEntity userEntity = randomEntityGenerator.createUserEntity();

        SharedPref.getSharedPref(mContext).write(Constants.preferenceKey.MY_USER_ID, myAddress);
        SharedPref.getSharedPref(mContext).write(Constants.preferenceKey.IMAGE_INDEX, 2);
        SharedPref.getSharedPref(mContext).write(Constants.preferenceKey.MY_REGISTRATION_TIME, System.currentTimeMillis());

        SUT = MeshDataSource.getRmDataSource();
    }

    @After
    public void tearDown() {
        appDatabase.close();
    }

    @Test
    public void testOnPeerAdd_checkUserFullName_setValidUser() {

        UserEntity userEntity = randomEntityGenerator.createUserEntity();

        UserModel userModel = randomEntityGenerator.createUserModel(userEntity);

        String byteData = new Gson().toJson(userModel);

        SUT.peerAdd(userEntity.getMeshId(), byteData.getBytes());

        addDelay(2000);

        UserEntity retrieveUser = userDataSource.getSingleUserById(userModel.getUserId());
        addDelay(2000);

        String retrieveFullName = retrieveUser == null ? null : retrieveUser.getFullName();
        if (retrieveFullName == null) {
            assertNull(retrieveFullName);
            return;
        }

        String realUserName = userEntity == null ? null : userEntity.getFullName();
        if (realUserName == null) {
            assertNull(realUserName);
            return;
        }
        assertEquals(realUserName, retrieveFullName);
    }


   /* @Test
    public void testOnPeerGone_getOnlineStatus_setExistingUser() {
        UserEntity userEntity = randomEntityGenerator.createUserEntity();

        BaseMeshData baseMeshData = randomEntityGenerator.createBaseMeshData(userEntity);
        SUT.onPeer(baseMeshData);

        addDelay();

        MeshPeer meshPeer = baseMeshData.mMeshPeer;
        SUT.onPeerGone(meshPeer);
        addDelay();
        UserEntity retrieveUser = userDataSource.getSingleUserById(baseMeshData.mMeshPeer.getPeerId());
        addDelay();
        assertFalse(retrieveUser != null && retrieveUser.getOnlineStatus() > Constants.UserStatus.OFFLINE);
    }*/

   /* @Test
    public void testDataSend() {

        addDelay();

        DataModel rmDataModel = randomEntityGenerator.createRMDataModel();
        SUT.DataSend(rmDataModel, UUID.randomUUID().toString());
    }*/

   /* @Test
    public void onlyNodeAddedTest() {
        addDelay();

        String nodeId = "0x3988dbfkjdf984rc9";
        SUT.nodeIdDiscovered(nodeId);

        addDelay(3000);

        UserEntity userEntity = userDataSource.getSingleUserById(nodeId);
        addDelay(1000);
        assertEquals(userEntity.getMeshId(), nodeId);

        addDelay();
    }*/

   /* @Test
    public void nodeAvailableTest() {
        addDelay();

        // create a user first
        String meshId = "0x3988dbfkjdf984rc9";
        UserEntity userEntity = randomEntityGenerator.createUserEntity();
        userEntity.setMeshId(meshId);
        addDelay(700);

        userDataSource.insertOrUpdateData(userEntity);
        addDelay(1000);

        SUT.isNodeAvailable(userEntity.getMeshId(), Constants.UserStatus.BLE_ONLINE);

        addDelay(4000);

        UserEntity updatedUserEntity = userDataSource.getSingleUserById(userEntity.getMeshId());

        addDelay(1000);

        assertEquals(updatedUserEntity.getOnlineStatus(), Constants.UserStatus.BLE_ONLINE);

        addDelay();

        // ownId test
        SharedPref.getSharedPref(mContext).write(Constants.preferenceKey.MY_USER_ID, meshId);
        String myUserId = SUT.getOwnUserId();
        assertEquals(myUserId, meshId);

        addDelay();
    }*/


   /* @Test
    public void testOnData_checkMessageProperties_forValidMessage() {

        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        addDelay();

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());

        String prevMessageId = chatEntity != null ? chatEntity.getMessageId() : null;

        addDelay();
        SUT.onData(randomEntityGenerator.createMeshData(userEntity.getMeshId(), chatEntity));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (prevMessageId == null) {
            assertNull(prevMessageId);
            return;
        }

        ChatEntity retrieveChatEntity = messageSourceData.getMessageEntityById(prevMessageId);
        addDelay();

        String newMessageId = retrieveChatEntity != null ? retrieveChatEntity.getMessageId() : null;

        if (newMessageId == null) {
            assertNull(newMessageId);
            return;
        }
        if (!TextUtils.isEmpty(newMessageId) && !TextUtils.isEmpty(prevMessageId)) {
            assertThat(prevMessageId, is(newMessageId));
        }


    }*/

   /* @Test
    public void testOnAcknowledgement() {
        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        addDelay();

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());
        messageSourceData.insertOrUpdateData(chatEntity);

        long transferKey = this.transferKey++;
        DataModel rmDataModel = randomEntityGenerator
                .createChatEntityRmDataModel(userEntity.getMeshId(), (MessageEntity) chatEntity);

        rmDataHelper.rmDataMap.put(String.valueOf(transferKey), rmDataModel);

        MeshAcknowledgement meshAcknowledgement = randomEntityGenerator.createAckRmDataModel(userEntity.getMeshId(), transferKey);

        SUT.onAcknowledgement(meshAcknowledgement);

        addDelay();

        ChatEntity retrieveChatEntity = messageSourceData.getMessageEntityById(chatEntity.getMessageId());

        assertThat(retrieveChatEntity.getStatus(), greaterThanOrEqualTo(Constants.MessageStatus.STATUS_SENDING));
    }*/

   /* @Test
    public void prepareDataObserver() {
        addDelay();

        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        addDelay();

        ChatEntity failEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());
        failEntity.setIncoming(false);
        messageSourceData.insertOrUpdateData(failEntity);
        source.reSendMessage(failEntity);
        addDelay(1000);

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());
        chatEntity.setIncoming(false);
        messageSourceData.insertOrUpdateData(chatEntity);

        addDelay(1000);

        RmDataHelper.getInstance().prepareDataObserver();

        addDelay(3000);

        ChatEntity retrieveChatEntity = messageSourceData.getMessageEntityById(chatEntity.getMessageId());
        addDelay();

        assertFalse(retrieveChatEntity.isIncoming());

    }*/


    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
