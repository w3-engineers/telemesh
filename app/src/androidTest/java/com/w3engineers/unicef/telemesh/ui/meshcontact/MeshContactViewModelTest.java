package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
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

//    private final String CAPITAL_SEARCH_TEXT = "OR";
//    private final String SMALL_CAPITAL_SEARCH_TEXT = "Or";

    private CompositeDisposable mCompositeDisposable;
    private List<UserEntity> mUserEntities;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MeshContactViewModel SUT;

    private UserEntity userEntity;
    private UserDataSource userDataSource;
    private AppDatabase appDatabase;

    @Before
    public void setUp() {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = new UserDataSource(appDatabase.userDao());

        SUT = new MeshContactViewModel(userDataSource);

        // Region constant
        String FIRST_NAME = "Danial";
        String LAST_NAME = "Alvez";
        int AVATAR_INDEX = 2;
        userEntity = new UserEntity()
                .setUserName(FIRST_NAME)
                .setAvatarIndex(AVATAR_INDEX);

        RandomEntityGenerator randomEntityGenerator = new RandomEntityGenerator();

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
    public void testGetAllUsers_forFirstTime_getEmptyUserList() throws InterruptedException {

        MutableLiveData<List<UserEntity>> allUserList = SUT.favouriteEntity;

        //TestObserver<List<UserEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getAllUsers());

        SUT.startFavouriteObserver();

        List<UserEntity> result = LiveDataTestUtil.getValue(allUserList);

        // assertTrue(testObserver.observedvalues.get(0).isEmpty());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllUsers_addUser_checkUserProperties() throws InterruptedException {

        String userMeshId = UUID.randomUUID().toString();

        userEntity.setMeshId(userMeshId);
        userDataSource.insertOrUpdateData(userEntity);

        MutableLiveData<List<UserEntity>> allUserList = SUT.favouriteEntity;

        //TestObserver<List<UserEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getAllUsers());

        SUT.startFavouriteObserver();

        List<UserEntity> result = LiveDataTestUtil.getValue(allUserList);

        // assertEquals(testObserver.observedvalues.get(0).get(0).getMeshId(), userMeshId);
        assertEquals(result.get(0).getMeshId(), userMeshId);
    }

    @Test
    public void testGetAllUsers_addUser_getUserSize() throws InterruptedException {

        userEntity.setMeshId(UUID.randomUUID().toString());
        userDataSource.insertOrUpdateData(userEntity);

        userEntity.setMeshId(UUID.randomUUID().toString());
        userDataSource.insertOrUpdateData(userEntity);

        MutableLiveData<List<UserEntity>> allUserList = SUT.favouriteEntity;

        //TestObserver<List<UserEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getAllUsers());

        SUT.startFavouriteObserver();

        List<UserEntity> result = LiveDataTestUtil.getValue(allUserList);

        assertThat(result.size(), is(2));
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

        addDelay(1500);
        //arrange
        String SMALL_SEARCH_TEXT = "or";
        int itemCount = getItemCountInList(mUserEntities, SMALL_SEARCH_TEXT);
        LiveData<List<UserEntity>> listLiveData = SUT.getGetFilteredList();

        //action
        SUT.startSearch(SMALL_SEARCH_TEXT, mUserEntities);

        //assertion
        List<UserEntity> userEntityList = LiveDataTestUtil.getValue(listLiveData);
        assertThat(userEntityList.size(), is(itemCount));

        addDelay(5000);
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
        String EMPTY_SEARCH_TEXT = "";
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
                userEntity.getFullName().contains(text)).subscribe(userEntity ->
                smallLetterActualUserCount[0]++));

        return smallLetterActualUserCount[0];
    }

    @After
    public void tearDown() {
        appDatabase.close();
        mCompositeDisposable.clear();
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}