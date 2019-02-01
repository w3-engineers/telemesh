package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.util.LiveDataTestUtil;
import com.w3engineers.unicef.telemesh.util.TestObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [29-Jan-2019 at 5:56 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [29-Jan-2019 at 5:56 PM].
 * --> <Second Editor> on [29-Jan-2019 at 5:56 PM].
 * Reviewed by :
 * --> <First Reviewer> on [29-Jan-2019 at 5:56 PM].
 * --> <Second Reviewer> on [29-Jan-2019 at 5:56 PM].
 * ============================================================================
 **/
@RunWith(AndroidJUnit4.class)
public class MeshContactViewModelTest {

    // Region constant
    private static String FIRST_NAME = "Danial";
    private static String LAST_NAME = "Alvez";
    private static int AVATAR_INDEX = 2;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MeshContactViewModel SUT;

    private UserEntity userEntity;
    private UserDataSource userDataSource;
    private AppDatabase appDatabase;

    @Before
    public void setUp() throws Exception {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = new UserDataSource(appDatabase.userDao());

        SUT = new MeshContactViewModel(userDataSource);

        userEntity = new UserEntity()
                .setUserFirstName(FIRST_NAME)
                .setUserLastName(LAST_NAME)
                .setAvatarIndex(AVATAR_INDEX);
    }

    @Test
    public void testGetAllUsers_forFirstTime_getEmptyUserList() {

        TestObserver<List<UserEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getAllUsers());

        assertTrue(testObserver.observedvalues.get(0).isEmpty());
    }

    @Test
    public void testGetAllUsers_addUser_checkUserProperties() {

        String userMeshId = UUID.randomUUID().toString();

        userEntity.setMeshId(userMeshId);
        userDataSource.insertOrUpdateData(userEntity);

        TestObserver<List<UserEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getAllUsers());

        assertEquals(testObserver.observedvalues.get(0).get(0).getMeshId(), userMeshId);
    }

    @Test
    public void testGetAllUsers_addUser_getUserSize() {

        userEntity.setMeshId(UUID.randomUUID().toString());
        userDataSource.insertOrUpdateData(userEntity);

        userEntity.setMeshId(UUID.randomUUID().toString());
        userDataSource.insertOrUpdateData(userEntity);

        TestObserver<List<UserEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getAllUsers());

        assertThat(testObserver.observedvalues.get(0).size(), is(2));
    }

    @Test
    public void testGetUserAvatarByIndex_useValidImageIndex_getImageId() {
        int imageIndex = 11;
        int imageId = TeleMeshDataHelper.getInstance().getAvatarImage(imageIndex);

        assertEquals(imageId, SUT.getUserAvatarByIndex(imageIndex));
    }

    @Test
    public void testGetUserAvatarByIndex_useNonValidImageIndex_getWrongImageId() {
        int imageIndex = -1;
        int imageId = TeleMeshDataHelper.getInstance().getAvatarImage(imageIndex);

        assertEquals(imageId, SUT.getUserAvatarByIndex(imageIndex));
    }

    @After
    public void tearDown() throws Exception {
        appDatabase.close();
    }
}