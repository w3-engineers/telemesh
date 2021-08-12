package com.w3engineers.unicef.telemesh.ui.meshdiscovered;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;
import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import android.os.Handler;
import android.os.Looper;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.telemesh.util.LiveDataTestUtil;
import com.w3engineers.unicef.util.helper.StatusHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

import io.reactivex.disposables.CompositeDisposable;

import static org.junit.Assert.*;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class DiscoverViewModelTest {
    private CompositeDisposable mCompositeDisposable;
    private List<UserEntity> mUserEntities;

 /*   @Rule
    public TestRule instantTaskExecutorRule = new InstantTaskExecutorRule();*/

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    private DiscoverViewModel SUT;

    private UserEntity userEntity;
    private UserDataSource userDataSource;
    private AppDatabase appDatabase;


    @Before
    public void setUp() {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = new UserDataSource(appDatabase.userDao());

        SUT = new DiscoverViewModel((Application) InstrumentationRegistry.getContext().getApplicationContext());

        // Region constant
        String FIRST_NAME = "Danial";
        String LAST_NAME = "Alvez";
        int AVATAR_INDEX = 2;
        userEntity = new UserEntity()
                .setUserName(FIRST_NAME)
                .setAvatarIndex(AVATAR_INDEX);

        mCompositeDisposable = new CompositeDisposable();
    }

    @After
    public void tearDown() {
        appDatabase.close();
    }

    @Test
    public void testGetAllUsers_forFirstTime_getEmptyUserList() throws InterruptedException {

        MutableLiveData<PagedList<UserEntity>> nearbyUsers = SUT.nearbyUsers;

        String userMeshId = UUID.randomUUID().toString();

        userEntity.setMeshId(userMeshId);
        userEntity.isFavourite = 1;
        userEntity.isOnline = 1;

        userDataSource.insertOrUpdateData(userEntity);


        addDelay(1000);


        UserEntity userEntityTwo = new UserEntity()
                .setAvatarIndex(1)
                .setOnlineStatus(Constants.UserStatus.INTERNET_ONLINE)
                .setMeshId("0xaa2dd785fc60eeb8151f65b3ded59ce6c2f12cd4")
                .setUserName("Mike")
                .setIsFavourite(Constants.FavouriteStatus.FAVOURITE)
                .setRegistrationTime(System.currentTimeMillis() + 1);
        userEntityTwo.setId(1);

        String SearchText = "Mike";

        SUT.userList.add(userEntityTwo);
        SUT.searchableText = SearchText;

        SUT.startUserObserver();

        addDelay(2000);


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    PagedList<UserEntity> result = LiveDataTestUtil.getValue(nearbyUsers);

                    addDelay(2000);

                    if (result == null) {
                        assertNull(result);
                    } else {
                        if (result.size() > 0) {
                            assertFalse(result.isEmpty());
                        } else {
                            assertTrue(result.isEmpty());
                        }

                        StatusHelper.out("Test executed");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}