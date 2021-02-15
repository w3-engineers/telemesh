package com.w3engineers.unicef.telemesh.data.local.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupMemberChangeModel;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;

import io.reactivex.Flowable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public interface DataSource {

    /**
     * Commonly used getLastData which is now inserted in DB
     * @return -
     */
    @NonNull
    Flowable<ChatEntity> getLastChatData();

    @Nullable
    String getCurrentUser();

    void setCurrentUser(@Nullable String currentUser);

    void updateMessageStatus(@NonNull String messageId, int messageStatus);

    void reSendMessage(@NonNull ChatEntity chatEntity);

    @Nullable
    Flowable<ChatEntity> getReSendMessage();

    Flowable<String> getLiveUserId();

    void setMeshInitiated(boolean isInitiated);

    Flowable<Boolean> getMeshInitiated();

    void setGroupUserLeaveEvent(GroupEntity groupEntity);

    @Nullable
    Flowable<GroupEntity> getGroupUserLeaveEvent();

    void setGroupInfoChangeEvent(GroupModel groupModel);

    @Nullable
    Flowable<GroupModel> getGroupInfoChangeEvent();

    void setAddNewMemberEvent(GroupMemberChangeModel model);

    @Nullable
    Flowable<GroupMemberChangeModel> getGroupMembersAddEvent();

    void setGroupMemberRemoveEvent(GroupMemberChangeModel model);

    @Nullable
    Flowable<GroupMemberChangeModel> getGroupMemberRemoveEvent();


    // TODO purpose -> didn't set any mood when user switch the user mood (This was pause during ipc attached)
    //void setMyMode(int mode);

    //Observable<Integer> getMyMode();
}
