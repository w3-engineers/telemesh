package com.w3engineers.unicef.telemesh.data.helper;

import android.support.annotation.NonNull;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupModel;
import com.w3engineers.unicef.util.helper.GsonBuilder;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.schedulers.Schedulers;

public class GroupDataHelper extends RmDataHelper {

    private static GroupDataHelper groupDataHelper = new GroupDataHelper();

    private GroupDataHelper() {

    }

    @NonNull
    public static GroupDataHelper getInstance() {
        return groupDataHelper;
    }

    public void groupDataObserver() {

        compositeDisposable.add(Objects.requireNonNull(dataSource.getGroupUserEvent())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::sendGroupForEvent, Throwable::printStackTrace));
    }

    private void sendGroupCreationInfo(GroupEntity groupEntity) {
        GroupModel groupModel = groupEntity.toGroupModel();
        String groupModelText = GsonBuilder.getInstance().getGroupModelJson(groupModel);

        ArrayList<GroupMembersInfo> groupMembersInfos = GsonBuilder.getInstance()
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());

        if (groupMembersInfos != null) {
            for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
                String userId = groupMembersInfo.getMemberId();
                if (!userId.equals(getMyMeshId())) {
                    dataSend(groupModelText.getBytes(), Constants.DataType.EVENT_GROUP_CREATION, userId, false);
                }
            }
        }

        setGroupJoined(groupMembersInfos);

        String groupMemberInfoText = GsonBuilder.getInstance().getGroupMemberInfoJson(groupMembersInfos);

        groupEntity.setOwnStatus(Constants.GroupUserOwnState.GROUP_JOINED);
        groupEntity.setMembersInfo(groupMemberInfoText);

        // TODO Group 1 - Update group databases
    }

    private void sendGroupForEvent(GroupEntity groupEntity) {

        ArrayList<GroupMembersInfo> groupMembersInfos = GsonBuilder.getInstance()
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());

        byte groupEvent = -1;

        if (groupEntity.getOwnStatus() == Constants.GroupUserOwnState.GROUP_JOINED) {

            groupEvent = Constants.DataType.EVENT_GROUP_JOIN;
            setGroupJoined(groupMembersInfos);

            String groupMemberInfoText = GsonBuilder.getInstance().getGroupMemberInfoJson(groupMembersInfos);
            groupEntity.setMembersInfo(groupMemberInfoText);

            // TODO Group 2 - Update group databases

        } else if (groupEntity.getOwnStatus() == Constants.GroupUserOwnState.GROUP_LEAVE) {
            groupEvent = Constants.DataType.EVENT_GROUP_LEAVE;

            // TODO Group 3 - Delete group databases
        }

        if (groupMembersInfos != null) {
            for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
                String userId = groupMembersInfo.getMemberId();
                if (!userId.equals(getMyMeshId())) {
                    dataSend(groupEntity.getGroupId().getBytes(),
                            groupEvent, userId, false);
                }
            }
        }
    }

    private void setGroupJoined(ArrayList<GroupMembersInfo> groupMembersInfos) {
        if (groupMembersInfos != null) {
            for (int i = 0; i < groupMembersInfos.size(); i++) {
                GroupMembersInfo groupMembersInfo = groupMembersInfos.get(i);

                if (groupMembersInfo.getMemberId().equals(getMyMeshId())) {
                    groupMembersInfo.setMemberStatus(Constants.GroupUserEvent.EVENT_JOINED);

                    groupMembersInfos.set(i, groupMembersInfo);
                }
            }
        }
    }

}
