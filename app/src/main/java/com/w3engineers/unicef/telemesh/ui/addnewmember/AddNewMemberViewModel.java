package com.w3engineers.unicef.telemesh.ui.addnewmember;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMemberChangeModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupUserNameMap;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.pager.MainThreadExecutor;
import com.w3engineers.unicef.telemesh.ui.meshcontact.UserPositionalDataSource;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddNewMemberViewModel extends BaseRxAndroidViewModel {

    private UserDataSource userDataSource;
    private GroupDataSource groupDataSource;
    private DataSource dataSource;

    MutableLiveData<PagedList<UserEntity>> nearbyUsers = new MutableLiveData<>();
    MutableLiveData<GroupEntity> groupUserList = new MutableLiveData<>();

    MutableLiveData<PagedList<UserEntity>> filterUserList = new MutableLiveData<>();

    MutableLiveData<List<UserEntity>> backUserEntity = new MutableLiveData<>();

    private String selectChattedUser = null;

    private static final int INITIAL_LOAD_KEY = 0;
    private static final int PAGE_SIZE = 50;
    private static final int PREFETCH_DISTANCE = 30;

    public String searchableText;

    private List<UserEntity> tempNearByList;
    public List<UserEntity> userList;

    public AddNewMemberViewModel(@NonNull Application application) {
        super(application);
        this.userDataSource = UserDataSource.getInstance();
        groupDataSource = GroupDataSource.getInstance();
        dataSource = Source.getDbSource();
        userList = new ArrayList<>();
        tempNearByList = new ArrayList<>();
    }

    void addMembersInGroup(GroupEntity groupEntity, List<UserEntity> userList) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        // add member info
        ArrayList<GroupMembersInfo> membersInfos = GsonBuilder.getInstance()
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());
        for (UserEntity userEntity : userList) {
            GroupMembersInfo groupMembersInfo = new GroupMembersInfo();
            groupMembersInfo.setMemberId(userEntity.getMeshId());
            groupMembersInfo.setMemberStatus(Constants.GroupUserEvent.EVENT_PENDING);
            membersInfos.add(groupMembersInfo);
        }

        //checking name group name contains user name or changed
        GroupNameModel groupNameModel = GsonBuilder.getInstance().getGroupNameModelObj(groupEntity.getGroupName());

        List<GroupUserNameMap> existGroupUserNameMap = groupNameModel.getGroupUserMap();


        for (UserEntity userEntity : userList) {
            GroupUserNameMap groupUserNameMap = new GroupUserNameMap()
                    .setUserId(userEntity.getMeshId())
                    .setUserName(userEntity.getUserName());
            existGroupUserNameMap.add(groupUserNameMap);
        }
        if (!groupNameModel.isGroupNameChanged()) {
            // We have to change group name
            groupNameModel.setGroupName(CommonUtil.getGroupName(existGroupUserNameMap));
        }


        groupEntity.setGroupName(gsonBuilder.getGroupNameModelJson(groupNameModel))
                .setMembersInfo(gsonBuilder.getGroupMemberInfoJson(membersInfos));

        getCompositeDisposable().add(Single.just(groupDataSource.insertOrUpdateGroup(groupEntity))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong > 0) {
                        groupUserList.postValue(groupEntity);
                        GroupMemberChangeModel memberChangeModel = new GroupMemberChangeModel();
                        memberChangeModel.changedUserList = userList;
                        memberChangeModel.groupEntity = groupEntity;
                        dataSource.setAddNewMemberEvent(memberChangeModel);
                    }
                }));


    }

    private String getMyUserId() {
        return SharedPref.getSharedPref(TeleMeshApplication.getContext())
                .read(Constants.preferenceKey.MY_USER_ID);
    }

    @NonNull
    LiveData<GroupEntity> getLiveGroupById(@NonNull String threadId) {
        return groupDataSource.getLiveGroupById(threadId);
    }


    public void startUserObserver(List<String> existMemberList) {
        getCompositeDisposable().add(userDataSource.getAllUsersForGroup(getMyUserId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntities -> {

                    Iterator<UserEntity> i2 = userEntities.iterator();

                    while (i2.hasNext()) {
                        if (existMemberList.contains(i2.next().getMeshId())) {
                            i2.remove();
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
        return userList;
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

}