package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.util.LiveDataTestUtil;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;
import com.w3engineers.unicef.telemesh.util.TestObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
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
public class MeshContactViewModelTest {

    // Region constant
    private static String FIRST_NAME = "Danial";
    private static String LAST_NAME = "Alvez";
    private static int AVATAR_INDEX = 2;

    private final String EMPTY_SEARCH_TEXT = "", SMALL_SEARCH_TEXT = "or", CAPITAL_SEARCH_TEXT = "OR",
            SMALL_CAPITAL_SEARCH_TEXT = "Or";

    private CompositeDisposable mCompositeDisposable;
    private List<UserEntity> mUserEntities;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MeshContactViewModel SUT;

    private RandomEntityGenerator randomEntityGenerator;

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

        randomEntityGenerator = new RandomEntityGenerator();

        UserEntity userEntity1 = randomEntityGenerator.createUserEntityWithId();
        UserEntity userEntity2 = randomEntityGenerator.createUserEntityWithId();
        UserEntity userEntity3 = randomEntityGenerator.createUserEntityWithId();
        UserEntity userEntity4 = randomEntityGenerator.createUserEntityWithId();
        UserEntity userEntity5 = randomEntityGenerator.createUserEntityWithId();

        mUserEntities = new ArrayList<>(Arrays.asList(userEntity1, userEntity2, userEntity3,
                userEntity4, userEntity5));

        mCompositeDisposable = new CompositeDisposable();
    }

    @Test
    public void testGetAllUsers_forFirstTime_getEmptyUserList() {

        TestObserver<List<UserEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getAllUsers());

        assertTrue(testObserver.observedvalues.get(0).isEmpty());
    }

    @Test
    public void testGetAllUsers_addUser_checkUserProperties() throws Exception {

        String userMeshId = UUID.randomUUID().toString();

        userEntity.setMeshId(userMeshId);
        userDataSource.insertOrUpdateData(userEntity);

        TestObserver<List<UserEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getAllUsers());

        assertEquals(testObserver.observedvalues.get(0).get(0).getMeshId(), userMeshId);
    }

    @Test
    public void testGetAllUsers_addUser_getUserSize() throws Exception {

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

    @Test
    public void meshContactViewModelSearch_smallLetter_retrieveUsers() throws InterruptedException {

        //arrange
        int itemCount = getItemCountInList(mUserEntities, SMALL_SEARCH_TEXT);
        LiveData<List<UserEntity>> listLiveData = SUT.getGetFilteredList();

        //action
        SUT.startSearch(SMALL_SEARCH_TEXT, mUserEntities);

        //assertion
        List<UserEntity> userEntityList = LiveDataTestUtil.getValue(listLiveData);
        assertThat(userEntityList.size(), is(itemCount));
    }

    /*@Test
    public void meshContactViewModelSearch_capitalLetter_retrieveUsers() throws InterruptedException {

        //arrange
        int itemCount = getItemCountInList(mUserEntities, CAPITAL_SEARCH_TEXT);
        LiveData<List<UserEntity>> listLiveData = SUT.getGetFilteredList();

        //action
        SUT.startSearch(CAPITAL_SEARCH_TEXT, mUserEntities);

        //assertion
        List<UserEntity> userEntityList = LiveDataTestUtil.getValue(listLiveData);
        assertThat(userEntityList.size(), is(itemCount));
    }

    @Test
    public void meshContactViewModelSearch_smallCapitalLetter_retrieveUsers() throws InterruptedException {

        //arrange
        int itemCount = getItemCountInList(mUserEntities, SMALL_CAPITAL_SEARCH_TEXT);
        LiveData<List<UserEntity>> listLiveData = SUT.getGetFilteredList();

        //action
        SUT.startSearch(SMALL_CAPITAL_SEARCH_TEXT, mUserEntities);

        //assertion
        List<UserEntity> userEntityList = LiveDataTestUtil.getValue(listLiveData);
        assertThat(userEntityList.size(), is(itemCount));
    }*/

    @Test
    public void meshContactViewModelSearch_emptyText_retrieveAllUsers() throws InterruptedException {

        //arrange
        LiveData<List<UserEntity>> listLiveData = SUT.getGetFilteredList();

        //action
        SUT.startSearch(EMPTY_SEARCH_TEXT, mUserEntities);

        //assertion
        List<UserEntity> userEntityList = LiveDataTestUtil.getValue(listLiveData);
        assertThat(userEntityList.size(), is(mUserEntities.size()));
    }

    //This count can be implemented many ways. I prefer so for easy coding.
    //Our objective here is to count item rather the frequency.
    private int getItemCountInList(List<UserEntity> userEntities, String text) {

        final int[] smallLetterActualUserCount = {0};

        mCompositeDisposable.add(Observable.fromIterable(userEntities).filter(userEntity ->
                userEntity.getFullName().contains(text) ).subscribe(userEntity ->
                smallLetterActualUserCount[0]++));

        return smallLetterActualUserCount[0];
    }

    @After
    public void tearDown() throws Exception {
        appDatabase.close();
        mCompositeDisposable.clear();
    }
}