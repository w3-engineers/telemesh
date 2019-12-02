package com.w3engineers.unicef.telemesh.data.local.feedback;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@Entity(tableName = TableNames.FEEDBACK)
public class FeedbackEntity {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = ColumnNames.COLUMN_FEED_ID)
    private String feedbackId;

    @ColumnInfo(name = ColumnNames.COLUMN_FEEDBACK)
    private String feedback;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_ID)
    private String userId;

    @ColumnInfo(name = ColumnNames.COLUMN_USER_NAME)
    private String userName;

    @ColumnInfo(name = ColumnNames.TIMESTAMP)
    private long timeStamp;

    @NonNull
    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(@NonNull String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public FeedbackModel toFeedbackModel() {
        FeedbackModel model = new FeedbackModel();
        model.setUserId(userId);
        model.setUserName(userName);
        model.setFeedback(feedback);
        model.setFeedbackId(feedbackId);
        return model;
    }

    public static FeedbackEntity toFeedbackEntity(FeedbackModel model) {
        FeedbackEntity entity = new FeedbackEntity();
        entity.setUserId(entity.getUserId());
        entity.setUserName(entity.getUserName());
        entity.setFeedback(entity.getFeedback());
        entity.setFeedbackId(entity.getFeedbackId());
        return entity;
    }
}
