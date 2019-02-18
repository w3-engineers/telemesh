package com.w3engineers.unicef.telemesh.data.local.messagetable;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

import io.reactivex.subscribers.TestSubscriber;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [07-Feb-2019 at 12:04 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [07-Feb-2019 at 12:04 PM].
 * --> <Second Editor> on [07-Feb-2019 at 12:04 PM].
 * Reviewed by :
 * --> <First Reviewer> on [07-Feb-2019 at 12:04 PM].
 * --> <Second Reviewer> on [07-Feb-2019 at 12:04 PM].
 * ============================================================================
 **/
@RunWith(AndroidJUnit4.class)
public class MessageSourceDataTest {

    private AppDatabase appDatabase;
    private UserDataSource userDataSource;
    MessageSourceData SUT;

    @Before
    public void setUp() throws Exception {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = new UserDataSource(appDatabase.userDao());

        SUT = new MessageSourceData(appDatabase.messageDao());
    }

    @After
    public void tearDown() throws Exception {
        appDatabase.close();
    }

    @Test
    public void basicMessageTest() throws Exception{

        String userId = UUID.randomUUID().toString();

        userDataSource.insertOrUpdateData(getUserInfo(userId));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String messageId_1 = UUID.randomUUID().toString();
        SUT.insertOrUpdateData(getChatInfo(messageId_1, userId));

        String messageId_2 = UUID.randomUUID().toString();
        SUT.insertOrUpdateData(getChatInfo(messageId_2, userId));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SUT.changeMessageStatusFrom(Constants.MessageStatus.STATUS_SENDING, Constants.MessageStatus.STATUS_UNREAD);

        TestSubscriber<List<ChatEntity>> getAllMessageSubscriber = SUT.getAllMessages(userId).test();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getAllMessageSubscriber.assertNoErrors().assertValue(chatEntities -> {
            ChatEntity chatEntity = chatEntities.get(0);
            return chatEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD;
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SUT.updateUnreadToRead(userId);

        ChatEntity chatEntity = SUT.getMessageEntityById(messageId_1);

        assertThat(chatEntity.getStatus(), is(Constants.MessageStatus.STATUS_READ));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TestSubscriber<ChatEntity> getLastChatEntity = SUT.getLastData().test();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getLastChatEntity.assertNoErrors().assertValue(lastChatEntity -> {
            return lastChatEntity.getMessageId().equals(messageId_2);
        });
    }

    private UserEntity getUserInfo(String meshId) {

        String firstName = "Daniel";
        String lastName = "Alvez";

        return new UserEntity()
                .setMeshId(meshId)
                .setCustomId(UUID.randomUUID().toString())
                .setUserFirstName(firstName)
                .setUserLastName(lastName)
                .setAvatarIndex(3)
                .setLastOnlineTime(System.currentTimeMillis())
                .setOnline(true);
    }

    private ChatEntity getChatInfo(String messageId, String userId) {

        return new MessageEntity().setMessage("Hi").setFriendsId(userId)
                .setMessageId(messageId).setIncoming(false)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_SENDING);
    }
}