package com.w3engineers.unicef.telemesh.data.local.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    // TODO purpose -> didn't set any mood when user switch the user mood (This was pause during ipc attached)
    //void setMyMode(int mode);

    //Observable<Integer> getMyMode();
}
