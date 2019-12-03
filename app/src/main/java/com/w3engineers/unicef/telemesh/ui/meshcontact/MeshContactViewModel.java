package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.mesh.util.MeshLog;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.pager.MainThreadExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class MeshContactViewModel extends BaseRxAndroidViewModel {

    private UserDataSource userDataSource;
    private CompositeDisposable mCompositeDisposable;
    private MutableLiveData<UserEntity> openUserMessage = new MutableLiveData<>();
    LiveData<PagedList<UserEntity>> allMessagedWithEntity = new MutableLiveData<>();
    LiveData<PagedList<UserEntity>> favoriteEntityList = new MutableLiveData<>();
    MutableLiveData<List<UserEntity>> backUserEntity = new MutableLiveData<>();
    private  MutableLiveData<PagedList<UserEntity>> filterUserList = new MutableLiveData<>();

    private static final int INITIAL_LOAD_KEY = 0;
    private static final int PAGE_SIZE = 50;
    private static final int PREFETCH_DISTANCE = 30;

    private String searchableText;

    public MeshContactViewModel(@NonNull Application application) {
        super(application);
        // mCompositeDisposable = new CompositeDisposable();
        userDataSource = UserDataSource.getInstance();
    }


    public void openMessage(@NonNull UserEntity userEntity) {
        openUserMessage.postValue(userEntity);
    }

    public void setSearchText(String searchText) {
        this.searchableText = searchText;
    }

    public int getUserAvatarByIndex(int imageIndex) {
        return TeleMeshDataHelper.getInstance().getAvatarImage(imageIndex);
    }

    MutableLiveData<UserEntity> openUserMessage() {
        return openUserMessage;
    }

    public void startAllMessagedWithFavouriteObserver() {
/*        getCompositeDisposable().add(userDataSource.getAllMessagedWithFavouriteUsers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntities -> {

                    MeshLog.v("all message data");

                    if (!TextUtils.isEmpty(searchableText)) {
                        backUserEntity.postValue(userEntities);
                        startSearch(searchableText, userEntities);
                    } else {
                        allMessagedWithEntity.postValue(userEntities);
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                }));*/

        allMessagedWithEntity = userDataSource.getAllMessagedWithFavouriteUsers();
    }

    public void startFavouriteObserver() {
/*        getCompositeDisposable().add(userDataSource.getFavouriteUsers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntities -> {

                    if (!TextUtils.isEmpty(searchableText)) {
                        backUserEntity.postValue(userEntities);
                        startSearch(searchableText, userEntities);
                    } else {
                        favouriteEntity.postValue(userEntities);
                    }

                }, Throwable::printStackTrace));*/

        favoriteEntityList = userDataSource.getFavouriteUsers();

    }

    public void stopAllMessageWithObserver() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    public void stopFavouriteObserver() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    /*LiveData<List<UserEntity>> getAllUsers() {
//        Timber.e("Get all user called");
        return LiveDataReactiveStreams.fromPublisher(userDataSource.getAllUsers());
    }*/

    @NonNull
    public LiveData<PagedList<UserEntity>> getGetFilteredList() {
        return filterUserList;
    }


    public void startSearch(@NonNull String searchText, @Nullable List<UserEntity> userEntities) {

        searchableText = searchText;

        if (userEntities != null) {
            List<UserEntity> filteredItemList = new ArrayList<>();

            for (UserEntity user : userEntities) {

                if (user.getFullName().toLowerCase(Locale.getDefault()).contains(searchText))
                    filteredItemList.add(user);
            }
            Log.d("SearchIssue", "user list post call");

            UserSearchDataSource userSearchDataSource = new UserSearchDataSource(filteredItemList);

            PagedList.Config myConfig = new PagedList.Config.Builder()
                    .setEnablePlaceholders(true)
                    .setPrefetchDistance(PREFETCH_DISTANCE)
                    .setPageSize(PAGE_SIZE)
                    .build();


            PagedList<UserEntity> pagedStrings = new PagedList.Builder<>(userSearchDataSource, myConfig)
                    .setInitialKey(INITIAL_LOAD_KEY)
                    .setNotifyExecutor(new MainThreadExecutor()) //The executor defining where page loading updates are dispatched.
                    .setFetchExecutor(Executors.newSingleThreadExecutor())
                    .build();


            filterUserList.postValue(pagedStrings);

        } else {
            Log.d("SearchIssue", "user list null");
        }
    }

}
