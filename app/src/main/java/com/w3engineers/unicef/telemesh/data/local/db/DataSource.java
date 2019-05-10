package com.w3engineers.unicef.telemesh.data.local.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

//    long insertOrUpdateData(DbBaseEntity baseEntity);

//    void deleteAllData();

//    boolean getMessage(String friendsId, String messageId);

    @Nullable
    String getCurrentUser();

    void setCurrentUser(@Nullable String currentUser);

    void updateMessageStatus(@NonNull String messageId, int messageStatus);

}
