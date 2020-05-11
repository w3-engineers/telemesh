package com.w3engineers.unicef.telemesh.data.helper;

import com.w3engineers.models.ContentMetaInfo;

public class ContentPendingModel {
    private String senderId, contentId, contentPath;
    private int progress, state;
    private boolean isIncoming;
    private ContentMetaInfo contentMetaInfo;


    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String pathPath) {
        this.contentPath = pathPath;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ContentMetaInfo getContentMetaInfo() {
        return contentMetaInfo;
    }

    public void setContentMetaInfo(ContentMetaInfo contentMetaInfo) {
        this.contentMetaInfo = contentMetaInfo;
    }

    public boolean isIncoming() {
        return isIncoming;
    }

    public void setIncoming(boolean incoming) {
        isIncoming = incoming;
    }
}
