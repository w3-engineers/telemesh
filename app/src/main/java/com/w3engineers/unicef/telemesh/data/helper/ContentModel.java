package com.w3engineers.unicef.telemesh.data.helper;

public class ContentModel {

    private String userId, thumbPath, contentPath, messageId, contentId, contentInfo; // Thumb Only applicable for message
    private int messageType, ackStatus, progress, receivingStatus; // Only applicable for message
    private boolean isThumbSend, receiveSuccessStatus, isResend, isRequestFromReceiver, isContent;
    private byte contentDataType;
    private boolean isGroupContent;
    private String groupId;
    private String originalSender;

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

    public boolean getReceiveSuccessStatus() {
        return receiveSuccessStatus;
    }

    public ContentModel setReceiveSuccessStatus(boolean receiveSuccessStatus) {
        this.receiveSuccessStatus = receiveSuccessStatus;
        return this;
    }

    public boolean isResendMessage() {
        return isResend;
    }

    public ContentModel setResendMessage(boolean resend) {
        isResend = resend;
        return this;
    }

    public String getContentId() {
        return contentId;
    }

    public ContentModel setContentId(String contentId) {
        this.contentId = contentId;
        return this;
    }

    public boolean isRequestFromReceiver() {
        return isRequestFromReceiver;
    }

    public ContentModel setRequestFromReceiver(boolean requestFromReceiver) {
        isRequestFromReceiver = requestFromReceiver;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public ContentModel setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public boolean isContent() {
        return isContent;
    }

    public ContentModel setContent(boolean content) {
        isContent = content;
        return this;
    }

    public String getContentInfo() {
        return contentInfo;
    }

    public ContentModel setContentInfo(String contentInfo) {
        this.contentInfo = contentInfo;
        return this;
    }

    public ContentModel setReceivingStatus(int receivingStatus) {
        this.receivingStatus = receivingStatus;
        return this;
    }

    public int getReceivingStatus() {
        return receivingStatus;
    }

    public boolean isGroupContent() {
        return isGroupContent;
    }

    public ContentModel setGroupContent(boolean groupContent) {
        isGroupContent = groupContent;
        return this;
    }

    public String getGroupId() {
        return groupId;
    }

    public ContentModel setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public ContentModel setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
        return this;
    }
}
