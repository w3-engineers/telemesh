package com.w3engineers.unicef.telemesh.data.local.feed;

public class FeedContentModel {
    private String contentUrl, contentPath, contentThumb, contentInfo;

    public FeedContentModel setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
        return this;
    }

    public FeedContentModel setContentPath(String contentPath) {
        this.contentPath = contentPath;
        return this;
    }

    public FeedContentModel setContentThumb(String contentThumb) {
        this.contentThumb = contentThumb;
        return this;
    }

    public FeedContentModel setContentInfo(String contentInfo) {
        this.contentInfo = contentInfo;
        return this;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public String getContentPath() {
        return contentPath;
    }

    public String getContentThumb() {
        return contentThumb;
    }

    public String getContentInfo() {
        return contentInfo;
    }
}
