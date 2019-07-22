package com.w3engineers.unicef.telemesh.data.analytics;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountDataService;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsViewModel;
import com.w3engineers.unicef.util.helper.TimeUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.UUID;


import static org.junit.Assert.*;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class AnalyticsDataHelperTest {

    Context context;
    private AppDatabase appDatabase;
    private UserDataSource userDataSource;
    private MessageSourceData SUT;
    private AppShareCountDataService appShareCountDataService;
    String userId;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = UserDataSource.getInstance(appDatabase.userDao());

        SUT = MessageSourceData.getInstance(appDatabase.messageDao());
        appShareCountDataService = AppShareCountDataService.getInstance();

        userId = UUID.randomUUID().toString();
    }

    @After
    public void tearDown() {
        appDatabase.close();
    }

    @Test
    public void messageCountAnalyticsTest() {
        addDelay(500);

        userDataSource.insertOrUpdateData(getUserInfo(userId));

        addDelay(1000);

        for (int i = 0; i < 10; i++) {
            String messageId_1 = UUID.randomUUID().toString() + "" + i;
            SUT.insertOrUpdateData(getChatInfo(messageId_1, userId));
            // addDelay();
        }

        AnalyticsDataHelper.getInstance().analyticsDataObserver();

        addDelay(3 * 1000);

        MessageEntity.MessageAnalyticsEntity entity = new MessageEntity.MessageAnalyticsEntity();
        entity.setTime(System.currentTimeMillis());
        entity.setUserId(userId);
        entity.syncMessageCountToken = 1;

        AnalyticsDataHelper.getInstance().processMessageForAnalytics(false, entity);

        assertTrue(true);
    }


    @Test
    public void appShareCountTest() {
        addDelay(500);

        // insert test
        long insertId = appShareCountDataService.insertAppShareCount(getAppShareCountEntity());


        assertTrue(insertId > 0);

        // send test

        RmDataHelper.getInstance().sendAppShareCountAnalytics();
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private UserEntity getUserInfo(String meshId) {

        String firstName = "Daniel";

        return new UserEntity()
                .setMeshId(meshId)
                .setCustomId(meshId)
                .setUserName(firstName)
                .setAvatarIndex(3)
                .setLastOnlineTime(System.currentTimeMillis())
                .setOnline(true);
    }

    private ChatEntity getChatInfo(String messageId, String userId) {

        return new MessageEntity().setMessage("Hi").setFriendsId(userId)
                .setMessageId(messageId).setIncoming(true)
                .setMessageType(Constants.MessageType.TEXT_MESSAGE)
                .setTime(System.currentTimeMillis())
                .setStatus(Constants.MessageStatus.STATUS_SENDING);
    }

    private AppShareCountEntity getAppShareCountEntity() {
        AppShareCountEntity entity = new AppShareCountEntity();
        entity.setUserId(UUID.randomUUID().toString());
        entity.setDate(TimeUtil.getDateString(TimeUtil.stringToDate("15-07-2019").getTime()));
        entity.setCount(1);
        entity.setSend(false);
        return entity;
    }


}