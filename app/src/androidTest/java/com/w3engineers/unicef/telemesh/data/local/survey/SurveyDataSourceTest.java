package com.w3engineers.unicef.telemesh.data.local.survey;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

import io.reactivex.subscribers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
@RunWith(AndroidJUnit4.class)
public class SurveyDataSourceTest {

    private AppDatabase appDatabase;
    private UserDataSource userDataSource;
    private SurveyDataSource SUT;

    @Before
    public void setUp() {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = new UserDataSource(appDatabase.userDao());

        SUT = new SurveyDataSource(appDatabase.surveyDao());
    }

    @After
    public void tearDown() {
        appDatabase.close();
    }

    @Test
    public void basicSurveyTest() {

        String userId = UUID.randomUUID().toString();

        userDataSource.insertOrUpdateData(getUserInfo(userId));

        addDelay();

        String surveyId_1 = UUID.randomUUID().toString();
        SUT.insertOrUpdateData(getSurveyInfo(surveyId_1, userId));

        String surveyId_2 = UUID.randomUUID().toString();
        SUT.insertOrUpdateData(getSurveyInfo(surveyId_2, userId));

        SurveyEntity surveyEntity = SUT.getSurveyById(surveyId_1);

        if (surveyEntity == null) return;

        assertEquals(surveyEntity.getSenderId(), userId);

        String surveyForm = surveyEntity.getSurveyForm();
        String surveyStart = surveyEntity.getSurveyStartTime();
        String surveyEnd = surveyEntity.getSurveyEndTime();
        String surveyAnswer = surveyEntity.getSurveyAnswer();
        String surveyVendor = surveyEntity.getVendorName();

        String surveyTitle = surveyEntity.getSurveyTitle();
        String surveyId = surveyEntity.getSurveyId();

        boolean isSubmitted = surveyEntity.isSubmitted();

        assertNotNull(surveyForm);
        assertNotNull(surveyStart);
        assertNotNull(surveyEnd);
        assertNotNull(surveyAnswer);
        assertNotNull(surveyVendor);
        assertNotNull(surveyTitle);
        assertNotNull(surveyId);

        assertFalse(isSubmitted);

        TestSubscriber<List<SurveyEntity>> getAllSurveySubscriber = SUT.getAllSurvey().test();

        addDelay();

        getAllSurveySubscriber.assertNoErrors().assertValue(surveyEntities -> surveyEntities.size() == 2);
    }

    private void addDelay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private UserEntity getUserInfo(String meshId) {

        String firstName = "Daniel";
        String lastName = "Alvez";

        return new UserEntity()
                .setMeshId(meshId)
                .setCustomId(UUID.randomUUID().toString())
                .setUserFirstName(firstName)
                .setUserLastName(lastName)
                .setAvatarIndex(3)
                .setLastOnlineTime(System.currentTimeMillis())
                .setOnline(true);
    }

    private SurveyEntity getSurveyInfo(String surveyId, String senderId) {
        String surveyTitle = "surveyTitle";
        String surveyFrom = "surveyFrom";
        String surveyStartTime = "1-Feb-2019";
        String surveyEndTime = "1-Mar-2019";
        String surveyAnswer = "Everything are going well.";
        String vendorName = "Daniel Alvez";

        boolean isSubmitted = false;

        return new SurveyEntity()
                .setSurveyTitle(surveyTitle)
                .setSurveyId(surveyId)
                .setSurveyForm(surveyFrom)
                .setSenderId(senderId)
                .setSurveyStartTime(surveyStartTime)
                .setSurveyEndTime(surveyEndTime)
                .setSubmitted(isSubmitted)
                .setSurveyAnswer(surveyAnswer)
                .setVendorName(vendorName);
    }
}