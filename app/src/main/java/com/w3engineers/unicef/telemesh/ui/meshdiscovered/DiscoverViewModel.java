package com.w3engineers.unicef.telemesh.ui.meshdiscovered;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DiscoverViewModel extends BaseRxViewModel {

    private UserDataSource userDataSource;
    private MutableLiveData<UserEntity> openUserMessage = new MutableLiveData<>();
    private MutableLiveData<UserEntity> changeFavouriteStatus = new MutableLiveData<>();
    MutableLiveData<List<UserEntity>> allUserEntity = new MutableLiveData<>();
    MutableLiveData<List<UserEntity>> backUserEntity = new MutableLiveData<>();
    private MutableLiveData<List<UserEntity>> getFilteredList = new MutableLiveData<>();

    private String searchableText;

    public DiscoverViewModel(@NonNull UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
    }

    public void openMessage(@NonNull UserEntity userEntity) {
        openUserMessage.postValue(userEntity);
    }

    public void changeFavouriteStatus(@NonNull UserEntity userEntity){
        changeFavouriteStatus.postValue(userEntity);
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

    MutableLiveData<UserEntity> changeFavourite() {
        return changeFavouriteStatus;
    }

    public boolean updateFavouriteStatus(String meshId, int favouriteStatus){
      return RmDataHelper.getInstance().updateFavouriteStatus(meshId, favouriteStatus);
    }

    public void startUserObserver() {
        getCompositeDisposable().add(userDataSource.getAllUsers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntities -> {

                    if (!TextUtils.isEmpty(searchableText)) {
                        backUserEntity.postValue(userEntities);
                        startSearch(searchableText, userEntities);
                    } else {
                        allUserEntity.postValue(userEntities);
                    }

                }, Throwable::printStackTrace));
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
