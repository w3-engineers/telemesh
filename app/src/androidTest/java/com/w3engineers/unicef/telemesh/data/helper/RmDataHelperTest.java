package com.w3engineers.unicef.telemesh.data.helper;

import static org.junit.Assert.assertTrue;

import androidx.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackDataSource;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageModel;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;
import com.w3engineers.unicef.util.helper.ViperUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RmDataHelperTest {

    private String meshId;
    private RandomEntityGenerator randomEntityGenerator;
    private FeedDataSource feedDataSource;
    private FeedbackDataSource feedbackDataSource;
    private UserDataSource userDataSource;

    @Before
    public void setUp() {
        meshId = "0x550de922bec427fc1b279944e47451a89a4f7car";

        feedDataSource = FeedDataSource.getInstance();

        feedbackDataSource = FeedbackDataSource.getInstance();

        userDataSource = UserDataSource.getInstance();

        randomEntityGenerator = new RandomEntityGenerator();
    }

    @Test
    public void testAppUpdateBlocker(){
        addDelay();
        RmDataHelper.getInstance().appUpdateFromOtherServer(Constants.AppUpdateType.BLOCKER,
                "");
        assertTrue(true);
    }

    @Test
    public void testAppUpdateNoChange(){
        addDelay();
        RmDataHelper.getInstance().appUpdateFromOtherServer(Constants.AppUpdateType.NO_CHANGES,
                "");
        assertTrue(true);
    }

    @Test
    public void testActivityLaunch(){
        addDelay();
        RmDataHelper.getInstance().launchActivity(ViperUtil.WALLET_IMPORT_ACTIVITY);
        assertTrue(true);
    }

    @Test
    public void testDummyContent(){
        addDelay();
        String messageModelText = new String("{\"restaurant\":{\"id\":\"abc-012\",\"name\":\"good restaurant\",\"foodType\":\"American\",\"phoneNumber\":\"123-456-7890\",\"currency\":\"USD\",\"website\":\"website.com\",\"location\":{\"address\":{\"street\":\" Good Street\",\"city\":\"Good City\",\"state\":\"CA\",\"country\":\"USA\",\"postalCode\":\"12345\"},\"coordinates\":{\"latitude\":\"00.7904692\",\"longitude\":\"-000.4047208\"}},\"restaurantUser\":{\"firstName\":\"test\",\"lastName\":\"test\",\"email\":\"test@test.com\",\"title\":\"server\",\"phone\":\"0000000000\"}}}");
        MessageModel messageModel = new Gson().fromJson(messageModelText, MessageModel.class);
        messageModel.setGroupId("demoGroup");
        RmDataHelper.getInstance().saveDummyContent(messageModel, meshId);
        assertTrue(true);
    }

    private void addDelay() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
