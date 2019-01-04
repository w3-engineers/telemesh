package com.w3engineers.unicef.telemesh.data.local.survey;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseDao;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;

import java.util.List;
import io.reactivex.Flowable;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/4/2018 at 11:28 AM.
 *  * Email : azizul@w3engineers.com
 *  *
 *  * Purpose: to access survey table
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/4/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */
@Dao
public interface SurveyDao extends BaseDao<SurveyEntity> {
    /**
     * <h1>Retrieve all survey</h1>
     * <p>All types of survey submitted and non submitted</p>
     *
     * @return: SurveyEntity list
     */
    @Query("SELECT * FROM " + TableNames.SURVEY)
    Flowable<List<SurveyEntity>> getAllSurvey();

    /**
     * <h1>Get specific survey by id</h1>
     *
     * @param surveyId: String
     * @return: SurveyEntity
     */
    @Query("SELECT * FROM " + TableNames.SURVEY + " WHERE " + ColumnNames.COLUMN_SENDER_ID + " LIKE :surveyId")
    SurveyEntity getSurveyById(String surveyId);
}
