package com.w3engineers.unicef.telemesh.ui.meshcontact;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MutableLiveData;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [04-Oct-2018 at 6:52 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [04-Oct-2018 at 6:52 PM].
 * * --> <Second Editor> on [04-Oct-2018 at 6:52 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [04-Oct-2018 at 6:52 PM].
 * * --> <Second Reviewer> on [04-Oct-2018 at 6:52 PM].
 * * ============================================================================
 **/
public class MeshContactViewModel extends BaseRxViewModel {

    private UserDataSource userDataSource;
    private MutableLiveData<UserEntity> openUserMessage = new MutableLiveData<>();


    private MutableLiveData<List<UserEntity>> getFilteredList = new MutableLiveData<>();

    public MeshContactViewModel(UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    public void openMessage(UserEntity userEntity) {
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

    public MutableLiveData<List<UserEntity>> getGetFilteredList() {
        return getFilteredList;
    }


    public void startSearch(String searchText, List<UserEntity> userEntities) {
        List<UserEntity> filteredItemList = new ArrayList<>();

        for (UserEntity user : userEntities) {

            if (user.getFullName().toLowerCase().contains(searchText))
                filteredItemList.add(user);
        }

        getFilteredList.postValue(filteredItemList);
    }

}
