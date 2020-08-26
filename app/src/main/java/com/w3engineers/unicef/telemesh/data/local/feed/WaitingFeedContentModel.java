package com.w3engineers.unicef.telemesh.data.local.feed;

public class WaitingFeedContentModel {
    private String feedId, userId, requestLink, responseLink;

    public String getFeedId() {
        return feedId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRequestLink() {
        return requestLink;
    }

    public String getResponseLink() {
        return responseLink;
    }

    public WaitingFeedContentModel setFeedId(String feedId) {
        this.feedId = feedId;
        return this;
    }

    public WaitingFeedContentModel setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public WaitingFeedContentModel setRequestLink(String requestLink) {
        this.requestLink = requestLink;
        return this;
    }

    public WaitingFeedContentModel setResponseLink(String responseLink) {
        this.responseLink = responseLink;
        return this;
    }
}
