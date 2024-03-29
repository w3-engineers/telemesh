package com.w3engineers.unicef.telemesh.data.local.feedback;

import androidx.room.Dao;
import androidx.room.Query;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
import com.w3engineers.unicef.util.base.database.BaseDao;

/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

@Dao
public abstract class FeedbackDao extends BaseDao<FeedbackEntity> {

    @Query("SELECT * FROM " + TableNames.FEEDBACK + " ORDER BY " + ColumnNames.TIMESTAMP + " ASC LIMIT 1")
    abstract FeedbackEntity getFirstFeedback();

    @Query("SELECT * FROM " + TableNames.FEEDBACK + " WHERE " + ColumnNames.COLUMN_FEEDBACK_ID + " = :feedbackId")
    abstract FeedbackEntity getFeedbackById(String feedbackId);

    @Query("DELETE FROM " + TableNames.FEEDBACK + " WHERE " + ColumnNames.COLUMN_FEEDBACK_ID + " = :feedbackId")
    abstract int deleteFeedbackById(String feedbackId);
}
