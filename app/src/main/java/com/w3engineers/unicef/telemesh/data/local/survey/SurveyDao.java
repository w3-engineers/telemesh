package com.w3engineers.unicef.telemesh.data.local.survey;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;
import io.reactivex.Flowable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
@Dao
public interface SurveyDao extends BaseDao<SurveyEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long writeSurvey(@NonNull SurveyEntity surveyEntity);
    /**
     * <h1>Retrieve all survey</h1>
     * <p>All types of survey submitted and non submitted</p>
     *
     * @return - SurveyEntity list
     */
    @NonNull
    @Query("SELECT * FROM " + TableNames.SURVEY)
    Flowable<List<SurveyEntity>> getAllSurvey();

    /**
     * <h1>Get specific survey by id</h1>
     *
     * @param surveyId: String
     * @return - SurveyEntity
     */
    @Nullable
    @Query("SELECT * FROM " + TableNames.SURVEY + " WHERE " + ColumnNames.COLUMN_SURVEY_ID + " LIKE :surveyId")
    SurveyEntity getSurveyById(@NonNull String surveyId);
}
