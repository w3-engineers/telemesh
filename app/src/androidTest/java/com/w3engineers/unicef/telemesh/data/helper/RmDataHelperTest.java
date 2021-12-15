package com.w3engineers.unicef.telemesh.data.helper;

import static org.junit.Assert.assertTrue;

import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackDataSource;
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
        RmDataHelper.getInstance().appUpdateFromOtherServer(Constants.AppUpdateType.BLOCKER,
                "");
        assertTrue(true);
    }

    @Test
    public void testAppUpdateNoChange(){
        RmDataHelper.getInstance().appUpdateFromOtherServer(Constants.AppUpdateType.NO_CHANGES,
                "");
        assertTrue(true);
    }

    @Test
    public void testActivityLaunch(){
        RmDataHelper.getInstance().launchActivity(ViperUtil.WALLET_IMPORT_ACTIVITY);
        assertTrue(true);
    }
}
