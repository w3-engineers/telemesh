package com.w3engineers.unicef.telemesh.data.local.dbsource;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageDao;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDao;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class Source implements DataSource {

    private static Source dbSource = new Source();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String chatCurrentUser = null;

    private MessageDao messageDao;
    private UserDao userDao;

    private Source() {
        messageDao = AppDatabase.getInstance().messageDao();
        userDao = AppDatabase.getInstance().userDao();
    }

    /**
     * This constructor is restricted and only used in unit test class
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

    /*@Override
    public long insertOrUpdateData(DbBaseEntity baseEntity) {

        try {
            if (MessageEntity.class.isInstance(baseEntity)) {
                return messageDao.writeMessage((MessageEntity) baseEntity);

            } else if (UserEntity.class.isInstance(baseEntity)) {
                return userDao.insertOrUpdate((UserEntity) baseEntity);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1L;
    }

    @Override
    public void deleteAllData() {

    }

    @Override
    public boolean getMessage(String friendsId, String messageId) {
        return messageDao.hasChatEntityExist(friendsId, messageId);
    }*/

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
}
