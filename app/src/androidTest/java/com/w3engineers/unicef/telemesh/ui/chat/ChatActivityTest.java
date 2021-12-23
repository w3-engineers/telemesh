package com.w3engineers.unicef.telemesh.ui.chat;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.runner.lifecycle.Stage.RESUMED;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

@RunWith(AndroidJUnit4.class)
public class ChatActivityTest {
    @Rule
    public ActivityTestRule<ChatActivity> mActivityTestRule = new ActivityTestRule<>(ChatActivity.class);
    public Activity currentActivity = null;
    private Context context;
    private String videoFilePath = "file:///android_asset/sample_vide.mp4";

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        currentActivity = getActivityInstance();
    }

    public void tearDown() {
    }

    /*@Test
    public void testChatFinish(){
        mActivityTestRule.getActivity().chatFinishAndStartApp();
        assertTrue(true);
    }*/

    @Test
    public void testOpenVideo(){
        mActivityTestRule.getActivity().openVideo(videoFilePath);
        assertTrue(true);
    }

    public Activity getActivityInstance() {
        getInstrumentation().runOnMainSync(() -> {
            Collection resumedActivities =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
            if (resumedActivities.iterator().hasNext()) {
                currentActivity = (Activity) resumedActivities.iterator().next();
            }
        });

        return currentActivity;
    }
}
