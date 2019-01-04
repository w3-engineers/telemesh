package com.w3engineers.unicef.telemesh.data.local.survey;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseEntity;
import com.w3engineers.unicef.telemesh.data.local.db.ColumnNames;
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

@Entity(tableName = TableNames.SURVEY,
        indices = {@Index(value = {ColumnNames.COLUMN_SURVEY_ID}, unique = true)},
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = ColumnNames.COLUMN_USER_MESH_ID,
                childColumns = ColumnNames.COLUMN_SENDER_ID)
)
public class SurveyEntity extends BaseEntity {

    /**
     * instance variable  and  table column
     *
     */

    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_TITLE)
    private String surveyTitle;

    @NonNull
    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_ID)
    private String surveyId;

    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_FORM)
    private String surveyForm;

    @ColumnInfo(name = ColumnNames.COLUMN_SENDER_ID)
    private String senderId;

    @ColumnInfo(name = ColumnNames.COLUMN_START_TIME)
    private String surveyStartTime;

    @ColumnInfo(name = ColumnNames.COLUMN_END_TIME)
    private String surveyEndTime;

    @ColumnInfo(name = ColumnNames.COLUMN_IS_SUBMITTED)
    private boolean isSubmitted;

    @ColumnInfo(name = ColumnNames.COLUMN_SURVEY_ANS)
    private String surveyAnswer;

    @ColumnInfo(name = ColumnNames.COLUMN_VENDOR_NAME)
    private String vendorName;

    public String getSurveyTitle() {
        return surveyTitle;
    }

    public void setSurveyTitle(String surveyTitle) {
        this.surveyTitle = surveyTitle;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyForm() {
        return surveyForm;
    }

    public void setSurveyForm(String surveyForm) {
        this.surveyForm = surveyForm;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSurveyStartTime() {
        return surveyStartTime;
    }

    public void setSurveyStartTime(String surveyStartTime) {
        this.surveyStartTime = surveyStartTime;
    }

    public String getSurveyEndTime() {
        return surveyEndTime;
    }

    public void setSurveyEndTime(String surveyEndTime) {
        this.surveyEndTime = surveyEndTime;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public void setSubmitted(boolean submitted) {
        isSubmitted = submitted;
    }

    public String getSurveyAnswer() {
        return surveyAnswer;
    }

    public void setSurveyAnswer(String surveyAnswer) {
        this.surveyAnswer = surveyAnswer;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public SurveyEntity(Parcel parcel) {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

    }

    public static final Creator<SurveyEntity> CREATOR = new Creator<SurveyEntity>() {
        @Override
        public SurveyEntity createFromParcel(Parcel source) {
            return new SurveyEntity(source);
        }

        @Override
        public SurveyEntity[] newArray(int size) {
            return new SurveyEntity[size];
        }
    };
}

