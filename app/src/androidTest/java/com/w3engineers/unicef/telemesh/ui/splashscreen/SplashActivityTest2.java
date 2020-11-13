/*
package com.w3engineers.unicef.telemesh.ui.splashscreen;


import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivityNew;
import com.w3engineers.unicef.telemesh.ui.security.SecurityActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SplashActivityTest2 {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    public String myAddress = "0x550de922bec427fc1b279944e47451a89a4f7cag";
    public String friendAddress = "0x3b52d4e229fd5396f468522e68f17cfe471b2e03";
    public String publicKey = "0x04647ba47589ace7e9636029e5355b9b71c1c66ccd3c1b7c127f3c21016dacea7d3aa12e41eca790d4c3eff8398fd523dc793c815da7bbdbf29c8744b761ad8e4c";
    public String defaultPassword = "mesh_123";

    @Test
    public void ui_test_01() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction baseButton = onView(
                allOf(withId(R.id.button_create_account), withText("I'm a New User"),
                        childAtPosition(
                                allOf(withId(R.id.activity_profile_choice_parent),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        baseButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction baseEditText = onView(
                allOf(withId(R.id.edit_text_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.name_layout),
                                        0),
                                0)));
        baseEditText.perform(scrollTo(), replaceText("Mimo"), closeSoftKeyboard());

        ViewInteraction baseButton2 = onView(
                allOf(withId(R.id.button_signup), withText("Next"),
                        childAtPosition(
                                allOf(withId(R.id.image_layout),
                                        childAtPosition(
                                                withId(R.id.scrollview),
                                                0)),
                                10)));
        baseButton2.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction boxPassword = onView(
                allOf(withId(R.id.edit_text_box_password),
                        childAtPosition(
                                allOf(withId(R.id.activity_security_scroll_parent),
                                        childAtPosition(
                                                withId(R.id.activity_security_scroll),
                                                0)),
                                1)));
        boxPassword.perform(scrollTo(), replaceText("mesh_123"), closeSoftKeyboard());

        ViewInteraction baseButton3 = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                allOf(withId(R.id.activity_security_scroll_parent),
                                        childAtPosition(
                                                withId(R.id.activity_security_scroll),
                                                0)),
                                4)));
        baseButton3.perform(scrollTo(), click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Activity currentActivity = getActivityInstance();

        if (currentActivity instanceof SecurityActivity) {

            SecurityActivity securityActivity = (SecurityActivity) getActivityInstance();

            securityActivity.processCompleted(myAddress, publicKey, defaultPassword);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        currentActivity = getActivityInstance();
        if (currentActivity instanceof MainActivity) {
            Activity finalCurrentActivity = currentActivity;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) finalCurrentActivity).stopAnimation();
                }
            });
        }
    }

    private void dumpThreads() {
        int activeCount = Thread.activeCount();
        Thread[] threads = new Thread[activeCount];
        Thread.enumerate(threads);
        for (Thread thread : threads) {
            System.err.println(thread.getName() + ": " + thread.getState());
            for (StackTraceElement stackTraceElement : thread.getStackTrace()) {
                System.err.println("\t" + stackTraceElement);
            }
        }
    }

    @Test
    public void ui_test_02() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        */
/*Activity currentActivity = getActivityInstance();
        if (currentActivity instanceof MainActivityNew) {
            Activity finalCurrentActivity = currentActivity;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
//                    ViewInteraction bottomNavigationItemView = onView(allOf(withId(R.id.action_message_feed), childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 2)));
//                    bottomNavigationItemView.perform(click());
                    ((MainActivityNew) finalCurrentActivity).initBottomBar(true);
                }
            });
        }*//*


        ViewInteraction settingsTab = onView(
                allOf(withId(R.id.action_setting),
                        childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 3), isDisplayed()));
        settingsTab.perform(click());

//        ViewInteraction bottomNavigationItemView = onView(allOf(withId(R.id.action_message_feed), childAtPosition(childAtPosition(withId(R.id.bottom_navigation), 0), 2)));
//        bottomNavigationItemView.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction constraintLayout = onView(
                allOf(withId(R.id.layout_view_profile),
                        childAtPosition(
                                allOf(withId(R.id.layout_settings),
                                        childAtPosition(
                                                withId(R.id.layout_scroll),
                                                0)),
                                0)));
        constraintLayout.perform(scrollTo(), click());

        */
/*ViewInteraction profileRow = onView(
                allOf(withId(R.id.layout_view_profile),
                        childAtPosition(allOf(withId(R.id.layout_settings),
                                childAtPosition(withId(R.id.layout_scroll), 0)), 0)));
        profileRow.perform(scrollTo(), click());*//*


        try {
            Thread.sleep(1000);
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
}
*/
