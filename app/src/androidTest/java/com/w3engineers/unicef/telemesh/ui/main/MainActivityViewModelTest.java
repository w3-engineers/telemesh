package com.w3engineers.unicef.telemesh.ui.main;

import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityViewModelTest {

    private UserEntity userEntity;

    private MainActivityViewModel SUT;
    private UserDataSource userDataSource;
    private MessageSourceData messageSourceData;

    @Before
    public void setUp() {

        userDataSource = UserDataSource.getInstance();

        messageSourceData = MessageSourceData.getInstance();

        SUT = new MainActivityViewModel();

        // Region constant
        String FIRST_NAME = "Danial";
        String LAST_NAME = "Alvez";
        int AVATAR_INDEX = 2;

        userEntity = new UserEntity()
                .setUserFirstName(FIRST_NAME)
                .setUserLastName(LAST_NAME)
                .setAvatarIndex(AVATAR_INDEX);
    }

    @Test
    public void testUserOfflineProcess_getOfflineState_afterOnlineState() {

        String userMeshId = UUID.randomUUID().toString();
        userEntity.setMeshId(userMeshId).setOnline(true);

        userDataSource.insertOrUpdateData(userEntity);

        SUT.userOfflineProcess();

        // Adding a time sleep for processing offline status
        addDelay();

        UserEntity userEntity = userDataSource.getSingleUserById(userMeshId);
        boolean isOnline = userEntity !=null && userEntity.isOnline();

        assertFalse(isOnline);
    }

    private void addDelay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMakeSendingMessageAsFailed_getFailedState_whenMessageIsSending() {

        String userMeshId = UUID.randomUUID().toString();
        userEntity.setMeshId(userMeshId).setOnline(true);

        userDataSource.insertOrUpdateData(userEntity);

        int messageStatus = Constants.MessageStatus.STATUS_SENDING;

        MessageEntity messageEntity = getMessageEntity(userMeshId, messageStatus);
        messageSourceData.insertOrUpdateData(messageEntity);

        SUT.makeSendingMessageAsFailed();

        // Adding a time sleep for processing offline status
        addDelay();

        assertEquals(messageSourceData.getMessageEntityById(messageEntity.getMessageId()).getStatus(),
                Constants.MessageStatus.STATUS_FAILED);
    }

    @Test
    public void testMakeSendingMessageAsFailed_getDeliverState_whenMessageIsDeliver() {

        String userMeshId = UUID.randomUUID().toString();
        userEntity.setMeshId(userMeshId).setOnline(true);

        userDataSource.insertOrUpdateData(userEntity);

        int messageStatus = Constants.MessageStatus.STATUS_DELIVERED;

        MessageEntity messageEntity = getMessageEntity(userMeshId, messageStatus);
        messageSourceData.insertOrUpdateData(messageEntity);

        SUT.makeSendingMessageAsFailed();

        // Adding a time sleep for processing offline status
        addDelay();

        assertEquals(messageSourceData.getMessageEntityById(messageEntity.getMessageId()).getStatus(), messageStatus);
    }

    private MessageEntity getMessageEntity(String userMeshId, int messageStatus) {
        String messageId = UUID.randomUUID().toString();

        MessageEntity messageEntity = new MessageEntity().setMessage("Hi");
        messageEntity.setFriendsId(userMeshId)
                .setMessageId(messageId)
                .setStatus(messageStatus)
                .setTime(System.currentTimeMillis())
                .setIncoming(false)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE);

        return messageEntity;
    }

    @After
    public void tearDown() {

    }
}