package com.w3engineers.unicef.telemesh.data.local.feed;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

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

    public String getMessageId() {
        return messageId;
    }

    public BulletinFeed setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public BulletinFeed setMessageBody(String messageBody) {
        this.messageBody = messageBody;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public BulletinFeed setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

}