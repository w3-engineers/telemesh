package com.w3engineers.unicef.telemesh.data.helper;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.net.Network;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsDataHelper;
import com.w3engineers.unicef.telemesh.data.analytics.model.GroupCountParseModel;
import com.w3engineers.unicef.telemesh.data.analytics.parseapi.ParseConstant;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackEntity;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.ForwardGroupModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupCountModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupDataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMemberChangeModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMembersInfo;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.RelayGroupModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.GroupMessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.ConnectivityUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.NotifyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        compositeDisposable.add(Objects.requireNonNull(dataSource.getGroupUserLeaveEvent())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::sendGroupLeave, throwable -> {
                    throwable.printStackTrace();
                }));

        compositeDisposable.add(Objects.requireNonNull(dataSource.getGroupInfoChangeEvent())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::sendGroupInfoChangeEvent, Throwable::printStackTrace));

        compositeDisposable.add(Objects.requireNonNull(dataSource.getGroupMembersAddEvent())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::addNewMemberOperation, Throwable::printStackTrace));

        compositeDisposable.add(Objects.requireNonNull(dataSource.getGroupMemberRemoveEvent())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::removeMemberOperation, Throwable::printStackTrace));
    }

    void groupDataReceive(int dataType, String userId, byte[] rawData, boolean isNewMessage) {
        if (!isNewMessage)
            return;

        switch (dataType) {
            case Constants.DataType.EVENT_GROUP_CREATION:
                receiveGroupCreationInfo(rawData, userId);
                break;
            case Constants.DataType.EVENT_GROUP_LEAVE:
                receiveGroupUserLeaveEvent(rawData, userId);
                break;
            case Constants.DataType.EVENT_GROUP_RENAME:
                receiveGroupInfoChangeEvent(rawData, userId);
                break;
            case Constants.DataType.EVENT_GROUP_MEMBER_ADD:
                receiveGroupMemberAddEvent(rawData, userId);
                break;
            case Constants.DataType.EVENT_GROUP_MEMBER_REMOVE:
                receiveGroupMemberRemoveEvent(rawData, userId);
                break;
            case Constants.DataType.EVENT_GROUP_DATA_RELAY:
                receiveTheRelayMessage(rawData, userId);
                break;
            case Constants.DataType.EVENT_GROUP_DATA_FORWARD:
                receiveForwardMessage(rawData, userId);
                break;
            case Constants.DataType.EVENT_GROUP_COUNT_SYNC_REQUEST:
                parseGroupCountSyncRequest(rawData);
                break;
            case Constants.DataType.EVENT_GROUP_COUNT_SYNC_ACK:
                groupCountSyncAckReceived(rawData);
                break;
            case Constants.DataType.EVENT_GROUP_COUNT_SYNCED:
                groupCountSyncedReceived(rawData);
                break;
        }
    }

    private void parseGroupCountSyncRequest(byte[] rawData) {
        ConnectivityUtil.isInternetAvailable(TeleMeshApplication.getContext(), (s, isConnected) -> {
            if (isConnected) {
                String groupCountRawData = new String(rawData);
                ArrayList<GroupCountModel> groupCountModels = GsonBuilder.getInstance().getGroupCountModels(groupCountRawData);
                AnalyticsDataHelper.getInstance().sendGroupCountToInternet(groupCountModels);
            }
        });
    }

    private void groupCountSyncedReceived(byte[] rawData) {
        String groupId = new String(rawData);
        compositeDisposable
                .add(Single.fromCallable(() -> GroupDataSource.getInstance().updateGroupAsSynced(groupId))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe((result) -> {
                            Timber.tag("GroupCount").d("update result: %s", result);
                        }, Throwable::printStackTrace));
    }

    private void groupCountSyncAckReceived(byte[] rawData) {
        String groupCountRawData = new String(rawData);
        ArrayList<GroupCountModel> groupCountModels = GsonBuilder.getInstance().getGroupCountModels(groupCountRawData);
        for (GroupCountModel groupCountModel : groupCountModels) {
            compositeDisposable
                    .add(Single.fromCallable(() -> GroupDataSource.getInstance().updateGroupAsSynced(groupCountModel.getGroupId()))
                            .subscribeOn(Schedulers.newThread())
                            .subscribe((result) -> {
                                Timber.tag("GroupCount").d("update result: %s", result);
                                RmDataHelper.getInstance().notifyGroupMembersAsSyncedGroup(groupCountModel.getGroupId());
                            }, Throwable::printStackTrace));

        }
    }


    public void sendTextMessageToGroup(String groupId, String messageTextData) {
        GroupEntity groupEntity = groupDataSource.getGroupById(groupId);

        ArrayList<GroupMembersInfo> groupMembersInfos = groupEntity.getMembersArray();

        sendDataToAllMembers(messageTextData, Constants.DataType.MESSAGE,
                groupEntity.getAdminInfo(), groupMembersInfos);
    }


    public void prepareAndSendGroupContent(GroupMessageEntity entity, boolean isSend) {

        //Send a text message for dummy content
        String messageModelString = new Gson().toJson(entity.toMessageModel());
        sendTextMessageToGroup(entity.groupId, messageModelString);

        //Update message status to prevent recursive call
        entity.setStatus(Constants.MessageStatus.STATUS_RECEIVED);
        entity.setContentProgress(100);
        MessageSourceData.getInstance().insertOrUpdateData(entity);

        //Send original content now
        GroupEntity groupEntity = groupDataSource.getGroupById(entity.groupId);
        ArrayList<GroupMembersInfo> groupMembersInfoList = groupEntity.getMembersArray();

        if (groupMembersInfoList != null) {
            List<String> liveMembersId = CommonUtil.getGroupLiveMembersId(groupMembersInfoList);
            liveMembersId.remove(getMyMeshId());

            List<UserEntity> availableUsers = UserDataSource.getInstance().getLiveGroupMembers(liveMembersId);

            for (UserEntity item : availableUsers) {

                prepareContent(entity, item);
                //Timber.v("Group Message Test", "content start %s", item.getMeshId());

            }
        }


    }

    public void prepareContent(GroupMessageEntity entity, UserEntity item){
        ContentModel contentModel = new ContentModel()
                .setMessageId(entity.getMessageId())
                .setMessageType(entity.getMessageType())
                .setGroupId(entity.getGroupId())
                .setOriginalSender(entity.getOriginalSender())
                .setGroupContent(true)
                .setContentPath(entity.getContentPath())
                .setThumbPath(entity.getContentThumb())
                .setUserId(entity.getFriendsId())
                .setContentInfo(entity.getContentInfo());

        contentModel.setUserId(item.getMeshId());
        contentMessageSend(contentModel);
    }

    private void contentMessageSend(ContentModel contentModel) {
        prepareRightMeshDataSource();

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> rightMeshDataSource.ContentDataSend(contentModel));
    }


    ///////////////////////////////////////////////////////////////

    private void sendGroupCreationInfo(GroupEntity groupEntity) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        GroupModel groupModel = groupEntity.toGroupModel();
        String groupModelText = gsonBuilder.getGroupModelJson(groupModel);

        ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());

        sendDataToAllMembers(groupModelText, Constants.DataType.EVENT_GROUP_CREATION,
                groupEntity.getAdminInfo(), groupMembersInfos);

        groupEntity.setOwnStatus(Constants.GroupEvent.GROUP_JOINED);
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
                groupEntity.setSynced(groupModel.isSynced());

                String storedMemberInfoText = groupEntity.getMembersInfo();

                ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder
                        .getGroupMemberInfoObj(groupModel.getMemberInfo());

                if (TextUtils.isEmpty(storedMemberInfoText)) {
                    storedMemberInfoText = gsonBuilder.getGroupMemberInfoJson(groupMembersInfos);

                } else {
                    ArrayList<GroupMembersInfo> storedMembersInfos = gsonBuilder
                            .getGroupMemberInfoObj(storedMemberInfoText);

                    HashMap<String, GroupMembersInfo> groupMembersInfoHashMap = new HashMap<>();
                    for (GroupMembersInfo groupMembersInfo : storedMembersInfos) {
                        groupMembersInfoHashMap.put(groupMembersInfo.getMemberId(), groupMembersInfo);
                    }

                    for (int i = 0; i < groupMembersInfos.size(); i++) {
                        GroupMembersInfo groupMembersInfo = groupMembersInfos.get(i);
                        if (groupMembersInfoHashMap.containsKey(groupMembersInfo.getMemberId())) {

                            GroupMembersInfo storedGroupMemberInfo = groupMembersInfoHashMap
                                    .get(groupMembersInfo.getMemberId());

                            if (storedGroupMemberInfo != null) {
                                groupMembersInfo.setMemberStatus(storedGroupMemberInfo.getMemberStatus());
                                groupMembersInfos.set(i, groupMembersInfo);
                            }
                        }
                    }
                    storedMemberInfoText = gsonBuilder.getGroupMemberInfoJson(groupMembersInfos);
                }

                groupEntity.setMembersInfo(storedMemberInfoText);
            }

            groupEntity.setOwnStatus(Constants.GroupEvent.GROUP_JOINED);

            groupDataSource.insertOrUpdateGroup(groupEntity);


            ArrayList<GroupEntity> groupEntities = new ArrayList<>();
            groupEntities.add(groupEntity);
            AnalyticsDataHelper.getInstance().sendGroupCount(groupEntities);


            NotifyUtil.showGroupEventNotification(userId, groupEntity);
            setGroupInfo(userId, groupEntity.getGroupId(), Constants.GroupEventMessageBody.CREATED,
                    groupEntity.getGroupCreationTime(), Constants.MessageType.GROUP_CREATE,
                    groupEntity.getGroupId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    private void sendGroupLeave(GroupEntity groupEntity) {
        compositeDisposable.add(Single.fromCallable(() -> sendGroupLeaveEvent(groupEntity))
                .subscribeOn(Schedulers.newThread())
                .subscribe(result -> {
                    Timber.d("Result: %s", result);
                }, Throwable::printStackTrace));
    }

    private boolean sendGroupLeaveEvent(GroupEntity groupEntity) {

        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());

        byte groupEvent = Constants.DataType.EVENT_GROUP_LEAVE;

        GroupModel groupModel = new GroupModel()
                .setGroupId(groupEntity.getGroupId())
                .setInfoId(groupEntity.getGroupInfoId());

        String groupModelText = gsonBuilder.getGroupModelJson(groupModel);

        sendDataToAllMembers(groupModelText, groupEvent,
                groupEntity.getAdminInfo(), groupMembersInfos);
        return true;
    }

    public GroupEntity formGroupEntity(String groupId, String userId){

        GroupEntity groupEntity = new GroupEntity().setGroupId(groupId);
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();
        ArrayList<GroupMembersInfo> groupMembersInfos = new ArrayList<>();

        GroupMembersInfo groupMembersInfo = new GroupMembersInfo()
                .setMemberId(userId)
                .setMemberStatus(Constants.GroupEvent.GROUP_LEAVE);
        groupMembersInfos.add(groupMembersInfo);


        String groupMemberInfoText = gsonBuilder.getGroupMemberInfoJson(groupMembersInfos);
        groupEntity.setMembersInfo(groupMemberInfoText);
        return groupEntity;
    }

    private void receiveGroupUserLeaveEvent(byte[] rawData, String userId) {
        try {
            GsonBuilder gsonBuilder = GsonBuilder.getInstance();

            String groupModelText = new String(rawData);
            GroupModel groupModel = gsonBuilder.getGroupModelObj(groupModelText);

            String groupId = groupModel.getGroupId();
            ArrayList<GroupMembersInfo> groupMembersInfos = new ArrayList<>();

            GroupEntity groupEntity = groupDataSource.getGroupById(groupId);

            if (groupEntity == null) {

                groupEntity = formGroupEntity(groupId, userId);

            } else {
                String groupMemberInfoText = groupEntity.getMembersInfo();
                groupMembersInfos = gsonBuilder.getGroupMemberInfoObj(groupMemberInfoText);

                boolean isMemberLeaved = false;

                for (int i = 0; i < groupMembersInfos.size(); i++) {
                    GroupMembersInfo groupMembersInfo = groupMembersInfos.get(i);

                    if (groupMembersInfo.getMemberId().equals(userId)) {

                        groupMembersInfo.setMemberStatus(Constants.GroupEvent.GROUP_LEAVE);
                        groupMembersInfos.set(i, groupMembersInfo);
                        isMemberLeaved = true;
                    }
                }

                if (!isMemberLeaved) {
                    GroupMembersInfo groupMembersInfo = new GroupMembersInfo()
                            .setMemberId(userId)
                            .setMemberStatus(Constants.GroupEvent.GROUP_LEAVE);
                    groupMembersInfos.add(groupMembersInfo);
                }

                groupMemberInfoText = gsonBuilder.getGroupMemberInfoJson(groupMembersInfos);
                groupEntity.setMembersInfo(groupMemberInfoText);
            }

            String groupName = groupEntity.getGroupName();

            if (!TextUtils.isEmpty(groupName)) {
                GroupNameModel groupNameModel = gsonBuilder.getGroupNameModelObj(groupName);

                if (!groupNameModel.isGroupNameChanged()) {
                    groupNameModel.setGroupName(CommonUtil.getGroupNameByUser(groupMembersInfos));
                    groupName = gsonBuilder.getGroupNameModelJson(groupNameModel);
                    groupEntity.setGroupName(groupName);
                }
            }

            groupDataSource.insertOrUpdateGroup(groupEntity);
            setGroupInfo(userId, groupEntity.getGroupId(), Constants.GroupEventMessageBody.LEAVE,
                    0, Constants.MessageType.GROUP_LEAVE, groupModel.getInfoId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    private void sendGroupInfoChangeEvent(GroupModel groupModel) {
        compositeDisposable.add(Single.fromCallable(() -> sendGroupInfoChange(groupModel))
                .subscribeOn(Schedulers.newThread())
                .subscribe(result -> {
                    Timber.d("Result: %s", result);
                }, Throwable::printStackTrace));
    }

    private long sendGroupInfoChange(GroupModel groupModel) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        String groupId = groupModel.getGroupId();
        String newName = groupModel.getGroupName();

        GroupEntity groupEntity = groupDataSource.getGroupById(groupId);

        GroupNameModel groupNameModel = gsonBuilder.getGroupNameModelObj(groupEntity.getGroupName());
        groupNameModel.setGroupName(newName).setGroupNameChanged(true);

        String groupNameModelText = gsonBuilder.getGroupNameModelJson(groupNameModel);
        groupEntity.setGroupName(groupNameModelText);

        long result = groupDataSource.insertOrUpdateGroup(groupEntity);

        String groupNameText = gsonBuilder.getGroupModelJson(groupModel);

        ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());

        sendDataToAllMembers(groupNameText, Constants.DataType.EVENT_GROUP_RENAME,
                groupEntity.getAdminInfo(), groupMembersInfos);

        setGroupInfo(getMyMeshId(), groupId, Constants.GroupEventMessageBody.RENAMED + " " + newName,
                0, Constants.MessageType.GROUP_RENAMED, groupModel.getInfoId());

        return result;
    }

    private void receiveGroupInfoChangeEvent(byte[] rawData, String userId) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        String groupModelText = new String(rawData);
        GroupModel groupModel = gsonBuilder.getGroupModelObj(groupModelText);

        GroupEntity groupEntity = groupDataSource.getGroupById(groupModel.getGroupId());

        if (groupEntity == null) return;

        GroupNameModel groupNameModel = gsonBuilder.getGroupNameModelObj(groupEntity.getGroupName());
        groupNameModel.setGroupName(groupModel.getGroupName()).setGroupNameChanged(true);

        String groupNameModelText = gsonBuilder.getGroupNameModelJson(groupNameModel);
        groupEntity.setGroupName(groupNameModelText);

        groupDataSource.insertOrUpdateGroup(groupEntity);
        setGroupInfo(userId, groupModel.getGroupId(),
                Constants.GroupEventMessageBody.RENAMED + " " + groupModel.getGroupName(),
                0, Constants.MessageType.GROUP_RENAMED, groupModel.getInfoId());
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    public void updateGroupUserInfo(UserEntity userEntity) {
        String updatedUserId = "%" + userEntity.getMeshId() + "%";
        List<GroupEntity> groupEntities = groupDataSource.getGroupByUserId(updatedUserId);

        for (GroupEntity groupEntity : groupEntities) {
            setGroupInfoUpdate(groupEntity, userEntity.getUserName(), userEntity.getUserLastName(),
                    userEntity.getMeshId(), userEntity.getAvatarIndex());
        }
    }

    public void updateMyUserInfo() {
        List<GroupEntity> groupEntities = groupDataSource.getAllGroup();
        String myNewName = SharedPref.read(Constants.preferenceKey.USER_NAME);
        String myLastName = SharedPref.read(Constants.preferenceKey.LAST_NAME);
        int avatarIndex = SharedPref.readInt(Constants.preferenceKey.IMAGE_INDEX);
        String myMeshId = getMyMeshId();

        for (GroupEntity groupEntity : groupEntities) {
            setGroupInfoUpdate(groupEntity, myNewName, myLastName, myMeshId, avatarIndex);
        }
    }

    private void setGroupInfoUpdate(GroupEntity groupEntity, String updatedName, String lastName, String userId, int avatarIndex) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());

        for (int i = 0; i < groupMembersInfos.size(); i++) {
            GroupMembersInfo groupMembersInfo = groupMembersInfos.get(i);

            if (groupMembersInfo.getMemberId().equals(userId)) {
                groupMembersInfo.setUserName(updatedName);
                groupMembersInfo.setLastName(lastName);
                groupMembersInfo.setAvatarPicture(avatarIndex);
                groupMembersInfos.set(i, groupMembersInfo);
            }
        }

        String groupMemberInfosText = gsonBuilder.getGroupMemberInfoJson(groupMembersInfos);
        groupEntity.setMembersInfo(groupMemberInfosText);

        String groupNameText = groupEntity.getGroupName();
        if (!TextUtils.isEmpty(groupNameText)) {
            GroupNameModel groupNameModel = gsonBuilder.getGroupNameModelObj(groupNameText);

            if (!groupNameModel.isGroupNameChanged()) {
                groupNameModel.setGroupName(CommonUtil.getGroupNameByUser(groupMembersInfos));
                groupNameText = gsonBuilder.getGroupNameModelJson(groupNameModel);
                groupEntity.setGroupName(groupNameText);
            }
        }
        groupDataSource.insertOrUpdateGroup(groupEntity);
    }

    public Single<Boolean> syncUserUpdateInfoForUnDiscovered(String data, byte type) {
        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {
                try {
                    emitter.onSuccess(syncUpdateInfoForUnDiscovered(data, type));
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }

    private boolean syncUpdateInfoForUnDiscovered(String data, byte type) {
        List<String> userIds = UserDataSource.getInstance().getAllUnDiscoveredUsers(getMyMeshId());

        for (String unDisCoverUserId : userIds) {
            sendMyInfoToUndiscovered(unDisCoverUserId, data, type);
        }
        return true;
    }

    private void sendMyInfoToUndiscovered(String userId, String data, byte type) {
        String updatedUserId = "%" + userId + "%";
        String adminId = groupDataSource.getGroupAdminByUserId(updatedUserId);

        List<String> liveMembersId = new ArrayList<>();
        liveMembersId.add(userId);

        RelayGroupModel relayGroupModel = new RelayGroupModel()
                .setData(data).setType(type).setUsers(liveMembersId);

        String relayGroupText = GsonBuilder.getInstance().getRelayGroupModelJson(relayGroupModel);
        dataSend(relayGroupText.getBytes(), Constants.DataType.EVENT_GROUP_DATA_RELAY,
                adminId, false);

    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    private void setGroupInfo(String userId, String groupId, String message, long time,
                              int type, String messageId) {
        if (TextUtils.isEmpty(messageId)) {
            messageId = UUID.randomUUID().toString();
        }

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

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    private void addNewMemberOperation(GroupMemberChangeModel groupMemberChangeModel) {
        compositeDisposable.add(Single.fromCallable(() -> sendNewMemberAddEvent(groupMemberChangeModel))
                .subscribeOn(Schedulers.newThread())
                .subscribe(result -> {
                    Timber.d("Result: %s", result);
                }, Throwable::printStackTrace));
    }

    /**
     * It is responsible for two things
     * 1. For sending updated group information including new added members
     * 2. Send group join request to new added members.
     * <p>
     * There are two different events.
     *
     * @param groupMemberChangeModel {@link GroupMemberChangeModel} It is contain the Update group
     *                               information and list of new added member
     */
    private boolean sendNewMemberAddEvent(GroupMemberChangeModel groupMemberChangeModel) {

        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        GroupEntity groupEntity = groupMemberChangeModel.groupEntity;
        ArrayList<GroupMembersInfo> newMembersInfos = groupMemberChangeModel.newMembersInfo;
        String infoId = groupMemberChangeModel.infoId;

        List<String> newAddedUserIdList = CommonUtil.getGroupMembersId(newMembersInfos);

        GroupModel groupModelForNewMembers = groupEntity.toGroupModel().setInfoId(infoId);
        String groupModelTextForNewMembers = gsonBuilder.getGroupModelJson(groupModelForNewMembers);

        sendDataToAllMembers(groupModelTextForNewMembers, Constants.DataType.EVENT_GROUP_CREATION,
                groupEntity.getAdminInfo(), newMembersInfos);


        ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());
        String newMembersInfoList = gsonBuilder.getGroupMemberInfoJson(newMembersInfos);

        GroupModel groupModelForExistingUsers = new GroupModel()
                .setGroupId(groupEntity.getGroupId())
                .setMemberInfo(newMembersInfoList)
                .setInfoId(infoId);
        String groupModelTextForExistingMembers = gsonBuilder.getGroupModelJson(groupModelForExistingUsers);

        if (groupMembersInfos != null) {
            ArrayList<GroupMembersInfo> existingMembers = new ArrayList<>();
            for (GroupMembersInfo groupMembersInfo : groupMembersInfos) {

                String userId = groupMembersInfo.getMemberId();
                if (!newAddedUserIdList.contains(userId)) {
                    existingMembers.add(groupMembersInfo);
                }
            }

            sendDataToAllMembers(groupModelTextForExistingMembers, Constants.DataType.EVENT_GROUP_MEMBER_ADD,
                    groupEntity.getAdminInfo(), existingMembers);
        }

        // add self message that I added a user

        for (int i = 0; i < newMembersInfos.size(); i++) {
            GroupMembersInfo groupMembersInfo = newMembersInfos.get(i);
            setGroupInfo(getMyMeshId(), groupEntity.groupId, Constants.GroupEventMessageBody.MEMBER_ADD
                            + " " + groupMembersInfo.getUserName(),
                    0, Constants.MessageType.GROUP_MEMBER_ADD, (infoId + "" + i));
        }
        return true;
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
            ArrayList<GroupMembersInfo> newGroupMembersInfos = gsonBuilder
                    .getGroupMemberInfoObj(groupModel.getMemberInfo());

            if (groupEntity == null) {

                groupEntity = new GroupEntity()
                        .setGroupId(groupModel.getGroupId())
                        .setMembersInfo(groupModel.getMemberInfo());
            } else {

                String existingGroupMembersInfoText = groupEntity.getMembersInfo();

                ArrayList<GroupMembersInfo> existingMembersInfos = gsonBuilder
                        .getGroupMemberInfoObj(existingGroupMembersInfoText);

                ArrayList<GroupMembersInfo> groupMembersInfos = CommonUtil
                        .mergeGroupMembersInfo(existingMembersInfos, newGroupMembersInfos);

                existingGroupMembersInfoText = gsonBuilder.getGroupMemberInfoJson(groupMembersInfos);
                groupEntity.setMembersInfo(existingGroupMembersInfoText);

                String groupNameModelText = groupEntity.getGroupName();
                if (!TextUtils.isEmpty(groupNameModelText)) {

                    GroupNameModel groupNameModel = gsonBuilder.getGroupNameModelObj(groupNameModelText);

                    if (!groupNameModel.isGroupNameChanged()) {
                        // We have to change group name
                        groupNameModel.setGroupName(CommonUtil.getGroupNameByUser(groupMembersInfos));
                        groupNameModelText = gsonBuilder.getGroupNameModelJson(groupNameModel);
                        groupEntity.setGroupName(groupNameModelText);
                    }
                }
            }

            groupDataSource.insertOrUpdateGroup(groupEntity);

            for (int i = 0; i < newGroupMembersInfos.size(); i++) {
                GroupMembersInfo groupMembersInfo = newGroupMembersInfos.get(i);
                setGroupInfo(userId, groupEntity.groupId, Constants.GroupEventMessageBody.MEMBER_ADD
                                + " " + groupMembersInfo.getUserName(),
                        0, Constants.MessageType.GROUP_MEMBER_ADD, (groupModel.getInfoId() + "" + i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    private void removeMemberOperation(GroupMemberChangeModel groupMemberChangeModel) {
        compositeDisposable.add(Single.fromCallable(() -> sendRemoveMemberEvent(groupMemberChangeModel))
                .subscribeOn(Schedulers.newThread())
                .subscribe(result -> {
                    Timber.d("Result: %s", result);
                }, Throwable::printStackTrace));
    }

    /**
     * This method is responsible for sending other group member that
     * a member is removed from the group
     * <p>
     * And this method is also notify the removed user
     *
     * @param groupMemberChangeModel
     */
    private boolean sendRemoveMemberEvent(GroupMemberChangeModel groupMemberChangeModel) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        GroupEntity groupEntity = groupMemberChangeModel.groupEntity;
        UserEntity removedUser = groupMemberChangeModel.removeUser;
        String infoId = groupMemberChangeModel.infoId;

        ArrayList<GroupMembersInfo> removeMemberInfos = new ArrayList<>();
        GroupMembersInfo removeMembersInfo = new GroupMembersInfo().setMemberId(removedUser.getMeshId());
        removeMemberInfos.add(removeMembersInfo);

        String removedMemberInfoText = gsonBuilder.getGroupMemberInfoJson(removeMemberInfos);

        GroupModel groupModel = new GroupModel()
                .setGroupId(groupEntity.getGroupId())
                .setMemberInfo(removedMemberInfoText)
                .setInfoId(infoId);

        String groupModelText = GsonBuilder.getInstance().getGroupModelJson(groupModel);

        ArrayList<GroupMembersInfo> groupMembersInfos = gsonBuilder
                .getGroupMemberInfoObj(groupEntity.getMembersInfo());

        sendDataToAllMembers(groupModelText, Constants.DataType.EVENT_GROUP_MEMBER_REMOVE,
                groupEntity.getAdminInfo(), groupMembersInfos);

        dataSend(groupModelText.getBytes(), Constants.DataType.EVENT_GROUP_MEMBER_REMOVE,
                removedUser.getMeshId(), false);

        // Send self message that i removed a user
        setGroupInfo(getMyMeshId(), groupEntity.groupId, Constants.GroupEventMessageBody.MEMBER_REMOVED
                        + " " + removedUser.getUserName(),
                0, Constants.MessageType.GROUP_MEMBER_REMOVE, groupModel.getInfoId());

        return true;
    }

    private void receiveGroupMemberRemoveEvent(byte[] rawData, String userId) {
        try {
            GsonBuilder gsonBuilder = GsonBuilder.getInstance();

            String groupModelText = new String(rawData);
            GroupModel groupModel = gsonBuilder.getGroupModelObj(groupModelText);

            GroupMembersInfo removedUser = gsonBuilder.getGroupMemberInfoObj(groupModel.getMemberInfo()).get(0);

            GroupMembersInfo removeGroupMembersInfo = null;

            if (removedUser.getMemberId().equals(getMyMeshId())) {
                // This is the removed user section
                groupDataSource.deleteGroupById(groupModel.getGroupId());
            } else {
                //This is group's other member section
                GroupEntity groupEntity = groupDataSource.getGroupById(groupModel.getGroupId());
                ArrayList<GroupMembersInfo> groupMembersInfos;

                if (groupEntity == null) {
                    //That mean this user did not get group create information

                    groupEntity = new GroupEntity().setGroupId(groupModel.getGroupId());

                    groupMembersInfos = new ArrayList<>();
                    GroupMembersInfo groupMembersInfo = new GroupMembersInfo()
                            .setMemberId(removedUser.getMemberId())
                            .setMemberStatus(Constants.GroupEvent.GROUP_LEAVE);
                    groupMembersInfos.add(groupMembersInfo);

                    String groupMemberInfoText = gsonBuilder.getGroupMemberInfoJson(groupMembersInfos);
                    groupEntity.setMembersInfo(groupMemberInfoText);
                } else {
                    String groupMemberInfoText = groupEntity.getMembersInfo();
                    groupMembersInfos = gsonBuilder.getGroupMemberInfoObj(groupMemberInfoText);

                    boolean isMemberLeaved = false;

                    for (int i = 0; i < groupMembersInfos.size(); i++) {
                        GroupMembersInfo groupMembersInfo = groupMembersInfos.get(i);

                        if (groupMembersInfo.getMemberId().equals(removedUser.getMemberId())) {

                            removeGroupMembersInfo = groupMembersInfo;

                            groupMembersInfo.setMemberStatus(Constants.GroupEvent.GROUP_LEAVE);
                            groupMembersInfos.set(i, groupMembersInfo);
                            isMemberLeaved = true;
                        }
                    }

                    if (!isMemberLeaved) {
                        GroupMembersInfo groupMembersInfo = new GroupMembersInfo()
                                .setMemberId(removedUser.getMemberId())
                                .setMemberStatus(Constants.GroupEvent.GROUP_LEAVE);
                        groupMembersInfos.add(groupMembersInfo);
                    }

                    groupMemberInfoText = gsonBuilder.getGroupMemberInfoJson(groupMembersInfos);
                    groupEntity.setMembersInfo(groupMemberInfoText);
                }

                String groupName = groupEntity.getGroupName();

                if (!TextUtils.isEmpty(groupName)) {
                    GroupNameModel groupNameModel = gsonBuilder.getGroupNameModelObj(groupName);

                    if (!groupNameModel.isGroupNameChanged()) {
                        groupNameModel.setGroupName(CommonUtil.getGroupNameByUser(groupMembersInfos));
                        groupName = gsonBuilder.getGroupNameModelJson(groupNameModel);
                        groupEntity.setGroupName(groupName);
                    }
                }

                groupDataSource.insertOrUpdateGroup(groupEntity);
            }

            if (removeGroupMembersInfo != null) {
                String removedUseName = removeGroupMembersInfo.getUserName();
                if (!TextUtils.isEmpty(removedUseName)) {
                    setGroupInfo(userId, groupModel.getGroupId(),
                            Constants.GroupEventMessageBody.MEMBER_REMOVED + " " + removedUseName,
                            0, Constants.MessageType.GROUP_MEMBER_REMOVE, groupModel.getInfoId());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    private void receiveTheRelayMessage(byte[] rawData, String userId) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        String relayGroupModelText = new String(rawData);
        RelayGroupModel relayGroupModel = gsonBuilder.getRelayGroupModelObj(relayGroupModelText);

        ForwardGroupModel forwardGroupModel = new ForwardGroupModel()
                .setData(relayGroupModel.getData()).setSender(userId)
                .setType(relayGroupModel.getType());

        String forwardGroupModelText = gsonBuilder.getForwarderGroupModelJson(forwardGroupModel);

        for (String receiverId : relayGroupModel.getUsers()) {
            dataSend(forwardGroupModelText.getBytes(), Constants.DataType.EVENT_GROUP_DATA_FORWARD,
                    receiverId, false);
        }
    }

    private void receiveForwardMessage(byte[] rawData, String userId) {
        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        String forwardGroupModelText = new String(rawData);
        ForwardGroupModel forwardGroupModel = gsonBuilder.getForwarderGroupModelObj(forwardGroupModelText);

        onDemandUserAdd(forwardGroupModel.getSender());

        DataModel dataModel = new DataModel()
                .setUserId(forwardGroupModel.getSender())
                .setRawData(forwardGroupModel.getData().getBytes())
                .setDataType(forwardGroupModel.getType());

        dataReceive(dataModel, true);
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    private void sendDataToAllMembers(String data, byte type, String adminId,
                                      ArrayList<GroupMembersInfo> groupMembersInfos) {

        GsonBuilder gsonBuilder = GsonBuilder.getInstance();

        if (groupMembersInfos != null) {

            List<String> liveMembersId = CommonUtil.getGroupLiveMembersId(groupMembersInfos);
            liveMembersId.remove(getMyMeshId());

            List<UserEntity> availableUsers = UserDataSource.getInstance()
                    .getLiveGroupMembers(liveMembersId);

            if (liveMembersId.size() == availableUsers.size()) {

                for (String userId : liveMembersId) {
                    dataSend(data.getBytes(), type, userId, (type == Constants.DataType.MESSAGE));
                }

            } else {

                for (UserEntity userEntity : availableUsers) {
                    String userId = userEntity.getMeshId();
                    if (!userId.equals(getMyMeshId())) {
                        dataSend(data.getBytes(), type, userId, (type == Constants.DataType.MESSAGE));
                        liveMembersId.remove(userId);
                    }
                }

                if (liveMembersId.size() > 0) {
                    // TODO we have lots of work in here for getting the invitor person for relay message
                    RelayGroupModel relayGroupModel = new RelayGroupModel()
                            .setData(data).setType(type).setUsers(liveMembersId);

                    String relayGroupText = gsonBuilder.getRelayGroupModelJson(relayGroupModel);
                    dataSend(relayGroupText.getBytes(), Constants.DataType.EVENT_GROUP_DATA_RELAY,
                            adminId, false);
                }
            }
        }
    }
}
