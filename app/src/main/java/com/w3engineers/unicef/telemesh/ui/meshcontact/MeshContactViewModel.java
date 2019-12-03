package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


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
    MutableLiveData<List<UserEntity>> allMessagedWithEntity = new MutableLiveData<>();
    MutableLiveData<List<UserEntity>> favouriteEntity = new MutableLiveData<>();
    MutableLiveData<List<UserEntity>> backUserEntity = new MutableLiveData<>();
    private MutableLiveData<List<UserEntity>> getFilteredList = new MutableLiveData<>();

    private String searchableText;



    public MeshContactViewModel(@NonNull Application application) {
        super(application);
        this.userDataSource = UserDataSource.getInstance();
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

    public void startAllMessagedWithObserver() {
        mCompositeDisposable = getCompositeDisposable();
        mCompositeDisposable.add(userDataSource.getAllMessagedWithUsers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntities -> {

                    if (!TextUtils.isEmpty(searchableText)) {
                        backUserEntity.postValue(userEntities);
                        startSearch(searchableText, userEntities);
                    } else {
                        allMessagedWithEntity.postValue(userEntities);
                    }

                }, Throwable::printStackTrace));
    }

    public void startFavouriteObserver() {
        mCompositeDisposable = getCompositeDisposable();
        getCompositeDisposable().add(userDataSource.getFavouriteUsers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntities -> {

                    if (!TextUtils.isEmpty(searchableText)) {
                        backUserEntity.postValue(userEntities);
                        startSearch(searchableText, userEntities);
                    } else {
                        favouriteEntity.postValue(userEntities);
                    }

                }, Throwable::printStackTrace));
    }

    public void stopAllMessageWithObserver() {
        if(mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    public void stopFavouriteObserver() {
        if(mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    /*LiveData<List<UserEntity>> getAllUsers() {
//        Timber.e("Get all user called");
        return LiveDataReactiveStreams.fromPublisher(userDataSource.getAllUsers());
    }*/

    @NonNull
    public MutableLiveData<List<UserEntity>> getGetFilteredList() {
        return getFilteredList;
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
            getFilteredList.postValue(filteredItemList);
        } else {
            Log.d("SearchIssue", "user list null");
        }
    }

}
