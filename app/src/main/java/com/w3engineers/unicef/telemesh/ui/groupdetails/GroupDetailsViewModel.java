package com.w3engineers.unicef.telemesh.ui.groupdetails;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailsViewModel extends BaseRxAndroidViewModel {
    private GroupDataSource groupDataSource;
    private UserDataSource userDataSource;

    public GroupDetailsViewModel(@NonNull Application application) {
        super(application);
        groupDataSource = GroupDataSource.getInstance();
        userDataSource = UserDataSource.getInstance();
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
            userList.add(groupMembersInfo.getMemberId());
        }

        return userDataSource.getGroupMembers(userList);
    }

}
