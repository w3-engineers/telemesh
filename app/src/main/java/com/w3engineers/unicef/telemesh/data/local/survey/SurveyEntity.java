package com.w3engineers.unicef.telemesh.data.local.survey;

import android.annotation.SuppressLint;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_TITLE)
    public String surveyTitle;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_ID)
    public String surveyId;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_FORM)
    public String surveyForm;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_SENDER_ID)
    public String senderId;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_START_TIME)
    public String surveyStartTime;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_END_TIME)
    public String surveyEndTime;

    @ColumnInfo(name = ColumnNames.COLUMN_IS_SUBMITTED)
    public boolean isSubmitted;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_ANS)
    public String surveyAnswer;

    @Nullable
    @ColumnInfo(name = ColumnNames.COLUMN_VENDOR_NAME)
    public String vendorName;

    @Nullable
    public String getSurveyTitle() {
        return surveyTitle;
    }

    @NonNull
    public SurveyEntity setSurveyTitle(@NonNull String surveyTitle) {
        this.surveyTitle = surveyTitle;
        return this;
    }

    @Nullable
    public String getSurveyId() {
        return surveyId;
    }

    @NonNull
    public SurveyEntity setSurveyId(@NonNull String surveyId) {
        this.surveyId = surveyId;
        return this;
    }

    @Nullable
    public String getSurveyForm() {
        return surveyForm;
    }

    @NonNull
    public SurveyEntity setSurveyForm(@NonNull String surveyForm) {
        this.surveyForm = surveyForm;
        return this;
    }

    @Nullable
    public String getSenderId() {
        return senderId;
    }

    @NonNull
    public SurveyEntity setSenderId(@NonNull String senderId) {
        this.senderId = senderId;
        return this;
    }

    @Nullable
    public String getSurveyStartTime() {
        return surveyStartTime;
    }

    @NonNull
    public SurveyEntity setSurveyStartTime(@NonNull String surveyStartTime) {
        this.surveyStartTime = surveyStartTime;
        return this;
    }

    @Nullable
    public String getSurveyEndTime() {
        return surveyEndTime;
    }

    @NonNull
    public SurveyEntity setSurveyEndTime(@NonNull String surveyEndTime) {
        this.surveyEndTime = surveyEndTime;
        return this;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    @NonNull
    public SurveyEntity setSubmitted(boolean submitted) {
        isSubmitted = submitted;
        return this;
    }

    @Nullable
    public String getSurveyAnswer() {
        return surveyAnswer;
    }

    @NonNull
    public SurveyEntity setSurveyAnswer(@NonNull String surveyAnswer) {
        this.surveyAnswer = surveyAnswer;
        return this;
    }

    @Nullable
    public String getVendorName() {
        return vendorName;
    }

    @NonNull
    public SurveyEntity setVendorName(@NonNull String vendorName) {
        this.vendorName = vendorName;
        return this;
    }
}

