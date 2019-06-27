
package com.w3engineers.unicef.telemesh.data.local.feed;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payload {

    @SerializedName("messageId")
    @Expose
    private String messageId;

    @Nullable
    public String getMessageId() {
        return messageId;
    }

    @Nullable
    public Payload setMessageId(@Nullable String messageId) {
        this.messageId = messageId;
        return this;
    }

}
