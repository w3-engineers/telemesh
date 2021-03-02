package com.w3engineers.unicef.telemesh.data.local.feed;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BulletinFeed {

    @SerializedName("messageId")
    @Expose
    private String messageId;
    @SerializedName("messageBody")
    @Expose
    private String messageBody;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @Nullable
    public String getMessageId() {
        return messageId;
    }

    @NonNull
    public BulletinFeed setMessageId(@Nullable String messageId) {
        this.messageId = messageId;
        return this;
    }

    @Nullable
    public String getMessageBody() {
        return messageBody;
    }

    @NonNull
    public BulletinFeed setMessageBody(@Nullable String messageBody) {
        this.messageBody = messageBody;
        return this;
    }

    @Nullable
    public String getCreatedAt() {
        return createdAt;
    }

    @NonNull
    public BulletinFeed setCreatedAt(@Nullable String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

}