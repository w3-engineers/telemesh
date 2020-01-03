package com.w3engineers.unicef.telemesh.ui.meshdiscovered;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.ui.meshcontact.MeshContactViewModel;
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

import io.reactivex.disposables.CompositeDisposable;

import static org.junit.Assert.*;


public class DiscoverViewModelTest {
    private CompositeDisposable mCompositeDisposable;
    private List<UserEntity> mUserEntities;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

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

        String userMeshId = UUID.randomUUID().toString();

        userEntity.setMeshId(userMeshId);
        userEntity.isFavourite = 1;
        userEntity.isOnline = 1;

        AsyncTask.execute(() -> userDataSource.insertOrUpdateData(userEntity));


        addDelay(1000);

        SUT.startUserObserver();

        addDelay(2000);


        List<UserEntity> result = LiveDataTestUtil.getValue(SUT.nearbyUsers);

        addDelay(2000);

        assertTrue(result.isEmpty());


    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}