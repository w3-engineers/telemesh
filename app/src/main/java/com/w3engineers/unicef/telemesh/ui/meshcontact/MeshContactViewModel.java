package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.pager.MainThreadExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
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
    private MutableLiveData<UserEntity> openUserMessage = new MutableLiveData<>();
    private MutableLiveData<UserEntity> changeFavouriteStatus = new MutableLiveData<>();
    MutableLiveData<PagedList<UserEntity>> allMessagedWithEntity = new MutableLiveData<>();
    MutableLiveData<PagedList<UserEntity>> favoriteEntityList = new MutableLiveData<>();
    MutableLiveData<List<UserEntity>> backUserEntity = new MutableLiveData<>();
    private MutableLiveData<PagedList<UserEntity>> filterUserList = new MutableLiveData<>();

    private static final int INITIAL_LOAD_KEY = 0;
    private static final int PAGE_SIZE = 50;
    private static final int PREFETCH_DISTANCE = 30;

    private List<UserEntity> favouriteList;
    private List<UserEntity> msgWithFavList;

    public int ALL_SEARCH = 1;

    private String searchableText;

    private int searchType;


    public MeshContactViewModel(@NonNull Application application) {
        super(application);
        userDataSource = UserDataSource.getInstance();
    }


    public void openMessage(@NonNull UserEntity userEntity) {
        openUserMessage.postValue(userEntity);
    }

    /*public void setSearchText(String searchText) {
        this.searchableText = searchText;
    }*/

    public int getUserAvatarByIndex(int imageIndex) {
        return TeleMeshDataHelper.getInstance().getAvatarImage(imageIndex);
    }

    public void changeFavouriteStatus(@NonNull UserEntity userEntity) {
        changeFavouriteStatus.postValue(userEntity);
    }

    MutableLiveData<UserEntity> changeFavourite() {
        return changeFavouriteStatus;
    }

    MutableLiveData<UserEntity> openUserMessage() {
        return openUserMessage;
    }

    public void updateFavouriteStatus(String userId, int favouriteStatus) {
        AsyncTask.execute(() -> {
            int updateId = userDataSource
                    .updateFavouriteStatus(userId, favouriteStatus);
        });

        //  return updateId > 0;
    }

    public void startAllMessagedWithFavouriteObserver() {
        getCompositeDisposable().add(userDataSource.getAllMessagedWithFavouriteUsers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntities -> {

                    msgWithFavList = userEntities;

                    if (!TextUtils.isEmpty(searchableText)) {
                        backUserEntity.postValue(userEntities);
                        if (searchType == Constants.SpinnerItem.ALL){
                            startSearch(searchableText, userEntities);
                        }
                    } else {

                        setUserMessageWithFavouriteData(userEntities);
                    }

                }, Throwable::printStackTrace));

        //  allMessagedWithEntity = userDataSource.getAllMessagedWithFavouriteUsers();
    }

    public void setUserMessageWithFavouriteData(List<UserEntity> userEntities) {
        UserPositionalDataSource userSearchDataSource = new UserPositionalDataSource(userEntities);

        PagedList.Config myConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .setPageSize(PAGE_SIZE)
                .build();


        PagedList<UserEntity> pagedStrings = new PagedList.Builder<>(userSearchDataSource, myConfig)
                .setInitialKey(INITIAL_LOAD_KEY)
                .setNotifyExecutor(new MainThreadExecutor()) //The executor defining where page loading updates are dispatched.asset
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();

        allMessagedWithEntity.postValue(pagedStrings);
    }

    public void startFavouriteObserver() {

        getCompositeDisposable().add(userDataSource.getFavouriteUsers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntities -> {

                    favouriteList = userEntities;

                    if (!TextUtils.isEmpty(searchableText)) {
                        backUserEntity.postValue(userEntities);
                        if (searchType == Constants.SpinnerItem.FAVOURITE){
                            startSearch(searchableText, userEntities);
                        }
                    } else {
                        setUserFavouriteData(userEntities);
                    }

                }, Throwable::printStackTrace));

        //  favoriteEntityList = userDataSource.getFavouriteUsers();
    }



    public void setUserFavouriteData(List<UserEntity> userEntities) {
        UserPositionalDataSource userSearchDataSource = new UserPositionalDataSource(userEntities);

        PagedList.Config myConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .setPageSize(PAGE_SIZE)
                .build();


        PagedList<UserEntity> pagedStrings = new PagedList.Builder<>(userSearchDataSource, myConfig)
                .setInitialKey(INITIAL_LOAD_KEY)
                .setNotifyExecutor(new MainThreadExecutor()) //The executor defining where page loading updates are dispatched.asset
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();

        favoriteEntityList.postValue(pagedStrings);
    }

    public List<UserEntity> getCurrentUserList(int srchType) {
        searchType = srchType;
        List<UserEntity> userList;
        if (searchType == Constants.SpinnerItem.FAVOURITE) {
            if (favouriteList == null) {
                favouriteList = new ArrayList<>();
            }
            userList = favouriteList;
        } else if (searchType == Constants.SpinnerItem.ALL){
            if (msgWithFavList == null) {
                msgWithFavList = new ArrayList<>();
            }
            userList = msgWithFavList;
        }else {
            userList = new ArrayList<>();
        }

        return userList;
    }


    @NonNull
    public LiveData<PagedList<UserEntity>> getGetFilteredList() {
        return filterUserList;
    }


    public void startSearch(@NonNull String searchText, @Nullable List<UserEntity> userEntities) {

        searchableText = searchText;

        if (userEntities != null) {

            if (TextUtils.isEmpty(searchText)) {
                setUserFavouriteData(favouriteList);
                setUserMessageWithFavouriteData(msgWithFavList);
                return;
            }

            List<UserEntity> filteredItemList = new ArrayList<>();

            for (UserEntity user : userEntities) {

                if (user.getFullName().toLowerCase(Locale.getDefault()).contains(searchText))
                    filteredItemList.add(user);
            }

            UserPositionalDataSource userSearchDataSource = new UserPositionalDataSource(filteredItemList);

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
//            Timber.tag("SearchIssue").d("user list null");
        }
    }

}
