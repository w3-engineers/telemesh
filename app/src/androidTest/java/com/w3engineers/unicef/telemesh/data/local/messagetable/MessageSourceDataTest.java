package com.w3engineers.unicef.telemesh.data.local.messagetable;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.util.helper.StatusHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

import io.reactivex.subscribers.TestSubscriber;

import static org.hamcrest.CoreMatchers.is;
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
public class MessageSourceDataTest {

    private AppDatabase appDatabase;
    private UserDataSource userDataSource;
    private MessageSourceData SUT;

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    @Before
    public void setUp() {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = new UserDataSource(appDatabase.userDao());

        SUT = new MessageSourceData(appDatabase.messageDao());
    }

    @After
    public void tearDown() {
        appDatabase.close();
    }

    @Test
    public void basicMessageTest() {

        addDelay();

        String userId = UUID.randomUUID().toString();

        userDataSource.insertOrUpdateData(getUserInfo(userId));

        addDelay();

        String messageId_1 = UUID.randomUUID().toString();
        SUT.insertOrUpdateData(getChatInfo(messageId_1, userId));

        String messageId_2 = UUID.randomUUID().toString();
        SUT.insertOrUpdateData(getChatInfo(messageId_2, userId));

        addDelay();

        SUT.changeMessageStatusFrom(Constants.MessageStatus.STATUS_SENDING, Constants.MessageStatus.STATUS_UNREAD);

        TestSubscriber<List<ChatEntity>> getAllMessageSubscriber = SUT.getAllMessages(userId).test();

        addDelay();

        getAllMessageSubscriber.assertNoErrors().assertValue(chatEntities -> {
            ChatEntity chatEntity = chatEntities.get(0);
            return chatEntity.getStatus() == Constants.MessageStatus.STATUS_UNREAD;
        });

        addDelay();

        SUT.updateUnreadToRead(userId);

        ChatEntity chatEntity = SUT.getMessageEntityById(messageId_1);

        assertThat(chatEntity.getStatus(), is(Constants.MessageStatus.STATUS_READ));

        addDelay();

        TestSubscriber<ChatEntity> getLastChatEntity = SUT.getLastData().test();

        addDelay();

        getLastChatEntity.assertNoErrors().assertValue(lastChatEntity -> lastChatEntity.getMessageId().equals(messageId_2));

        assertTrue(true);

        StatusHelper.out("Test executed");
    }

    private void addDelay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private UserEntity getUserInfo(String meshId) {

        String firstName = "Daniel";
        String lastName = "Alvez";

        return new UserEntity()
                .setMeshId(meshId)
                .setCustomId(UUID.randomUUID().toString())
                .setUserName(firstName)
                .setUserLastName(lastName)
                .setAvatarIndex(3)
                .setLastOnlineTime(System.currentTimeMillis())
                .setOnlineStatus(Constants.UserStatus.INTERNET_ONLINE);
    }

    private ChatEntity getChatInfo(String messageId, String userId) {

        return new MessageEntity().setMessage("Hi").setFriendsId(userId)
                .setMessageId(messageId).setIncoming(false)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_SENDING);
    }
}