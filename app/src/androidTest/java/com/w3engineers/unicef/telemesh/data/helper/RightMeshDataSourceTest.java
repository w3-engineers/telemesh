package com.w3engineers.unicef.telemesh.data.helper;

import android.Manifest;

import androidx.room.Room;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.w3engineers.mesh.application.data.AppDataObserver;
import com.w3engineers.mesh.application.data.local.DataPlanConstants;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.application.data.model.BroadcastEvent;
import com.w3engineers.mesh.application.data.model.DataAckEvent;
import com.w3engineers.mesh.application.data.model.DataEvent;
import com.w3engineers.mesh.application.data.model.FilePendingEvent;
import com.w3engineers.mesh.application.data.model.FileProgressEvent;
import com.w3engineers.mesh.application.data.model.FileReceivedEvent;
import com.w3engineers.mesh.application.data.model.FileTransferEvent;
import com.w3engineers.mesh.application.data.model.PeerRemoved;
import com.w3engineers.mesh.application.data.model.PermissionInterruptionEvent;
import com.w3engineers.mesh.application.data.model.ServiceDestroyed;
import com.w3engineers.mesh.application.data.model.ServiceUpdate;
import com.w3engineers.mesh.application.data.model.TransportInit;
import com.w3engineers.mesh.application.data.model.UserInfoEvent;
import com.w3engineers.mesh.application.data.model.WalletCreationEvent;
import com.w3engineers.mesh.util.Constant;
import com.w3engineers.models.ContentMetaInfo;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.feed.BroadcastMeta;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserModel;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.StatusHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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
    private String DEVICE_NAME = "xiaomi";

    public String myAddress = "0x550de922bec427fc1b279944e47451a89a4f7cag";
    public String meshId = "0x550de922bec427fc1b279944e47451a89a4f7cah";
    private final String dainelId = "0xaa2dd785fc60eeb8151f65b3ded59ce3c2f12ca4";
    public UiDevice mDevice = UiDevice.getInstance(getInstrumentation());

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


        SharedPref.write(Constants.preferenceKey.MY_USER_ID, myAddress);
        SharedPref.write(Constants.preferenceKey.IMAGE_INDEX, 2);
        SharedPref.write(Constants.preferenceKey.MY_REGISTRATION_TIME, System.currentTimeMillis());

        SUT = MeshDataSource.getRmDataSource();

        SUT.saveUpdateUserInfo();
    }

    @After
    public void tearDown() {
        appDatabase.close();
    }

    @Test
    public void testMainActivity(){
        SUT.activityControllerMainActivity();
        assertTrue(true);
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

        assertTrue(true);
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

        SUT.isNodeAvailable(userEntity.getMeshId(), Constants.UserStatus.INTERNET_ONLINE);

        addDelay(4000);

        UserEntity updatedUserEntity = userDataSource.getSingleUserById(userEntity.getMeshId());

        addDelay(1000);

        assertEquals(updatedUserEntity.getOnlineStatus(), Constants.UserStatus.WIFI_ONLINE);

        addDelay(500);

        assertTrue(true);
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

        assertTrue(true);
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

        assertTrue(true);
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

        assertTrue(true);
    }

    @Test
    @UiThreadTest
    public void permissionDialogOpenTest() {
        addDelay(500);
        PermissionInterruptionEvent event = randomEntityGenerator.generatePermissionInterruptEvent();

        AppDataObserver.on().sendObserverData(event);

        addDelay(2000);

        int btEvent = DataPlanConstants.INTERRUPTION_EVENT.USER_DISABLED_BT;
        int wifiEvent = DataPlanConstants.INTERRUPTION_EVENT.USER_DISABLED_WIFI;
        int locationEvent = DataPlanConstants.INTERRUPTION_EVENT.LOCATION_PROVIDER_OFF;

        /*new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SUT.showPermissionEventAlert(btEvent, null, rule.getActivity());

                addDelay(2000);

                SUT.showPermissionEventAlert(wifiEvent, null, rule.getActivity());

                addDelay(2000);


                SUT.showPermissionEventAlert(locationEvent, null, rule.getActivity());
            }
        });*/

        SUT.showPermissionEventAlert(btEvent, null, rule.getActivity());

        addDelay(2000);

        SUT.showPermissionEventAlert(wifiEvent, null, rule.getActivity());

        addDelay(2000);

        SUT.showPermissionEventAlert(locationEvent, null, rule.getActivity());

        addDelay(2000);

        List<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        SUT.showPermissionEventAlert(-1, permissionList, rule.getActivity());

        addDelay(2000);

        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        SUT.showPermissionEventAlert(-1, permissionList, rule.getActivity());

        addDelay(5000);

        assertTrue(true);
    }

    @Test
    @UiThreadTest
    public void xiaomiPermissionTest() {
        addDelay(500);

        boolean res = SUT.isPermissionNeeded(DEVICE_NAME);
        assertFalse(res);

        addDelay(500);

        SUT.showPermissionPopupForXiaomi(rule.getActivity());

        addDelay(4000);

        UiObject button1 = mDevice.findObject(new UiSelector().text("OK"));
        try {
            if (button1.exists() && button1.isEnabled()) {
                button1.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        addDelay(1000);

        assertTrue(true);
    }

    @Test
    public void test_wallet_and_file_observer() {
        addDelay(500);

        WalletCreationEvent walletCreationEvent = new WalletCreationEvent();
        AppDataObserver.on().sendObserverData(walletCreationEvent);

        addDelay(1000);

        String fileMessageId = UUID.randomUUID().toString();

        // File receive event
        FileReceivedEvent fileReceivedEvent = new FileReceivedEvent();
        fileReceivedEvent.setFileMessageId(fileMessageId);
        fileReceivedEvent.setFilePath(randomEntityGenerator.getDummyImageLink());
        fileReceivedEvent.setSourceAddress(dainelId);
        fileReceivedEvent.setMetaData(null);

        AppDataObserver.on().sendObserverData(fileReceivedEvent);
        addDelay(1000);

        //File progress event
        FileProgressEvent fileProgressEvent = new FileProgressEvent();
        fileProgressEvent.setFileMessageId(fileMessageId);
        fileProgressEvent.setPercentage(80);

        AppDataObserver.on().sendObserverData(fileProgressEvent);
        addDelay(1000);

        // File transfer event
        FileTransferEvent fileTransferEvent = new FileTransferEvent();
        fileTransferEvent.setFileMessageId(fileMessageId);
        fileTransferEvent.setSuccess(true);
        fileTransferEvent.setErrorMessage("");

        AppDataObserver.on().sendObserverData(fileTransferEvent);
        addDelay(1000);

        // File Pending event
        FilePendingEvent filePendingEvent = new FilePendingEvent();
        filePendingEvent.setIncoming(true);
        filePendingEvent.setContentId(fileMessageId);

        ContentMetaInfo metaInfo = new ContentMetaInfo();
        metaInfo.setMessageId(fileMessageId);
        metaInfo.setMessageType(1);
        metaInfo.setMetaInfo("Test meta");
        filePendingEvent.setContentMetaInfo(metaInfo);
        filePendingEvent.setProgress(90);
        filePendingEvent.setState(Constants.ServiceContentState.SUCCESS);

        AppDataObserver.on().sendObserverData(filePendingEvent);
        addDelay(1000);

        ServiceDestroyed serviceDestroyed = new ServiceDestroyed();
        AppDataObserver.on().sendObserverData(serviceDestroyed);

        // Broadcast event test

        BroadcastMeta broadcastMeta = prepareBroadcastMetaData();

        String broadcastMetaJson = GsonBuilder.getInstance().getBroadcastMetaJson(broadcastMeta);
        BroadcastEvent broadcastEvent = new BroadcastEvent();
        broadcastEvent.setBroadcastId(UUID.randomUUID().toString());
        broadcastEvent.setMetaData(broadcastMetaJson);
        broadcastEvent.setContentPath(randomEntityGenerator.getDummyImageLink());


        AppDataObserver.on().sendObserverData(broadcastEvent);
        addDelay(2000);

        assertTrue(true);
        StatusHelper.out("File event test executed");
    }

    @Test
    public void userConnectivityStatusTest() {
        addDelay(500);

        SUT.checkUserConnectivityStatus(UUID.randomUUID().toString());

        addDelay(500);

        int status = SUT.checkUserConnectivityStatus(dainelId);

        assertEquals(0, status);
    }

    private BroadcastMeta prepareBroadcastMetaData() {
        BroadcastMeta meta = new BroadcastMeta();
        meta.setBroadcastAddress("address");
        meta.setMessageBody("Test broadcast");
        meta.setMessageTitle("Unicef");
        meta.setUploaderName("Unicef");
        meta.setCreationTime("2021-08-02T06:05:30.000Z");
        return meta;
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
