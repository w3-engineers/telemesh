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

public class AckCommand {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("ack_msg_id")
    @Expose
    private String ackMsgId;
    @SerializedName("clientId")
    @Expose
    private String clientId;

    @NonNull
    public Integer getStatus() {
        return status;
    }

    public void setStatus(@NonNull Integer status) {
        this.status = status;
    }

    @Nullable
    public String getAckMsgId() {
        return ackMsgId;
    }

    public void setAckMsgId(@Nullable String ackMsgId) {
        this.ackMsgId = ackMsgId;
    }

    @Nullable
    public String getClientId() {
        return clientId;
    }

    public void setClientId(@Nullable String clientId) {
        this.clientId = clientId;
    }


}
