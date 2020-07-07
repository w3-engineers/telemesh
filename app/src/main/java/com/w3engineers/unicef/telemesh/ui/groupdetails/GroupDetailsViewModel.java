package com.w3engineers.unicef.telemesh.ui.groupdetails;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
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
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GroupDetailsViewModel extends BaseRxAndroidViewModel {
    private GroupDataSource groupDataSource;
    private UserDataSource userDataSource;
    private DataSource dataSource;

    MutableLiveData<Boolean> finishForGroupLeave = new MutableLiveData<>();

    public GroupDetailsViewModel(@NonNull Application application) {
        super(application);
        groupDataSource = GroupDataSource.getInstance();
        userDataSource = UserDataSource.getInstance();
        dataSource = Source.getDbSource();
    }

    @NonNull
    LiveData<GroupEntity> getLiveGroupById(@NonNull String threadId) {
        return groupDataSource.getLiveGroupById(threadId);
    }

    @NonNull
    LiveData<List<UserEntity>> getGroupUsersById(@NonNull String membersInfo) {

        List<GroupMembersInfo> groupMembersInfos = GsonBuilder.getInstance()
                .getGroupMemberInfoObj(membersInfo);
        List<String> userList = new ArrayList<>();
        for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
            if (!groupMembersInfo.getMemberId().equals(getMyUserId())) {
                userList.add(groupMembersInfo.getMemberId());
            }
        }

        return userDataSource.getGroupMembers(userList);
    }

    void groupLeaveAction(GroupEntity groupEntity) {
        getCompositeDisposable().add(deleteGroupOperation(groupEntity.groupId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    groupEntity.setOwnStatus(Constants.GroupUserOwnState.GROUP_LEAVE);
                    dataSource.setGroupUserEvent(groupEntity);
                    finishForGroupLeave.postValue(true);
                }, Throwable::printStackTrace));
    }

    void memberRemoveAction(GroupEntity groupEntity, UserEntity userEntity) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        ArrayList<GroupMembersInfo> groupMemberList = GsonBuilder.getInstance()
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());

        //checking name group name contains user name or changed
        GroupNameModel groupNameModel = GsonBuilder.getInstance().getGroupNameModelObj(groupEntity.getGroupName());

        List<GroupUserNameMap> existGroupUserNameMap = new ArrayList<>(groupNameModel.getGroupUserMap());


        GroupMembersInfo removedMemberInfo = null;

        for (GroupMembersInfo membersInfo : groupMemberList) {
            if (userEntity.getMeshId() != null &&
                    userEntity.getMeshId().equals(membersInfo.getMemberId())) {
                removedMemberInfo = membersInfo;
                break;
            }
        }

        if (removedMemberInfo != null) {
            groupMemberList.remove(removedMemberInfo);
        } else {
            return;
        }

        // remove the user from group name map
        GroupUserNameMap removedUserNameMap = null;
        for (GroupUserNameMap nameMap : existGroupUserNameMap) {
            if (nameMap.getUserId().equals(userEntity.getMeshId())) {
                removedUserNameMap = nameMap;
                break;
            }
        }

        if (removedUserNameMap != null) {
            existGroupUserNameMap.remove(removedUserNameMap);
        }


        if (!groupNameModel.isGroupNameChanged()) {
            groupNameModel.setGroupName(CommonUtil.getGroupName(existGroupUserNameMap));
        }

        groupEntity.setMembersInfo(gsonBuilder
                .getGroupMemberInfoJson(groupMemberList))
                .setGroupName(gsonBuilder.getGroupNameModelJson(groupNameModel));


        getCompositeDisposable().add(Single.just(groupDataSource.insertOrUpdateGroup(groupEntity))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong > 0) {
                        GroupMemberChangeModel memberChangeModel = new GroupMemberChangeModel();
                        List<UserEntity> memberList = new ArrayList<>();
                        memberList.add(userEntity);
                        memberChangeModel.groupEntity = groupEntity;
                        memberChangeModel.changedUserList = memberList;
                        dataSource.setGroupMemberRemoveEvent(memberChangeModel);
                    }
                }));
    }

    private Single<Integer> deleteGroupOperation(String groupId) {
        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    emitter.onSuccess(groupDataSource.deleteGroupById(groupId));
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }

    private String getMyUserId() {
        return SharedPref.getSharedPref(TeleMeshApplication.getContext())
                .read(Constants.preferenceKey.MY_USER_ID);
    }

}
