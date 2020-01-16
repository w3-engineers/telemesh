package com.w3engineers.unicef.telemesh.data.analytics.model;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class FeedbackParseModel {
    private String userName;
    private String userId;
    private String feedback;
    private String feedbackId;
    private boolean isDirectSend;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public boolean isDirectSend() {
        return isDirectSend;
    }

    public void setDirectSend(boolean directSend) {
        isDirectSend = directSend;
    }
}
