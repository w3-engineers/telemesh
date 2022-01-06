package com.w3engineers.unicef.telemesh.ui.groupcreate;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;
import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.common.util.CollectionUtils;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.pager.MainThreadExecutor;
import com.w3engineers.unicef.telemesh.ui.meshcontact.UserPositionalDataSource;
import com.w3engineers.unicef.util.base.ui.BaseRxAndroidViewModel;
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

    MutableLiveData<PagedList<UserEntity>> nearbyUsers = new MutableLiveData<>();
    MutableLiveData<GroupEntity> groupUserList = new MutableLiveData<>();

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

    private String getMyUserId() {
        return SharedPref.read(Constants.preferenceKey.MY_USER_ID);
    }

    public boolean isDifferentLists(List<String> listOne, List<String> listTwo) {
        List<String> listOneCopy = new ArrayList<>(listOne);
        List<String> listTwoCopy = new ArrayList<>(listTwo);
        listOneCopy.removeAll(listTwoCopy);

        return CollectionUtils.isEmpty(listOneCopy);
    }

    public boolean checkGroupExists(List<UserEntity> userEntities, ArrayList<GroupMembersInfo> groupMembersInfos) {
        List<String> previousUserIds = new ArrayList<>();
        String myUserId = getMyUserId();
        for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
            if (!groupMembersInfo.getMemberId().equals(myUserId) && groupMembersInfo.getMemberStatus() == Constants.GroupEvent.GROUP_JOINED) {
                previousUserIds.add(groupMembersInfo.getMemberId());
            }
        }

        List<String> newUserIds = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            newUserIds.add(userEntity.getMeshId());
        }
        return isDifferentLists(previousUserIds, newUserIds);
    }

    GroupEntity createGroup(List<UserEntity> userEntities) {

        List<GroupEntity> allGroups = groupDataSource.getAllGroup();
        for (GroupEntity entity : allGroups) {
            ArrayList<GroupMembersInfo> groupMembersInfos = entity.getMembersArray();
            if (groupMembersInfos.size() == userEntities.size() + 1) {
                if (checkGroupExists(userEntities, groupMembersInfos)) {
                    return entity;
                }
            }
        }

        if (userEntities == null || userEntities.isEmpty())
            return null;

        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        ArrayList<GroupMembersInfo> groupMembersInfos = new ArrayList<>();

        String myUserId = getMyUserId();

        String myUserName = SharedPref.read(Constants.preferenceKey.USER_NAME);
        String myLastName = SharedPref.read(Constants.preferenceKey.LAST_NAME);
        int avatarIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);

        GroupMembersInfo myGroupMembersInfo = new GroupMembersInfo()
                .setMemberId(myUserId)
                .setUserName(myUserName)
                .setLastName(myLastName)
                .setMemberStatus(Constants.GroupEvent.GROUP_JOINED)
                .setAvatarPicture(avatarIndex)
                .setIsAdmin(true);
        groupMembersInfos.add(myGroupMembersInfo);

        for (UserEntity userEntity : userEntities) {

            GroupMembersInfo groupMembersInfo = new GroupMembersInfo()
                    .setMemberId(userEntity.getMeshId())
                    .setUserName(userEntity.getUserName())
                    .setLastName(userEntity.getUserLastName())
                    .setAvatarPicture(userEntity.getAvatarIndex())
                    .setMemberStatus(Constants.GroupEvent.GROUP_JOINED);

            groupMembersInfos.add(groupMembersInfo);
        }

        String groupId = UUID.randomUUID().toString();

        GroupNameModel groupNameModel = new GroupNameModel()
                .setGroupName(CommonUtil.getGroupNameByUser(groupMembersInfos));

        GroupEntity groupEntity = new GroupEntity()
                .setGroupId(groupId)
                .setGroupName(gsonBuilder.getGroupNameModelJson(groupNameModel))
                .setOwnStatus(Constants.GroupEvent.GROUP_CREATE)
                .setMembersInfo(gsonBuilder.getGroupMemberInfoJson(groupMembersInfos))
                .setAdminInfo(myUserId)
                .setGroupCreationTime(System.currentTimeMillis());

        getCompositeDisposable().add(Single.just(groupDataSource.insertOrUpdateGroup(groupEntity))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong > 0) {
                        groupUserList.postValue(groupEntity);

                        ArrayList<GroupEntity> groupEntities = new ArrayList<>();
                        groupEntities.add(groupEntity);
                        AnalyticsDataHelper.getInstance().sendGroupCount(groupEntities);
                    }
                }));
        return null;
    }


    public void startUserObserver() {
        getCompositeDisposable().add(userDataSource.getAllUsersForGroup(getMyUserId())
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
                        userEntity.setUserLastName(diffElement.getUserLastName());
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