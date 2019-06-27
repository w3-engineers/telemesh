
package com.w3engineers.unicef.telemesh.data.local.feed;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BroadcastCommand {

    @SerializedName("event")
    @Expose
    private String event;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("clientId")
    @Expose
    private String clientId;
    @SerializedName("baseStationId")
    @Expose
    private String baseStationId;
    @SerializedName("payload")
    @Expose
    private Payload payload;

    @Nullable
    public String getEvent() {
        return event;
    }

    @NonNull
    public BroadcastCommand setEvent(@Nullable String event) {
        this.event = event;
        return this;
    }

    @Nullable
    public String getToken() {
        return token;
    }

    @NonNull
    public BroadcastCommand setToken(@Nullable String token) {
        this.token = token;
        return this;
    }

    @Nullable
    public String getClientId() {
        return clientId;
    }

    @NonNull
    public BroadcastCommand setClientId(@Nullable String clientId) {
        this.clientId = clientId;
        return this;
    }

    @Nullable
    public String getBaseStationId() {
        return baseStationId;
    }

    @NonNull
    public BroadcastCommand setBaseStationId(@Nullable String baseStationId) {
        this.baseStationId = baseStationId;
        return this;
    }

    @Nullable
    public Payload getPayload() {
        return payload;
    }

    @NonNull
    public BroadcastCommand setPayload(@Nullable Payload payload) {
        this.payload = payload;
        return this;
    }

}
