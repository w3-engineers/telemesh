package com.w3engineers.unicef.telemesh.data.helper;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMemberChangeModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupUserNameMap;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class GroupDataHelper extends RmDataHelper {

    private static GroupDataHelper groupDataHelper = new GroupDataHelper();
    private GroupDataSource groupDataSource;

    private GroupDataHelper() {
        groupDataSource = GroupDataSource.getInstance();
    }

    @NonNull
    public static GroupDataHelper getInstance() {
        return groupDataHelper;
    }

    void groupDataObserver() {

        compositeDisposable.add(groupDataSource.getLastCreatedGroup()
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::sendGroupCreationInfo, Throwable::printStackTrace));

        compositeDisposable.add(Objects.requireNonNull(dataSource.getGroupUserEvent())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::sendGroupForEvent, Throwable::printStackTrace));

        compositeDisposable.add(Objects.requireNonNull(dataSource.getGroupRenameEvent())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::sendAndUpdateGroupChangeEvent, Throwable::printStackTrace));

        compositeDisposable.add(Objects.requireNonNull(dataSource.getGroupMembersAddEvent())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::sendNewAddedMemberToOther, Throwable::printStackTrace));
    }

    void groupDataReceive(int dataType, String userId, byte[] rawData, boolean isNewMessage) {
        if (!isNewMessage)
            return;

        switch (dataType) {
            case Constants.DataType.EVENT_GROUP_CREATION:
                receiveGroupCreationInfo(rawData, userId);
                break;
            case Constants.DataType.EVENT_GROUP_JOIN:
                receiveGroupForJoinEvent(rawData, userId);
                break;
            case Constants.DataType.EVENT_GROUP_LEAVE:
                receiveGroupForLeaveEvent(rawData, userId);
                break;
            case Constants.DataType.EVENT_GROUP_RENAME:
                receiveGroupNameChangedEvent(rawData, userId);
                break;
            case Constants.DataType.EVENT_GROUP_MEMBER_ADD:
                receiveGroupMemberAddEvent(rawData, userId);
                break;
        }
    }

    public void sendTextMessageToGroup(String groupId, String messageTextData) {
        GroupEntity groupEntity = groupDataSource.getGroupById(groupId);

        ArrayList<GroupMembersInfo> groupMembersInfos = GsonBuilder.getInstance()
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());

        if (groupMembersInfos != null) {
            for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
                String userId = groupMembersInfo.getMemberId();
                if (!userId.equals(getMyMeshId())) {
                    dataSend(messageTextData.getBytes(), Constants.DataType.MESSAGE,
                            userId, true);
                }
            }
        }
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

        groupDataSource.insertOrUpdateGroup(groupEntity);
    }

    private void receiveGroupCreationInfo(byte[] rawData, String userId) {
        try {

            GsonBuilder gsonBuilder = GsonBuilder.getInstance();

            String groupModelText = new String(rawData);
            GroupModel groupModel = gsonBuilder.getGroupModelObj(groupModelText);

            GroupEntity groupEntity = groupDataSource.getGroupById(groupModel.getGroupId());

            if (groupEntity == null) {

                groupEntity = new GroupEntity().toGroupEntity(groupModel);
            } else {

                groupEntity.setGroupName(groupModel.getGroupName());
                groupEntity.setAvatarIndex(groupModel.getAvatar());

                groupEntity.setAdminInfo(groupModel.getAdminInfo());
                groupEntity.setGroupCreationTime(groupModel.getCreatedTime());

                ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder.getGroupMemberInfoObj(groupModel.getMemberInfo());

                String storedMemberInfoText = groupEntity.getMembersInfo();

                if (TextUtils.isEmpty(storedMemberInfoText)) {
                    storedMemberInfoText = gsonBuilder.getGroupMemberInfoJson(groupMembersInfos);

                } else {
                    ArrayList<GroupMembersInfo> storedMembersInfos = gsonBuilder
                            .getGroupMemberInfoObj(storedMemberInfoText);

                    HashMap<String, GroupMembersInfo> groupMembersInfoHashMap = new HashMap<>();
                    for (GroupMembersInfo groupMembersInfo : storedMembersInfos) {
                        groupMembersInfoHashMap.put(groupMembersInfo.getMemberId(), groupMembersInfo);
                    }

                    for (int i = (groupMembersInfos.size() - 1); i >= 0; i--) {
                        GroupMembersInfo groupMembersInfo = groupMembersInfos.get(i);

                        if (groupMembersInfoHashMap.containsKey(groupMembersInfo.getMemberId())) {

                            GroupMembersInfo storedMembersInfo = groupMembersInfoHashMap
                                    .get(groupMembersInfo.getMemberId());

                            if (storedMembersInfo != null) {
                                if (storedMembersInfo.getMemberStatus() == Constants.GroupUserEvent.EVENT_LEAVE) {
                                    //Todo Mimo vai, may be You can get an exception  for remove while iterating
                                    groupMembersInfos.remove(groupMembersInfo);
                                } else {
                                    groupMembersInfos.set(i, storedMembersInfo);
                                }
                            }
                        }
                    }

                    storedMemberInfoText = gsonBuilder.getGroupMemberInfoJson(groupMembersInfos);
                }

                groupEntity.setMembersInfo(storedMemberInfoText);
            }

            groupEntity.setOwnStatus(Constants.GroupUserOwnState.GROUP_PENDING);

            groupDataSource.insertOrUpdateGroup(groupEntity);

            setGroupInfo(userId, groupEntity.getGroupId(), Constants.GroupEventMessageBody.CREATED,
                    groupEntity.getGroupCreationTime(), Constants.MessageType.GROUP_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            groupDataSource.insertOrUpdateGroup(groupEntity);

        } else if (groupEntity.getOwnStatus() == Constants.GroupUserOwnState.GROUP_LEAVE) {
            groupEvent = Constants.DataType.EVENT_GROUP_LEAVE;
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

    private void receiveGroupForJoinEvent(byte[] rawData, String userId) {
        try {
            String groupId = new String(rawData);
            GroupEntity groupEntity = groupDataSource.getGroupById(groupId);

            if (groupEntity == null) {
                groupEntity = new GroupEntity()
                        .setGroupId(groupId);

                ArrayList<GroupMembersInfo> groupMembersInfos = new ArrayList<>();
                GroupMembersInfo groupMembersInfo = new GroupMembersInfo().setMemberId(userId)
                        .setMemberStatus(Constants.GroupUserEvent.EVENT_JOINED);
                groupMembersInfos.add(groupMembersInfo);

                String groupMemberInfoText = GsonBuilder.getInstance()
                        .getGroupMemberInfoJson(groupMembersInfos);
                groupEntity.setMembersInfo(groupMemberInfoText);

            } else {
                String groupMemberInfoText = groupEntity.getMembersInfo();
                ArrayList<GroupMembersInfo> groupMembersInfos = GsonBuilder.getInstance()
                        .getGroupMemberInfoObj(groupMemberInfoText);

                boolean isMemberUpdate = false;
                for (int i = 0; i < groupMembersInfos.size(); i++) {
                    GroupMembersInfo groupMembersInfo = groupMembersInfos.get(i);

                    if (groupMembersInfo.getMemberId().equals(userId)) {
                        groupMembersInfo.setMemberStatus(Constants.GroupUserEvent.EVENT_JOINED);
                        groupMembersInfos.set(i, groupMembersInfo);
                        isMemberUpdate = true;
                    }
                }

                if (!isMemberUpdate) {
                    GroupMembersInfo groupMembersInfo = new GroupMembersInfo().setMemberId(userId)
                            .setMemberStatus(Constants.GroupUserEvent.EVENT_JOINED);
                    groupMembersInfos.add(groupMembersInfo);
                }

                groupMemberInfoText = GsonBuilder.getInstance()
                        .getGroupMemberInfoJson(groupMembersInfos);
                groupEntity.setMembersInfo(groupMemberInfoText);
            }

            groupDataSource.insertOrUpdateGroup(groupEntity);
            setGroupInfo(userId, groupEntity.getGroupId(), Constants.GroupEventMessageBody.JOINED,
                    0, Constants.MessageType.GROUP_JOIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveGroupForLeaveEvent(byte[] rawData, String userId) {
        try {
            String groupId = new String(rawData);
            GroupEntity groupEntity = groupDataSource.getGroupById(groupId);

            if (groupEntity == null) {
                groupEntity = new GroupEntity()
                        .setGroupId(groupId);

                ArrayList<GroupMembersInfo> groupMembersInfos = new ArrayList<>();
                GroupMembersInfo groupMembersInfo = new GroupMembersInfo().setMemberId(userId)
                        .setMemberStatus(Constants.GroupUserEvent.EVENT_LEAVE);
                groupMembersInfos.add(groupMembersInfo);

                String groupMemberInfoText = GsonBuilder.getInstance()
                        .getGroupMemberInfoJson(groupMembersInfos);
                groupEntity.setMembersInfo(groupMemberInfoText);

            } else {
                String groupMemberInfoText = groupEntity.getMembersInfo();
                ArrayList<GroupMembersInfo> groupMembersInfos = GsonBuilder.getInstance()
                        .getGroupMemberInfoObj(groupMemberInfoText);

                boolean isMemberLeaved = false;

                for (int i = (groupMembersInfos.size() - 1); i >= 0; i--) {
                    GroupMembersInfo groupMembersInfo = groupMembersInfos.get(i);

                    if (groupMembersInfo.getMemberId().equals(userId)) {
                        if (!TextUtils.isEmpty(groupEntity.getGroupName())) {
                            groupMembersInfos.remove(groupMembersInfo);
                        } else {
                            groupMembersInfo.setMemberStatus(Constants.GroupUserEvent.EVENT_LEAVE);
                            groupMembersInfos.set(i, groupMembersInfo);
                        }
                        isMemberLeaved = true;
                    }
                }

                if (!isMemberLeaved) {
                    GroupMembersInfo groupMembersInfo = new GroupMembersInfo().setMemberId(userId)
                            .setMemberStatus(Constants.GroupUserEvent.EVENT_LEAVE);
                    groupMembersInfos.add(groupMembersInfo);
                }

                groupMemberInfoText = GsonBuilder.getInstance()
                        .getGroupMemberInfoJson(groupMembersInfos);
                groupEntity.setMembersInfo(groupMemberInfoText);
            }

            groupDataSource.insertOrUpdateGroup(groupEntity);
            setGroupInfo(userId, groupEntity.getGroupId(), Constants.GroupEventMessageBody.LEAVE,
                    0, Constants.MessageType.GROUP_LEAVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long sendGroupNameChangeEvent(GroupModel groupModel) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        String groupId = groupModel.getGroupId();
        String newName = groupModel.getGroupName();

        GroupEntity groupEntity = groupDataSource.getGroupById(groupId);

        GroupNameModel groupNameModel = gsonBuilder.getGroupNameModelObj(groupEntity.getGroupName());
        groupNameModel.setGroupName(newName).setGroupNameChanged(true);

        String groupNameModelText = gsonBuilder.getGroupNameModelJson(groupNameModel);
        groupEntity.setGroupName(groupNameModelText);

        long result = groupDataSource.insertOrUpdateGroup(groupEntity);

        //Todo we have to update this method calling system. Cause user table has no self information
        /*setGroupInfo(getMyMeshId(), groupId, Constants.GroupEventMessageBody.RENAMED + " " + newName,
                0, Constants.MessageType.GROUP_RENAMED);*/

        String groupNameText = gsonBuilder.getGroupModelJson(groupModel);

        ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());

        if (groupMembersInfos != null) {
            for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {
                String userId = groupMembersInfo.getMemberId();
                if (!userId.equals(getMyMeshId())) {
                    dataSend(groupNameText.getBytes(), Constants.DataType.EVENT_GROUP_RENAME,
                            userId, false);
                }
            }
        }

        return result;
    }

    private void sendAndUpdateGroupChangeEvent(GroupModel groupModel) {
        compositeDisposable.add(Single.fromCallable(() -> sendGroupNameChangeEvent(groupModel))
                .subscribeOn(Schedulers.newThread())
                .subscribe(result -> {
                    Timber.d("Result: %s", result);
                }, Throwable::printStackTrace));
    }

    private void receiveGroupNameChangedEvent(byte[] rawData, String userId) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        String groupModelText = new String(rawData);
        GroupModel groupModel = gsonBuilder.getGroupModelObj(groupModelText);

        GroupEntity groupEntity = groupDataSource.getGroupById(groupModel.getGroupId());

        GroupNameModel groupNameModel = gsonBuilder.getGroupNameModelObj(groupEntity.getGroupName());
        groupNameModel.setGroupName(groupModel.getGroupName()).setGroupNameChanged(true);

        String groupNameModelText = gsonBuilder.getGroupNameModelJson(groupNameModel);
        groupEntity.setGroupName(groupNameModelText);

        groupDataSource.insertOrUpdateGroup(groupEntity);
        setGroupInfo(userId, groupModel.getGroupId(),
                Constants.GroupEventMessageBody.RENAMED + " " + groupModel.getGroupName(),
                0, Constants.MessageType.GROUP_RENAMED);
    }

    public void updateGroupUserInfo(UserEntity userEntity) {
        String updatedUserId = "%" + userEntity.getMeshId() + "%";
        List<GroupEntity> groupEntities = groupDataSource.getGroupByUserId(updatedUserId);

        for (GroupEntity groupEntity : groupEntities) {

            GroupNameModel groupNameModel = GsonBuilder.getInstance()
                    .getGroupNameModelObj(groupEntity.getGroupName());
            List<GroupUserNameMap> groupUserNameMaps = groupNameModel.getGroupUserMap();

            for (int i = 0; i < groupUserNameMaps.size(); i++) {

                GroupUserNameMap groupUserNameMap = groupUserNameMaps.get(i);
                if (groupUserNameMap.getUserId().equals(userEntity.getMeshId())) {
                    groupUserNameMap.setUserName(userEntity.getUserName());

                    groupUserNameMaps.set(i, groupUserNameMap);
                }
            }

            groupNameModel.setGroupUserMap(groupUserNameMaps);
            if (!groupNameModel.isGroupNameChanged()) {
                groupNameModel.setGroupName(CommonUtil.getGroupName(groupUserNameMaps));
            }
            String groupNameText = GsonBuilder.getInstance().getGroupNameModelJson(groupNameModel);

            groupEntity.setGroupName(groupNameText);
            groupDataSource.insertOrUpdateGroup(groupEntity);
        }
    }

    private void setGroupInfo(String userId, String groupId, String message, long time, int type) {
        String messageId = UUID.randomUUID().toString();

        MessageEntity messageEntity = new MessageEntity()
                .setMessage(message)
                .setGroupId(groupId)
                .setMessagePlace(true);

        if (time <= 0) {
            time = System.currentTimeMillis();
        }

        ChatEntity chatEntity = messageEntity
                .setFriendsId(userId)
                .setMessageId(messageId)
                .setMessageType(type)
                .setStatus(Constants.MessageStatus.STATUS_READ)
                .setTime(time)
                .setIncoming(true);

        MessageSourceData.getInstance().insertOrUpdateData(chatEntity);
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

    /**
     * This method is responsible for handle new member added in the exists group
     *
     * @param rawData The group member added info received
     * @param userId  sender id
     */
    private void receiveGroupMemberAddEvent(byte[] rawData, String userId) {
        try {
            GsonBuilder gsonBuilder = GsonBuilder.getInstance();

            String groupModelText = new String(rawData);
            GroupModel groupModel = gsonBuilder.getGroupModelObj(groupModelText);

            GroupEntity groupEntity = groupDataSource.getGroupById(groupModel.getGroupId());

            List<String> newAddedMemberList = new ArrayList<>();

            if (groupEntity == null) {
                /*
                  GroupEntity null refers that this user did not
                  receive any group information before
                 */

                groupEntity = new GroupEntity().toGroupEntity(groupModel);
            } else {
                groupEntity.setGroupName(groupModel.getGroupName());
                groupEntity.setAvatarIndex(groupModel.getAvatar());

                groupEntity.setAdminInfo(groupModel.getAdminInfo());
                groupEntity.setGroupCreationTime(groupModel.getCreatedTime());

                //This is the all member list in the received group
                ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder.getGroupMemberInfoObj(groupModel.getMemberInfo());

                String existsMemberInfo = groupEntity.getMembersInfo();

                if (TextUtils.isEmpty(existsMemberInfo)) {
                    // This indicate that this user have no group create information
                    // So ignore to to notify that new user invited
                    existsMemberInfo = gsonBuilder.getGroupMemberInfoJson(groupMembersInfos);
                    //We not use that now
                } else {
                    // The group is old. And some member information is exists

                    ArrayList<GroupMembersInfo> existMemberInfoList = gsonBuilder
                            .getGroupMemberInfoObj(existsMemberInfo);

                    List<String> oldMemberIdList = new ArrayList<>();
                    for (GroupMembersInfo membersInfo : existMemberInfoList) {
                        oldMemberIdList.add(membersInfo.getMemberId());
                    }

                    for (GroupMembersInfo membersInfo : groupMembersInfos) {
                        if (!oldMemberIdList.contains(membersInfo.getMemberId())) {
                            newAddedMemberList.add(membersInfo.getMemberId());
                        }
                    }

                }
                //Todo If user receive the group join event then group member add event what happen
            }


            //Todo check newAddedMemberList is empty or not. This list is for new added member list
            groupEntity.setMembersInfo(groupModel.getMemberInfo());

            groupDataSource.insertOrUpdateGroup(groupEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * It is responsible for two things
     * 1. For sending updated group information including new added members
     * 2. Send group join request to new added members.
     * <p>
     * There are two different events.
     *
     * @param model {@link GroupMemberChangeModel} It is contain the Update group
     *              information and list of new added member
     */
    private void sendNewAddedMemberToOther(GroupMemberChangeModel model) {
        GroupEntity updatedGroupInfo = model.groupEntity;
        List<UserEntity> newAddedUserList = model.changedUserList;
        List<String> newAddedUserIdList = convertUserModelToIds(newAddedUserList);

        GroupModel groupModel = updatedGroupInfo.toGroupModel();
        String groupModelText = GsonBuilder.getInstance().getGroupModelJson(groupModel);

        ArrayList<GroupMembersInfo> groupMembersInfos = GsonBuilder.getInstance()
                .getGroupMemberInfoObj(updatedGroupInfo.getMembersInfo());

        for (GroupMembersInfo membersInfo : groupMembersInfos) {
            if (!newAddedUserIdList.contains(membersInfo.getMemberId())) {
                dataSend(groupModelText.getBytes(), Constants.DataType.EVENT_GROUP_MEMBER_ADD,
                        membersInfo.getMemberId(), false);
            }
        }

        // Send group creation event to new added members
        for (String userId : newAddedUserIdList) {
            dataSend(groupModelText.getBytes(), Constants.DataType.EVENT_GROUP_CREATION,
                    userId, false);
        }
    }

    private List<String> convertUserModelToIds(List<UserEntity> userList) {
        List<String> userIdList = new ArrayList<>();
        for (UserEntity entity : userList) {
            userIdList.add(entity.getMeshId());
        }
        return userIdList;
    }

}
