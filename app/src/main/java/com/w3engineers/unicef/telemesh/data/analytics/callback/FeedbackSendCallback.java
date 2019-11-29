package com.w3engineers.unicef.telemesh.data.analytics.callback;

public interface FeedbackSendCallback {
    void onGetFeedbackSendResponse(boolean isSuccess, String userId, String feedbackId);
}
