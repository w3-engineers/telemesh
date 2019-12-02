package com.w3engineers.unicef.telemesh.data.local.feedback;

import android.arch.persistence.room.Query;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

public abstract class FeedbackDao extends BaseDao<FeedbackEntity> {

    @Query("SELECT * FROM " + TableNames.FEEDBACK + " ORDER BY " + ColumnNames.TIMESTAMP + " ASC LIMIT 1")
    abstract FeedbackEntity getFirstFeedback();

    @Query("SELECT * FROM " + TableNames.FEEDBACK + " WHERE " + ColumnNames.COLUMN_FEEDBACK_ID + " = :feedbackId")
    abstract FeedbackEntity getFeedbackById(String feedbackId);

    @Query("DELETE FROM " + TableNames.FEEDBACK + " WHERE " + ColumnNames.COLUMN_FEEDBACK_ID + " = :feedbackId")
    abstract int deleteFeedbackById(String feedbackId);
}
