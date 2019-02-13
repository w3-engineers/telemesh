package com.w3engineers.unicef.telemesh.data.local.survey;

import android.annotation.SuppressLint;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.os.Parcel;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseEntity;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
import com.w3engineers.unicef.telemesh.data.local.db.DbBaseEntity;
import com.w3engineers.unicef.telemesh.data.local.db.TableNames;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/4/2018 at 10:49 AM.
 *  *
 *  * Purpose: prepare survey table
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/4/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */
@SuppressLint("ParcelCreator")
@Entity(tableName = TableNames.SURVEY,
        indices = {@Index(value = {ColumnNames.COLUMN_SURVEY_ID}, unique = true)},
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = ColumnNames.COLUMN_USER_MESH_ID,
                childColumns = ColumnNames.COLUMN_SENDER_ID))
public class SurveyEntity extends DbBaseEntity {

    /**
     * instance variable  and  table column
     *
     */

    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_TITLE)
    public String surveyTitle;

    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_ID)
    public String surveyId;

    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_FORM)
    public String surveyForm;

    @ColumnInfo(name = ColumnNames.COLUMN_SENDER_ID)
    public String senderId;

    @ColumnInfo(name = ColumnNames.COLUMN_START_TIME)
    public String surveyStartTime;

    @ColumnInfo(name = ColumnNames.COLUMN_END_TIME)
    public String surveyEndTime;

    @ColumnInfo(name = ColumnNames.COLUMN_IS_SUBMITTED)
    public boolean isSubmitted;

    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_ANS)
    public String surveyAnswer;

    @ColumnInfo(name = ColumnNames.COLUMN_VENDOR_NAME)
    public String vendorName;

    public String getSurveyTitle() {
        return surveyTitle;
    }

    public SurveyEntity setSurveyTitle(String surveyTitle) {
        this.surveyTitle = surveyTitle;
        return this;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public SurveyEntity setSurveyId(String surveyId) {
        this.surveyId = surveyId;
        return this;
    }

    public String getSurveyForm() {
        return surveyForm;
    }

    public SurveyEntity setSurveyForm(String surveyForm) {
        this.surveyForm = surveyForm;
        return this;
    }

    public String getSenderId() {
        return senderId;
    }

    public SurveyEntity setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public String getSurveyStartTime() {
        return surveyStartTime;
    }

    public SurveyEntity setSurveyStartTime(String surveyStartTime) {
        this.surveyStartTime = surveyStartTime;
        return this;
    }

    public String getSurveyEndTime() {
        return surveyEndTime;
    }

    public SurveyEntity setSurveyEndTime(String surveyEndTime) {
        this.surveyEndTime = surveyEndTime;
        return this;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public SurveyEntity setSubmitted(boolean submitted) {
        isSubmitted = submitted;
        return this;
    }

    public String getSurveyAnswer() {
        return surveyAnswer;
    }

    public SurveyEntity setSurveyAnswer(String surveyAnswer) {
        this.surveyAnswer = surveyAnswer;
        return this;
    }

    public String getVendorName() {
        return vendorName;
    }

    public SurveyEntity setVendorName(String vendorName) {
        this.vendorName = vendorName;
        return this;
    }
}

