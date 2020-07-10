package com.w3engineers.unicef.telemesh.ui.groupdetails;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

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
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            if (!groupMembersInfo.getMemberId().equals(getMyUserId())
                    && groupMembersInfo.getMemberStatus() == Constants.GroupEvent.GROUP_JOINED) {
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
                    groupEntity.setOwnStatus(Constants.GroupEvent.GROUP_LEAVE);
                    groupEntity.setGroupInfoId(UUID.randomUUID().toString());
                    dataSource.setGroupUserLeaveEvent(groupEntity);
                    finishForGroupLeave.postValue(true);
                }, Throwable::printStackTrace));
    }

    void memberRemoveAction(GroupEntity groupEntity, UserEntity userEntity) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());

        for (int i = 0; i < groupMembersInfos.size(); i++) {

            GroupMembersInfo groupMembersInfo = groupMembersInfos.get(i);

            if (groupMembersInfo.getMemberId().equals(userEntity.getMeshId())) {
                groupMembersInfo.setMemberStatus(Constants.GroupEvent.GROUP_LEAVE);
                groupMembersInfos.set(i, groupMembersInfo);
            }
        }

        groupEntity.setMembersInfo(gsonBuilder.getGroupMemberInfoJson(groupMembersInfos));

        //checking name group name contains user name or changed
        GroupNameModel groupNameModel = gsonBuilder.getGroupNameModelObj(groupEntity.getGroupName());

        if (!groupNameModel.isGroupNameChanged()) {
            groupNameModel.setGroupName(CommonUtil.getGroupNameByUser(groupMembersInfos));
            groupEntity.setGroupName(gsonBuilder.getGroupNameModelJson(groupNameModel));
        }

        getCompositeDisposable().add(Single.just(groupDataSource.insertOrUpdateGroup(groupEntity))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong > 0) {
                        GroupMemberChangeModel memberChangeModel = new GroupMemberChangeModel();
                        memberChangeModel.groupEntity = groupEntity;
                        memberChangeModel.removeUser = userEntity;
                        memberChangeModel.infoId = UUID.randomUUID().toString();
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
