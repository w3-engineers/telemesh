package com.w3engineers.unicef.telemesh.ui.meshdiscovered;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.pager.MainThreadExecutor;
import com.w3engineers.unicef.telemesh.ui.meshcontact.UserPositionalDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DiscoverViewModel extends BaseRxAndroidViewModel {

    private UserDataSource userDataSource;
    private MutableLiveData<UserEntity> openUserMessage = new MutableLiveData<>();
    private MutableLiveData<UserEntity> changeFavouriteStatus = new MutableLiveData<>();
    MutableLiveData<PagedList<UserEntity>> nearbyUsers = new MutableLiveData<>();
    MutableLiveData<List<UserEntity>> backUserEntity = new MutableLiveData<>();
    private MutableLiveData<PagedList<UserEntity>> filterUserList = new MutableLiveData<>();

    private static final int INITIAL_LOAD_KEY = 0;
    private static final int PAGE_SIZE = 50;
    private static final int PREFETCH_DISTANCE = 30;

    private String searchableText;

    public DiscoverViewModel(@NonNull Application application) {
        super(application);
        this.userDataSource = UserDataSource.getInstance();
    }

    public void openMessage(@NonNull UserEntity userEntity) {
        openUserMessage.postValue(userEntity);
    }

    public void changeFavouriteStatus(@NonNull UserEntity userEntity) {
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

    public void updateFavouriteStatus(String userId, int favouriteStatus) {
        AsyncTask.execute(() -> {
            int updateId = userDataSource
                    .updateFavouriteStatus(userId, favouriteStatus);
        });

        //  return updateId > 0;
    }

    public void startUserObserver() {
        getCompositeDisposable().add(userDataSource.getAllOnlineUsers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntities -> {


                    if (!TextUtils.isEmpty(searchableText)) {
                        backUserEntity.postValue(userEntities);
                        startSearch(searchableText, userEntities);
                    } else {

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

                        nearbyUsers.postValue(pagedStrings);
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                }));

    }


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
            Log.d("SearchIssue", "user list null");
        }
    }

}
