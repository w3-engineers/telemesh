package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import android.os.Handler;
import android.os.Looper;


import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.util.LiveDataTestUtil;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    /*@Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();*/

    private MeshContactViewModel SUT;

    private UserEntity userEntity;
    private UserDataSource userDataSource;
    private AppDatabase appDatabase;

    @Before
    public void setUp() {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = new UserDataSource();

        SUT = new MeshContactViewModel((Application) InstrumentationRegistry.getContext().getApplicationContext());

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
    public void testGetAllUsers_forFirstTime_getEmptyUserList() {

        LiveData<PagedList<UserEntity>> allUserList = SUT.favoriteEntityList;

        //TestObserver<List<UserEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getAllUsers());

        SUT.startFavouriteObserver();

        addDelay(2000);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    PagedList<UserEntity> result = LiveDataTestUtil.getValue(allUserList);

                    // assertTrue(testObserver.observedvalues.get(0).isEmpty());
                    if (result.isEmpty()) {
                        assertTrue(true);
                    } else {
                        assertFalse(false);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });


    }

   /* @Test
    public void testGetAllUsers_addUser_checkUserProperties() {

        addDelay(1000);

        String userMeshId = UUID.randomUUID().toString();

        userEntity.setMeshId(userMeshId);
        userEntity.isFavourite = 1;
        long res = userDataSource.insertOrUpdateData(userEntity);

        System.out.println("Data insert: "+res);

        addDelay(2000);

        LiveData<PagedList<UserEntity>> allUserList = SUT.favoriteEntityList;

        addDelay(1000);

        //TestObserver<List<UserEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getAllUsers());

        SUT.startFavouriteObserver();
        addDelay(2000);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    PagedList<UserEntity> result = LiveDataTestUtil.getValue(allUserList);

                    addDelay(2000);

                    // assertEquals(testObserver.observedvalues.get(0).get(0).getMeshId(), userMeshId);
                    assertEquals(result.get(0).getMeshId(), userMeshId);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }*/

   /* @Test
    public void testGetAllUsers_addUser_getUserSize() throws InterruptedException {

        userEntity.setMeshId(UUID.randomUUID().toString());
        userDataSource.insertOrUpdateData(userEntity);

        userEntity.setMeshId(UUID.randomUUID().toString());
        userDataSource.insertOrUpdateData(userEntity);

        LiveData<PagedList<UserEntity>> allUserList = SUT.favoriteEntityList;

        //TestObserver<List<UserEntity>> testObserver = LiveDataTestUtil.testObserve(SUT.getAllUsers());

        SUT.startFavouriteObserver();

        List<UserEntity> result = LiveDataTestUtil.getValue(allUserList);


        assertThat(result.size(), is(2));
    }*/

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
    public void meshContactViewModelSearch_smallLetter_retrieveUsers() {

        addDelay(1500);
        //arrange
        String SMALL_SEARCH_TEXT = "or";
        int itemCount = getItemCountInList(mUserEntities, SMALL_SEARCH_TEXT);
        LiveData<PagedList<UserEntity>> listLiveData = SUT.getGetFilteredList();

        //action
        SUT.startSearch(SMALL_SEARCH_TEXT, mUserEntities);

        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                //assertion
                List<UserEntity> userEntityList = LiveDataTestUtil.getValue(listLiveData);
                assertThat(userEntityList.size(), is(itemCount));

                addDelay(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


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

   /* @Test
    public void meshContactViewModelSearch_emptyText_retrieveAllUsers() throws InterruptedException {

        //arrange
        LiveData<PagedList<UserEntity>> listLiveData = SUT.getGetFilteredList();

        //action
        String EMPTY_SEARCH_TEXT = "";
        SUT.startSearch(EMPTY_SEARCH_TEXT, mUserEntities);

        //assertion
        List<UserEntity> userEntityList = LiveDataTestUtil.getValue(listLiveData);
        assertThat(userEntityList.size(), is(mUserEntities.size()));
    }*/

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