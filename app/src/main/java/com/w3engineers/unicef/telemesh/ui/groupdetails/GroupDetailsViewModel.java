package com.w3engineers.unicef.telemesh.ui.groupdetails;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
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
import com.w3engineers.unicef.util.base.ui.BaseRxAndroidViewModel;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
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
    MutableLiveData<List<UserEntity>> userListsData = new MutableLiveData<>();
    HashMap<String, GroupMembersInfo> membersInfoHashMap = new HashMap<>();

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

    /*@NonNull
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
    }*/

    public void startMemberObserver(String membersInfo) {
        List<GroupMembersInfo> groupMembersInfos = GsonBuilder.getInstance()
                .getGroupMemberInfoObj(membersInfo);
        List<String> userList = new ArrayList<>();
        for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
            membersInfoHashMap.put(groupMembersInfo.getMemberId(), groupMembersInfo);

            if (!groupMembersInfo.getMemberId().equals(getMyUserId())
                    && groupMembersInfo.getMemberStatus() == Constants.GroupEvent.GROUP_JOINED) {
                userList.add(groupMembersInfo.getMemberId());
            }
        }

        getCompositeDisposable().add(userDataSource.getGroupMembers(userList)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::alignAllGroupMembers, throwable -> {
                    throwable.printStackTrace();
                }));
    }

    private void alignAllGroupMembers(List<UserEntity> userEntities) {
        for (int i = 0; i < userEntities.size(); i++) {

            UserEntity userEntity = userEntities.get(i);
            if (TextUtils.isEmpty(userEntity.getUserName())) {

                GroupMembersInfo groupMembersInfo = membersInfoHashMap.get(userEntity.getMeshId());
                if (groupMembersInfo != null) {

                    userEntity.setUserName(groupMembersInfo.getUserName())
                            .setAvatarIndex(groupMembersInfo.getAvatarPicture());
                    userEntities.set(i, userEntity);
                }
            }
        }

        userListsData.postValue(userEntities);
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
        return SharedPref.read(Constants.preferenceKey.MY_USER_ID);
    }

}
