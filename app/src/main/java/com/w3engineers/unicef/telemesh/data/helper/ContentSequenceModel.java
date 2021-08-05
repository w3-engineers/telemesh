package com.w3engineers.unicef.telemesh.data.helper;

public class ContentSequenceModel {
    private String contentId;
    private boolean contentStatus;
    private int progress, receiveStatus;

    public String getContentId() {
        return contentId;
    }

    public ContentSequenceModel setContentId(String contentId) {
        this.contentId = contentId;
        return this;
    }

    public boolean isContentStatus() {
        return contentStatus;
    }

    public ContentSequenceModel setContentStatus(boolean contentStatus) {
        this.contentStatus = contentStatus;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public ContentSequenceModel setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public int getReceiveStatus() {
        return receiveStatus;
    }

    public ContentSequenceModel setReceiveStatus(int receiveStatus) {
        this.receiveStatus = receiveStatus;
        return this;
    }
}
