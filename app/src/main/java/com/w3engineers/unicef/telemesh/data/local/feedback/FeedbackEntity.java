package com.w3engineers.unicef.telemesh.data.local.feedback;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

@Entity(tableName = TableNames.FEEDBACK)
public class FeedbackEntity {

    @PrimaryKey
    @ColumnInfo(name = ColumnNames.COLUMN_FEED_ID)
    private String feedbackId;

    @ColumnInfo(name = ColumnNames.COLUMN_FEEDBACK)
    private String feedback;

    @ColumnInfo(name = ColumnNames.TIMESTAMP)
    private long timeStamp;

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
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
}
