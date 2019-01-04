package com.w3engineers.unicef.telemesh.data.local.dbsource;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.db.DbBaseEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageDao;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDao;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [24-Oct-2018 at 12:23 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [24-Oct-2018 at 12:23 PM].
 * * --> <Second Editor> on [24-Oct-2018 at 12:23 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [24-Oct-2018 at 12:23 PM].
 * * --> <Second Reviewer> on [24-Oct-2018 at 12:23 PM].
 * * ============================================================================
 **/
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

    public static Source getDbSource() {
        return dbSource;
    }

    @Override
    public Flowable<ChatEntity> getLastChatData() {
        return MessageSourceData.getInstance().getLastData();
    }

    @Override
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
    }

    @Override
    public String getCurrentUser() {
        return chatCurrentUser;
    }

    @Override
    public void setCurrentUser(String currentUser) {
        this.chatCurrentUser = currentUser;
    }
}
