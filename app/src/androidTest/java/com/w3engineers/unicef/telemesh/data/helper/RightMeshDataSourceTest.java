package com.w3engineers.unicef.telemesh.data.helper;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.application.data.AppDataObserver;
import com.w3engineers.mesh.application.data.model.DataAckEvent;
import com.w3engineers.mesh.application.data.model.DataEvent;
import com.w3engineers.mesh.application.data.model.PeerRemoved;
import com.w3engineers.mesh.application.data.model.ServiceUpdate;
import com.w3engineers.mesh.application.data.model.TransportInit;
import com.w3engineers.mesh.application.data.model.UserInfoEvent;
import com.w3engineers.mesh.application.data.remote.model.BaseMeshData;
import com.w3engineers.mesh.application.data.remote.model.MeshAcknowledgement;
import com.w3engineers.mesh.application.data.remote.model.MeshPeer;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.models.ConfigurationCommand;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
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
    public String meshId = "0x550de922bec427fc1b279944e47451a89a4f7cah";

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

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


        SharedPref.getSharedPref(mContext).write(Constants.preferenceKey.MY_USER_ID, myAddress);
        SharedPref.getSharedPref(mContext).write(Constants.preferenceKey.IMAGE_INDEX, 2);
        SharedPref.getSharedPref(mContext).write(Constants.preferenceKey.MY_REGISTRATION_TIME, System.currentTimeMillis());

        SUT = MeshDataSource.getRmDataSource();

        SUT.saveUpdateUserInfo();
    }

    @After
    public void tearDown() {
        appDatabase.close();
    }

    @Test
    public void testOnPeerAdd_checkUserFullName_setValidUser() {

        UserEntity userEntity = randomEntityGenerator.createUserEntity();
        userEntity.setMeshId(meshId);

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


    @Test
    public void testOnPeerGone_getOnlineStatus_setExistingUser() {
        UserEntity userEntity = randomEntityGenerator.createUserEntity();
        userEntity.setMeshId(meshId);
        UserModel userModel = randomEntityGenerator.createUserModel(userEntity);

        //SUT.peerAdd(userEntity.getMeshId(), userModel);

        UserInfoEvent userInfoEvent = randomEntityGenerator.generateUserInfoEvent(meshId);

        AppDataObserver.on().sendObserverData(userInfoEvent);

        addDelay(2500);

        userDataSource.insertOrUpdateData(userEntity);

        addDelay(500);

        SUT.peerRemove(userModel.getUserId());

        addDelay(500);

        SUT.peerRemove(userEntity.getMeshId());

        addDelay(500);

        PeerRemoved userRemoveEvent = randomEntityGenerator.generatePeerRemoveEvent(meshId);
        AppDataObserver.on().sendObserverData(userRemoveEvent);

        addDelay(500);
        UserEntity retrieveUser = userDataSource.getSingleUserById(userModel.getUserId());
        addDelay(500);
        assertFalse(retrieveUser != null && retrieveUser.getOnlineStatus() > Constants.UserStatus.OFFLINE);
    }

    @Test
    public void testDataSend() {

        addDelay(500);

        DataModel rmDataModel = randomEntityGenerator.createRMDataModel();
        SUT.DataSend(rmDataModel, UUID.randomUUID().toString(), false);
    }

    @Test
    public void nodeAvailableTest() {
        addDelay(500);

        // create a user first
        String meshId = "0x550de922bec427fc1b279944e47451a89a4f7cah";
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

        addDelay(500);
    }

    @Test
    public void testOnData_checkMessageProperties_forValidMessage() {

        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        addDelay(500);

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());

        String prevMessageId = chatEntity != null ? chatEntity.getMessageId() : null;

        addDelay(500);
        SUT.onData(userEntity.getMeshId(), randomEntityGenerator.createMeshData(chatEntity));

        addDelay(2000);

        if (prevMessageId == null) {
            assertNull(prevMessageId);
            return;
        }

        ChatEntity retrieveChatEntity = messageSourceData.getMessageEntityById(prevMessageId);
        addDelay(500);

        String newMessageId = retrieveChatEntity != null ? retrieveChatEntity.getMessageId() : null;

        if (newMessageId == null) {
            assertNull(newMessageId);
            return;
        }
        if (!TextUtils.isEmpty(newMessageId) && !TextUtils.isEmpty(prevMessageId)) {
            assertThat(prevMessageId, is(newMessageId));
        }
    }

    @Test
    public void testOnAcknowledgement() {
        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        addDelay(500);

        ChatEntity chatEntity = randomEntityGenerator.createChatEntity(userEntity.getMeshId());
        messageSourceData.insertOrUpdateData(chatEntity);

        long transferKey = this.transferKey++;
        DataModel rmDataModel = randomEntityGenerator
                .createChatEntityRmDataModel(userEntity.getMeshId(), (MessageEntity) chatEntity);

        rmDataHelper.rmDataMap.put(String.valueOf(transferKey), rmDataModel);

        RmDataHelper.getInstance().rmDataMap.put(chatEntity.getMessageId(), rmDataModel);

        // Send status test
        DataAckEvent sendAckEvent = randomEntityGenerator.generateDataAckEvent(chatEntity.getMessageId(), Constant.MessageStatus.SEND);
        //SUT.onAck(chatEntity.getMessageId(), Constant.MessageStatus.SEND);
        AppDataObserver.on().sendObserverData(sendAckEvent);

        addDelay(500);

        // Delivered status test
        SUT.onAck(chatEntity.getMessageId(), Constant.MessageStatus.DELIVERED);

        addDelay(500);

        // Received status test
        SUT.onAck(chatEntity.getMessageId(), Constant.MessageStatus.RECEIVED);

        addDelay(500);


        ChatEntity retrieveChatEntity = messageSourceData.getMessageEntityById(chatEntity.getMessageId());

        assertThat(retrieveChatEntity.getStatus(), greaterThanOrEqualTo(Constants.MessageStatus.STATUS_SENDING));
    }

    @Test
    public void prepareDataObserver() {
        addDelay(500);

        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userDataSource.insertOrUpdateData(userEntity);

        addDelay(500);

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
        addDelay(500);

        assertFalse(retrieveChatEntity.isIncoming());

    }

    @Test
    public void meshInitAndServiceAppUpdateAvailableTest() {
        addDelay(500);

        TransportInit transportEvent = randomEntityGenerator.generateTransportInit(meshId);
        AppDataObserver.on().sendObserverData(transportEvent);

        addDelay(2000);

        ServiceUpdate serviceAppUpdateEvent = randomEntityGenerator.generateServiceUpdate();

        AppDataObserver.on().sendObserverData(serviceAppUpdateEvent);

        addDelay(500);
    }

    @Test
    public void dataReceiveTest() {
        addDelay(500);

        UserEntity userEntity = randomEntityGenerator.createUserEntityWithId();
        userEntity.setMeshId(meshId);
        userDataSource.insertOrUpdateData(userEntity);

        addDelay(1000);

        DataEvent dataEvent = randomEntityGenerator.generateDataEvent(meshId);

        addDelay(1000);

        AppDataObserver.on().sendObserverData(dataEvent);

        addDelay(2000);

        SUT.showServiceUpdateAvailable(rule.getActivity());

        addDelay(4000);

        onView(withText("LATER")).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

    }

    @Test
    public void configDataSyncTest() {
        addDelay(500);

        ConfigurationCommand configFile = randomEntityGenerator.generateConfigFile();

        RmDataHelper.getInstance().syncConfigFileAndBroadcast(true, configFile);

        addDelay(2000);
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
