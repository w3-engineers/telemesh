package com.w3engineers.unicef.telemesh._UiTest;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.runner.lifecycle.Stage.RESUMED;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static org.hamcrest.Matchers.allOf;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.uiautomator.UiDevice;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageSourceData;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserDataSource;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class VTelemeshTest {

    public String myAddress = "0x550de922bec427fc1b279944e47451a89a4f7cag";
    public UiDevice mDevice = UiDevice.getInstance(getInstrumentation());

    private AppDatabase appDatabase;
    private UserDataSource userDataSource;
    private FeedDataSource feedDataSource;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp() {

        appDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                AppDatabase.class).allowMainThreadQueries().build();

        userDataSource = UserDataSource.getInstance();
        feedDataSource = FeedDataSource.getInstance();
    }

    @After
    public void closeDb() throws IOException {
        appDatabase.close();
    }

    // For broadcast feed

    @Test
    public void uiTest_03() {
        addDelay(3800);

        SharedPref.write(Constants.preferenceKey.USER_NAME, "Mimo");
        SharedPref.write(Constants.preferenceKey.IMAGE_INDEX, 1);
        SharedPref.write(Constants.preferenceKey.MY_USER_ID, myAddress);

        long version = (BuildConfig.VERSION_CODE + 5);
        SharedPref.write(Constants.preferenceKey.UPDATE_APP_VERSION, version);

        currentActivity = getActivityInstance();

        if (currentActivity instanceof MainActivity) {
            new Handler(Looper.getMainLooper()).post(() -> ((MainActivity) currentActivity).stopAnimation());
        }

        addDelay(6000);


        ViewInteraction broadcastMessageTab = onView(
                allOf(withId(R.id.action_message_feed),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 2), isDisplayed()));
        broadcastMessageTab.perform(click());

        addDelay(500);

        addFeedItem();

        addDelay(1500);

        // click feed item.

        onView(withId(R.id.message_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        addDelay(1500);

        mDevice.pressBack();

        addDelay(1000);

        Activity activity = getActivityInstance();

        if (activity instanceof MainActivity) {

            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.feedRefresh();

            addDelay(1000);

            try {
                mDevice.pressBack();
                addDelay(700);
                mDevice.pressBack();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public Activity currentActivity = null;

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

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private void addFeedItem() {
        FeedEntity entity = new FeedEntity();
        entity.setFeedId(UUID.randomUUID().toString());
        entity.setFeedTitle("Sample title");
        entity.setFeedDetail("Sample broadcast");
        entity.setFeedProviderName("Unicef");
        entity.setFeedTime("2019-08-02T06:05:30.000Z");
        entity.setFeedReadStatus(false);

        feedDataSource.insertOrUpdateData(entity);
    }
}
