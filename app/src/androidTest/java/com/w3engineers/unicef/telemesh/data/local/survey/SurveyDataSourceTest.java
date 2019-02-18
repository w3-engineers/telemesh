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

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [07-Feb-2019 at 3:49 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [07-Feb-2019 at 3:49 PM].
 * --> <Second Editor> on [07-Feb-2019 at 3:49 PM].
 * Reviewed by :
 * --> <First Reviewer> on [07-Feb-2019 at 3:49 PM].
 * --> <Second Reviewer> on [07-Feb-2019 at 3:49 PM].
 * ============================================================================
 **/
@RunWith(AndroidJUnit4.class)
public class SurveyDataSourceTest {

    private AppDatabase appDatabase;
    private UserDataSource userDataSource;
    SurveyDataSource SUT;

    @Before
    public void setUp() throws Exception {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = new UserDataSource(appDatabase.userDao());

        SUT = new SurveyDataSource(appDatabase.surveyDao());
    }

    @After
    public void tearDown() throws Exception {
        appDatabase.close();
    }

    @Test
    public void basicSurveyTest() throws Exception {

        String userId = UUID.randomUUID().toString();

        userDataSource.insertOrUpdateData(getUserInfo(userId));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String surveyId_1 = UUID.randomUUID().toString();
        SUT.insertOrUpdateData(getSurveyInfo(surveyId_1, userId));

        String surveyId_2 = UUID.randomUUID().toString();
        SUT.insertOrUpdateData(getSurveyInfo(surveyId_2, userId));

        SurveyEntity surveyEntity = SUT.getSurveyById(surveyId_1);
        assertEquals(surveyEntity.getSenderId(), userId);

        TestSubscriber<List<SurveyEntity>> getAllSurveySubscriber = SUT.getAllSurvey().test();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getAllSurveySubscriber.assertNoErrors().assertValue(surveyEntities -> surveyEntities.size() == 2);
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