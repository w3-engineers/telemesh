package com.w3engineers.unicef.telemesh.data.local.dbsource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageDao;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDao;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
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
