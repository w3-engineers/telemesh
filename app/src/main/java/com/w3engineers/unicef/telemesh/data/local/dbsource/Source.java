package com.w3engineers.unicef.telemesh.data.local.dbsource;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupEntity;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageDao;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDao;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class Source implements DataSource {

    private static Source dbSource = new Source();
    private String chatCurrentUser = null;
    final PublishSubject<Integer> myMode = PublishSubject.create();
    private MessageDao messageDao;
    private UserDao userDao;
    private BehaviorSubject<ChatEntity> failedMessage = BehaviorSubject.create();
    private BehaviorSubject<String> liveUserId = BehaviorSubject.create();
    private BehaviorSubject<Boolean> isMeshInitiated = BehaviorSubject.create();
    private BehaviorSubject<GroupEntity> groupUserEvent = BehaviorSubject.create();
    private BehaviorSubject<GroupModel> groupRenameEvent = BehaviorSubject.create();

    private Source() {
        messageDao = AppDatabase.getInstance().messageDao();
        userDao = AppDatabase.getInstance().userDao();
    }

    /**
     * This constructor is restricted and only used in unit test class
     *
     * @param appDatabase -> provide mock database from unit test class
     */
    public Source(@NonNull AppDatabase appDatabase) {
        messageDao = appDatabase.messageDao();
        userDao = appDatabase.userDao();
    }

    @NonNull
    public static Source getDbSource() {
        return dbSource;
    }

    @NonNull
    @Override
    public Flowable<ChatEntity> getLastChatData() {
        return MessageSourceData.getInstance().getLastData();
    }

    @Nullable
    @Override
    public String getCurrentUser() {
        return chatCurrentUser;
    }

    @Override
    public void setCurrentUser(@Nullable String currentUser) {
        this.chatCurrentUser = currentUser;
        if (currentUser != null && !TextUtils.isEmpty(currentUser)) {
            liveUserId.onNext(currentUser);
        }
    }

    @Override
    public void updateMessageStatus(@NonNull String messageId, int messageStatus) {
        messageDao.updateMessageStatus(messageId, messageStatus);
    }

    @Override
    public void reSendMessage(@NonNull ChatEntity chatEntity) {
        failedMessage.onNext(chatEntity);
    }

    @Override
    public Flowable<String> getLiveUserId() {
        return liveUserId.toFlowable(BackpressureStrategy.LATEST);
    }

    @Override
    public void setMeshInitiated(boolean isInitiated) {
        isMeshInitiated.onNext(true);
    }

    @Override
    public Flowable<Boolean> getMeshInitiated() {
        return isMeshInitiated.toFlowable(BackpressureStrategy.LATEST);
    }

    @Override
    public void setGroupUserEvent(GroupEntity groupEntity) {
        groupUserEvent.onNext(groupEntity);
    }

    @Nullable
    @Override
    public Flowable<GroupEntity> getGroupUserEvent() {
        return groupUserEvent.toFlowable(BackpressureStrategy.LATEST);
    }

    @Override
    public void setGroupRenameEvent(GroupModel groupModel) {
        groupRenameEvent.onNext(groupModel);
    }

    @Nullable
    @Override
    public Flowable<GroupModel> getGroupRenameEvent() {
        return groupRenameEvent.toFlowable(BackpressureStrategy.LATEST);
    }

    @Override
    @Nullable
    public Flowable<ChatEntity> getReSendMessage() {
        return failedMessage.toFlowable(BackpressureStrategy.LATEST);
    }

    // TODO purpose -> didn't set any mood when user switch the user mood (This was pause during ipc attached)
    /*@Override
    public void setMyMode(int mode) {
        this.myMode.onNext(mode);
    }

    @Override
    public Observable<Integer> getMyMode() {
        return this.myMode;
    }*/
}
