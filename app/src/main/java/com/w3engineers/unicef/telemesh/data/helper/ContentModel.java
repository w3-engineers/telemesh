package com.w3engineers.unicef.telemesh.data.helper;

public class ContentModel {

    private String userId, thumbPath, contentPath, messageId; // Thumb Only applicable for message
    private int messageType, ackStatus; // Only applicable for message
    private boolean isThumbSend;
    private byte contentDataType;

    public String getThumbPath() {
        return thumbPath;
    }

    public ContentModel setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
        return this;
    }

    public String getContentPath() {
        return contentPath;
    }

    public ContentModel setContentPath(String contentPath) {
        this.contentPath = contentPath;
        return this;
    }

    public String getMessageId() {
        return messageId;
    }

    public ContentModel setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public int getMessageType() {
        return messageType;
    }

    public ContentModel setMessageType(int messageType) {
        this.messageType = messageType;
        return this;
    }

    public boolean isThumbSend() {
        return isThumbSend;
    }

    public ContentModel setThumbSend(boolean thumbSend) {
        isThumbSend = thumbSend;
        return this;
    }

    public byte getContentDataType() {
        return contentDataType;
    }

    public ContentModel setContentDataType(byte contentDataType) {
        this.contentDataType = contentDataType;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public ContentModel setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public int getAckStatus() {
        return ackStatus;
    }

    public ContentModel setAckStatus(int ackStatus) {
        this.ackStatus = ackStatus;
        return this;
    }
}
