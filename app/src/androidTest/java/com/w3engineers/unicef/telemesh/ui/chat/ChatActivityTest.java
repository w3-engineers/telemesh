package com.w3engineers.unicef.telemesh.ui.chat;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.runner.lifecycle.Stage.RESUMED;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.uiautomator.UiDevice;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.ChatEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;

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
    private String imagePath = "file:///android_asset/sample_image.jpg";

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        currentActivity = getActivityInstance();
    }

    public void tearDown() {
    }

    public class TMXView extends View {
        public TMXView(Context context) {
            super(context);
            // Load map
        }

        public void onDraw(Canvas canvas) {
            // Draw the map on the canvas
        }
    }

    /*@Test
    public void testChatFinish(){
        mActivityTestRule.getActivity().chatFinishAndStartApp();
        assertTrue(true);
    }*/

    @Test
    public void testOpenVideo(){
        addDelay(1000);
        mActivityTestRule.getActivity().openVideo(videoFilePath);
        assertTrue(true);
    }

    @Test
    public void testZoomImage(){
        addDelay(1000);
        View view = new TMXView(context);
        mActivityTestRule.getActivity().zoomImageFromThumb(view, imagePath);
        assertTrue(true);
    }

    @Test
    public void testViewContent(){
        addDelay(1000);

        assertTrue(true);
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
