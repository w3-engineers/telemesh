package com.w3engineers.unicef.telemesh.data.analytics.callback;

import com.w3engineers.unicef.telemesh.data.analytics.model.FeedbackParseModel;

public interface FeedbackSendCallback {
    void onGetFeedbackSendResponse(boolean isSuccess, FeedbackParseModel model);
}
