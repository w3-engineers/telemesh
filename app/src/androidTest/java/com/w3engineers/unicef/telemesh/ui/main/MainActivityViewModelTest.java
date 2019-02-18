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

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [31-Jan-2019 at 3:53 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [31-Jan-2019 at 3:53 PM].
 * --> <Second Editor> on [31-Jan-2019 at 3:53 PM].
 * Reviewed by :
 * --> <First Reviewer> on [31-Jan-2019 at 3:53 PM].
 * --> <Second Reviewer> on [31-Jan-2019 at 3:53 PM].
 * ============================================================================
 **/
@RunWith(AndroidJUnit4.class)
public class MainActivityViewModelTest {

    // Region constant
    private static String FIRST_NAME = "Danial";
    private static String LAST_NAME = "Alvez";
    private static int AVATAR_INDEX = 2;

    private UserEntity userEntity;

    private MainActivityViewModel SUT;
    private UserDataSource userDataSource;
    private MessageSourceData messageSourceData;

    @Before
    public void setUp() throws Exception {

        userDataSource = UserDataSource.getInstance();

        messageSourceData = MessageSourceData.getInstance();

        SUT = new MainActivityViewModel();

        userEntity = new UserEntity()
                .setUserFirstName(FIRST_NAME)
                .setUserLastName(LAST_NAME)
                .setAvatarIndex(AVATAR_INDEX);
    }

    @Test
    public void testUserOfflineProcess_getOfflineState_afterOnlineState() throws Exception {

        String userMeshId = UUID.randomUUID().toString();
        userEntity.setMeshId(userMeshId).setOnline(true);

        userDataSource.insertOrUpdateData(userEntity);

        SUT.userOfflineProcess();

        // Adding a time sleep for processing offline status
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean isOnline = userDataSource.getSingleUserById(userMeshId).isOnline();

        assertFalse(isOnline);
    }

    @Test
    public void testMakeSendingMessageAsFailed_getFailedState_whenMessageIsSending() throws Exception {

        String userMeshId = UUID.randomUUID().toString();
        userEntity.setMeshId(userMeshId).setOnline(true);

        userDataSource.insertOrUpdateData(userEntity);

        int messageStatus = Constants.MessageStatus.STATUS_SENDING;

        MessageEntity messageEntity = getMessageEntity(userMeshId, messageStatus);
        messageSourceData.insertOrUpdateData(messageEntity);

        SUT.makeSendingMessageAsFailed();

        // Adding a time sleep for processing offline status
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(messageSourceData.getMessageEntityById(messageEntity.getMessageId()).getStatus(),
                Constants.MessageStatus.STATUS_FAILED);
    }

    @Test
    public void testMakeSendingMessageAsFailed_getDeliverState_whenMessageIsDeliver() throws Exception {

        String userMeshId = UUID.randomUUID().toString();
        userEntity.setMeshId(userMeshId).setOnline(true);

        userDataSource.insertOrUpdateData(userEntity);

        int messageStatus = Constants.MessageStatus.STATUS_DELIVERED;

        MessageEntity messageEntity = getMessageEntity(userMeshId, messageStatus);
        messageSourceData.insertOrUpdateData(messageEntity);

        SUT.makeSendingMessageAsFailed();

        // Adding a time sleep for processing offline status
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
    public void tearDown() throws Exception {

    }
}