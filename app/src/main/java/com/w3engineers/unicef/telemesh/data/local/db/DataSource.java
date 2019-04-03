package com.w3engineers.unicef.telemesh.data.local.db;

import android.support.annotation.NonNull;

import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.Flowable;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [22-Oct-2018 at 4:32 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [22-Oct-2018 at 4:32 PM].
 * * --> <Second Editor> on [22-Oct-2018 at 4:32 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [22-Oct-2018 at 4:32 PM].
 * * --> <Second Reviewer> on [22-Oct-2018 at 4:32 PM].
 * * ============================================================================
 **/
public interface DataSource {

    /**
     * Commonly used getLastData which is now inserted in DB
     * @return
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
