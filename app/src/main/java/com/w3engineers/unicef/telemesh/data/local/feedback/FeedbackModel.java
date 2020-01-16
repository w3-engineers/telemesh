package com.w3engineers.unicef.telemesh.data.local.feedback;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class FeedbackModel {
    private String fi, ui, un, f;

    public String getFeedbackId() {
        return fi;
    }

    public void setFeedbackId(String fi) {
        this.fi = fi;
    }

    public String getUserId() {
        return ui;
    }

    public void setUserId(String ui) {
        this.ui = ui;
    }

    public String getUserName() {
        return un;
    }

    public void setUserName(String un) {
        this.un = un;
    }

    public String getFeedback() {
        return f;
    }

    public void setFeedback(String f) {
        this.f = f;
    }
}
