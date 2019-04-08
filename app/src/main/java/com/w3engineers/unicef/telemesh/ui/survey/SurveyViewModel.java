package com.w3engineers.unicef.telemesh.ui.survey;


import com.w3engineers.ext.strom.application.ui.base.BaseRxViewModel;
import com.w3engineers.unicef.telemesh.data.local.survey.SurveyDataSource;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class SurveyViewModel extends BaseRxViewModel {

    public SurveyViewModel() {
        super();

        SurveyDataSource surveyDataSource = SurveyDataSource.getInstance();
    }

}
