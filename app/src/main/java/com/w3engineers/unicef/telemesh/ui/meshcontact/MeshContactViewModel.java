package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class MeshContactViewModel extends BaseRxViewModel {

    private UserDataSource userDataSource;
    private MutableLiveData<UserEntity> openUserMessage = new MutableLiveData<>();


    private MutableLiveData<List<UserEntity>> getFilteredList = new MutableLiveData<>();

    public MeshContactViewModel(@NonNull UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    public void openMessage(@NonNull UserEntity userEntity) {
        openUserMessage.postValue(userEntity);

    }

    public int getUserAvatarByIndex(int imageIndex) {
        return TeleMeshDataHelper.getInstance().getAvatarImage(imageIndex);
    }

    MutableLiveData<UserEntity> openUserMessage() {
        return openUserMessage;
    }

    LiveData<List<UserEntity>> getAllUsers() {
        Timber.e("Get all user called");
        return LiveDataReactiveStreams.fromPublisher(userDataSource.getAllUsers());
    }

    @NonNull
    public MutableLiveData<List<UserEntity>> getGetFilteredList() {
        return getFilteredList;
    }


    public void startSearch(@NonNull String searchText, @Nullable List<UserEntity> userEntities) {
        if (userEntities != null) {
            List<UserEntity> filteredItemList = new ArrayList<>();

            for (UserEntity user : userEntities) {

                if (user.getFullName().toLowerCase(Locale.getDefault()).contains(searchText))
                    filteredItemList.add(user);
            }

            getFilteredList.postValue(filteredItemList);
        }
    }

}
