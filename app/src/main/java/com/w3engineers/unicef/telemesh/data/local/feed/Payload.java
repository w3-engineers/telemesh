
package com.w3engineers.unicef.telemesh.data.local.feed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payload {

    @SerializedName("messageId")
    @Expose
    private String messageId;

    public String getMessageId() {
        return messageId;
    }

    public Payload setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

}
