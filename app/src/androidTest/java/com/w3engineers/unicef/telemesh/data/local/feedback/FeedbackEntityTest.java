package com.w3engineers.unicef.telemesh.data.local.feedback;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.*;

import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;
import com.w3engineers.unicef.util.helper.StatusHelper;

@RunWith(AndroidJUnit4.class)
public class FeedbackEntityTest {
    private String userId;
    private String userName;
    private String feedback;

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    @Before
    public void setup() {
        userId = UUID.randomUUID().toString();
        userName = "John Doe";
        feedback = "This is sample feedback.";
    }

    @Test
    public void feedbackConvertTest() {
        FeedbackModel model = getFeedbackModel();

        FeedbackEntity feedbackEntity = FeedbackEntity.toFeedbackEntity(model);

        FeedbackModel convertedModel = feedbackEntity.toFeedbackModel();

        assertEquals(convertedModel.getUserId(), model.getUserId());

        StatusHelper.out("Test executed");
    }

    private FeedbackModel getFeedbackModel() {
        FeedbackModel model = new FeedbackModel();
        model.setUserId(userId);
        model.setUserName(userName);
        model.setFeedbackId(UUID.randomUUID().toString());
        model.setFeedback(feedback);

        return model;
    }
}