package com.w3engineers.unicef.telemesh.data.local.survey;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import java.util.List;

import io.reactivex.Flowable;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class SurveyDataSource {

    private SurveyDao surveyDao;
    private static SurveyDataSource surveyDataSource = new SurveyDataSource();

    public SurveyDataSource() {
        surveyDao = AppDatabase.getInstance().surveyDao();
    }

    /*
     * This constructor is restricted and only used in unit test class
     * @param surveyDao -> provide dao from unit test class
     */
    public SurveyDataSource(@NonNull SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @NonNull
    public static SurveyDataSource getInstance() {
        return surveyDataSource;
    }

    public long insertOrUpdateData(@NonNull SurveyEntity surveyEntity) {
        return surveyDao.writeSurvey(surveyEntity);
    }

    @NonNull
    public Flowable<List<SurveyEntity>> getAllSurvey() {
        return surveyDao.getAllSurvey();
    }

    @Nullable
    public SurveyEntity getSurveyById(@NonNull String surveyId) {
        return surveyDao.getSurveyById(surveyId);
    }
}
