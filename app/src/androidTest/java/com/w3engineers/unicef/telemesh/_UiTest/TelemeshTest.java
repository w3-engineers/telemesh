package com.w3engineers.unicef.telemesh._UiTest;


import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.core.internal.deps.guava.collect.Iterables;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.test.uiautomator.UiDevice;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.ui.chat.ChatActivity;
import com.w3engineers.unicef.telemesh.ui.security.SecurityActivity;
import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TelemeshTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Rule
    public ActivityTestRule<SecurityActivity> mChatTestRule = new ActivityTestRule<>(SecurityActivity.class);


    public UiDevice mDevice = UiDevice.getInstance(getInstrumentation());

    public String myAddress = "0x550de922bec427fc1b279944e47451a89a4f7cag";
    public String publicKey = "0x04647ba47589ace7e9636029e5355b9b71c1c66ccd3c1b7c127f3c21016dacea7d3aa12e41eca790d4c3eff8398fd523dc793c815da7bbdbf29c8744b761ad8e4c";
    public String defaultPassword = "mesh_123";

    @Test
    public void uiTest_01() {

        addDelay(3200);

        ViewInteraction buttonImportAccount = onView(
                allOf(withId(R.id.button_import_account),
                        childAtPosition(
                                childAtPosition(withId(android.R.id.content), 0), 3),
                        isDisplayed()));
        buttonImportAccount.perform(click());

        addDelay(500);

        ViewInteraction importAnotherId = onView(
                allOf(withId(R.id.button_import_profile),
                        childAtPosition(allOf(withId(R.id.activity_import_profile_scroll_parent),
                                        childAtPosition(withId(R.id.activity_import_profile_scroll), 0)), 5)));
        importAnotherId.perform(scrollTo(), click());

        addDelay(3000);

        mDevice.pressBack();

        addDelay(1000);

        ViewInteraction activityImportProfileBack = onView(
                allOf(withId(R.id.image_view_back),
                        childAtPosition(childAtPosition(withId(android.R.id.content), 0), 0), isDisplayed()));
        activityImportProfileBack.perform(click());

        addDelay(500);

        ViewInteraction buttonCreateAccount = onView(
                allOf(withId(R.id.button_create_account),
                        childAtPosition(allOf(withId(R.id.activity_profile_choice_parent),
                                childAtPosition(withId(android.R.id.content), 0)), 2),
                        isDisplayed()));

        /*ViewInteraction buttonCreateAccount = onView(
                allOf(withId(R.id.button_create_account),
                        childAtPosition(
                                childAtPosition(withId(R.id.activity_profile_choice_parent), 0), 2), isDisplayed()));*/
        buttonCreateAccount.perform(click());

        addDelay(500);

        ViewInteraction baseEditText = onView(
                allOf(withId(R.id.edit_text_name),
                        childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)));
        addDelay(500);
        baseEditText.perform(scrollTo(), replaceText("Mimo"), closeSoftKeyboard());

        addDelay(500);

        ViewInteraction baseEditText2 = onView(
                allOf(withId(R.id.edit_text_name), withText("Mimo"),
                        childAtPosition(childAtPosition(withId(R.id.name_layout), 0), 0)));
        baseEditText2.perform(pressImeActionButton());

        addDelay(1000);

        ViewInteraction buttonImageChooserFirst = onView(
                allOf(withId(R.id.image_profile),
                        childAtPosition(allOf(withId(R.id.image_layout), childAtPosition(withId(R.id.scrollview), 0)), 6)));
        buttonImageChooserFirst.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction selectImageForTheFirst = onView(
                allOf(childAtPosition(allOf(withId(R.id.recycler_view),
                                childAtPosition(withId(R.id.profile_image_layout), 1)), 0), isDisplayed()));
        selectImageForTheFirst.perform(click());

        addDelay(300);

        ViewInteraction selectDoneMenuForTheFirst = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        selectDoneMenuForTheFirst.perform(click());

        addDelay(500);

        ViewInteraction buttonImageChooserSecond = onView(
                allOf(withId(R.id.image_profile),
                        childAtPosition(allOf(withId(R.id.image_layout), childAtPosition(withId(R.id.scrollview), 0)), 6)));
        buttonImageChooserSecond.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction selectImageForTheSecond = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.recycler_view),
                                childAtPosition(withId(R.id.profile_image_layout), 1)), 4), isDisplayed()));
        selectImageForTheSecond.perform(click());

        addDelay(300);

        ViewInteraction selectImageForTheThird = onView(
                allOf(childAtPosition(allOf(withId(R.id.recycler_view),
                        childAtPosition(withId(R.id.profile_image_layout), 1)), 1), isDisplayed()));
        selectImageForTheThird.perform(click());

        addDelay(300);

        ViewInteraction selectDoneMenuForTheSecond = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        selectDoneMenuForTheSecond.perform(click());

        addDelay(500);

        ViewInteraction buttonImageChooserThird = onView(
                allOf(withId(R.id.image_profile),
                        childAtPosition(allOf(withId(R.id.image_layout),
                                childAtPosition(withId(R.id.scrollview), 0)), 6)));
        buttonImageChooserThird.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction selectImageForTheFourth = onView(
                allOf(childAtPosition(allOf(withId(R.id.recycler_view),
                        childAtPosition(withId(R.id.profile_image_layout), 1)), 4), isDisplayed()));
        selectImageForTheFourth.perform(click());

        addDelay(300);

        ViewInteraction selectDoneMenuForTheThird = onView(
                allOf(withId(R.id.menu_done),
                        childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        selectDoneMenuForTheThird.perform(click());

        addDelay(500);

        ViewInteraction ActionCreateProfileNext = onView(
                allOf(withId(R.id.button_signup),
                        childAtPosition(allOf(withId(R.id.image_layout),
                                        childAtPosition(withId(R.id.scrollview), 0)), 10)));
        ActionCreateProfileNext.perform(scrollTo(), click());

        addDelay(500);

        ViewInteraction boxPassword = onView(
                allOf(withId(R.id.edit_text_box_password),
                        childAtPosition(allOf(withId(R.id.activity_security_scroll_parent),
                                childAtPosition(withId(R.id.activity_security_scroll), 0)), 1)));
        boxPassword.perform(scrollTo(), replaceText("mesh_123"), closeSoftKeyboard());

        addDelay(1000);

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.text_view_show_password),
                        childAtPosition(allOf(withId(R.id.activity_security_scroll_parent),
                                childAtPosition(withId(R.id.activity_security_scroll), 0)), 2)));
        appCompatTextView.perform(scrollTo(), click());

        addDelay(300);

        ViewInteraction showPassword = onView(
                allOf(withId(R.id.text_view_show_password),
                        childAtPosition(
                                allOf(withId(R.id.activity_security_scroll_parent),
                                        childAtPosition(
                                                withId(R.id.activity_security_scroll),
                                                0)),
                                2)));
        showPassword.perform(scrollTo(), click());

        addDelay(300);

        ViewInteraction securityButtonNext = onView(
                allOf(withId(R.id.button_next),
                        childAtPosition(allOf(withId(R.id.activity_security_scroll_parent),
                                childAtPosition(withId(R.id.activity_security_scroll), 0)), 4)));
        securityButtonNext.perform(scrollTo(), click());

        addDelay(5000);

        SecurityActivity securityActivity = (SecurityActivity) getActivityInstance();

        securityActivity.processCompleted(myAddress, publicKey, defaultPassword);

        addDelay(300);
    }

    public Activity currentActivity = null;

    public Activity getActivityInstance() {
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities =
                        ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()){
                    currentActivity = (Activity) resumedActivities.iterator().next();
                }
            }
        });

        return currentActivity;
    }

    @Test
    public void uiTest_02() {
        addDelay(3800);
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
}
