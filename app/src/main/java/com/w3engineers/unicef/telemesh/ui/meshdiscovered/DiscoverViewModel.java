package com.w3engineers.unicef.telemesh.ui.meshdiscovered;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;

import com.w3engineers.appshare.application.ui.InAppShareControl;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountDataService;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.pager.MainThreadExecutor;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.ui.meshcontact.UserPositionalDataSource;
import com.w3engineers.unicef.util.base.ui.BaseRxAndroidViewModel;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DiscoverViewModel extends BaseRxAndroidViewModel implements InAppShareControl.AppShareCallback {

    private UserDataSource userDataSource;
    private MutableLiveData<UserEntity> openUserMessage = new MutableLiveData<>();
    private MutableLiveData<UserEntity> changeFavouriteStatus = new MutableLiveData<>();
    MutableLiveData<PagedList<UserEntity>> nearbyUsers = new MutableLiveData<>();
    MutableLiveData<List<UserEntity>> backUserEntity = new MutableLiveData<>();
    private MutableLiveData<PagedList<UserEntity>> filterUserList = new MutableLiveData<>();
    private String selectChattedUser = null;

    private static final int INITIAL_LOAD_KEY = 0;
    private static final int PAGE_SIZE = 50;
    private static final int PREFETCH_DISTANCE = 30;

    public String searchableText;

    private List<UserEntity> tempNearByList;
    public List<UserEntity> userList;
    private String myMeshId = null;

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

    public void selectedChattedUser(String selectChattedUser) {
        this.selectChattedUser = selectChattedUser;
    }

    private String getMyMeshId() {
        if (TextUtils.isEmpty(myMeshId)) {
            myMeshId = SharedPref.read(Constants.preferenceKey.MY_USER_ID);
        }
        return myMeshId;
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

                    if (userEntities != null) {

                        if (!TextUtils.isEmpty(getMyMeshId())) {

                            Iterator<UserEntity> userEntityIterator = userEntities.iterator();

                            while (userEntityIterator.hasNext()) {
                                if (getMyMeshId().equals(userEntityIterator.next().getMeshId())) {
                                    userEntityIterator.remove();
                                }
                            }
                        }

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
                            userEntity.setOnlineStatus(Constants.UserStatus.OFFLINE);
                            userEntity.hasUnreadMessage = (!TextUtils.isEmpty(selectChattedUser) && selectChattedUser.equals(userEntity.getMeshId())) ? 0 : diffElement.hasUnreadMessage;

                            userEntityList.add(userEntity);
                        }

                        userList.clear();
                        userList.addAll(userEntities);

                        if (!userEntityList.isEmpty()) {
                            Collections.sort(userEntityList, (o1, o2) -> {
                                if (o1.getUserName() != null && o2.getUserName() != null) {
                                    return o1.getUserName().compareTo(o2.getUserName());
                                }
                                return 0;
                            });
                        }

                        userList.addAll(userEntityList);

                        if (!TextUtils.isEmpty(searchableText)) {
                            backUserEntity.postValue(userList);
                            startSearch(searchableText, userList);
                        } else {
                            setUserData(userList);
                        }
                    }

                }, Throwable::printStackTrace));
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
//            Timber.tag("SearchIssue").d("user list null");
        }
    }

    public void startInAppShareProcess() {
        InAppShareControl.getInstance().startInAppShareProcess(getApplication().getApplicationContext(), this);
    }

    @Override
    public void closeRmService() {
        RmDataHelper.getInstance().stopRmService();
    }

    @Override
    public void successShared() {
        HandlerUtil.postBackground(() -> {
            String date = TimeUtil.getDateString(System.currentTimeMillis());
            String myId = SharedPref.read(Constants.preferenceKey.MY_USER_ID);
            Log.d("WalletAddress", "My address: " + myId);
            boolean isExist = AppShareCountDataService.getInstance().isCountExist(myId, date);
            if (isExist) {
                AppShareCountDataService.getInstance().updateCount(myId, date);
            } else {
                AppShareCountEntity entity = new AppShareCountEntity();
                entity.setDate(date);
                entity.setCount(1);
                entity.setUserId(myId);
                AppShareCountDataService.getInstance().insertAppShareCount(entity);
            }
        });
    }

    @Override
    public void closeInAppShare() {
        HandlerUtil.postBackground(this::restartMesh, 5000);
    }

    private void restartMesh() {
        ServiceLocator.getInstance().resetMesh();
    }
}
