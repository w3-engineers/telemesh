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

    @SerializedName("uploaderInfo")
    @Expose
    private String uploaderInfo;
    @SerializedName("messageType")
    @Expose
    private long messageType;
    @SerializedName("messageTitle")
    @Expose
    private String messageTitle;
    @SerializedName("fileName")
    @Expose
    private String fileName;
    @SerializedName("messageId")
    @Expose
    private String messageId;
    @SerializedName("messageBody")
    @Expose
    private String messageBody;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("expiredAt")
    @Expose
    private String expiredAt;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;

    @SerializedName("range")
    @Expose
    private double range;

    @SerializedName("broadcastAddress")
    @Expose
    private String broadcastAddress;

    public String getUploaderInfo() {
        return uploaderInfo;
    }

    public BulletinFeed setUploaderInfo(String uploaderInfo) {
        this.uploaderInfo = uploaderInfo;
        return this;
    }

    public long getMessageType() {
        return messageType;
    }

    public BulletinFeed setMessageType(long messageType) {
        this.messageType = messageType;
        return this;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public BulletinFeed setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public BulletinFeed setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

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

    public String getExpiredAt() {
        return expiredAt;
    }

    public BulletinFeed setExpiredAt(String expiredAt) {
        this.expiredAt = expiredAt;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public BulletinFeed setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public BulletinFeed setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getRange() {
        return range;
    }

    public BulletinFeed setRange(double range) {
        this.range = range;
        return this;
    }

    public String getBroadcastAddress() {
        return broadcastAddress;
    }

    public BulletinFeed setBroadcastAddress(String broadcastAddress) {
        this.broadcastAddress = broadcastAddress;
        return this;
    }
}