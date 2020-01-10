package com.w3engineers.unicef.telemesh.ui.meshdiscovered;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.pager.MainThreadExecutor;
import com.w3engineers.unicef.telemesh.ui.meshcontact.UserPositionalDataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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

    public String searchableText;

    private List<UserEntity> tempNearByList;
    public List<UserEntity> userList;

    public DiscoverViewModel(@NonNull Application application) {
        super(application);
        this.userDataSource = UserDataSource.getInstance();
        userList = new ArrayList<>();
        tempNearByList = new ArrayList<>();
    }

    public void openMessage(@NonNull UserEntity userEntity) {
        openUserMessage.postValue(userEntity);
    }

    public void changeFavouriteStatus(@NonNull UserEntity userEntity) {
        changeFavouriteStatus.postValue(userEntity);
    }

    /*public void setSearchText(String searchText) {
        this.searchableText = searchText;
    }*/

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

                    Set<String> set = new HashSet<String>();
                    for (UserEntity o2 : userEntities) {
                        set.add(o2.getMeshId());
                    }
                    Iterator<UserEntity> i = userList.iterator();
                    while (i.hasNext()) {
                        if (set.contains(i.next().getMeshId())) {
                            i.remove();
                        }
                    }

                    List<UserEntity> userEntityList = new ArrayList<>();
                    for (UserEntity diffElement : userList) {
                        UserEntity userEntity = new UserEntity();
                        userEntity.setMeshId(diffElement.getMeshId());
                        userEntity.setUserName(diffElement.getUserName());
                        userEntity.setAvatarIndex(diffElement.getAvatarIndex());
                        userEntity.setIsFavourite(diffElement.getIsFavourite());
                        userEntity.setOnlineStatus(0);
                        userEntity.hasUnreadMessage = diffElement.hasUnreadMessage;

                        userEntityList.add(userEntity);
                    }

                    userList.clear();
                    userList.addAll(userEntityList);
                    userList.addAll(userEntities);

                    if (!TextUtils.isEmpty(searchableText)) {
                        backUserEntity.postValue(userList);
                        startSearch(searchableText, userList);
                    } else {
                        setUserData(userList);
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                }));
    }

    public void setUserData(List<UserEntity> userEntities) {
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

    public List<UserEntity> getCurrentUserList() {
/*        if (userList == null) {
            userList = new ArrayList<>();
        }*/
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
                setUserData(userEntities);
                return;
            }

            List<UserEntity> filteredItemList = new ArrayList<>();

            for (UserEntity user : userEntities) {

                if (user.getFullName().toLowerCase(Locale.getDefault()).contains(searchText)) {
                    filteredItemList.add(user);
                }

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
            Timber.tag("SearchIssue").d("user list null");
        }
    }

}
