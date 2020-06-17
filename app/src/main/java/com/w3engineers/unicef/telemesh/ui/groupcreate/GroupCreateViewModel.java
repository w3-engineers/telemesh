package com.w3engineers.unicef.telemesh.ui.groupcreate;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.helper.TeleMeshDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupAdminInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GroupCreateViewModel extends BaseRxAndroidViewModel {

    private UserDataSource userDataSource;
    private GroupDataSource groupDataSource;

    private MutableLiveData<UserEntity> openUserMessage = new MutableLiveData<>();
    MutableLiveData<PagedList<UserEntity>> nearbyUsers = new MutableLiveData<>();

    private String selectChattedUser = null;

    private static final int INITIAL_LOAD_KEY = 0;
    private static final int PAGE_SIZE = 50;
    private static final int PREFETCH_DISTANCE = 30;

    public String searchableText;

    private List<UserEntity> tempNearByList;
    public List<UserEntity> userList;

    public GroupCreateViewModel(@NonNull Application application) {
        super(application);
        this.userDataSource = UserDataSource.getInstance();
        groupDataSource = GroupDataSource.getInstance();
        userList = new ArrayList<>();
        tempNearByList = new ArrayList<>();
    }

    public void openMessage(@NonNull UserEntity userEntity) {
        openUserMessage.postValue(userEntity);
    }


    public void selectedChattedUser(String selectChattedUser) {
        this.selectChattedUser = selectChattedUser;
    }


    public int getUserAvatarByIndex(int imageIndex) {
        return TeleMeshDataHelper.getInstance().getAvatarImage(imageIndex);
    }

    MutableLiveData<UserEntity> openUserMessage() {
        return openUserMessage;
    }

    void createGroup(List<UserEntity> userEntities) {
        if (userEntities == null || userEntities.isEmpty())
            return;

        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        ArrayList<GroupMembersInfo> groupMembersInfos = new ArrayList<>();
        ArrayList<GroupAdminInfo> groupAdminInfos = new ArrayList<>();
        ArrayList<GroupUserNameMap> groupUserNameMaps = new ArrayList<>();

        String myUserId = SharedPref.getSharedPref(TeleMeshApplication.getContext())
                .read(Constants.preferenceKey.MY_USER_ID);

        String myUserName = SharedPref.getSharedPref(TeleMeshApplication.getContext())
                .read(Constants.preferenceKey.USER_NAME);

        GroupMembersInfo myGroupMembersInfo = new GroupMembersInfo();
        myGroupMembersInfo.setMemberId(myUserId);
        myGroupMembersInfo.setMemberStatus(Constants.GroupUserEvent.EVENT_PENDING);
        groupMembersInfos.add(myGroupMembersInfo);

        for (UserEntity userEntity : userEntities) {

            GroupUserNameMap groupUserNameMap = new GroupUserNameMap()
                    .setUserId(userEntity.getMeshId())
                    .setUserName(userEntity.getUserName());
            groupUserNameMaps.add(groupUserNameMap);

            GroupMembersInfo groupMembersInfo = new GroupMembersInfo();
            groupMembersInfo.setMemberId(userEntity.getMeshId());
            groupMembersInfo.setMemberStatus(Constants.GroupUserEvent.EVENT_PENDING);
            groupMembersInfos.add(groupMembersInfo);
        }

        GroupUserNameMap groupUserNameMap = new GroupUserNameMap()
                .setUserId(myUserId)
                .setUserName(myUserName);
        groupUserNameMaps.add(groupUserNameMap);

        GroupAdminInfo groupAdminInfo = new GroupAdminInfo();
        groupAdminInfo.setAdminId(myUserId);
        groupAdminInfo.setAdminStatus(true);

        groupAdminInfos.add(groupAdminInfo);

        String groupId = UUID.randomUUID().toString();

        GroupNameModel groupNameModel = new GroupNameModel()
                .setGroupName(CommonUtil.getGroupName(groupUserNameMaps))
                .setGroupUserMap(groupUserNameMaps);

        GroupEntity groupEntity = new GroupEntity()
                .setGroupId(groupId)
                .setGroupName(gsonBuilder.getGroupNameModelJson(groupNameModel))
                .setOwnStatus(Constants.GroupUserOwnState.GROUP_CREATE)
                .setMembersInfo(gsonBuilder.getGroupMemberInfoJson(groupMembersInfos))
                .setAdminInfo(gsonBuilder.getGroupAdminInfoJson(groupAdminInfos))
                .setGroupCreationTime(System.currentTimeMillis());

        getCompositeDisposable().add(Single.just(groupDataSource.insertOrUpdateGroup(groupEntity))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {

                }));
    }


    public void startUserObserver() {
        getCompositeDisposable().add(userDataSource.getAllUsersForGroup()
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

                    setUserData(userList);

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

}