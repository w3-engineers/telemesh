package com.w3engineers.unicef.telemesh.local.usertable;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [04-Feb-2019 at 4:24 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [04-Feb-2019 at 4:24 PM].
 * --> <Second Editor> on [04-Feb-2019 at 4:24 PM].
 * Reviewed by :
 * --> <First Reviewer> on [04-Feb-2019 at 4:24 PM].
 * --> <Second Reviewer> on [04-Feb-2019 at 4:24 PM].
 * ============================================================================
 **/
@RunWith(AndroidJUnit4.class)
public class UserDataSourceTest {

    private AppDatabase appDatabase;
    UserDataSource SUT;

    @Before
    public void setUp() throws Exception {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        SUT = new UserDataSource(appDatabase.userDao());
    }

    @After
    public void tearDown() throws Exception {
        appDatabase.close();
    }

    @Test
    public void basicUserDataTest() {

        String meshId1 = getRandomString();
        String customId1 = getRandomString();
        long lastOnlineTime1 = System.currentTimeMillis();

        UserEntity user1 = getUserInfo(customId1, lastOnlineTime1, meshId1);
        // Test on user operation
        SUT.insertOrUpdateData(user1);

        String meshId2 = getRandomString();
        String customId2 = getRandomString();
        long lastOnlineTime2 = System.currentTimeMillis();

        UserEntity user2 = getUserInfo(customId2, lastOnlineTime2, meshId2);
        SUT.insertOrUpdateData(user2);

        TestSubscriber<List<UserEntity>> getAllUserEntitySubscriber = SUT.getAllUsers().test();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test and check number of users size
        getAllUserEntitySubscriber.assertNoErrors().assertValue(userEntities -> userEntities.size() == 2);

        // Test on user entities properties
        getAllUserEntitySubscriber.assertNoErrors().assertValue(userEntities -> {
            UserEntity userEntity = userEntities.get(0);
            return userEntity.getCustomId().equals(customId1);
        });

        TestSubscriber<UserEntity> getLastInsertedUser = SUT.getLastData().test();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test on last user entities properties
        getLastInsertedUser.assertNoErrors().assertValue(userEntity ->
                userEntity.getCustomId().equals(customId2));

        UserEntity userEntity = SUT.getSingleUserById(meshId1);
        assertEquals(userEntity.getLastOnlineTime(), lastOnlineTime1);

        TestSubscriber<UserEntity> getUserById = SUT.getUserById(meshId2).test();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test on last user entities properties
        getUserById.assertNoErrors().assertValue(user ->
                user.getLastOnlineTime() == lastOnlineTime2);

        SUT.updateUserToOffline();

        userEntity = SUT.getSingleUserById(meshId2);
        assertFalse(userEntity.isOnline());

        SUT.deleteUser(meshId1);
        userEntity = SUT.getSingleUserById(meshId1);

        assertNull(userEntity);
    }

    private UserEntity getUserInfo(String customId, long lastOnlineTime, String meshId) {

        String firstName = "Daniel";
        String lastName = "Alvez";

        return new UserEntity()
                .setMeshId(meshId)
                .setCustomId(customId)
                .setUserFirstName(firstName)
                .setUserLastName(lastName)
                .setAvatarIndex(3)
                .setLastOnlineTime(lastOnlineTime)
                .setOnline(true);
    }

    private String getRandomString() {
        return UUID.randomUUID().toString();
    }
}